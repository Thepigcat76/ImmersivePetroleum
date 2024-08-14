package flaxbeard.immersivepetroleum.api.crafting;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.crafting.TagOutput;
import flaxbeard.immersivepetroleum.common.cfg.IPServerConfig;
import flaxbeard.immersivepetroleum.common.crafting.Serializers;
import flaxbeard.immersivepetroleum.common.util.ChancedItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DistillationTowerRecipe extends MultiblockRecipe{
	public static Map<ResourceLocation, RecipeHolder<DistillationTowerRecipe>> recipes = new HashMap<>();
	
    private static final RandomSource RANDOM = RandomSource.create();
	
	/** May return null! */
	@Nullable
	public static RecipeHolder<DistillationTowerRecipe> findRecipe(FluidStack input){
		if(!recipes.isEmpty()){
			for(RecipeHolder<DistillationTowerRecipe> holder:recipes.values()){
				final DistillationTowerRecipe recipe = holder.value();
				
				if(recipe.input != null && recipe.input.testIgnoringAmount(input)){
					return holder;
				}
			}
		}
		return null;
	}
	
	@Nullable
	public static RecipeHolder<DistillationTowerRecipe> getRecipe(ResourceLocation id){
		return recipes.get(id);
	}
	
	@Nullable
	public static RecipeHolder<DistillationTowerRecipe> loadFromNBT(CompoundTag nbt){
		FluidStack input = FluidStack.loadFluidStackFromNBT(nbt.getCompound("input"));
		return findRecipe(input);
	}
	
	private static final RecipeMultiplier MULTIPLIER = new RecipeMultiplier(IPServerConfig.REFINING.distillationTower_timeModifier::get, IPServerConfig.REFINING.distillationTower_energyModifier::get);
	private static RecipeMultiplier multipliers(){
		return MULTIPLIER;
	}
	
	protected final Lazy<NonNullList<ItemStack>> lazyOutputList;
	protected final FluidTagInput input;
	protected final List<FluidStack> fluidOutputs;
	protected final List<ChancedItemStack> byproducts;
	
	public DistillationTowerRecipe(List<FluidStack> fluidOutput, List<ChancedItemStack> byproducts, FluidTagInput input, int energy, int time){
		super(TagOutput.EMPTY, IPRecipeTypes.DISTILLATION, energy, time, DistillationTowerRecipe::multipliers);
		this.fluidOutputs = fluidOutput;
		this.byproducts = byproducts;
		
		this.input = input;
		this.fluidInputList = Collections.singletonList(input);
		this.fluidOutputList = fluidOutput;
		this.lazyOutputList = Lazy.of(() -> {
			if(byproducts.isEmpty())
				return NonNullList.createWithCapacity(0);
			
			NonNullList<ItemStack> list = NonNullList.withSize(byproducts.size(), ItemStack.EMPTY);
			for(int i = 0;i < byproducts.size();i++){
				list.set(i, byproducts.get(i).stack());
			}
			return list;
		});
	}
	
	@Override
	protected IERecipeSerializer<DistillationTowerRecipe> getIESerializer(){
		return Serializers.DISTILLATION_SERIALIZER.get();
	}
	
	@Override
	public int getMultipleProcessTicks(){
		return 0;
	}
	
	@Override
	public NonNullList<ItemStack> getItemOutputs(){
		return this.lazyOutputList.get();
	}
	
	@Override
	public NonNullList<ItemStack> getActualItemOutputs(){
		if(this.byproducts.isEmpty())
			return NonNullList.withSize(0, ItemStack.EMPTY);
		
		NonNullList<ItemStack> output = NonNullList.create();
		for(ChancedItemStack stack : byproducts){
			if(RANDOM.nextFloat() <= stack.chance()){
				output.add(stack.stack());
			}
		}
		
		return output;
	}
	
	public FluidTagInput getInputFluid(){
		return this.input;
	}
	
	@Deprecated
	public double[] chances(){
		throw new UnsupportedOperationException();
	}
	
	public List<ChancedItemStack> getByproducts(){
		return this.byproducts;
	}
}
