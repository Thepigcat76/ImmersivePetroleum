package flaxbeard.immersivepetroleum.common.fluids;

import net.minecraft.world.level.LevelReader;

public class CrudeOilFluid extends IPFluid{
	public CrudeOilFluid(IPFluidEntry entry){
		super(entry, 1000, 2250);
	}
	
	@Override
	public int getTickDelay(LevelReader p_205569_1_){
		return 10;
	}
}
