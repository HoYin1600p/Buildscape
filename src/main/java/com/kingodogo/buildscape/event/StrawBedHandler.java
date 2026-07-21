package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.StrawBedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = BuildScape.MODID)
public class StrawBedHandler {

    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
        Player player = event.getPlayer();
        if (player.level.isClientSide) {
            return;
        }

        // Get the position where the player was sleeping
        Optional<BlockPos> sleepPosOpt = player.getSleepingPos();
        if (sleepPosOpt.isPresent()) {
            BlockPos sleepPos = sleepPosOpt.get();
            BlockState state = player.level.getBlockState(sleepPos);

            // Check if the block is a Straw Bed
            if (state.getBlock() instanceof StrawBedBlock) {
                // Destroy the straw bed block (consumable bed) on wake up
                player.level.destroyBlock(sleepPos, false);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerSetSpawn(PlayerSetSpawnEvent event) {
        BlockPos newSpawn = event.getNewSpawn();
        if (newSpawn != null) {
            BlockState state = event.getPlayer().level.getBlockState(newSpawn);
            if (state.getBlock() instanceof StrawBedBlock) {
                event.setCanceled(true);
            }
        }
    }
}
