package com.kingodogo.buildscape.block;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kingodogo.buildscape.TestBootstrap;
import com.kingodogo.buildscape.adapter.v26x.BlockFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.kingodogo.buildscape.platform.Services;
import java.util.LinkedHashSet;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Builds every Buildscape block from the definitions in {@link ModBlocks} and checks that each blockstate JSON
 * only names properties (and values) that the built block really has. The registration helper in ModBlocks needs
 * the test-only Services stand-in that records the registrations.
 */
class BlockstatePropertiesTest {
    private static final Path MAIN_RESOURCES = Path.of(System.getProperty("buildscape.mainResources"));
    private static final Path BLOCKSTATES = MAIN_RESOURCES.resolve("assets/buildscape/blockstates");

    /** Blocks that cannot be built without a running game; id -> reason. Keep this list small. */
    private static final Map<String, String> SKIPS = Map.of(
            "experience_liquid", "its fluid pair is created by the loader fluid registration (ExperienceFluids.pair()), which needs a running registry");

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

    private static List<BlockDefinition> definitions() throws IOException {
        ModBlocks.init(); // runs ModBlocks' static registration against the recording test Services
        List<BlockDefinition> result = new ArrayList<>();
        for (var supplier : Services.REGISTERED_BLOCKS) {
            if (supplier.get() instanceof BlockDefinition def) result.add(def);
        }
        return result;
    }

