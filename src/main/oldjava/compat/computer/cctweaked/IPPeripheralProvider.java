package flaxbeard.immersivepetroleum.common.util.compat.computer.cctweaked;

import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartBlockEntity;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.CokerUnitTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.DerrickTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.DistillationTowerTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.HydrotreaterTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.OilTankTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.PumpjackTileEntity;
import flaxbeard.immersivepetroleum.common.util.compat.computer.cctweaked.multiblocks.CokerUnitPeripheral;
import flaxbeard.immersivepetroleum.common.util.compat.computer.cctweaked.multiblocks.DerrickPeripheral;
import flaxbeard.immersivepetroleum.common.util.compat.computer.cctweaked.multiblocks.DistillationTowerPeripheral;
import flaxbeard.immersivepetroleum.common.util.compat.computer.cctweaked.multiblocks.HydrotreaterPeripheral;
import flaxbeard.immersivepetroleum.common.util.compat.computer.cctweaked.multiblocks.OilTankPeripheral;
import flaxbeard.immersivepetroleum.common.util.compat.computer.cctweaked.multiblocks.PumpjackPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;

public class IPPeripheralProvider implements IPeripheralProvider{
	public static final IPPeripheralProvider INSTANCE = new IPPeripheralProvider();
	
	@Override
	public LazyOptional<IPeripheral> getPeripheral(Level world, BlockPos pos, Direction side){
		BlockEntity be = world.getBlockEntity(pos);
		if(be instanceof MultiblockPartBlockEntity<?> mbpbe && mbpbe.isRedstonePos()){
			
			if(be instanceof HydrotreaterTileEntity hydrotreater){
				return LazyOptional.of(() -> new HydrotreaterPeripheral(hydrotreater));
			}
			if(be instanceof DistillationTowerTileEntity tower){
				return LazyOptional.of(() -> new DistillationTowerPeripheral(tower));
			}
			if(be instanceof CokerUnitTileEntity coker){
				return LazyOptional.of(() -> new CokerUnitPeripheral(coker));
			}
			if(be instanceof PumpjackTileEntity pumpjack){
				return LazyOptional.of(() -> new PumpjackPeripheral(pumpjack));
			}
			if(be instanceof OilTankTileEntity oiltank){
				return LazyOptional.of(() -> new OilTankPeripheral(oiltank));
			}
			if(be instanceof DerrickTileEntity derrick){
				return LazyOptional.of(() -> new DerrickPeripheral(derrick));
			}
			
		}
		return LazyOptional.empty();
	}
	
	public static void init(){
		ComputerCraftAPI.registerPeripheralProvider(IPPeripheralProvider.INSTANCE);
	}
}
