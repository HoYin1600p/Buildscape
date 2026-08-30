package com.kingodogo.buildscape.pipe.transport;

import com.kingodogo.buildscape.block.HollowPipeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// Coordinator for Hollow Steel Pipe fluid transport network updates
@Mod.EventBusSubscriber(modid = "buildscape", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HollowPipeTransportManager {

    private static final Logger LOGGER = LogManager.getLogger("PipeTransport");
    public static boolean DEBUG_TRANSPORT = false;
    private static final Map<Level, Set<BlockPos>> PENDING_DIRTY = new ConcurrentHashMap<>();

    public static void markDirty(Level level, BlockPos pos) {
        if (level == null || level.isClientSide || pos == null) {
            return;
        }
        PENDING_DIRTY.computeIfAbsent(level, k -> Collections.synchronizedSet(new LinkedHashSet<>())).add(pos.immutable());
        processPendingRecalculations(level);
    }

    /**
     * Immediately processes and clears pending recalculations for the specified level.
     */
    public static void processPendingRecalculations(Level level) {
        Set<BlockPos> queue = PENDING_DIRTY.remove(level);
        if (queue == null || queue.isEmpty()) {
            return;
        }

        Set<BlockPos> pending;
        synchronized (queue) {
            pending = new LinkedHashSet<>(queue);
        }

        while (!pending.isEmpty()) {
            Iterator<BlockPos> it = pending.iterator();
            BlockPos nextPos = it.next();
            it.remove();

            Set<BlockPos> component = WaterPipeTransport.INSTANCE.recalculateNetwork(level, nextPos);
            if (component != null && !component.isEmpty()) {
                pending.removeAll(component);
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        // Process pending BFS pipe recalculations
        if (!PENDING_DIRTY.isEmpty()) {
            for (Level level : new ArrayList<>(PENDING_DIRTY.keySet())) {
                processPendingRecalculations(level);
            }
        }
    }

    public static void onBlockPlaced(Level level, BlockPos pos, BlockState state) {
        if (level == null || level.isClientSide || pos == null || state == null) {
            return;
        }
        if (state.hasProperty(HollowPipeBlock.WATERLOGGED) && state.getValue(HollowPipeBlock.WATERLOGGED)) {
            markDirty(level, pos);
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
