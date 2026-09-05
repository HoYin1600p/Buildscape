package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class TreeChopJobManager {

    private static final int TICKS_BETWEEN_BATCHES = 2;
    private static final int MAX_BATCH_SIZE = 8;
    private static final Map<UUID, TreeChopJob> JOBS = new HashMap<>();

    private TreeChopJobManager() {
    }

    public static void start(ServerPlayer player, ServerLevel level, Block targetBlock, Iterable<BlockPos> blocks) {
        List<BlockPos> positions = new ArrayList<>();
        for (BlockPos block : blocks) {
            positions.add(block.immutable());
        }
        if (!positions.isEmpty()) {
            JOBS.put(player.getUUID(), new TreeChopJob(level, targetBlock, positions));
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null || !server.isRunning()) {
            JOBS.clear();
            return;
        }
        Iterator<Map.Entry<UUID, TreeChopJob>> iterator = JOBS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, TreeChopJob> entry = iterator.next();
            ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
            if (player == null || entry.getValue().tick(server.getTickCount(), player)) {
                iterator.remove();
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getPlayer() instanceof ServerPlayer) {
            JOBS.remove(event.getPlayer().getUUID());
        }
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        JOBS.clear();
    }

    private static final class TreeChopJob {
        private final ServerLevel level;
        private final Block targetBlock;
        private final List<BlockPos> blocks;
        private int currentIndex;
        private int batchSize = 1;
        private int completedBatches;
        private int nextTick;

        private TreeChopJob(ServerLevel level, Block targetBlock, List<BlockPos> blocks) {
            this.level = level;
            this.targetBlock = targetBlock;
            this.blocks = blocks;
            this.nextTick = level.getServer().getTickCount();
        }

        private boolean tick(int serverTick, ServerPlayer player) {
            if (serverTick < nextTick) {
                return false;
            }
            if (!player.isAlive()
                    || !player.isCreative()
                    || !player.hasPermissions(2)
                    || !player.mayBuild()
                    || player.getLevel() != level) {
                return true;
            }

            int end = Math.min(currentIndex + batchSize, blocks.size());
            for (int i = currentIndex; i < end; i++) {
                BlockPos pos = blocks.get(i);
                if (!level.isLoaded(pos)
                        || player.blockActionRestricted(level, pos, player.gameMode.getGameModeForPlayer())) {
                    continue;
                }
                BlockState state = level.getBlockState(pos);
                if (state.getBlock() != targetBlock) {
                    continue;
                }
                if (i % 3 == 0) {
                    level.levelEvent(2001, pos, Block.getId(state));
                }
                level.destroyBlock(pos, false);
            }

            currentIndex = end;
            completedBatches++;
            if (completedBatches % 3 == 0 && batchSize < MAX_BATCH_SIZE) {
                batchSize++;
            }
            nextTick = serverTick + TICKS_BETWEEN_BATCHES;
            return currentIndex >= blocks.size();
        }
    }
}
