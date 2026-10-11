package com.kingodogo.buildscape.showcase;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kingodogo.buildscape.TestBootstrap;
import com.kingodogo.buildscape.adapter.v26x.BlockFactory;
import com.kingodogo.buildscape.block.BlockDefinition;
import com.kingodogo.buildscape.block.ModBlocks;
import com.kingodogo.buildscape.platform.Services;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.IdentityHashMap;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

/** Standalone test-runtime tool; the recording Services shadows the loader service. */
public final class DumpBlockStates {
    private DumpBlockStates() {}

    public static void main(String[] args) throws Exception {
        if (args.length != 1) throw new IllegalArgumentException("Expected output JSON path");
        TestBootstrap.initialize();
        var holders = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
        var frozen = MappedRegistry.class.getDeclaredField("frozen");
        holders.setAccessible(true);
        frozen.setAccessible(true);
        Object prevHolders = holders.get(BuiltInRegistries.BLOCK);
        boolean wasFrozen = frozen.getBoolean(BuiltInRegistries.BLOCK);
        if (prevHolders == null) holders.set(BuiltInRegistries.BLOCK, new IdentityHashMap<>());
        frozen.setBoolean(BuiltInRegistries.BLOCK, false);
        JsonArray blocks = new JsonArray();
        JsonArray unavailable = new JsonArray();
        try {
            ModBlocks.init();
            BlockFactory factory = new BlockFactory();
            for (var supplier : Services.REGISTERED_BLOCKS) {
                if (!(supplier.get() instanceof BlockDefinition def)) {
                    throw new IllegalStateException("Registration is not a BlockDefinition: " + supplier);
                }
                JsonObject entry = new JsonObject();
                entry.addProperty("id", def.getNamespacedId());
                entry.addProperty("blockType", def.getBlockType());
                if (def.getId().equals("experience_liquid")) {
                    String reason = "Loader-created ExperienceFluids.pair() needs a running fluid registry";
                    entry.addProperty("className", "ExperienceFluidBlock");
                    entry.addProperty("manual", true);
                    entry.addProperty("headlessReason", reason);
                    JsonObject properties = new JsonObject();
                    JsonArray values = new JsonArray();
                    JsonArray states = new JsonArray();
                    for (int level = 0; level <= 15; level++) {
                        values.add(Integer.toString(level));
                        JsonObject state = new JsonObject();
                        state.addProperty("level", Integer.toString(level));
                        states.add(state);
                    }
                    properties.add("level", values);
                    entry.add("properties", properties);
                    entry.add("states", states);
                    unavailable.add(failure(def, reason, true));
                } else {
                    Block block;
                    try {
                        // Match BlockstatePropertiesTest's explicit intrusive-holder window.
                        // Its recording platform proxy cannot execute wrapRegistryAction callbacks.
                        block = factory.createBlock(def);
                    } catch (Exception | LinkageError failure) {
                        unavailable.add(failure(def, failure.toString(), false));
                        continue;
                    }
                    entry.addProperty("className", block.getClass().getSimpleName());
                    JsonObject properties = new JsonObject();
                    for (Property<?> property : block.getStateDefinition().getProperties()) {
                        properties.add(property.getName(), values(property));
                    }
                    JsonArray states = new JsonArray();
                    // Keep the engine's StateDefinition order, including singleton empty maps.
                    for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                        JsonObject map = new JsonObject();
                        for (Property<?> property : block.getStateDefinition().getProperties()) {
                            map.addProperty(property.getName(), value(property, state));
                        }
                        states.add(map);
                    }
                    entry.add("properties", properties);
                    entry.add("states", states);
                }
                blocks.add(entry);
            }
        } finally {
            frozen.setBoolean(BuiltInRegistries.BLOCK, wasFrozen);
            holders.set(BuiltInRegistries.BLOCK, prevHolders);
        }
        JsonObject dump = new JsonObject();
        dump.addProperty("schemaVersion", 1);
        dump.addProperty("minecraftVersion", "26.2");
        dump.add("blocks", blocks);
        dump.add("unavailable", unavailable);
        Path output = Path.of(args[0]).toAbsolutePath();
        Files.createDirectories(output.getParent());
        try (var writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(dump, writer);
        }
        System.out.println("Showcase: " + blocks.size() + " blocks, " + unavailable.size()
                + " headless exceptions (including manual fluid states), written to " + output);
    }

    private static JsonObject failure(BlockDefinition def, String reason, boolean coveredManually) {
        JsonObject failure = new JsonObject();
        failure.addProperty("id", def.getNamespacedId());
        failure.addProperty("blockType", def.getBlockType());
        failure.addProperty("reason", reason);
        failure.addProperty("coveredManually", coveredManually);
        return failure;
    }

    private static <T extends Comparable<T>> JsonArray values(Property<T> property) {
        JsonArray values = new JsonArray();
        for (T value : property.getPossibleValues()) values.add(property.getName(value));
        return values;
    }

    private static <T extends Comparable<T>> String value(Property<T> property, BlockState state) {
        return property.getName(state.getValue(property));
    }
}
