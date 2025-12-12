package cn.mlus.neobiosphere.data;

import cn.mlus.neobiosphere.registry.SphereBiomeModifierSerializers;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeGenerationSettingsBuilder;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import org.jetbrains.annotations.NotNull;

public record SphereBiomeFeatureModifier(HolderSet<PlacedFeature> features) implements BiomeModifier {
    @Override
    public void modify(final @NotNull Holder<Biome> biome, final @NotNull Phase phase, final ModifiableBiomeInfo.BiomeInfo.@NotNull Builder builder) {
        if (phase == Phase.ADD) {
            BiomeGenerationSettingsBuilder generationSettings = builder.getGenerationSettings();
            this.features.forEach((holder) -> generationSettings.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION,holder));
        }
    }

    @Override
    public @NotNull MapCodec<? extends BiomeModifier> codec() {
        return SphereBiomeModifierSerializers.ADD_FEATURE.get();
    }

}
