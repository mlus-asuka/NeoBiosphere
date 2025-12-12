package cn.mlus.neobiosphere.mixin;

import cn.mlus.neobiosphere.registry.ModBiomeAccess;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiNoiseBiomeSourceParameterList.class)
public class MixinMultiNoiseBiomeSourceParameterList {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void catchLookup(MultiNoiseBiomeSourceParameterList.Preset preset, HolderGetter<Biome> getter, CallbackInfo ci) {
        ModBiomeAccess.register(getter);
    }
}
