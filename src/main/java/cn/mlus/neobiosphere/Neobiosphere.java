package cn.mlus.neobiosphere;

import cn.mlus.neobiosphere.config.SphereConfig;
import cn.mlus.neobiosphere.registry.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(Neobiosphere.MODID)
public class Neobiosphere {
    public static final String MODID = "neobiosphere";

    public Neobiosphere(IEventBus modEventBus, ModContainer modContainer) {
        SphereConfig.setup(modContainer);
        ModDensityFunctions.DENSITY_FUNCTIONS.register(modEventBus);
        ModSurfaceCondition.SURFACE_RULES.register(modEventBus);
        ModCarvers.CARVERS.register(modEventBus);
        ModFeature.FEATURES.register(modEventBus);
        SphereBiomeModifierSerializers.SERIALIZERS.register(modEventBus);
    }

    public static ResourceLocation prefix(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
