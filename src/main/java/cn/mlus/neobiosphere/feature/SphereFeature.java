package cn.mlus.neobiosphere.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import org.jetbrains.annotations.NotNull;

/**
 * Not in use
 */
public class SphereFeature extends Feature<SphereFeature.Config> {

    public SphereFeature(Codec<Config> codec) {
        super(codec);
    }

    @Override
    public boolean place(@NotNull FeaturePlaceContext<Config> context) {
        Config config = context.config();
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();

        double spacing = config.spacing();
        double radius = config.radius();
        int centerY = config.centerY();
        double tolerance = config.tolerance();
        BlockState blockState = config.blockState();

        double gridX = Math.round((double) origin.getX() / spacing) * spacing;
        double gridZ = Math.round((double) origin.getZ() / spacing) * spacing;

        BlockPos sphereCenter = new BlockPos((int) gridX, centerY, (int) gridZ);

        int radiusInt = (int) Math.ceil(radius);
        int minX = sphereCenter.getX() - radiusInt;
        int maxX = sphereCenter.getX() + radiusInt;
        int minY = sphereCenter.getY() - radiusInt;
        int maxY = sphereCenter.getY() + radiusInt;
        int minZ = sphereCenter.getZ() - radiusInt;
        int maxZ = sphereCenter.getZ() + radiusInt;

        boolean placedAny = false;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);

                    double dx = x - sphereCenter.getX();
                    double dy = y - sphereCenter.getY();
                    double dz = z - sphereCenter.getZ();
                    double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

                    if (Math.abs(distance - radius) <= tolerance) {
                        level.setBlock(pos, blockState, 3);
                        placedAny = true;

                    }
                }
            }
        }

        return placedAny;
    }

    public record Config(
            double spacing,
            double radius,
            int centerY,
            double tolerance,
            BlockState blockState
    ) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.DOUBLE.fieldOf("spacing").forGetter(c -> c.spacing),
                        Codec.DOUBLE.fieldOf("radius").forGetter(c -> c.radius),
                        Codec.INT.fieldOf("center_y").forGetter(c -> c.centerY),
                        Codec.DOUBLE.fieldOf("tolerance").forGetter(c -> c.tolerance),
                        BlockState.CODEC.fieldOf("block_state").forGetter(c -> c.blockState)
                ).apply(instance, Config::new)
        );
    }
}