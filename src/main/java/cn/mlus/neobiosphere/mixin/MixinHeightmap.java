package cn.mlus.neobiosphere.mixin;

import cn.mlus.neobiosphere.config.SphereConfig;
import cn.mlus.neobiosphere.util.SphereUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Heightmap.class)
public class MixinHeightmap {

    @ModifyVariable(
            method = "update",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private BlockState onUpdateBlockState(BlockState state, int x, int y, int z) {
        if (SphereUtil.isSphereBlock(state) && y > SphereConfig.CENTER_Y.get().intValue()) {
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Redirect(
            method = "primeHeightmaps",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/chunk/ChunkAccess;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
            )
    )
    private static BlockState onPrimeGetBlockState(ChunkAccess instance, BlockPos pos) {
        BlockState original = instance.getBlockState(pos);
        if (SphereUtil.isSphereBlock(original) && pos.getY() > SphereConfig.CENTER_Y.get().intValue()) {
            return Blocks.AIR.defaultBlockState();
        }
        return original;
    }
}
