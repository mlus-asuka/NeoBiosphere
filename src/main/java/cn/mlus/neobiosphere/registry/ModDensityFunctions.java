package cn.mlus.neobiosphere.registry;

import cn.mlus.neobiosphere.Neobiosphere;
import cn.mlus.neobiosphere.density.MultipleSpheresDistanceFunction;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDensityFunctions {
    public static final DeferredRegister<MapCodec<? extends DensityFunction>> DENSITY_FUNCTIONS =
            DeferredRegister.create(Registries.DENSITY_FUNCTION_TYPE, Neobiosphere.MODID);
    public static final DeferredHolder<MapCodec<? extends DensityFunction>, MapCodec<MultipleSpheresDistanceFunction>> MULTIPLE_SPHERES = register("multiple_spheres", MultipleSpheresDistanceFunction.CODEC);

    private static <T extends DensityFunction> DeferredHolder<MapCodec<? extends DensityFunction>, MapCodec<T>> register(String name, MapCodec<T> codec) {
        return DENSITY_FUNCTIONS.register(name, () -> codec);
    }

    public static final ResourceKey<DensityFunction> MULTIPLE_SPHERES_RAW = ResourceKey.create(Registries.DENSITY_FUNCTION, Neobiosphere.prefix("multiple_spheres"));
}