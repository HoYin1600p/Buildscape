package com.kingodogo.buildscape.block;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kingodogo.buildscape.registry.ModBlocks;
import com.kingodogo.buildscape.testutil.RegistryTestSupport;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ToolSpeedTagsTest {
    @BeforeClass
    public static void bootstrap() {
        RegistryTestSupport.bootstrap();
    }

    @Test
    public void everyRegisteredVariantHasItsMatchingToolTag() throws IOException {
        Set<String> hoe = readTag("minecraft:mineable/hoe");
        Set<String> shovel = readTag("minecraft:mineable/shovel");
        Set<String> pickaxe = readTag("minecraft:mineable/pickaxe");
        Set<String> wool = readTag("minecraft:wool");
        Set<String> shears = readTag("buildscape:shears_mineable");
        Set<String> leaves = readTag("minecraft:leaves");
        int leafCount = 0;
        int mossCount = 0;
        int shovelCount = 0;
        int woolCount = 0;
        int bulbCount = 0;
        for (String name : registeredNames()) {
            String id = "buildscape:" + name;
            if (name.matches(".*_leaf_(hedge|layers)")) {
                assertTrue(id, hoe.contains(id));
                assertTrue(id, shears.contains(id));
                assertFalse(id, leaves.contains(id));
                leafCount++;
            }
            if (name.matches("(?:.*_)?moss_(layers|overlay)") || name.equals("moss_block_slab")
                    || name.equals("moss_block_vertical_slab")) {
                assertTrue(id, hoe.contains(id));
                mossCount++;
            }
            if (name.matches("(?:dirt|podzol|mycelium|snow|snow_bricks|snowy_grass_block)_(?:vertical_)?slab")
                    || name.equals("snow_overlay")) {
                assertTrue(id, shovel.contains(id));
                shovelCount++;
            }
            if (name.matches(".*_wool_(?:(?:vertical_)?slab|layers)")) {
                assertTrue(id, wool.contains(id));
                woolCount++;
            }
            if (name.endsWith("copper_bulb")) {
                assertTrue(id, pickaxe.contains(id));
                bulbCount++;
            }
        }
        assertEquals(56, leafCount);
        assertEquals(12, mossCount);
        assertEquals(13, shovelCount);
        assertEquals(80, woolCount);
        assertEquals(12, bulbCount);
    }

    @Test
    public void carpetLayersUseVanillaCarpetStrengthAndShearsSpeed() throws IOException {
        Set<String> wool = readTag("minecraft:wool");
        int count = 0;
        for (String name : registeredNames()) {
            if (!name.matches("[a-z_]+_carpet_layers")) {
                continue;
            }
            Block block = RegistryTestSupport.create(ModBlocks.BLOCKS, name);
            assertTrue(name, block instanceof WoolLayersBlock);
            assertEquals(name, 0.1f, block.defaultDestroyTime(), 0.0f);
            assertFalse(name, wool.contains("buildscape:" + name));
            count++;
        }
        assertEquals(16, count);
    }

    private static Set<String> registeredNames() {
        Set<String> names = new HashSet<>();
        for (RegistryObject<Block> entry : ModBlocks.BLOCKS.getEntries()) {
            names.add(entry.getId().getPath());
        }
        return names;
    }

    private static Set<String> readTag(String id) throws IOException {
        String[] parts = id.split(":", 2);
        String resource = "data/" + parts[0] + "/tags/blocks/" + parts[1] + ".json";
        InputStream stream = ToolSpeedTagsTest.class.getClassLoader().getResourceAsStream(resource);
        assertNotNull(resource, stream);
        Set<String> values = new HashSet<>();
        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            JsonObject tag = JsonParser.parseReader(reader).getAsJsonObject();
            if (tag.has("replace")) {
                assertFalse(resource, tag.get("replace").getAsBoolean());
            }
            for (JsonElement value : tag.getAsJsonArray("values")) {
                String entry = value.getAsString();
                if (entry.startsWith("#")) {
                    values.addAll(readTag(entry.substring(1)));
                } else {
                    values.add(entry);
                }
            }
        }
        return values;
    }
}
