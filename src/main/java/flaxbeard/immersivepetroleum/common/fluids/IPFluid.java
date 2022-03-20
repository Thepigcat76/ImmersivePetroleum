package flaxbeard.immersivepetroleum.common.fluids;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.IPRegisters;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.minecraftforge.registries.RegistryObject;

public class IPFluid extends FlowingFluid{
	public static final List<IPFluidEntry> FLUIDS = new ArrayList<>();
	
	protected final IPFluidEntry entry;
	protected final ResourceLocation stillTexture;
	protected final ResourceLocation flowingTexture;
	@Nullable
	protected final Consumer<FluidAttributes.Builder> buildAttributes;
	
	public IPFluid(IPFluidEntry entry, int density, int viscosity){
		this(entry,
				new ResourceLocation(ImmersivePetroleum.MODID, "block/fluid/" + entry.name + "_still"),
				new ResourceLocation(ImmersivePetroleum.MODID, "block/fluid/" + entry.name + "_flow"), IPFluid.createBuilder(density, viscosity));
	}
	
	protected IPFluid(IPFluidEntry entry, ResourceLocation stillTexture, ResourceLocation flowingTexture, @Nullable Consumer<FluidAttributes.Builder> buildAttributes){
		this(entry, stillTexture, flowingTexture, buildAttributes, true);
	}
	
	protected IPFluid(IPFluidEntry entry, ResourceLocation stillTexture, ResourceLocation flowingTexture, @Nullable Consumer<FluidAttributes.Builder> buildAttributes, boolean isSource){
		this.entry = entry;
		this.stillTexture = stillTexture;
		this.flowingTexture = flowingTexture;
		this.buildAttributes = buildAttributes;
	}

	public static IPFluidEntry makeFluid(String name, Function<IPFluidEntry, IPFluid> factory) {
		return makeFluid(name, factory, IPFluidBlock::new);
	}

	public static IPFluidEntry makeFluid(
			String name, Function<IPFluidEntry, IPFluid> factory, Function<IPFluidEntry, Block> blockFactory
	) {
		Mutable<IPFluidEntry> entry = new MutableObject<>();

		entry.setValue(new IPFluidEntry(
				name,
				IPRegisters.registerFluid(name, () -> factory.apply(entry.getValue())),
				IPRegisters.registerFluid(name+"_flowing", () -> new IPFluidFlowing(entry.getValue().still.get())),
				IPRegisters.registerBlock(name, () -> blockFactory.apply(entry.getValue())),
				IPRegisters.registerItem(name+"_bucket", () -> new IPBucketItem(entry.getValue().still()))
		));
		FLUIDS.add(entry.getValue());
		return entry.getValue();
	}
	
	@Override
	protected FluidAttributes createAttributes(){
		FluidAttributes.Builder builder = FluidAttributes.builder(this.stillTexture, this.flowingTexture)
				.overlay(this.stillTexture)
				.sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY);
		
		if(this.buildAttributes != null)
			this.buildAttributes.accept(builder);
		
		return builder.build(this);
	}
	
	@Override
	protected void beforeDestroyingBlock(LevelAccessor arg0, BlockPos arg1, BlockState arg2){
	}
	
	@Override
	protected boolean canConvertToSource(){
		return false;
	}
	
	@Override
	public Fluid getFlowing(){
		return this.entry.flowing.get();
	}
	
	@Override
	public Fluid getSource(){
		return this.entry.still.get();
	}
	
	@Override
	public Item getBucket(){
		return this.entry.bucket.get();
	}
	
	@Override
	protected int getDropOff(LevelReader arg0){
		return 1;
	}
	
	@Override
	protected int getSlopeFindDistance(LevelReader arg0){
		return 4;
	}
	
	@Override
	protected boolean canBeReplacedWith(FluidState p_215665_1_, BlockGetter p_215665_2_, BlockPos p_215665_3_, Fluid p_215665_4_, Direction p_215665_5_){
		return p_215665_5_ == Direction.DOWN && !isSame(p_215665_4_);
	}
	
	@Override
	public int getTickDelay(LevelReader p_205569_1_){
		return 5;
	}
	
	@Override
	protected float getExplosionResistance(){
		return 100;
	}
	
	@Override
	protected BlockState createLegacyBlock(FluidState state){
		return this.entry.block.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
	}
	
	@Override
	public boolean isSource(FluidState state){
		return state.is(this.getSource());
	}
	
	@Override
	public int getAmount(FluidState state){
		return isSource(state) ? 8 : state.getValue(LEVEL);
	}
	
	@Override
	public boolean isSame(Fluid fluidIn){
		return fluidIn.isSame(this.getSource()) || fluidIn.isSame(this.getFlowing());
	}
	
	public static Consumer<FluidAttributes.Builder> createBuilder(int density, int viscosity){
		return builder -> builder.viscosity(viscosity).density(density);
	}
	
	// STATIC CLASSES
	
	public static class IPFluidBlock extends LiquidBlock{
		public IPFluidBlock(IPFluidEntry entry){
			super(entry.still(), BlockBehaviour.Properties.of(Material.WATER));
		}
	}
	
	public static class IPBucketItem extends BucketItem{
		private static final Item.Properties PROPS = new Item.Properties().stacksTo(1).tab(ImmersivePetroleum.creativeTab);
		
		public IPBucketItem(Supplier<? extends Fluid> fluid){
			super(fluid, PROPS);
		}
		
		@Override
		public ItemStack getContainerItem(ItemStack itemStack){
			return new ItemStack(Items.BUCKET);
		}
		
		@Override
		public boolean hasContainerItem(ItemStack stack){
			return true;
		}
		
		@Override
		public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt){
			return new FluidBucketWrapper(stack);
		}
	}
	
	public static class IPFluidFlowing extends IPFluid{
		public IPFluidFlowing(IPFluid source){
			super(source.entry, source.stillTexture, source.flowingTexture, source.buildAttributes, false);
			registerDefaultState(this.getStateDefinition().any().setValue(LEVEL, 7));
		}
		
		@Override
		protected void createFluidStateDefinition(Builder<Fluid, FluidState> builder){
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}
	}

	public record IPFluidEntry(
			String name,
			RegistryObject<IPFluid> still,
			RegistryObject<IPFluid> flowing,
			RegistryObject<Block> block,
			RegistryObject<Item> bucket
	) {
		public Fluid get() {
			return still().get();
		}
	}
}
