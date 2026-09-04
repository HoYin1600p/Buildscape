package com.kingodogo.buildscape.pipe.transport;

import com.kingodogo.buildscape.block.HollowLogBlock;
import com.kingodogo.buildscape.block.HollowLogBlockEntity;
import com.kingodogo.buildscape.block.HollowPipeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import java.util.*;

public class WaterPipeTransport extends PipeFluidTransport {

    public static final WaterPipeTransport INSTANCE = new WaterPipeTransport();
    public static final int MAX_NETWORK_SIZE = 512;
    public static final int MAX_FLOW_DISTANCE = 128;
    public static final int MAX_HORIZONTAL_FLOW = 7;

    private static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{
            Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST
    };

    @Override
    public Fluid getFluidType() {
        return Fluids.WATER;
    }

    public record FlowStep(BlockPos pos, Direction arrivedFromDir, int distance) {}

    public Set<BlockPos> discoverComponent(PipeTopologyAccess topology, BlockPos startPos) {
        if (topology == null || startPos == null || !topology.isHollowPipe(startPos)) {
            return Collections.emptySet();
        }

        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        queue.add(startPos);
        visited.add(startPos);

        int maxNetworkLimit = com.kingodogo.buildscape.config.BuildScapeConfig.getMaxPipeNetworkSize();

        while (!queue.isEmpty() && visited.size() < maxNetworkLimit) {
            BlockPos curr = queue.poll();
            for (Direction dir : Direction.values()) {
                if (topology.isConnected(curr, dir)) {
                    BlockPos neighbor = curr.relative(dir);
                    if (topology.isHollowPipe(neighbor) && visited.add(neighbor)) {
                        queue.add(neighbor);
                    }
                }
            }
        }
        return visited;
    }

