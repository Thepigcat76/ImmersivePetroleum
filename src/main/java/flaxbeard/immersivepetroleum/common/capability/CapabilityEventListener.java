package flaxbeard.immersivepetroleum.common.capability;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.IPTileTypes;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.AutoLubricatorTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.FlarestackTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.GasGeneratorTileEntity;
import flaxbeard.immersivepetroleum.common.fluids.IPFluid;
import net.minecraft.world.item.BucketItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod.EventBusSubscriber;
import net.neoforged.fml.common.Mod.EventBusSubscriber.Bus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;

@EventBusSubscriber(modid = ImmersivePetroleum.MODID, bus = Bus.MOD)
public class CapabilityEventListener{
	
	@SubscribeEvent
	public static void cap(RegisterCapabilitiesEvent event){
		BucketItem[] buckets = IPFluid.FLUIDS.stream().map(f -> f.bucket().get()).toList().toArray(BucketItem[]::new);
		event.registerItem(Capabilities.FluidHandler.ITEM, (stack, v) -> new FluidBucketWrapper(stack), buckets);
		
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, IPTileTypes.AUTOLUBE.get(), AutoLubricatorTileEntity::getCapability);
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, IPTileTypes.FLARE.get(), FlarestackTileEntity::getCapability);
		
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, IPTileTypes.GENERATOR.get(), GasGeneratorTileEntity::getEnergyCapability);
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, IPTileTypes.GENERATOR.get(), GasGeneratorTileEntity::getFluidCapability);
	}
	
}
