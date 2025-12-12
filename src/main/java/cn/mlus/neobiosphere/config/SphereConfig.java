package cn.mlus.neobiosphere.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class SphereConfig {
    private static final ModConfigSpec SPEC;
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<Number> RADIUS;
    public static final ModConfigSpec.ConfigValue<Number> SPACING;
    public static final ModConfigSpec.ConfigValue<Number> CENTER_Y;
    public static final ModConfigSpec.ConfigValue<String> SPHERE_BLOCK;
    public static final ModConfigSpec.ConfigValue<Boolean> ONLY_UPPER_HEMISPHERE;
    public static final ModConfigSpec.ConfigValue<Boolean> GENERATE_BRIDGE;
    public static final ModConfigSpec.ConfigValue<String> BRIDGE_BLOCK;
    public static final ModConfigSpec.ConfigValue<Number> BRIDGE_RADIUS;

    static {
        BUILDER.push("Settings");
        RADIUS = BUILDER.comment("Radius of the spheres").define("radius", 128);
        SPACING = BUILDER.comment("Spacing between the spheres").define("spacing", 500);
        CENTER_Y = BUILDER.comment("Center Y level for sphere generation").define("center_y", 62);
        SPHERE_BLOCK = BUILDER.comment("Block used for the spheres").define("sphere_block", "minecraft:glass", SphereConfig::validateItemName);
        ONLY_UPPER_HEMISPHERE = BUILDER.comment("Generate only the upper hemisphere of the spheres").define("only_upper_hemisphere", false);
        GENERATE_BRIDGE = BUILDER.comment("Generate bridges between spheres").define("generate_bridge", true);
        BRIDGE_BLOCK = BUILDER.comment("Block used for the bridges").define("bridge_block", "minecraft:oak_planks", SphereConfig::validateItemName);
        BRIDGE_RADIUS = BUILDER.comment("Radius of the bridges").define("bridge_radius", 1);
        SPEC = BUILDER.build();
    }

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }

    public static void setup(ModContainer modContainer)
    {
        modContainer.registerConfig(ModConfig.Type.COMMON, SPEC, "NeoBiosphere.toml");
    }
}
