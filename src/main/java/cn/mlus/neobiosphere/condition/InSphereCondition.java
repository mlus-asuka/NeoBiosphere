package cn.mlus.neobiosphere.condition;

import cn.mlus.neobiosphere.config.SphereConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.jetbrains.annotations.NotNull;

/**
 *  Expect to shield surface rule from sphere top.
 */
public record InSphereCondition(
        int spacing,
        int radius,
        int centerY
) implements SurfaceRules.ConditionSource {

    private static final MapCodec<InSphereCondition> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.INT.fieldOf("spacing").forGetter(InSphereCondition::spacing),
                    Codec.INT.fieldOf("radius").forGetter(InSphereCondition::radius),
                    Codec.INT.fieldOf("center_y").forGetter(InSphereCondition::centerY)
            ).apply(instance, InSphereCondition::new)
    );

    public static final KeyDispatchDataCodec<InSphereCondition> KEY_CODEC = KeyDispatchDataCodec.of(CODEC);

    @Override
    public @NotNull KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
        return KEY_CODEC;
    }

    @Override
    public SurfaceRules.Condition apply(SurfaceRules.Context context) {
        return new Condition(context, spacing, radius, centerY);
    }

    private static class Condition extends SurfaceRules.LazyYCondition {
        private final int spacing;
        private final int radius;
        private final int centerY;

        public Condition(SurfaceRules.Context context, int spacing, int radius, int centerY) {
            super(context);
            this.spacing = SphereConfig.SPACING.get().intValue();
            this.radius = SphereConfig.RADIUS.get().intValue();
            this.centerY = SphereConfig.CENTER_Y.get().intValue();
        }

        @Override
        protected boolean compute() {
            int x = this.context.blockX;
            int y = this.context.blockY;
            int z = this.context.blockZ;

            int gridX = Math.round((float) x / spacing) * spacing;
            int gridZ = Math.round((float) z / spacing) * spacing;

            double dx = x - gridX;
            double dy = y - centerY;
            double dz = z - gridZ;
            double distanceSqr = dx * dx + dy * dy + dz * dz;

            return distanceSqr < radius * radius;
        }
    }
}
