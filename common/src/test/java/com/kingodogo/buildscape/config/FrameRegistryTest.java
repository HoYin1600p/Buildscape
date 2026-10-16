package com.kingodogo.buildscape.config;

import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FrameRegistryTest {
    private static final String DIMENSION = "minecraft:overworld";
    private static final BlockPos POS = new BlockPos(12, 64, -8);

    private static PillarIdManager.PillarData frame(String id, String facing, long modified) {
        PillarIdManager.PillarData data = new PillarIdManager.PillarData(id, DIMENSION, POS);
        data.pillarType = "minecraft:item_frame";
        data.facing = facing;
        data.modifiedTime = modified;
        data.createdTime = 0;
        return data;
    }

    private static Map<String, PillarIdManager.PillarData> entries(PillarIdManager.PillarData... values) {
        Map<String, PillarIdManager.PillarData> result = new LinkedHashMap<>();
        for (PillarIdManager.PillarData data : values) result.put(data.id, data);
        return result;
    }

    @Test
    void lookupPrefersStoredIdentityOverNewerDuplicateInEitherOrder() {
        var own = frame("I-F0001", "north", 1);
        var stale = frame("I-F0002", "north", 100);
        assertEquals(own.id, PillarIdManager.findFrameId(Arrays.asList(stale, own), DIMENSION, POS, "north", own.id));
        assertEquals(own.id, PillarIdManager.findFrameId(Arrays.asList(own, stale), DIMENSION, POS, "north", own.id));
        assertNull(PillarIdManager.findFrameId(Collections.singleton(stale), DIMENSION, POS, "north", own.id));
    }

    @Test
    void lookupRejectsOtherFacingLegacyFacingOtherDimensionAndNeighbour() {
        var otherFacing = frame("I-F0001", "south", 1);
        var legacy = frame("I-F0002", null, 2);
        var otherDimension = frame("I-F0003", "north", 3);
        otherDimension.dimension = "minecraft:the_nether";
        var neighbour = frame("I-F0004", "north", 4);
        neighbour.x++;
        assertNull(PillarIdManager.findFrameId(Arrays.asList(otherFacing, legacy, otherDimension, neighbour),
                DIMENSION, POS, "north", null));
        assertNull(PillarIdManager.findFrameId(Collections.singleton(otherFacing), DIMENSION, POS, null, null));
    }

    @Test
    void lookupWithoutStoredIdSelectsNewestExactFrameAndNeverAPillar() {
        var old = frame("I-F0001", "north", 1);
        var latest = frame("I-F0002", "north", 2);
        var pillar = frame("S-P0001", "north", 10);
        pillar.pillarType = "buildscape:stone_pillar";
        assertEquals(latest.id, PillarIdManager.findFrameId(Arrays.asList(pillar, latest, old),
                DIMENSION, POS, "north", null));
    }

    @Test
    void cleanupKeepsNewestAndPreservesDifferentVanillaFacesAndPillars() {
        var old = frame("I-F0001", "north", 1);
        var latest = frame("I-F0002", "north", 2);
        var south = frame("I-F0003", "south", 3);
        var pillar = frame("S-P0001", null, 0);
        pillar.pillarType = "buildscape:stone_pillar";
        var data = entries(old, latest, south, pillar);
        assertEquals(1, PillarIdManager.deduplicateFrames(data, Collections.emptySet()));
        assertEquals(entries(latest, south, pillar), data);
        assertEquals(0, PillarIdManager.deduplicateFrames(data, Collections.emptySet()));
    }

    @Test
    void cleanupPrefersAlreadyRegisteredIdentityAndDropsFacingFreeLegacyEntry() {
        var own = frame("I-F0001", "north", 1);
        var latest = frame("I-F0002", "north", 20);
        var legacy = frame("I-F0003", null, 30);
        var data = entries(latest, legacy, own);
        assertEquals(2, PillarIdManager.deduplicateFrames(data, Collections.singleton(own.id)));
        assertEquals(entries(own), data);
    }

    @Test
    void coloredFramesAreUniqueAtPositionButOtherDimensionsRemainSeparate() {
        var old = frame("I-FW0001", "north", 1);
        old.pillarType = "buildscape:white_item_frame";
        var latest = frame("I-FR0002", "south", 2);
        latest.pillarType = "buildscape:red_item_frame";
        var elsewhere = frame("I-F0003", "south", 3);
        elsewhere.dimension = "minecraft:the_nether";
        var data = entries(old, latest, elsewhere);
        assertEquals(1, PillarIdManager.deduplicateFrames(data, Collections.emptySet()));
        assertEquals(entries(latest, elsewhere), data);
    }

    @Test
    void cleanupBreaksTimestampTiesDeterministically() {
        var first = frame("I-F0001", "north", 1);
        var second = frame("I-F0002", "north", 1);
        var forward = entries(first, second);
        var reverse = entries(second, first);
        PillarIdManager.deduplicateFrames(forward, Collections.emptySet());
        PillarIdManager.deduplicateFrames(reverse, Collections.emptySet());
        assertEquals(entries(second), forward);
        assertEquals(forward, reverse);
    }
}
