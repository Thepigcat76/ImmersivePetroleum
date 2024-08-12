package flaxbeard.immersivepetroleum.common.data;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.data.loot.IPLootGenerator;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod.EventBusSubscriber;
import net.neoforged.fml.common.Mod.EventBusSubscriber.Bus;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = ImmersivePetroleum.MODID, bus = Bus.MOD)
public class IPDataGenerator{
	public static final Logger log = LogManager.getLogger(ImmersivePetroleum.MODID + "/DataGenerator");
	
	@SubscribeEvent
	public static void generate(GatherDataEvent event){
		final ExistingFileHelper exHelper = event.getExistingFileHelper();
		final DataGenerator generator = event.getGenerator();
		final PackOutput output = generator.getPackOutput();
		final CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();
		
		if(event.includeServer())
		{
			IPBlockTags blockTags = new IPBlockTags(output, lookup, exHelper);
			generator.addProvider(true, blockTags);
			generator.addProvider(true, new IPItemTags(output, lookup, blockTags.contentsGetter(), exHelper));
			generator.addProvider(true, new IPFluidTags(output, lookup, exHelper));
			generator.addProvider(true, new IPLootGenerator(output));
			generator.addProvider(true, new IPRecipes(output));
			generator.addProvider(true, new IPAdvancements(output, lookup, exHelper));
			
			generator.addProvider(true, new IPBlockStates(output, exHelper));
			generator.addProvider(true, new IPItemModels(output, generator, exHelper));
			
			IPBiomeModifierProvider.method(generator, exHelper, d -> generator.addProvider(true, d));
		}
	}
}
