package flaxbeard.immersivepetroleum.common.blocks.ticking;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;

/**
 * If both Client and Server ticking is required {@link IPCommonTickableTile} should to be used.
 */
public interface IPClientTickableTile{
	void tickClient();
	
	static <T extends BlockEntity & IPClientTickableTile> BlockEntityTicker<T> makeTicker(){
		return (level, pos, state, te) -> te.tickClient();
	}
}
