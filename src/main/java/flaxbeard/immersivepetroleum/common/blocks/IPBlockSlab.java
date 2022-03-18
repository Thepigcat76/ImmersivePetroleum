package flaxbeard.immersivepetroleum.common.blocks;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.IPContent;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;

public class IPBlockSlab<B extends IPBlockBase> extends SlabBlock{
	private final B base;
	
	public IPBlockSlab(B base){
		super(Properties.copy(base).isSuffocating(causesSuffocation(base)).isRedstoneConductor(isNormalCube(base)));

		this.base = base;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos){
		return Math.min(base.getLightBlock(state, worldIn, pos), super.getLightBlock(state, worldIn, pos));
	}
	
	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos){
		return super.propagatesSkylightDown(state, reader, pos) || base.propagatesSkylightDown(state, reader, pos);
	}
	
	public static BlockBehaviour.StatePredicate causesSuffocation(Block base){
		return (state, world, pos) -> base.defaultBlockState().isSuffocating(world, pos) && state.getValue(TYPE) == SlabType.DOUBLE;
	}
	
	public static BlockBehaviour.StatePredicate isNormalCube(Block base){
		return (state, world, pos) -> base.defaultBlockState().isRedstoneConductor(world, pos) && state.getValue(TYPE) == SlabType.DOUBLE;
	}
}
