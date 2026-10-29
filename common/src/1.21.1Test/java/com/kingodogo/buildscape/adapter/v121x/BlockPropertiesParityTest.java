package com.kingodogo.buildscape.adapter.v121x;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kingodogo.buildscape.block.BlockDefinition;
import com.kingodogo.buildscape.block.ModBlocks;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.registry.ContentRemovalPolicy;
import com.kingodogo.buildscape.trophy.Trophies;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.junit.jupiter.api.Test;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class BlockPropertiesParityTest {
    // Vanilla changed nether planks from wood to nether_wood sounds after 1.18.2.
    // These four blocks deliberately copy their vanilla plank bases. Allow only
    // the five exact replacement sound IDs, never state or numeric differences.
    private static final String RUNTIME_MIXIN = "<provided by a mixin at runtime>";
    private static final Map<String, String> ALLOWED_DIFFERENCES = netherWoodSounds();

    private static Map<String, String> netherWoodSounds() {
        var differences = new TreeMap<String, String>();
        for (String id : new String[]{"crimson_shelf", "warped_shelf", "crimson_vertical_slab", "warped_vertical_slab"}) {
            for (String event : new String[]{"break", "fall", "hit", "place", "step"}) {
                differences.put("buildscape:" + id + " sound." + event, "minecraft:block.nether_wood." + event);
            }
        }
        // On 1.21.1 Buildscape's stripped_bamboo_block is replaced by vanilla's (removals121x), and the stairs copy
        // their base's blast resistance, so they follow vanilla's 3.0 instead of main's custom block's 2.0.
        differences.put("buildscape:stripped_bamboo_block_stairs explosionResistance", "3.0");
        // VineBlockMixin adds the 'sheared' property to vine-derived blocks at runtime; unit tests run without
        // mixins, so the glow lights' property set and default state are checked in game instead.
        for (String id : new String[]{"glow_lights", "multicolor_glow_lights"}) {
            differences.put("buildscape:" + id + " properties", RUNTIME_MIXIN);
            differences.put("buildscape:" + id + " defaultState", RUNTIME_MIXIN);
        }
        return Map.copyOf(differences);
    }
    @Test
    void everyBlockMatchesMain1182() throws Exception {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
        ModBlocks.init();
        Trophies.init();
        JsonObject reference;
        try (var input = getClass().getResourceAsStream("/main-1.18.2-blocks.json")) {
            assertNotNull(input, "Missing main block parity dump");
            reference = JsonParser.parseReader(new InputStreamReader(input, StandardCharsets.UTF_8)).getAsJsonObject();
        }
        var failures = new ArrayList<String>();
        var factory = new BlockFactory();
        assertFalse(reference.entrySet().isEmpty(), "Empty block parity reference");
        var created = new TreeSet<String>();
        for (var entry : Services.REGISTRY.getRegisteredBlocks()) {
            try {
                Services.PLATFORM.wrapRegistryAction(() -> {
                    var trophy = Trophies.get(entry.getPath());
                    net.minecraft.world.level.block.Block block;
                    if (trophy != null) {
                        // Trophy queues supply real blocks, not BlockDefinitions. Resolve
                        // them through Trophies using the fixture's platform construction.
                        block = trophy.getBlock();
                    } else {
                        var definition = assertInstanceOf(BlockDefinition.class, entry.get(), entry.getId());
                        block = definition.getBlock();
                        if (block == null) block = factory.createBlock(definition);
                        definition.setBlock(block);
                    }
                    assertNotNull(block, entry.getId());
                    Services.PARITY_BLOCKS.put(entry.getPath(), block);
                });
                created.add(entry.getId());
            } catch (Throwable failure) {
                failures.add(entry.getId() + " construction: " + failure);
            }
        }
        for (var entry : reference.entrySet()) {
            String id = entry.getKey();
            if (ContentRemovalPolicy.V121X.stream().anyMatch(removal -> removal.block() && removal.id().equals(id))) continue;
            if (!created.contains(id)) {
                failures.add(id + " was not constructed");
                continue;
            }
            try {
                var block = Services.PARITY_BLOCKS.get(id.substring(id.indexOf(':') + 1));
                BlockState state = block.defaultBlockState();
                JsonObject expected = entry.getValue().getAsJsonObject();
                var properties = new TreeMap<String, TreeSet<String>>();
                for (Property<?> property : block.getStateDefinition().getProperties()) {
                    properties.put(property.getName(), values(property));
                }
                var expectedProperties = new TreeMap<String, TreeSet<String>>();
                expected.getAsJsonObject("properties").entrySet().forEach(property -> {
                    var values = new TreeSet<String>();
                    property.getValue().getAsJsonArray().forEach(value -> values.add(value.getAsString()));
                    expectedProperties.put(property.getKey(), values);
                });
                compare(failures, id, "properties", expectedProperties, properties);
                var defaults = new TreeMap<String, String>();
                state.getProperties().forEach(property -> defaults.put(property.getName(), stateValue(state, property)));
                compare(failures, id, "defaultState", expected.get("defaultState").getAsString(),
                        String.join(",", defaults.entrySet().stream().map(value -> value.getKey() + "=" + value.getValue()).toList()));
                compare(failures, id, "destroyTime", expected.get("destroyTime").getAsFloat(), state.getDestroySpeed(EmptyBlockGetter.INSTANCE, BlockPos.ZERO));
                compare(failures, id, "explosionResistance", expected.get("explosionResistance").getAsFloat(), block.getExplosionResistance());
                compare(failures, id, "light", expected.get("light").getAsInt(), state.getLightEmission());
                var sound = state.getSoundType();
                var sounds = Map.of("break", sound.getBreakSound(), "fall", sound.getFallSound(), "hit", sound.getHitSound(),
                        "place", sound.getPlaceSound(), "step", sound.getStepSound());
                sounds.forEach((kind, event) -> compare(failures, id, "sound." + kind,
                        expected.getAsJsonObject("sound").get(kind).getAsString(), event.getLocation().toString()));
            } catch (Throwable failure) {
                failures.add(id + " comparison: " + failure);
            }
        }
        assertTrue(failures.isEmpty(), () -> failures.size() + " block mismatches:\n" + String.join("\n", failures));
    }

    private static <T extends Comparable<T>> TreeSet<String> values(Property<T> property) {
        var result = new TreeSet<String>();
        property.getPossibleValues().forEach(value -> result.add(property.getName(value)));
        return result;
    }

    private static <T extends Comparable<T>> String stateValue(BlockState state, Property<T> property) {
        return property.getName(state.getValue(property));
    }

    private static void compare(ArrayList<String> failures, String id, String field, Object expected, Object actual) {
        String allowed = ALLOWED_DIFFERENCES.get(id + " " + field);
        if (!expected.equals(actual) && !RUNTIME_MIXIN.equals(allowed) && !String.valueOf(actual).equals(allowed)) {
            failures.add(id + " " + field + ": expected " + expected + ", actual " + actual);
        }
    }
}



