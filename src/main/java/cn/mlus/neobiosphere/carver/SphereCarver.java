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
        int radius = SphereConfig.RADIUS.get().intValue();
        int centerY = SphereConfig.CENTER_Y.get().intValue();

        if(SphereConfig.ONLY_UPPER_HEMISPHERE.get())
            radius --;

        BlockState blockState = BuiltInRegistries.BLOCK.get(
                ResourceLocation.parse(SphereConfig.SPHERE_BLOCK.get())).defaultBlockState();

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