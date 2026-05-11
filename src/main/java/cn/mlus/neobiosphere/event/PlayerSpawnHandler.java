package cn.mlus.neobiosphere.event;

import cn.mlus.neobiosphere.carver.SphereCarver;
import cn.mlus.neobiosphere.util.SphereUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber
public class PlayerSpawnHandler {
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player entity = event.getEntity();
        Level level = entity.level();
        new Thread(() -> teleportIntoSphere(level, entity)).start();
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player entity = event.getEntity();
        Level level = entity.level();
        new Thread(() -> teleportIntoSphere(level, entity)).start();
    }

    private static void teleportIntoSphere(Level level, Player entity) {
        BlockPos currentPos = entity.blockPosition();

        if(!SphereUtil.isSphereBlock(level.getBlockState(currentPos))) {
            return;
        }

        int radius = SphereCarver.getSphereRadiusAt(currentPos.getX(), currentPos.getZ());
        BlockPos targetPos = findValidPosition(level, currentPos, radius);

        if (targetPos != null) {
            level.getServer().execute(() -> entity.teleportTo(targetPos.getX() + 0.5, targetPos.getY() + 1.0, targetPos.getZ() + 0.5));
        }
    }

    private static BlockPos findValidPosition(Level level, BlockPos center, int radius) {
        int minY = level.getMinBuildHeight();
        int maxY = level.getMaxBuildHeight() - 1;

        for (int y = center.getY(); y >= minY; y--) {
            BlockPos checkPos = new BlockPos(center.getX(), y, center.getZ());
            BlockState state = level.getBlockState(checkPos);

            if (isSolidBlock(state,level,checkPos) && !SphereUtil.isSphereBlock(state)) {
                if (hasEnoughSpace(level, checkPos.above())) {
                    return checkPos.above();
                }
            }
        }

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz <= radius * radius) {
                    for (int y = maxY; y >= minY; y--) {
                        BlockPos checkPos = new BlockPos(center.getX() + dx, y, center.getZ() + dz);
                        BlockState state = level.getBlockState(checkPos);

                        if (isSolidBlock(state,level,checkPos) && !SphereUtil.isSphereBlock(state)) {
                            if (hasEnoughSpace(level, checkPos.above())) {
                                return checkPos.above();
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    private static boolean isSolidBlock(BlockState state, Level level, BlockPos pos) {
        return state.isSolid() &&
                state.isCollisionShapeFullBlock(level, pos) &&
                !state.isAir();
    }

    private static boolean hasEnoughSpace(Level level, BlockPos pos) {
        return level.getBlockState(pos).isAir() &&
                level.getBlockState(pos.above()).isAir();
    }
}
