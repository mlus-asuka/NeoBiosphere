package cn.mlus.neobiosphere.carver;

import cn.mlus.neobiosphere.config.SphereConfig;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class SphereBridgeCarver extends SphereCarver {

    public SphereBridgeCarver(Codec<SphereCarverConfig> codec) {
        super(codec);
    }

    @Override
    public boolean carve(@NotNull CarvingContext context, @NotNull SphereCarverConfig config,
                         @NotNull ChunkAccess chunk, @NotNull Function<BlockPos, Holder<Biome>> biomeAccessor,
                         @NotNull RandomSource random, @NotNull Aquifer aquifer, ChunkPos chunkPos,
                         @NotNull CarvingMask carvingMask) {
        int spacing = SphereConfig.SPACING.get().intValue();
        int centerY = SphereConfig.CENTER_Y.get().intValue();
        int bridgeRadius = SphereConfig.BRIDGE_RADIUS.get().intValue();
        boolean enableBridges = SphereConfig.GENERATE_BRIDGE.get();

        BlockState bridgeState = BuiltInRegistries.BLOCK.get(
                ResourceLocation.parse(SphereConfig.BRIDGE_BLOCK.get())).defaultBlockState();

        int chunkStartX = chunkPos.getMinBlockX();
        int chunkStartZ = chunkPos.getMinBlockZ();
        int chunkEndX = chunkStartX + 15;
        int chunkEndZ = chunkStartZ + 15;

        boolean generated = false;

        int gridXStart = (int)Math.floor((double)chunkStartX / spacing);
        int gridXEnd = (int)Math.ceil((double)chunkEndX / spacing);
        int gridZStart = (int)Math.floor((double)chunkStartZ / spacing);
        int gridZEnd = (int)Math.ceil((double)chunkEndZ / spacing);

        for (int gridX = gridXStart; gridX <= gridXEnd; gridX++) {
            for (int gridZ = gridZStart; gridZ <= gridZEnd; gridZ++) {
                double centerX = gridX * spacing;
                double centerZ = gridZ * spacing;

                if (enableBridges) {
                    generated |= carveBridge(chunk, centerX, centerZ, centerY,
                            centerX + spacing, centerZ, centerY,
                            spacing, bridgeRadius, bridgeState,
                            chunkStartX, chunkEndX, chunkStartZ, chunkEndZ);

                    generated |= carveBridge(chunk, centerX, centerZ, centerY,
                            centerX, centerZ + spacing, centerY,
                            spacing, bridgeRadius, bridgeState,
                            chunkStartX, chunkEndX, chunkStartZ, chunkEndZ);
                }
            }
        }

        return generated;
    }

    private boolean carveBridge(ChunkAccess chunk, double x1, double z1, double y1,
                                double x2, double z2, double y2,
                                double spacing, int bridgeRadius, BlockState bridgeState,
                                int chunkStartX, int chunkEndX, int chunkStartZ, int chunkEndZ) {
        boolean generated = false;

        double dx = x2 - x1;
        double dz = z2 - z1;
        double dy = y2 - y1;

        double length = Math.sqrt(dx*dx + dz*dz + dy*dy);
        if (length == 0) return false;

        double nx = dx / length;
        double nz = dz / length;
        double ny = dy / length;

        double bridgeRadiusSq = bridgeRadius * bridgeRadius;
        double bridgeRadiusMinusOneSq = (bridgeRadius - 1) * (bridgeRadius - 1);

        int minX = Math.max(chunkStartX, (int)Math.min(x1, x2) - bridgeRadius);
        int maxX = Math.min(chunkEndX, (int)Math.max(x1, x2) + bridgeRadius);
        int minY = Math.max(chunk.getMinBuildHeight(), (int)Math.min(y1, y2) - bridgeRadius);
        int maxY = Math.min(chunk.getMaxBuildHeight(), (int)Math.max(y1, y2) + bridgeRadius);
        int minZ = Math.max(chunkStartZ, (int)Math.min(z1, z2) - bridgeRadius);
        int maxZ = Math.min(chunkEndZ, (int)Math.max(z1, z2) + bridgeRadius);

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = minY; y <= maxY; y++) {
                    double ax = x - x1;
                    double az = z - z1;
                    double ay = y - y1;

                    double t = ax*nx + az*nz + ay*ny;

                    if (t < 0 || t > length) continue;

                    double projX = x1 + t * nx;
                    double projY = y1 + t * ny;
                    double projZ = z1 + t * nz;

                    double distX = x - projX;
                    double distY = y - projY;
                    double distZ = z - projZ;
                    double distSq = distX*distX + distY*distY + distZ*distZ;

                    if (distSq <= bridgeRadiusSq && distSq >= bridgeRadiusMinusOneSq) {
                        BlockPos pos = new BlockPos(x, y, z);
                        if (!isInsideAnySphere(x, y, z, chunk.getMinBuildHeight(), chunk.getMaxBuildHeight())) {
                            if (chunk.getBlockState(pos).isAir()) {
                                chunk.setBlockState(pos, bridgeState, false);
                                generated = true;
                            }
                        }
                    }
                }
            }
        }

        return generated;
    }

    private boolean isInsideAnySphere(double x, double y, double z, int minY, int maxY) {
        int spacing = SphereConfig.SPACING.get().intValue();
        int radius = SphereConfig.RADIUS.get().intValue();
        int centerY = SphereConfig.CENTER_Y.get().intValue();

        int gridX = (int)Math.round(x / spacing);
        int gridZ = (int)Math.round(z / spacing);

        double centerX = gridX * spacing;
        double centerZ = gridZ * spacing;

        double dx = x - centerX;
        double dy = y - centerY;
        double dz = z - centerZ;
        double distanceSq = dx*dx + dy*dy + dz*dz;

        double shellThickness = 2.0;
        double innerRadiusSq = (radius - shellThickness) * (radius - shellThickness);

        return distanceSq < innerRadiusSq;
    }
}