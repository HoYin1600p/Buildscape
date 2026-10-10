package com.kingodogo.buildscape.block;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kingodogo.buildscape.TestBootstrap;
import com.kingodogo.buildscape.adapter.v26x.BlockFactory;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Every property a wallpaper_flat blockstate JSON names must exist on the block the 26.x factory builds. */
class WallpaperFlatStateTest {
    private static final String[] COLORS = {"white", "orange", "magenta", "light_blue", "yellow", "lime", "pink",
            "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"};

    @BeforeAll
    static void bootstrap() throws Exception {
        TestBootstrap.initialize();
        setField("unregisteredIntrusiveHolders", new java.util.IdentityHashMap<>());
        setField("frozen", false);
    }

    @AfterAll
    static void restoreRegistry() throws Exception {
        setField("frozen", true);
        setField("unregisteredIntrusiveHolders", null);
    }

    private static void setField(String name, Object value) throws Exception {
        var field = MappedRegistry.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(BuiltInRegistries.BLOCK, value);
    }

    @Test
    void blockstatePropertiesExistOnWallpaperFlatBlocks() throws Exception {
        for (String color : COLORS) {
            String id = color + "_wallpaper_flat";
            Block block = new BlockFactory().createBlock(
                    new BlockDefinition(id, "WallpaperFlatBlock", CommonBlockProperties.of()));
            Set<String> names = new HashSet<>();
            block.getStateDefinition().getProperties().forEach(property -> names.add(property.getName()));
            try (var reader = new InputStreamReader(getClass().getResourceAsStream(
                    "/assets/buildscape/blockstates/" + id + ".json"), StandardCharsets.UTF_8)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                for (String used : usedProperties(json)) {
                    assertTrue(names.contains(used), id + " blockstate uses '" + used + "' but " + block.getClass().getSimpleName() + " has " + names);
                }
            }
        }
    }

    private static Set<String> usedProperties(JsonObject json) {
        Set<String> used = new HashSet<>();
        if (json.has("multipart")) {
            for (JsonElement part : json.getAsJsonArray("multipart")) {
                if (part.getAsJsonObject().has("when")) collectWhen(part.getAsJsonObject().get("when"), used);
            }
        }
        if (json.has("variants")) {
            for (String key : json.getAsJsonObject("variants").keySet()) {
                if (key.isEmpty()) continue;
                for (String pair : key.split(",")) used.add(pair.split("=")[0]);
            }
        }
        return used;
    }

    private static void collectWhen(JsonElement when, Set<String> used) {
        for (var entry : when.getAsJsonObject().entrySet()) {
            if (entry.getKey().equals("OR") || entry.getKey().equals("AND")) {
                for (JsonElement child : entry.getValue().getAsJsonArray()) collectWhen(child, used);
            } else {
                used.add(entry.getKey());
            }
        }
    }
}
