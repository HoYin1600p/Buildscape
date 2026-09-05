package com.kingodogo.buildscape.pipe.transport;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class PipeOutletWaterTest {
    private static int checks;

    public static void main(String[] args) {
        for (Direction exit : Direction.Plane.HORIZONTAL) {
            PipeFlowState flow = new PipeFlowState();
            flow.setHasWater(true);
            flow.addFlowDirection(exit);
            for (int distance = 0; distance <= 8; distance++) {
                flow.setDistance(distance);
                expect(Math.max(0, 7 - distance), PipeOutletWater.amount(flow, exit, true, true), "remaining range");
            }
            flow.setDistance(3);
            expect(0, PipeOutletWater.amount(flow, exit.getOpposite(), true, true), "inactive open end");
            expect(0, PipeOutletWater.amount(flow, exit, false, true), "closed or connected face");
            expect(0, PipeOutletWater.amount(flow, exit, true, false), "dry pipe");
            flow.setHasWater(false);
            expect(0, PipeOutletWater.amount(flow, exit, true, true), "drained flow");
            expect(0, PipeOutletWater.amount(null, exit, true, true), "missing flow");
        }
        PipeFlowState flow = new PipeFlowState();
        flow.setHasWater(true);
        flow.addFlowDirection(Direction.DOWN);
        flow.addFlowDirection(Direction.UP);
        flow.setDistance(7);
        expect(8, PipeOutletWater.amount(flow, Direction.DOWN, true, true), "falling water resets range");
        expect(0, PipeOutletWater.amount(flow, Direction.UP, true, true), "no upward world spill");
        testHorizontalRange();
        testBranchDistances();
        testVerticalReset();
        System.out.println("Pipe outlet supply: " + checks + " checks passed.");
    }

    private static void testHorizontalRange() {
        TestTopology topology = new TestTopology();
        List<BlockPos> positions = topology.addLine(new BlockPos(0, 0, 0), Direction.EAST, 9);
        topology.sources.add(positions.get(0));
        topology.endpoints.computeIfAbsent(positions.get(8), ignored -> new HashSet<>()).add(Direction.EAST);

        Map<BlockPos, PipeFlowState> states = WaterPipeTransport.INSTANCE.calculateFlow(
                topology, Set.copyOf(positions), List.of(positions.get(0)));
        for (int distance = 0; distance <= 7; distance++) {
            PipeFlowState state = states.get(positions.get(distance));
            expect(true, state.hasWater(), "horizontal pipe supplied at " + distance);
            expect(distance, state.getDistance(), "horizontal distance at " + distance);
        }
        expect(false, states.get(positions.get(8)).hasWater(), "horizontal range limit");
        expect(7, states.get(positions.get(0)).getMaxDistance(), "horizontal branch maximum");
    }

    private static void testBranchDistances() {
        TestTopology topology = new TestTopology();
        BlockPos source = BlockPos.ZERO;
        List<BlockPos> east = topology.addLine(source, Direction.EAST, 8);
        List<BlockPos> west = topology.addLine(source, Direction.WEST, 4);
        topology.sources.add(source);

        Set<BlockPos> component = new HashSet<>(east);
        component.addAll(west);
        Map<BlockPos, PipeFlowState> states = WaterPipeTransport.INSTANCE.calculateFlow(
                topology, component, List.of(source));
        expect(7, states.get(source).getMaxDistance(), "longest branch selected");
        expect(3, states.get(west.get(1)).getMaxDistance(), "short branch maximum");
    }

    private static void testVerticalReset() {
        TestTopology topology = new TestTopology();
        BlockPos source = new BlockPos(0, 2, 0);
        BlockPos lower = source.below();
        topology.connect(source, Direction.DOWN, lower);
        List<BlockPos> run = topology.addLine(lower, Direction.EAST, 8);
        topology.sources.add(source);

        Set<BlockPos> component = new HashSet<>(run);
        component.add(source);
        Map<BlockPos, PipeFlowState> states = WaterPipeTransport.INSTANCE.calculateFlow(
                topology, component, List.of(source));
        expect(0, states.get(lower).getDistance(), "downward flow resets distance");
        expect(7, states.get(run.get(7)).getDistance(), "full range after downward reset");
    }

    private static void expect(int expected, int actual, String label) {
        checks++;
        if (expected != actual) throw new AssertionError(label + ": expected " + expected + ", got " + actual);
    }

    private static void expect(boolean expected, boolean actual, String label) {
        checks++;
        if (expected != actual) throw new AssertionError(label + ": expected " + expected + ", got " + actual);
    }

    private static final class TestTopology implements PipeTopologyAccess {
        private final Set<BlockPos> pipes = new HashSet<>();
        private final Set<BlockPos> sources = new HashSet<>();
        private final Map<BlockPos, Set<Direction>> connections = new HashMap<>();
        private final Map<BlockPos, Set<Direction>> endpoints = new HashMap<>();

        private List<BlockPos> addLine(BlockPos start, Direction direction, int count) {
            List<BlockPos> positions = new ArrayList<>(count);
            BlockPos current = start;
            pipes.add(current);
            positions.add(current);
            for (int index = 1; index < count; index++) {
                BlockPos next = current.relative(direction);
                connect(current, direction, next);
                positions.add(next);
                current = next;
            }
            return positions;
        }

        private void connect(BlockPos first, Direction direction, BlockPos second) {
            pipes.add(first);
            pipes.add(second);
            connections.computeIfAbsent(first, ignored -> new HashSet<>()).add(direction);
            connections.computeIfAbsent(second, ignored -> new HashSet<>()).add(direction.getOpposite());
        }

        @Override
        public boolean isHollowPipe(BlockPos pos) {
            return pipes.contains(pos);
        }

        @Override
        public boolean isConnected(BlockPos pos, Direction dir) {
            return connections.getOrDefault(pos, Set.of()).contains(dir);
        }

        @Override
        public boolean isOpenEndpoint(BlockPos pos, Direction dir) {
            return endpoints.getOrDefault(pos, Set.of()).contains(dir);
        }

        @Override
        public BubbleColumnState getBubbleColumnBase(BlockPos pos) {
            return BubbleColumnState.NONE;
        }

        @Override
        public boolean isWaterSource(BlockPos pos) {
            return sources.contains(pos);
        }
    }
}
