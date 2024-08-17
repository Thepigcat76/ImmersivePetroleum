package flaxbeard.immersivepetroleum.client.render;

import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MultiblockRenderer;

public abstract class IPMultiblockRenderer<State extends IMultiblockState> extends IPBlockEntityRenderer<MultiblockBlockEntityMaster<State>> implements MultiblockRenderer<State>{
}
