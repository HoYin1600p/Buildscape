package com.kingodogo.buildscape.pipe.transport;

import com.kingodogo.buildscape.block.HollowLogBlockEntity;
import com.kingodogo.buildscape.block.HollowPipeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.*;

/**
 * Concrete implementation of PipeFluidTransport for Water.
 * Implements deterministic flow propagation, priority rules (DOWN > Straight > Branch > UP with Bubble Column),
 * bubble-column elevators via Soul Sand / Magma, per-branch exponential slopes, and animated block-by-block flow.
 */
public class WaterPipeTransport extends PipeFluidTransport {

    public static final WaterPipeTransport INSTANCE = new WaterPipeTransport();
    public static final int MAX_NETWORK_SIZE = 512;
    public static final int MAX_FLOW_DISTANCE = 128;

    private static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{
            Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST
    };

    @Override
    public Fluid getFluidType() {
        return Fluids.WATER;
    }

    public record FlowStep(BlockPos pos, Direction arrivedFromDir, int distance) {}

    @Override
    public void recalculateNetwork(Level level, BlockPos startPos) {
        if (level == null || level.isClientSide || startPos == null) {
            return;
        }

        WorldPipeTopologyAccess topology = new WorldPipeTopologyAccess(level);

        // 1. Discover the connected pipe component
        Set<BlockPos> component = discoverComponent(topology, startPos);
        if (component.isEmpty()) {
            return;
        }

        // 2. Identify all water sources
        List<BlockPos> sources = new ArrayList<>();
        for (BlockPos pos : component) {
            if (topology.isWaterSource(pos)) {
                sources.add(pos);
            }
        }

        // 3. If NO sources exist, clear transport state across the entire component
        if (sources.isEmpty()) {
            for (BlockPos pos : component) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof HollowLogBlockEntity hollowBe) {
                    PipeFlowState oldState = hollowBe.getPipeFlowState();
                    if (!oldState.isEmpty()) {
                        // Animate drainage: delay by distance * 2 ticks so water recedes naturally
                        int delay = (oldState.getDistance() + 1) * 2;
                        hollowBe.setPendingFlowState(new PipeFlowState(), delay);
                        if (HollowPipeTransportManager.DEBUG_TRANSPORT) {
                            HollowPipeTransportManager.logDebug(String.format("Draining water state at %s (delay: %d ticks)", pos, delay));
                        }
                    }
                }
                // Clear any active outflow blocks associated with this pipe
                clearOutflow(level, pos);
            }
            return;
        }

        // 4. Simulate deterministic flow propagation from sources
        Map<BlockPos, PipeFlowState> newStates = calculateFlow(topology, component, sources);

        // 5. Handle open endpoints (outflow to the world)
        for (BlockPos pos : component) {
            PipeFlowState flow = newStates.get(pos);
            if (flow != null && flow.hasWater()) {
                BlockState currBlockState = level.getBlockState(pos);
                handleOutflowToEndpoints(level, pos, currBlockState, flow);
            }
        }

        // 6. Apply new states with block-by-block animated flow progression
        for (BlockPos pos : component) {
            PipeFlowState calculated = newStates.get(pos);
            if (calculated == null) {
                calculated = new PipeFlowState();
            }
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof HollowLogBlockEntity hollowBe) {
                PipeFlowState existing = hollowBe.getPipeFlowState();
                if (!existing.equals(calculated)) {
                    if (calculated.hasWater()) {
                        if (!existing.hasWater()) {
                            // Water arriving: delay activation by distance * 3 ticks (~0.15s per block)
                            int delay = calculated.getDistance() * 3;
                            hollowBe.setPendingFlowState(calculated, delay);
                        } else {
                            // Already wet: update immediately
                            hollowBe.setPendingFlowState(calculated, 0);
                        }
                    } else {
                        if (existing.hasWater()) {
                            // Water draining: delay by distance * 2 ticks
                            int delay = (existing.getDistance() + 1) * 2;
                            hollowBe.setPendingFlowState(calculated, delay);
                        } else {
                            hollowBe.setPendingFlowState(calculated, 0);
                        }
                    }

                    if (HollowPipeTransportManager.DEBUG_TRANSPORT) {
                        logPipeDebug(pos, calculated, level.getBlockState(pos), topology.getBubbleColumnBase(pos));
                    }
                }
            }
        }
    }

    /**
     * Core simulation algorithm operating through the PipeTopologyAccess interface.
     */
    public Map<BlockPos, PipeFlowState> calculateFlow(
            PipeTopologyAccess topology,
            Set<BlockPos> component,
            List<BlockPos> sources
    ) {
        Map<BlockPos, PipeFlowState> newStates = new HashMap<>(component.size());
        for (BlockPos pos : component) {
            newStates.put(pos, new PipeFlowState());
        }

        Queue<FlowStep> flowQueue = new ArrayDeque<>();
        for (BlockPos sourcePos : sources) {
            PipeFlowState state = newStates.get(sourcePos);
            if (state != null) {
                state.setHasWater(true);
                state.setSource(true);
                state.setDistance(0);

                // If source has bubble column base directly below, activate it immediately
                BubbleColumnState baseState = topology.getBubbleColumnBase(sourcePos);
                if (baseState != null && baseState != BubbleColumnState.NONE) {
                    state.setBubbleColumn(baseState);
                }

                flowQueue.add(new FlowStep(sourcePos, null, 0));
            }
        }

        Set<BlockPos> visitedInFlow = new HashSet<>();

        while (!flowQueue.isEmpty()) {
            FlowStep step = flowQueue.poll();
            BlockPos currPos = step.pos();
            Direction inDir = step.arrivedFromDir();
            int dist = step.distance();

            if (dist >= MAX_FLOW_DISTANCE) {
                continue;
            }

            PipeFlowState currFlow = newStates.get(currPos);
            if (currFlow == null || !currFlow.hasWater()) {
                continue;
            }

            // Check if this pipe has a bubble column base directly below
            BubbleColumnState baseState = topology.getBubbleColumnBase(currPos);
            if (baseState != null && baseState != BubbleColumnState.NONE && currFlow.getBubbleColumn() == BubbleColumnState.NONE) {
                currFlow.setBubbleColumn(baseState);
            }

            // Determine prioritized exit directions to connected pipes
            List<Direction> prioritizedExits = getPrioritizedExitDirections(topology, currPos, inDir, currFlow.getBubbleColumn());

            for (Direction exitDir : prioritizedExits) {
                currFlow.addFlowDirection(exitDir);
                BlockPos nextPos = currPos.relative(exitDir);
                PipeFlowState nextFlow = newStates.get(nextPos);

                if (nextFlow != null) {
                    boolean needsEnqueue = false;
                    int nextDist = dist + 1;

                    if (!nextFlow.hasWater() || nextFlow.getDistance() > nextDist) {
                        nextFlow.setHasWater(true);
                        nextFlow.setDistance(nextDist);
                        needsEnqueue = true;
                    }

                    // Propagate bubble column state upward or downward through contiguous vertical column
                    if (exitDir == Direction.UP && currFlow.getBubbleColumn() == BubbleColumnState.UP) {
                        if (nextFlow.getBubbleColumn() != BubbleColumnState.UP) {
                            nextFlow.setBubbleColumn(BubbleColumnState.UP);
                            needsEnqueue = true;
                        }
                    } else if (exitDir == Direction.DOWN && currFlow.getBubbleColumn() == BubbleColumnState.DOWN) {
                        if (nextFlow.getBubbleColumn() != BubbleColumnState.DOWN) {
                            nextFlow.setBubbleColumn(BubbleColumnState.DOWN);
                            needsEnqueue = true;
                        }
                    }

                    if (needsEnqueue) {
                        flowQueue.add(new FlowStep(nextPos, exitDir.getOpposite(), nextDist));
                    }
                }
            }

            // Also check for open endpoints (physical openings into the world) on this pipe
            // E.g., open straight continuation, open bottom, open sides
            for (Direction dir : Direction.values()) {
                if (inDir != null && dir == inDir) {
                    continue; // Don't flow backward into the entry face
                }
                if (topology.isOpenEndpoint(currPos, dir)) {
                    boolean allowed = (dir == Direction.DOWN)
                            || dir.getAxis().isHorizontal()
                            || (dir == Direction.UP && currFlow.getBubbleColumn() == BubbleColumnState.UP);
                    if (allowed) {
                        currFlow.addFlowDirection(dir);
                        currFlow.setOpenEndpoint(true);
                    }
                }
            }
        }

        // --- Post-processing: Compute per-branch maxDistance and mark open endpoints ---
        // Each branch calculates its own maxDistance along its downstream path from the source.
        // This ensures every branch independently calculates its own exponential slope and reaches
        // full drop at its own end.
        Map<BlockPos, Integer> memoMaxDist = new HashMap<>();
        for (Map.Entry<BlockPos, PipeFlowState> entry : newStates.entrySet()) {
            BlockPos pos = entry.getKey();
            PipeFlowState s = entry.getValue();
            if (s.hasWater()) {
                int branchMax = getBranchMaxDistance(pos, newStates, new HashSet<>(), memoMaxDist);
                s.setMaxDistance(Math.max(1, Math.max(s.getDistance(), branchMax)));

                // If this pipe has an open endpoint direction, mark it
                for (Direction dir : s.getFlowDirections()) {
                    BlockPos neighbor = pos.relative(dir);
                    PipeFlowState neighborState = newStates.get(neighbor);
                    if (neighborState == null || !neighborState.hasWater() || topology.isOpenEndpoint(pos, dir)) {
                        s.setOpenEndpoint(true);
                        break;
                    }
                }
            }
        }

        return newStates;
    }

    /**
     * Recursively traverses downstream flow directions in the component DAG to find
     * the maximum distance reached along this specific branch.
     */
    private int getBranchMaxDistance(BlockPos pos, Map<BlockPos, PipeFlowState> states, Set<BlockPos> visited, Map<BlockPos, Integer> memo) {
        if (memo.containsKey(pos)) {
            return memo.get(pos);
        }
        PipeFlowState s = states.get(pos);
        if (s == null || !s.hasWater()) {
            return 0;
        }
        int maxDist = s.getDistance();
        visited.add(pos);

        for (Direction dir : s.getFlowDirections()) {
            BlockPos nextPos = pos.relative(dir);
            PipeFlowState nextState = states.get(nextPos);
            if (nextState != null && nextState.hasWater() && !visited.contains(nextPos)) {
                maxDist = Math.max(maxDist, getBranchMaxDistance(nextPos, states, visited, memo));
            }
        }

        visited.remove(pos);
        memo.put(pos, maxDist);
        return maxDist;
    }

    /**
     * Determines exit directions ordered by priority:
     * 1. DOWN
     * 2. Straight horizontal continuation
     * 3. Horizontal turns/branches (NORTH, SOUTH, WEST, EAST)
     * 4. UP (STRICTLY requires BubbleColumnState.UP)
     */
    public List<Direction> getPrioritizedExitDirections(PipeTopologyAccess topology, BlockPos pos, Direction inDir, BubbleColumnState bubbleColumn) {
        List<Direction> exits = new ArrayList<>();

        // Priority 1: DOWN
        if (inDir != Direction.DOWN && topology.isConnected(pos, Direction.DOWN)) {
            exits.add(Direction.DOWN);
        }

        // Priority 2: Straight horizontal continuation
        Direction straightDir = null;
        if (inDir != null && inDir.getAxis().isHorizontal()) {
            straightDir = inDir.getOpposite();
            if (topology.isConnected(pos, straightDir) && !exits.contains(straightDir)) {
                exits.add(straightDir);
            }
        }

        // Priority 3: Other horizontal connected branches
        for (Direction hDir : HORIZONTAL_DIRECTIONS) {
            if (hDir != inDir && hDir != straightDir) {
                if (topology.isConnected(pos, hDir) && !exits.contains(hDir)) {
                    exits.add(hDir);
                }
            }
        }

        // Priority 4: UP (STRICTLY requires BubbleColumnState.UP)
        if (inDir != Direction.UP && bubbleColumn == BubbleColumnState.UP) {
            if (topology.isConnected(pos, Direction.UP) && !exits.contains(Direction.UP)) {
                exits.add(Direction.UP);
            }
        }

        return exits;
    }

    /**
     * Handles outflow at open endpoints (direction particles and spray at open outlets).
     */
    public void handleOutflowToEndpoints(Level level, BlockPos pos, BlockState state, PipeFlowState flowState) {
        // Outflow is cleanly contained and rendered with end-slopes and particle spray at open outlets.
    }

    /**
     * Clears any active outflow state associated with this pipe.
     */
    public void clearOutflow(Level level, BlockPos pos) {
    }


    /**
     * Discovers all connected Hollow Pipe blocks in the network component via BFS.
     */
    public Set<BlockPos> discoverComponent(PipeTopologyAccess topology, BlockPos startPos) {
        Set<BlockPos> component = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();

        if (topology.isHollowPipe(startPos)) {
            component.add(startPos);
            queue.add(startPos);
        } else {
            // If startPos was broken/removed, check the 6 adjacent neighbor blocks
            for (Direction dir : Direction.values()) {
                BlockPos adj = startPos.relative(dir);
                if (topology.isHollowPipe(adj)) {
                    if (component.add(adj)) {
                        queue.add(adj);
                    }
                }
            }
        }

        while (!queue.isEmpty() && component.size() < MAX_NETWORK_SIZE) {
            BlockPos current = queue.poll();
            for (Direction dir : Direction.values()) {
                if (topology.isConnected(current, dir)) {
                    BlockPos neighbor = current.relative(dir);
                    if (component.add(neighbor)) {
                        queue.add(neighbor);
                    }
                }
            }
        }
        return component;
    }

    private void logPipeDebug(BlockPos pos, PipeFlowState state, BlockState blockState, BubbleColumnState baseState) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n[PipeTransport]\n");
        sb.append(String.format("Position: %d %d %d\n", pos.getX(), pos.getY(), pos.getZ()));
        sb.append("Fluid: WATER\n");
        sb.append(String.format("Flow: %s\n", state.getFlowDirections().isEmpty() ? "NONE" : state.getFlowDirections().toString()));
        sb.append(String.format("Source: %s\n", state.isSource()));
        sb.append(String.format("BubbleColumn: %s\n", state.getBubbleColumn()));
        sb.append(String.format("Connected: %s\n", getConnectedDirections(blockState)));
        if (baseState != null) {
            sb.append(String.format("Base: %s\n", baseState == BubbleColumnState.UP ? "SOUL_SAND" : "MAGMA_BLOCK"));
        }
        HollowPipeTransportManager.logDebug(sb.toString());
    }
}
