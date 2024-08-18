package flaxbeard.immersivepetroleum.common;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import flaxbeard.immersivepetroleum.common.IPContent.Blocks;
import flaxbeard.immersivepetroleum.common.IPContent.Multiblock;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.CokerUnitLogic;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.DerrickLogic;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.DistillationTowerLogic;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.HydroTreaterLogic;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.OilTankLogic;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.PumpjackLogic;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.AutoLubricatorTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.FlarestackTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.GasGeneratorTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.SeismicSurveyTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.WellPipeTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.WellTileEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class IPTileTypes{
	// Multiblocks
	public static final BlockEntityType<MultiblockBlockEntityMaster<DerrickLogic.State>> DERRICK = getMBTE(Multiblock.DERRICK);
	public static final BlockEntityType<MultiblockBlockEntityMaster<PumpjackLogic.State>> PUMPJACK = getMBTE(Multiblock.PUMPJACK);
	public static final BlockEntityType<MultiblockBlockEntityMaster<OilTankLogic.State>> OILTANK = getMBTE(Multiblock.OILTANK);
	public static final BlockEntityType<MultiblockBlockEntityMaster<DistillationTowerLogic.State>> DISTILLATIONTOWER = getMBTE(Multiblock.DISTILLATIONTOWER);
	public static final BlockEntityType<MultiblockBlockEntityMaster<CokerUnitLogic.State>> COKERUNIT = getMBTE(Multiblock.COKERUNIT);
	public static final BlockEntityType<MultiblockBlockEntityMaster<HydroTreaterLogic.State>> HYDROTREATER = getMBTE(Multiblock.HYDROTREATER);
	
	// Normal Blocks
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SeismicSurveyTileEntity>> SEISMIC_SURVEY = IPRegisters.registerTE("seismic_survey", SeismicSurveyTileEntity::new, Blocks.SEISMIC_SURVEY);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WellTileEntity>> WELL = IPRegisters.registerTE("well", WellTileEntity::new, Blocks.WELL);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WellPipeTileEntity>> WELL_PIPE = IPRegisters.registerTE("well_pipe", WellPipeTileEntity::new, Blocks.WELL_PIPE);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GasGeneratorTileEntity>> GENERATOR = IPRegisters.registerTE("gasgenerator", GasGeneratorTileEntity::new, Blocks.GAS_GENERATOR);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AutoLubricatorTileEntity>> AUTOLUBE = IPRegisters.registerTE("autolubricator", AutoLubricatorTileEntity::new, Blocks.AUTO_LUBRICATOR);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FlarestackTileEntity>> FLARE = IPRegisters.registerTE("flarestack", FlarestackTileEntity::new, Blocks.FLARESTACK);
	
	
	@SuppressWarnings({"unchecked"})
	private static <S extends IMultiblockState> BlockEntityType<MultiblockBlockEntityMaster<S>> getMBTE(MultiblockRegistration<S> mbReg){
		return (BlockEntityType<MultiblockBlockEntityMaster<S>>) mbReg.masterBE().get();
	}
	
	public static void forceClassLoad(){
	}
}
