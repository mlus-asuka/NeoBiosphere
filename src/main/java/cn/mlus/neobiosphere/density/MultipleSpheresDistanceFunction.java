package cn.mlus.neobiosphere.density;

import cn.mlus.neobiosphere.config.SphereConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.jetbrains.annotations.NotNull;

public record MultipleSpheresDistanceFunction(
        int spacing,
        int radius,
        int centerY
) implements DensityFunction.SimpleFunction {

    public static final MapCodec<MultipleSpheresDistanceFunction> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.INT.fieldOf("spacing").forGetter(MultipleSpheresDistanceFunction::spacing),
                    Codec.INT.fieldOf("radius").forGetter(MultipleSpheresDistanceFunction::radius),
                    Codec.INT.fieldOf("center_y").forGetter(MultipleSpheresDistanceFunction::centerY)
            ).apply(instance, MultipleSpheresDistanceFunction::new));
    public static final KeyDispatchDataCodec<MultipleSpheresDistanceFunction> KEY_CODEC = KeyDispatchDataCodec.of(CODEC);

    @Override
    public double compute(FunctionContext context) {
        int spacing = SphereConfig.SPACING.get().intValue();
        int radius = SphereConfig.RADIUS.get().intValue();
        int centerY = SphereConfig.CENTER_Y.get().intValue();

        // Calculate nearest grid point
        double gridX = Math.round((double)context.blockX() / spacing) * spacing;
        double gridZ = Math.round((double)context.blockZ() / spacing) * spacing;

        // Calculate distance to nearest sphere center
        double dx = context.blockX() - gridX;
        double dy = context.blockY() - centerY;
        double dz = context.blockZ() - gridZ;

        double distance = dx * dx + dy * dy + dz * dz;
        return distance / (radius * radius); // return value from 0 to 1, 0 = center, >1 = outside
    }

    @Override
    public double minValue() {
        return 0;
    }

    @Override
    public double maxValue() {
        return 2.0; // possible max value
    }

    @Override
    public @NotNull KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return KEY_CODEC;
    }
}
