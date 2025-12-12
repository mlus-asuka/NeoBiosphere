package cn.mlus.neobiosphere.registry;

import cn.mlus.neobiosphere.Neobiosphere;
import cn.mlus.neobiosphere.data.SphereBiomeCarverModifier;
import cn.mlus.neobiosphere.data.SphereBiomeFeatureModifier;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;


public class SphereBiomeModifierSerializers {
    public static final DeferredRegister<MapCodec<? extends BiomeModifier>> SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Neobiosphere.MODID);

    private static boolean isInitialised = false;

    public static final DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<SphereBiomeCarverModifier>> ADD_CARVER = SERIALIZERS.register("sphere_add_carver", () ->
            RecordCodecBuilder.mapCodec(builder -> builder.group(
                    ConfiguredWorldCarver.LIST_CODEC.fieldOf("carvers").forGetter(SphereBiomeCarverModifier::carvers)
            ).apply(builder, SphereBiomeCarverModifier::new)));

    public static final DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<SphereBiomeFeatureModifier>> ADD_FEATURE = SERIALIZERS.register("sphere_add_feature", () ->
            RecordCodecBuilder.mapCodec(builder -> builder.group(
                    PlacedFeature.LIST_CODEC.fieldOf("feature").forGetter(SphereBiomeFeatureModifier::features)
            ).apply(builder, SphereBiomeFeatureModifier::new)));

    /**
     * Registers the {@link DeferredRegister} instance with the mod event bus.
     * <p>
     * This should be called during mod construction.
     *
     * @param modEventBus The mod event bus
     */
    public static void register(final IEventBus modEventBus) {
        if (isInitialised) {
            throw new IllegalStateException("Already initialised");
        }

        SERIALIZERS.register(modEventBus);

        isInitialised = true;
    }
}
