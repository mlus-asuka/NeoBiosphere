package cn.mlus.neobiosphere.util;

import cn.mlus.neobiosphere.config.SphereConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class SphereUtil {
    public static boolean isInSideSphere(int x, int y, int z, int radius, int spacing, int centetY){
        // Calculate nearest grid point
        int gridX = Math.round((float) x / spacing) * spacing;
        int gridZ = Math.round((float) z / spacing) * spacing;

        // Calculate distance to nearest sphere center
        double dx = x - gridX;
        double dy = y - centetY;
        double dz = z - gridZ;

        double distanceSquared = dx*dx + dy*dy + dz*dz;
        return distanceSquared <= radius * radius;
    }

    public static boolean isSphereBlock(BlockState state){
        return state.is(BuiltInRegistries.BLOCK.get(ResourceLocation.parse(SphereConfig.SPHERE_BLOCK.get())));
    }
}
