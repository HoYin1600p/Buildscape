package com.kingodogo.buildscape.config;

import com.google.gson.JsonObject;
import org.junit.Test;

import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class DefaultPillarItems405Test {
    @Test
    public void groupedDefaultsHaveNoDuplicatesAndGateVaultItems() throws Exception {
        for (boolean vaultLoaded : new boolean[]{false, true}) {
            StringWriter writer = new StringWriter();
            DefaultPillarItems.writeGrouped(writer, vaultLoaded);
            String text = writer.toString();
            JsonObject document = ConfigLayoutIO.readProperties(text);
            Set<String> unique = new HashSet<>();
            document.getAsJsonArray("items").forEach(item -> assertTrue(unique.add(item.getAsString())));
            assertEquals(DefaultPillarItems.items(vaultLoaded), unique);
            assertEquals(vaultLoaded, unique.contains("#the_vault:companion_relic"));
            assertEquals(vaultLoaded, document.has("vault_defaults_version"));
            assertTrue(unique.contains("buildscape:ashenking_gold_pillar"));
            assertTrue(unique.contains("buildscape:bone_dice"));
            assertTrue(text.indexOf("VANILLA MINECRAFT ITEMS") < text.indexOf("BUILDSCAPE ITEMS"));
        }
    }

    @Test
    public void migrationPreservesAnUnrestrictedEmptyList() {
        JsonObject document = new JsonObject();
        Set<String> items = new HashSet<>();
        assertTrue(DefaultPillarItems.merge405(document, items, true));
        assertTrue(items.isEmpty());
        assertFalse(DefaultPillarItems.merge405(document, items, true));
    }

    @Test
    public void migrationAddsOnlyNewItemsAndNeverRepeatsAfterRemoval() {
        JsonObject document = new JsonObject();
        Set<String> items = new HashSet<>(Set.of("example:custom"));
        assertTrue(DefaultPillarItems.merge405(document, items, false));
        assertTrue(items.contains("buildscape:bone_dice"));
        assertTrue(items.contains("example:custom"));
        assertFalse(items.contains("minecraft:diamond"));
        assertFalse(items.contains("buildscape:ancient_ashen_scroll"));
        assertFalse(items.contains("the_vault:greed_coin"));
        items.remove("buildscape:bone_dice");
        assertFalse(DefaultPillarItems.merge405(document, items, false));
        assertFalse(items.contains("buildscape:bone_dice"));

        assertTrue(DefaultPillarItems.merge405(document, items, true));
        assertTrue(items.contains("the_vault:greed_coin"));
        assertFalse(items.contains("the_vault:echo_pog"));
        assertFalse(items.contains("buildscape:bone_dice"));
        items.remove("the_vault:greed_coin");
        assertFalse(DefaultPillarItems.merge405(document, items, true));
        assertFalse(items.contains("the_vault:greed_coin"));
    }
}
