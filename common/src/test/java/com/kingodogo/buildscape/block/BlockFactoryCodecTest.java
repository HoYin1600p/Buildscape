package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.TestBootstrap;
import com.kingodogo.buildscape.adapter.v26x.BlockFactory;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.registry.VanillaReplacementAliases;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BlockFactoryCodecTest {
    @BeforeAll
    static void bootstrap() throws Exception {
        TestBootstrap.initialize();
        // Blocks create intrusive holders in their constructor; the frozen block registry must be reopened for that.
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
    void leafVariantsEncodeUsingVanillaPropertiesField() {
        for (String type : new String[]{"LeavesBlock", "MangroveLeavesBlock", "SnowyLeavesBlock"}) {
            LeavesBlock block = (LeavesBlock) create(type.toLowerCase(), type);
            assertEncodes(block.codec(), block);
        }
    }

    @Test
    void restored26xPotsAreRegisteredWithVanillaPlants() {
        ModBlocks.init();
        var plants = java.util.Map.of(
                "potted_bush", Blocks.BUSH,
                "potted_dry_grass", Blocks.SHORT_DRY_GRASS,
                "potted_tall_dry_grass", Blocks.TALL_DRY_GRASS,
                "potted_firefly_bush", Blocks.FIREFLY_BUSH,
                "potted_wildflowers", Blocks.WILDFLOWERS,
                "potted_leaf_litter", Blocks.LEAF_LITTER,
                "potted_cactus_flower", Blocks.CACTUS_FLOWER);
        for (var entry : plants.entrySet()) {
            String id = "buildscape:" + entry.getKey();
            var registrations = Services.REGISTERED_BLOCKS.stream()
                    .filter(supplier -> supplier.getId().toString().equals(id)).toList();
            assertEquals(1, registrations.size(), id);
            var definition = (BlockDefinition) registrations.getFirst().get();
            assertEquals("FlowerPotBlock", definition.getBlockType(), id);
            var block = new BlockFactory().createBlock(definition);
            var pot = assertInstanceOf(FlowerPotBlock.class, block, id);
            assertSame(entry.getValue(), pot.getPotted(), id);
            assertSame(entry.getValue().asItem(), pot.getPotted().asItem(), id);
            assertTrue(VanillaReplacementAliases.ALL.stream().noneMatch(alias -> alias.source().equals(id)), id);
        }
    }

    private static Block create(String id, String type) {
        return new BlockFactory().createBlock(new BlockDefinition(id, type, CommonBlockProperties.of()));
    }

    @SuppressWarnings("unchecked")
    private static <B extends Block> void assertEncodes(MapCodec<B> codec, Block block) {
        assertNotNull(codec);
        var encoded = codec.codec().encodeStart(JsonOps.INSTANCE, (B) block);
        assertTrue(encoded.error().isEmpty(), () -> encoded.error().toString());
        assertTrue(encoded.getOrThrow().getAsJsonObject().has("properties"));
    }
}
