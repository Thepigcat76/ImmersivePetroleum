package flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic;

import java.util.function.Function;

import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.shapes.HydroTreaterShape;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.shapes.VoxelShape;

// TODO
public class HydroTreaterLogic implements IMultiblockLogic<HydroTreaterLogic.State>, IServerTickableComponent<HydroTreaterLogic.State>, IClientTickableComponent<HydroTreaterLogic.State>{
	
	@Override
	public void tickClient(IMultiblockContext<HydroTreaterLogic.State> context){
	}
	
	@Override
	public void tickServer(IMultiblockContext<HydroTreaterLogic.State> context){
	}
	
	@Override
	public State createInitialState(IInitialMultiblockContext<HydroTreaterLogic.State> capabilitySource){
		return null;
	}
	
	@Override
	public Function<BlockPos, VoxelShape> shapeGetter(ShapeType forType){
		return HydroTreaterShape.GETTER;
	}
	
	// TODO
	public static class State implements IMultiblockState{
		
		@Override
		public void writeSaveNBT(CompoundTag nbt){
		}
		
		@Override
		public void readSaveNBT(CompoundTag nbt){
		}
	}
}
