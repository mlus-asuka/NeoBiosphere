package cn.mlus.neobiosphere.registry;

import cn.mlus.neobiosphere.Neobiosphere;
import cn.mlus.neobiosphere.carver.SphereBridgeCarver;
import cn.mlus.neobiosphere.carver.SphereCarver;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCarvers {

    public static final DeferredRegister<WorldCarver<?>> CARVERS = DeferredRegister.create(Registries.CARVER, Neobiosphere.MODID);
    public static final DeferredHolder<WorldCarver<?>,?> BIOSPHERE_CARVER = register("sphere_carver", new SphereCarver(SphereCarver.SphereCarverConfig.CODEC));
    public static final DeferredHolder<WorldCarver<?>,?> BIOSPHERE_BRIDGE_CARVER = register("sphere_bridge_carver", new SphereBridgeCarver(SphereCarver.SphereCarverConfig.CODEC));
    public static final ResourceKey<ConfiguredWorldCarver<?>> BIOSPHERE_CARVER_KEY = ResourceKey.create(Registries.CONFIGURED_CARVER, Neobiosphere.prefix("sphere"));
    public static final ResourceKey<ConfiguredWorldCarver<?>> BIOSPHERE_BRIDGE_CARVER_KEY = ResourceKey.create(Registries.CONFIGURED_CARVER, Neobiosphere.prefix("sphere_bridge"));

    private static DeferredHolder<WorldCarver<?>, WorldCarver<?>> register(String name, WorldCarver<?> worldCarver) {
        return CARVERS.register(name, () -> worldCarver);
    }
}
