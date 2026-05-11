package cn.mlus.neobiosphere.carver;

import cn.mlus.neobiosphere.config.SphereConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarverDebugSettings;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class SphereCarver extends WorldCarver<SphereCarver.SphereCarverConfig> {
    public SphereCarver(Codec<SphereCarverConfig> codec) {
        super(codec);
    }

    @Override
    public boolean carve(@NotNull CarvingContext context, @NotNull SphereCarverConfig config,
                         @NotNull ChunkAccess chunk, @NotNull Function<BlockPos, Holder<Biome>> biomeAccessor,
                         @NotNull RandomSource random, @NotNull Aquifer aquifer, @NotNull ChunkPos chunkPos,
                         @NotNull CarvingMask carvingMask) {

        int spacing = SphereConfig.SPACING.get().intValue();
        int centerY = SphereConfig.CENTER_Y.get().intValue();

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

                int radius = getSphereRadius(gridX, gridZ);
                if (SphereConfig.ONLY_UPPER_HEMISPHERE.get())
                    radius--;

                BlockState blockState = getSphereBlockState(gridX, gridZ);

                int minY = Math.max(chunk.getMinBuildHeight(), centerY - radius - 1);
                int maxY = Math.min(chunk.getMaxBuildHeight(), centerY + radius + 1);

                double shellThickness = 2.0;
                double innerRadiusSq = (radius - shellThickness) * (radius - shellThickness);
                double outerRadiusSq = radius * radius;

                for (int x = chunkStartX; x <= chunkEndX; x++) {
                    double dx = x - centerX;
                    double dx2 = dx * dx;

                    if (dx2 > outerRadiusSq) {
                        continue;
                    }

                    for (int z = chunkStartZ; z <= chunkEndZ; z++) {
                        double dz = z - centerZ;
                        double dz2 = dz * dz;
                        double horizontalDistSq = dx2 + dz2;

                        if (horizontalDistSq > outerRadiusSq) {
                            continue;
                        }

                        for (int y = minY; y <= maxY; y++) {
                            double dy = y - centerY;
                            double distanceSq = horizontalDistSq + dy * dy;

                            if (distanceSq >= innerRadiusSq && distanceSq <= outerRadiusSq) {
                                if(!SphereConfig.ONLY_UPPER_HEMISPHERE.get() || canReplaceBlock(config, chunk.getBlockState(new BlockPos(x, y, z)))) {
                                    if(SphereConfig.ONLY_UPPER_HEMISPHERE.get() && (y < 55 && !chunk.getBlockState(new BlockPos(x, y, z)).is(Blocks.WATER)))
                                        continue;
                                    BlockPos pos = new BlockPos(x, y, z);
                                    chunk.setBlockState(pos, blockState, false);
                                    generated = true;
                                }
                            }
                        }
                    }
                }
            }
        }

        return generated;
    }

    @Override
    protected boolean canReplaceBlock(@NotNull SphereCarverConfig config, @NotNull BlockState state) {
        return state.is(BlockTags.AIR) || state.is(Blocks.WATER);
    }

    @Override
    public boolean isStartChunk(@NotNull SphereCarverConfig config, @NotNull RandomSource random) {
        return true;
    }

    public static int getSphereGridX(double x) {
        return (int) Math.round(x / SphereConfig.SPACING.get().intValue());
    }

    public static int getSphereGridZ(double z) {
        return (int) Math.round(z / SphereConfig.SPACING.get().intValue());
    }

    public static double getSphereCenterX(int gridX) {
        return gridX * SphereConfig.SPACING.get().intValue();
    }

    public static double getSphereCenterZ(int gridZ) {
        return gridZ * SphereConfig.SPACING.get().intValue();
    }

    public static int getSphereRadius(int gridX, int gridZ) {
        int minRadius = SphereConfig.MIN_RADIUS.get().intValue();
        int maxRadius = SphereConfig.RADIUS.get().intValue();
        if (minRadius >= maxRadius) return maxRadius;
        int seed = gridX * 31 + gridZ;
        int hash = seed * 1664525 + 1013904223;
        return minRadius + Math.abs(hash) % (maxRadius - minRadius + 1);
    }

    public static int getSphereRadiusAt(double x, double z) {
        return getSphereRadius(getSphereGridX(x), getSphereGridZ(z));
    }

    public static BlockState getSphereBlockState(int gridX, int gridZ) {
        java.util.List<? extends String> sphereBlocks = SphereConfig.SPHERE_BLOCK.get();
        if (sphereBlocks.isEmpty()) return Blocks.AIR.defaultBlockState();
        int blockIndex = Math.abs(gridX * 31 + gridZ) % sphereBlocks.size();
        return BuiltInRegistries.BLOCK.get(
                ResourceLocation.parse(sphereBlocks.get(blockIndex))).defaultBlockState();
    }

    public static boolean isInsideAnySphere(double x, double y, double z) {
        int spacing = SphereConfig.SPACING.get().intValue();
        int centerY = SphereConfig.CENTER_Y.get().intValue();
        int gridX = getSphereGridX(x);
        int gridZ = getSphereGridZ(z);
        // Check the nearest sphere and its neighbors to handle edge cases
        for (int gx = gridX - 1; gx <= gridX + 1; gx++) {
            for (int gz = gridZ - 1; gz <= gridZ + 1; gz++) {
                double centerX = getSphereCenterX(gx);
                double centerZ = getSphereCenterZ(gz);
                int radius = getSphereRadius(gx, gz);
                double dx = x - centerX;
                double dy = y - centerY;
                double dz = z - centerZ;
                double shellThickness = 2.0;
                double innerRadiusSq = (radius - shellThickness) * (radius - shellThickness);
                if (dx * dx + dy * dy + dz * dz < innerRadiusSq) {
                    return true;
                }
            }
        }
        return false;
    }

    public static class SphereCarverConfig extends CarverConfiguration {
        public static final Codec<SphereCarverConfig> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        CarverConfiguration.CODEC.forGetter(c -> c),
                        Codec.INT.fieldOf("spacing").forGetter(c -> c.spacing),
                        Codec.INT.fieldOf("radius").forGetter(c -> c.radius),
                        Codec.INT.fieldOf("center_y").forGetter(c -> c.centerY),
                        Codec.DOUBLE.fieldOf("tolerance").forGetter(c -> c.tolerance),
                        BlockState.CODEC.fieldOf("block_state").forGetter(c -> c.blockState)
                ).apply(instance, SphereCarverConfig::new)
        );

        public final int spacing;
        public final int radius;
        public final int centerY;
        public final double tolerance;
        public final BlockState blockState;

        public SphereCarverConfig(float probability, HeightProvider y, FloatProvider yScale,
                                  VerticalAnchor lavaLevel, CarverDebugSettings debugSettings,
                                  int spacing, int radius, int centerY,
                                  double tolerance, BlockState blockState) {
            super(probability, y, yScale, lavaLevel, debugSettings, HolderSet.direct(Holder.direct(Blocks.AIR)));
            this.spacing = spacing;
            this.radius = radius;
            this.centerY = centerY;
            this.tolerance = tolerance;
            this.blockState = blockState;
        }

        public SphereCarverConfig(CarverConfiguration baseConfig, int spacing, int radius,
                                  int centerY, double tolerance, BlockState blockState) {
            this(baseConfig.probability, baseConfig.y, baseConfig.yScale, baseConfig.lavaLevel,
                    baseConfig.debugSettings, spacing, radius, centerY, tolerance, blockState);
        }
    }
}