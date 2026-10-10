package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.TestBootstrap;
import com.kingodogo.buildscape.adapter.v26x.BlockFactory;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BlockFactoryCodecTest {
    @BeforeAll
    static void bootstrap() { TestBootstrap.initialize(); }

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
