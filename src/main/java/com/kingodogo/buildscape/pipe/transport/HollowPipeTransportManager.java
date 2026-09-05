package com.kingodogo.buildscape.pipe.transport;

import com.kingodogo.buildscape.block.HollowPipeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = "buildscape", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HollowPipeTransportManager {

    private static final Logger LOGGER = LogManager.getLogger("PipeTransport");
    private static final long RECALCULATION_BUDGET_NANOS = 2_000_000L;
    private static final int MAX_NETWORKS_PER_TICK = 16;
    public static boolean DEBUG_TRANSPORT = false;
    private static final Map<Level, Set<BlockPos>> PENDING_DIRTY = new ConcurrentHashMap<>();

    public static void markDirty(Level level, BlockPos pos) {
        if (level == null || level.isClientSide || pos == null) {
            return;
        }
        PENDING_DIRTY.computeIfAbsent(level, k -> Collections.synchronizedSet(new LinkedHashSet<>())).add(pos.immutable());
    }

    private static int processPendingRecalculations(Level level, long deadlineNanos, int networkBudget) {
        Set<BlockPos> queue = PENDING_DIRTY.get(level);
        if (queue == null) {
            return 0;
        }
        if (queue.isEmpty()) {
            PENDING_DIRTY.remove(level, queue);
            return 0;
        }

        int processed = 0;
        while (processed < networkBudget && System.nanoTime() < deadlineNanos) {
            BlockPos nextPos;
            synchronized (queue) {
                Iterator<BlockPos> iterator = queue.iterator();
                if (!iterator.hasNext()) {
                    PENDING_DIRTY.remove(level, queue);
                    break;
                }
                nextPos = iterator.next();
                iterator.remove();
            }

            if (!level.isLoaded(nextPos)) {
                continue;
            }

            Set<BlockPos> component = WaterPipeTransport.INSTANCE.recalculateNetwork(level, nextPos);
            if (component != null && !component.isEmpty()) {
                synchronized (queue) {
                    queue.removeAll(component);
                }
            }
            processed++;
        }

        synchronized (queue) {
            if (queue.isEmpty()) {
                PENDING_DIRTY.remove(level, queue);
            }
        }
        return processed;
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        long deadlineNanos = System.nanoTime() + RECALCULATION_BUDGET_NANOS;
        int remainingNetworks = MAX_NETWORKS_PER_TICK;
        if (!PENDING_DIRTY.isEmpty()) {
            for (Level level : new ArrayList<>(PENDING_DIRTY.keySet())) {
                remainingNetworks -= processPendingRecalculations(level, deadlineNanos, remainingNetworks);
                if (remainingNetworks <= 0 || System.nanoTime() >= deadlineNanos) {
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLevelUnload(WorldEvent.Unload event) {
        if (event.getWorld() instanceof Level level) {
            PENDING_DIRTY.remove(level);
        }
    }

    public static void onBlockPlaced(Level level, BlockPos pos, BlockState state) {
        if (level == null || level.isClientSide || pos == null || state == null) {
            return;
        }
        markDirty(level, pos);
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = pos.relative(dir);
            BlockState neighborState = level.getBlockState(neighborPos);
            if (neighborState.getBlock() instanceof HollowPipeBlock) {
                markDirty(level, neighborPos);
            }
        }
    }

    public static void onBlockRemoved(Level level, BlockPos pos, BlockState state) {
        if (level == null || level.isClientSide || pos == null || state == null) {
            return;
        }
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = pos.relative(dir);
            BlockState neighborState = level.getBlockState(neighborPos);
            if (neighborState.getBlock() instanceof HollowPipeBlock) {
                markDirty(level, neighborPos);
            }
        }
    }

    public static void onNeighborChanged(Level level, BlockPos pos, BlockState state, BlockPos neighborPos) {
        if (level == null || level.isClientSide || pos == null) {
            return;
        }

        BlockState neighborState = level.getBlockState(neighborPos);
        FluidState neighborFluid = level.getFluidState(neighborPos);

        boolean neighborIsPipe = neighborState.getBlock() instanceof HollowPipeBlock;
        boolean neighborIsFluid = !neighborFluid.isEmpty()
                && (neighborFluid.getType() == Fluids.WATER || neighborFluid.getType() == Fluids.FLOWING_WATER
                || neighborFluid.getType() == Fluids.LAVA || neighborFluid.getType() == Fluids.FLOWING_LAVA);

        boolean neighborIsOpenEndpoint = false;
        for (Direction direction : Direction.values()) {
            if (pos.relative(direction).equals(neighborPos)
                    && HollowPipeBlock.isOpenEndpoint(state, direction)) {
                neighborIsOpenEndpoint = true;
                break;
            }
        }

        if (neighborIsPipe || neighborIsFluid || neighborIsOpenEndpoint) {
            markDirty(level, pos);
        }
    }

    public static void onBucketUsed(Level level, BlockPos pos, BlockState state) {
        markDirty(level, pos);
    }

    public static void logDebug(String message) {
        if (DEBUG_TRANSPORT) {
            LOGGER.info(message);
        }
    }
}
