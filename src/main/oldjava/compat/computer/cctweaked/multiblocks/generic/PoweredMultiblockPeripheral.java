package flaxbeard.immersivepetroleum.common.util.compat.computer.cctweaked.multiblocks.generic;

import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockBlockEntity;
import dan200.computercraft.api.lua.LuaFunction;

public abstract class PoweredMultiblockPeripheral extends MultiblockPartPeripheral{
	PoweredMultiblockBlockEntity<?, ?> mbPowered;
	public PoweredMultiblockPeripheral(PoweredMultiblockBlockEntity<?, ?> mbPowered){
		super(mbPowered);
		this.mbPowered = mbPowered.master();
	}
	
	@LuaFunction
	public final boolean isRunning(){
		return this.mbPowered.shouldRenderAsActive();
	}
	
	@LuaFunction
	public final int getEnergyStored(){
		return this.mbPowered.energyStorage.getEnergyStored();
	}
	
	@LuaFunction
	public final int getMaxEnergyStored(){
		return this.mbPowered.energyStorage.getMaxEnergyStored();
	}
}
