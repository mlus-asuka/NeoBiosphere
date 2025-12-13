package cn.mlus.neobiosphere.mixin;

import cn.mlus.neobiosphere.config.SphereConfig;
import cn.mlus.neobiosphere.registry.ModBiomeAccess;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiNoiseBiomeSource.class)
public abstract class MixinMultiNoiseBiomeSource{
    @Shadow public abstract Holder<Biome> getNoiseBiome(Climate.TargetPoint targetPoint);

    @Inject(method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;", at = @At("HEAD"), cancellable = true)
    private void getNoiseBiome(int x, int y, int z, Climate.Sampler sampler, CallbackInfoReturnable<Holder<Biome>> cir){
        Holder<Biome> biomeHolder = this.getNoiseBiome(sampler.sample(x, y, z));

        int sphereSpacing = SphereConfig.SPACING.get().intValue();
        int sphereRadius = SphereConfig.RADIUS.get().intValue();
        int centerY = SphereConfig.CENTER_Y.get().intValue();

        int i = QuartPos.toBlock(x);
        int j = QuartPos.toBlock(y);
        int k = QuartPos.toBlock(z);

        int gridX = Math.round((float) i / sphereSpacing) * sphereSpacing;
        int gridZ = Math.round((float) k / sphereSpacing) * sphereSpacing;

        double dx = i - gridX;
        double dy = j - centerY;
        double dz = k - gridZ;

        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (!biomeHolder.is(BiomeTags.IS_NETHER) && !biomeHolder.is(BiomeTags.IS_END) && distance - sphereRadius > 2) {
            cir.setReturnValue(ModBiomeAccess.LOOKUP.getOrThrow(Biomes.THE_VOID));
        }
    }
}
