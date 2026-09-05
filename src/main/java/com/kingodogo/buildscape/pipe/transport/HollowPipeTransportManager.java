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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Mod.EventBusSubscriber(modid = "buildscape", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HollowPipeTransportManager {

    private static final Logger LOGGER = LogManager.getLogger("PipeTransport");
    private static final long RECALCULATION_BUDGET_NANOS = 2_000_000L;
    private static final int MAX_NETWORKS_PER_TICK = 16;
    private static final int MAX_ASYNC_NETWORKS = Math.max(1,
            Math.min(4, Runtime.getRuntime().availableProcessors() / 2));
    public static boolean DEBUG_TRANSPORT = false;
    private static final Map<Level, Set<BlockPos>> PENDING_DIRTY = new ConcurrentHashMap<>();
    private static final Map<Level, Map<BlockPos, NetworkJob>> IN_FLIGHT = new ConcurrentHashMap<>();
    private static final Queue<CompletedJob> COMPLETED = new ConcurrentLinkedQueue<>();
    private static final AtomicInteger ASYNC_NETWORKS = new AtomicInteger();

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
                            com.kingodogo.buildscape.BuildScape.getAsyncPool())
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

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

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
