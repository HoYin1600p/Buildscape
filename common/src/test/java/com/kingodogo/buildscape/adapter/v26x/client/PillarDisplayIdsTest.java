package com.kingodogo.buildscape.adapter.v26x.client;

import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PillarDisplayIdsTest {
    @Test void displayIdsAreNonzeroAndUniqueAcrossConcurrentDisplays() {
        Set<Integer> ids = ConcurrentHashMap.newKeySet();
        IntStream.range(0, 1024).parallel().forEach(unused -> {
            int id = ClientPillarEntities.nextDisplayId();
            assertTrue(id < 0, "Display IDs must be nonzero and distinct from server IDs");
            assertTrue(ids.add(id), "Two displays must not share a cache ID");
        });
        assertEquals(1024, ids.size());
    }
}
