package com.kingodogo.buildscape.feature.geyser;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class GeyserLiftRulesTest {
    @Test
    public void ceilingStopsLiftBeforeTheBlockAndEverythingAboveIt() {
        assertEquals(-56, GeyserLiftRules.unobstructedTop(-59, y -> y != -55));
        assertEquals(-59, GeyserLiftRules.unobstructedTop(-59, y -> false));
        assertEquals(75, GeyserLiftRules.unobstructedTop(70, y -> y != 76));
    }

    @Test
    public void unobstructedColumnKeepsItsDocumentedReach() {
        List<Integer> visited = new ArrayList<>();
        assertEquals(-36, GeyserLiftRules.unobstructedTop(-59, y -> {
            visited.add(y);
            return true;
        }));
        assertEquals(23, visited.size());
        assertEquals(Integer.valueOf(-58), visited.get(0));
        assertEquals(Integer.valueOf(-36), visited.get(22));
    }

    @Test
    public void onlyTheFirstObstructionMatters() {
        List<Integer> visited = new ArrayList<>();
        assertEquals(2, GeyserLiftRules.unobstructedTop(0, y -> {
            visited.add(y);
            return y != 3 && y != 7;
        }));
        assertEquals(List.of(1, 2, 3), visited);
    }
}
