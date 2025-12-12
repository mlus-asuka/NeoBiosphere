package cn.mlus.neobiosphere.registry;

import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.biome.Biome;

public class ModBiomeAccess {
    public static HolderGetter<Biome> LOOKUP;

    public static void register(HolderGetter<Biome> lookup) {
        LOOKUP = lookup;
    }
}
