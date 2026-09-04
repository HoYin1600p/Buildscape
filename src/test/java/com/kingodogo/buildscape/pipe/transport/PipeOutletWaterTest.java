package com.kingodogo.buildscape.pipe.transport;

import net.minecraft.core.Direction;

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
        System.out.println("Pipe outlet supply: " + checks + " checks passed.");
    }

    private static void expect(int expected, int actual, String label) {
        checks++;
        if (expected != actual) throw new AssertionError(label + ": expected " + expected + ", got " + actual);
    }
}
