package flaxbeard.immersivepetroleum.common.data;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import flaxbeard.immersivepetroleum.common.world.FeatureReservoir;
import flaxbeard.immersivepetroleum.common.world.IPWorldGen;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;
import net.neoforged.neoforge.registries.holdersets.AnyHolderSet;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class IPBiomeModifierProvider{
	
	public static void reservoirSetup(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, ExistingFileHelper exhelper, Consumer<DataProvider> add){
		final ResourceLocation rl = ResourceUtils.ip("reservoir");
		final FeatureReservoir feature = IPWorldGen.RESERVOIR_FEATURE.get();
		final NoneFeatureConfiguration noneConfig = new NoneFeatureConfiguration();
		
		RegistrySetBuilder regBuilder = new RegistrySetBuilder();
		
		final Ref ref = new Ref();
		
		regBuilder.add(Registries.CONFIGURED_FEATURE, ctx -> {
			ref.configured = ctx.register(ResourceKey.create(Registries.CONFIGURED_FEATURE, rl), new ConfiguredFeature<>(feature, noneConfig));
		});
		
		regBuilder.add(Registries.PLACED_FEATURE, ctx -> {
			ref.placed = ctx.register(ResourceKey.create(Registries.PLACED_FEATURE, rl), new PlacedFeature(ref.configured, List.of()));
		});
		
		regBuilder.add(Keys.BIOME_MODIFIERS, ctx -> {
			final HolderGetter<Biome> biomeReg = ctx.lookup(Registries.BIOME);
			final HolderSet<Biome> biomes = new AnyHolderSet<>(new DummyRegistryLookup<>(biomeReg, Registries.BIOME));
			AddFeaturesBiomeModifier mod = new AddFeaturesBiomeModifier(biomes, HolderSet.direct(ref.placed), Decoration.UNDERGROUND_ORES);
			ctx.register(ResourceKey.create(Keys.BIOME_MODIFIERS, rl), mod);
		});
		
		add.accept(new DatapackBuiltinEntriesProvider(output, lookup, regBuilder, Set.of(ImmersivePetroleum.MODID)));
	}
	
	private static class Ref{
		protected Holder.Reference<ConfiguredFeature<?, ?>> configured;
		protected Holder.Reference<PlacedFeature> placed;
	}
	
	private static record DummyRegistryLookup<T>(HolderGetter<T> getter, ResourceKey<? extends Registry<? extends T>> key) implements HolderLookup.RegistryLookup<T>{
		
		@Override
		public boolean canSerializeIn(@Nonnull HolderOwner<T> owner){
			return true;
		}
		
		@Nonnull
		@Override
		public Lifecycle registryLifecycle(){
			return Lifecycle.stable();
		}
		
		@Nonnull
		@Override
		public Stream<Holder.Reference<T>> listElements(){
			return Stream.empty();
		}
		
		@Nonnull
		@Override
		public Stream<HolderSet.Named<T>> listTags(){
			return Stream.empty();
		}
		
		@Nonnull
		@Override
		public Optional<Holder.Reference<T>> get(@Nonnull ResourceKey<T> resourceKey){
			return Optional.empty();
		}
		
		@Nonnull
		@Override
		public Optional<HolderSet.Named<T>> get(@Nonnull TagKey<T> tagKey){
			return Optional.empty();
		}
	}
}
