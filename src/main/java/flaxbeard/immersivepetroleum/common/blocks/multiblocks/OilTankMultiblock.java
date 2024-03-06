package flaxbeard.immersivepetroleum.common.blocks.multiblocks;

import java.util.function.Consumer;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import net.minecraft.core.BlockPos;

public class OilTankMultiblock extends IPTemplateMultiblock{
	public static final OilTankMultiblock INSTANCE = new OilTankMultiblock();
	
	public OilTankMultiblock(){
		super(ResourceUtils.ip("multiblocks/oiltank"),
				new BlockPos(2, 0, 3), new BlockPos(2, 1, 5), new BlockPos(5, 4, 6), IPContent.Multiblock.OILTANK);
	}
	
	@Override
	public float getManualScale(){
		return 12;
	}
	
	@Override
	public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer){
		consumer.accept(new IPClientMultiblockProperties(this, 2.5, 0.5, 3.5));
	}
}
