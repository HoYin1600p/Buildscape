package com.kingodogo.buildscape.feature.copper;

import org.junit.Test;

public final class CopperTransitionsTest {

    @Test
    public void transitionTableMapsChainsAndWax() {
        CopperTransitions<Object> table = new CopperTransitions<>();
        Object[] stages = {new String("copper"), new String("copper"),
                new String("copper"), new String("copper")};
        Object[] waxed = {new Object(), new Object(), new Object(), new Object()};
        table.addChain(stages[0], stages[1], stages[2], stages[3]);
        for (int i = 0; i < stages.length; i++) {
            table.addWaxPair(stages[i], waxed[i]);
        }
        for (int i = 0; i < stages.length; i++) {
            check(table.next(stages[i]) == (i == 3 ? null : stages[i + 1]), "next stage " + i);
            check(table.previous(stages[i]) == (i == 0 ? null : stages[i - 1]), "previous stage " + i);
            check(table.first(stages[i]) == stages[0], "lightning first stage " + i);
            check(table.waxed(stages[i]) == waxed[i], "waxing retains age " + i);
            check(table.unwaxed(waxed[i]) == stages[i], "wax removal retains age " + i);
            check(table.first(waxed[i]) == null && table.next(waxed[i]) == null
                    && table.previous(waxed[i]) == null, "waxed blocks are outside weathering chains " + i);
        }
        Object other = new String("copper");
        check(table.next(other) == null && table.first(other) == null, "lookup uses block identity");
        Object secondFresh = new Object();
        Object secondExposed = new Object();
        Object secondWeathered = new Object();
        Object secondOxidized = new Object();
        table.addChain(secondFresh, secondExposed, secondWeathered, secondOxidized);
        check(table.first(secondOxidized) == secondFresh, "independent family first stage");
        check(table.first(stages[3]) == stages[0], "adding a family preserves existing chains");
        check(table.unwaxed(other) == null && table.waxed(other) == null, "unmapped waxing is absent");
    }

    private static void check(boolean condition, String description) {
        if (!condition) {
            throw new AssertionError(description);
        }
    }
}
