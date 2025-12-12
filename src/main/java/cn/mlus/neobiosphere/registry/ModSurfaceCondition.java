package cn.mlus.neobiosphere.registry;

import cn.mlus.neobiosphere.Neobiosphere;
import cn.mlus.neobiosphere.condition.InSphereCondition;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSurfaceCondition {
    public static final DeferredRegister<MapCodec<? extends SurfaceRules.ConditionSource>> SURFACE_RULES =
            DeferredRegister.create(BuiltInRegistries.MATERIAL_CONDITION, Neobiosphere.MODID);

    public static final DeferredHolder<MapCodec<? extends SurfaceRules.ConditionSource>, ?> SPHERE_CONDITION = SURFACE_RULES.register("in_sphere", InSphereCondition.KEY_CODEC::codec);
}