    @Override
    public Set<BlockPos> recalculateNetwork(Level level, BlockPos startPos) {
        if (level == null || level.isClientSide || startPos == null) {
            return Collections.emptySet();
        }

        WorldPipeTopologyAccess topology = new WorldPipeTopologyAccess(level);

        Set<BlockPos> component = discoverComponent(topology, startPos);
        if (component.isEmpty()) {
            return Collections.emptySet();
        }

        List<BlockPos> sources = new ArrayList<>();
        for (BlockPos pos : component) {
            if (topology.isWaterSource(pos)) {
                sources.add(pos);
            }
        }

        if (sources.isEmpty()) {
            for (BlockPos pos : component) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof HollowLogBlockEntity hollowBe) {
                    PipeFlowState oldState = hollowBe.getPipeFlowState();
                    if (!oldState.isEmpty()) {
                        int delay = (oldState.getDistance() + 1) * 2;
                        hollowBe.setPendingFlowState(new PipeFlowState(), delay);
                        if (HollowPipeTransportManager.DEBUG_TRANSPORT) {
                            HollowPipeTransportManager.logDebug(String.format("Draining water state at %s (delay: %d ticks)", pos, delay));
                        }
                    }
                }
                BlockState pipeState = level.getBlockState(pos);
                if (pipeState.hasProperty(HollowPipeBlock.WATER_LEVEL) && pipeState.getValue(HollowPipeBlock.WATER_LEVEL) > 0) {
                    level.setBlock(pos, pipeState.setValue(HollowPipeBlock.WATER_LEVEL, 0), 2);
                }
            }
            return component;
        }

        Map<BlockPos, PipeFlowState> newStates = calculateFlow(topology, component, sources);

        for (BlockPos pos : component) {
            PipeFlowState calculated = newStates.get(pos);
            if (calculated == null) {
                calculated = new PipeFlowState();
            }

            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof HollowLogBlockEntity hollowBe) {
                hollowBe.setPipeFlowState(calculated);
            }

            BlockState pipeState = level.getBlockState(pos);
            int targetLevel = 0;
            if (calculated.hasWater()) {
                if (calculated.isSource()) {
                    targetLevel = 7;
                } else {
                    targetLevel = Math.min(7, Math.max(1, 8 - calculated.getDistance()));
                }
            }
            int currentLevel = pipeState.hasProperty(HollowPipeBlock.WATER_LEVEL)
                    ? pipeState.getValue(HollowPipeBlock.WATER_LEVEL) : 0;
            if (targetLevel != currentLevel) {
                pipeState = pipeState.setValue(HollowPipeBlock.WATER_LEVEL, targetLevel);
                level.setBlock(pos, pipeState, 2);
            }

            if (calculated.hasWater()) {
                handleOutflowToEndpoints(level, pos, pipeState, calculated);
                level.scheduleTick(pos, pipeState.getBlock(), Fluids.WATER.getTickDelay(level));
            }

            if (HollowPipeTransportManager.DEBUG_TRANSPORT) {
                logPipeDebug(pos, calculated, pipeState, topology.getBubbleColumnBase(pos));
            }
        }
        return component;
    }

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
                int initialDistance = Math.min(MAX_HORIZONTAL_FLOW,
                        Math.max(0, topology.getInitialWaterFlowDistance(sourcePos)));
                state.setHasWater(true);
                state.setSource(true);
                state.setDistance(initialDistance);
                state.setInflowDirection(topology.getSourceInflowDirection(sourcePos));

                BubbleColumnState baseState = topology.getBubbleColumnBase(sourcePos);
                if (baseState != null && baseState != BubbleColumnState.NONE) {
                    state.setBubbleColumn(baseState);
                }

                flowQueue.add(new FlowStep(sourcePos, state.getInflowDirection(), initialDistance));
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

            BubbleColumnState baseState = topology.getBubbleColumnBase(currPos);
            if (baseState != null && baseState != BubbleColumnState.NONE && currFlow.getBubbleColumn() == BubbleColumnState.NONE) {
                currFlow.setBubbleColumn(baseState);
            }

            List<Direction> prioritizedExits = getPrioritizedExitDirections(topology, currPos, inDir, currFlow.getBubbleColumn());

            for (Direction exitDir : prioritizedExits) {
                currFlow.addFlowDirection(exitDir);
                BlockPos nextPos = currPos.relative(exitDir);
                PipeFlowState nextFlow = newStates.get(nextPos);

                if (nextFlow != null) {
                    boolean isDownwardDrop = (exitDir == Direction.DOWN);
                    boolean isUpwardBubble = (exitDir == Direction.UP && currFlow.getBubbleColumn() == BubbleColumnState.UP);

                    int nextDist = (isDownwardDrop || isUpwardBubble) ? 0 : (dist + 1);

                    if (!isDownwardDrop && !isUpwardBubble && nextDist > MAX_HORIZONTAL_FLOW) {
                        continue;
                    }

                    boolean needsEnqueue = false;

                    if (!nextFlow.hasWater() || nextFlow.getDistance() > nextDist) {
                        nextFlow.setHasWater(true);
                        nextFlow.setDistance(nextDist);
                        nextFlow.setInflowDirection(exitDir.getOpposite());
                        needsEnqueue = true;
                    }

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
                        visitedInFlow.add(nextPos);
                        flowQueue.add(new FlowStep(nextPos, exitDir.getOpposite(), nextDist));
                    }
                }
            }

            for (Direction dir : Direction.values()) {
                if (inDir != null && dir == inDir) {
                    continue;
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

        for (Map.Entry<BlockPos, PipeFlowState> entry : newStates.entrySet()) {
            BlockPos pos = entry.getKey();
            PipeFlowState s = entry.getValue();
            if (s.hasWater()) {
                int branchMax = getBranchMaxDistance(pos, newStates, new HashSet<>());
                s.setMaxDistance(Math.max(1, Math.max(s.getDistance(), branchMax)));

                boolean isTerminal = (s.getDistance() == s.getMaxDistance());
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

    private int getBranchMaxDistance(BlockPos pos, Map<BlockPos, PipeFlowState> states, Set<BlockPos> visited) {
        if (!visited.add(pos)) {
            return 0;
        }

        PipeFlowState state = states.get(pos);
        if (state == null || !state.hasWater()) {
            return 0;
        }

        int max = state.getDistance();
        for (Direction exitDir : state.getFlowDirections()) {
            BlockPos nextPos = pos.relative(exitDir);
            PipeFlowState nextState = states.get(nextPos);
            if (nextState != null && nextState.hasWater() && nextState.getDistance() > state.getDistance()) {
                int branchMax = getBranchMaxDistance(nextPos, states, visited);
                if (branchMax > max) {
                    max = branchMax;
                }
            }
        }
        return max;
    }

    public List<Direction> getPrioritizedExitDirections(
            PipeTopologyAccess topology,
            BlockPos pos,
            Direction inDir,
            BubbleColumnState bubbleColumn
    ) {
        List<Direction> validExits = new ArrayList<>();

        if (inDir != Direction.DOWN && topology.isConnected(pos, Direction.DOWN)) {
            validExits.add(Direction.DOWN);
        }

        if (inDir != null) {
            Direction straightDir = inDir.getOpposite();
            if (straightDir != Direction.DOWN && straightDir != Direction.UP) {
                if (topology.isConnected(pos, straightDir)) {
                    validExits.add(straightDir);
                }
            }
        }

        for (Direction dir : HORIZONTAL_DIRECTIONS) {
            if (dir == inDir) continue;
            if (inDir != null && dir == inDir.getOpposite()) continue;
            if (topology.isConnected(pos, dir)) {
                validExits.add(dir);
            }
        }

        if (bubbleColumn == BubbleColumnState.UP && inDir != Direction.UP) {
            if (topology.isConnected(pos, Direction.UP)) {
                validExits.add(Direction.UP);
            }
        }

        return validExits;
    }

    private void handleOutflowToEndpoints(Level level, BlockPos pos, BlockState state, PipeFlowState flow) {
        if (level == null || level.isClientSide || pos == null || state == null || flow == null || !flow.hasWater()) {
            return;
        }
        HollowPipeBlock.trySpreadToWorld(level, pos, state, Fluids.WATER,
                flow.getDistance(), flow.getFlowDirections());
    }

    private void clearOutflow(Level level, BlockPos pos) {
    }

    private void logPipeDebug(BlockPos pos, PipeFlowState flow, BlockState state, BubbleColumnState base) {
        HollowPipeTransportManager.logDebug(String.format(
                "Pipe at %s: hasWater=%b, isSource=%b, dist=%d, dirs=%s, bubble=%s, baseBelow=%s",
                pos, flow.hasWater(), flow.isSource(), flow.getDistance(),
                flow.getFlowDirections(), flow.getBubbleColumn(), base
        ));
    }
}
