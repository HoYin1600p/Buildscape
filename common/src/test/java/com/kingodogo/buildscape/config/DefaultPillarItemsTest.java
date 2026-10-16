package com.kingodogo.buildscape.config;

import com.google.gson.JsonParser;
import com.kingodogo.buildscape.TestBootstrap;
import com.kingodogo.buildscape.platform.IPlatformAdapter;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DefaultPillarItemsTest {
    @TempDir Path directory;

    @Test
    void pillarAndPresetDefaultsAgreeWithAndWithoutVault() throws Exception {
        for (boolean vaultLoaded : new boolean[]{false, true}) {
            PillarParticleConfig config = new PillarParticleConfig(directory.resolve(String.valueOf(vaultLoaded)));
            config.loadItemsInternal(vaultLoaded);
            assertEquals(PresetsConfig.getDefaultItems(vaultLoaded), config.items);
            Set<String> expected = new HashSet<>(Arrays.asList(PillarParticleConfig.getDefaultItemArray()));
            VaultPillarItems.merge(expected, vaultLoaded);
            assertEquals(expected, config.items);
            assertEquals(vaultLoaded, config.items.contains("the_vault:greedy_anchor"));
            assertEquals(vaultLoaded, config.items.contains("#the_vault:vault_globe"));
            assertEquals(vaultLoaded ? 154 : 109, config.items.size());
            assertEquals(138, DefaultPillarItems.items(true).stream().filter(id -> !id.startsWith("#")).count());
        }
    }

    @Test
    void newFilesKeepSectionsAndDesignerOrder() throws Exception {
        for (boolean vaultLoaded : new boolean[]{false, true}) {
            Path folder = directory.resolve(String.valueOf(vaultLoaded));
            PillarParticleConfig config = new PillarParticleConfig(folder);
            config.loadItemsInternal(vaultLoaded);
            Path file = folder.resolve("pillar-items.json");
            String text = Files.readString(file);
            assertTrue(text.contains("// ===== VANILLA MINECRAFT ITEMS ====="));
            assertTrue(text.contains("// ===== BUILDSCAPEahh  ITEMS ====="));
            assertEquals(vaultLoaded, text.contains("// ===== VAULT / MODDED ITEMS ====="));
            assertTrue(text.indexOf("minecraft:elder_guardian_spawn_egg") < text.indexOf("minecraft:diamond_hoe"));
            assertTrue(text.indexOf("minecraft:netherite_sword") < text.indexOf("buildscape:ancient_ashen_scroll"));
            if (vaultLoaded) {
                assertTrue(text.indexOf("minecraft:netherite_sword") < text.indexOf("the_vault:vault_crystal"));
                assertTrue(text.indexOf("#the_vault:greed_trinket") < text.indexOf("buildscape:ancient_ashen_scroll"));
            }
            assertEquals(vaultLoaded ? 155 : 110,
                    JsonParser.parseString(text).getAsJsonObject().getAsJsonArray("items").size());
            config.loadItemsInternal(vaultLoaded);
            assertEquals(text, Files.readString(file));
        }
    }

    @Test
    void existingFilesKeepUnknownUserEntriesAndGainDefaults() throws Exception {
        for (boolean vaultLoaded : new boolean[]{false, true}) {
            Path folder = Files.createDirectories(directory.resolve(String.valueOf(vaultLoaded)));
            Path file = folder.resolve("pillar-items.json");
            Set<String> userItems = Set.of("other:custom", "#missing:tag", "buildscape:missing_item",
                    "the_vault:user_item", "odd:\"quoted", "minecraft:diamond");
            Files.writeString(file, new com.google.gson.Gson().toJson(java.util.Map.of("items", userItems)));
            PillarParticleConfig config = new PillarParticleConfig(folder);
            config.loadItemsInternal(vaultLoaded);
            Set<String> expected = new HashSet<>(userItems);
            expected.addAll(DefaultPillarItems.items(vaultLoaded));
            assertEquals(expected, config.items);
            String saved = Files.readString(file);
            assertTrue(saved.indexOf("#missing:tag") < saved.indexOf("minecraft:diamond"));
            assertFalse(saved.contains("===== VANILLA"));
            config.loadItemsInternal(vaultLoaded);
            assertEquals(expected, config.items);
            assertEquals(saved, Files.readString(file));
        }
    }

    @Test
    void unknownIdsAndMalformedOrMissingTagsAreIgnoredWithoutItemLookup() {
        TestBootstrap.initialize();
        IPlatformAdapter platform = (IPlatformAdapter) Proxy.newProxyInstance(
                getClass().getClassLoader(), new Class<?>[]{IPlatformAdapter.class}, (proxy, method, args) -> {
                    if (method.getName().equals("getItemId")) return CommonId.parse("minecraft:diamond");
                    if (method.getName().equals("isItemInTag")) {
                        assertEquals(CommonId.parse("missing:tag"), args[1]);
                        return false;
                    }
                    throw new AssertionError("Unexpected lookup: " + method.getName());
                });
        PillarParticleConfig config = new PillarParticleConfig(directory);
        config.items.addAll(Set.of("buildscape:missing_item", "missing:item", "#missing:tag", "#BAD TAG", "#"));
        ItemStack stack = new ItemStack(Items.DIAMOND);
        assertFalse(config.matchesUncached(stack, platform));
        config.items.add("minecraft:diamond");
        assertTrue(config.matchesUncached(stack, platform));
    }
}
