package cn.mlus.neobiosphere.data;

import cn.mlus.neobiosphere.Neobiosphere;
import cn.mlus.neobiosphere.registry.ModCarvers;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SphereBiomeBoostrap
{
    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        var biomes = context.lookup(Registries.BIOME);

        addCarver(context, "sphere_carver", ModCarvers.BIOSPHERE_CARVER_KEY);
        addCarver(context, "sphere_bridge_carver", ModCarvers.BIOSPHERE_BRIDGE_CARVER_KEY);
        addFeature(context,"sphere_snow", HolderSet.direct(biomes.getOrThrow(Biomes.THE_VOID))
                , ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.parse("freeze_top_layer")));
//        addFeatureForAll(context, "sphere_feature", ModFeature.BIOSPHERE_FEATURE_KEY);
    }

    private static void register(BootstrapContext<BiomeModifier> context, String name, Supplier<? extends BiomeModifier> modifier) {
        context.register(ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Neobiosphere.prefix(name)), modifier.get());
    }

    @SafeVarargs
    private static HolderSet<ConfiguredWorldCarver<?>> carverSet(BootstrapContext<?> context, ResourceKey<ConfiguredWorldCarver<?>>... carvers) {
        return HolderSet.direct(Stream.of(carvers).map(key -> context.lookup(Registries.CONFIGURED_CARVER).getOrThrow(key)).collect(Collectors.toList()));
    }

    @SafeVarargs
    private static HolderSet<PlacedFeature> featureSet(BootstrapContext<?> context, ResourceKey<PlacedFeature>... features) {
        return HolderSet.direct(Stream.of(features).map(key -> context.lookup(Registries.PLACED_FEATURE).getOrThrow(key)).collect(Collectors.toList()));
    }

    @SafeVarargs
    private static void addCarver(BootstrapContext<BiomeModifier> context, String name, ResourceKey<ConfiguredWorldCarver<?>>... carver) {
        register(context, "add_carver/" + name, () -> new SphereBiomeCarverModifier(carverSet(context, carver)));
    }

    @SafeVarargs
    private static void addFeatureForAll(BootstrapContext<BiomeModifier> context, String name, ResourceKey<PlacedFeature>... features) {
        register(context, "add_feature/" + name, () -> new SphereBiomeFeatureModifier(featureSet(context, features)));
    }

    @SafeVarargs
    private static void addFeature(BootstrapContext<BiomeModifier> context, String name, HolderSet<Biome> biomes, ResourceKey<PlacedFeature>... features) {
        register(context, "add_feature/" + name, () -> new BiomeModifiers.AddFeaturesBiomeModifier(biomes, featureSet(context, features), GenerationStep.Decoration.TOP_LAYER_MODIFICATION));
    }
}
