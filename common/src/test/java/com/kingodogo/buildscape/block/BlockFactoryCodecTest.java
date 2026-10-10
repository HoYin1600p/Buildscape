package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.TestBootstrap;
import com.kingodogo.buildscape.adapter.v26x.BlockFactory;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
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
    void copperTorchesHaveWorkingCodecsWithTheirCustomParticleOverrides() {
        TorchBlock torch = (TorchBlock) create("copper_torch", "CopperTorchBlock");
        WallTorchBlock wallTorch = (WallTorchBlock) create("copper_wall_torch", "CopperWallTorchBlock");
        assertEncodes(torch.codec(), torch);
        assertEncodes(wallTorch.codec(), wallTorch);
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
