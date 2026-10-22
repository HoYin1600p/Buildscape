package com.kingodogo.buildscape.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kingodogo.buildscape.registry.blocks.BlocksRegister;
import com.kingodogo.buildscape.testutil.BootstrappedGameTest;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
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

public final class BeaconBeamStopperTagTest extends BootstrappedGameTest {

    @Test
    public void tagListsEveryRequiredShapeAndOnlyRegisteredBlocks() throws IOException {
        Set<ResourceLocation> registered = new HashSet<>(Registry.BLOCK.keySet());
        for (RegistryObject<Block> entry : BlocksRegister.blocks().getEntries()) {
            registered.add(entry.getId());
        }

        String resource = "data/buildscape/tags/blocks/beacon_beam_stoppers.json";
        InputStream stream = getClass().getClassLoader().getResourceAsStream(resource);
        assertNotNull(resource, stream);
        Set<String> values = new HashSet<>();
        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            JsonObject tag = JsonParser.parseReader(reader).getAsJsonObject();
            assertFalse(tag.get("replace").getAsBoolean());
            for (JsonElement value : tag.getAsJsonArray("values")) {
                String id = value.getAsString();
                assertTrue("Unregistered block: " + id, registered.contains(new ResourceLocation(id)));
                assertTrue(id, BeaconBeamStoppers.isStopper(id, true));
                assertTrue("Duplicate block: " + id, values.add(id));
            }
        }
        assertEquals(Set.of(
                "minecraft:tinted_glass",
                "buildscape:tinted_glass_slab",
                "buildscape:tinted_glass_stairs",
                "buildscape:tinted_glass_vertical_slab",
                "buildscape:tinted_glass_ornament",
                "buildscape:big_tinted_glass_ornament"), values);
        assertFalse(values.contains("buildscape:tinted_glass_jar"));
    }
}
