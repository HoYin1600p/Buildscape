package com.kingodogo.buildscape.feature.pipe.transport;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Server-side scheduler for pipe network recalculation. Placing or removing a pipe, a neighbour change or a bucket use
 * marks positions dirty. Each server tick (end phase) it spends at most 2 ms and 16 networks: it applies finished
 * results, then starts new calculations. The flow of a network is calculated on {@code BuildScape.getAsyncPool()} (at
 * most four at once) from a snapshot, and applied on the server thread. A result is discarded and requeued if the
 * network changed while it was being calculated. State is in memory only and is cleared when a level unloads.
 */
@Mod.EventBusSubscriber(modid = "buildscape", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HollowPipeTransportManager {

    private static final Logger LOGGER = BuildScape.LOGGER;
    private static final long RECALCULATION_BUDGET_NANOS = 2_000_000L;
    private static final int MAX_NETWORKS_PER_TICK = 16;
    private static final int MAX_ASYNC_NETWORKS = Math.max(1,
            Math.min(4, Runtime.getRuntime().availableProcessors() / 2));
    /** When true, {@link #logDebug(String)} and the transport code write extra lines to the log. Off by default; there is no setting for it. */
    public static boolean DEBUG_TRANSPORT = false;
    private static final Map<Level, Set<BlockPos>> PENDING_DIRTY = new ConcurrentHashMap<>();
    private static final Map<Level, Map<BlockPos, NetworkJob>> IN_FLIGHT = new ConcurrentHashMap<>();
    private static final Queue<CompletedJob> COMPLETED = new ConcurrentLinkedQueue<>();
    private static final AtomicInteger ASYNC_NETWORKS = new AtomicInteger();

    /**
     * Queues the pipe at {@code pos} for recalculation. Ignored on the client. If a calculation already covers the
     * position, that calculation is flagged so its result is requeued instead of applied.
     */
    public static void markDirty(Level level, BlockPos pos) {
        if (level == null || level.isClientSide || pos == null) {
            return;
        }
        BlockPos immutablePos = pos.immutable();
        Map<BlockPos, NetworkJob> jobs = IN_FLIGHT.get(level);
        NetworkJob job = jobs == null ? null : jobs.get(immutablePos);
        if (job != null) {
            job.markDirty(immutablePos);
            return;
        }
        PENDING_DIRTY.computeIfAbsent(level, k -> Collections.synchronizedSet(new LinkedHashSet<>())).add(immutablePos);
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
        while (processed < networkBudget && System.nanoTime() < deadlineNanos
                && ASYNC_NETWORKS.get() < MAX_ASYNC_NETWORKS) {
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

            Map<BlockPos, NetworkJob> levelJobs = IN_FLIGHT.computeIfAbsent(level,
                    ignored -> new ConcurrentHashMap<>());
            NetworkJob overlapping = levelJobs.get(nextPos);
            if (overlapping != null) {
                overlapping.markDirty(nextPos);
                continue;
            }

            WaterPipeTransport.PreparedNetwork prepared = WaterPipeTransport.INSTANCE.prepareNetwork(level, nextPos);
            if (prepared == null || prepared.component().isEmpty()) {
                continue;
            }

            for (BlockPos componentPos : prepared.component()) {
                NetworkJob existing = levelJobs.get(componentPos);
                if (existing != null) {
                    existing.markDirty(nextPos);
                    overlapping = existing;
                }
            }
            if (overlapping != null) {
                continue;
            }

            NetworkJob job = new NetworkJob(level, prepared);
            for (BlockPos componentPos : prepared.component()) {
                levelJobs.put(componentPos, job);
            }
            ASYNC_NETWORKS.incrementAndGet();
            CompletableFuture.supplyAsync(
                    () -> WaterPipeTransport.INSTANCE.calculatePreparedNetwork(prepared),
                    BuildScape.getAsyncPool())
                    .whenComplete((states, error) -> {
                        ASYNC_NETWORKS.decrementAndGet();
                        COMPLETED.add(new CompletedJob(job, states, error));
                    });

            Set<BlockPos> component = prepared.component();
            if (!component.isEmpty()) {
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

    private static int processCompletedRecalculations(long deadlineNanos, int networkBudget) {
        int processed = 0;
        while (processed < networkBudget && System.nanoTime() < deadlineNanos) {
            CompletedJob completed = COMPLETED.poll();
            if (completed == null) {
                break;
            }

            NetworkJob job = completed.job();
            Map<BlockPos, NetworkJob> levelJobs = IN_FLIGHT.get(job.level);
            if (levelJobs != null) {
                for (BlockPos pos : job.prepared.component()) {
                    levelJobs.remove(pos, job);
                }
                if (levelJobs.isEmpty()) {
                    IN_FLIGHT.remove(job.level, levelJobs);
                }
            }

            if (job.cancelled.get()) {
                processed++;
                continue;
            }
            if (completed.error() != null) {
                LOGGER.error("Failed to calculate pipe network", completed.error());
                requeue(job, job.prepared.component());
                processed++;
                continue;
            }
            if (!job.dirtyPositions.isEmpty()) {
                requeue(job, job.dirtyPositions);
                processed++;
                continue;
            }
            if (!WaterPipeTransport.INSTANCE.applyPreparedNetwork(
                    job.level, job.prepared, completed.states())) {
                requeue(job, job.prepared.component());
            }
            processed++;
        }
        return processed;
    }

    private static void requeue(NetworkJob job, Collection<BlockPos> positions) {
        if (job.cancelled.get()) {
            return;
        }
        Set<BlockPos> queue = PENDING_DIRTY.computeIfAbsent(job.level,
                ignored -> Collections.synchronizedSet(new LinkedHashSet<>()));
        synchronized (queue) {
            for (BlockPos pos : positions) {
                if (job.level.isLoaded(pos)) {
                    queue.add(pos.immutable());
                }
            }
        }
    }

    /** End of every server tick: applies finished calculations first, then starts queued ones, within the time and network budget. */
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        long deadlineNanos = System.nanoTime() + RECALCULATION_BUDGET_NANOS;
        int remainingNetworks = MAX_NETWORKS_PER_TICK;
        remainingNetworks -= processCompletedRecalculations(deadlineNanos, remainingNetworks);
        if (!PENDING_DIRTY.isEmpty()) {
            for (Level level : new ArrayList<>(PENDING_DIRTY.keySet())) {
                remainingNetworks -= processPendingRecalculations(level, deadlineNanos, remainingNetworks);
                if (remainingNetworks <= 0 || System.nanoTime() >= deadlineNanos) {
                    break;
                }
            }
        }
    }

    /** Drops the queued positions of an unloading level and cancels its running calculations. */
    @SubscribeEvent
    public static void onLevelUnload(WorldEvent.Unload event) {
        if (event.getWorld() instanceof Level level) {
            PENDING_DIRTY.remove(level);
            Map<BlockPos, NetworkJob> jobs = IN_FLIGHT.remove(level);
            if (jobs != null) {
                new HashSet<>(jobs.values()).forEach(job -> job.cancelled.set(true));
            }
        }
    }

    private static final class NetworkJob {
        private final Level level;
        private final WaterPipeTransport.PreparedNetwork prepared;
        private final Set<BlockPos> dirtyPositions = ConcurrentHashMap.newKeySet();
        private final AtomicBoolean cancelled = new AtomicBoolean();

        private NetworkJob(Level level, WaterPipeTransport.PreparedNetwork prepared) {
            this.level = level;
            this.prepared = prepared;
        }

        private void markDirty(BlockPos pos) {
            dirtyPositions.add(pos.immutable());
        }
    }

    private record CompletedJob(NetworkJob job, Map<BlockPos, PipeFlowState> states, Throwable error) {
    }

    /** Server only: queues a placed pipe and the hollow pipes next to it. */
    public static void onBlockPlaced(Level level, BlockPos pos, BlockState state) {
        if (level == null || level.isClientSide || pos == null || state == null) {
            return;
        }
        markDirty(level, pos);
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = pos.relative(dir);
            BlockState neighborState = level.getBlockState(neighborPos);
            if (PipeFluidTransport.isHollowPipe(neighborState)) {
                markDirty(level, neighborPos);
            }
        }
    }

    /** Server only: queues the hollow pipes next to a removed pipe. */
    public static void onBlockRemoved(Level level, BlockPos pos, BlockState state) {
        if (level == null || level.isClientSide || pos == null || state == null) {
            return;
        }
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = pos.relative(dir);
            BlockState neighborState = level.getBlockState(neighborPos);
            if (PipeFluidTransport.isHollowPipe(neighborState)) {
                markDirty(level, neighborPos);
            }
        }
    }

    /** Server only: queues the pipe at {@code pos} when the changed neighbour is a hollow pipe, a transport fluid, or an open end of this pipe. */
    public static void onNeighborChanged(Level level, BlockPos pos, BlockState state, BlockPos neighborPos) {
        if (level == null || level.isClientSide || pos == null) {
            return;
        }

        BlockState neighborState = level.getBlockState(neighborPos);
        FluidState neighborFluid = level.getFluidState(neighborPos);

        boolean neighborIsPipe = PipeFluidTransport.isHollowPipe(neighborState);
        boolean neighborIsFluid = !neighborFluid.isEmpty()
                && WorldPipeTopologyAccess.isTransportFluid(neighborFluid.getType());

        boolean neighborIsOpenEndpoint = false;
        for (Direction direction : Direction.values()) {
            if (pos.relative(direction).equals(neighborPos)
                    && PipeFluidTransport.isOpenEndpoint(state, direction)) {
                neighborIsOpenEndpoint = true;
                break;
            }
        }

        if (neighborIsPipe || neighborIsFluid || neighborIsOpenEndpoint) {
            markDirty(level, pos);
        }
    }

    /** Queues the pipe at {@code pos} after a bucket was used on it. */
    public static void onBucketUsed(Level level, BlockPos pos, BlockState state) {
        markDirty(level, pos);
    }

    /** Logs the message at info level, only while {@link #DEBUG_TRANSPORT} is on. */
    public static void logDebug(String message) {
        if (DEBUG_TRANSPORT) {
            LOGGER.info(message);
        }
    }
}
