package com.kingodogo.buildscape.event;

/** Item counts, rather than crafting callback counts, advance crafting milestones. */
public final class MilestoneCounter {
    private MilestoneCounter() {}

    public static int advance(int current, int amount) {
        return (int) Math.min(Integer.MAX_VALUE, (long) Math.max(0, current) + Math.max(0, amount));
    }

    public static int baseline(int current, Integer previous) {
        return previous == null || current < previous ? Math.max(0, current - 1) : previous;
    }

    public static boolean reached(int current, int baseline, int target) {
        return (long) current - baseline >= target;
    }
}
