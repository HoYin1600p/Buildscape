package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.block.StrawBedBlock;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
public final class StrawBedHandler {

    private StrawBedHandler() {}
    public static void onPlayerWakeUp(Player player) {
        Level level = Services.PLATFORM.getEntityLevel(player);
        if (level == null || level.isClientSide()) {
            return;
        }

        Optional<BlockPos> sleepPosOpt = player.getSleepingPos();
        if (sleepPosOpt.isPresent()) {
            BlockPos sleepPos = sleepPosOpt.get();
            BlockState state = level.getBlockState(sleepPos);

            if (state.getBlock() instanceof StrawBedBlock) {
                level.destroyBlock(sleepPos, false);
            }
        }
    }
    public static boolean shouldCancelSpawn(Player player, BlockPos newSpawn) {
        if (newSpawn != null) {
            Level level = Services.PLATFORM.getEntityLevel(player);
            if (level != null) {
                BlockState state = level.getBlockState(newSpawn);
                if (state.getBlock() instanceof StrawBedBlock) {
                    return true;
                }
            }
        }
        return false;
    }
}
