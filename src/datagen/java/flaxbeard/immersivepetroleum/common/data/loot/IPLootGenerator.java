package flaxbeard.immersivepetroleum.common.data.loot;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Set;

// TODO
public class IPLootGenerator extends LootTableProvider{
	public IPLootGenerator(PackOutput output, Set<ResourceLocation> requiredTables, List<SubProviderEntry> subProviders){
		super(output, requiredTables, subProviders);
	}
	
	/*
	public IPLootGenerator(DataGenerator pGenerator){
		super(pGenerator);
	}
	
	@Override
	protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables(){
		return List.of(
				Pair.of(IPLoot::new, LootContextParamSets.EMPTY),
				Pair.of(IPBlockLoot::new, LootContextParamSets.BLOCK)
			);
	}
	
	@Override
	protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker){
		map.forEach((p_218436_2_, p_218436_3_) -> {
			LootTables.validate(validationtracker, p_218436_2_, p_218436_3_);
		});
	}
	*/
}