    @Test
    void everyBlockHasABlockstateUsingOnlyItsOwnProperties() throws Exception {
        List<BlockDefinition> defs = definitions();
        assertTrue(defs.size() > 3000, "expected the full block list, got " + defs.size());
        List<String> problems = new ArrayList<>();
        int checked = 0;
        for (BlockDefinition def : defs) {
            if (SKIPS.containsKey(def.getId())) continue;
            Block block;
            try {
                block = new BlockFactory().createBlock(def);
            } catch (Throwable t) {
                problems.add(def + ": cannot build (" + t + ")");
                continue;
            }
            Path file = BLOCKSTATES.resolve(def.getId() + ".json");
            if (!Files.exists(file)) {
                problems.add(def + ": no blockstate file");
                continue;
            }
            checked++;
            if (block instanceof HollowLogBlock || block instanceof HollowPipeBlock) {
                // HollowPipeBlock.getSourceFluid reads these two for every hollow log, stem and pipe (by instance).
                if (!block.defaultBlockState().hasProperty(HollowPipeBlock.WATERLOGGED)
                        || !block.defaultBlockState().hasProperty(HollowPipeBlock.LAVA_LOGGED)) {
                    problems.add(def + ": hollow block lacks the shared WATERLOGGED/LAVA_LOGGED properties");
                }
            }
            if (def.isHollowLog() || def.isHollowPipe() || def.isPipe()) {
                try {
                    var state = block.defaultBlockState();
                    if (def.isHollowPipe()) assertInstanceOf(HollowPipeBlock.class, block, def.getId());
                    else if (def.isHollowLog()) assertInstanceOf(HollowLogBlock.class, block, def.getId());
                    else assertInstanceOf(PipeBlock.class, block, def.getId());
                    state.getFluidState();
                    HollowPipeBlock.getSourceFluid(state, null);
                    HollowPipeBlock.getContainedFluid(state, null);
                    if (block instanceof HollowLogBlock || block instanceof HollowPipeBlock) {
                        assertFalse(state.getValue(HollowPipeBlock.WATERLOGGED));
                        assertFalse(state.getValue(HollowPipeBlock.LAVA_LOGGED));
                        var water = state.setValue(HollowPipeBlock.WATERLOGGED, true);
                        var lava = state.setValue(HollowPipeBlock.LAVA_LOGGED, true);
                        assertSame(net.minecraft.world.level.material.Fluids.WATER, HollowPipeBlock.getSourceFluid(water, null));
                        assertSame(net.minecraft.world.level.material.Fluids.LAVA, HollowPipeBlock.getSourceFluid(lava, null));
                        water.getFluidState();
                        lava.getFluidState();
                    }
                    if (block instanceof HollowPipeBlock pipe) {
                        pipe.getActualShape(state);
                        HollowPipeBlock.getConnectCount(state);
                        HollowPipeBlock.getPrimaryAxis(state);
                        com.kingodogo.buildscape.client.renderer.PipeWaterSurface.flowing(state, null);
                        for (var direction : net.minecraft.core.Direction.values()) {
                            HollowPipeBlock.isOpenEndpoint(state, direction);
                            com.kingodogo.buildscape.pipe.transport.PipeOutletWater.amount(state, null, direction);
                        }
                    }
                } catch (Throwable failure) {
                    problems.add(def + ": pipe/hollow getters failed (" + failure + ")");
                }
            }
            Map<String, Property<?>> props = new LinkedHashMap<>();
            block.getStateDefinition().getProperties().forEach(p -> props.put(p.getName(), p));
            JsonObject json = JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8)).getAsJsonObject();
            Set<String> bad = new LinkedHashSet<>();
            if (json.has("variants")) {
                for (String key : json.getAsJsonObject("variants").keySet()) {
                    if (key.isEmpty()) continue;
                    for (String pair : key.split(",")) {
                        String[] kv = pair.split("=", 2);
                        check(props, kv[0], kv.length > 1 ? kv[1] : "", bad);
                    }
                }
            }
            if (json.has("multipart")) {
                for (JsonElement part : json.getAsJsonArray("multipart")) {
                    if (part.getAsJsonObject().has("when")) collectWhen(props, part.getAsJsonObject().get("when"), bad);
                }
            }
            if (!bad.isEmpty()) {
                problems.add(def + " [" + block.getClass().getName() + "] has " + props.keySet() + " but blockstate uses: " + bad);
            }
        }
        System.out.println("BlockstatePropertiesTest: " + defs.size() + " blocks, " + checked + " blockstates checked, "
                + SKIPS.size() + " skipped");
        assertTrue(problems.isEmpty(), problems.size() + " problem(s):\n" + String.join("\n", problems));
    }

    @Test
    void pillarShapeUpdatesConnectStacksAndDisconnectRemovedNeighbors() throws Exception {
        var factory = new BlockFactory();
        var lower = factory.createBlock(new BlockDefinition("stone_pillar", "PillarBlock", CommonBlockProperties.of()));
        var upper = factory.createBlock(new BlockDefinition("copper_pillar", "PillarBlock", CommonBlockProperties.of()));
        var origin = net.minecraft.core.BlockPos.ZERO;
        var neighbors = new java.util.HashMap<net.minecraft.core.BlockPos, net.minecraft.world.level.block.state.BlockState>();
        var air = net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
        var reader = (net.minecraft.world.level.LevelReader) java.lang.reflect.Proxy.newProxyInstance(
                getClass().getClassLoader(), new Class<?>[]{net.minecraft.world.level.LevelReader.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("getBlockState")) return neighbors.getOrDefault(args[0], air);
                    throw new AssertionError("Unexpected level query: " + method.getName());
                });
        var update = lower.getClass().getDeclaredMethod("updateShape",
                net.minecraft.world.level.block.state.BlockState.class, net.minecraft.world.level.LevelReader.class,
                net.minecraft.world.level.ScheduledTickAccess.class, net.minecraft.core.BlockPos.class,
                net.minecraft.core.Direction.class, net.minecraft.core.BlockPos.class,
                net.minecraft.world.level.block.state.BlockState.class, net.minecraft.util.RandomSource.class);
        update.setAccessible(true);
        var single = lower.defaultBlockState();
        var above = upper.defaultBlockState();
        var random = net.minecraft.util.RandomSource.create(1);
        var bottom = (net.minecraft.world.level.block.state.BlockState) update.invoke(lower, single, reader, null,
                origin, net.minecraft.core.Direction.UP, origin.above(), above, random);
        assertEquals(PillarPart.BOTTOM, bottom.getValue(PillarBlock.PART));
        neighbors.put(origin.below(), above);
        var middle = (net.minecraft.world.level.block.state.BlockState) update.invoke(lower, bottom, reader, null,
                origin, net.minecraft.core.Direction.UP, origin.above(), above, random);
        assertEquals(PillarPart.MIDDLE, middle.getValue(PillarBlock.PART));
        var top = (net.minecraft.world.level.block.state.BlockState) update.invoke(lower, middle, reader, null,
                origin, net.minecraft.core.Direction.UP, origin.above(), air, random);
        assertEquals(PillarPart.TOP, top.getValue(PillarBlock.PART));
        var disconnected = (net.minecraft.world.level.block.state.BlockState) update.invoke(lower, top, reader, null,
                origin, net.minecraft.core.Direction.DOWN, origin.below(), air, random);
        assertEquals(PillarPart.SINGLE, disconnected.getValue(PillarBlock.PART));
    }

    /** Models name the experience fluid sprites; they only exist in the block atlas if the atlas source lists them. */
    @Test
    void blockAtlasListsTheExperienceFluidSprites() throws Exception {
        JsonObject atlas = JsonParser.parseString(Files.readString(
                MAIN_RESOURCES.resolve("assets/minecraft/atlases/blocks.json"), StandardCharsets.UTF_8)).getAsJsonObject();
        Set<String> singles = new LinkedHashSet<>();
        for (JsonElement source : atlas.getAsJsonArray("sources")) {
            JsonObject object = source.getAsJsonObject();
            if (object.get("type").getAsString().equals("minecraft:single")) singles.add(object.get("resource").getAsString());
        }
        assertTrue(singles.contains("buildscape:fluid/experience_still"), "still sprite missing from " + singles);
        assertTrue(singles.contains("buildscape:fluid/experience_flow"), "flow sprite missing from " + singles);
    }

    private static void collectWhen(Map<String, Property<?>> props, JsonElement when, Set<String> bad) {
        for (var entry : when.getAsJsonObject().entrySet()) {
            if (entry.getKey().equals("OR") || entry.getKey().equals("AND")) {
                for (JsonElement child : entry.getValue().getAsJsonArray()) collectWhen(props, child, bad);
            } else {
                check(props, entry.getKey(), entry.getValue().getAsString(), bad);
            }
        }
    }

    private static void check(Map<String, Property<?>> props, String name, String values, Set<String> bad) {
        Property<?> property = props.get(name);
        if (property == null) {
            bad.add(name);
            return;
        }
        for (String value : values.split("\\|")) {
            String v = value.startsWith("!") ? value.substring(1) : value;
            if (property.getValue(v).isEmpty()) bad.add(name + "=" + v);
        }
    }
}
