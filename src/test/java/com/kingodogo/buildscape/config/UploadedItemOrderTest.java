package com.kingodogo.buildscape.config;

import org.junit.BeforeClass;
import org.junit.Test;

import com.kingodogo.buildscape.recipe.framework.cache.RecipeCacheRegressionTest;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class UploadedItemOrderTest {
    @BeforeClass
    public static void bootstrap() {
        RecipeCacheRegressionTest.bootstrap();
    }

    /**
     * A client's merged upload is a set (the server list plus the sender's additions). The server writes it
     * against the file, so the file order is the server's own list with the new entry appended.
     */
    @Test
    public void aMergedUploadKeepsTheServerListOrderAndAppendsTheNewEntry() {
        List<String> serverFile = List.of("minecraft:stone", "minecraft:emerald", "minecraft:dirt", "not an id");
        Set<String> merged = Set.of("minecraft:dirt", "minecraft:stone", "minecraft:emerald", "minecraft:diamond");
        assertEquals(
                List.of("minecraft:stone", "minecraft:emerald", "minecraft:dirt", "not an id", "minecraft:diamond"),
                PillarParticleConfig.orderedItemsForSave(serverFile, merged));
    }
}
