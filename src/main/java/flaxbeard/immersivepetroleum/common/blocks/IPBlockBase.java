package flaxbeard.immersivepetroleum.common.blocks;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import flaxbeard.immersivepetroleum.common.IPCreativeTab.IMightShowUpInCreativeTab;
import flaxbeard.immersivepetroleum.common.blocks.interfaces.IPlacementReader;
import flaxbeard.immersivepetroleum.common.blocks.interfaces.IPlayerInteraction;
import flaxbeard.immersivepetroleum.common.blocks.ticking.IPClientTickableTile;
import flaxbeard.immersivepetroleum.common.blocks.ticking.IPCommonTickableTile;
import flaxbeard.immersivepetroleum.common.blocks.ticking.IPServerTickableTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredHolder;

public class IPBlockBase extends Block implements IMightShowUpInCreativeTab{
	public IPBlockBase(Block.Properties props){
		super(props);
	}
	
	public Supplier<BlockItem> blockItemSupplier(){
		return () -> new IPBlockItemBase(this, new Item.Properties());
	}
	
	private final boolean isEntityBlock = this instanceof EntityBlock;
	
	@Override
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit){
		if(this.isEntityBlock){
			if(pLevel.getBlockEntity(pPos) instanceof IPlayerInteraction inst){
				Vec3 loc = pHit.getLocation();
				return inst.interact(pHit.getDirection(), pPlayer, pHand, pPlayer.getItemInHand(pHand), (float) loc.x, (float) loc.y, (float) loc.z);
			}
		}
		return InteractionResult.FAIL;
	}
	
	@Override
	public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack){
		if(!pLevel.isClientSide && this.isEntityBlock){
			if(pLevel.getBlockEntity(pPos) instanceof IPlacementReader read){
				read.readOnPlacement(pPlacer, pStack);
			}
		}
	}
	
	protected static final BlockBehaviour.Properties metalProperty(){
		return BlockBehaviour.Properties.of().sound(SoundType.METAL).mapColor(MapColor.METAL);
	}
	
	protected static final BlockBehaviour.Properties stoneProperty(){
		return stoneProperty(MapColor.STONE);
	}
	
	protected static final BlockBehaviour.Properties stoneProperty(MapColor color){
		return BlockBehaviour.Properties.of().sound(SoundType.STONE).mapColor(color);
	}
	
	@Nullable
	public static <E extends BlockEntity & IPCommonTickableTile, A extends BlockEntity> BlockEntityTicker<A> createCommonTicker(boolean isClient, BlockEntityType<A> actual, DeferredHolder<BlockEntityType<?>, BlockEntityType<E>> expected){
		return createCommonTicker(isClient, actual, expected.get());
	}
	
	@Nullable
	public static <E extends BlockEntity & IPCommonTickableTile, A extends BlockEntity> BlockEntityTicker<A> createCommonTicker(boolean isClient, BlockEntityType<A> actual, BlockEntityType<E> expected){
		if(isClient){
			return createClientTicker(actual, expected);
		}else{
			return createServerTicker(actual, expected);
		}
	}
	
	@Nullable
	public static <E extends BlockEntity & IPClientTickableTile, A extends BlockEntity> BlockEntityTicker<A> createClientTicker(BlockEntityType<A> actual, BlockEntityType<E> expected){
		return createTickerHelper(actual, expected, IPClientTickableTile::makeTicker);
	}
	
	@Nullable
	public static <E extends BlockEntity & IPServerTickableTile, A extends BlockEntity> BlockEntityTicker<A> createServerTicker(BlockEntityType<A> actual, BlockEntityType<E> expected){
		return createTickerHelper(actual, expected, IPServerTickableTile::makeTicker);
	}
	
	@SuppressWarnings("unchecked")
	@Nullable
	private static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> actual, BlockEntityType<E> expected, Supplier<BlockEntityTicker<? super E>> ticker){
		return expected == actual ? (BlockEntityTicker<A>) ticker.get() : null;
	}
}
