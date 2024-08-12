package flaxbeard.immersivepetroleum.common.data.loot;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class IPLootGenerator extends LootTableProvider{
	public IPLootGenerator(PackOutput output){
		super(output, Set.of(), List.of());
	}
	
	@Override
	public List<SubProviderEntry> getTables(){
		return List.of(
				new SubProviderEntry(IPLoot::new, LootContextParamSets.EMPTY),
				new SubProviderEntry(IPBlockLoot::new, LootContextParamSets.BLOCK)
		);
	}
	
	@Override
	protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationcontext){
		map.forEach((rl, table) -> {
			table.validate(validationcontext);
		});
	}
}
