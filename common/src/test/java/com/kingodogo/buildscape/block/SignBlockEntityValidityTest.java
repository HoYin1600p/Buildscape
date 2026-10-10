package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.TestBootstrap;
import com.kingodogo.buildscape.adapter.v26x.BlockFactory;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test: placing a Buildscape sign (for example with /setblock) creates its block entity, and the game
 * rejects a block entity whose type does not accept the block state. The vanilla SIGN type does not list Buildscape's
 * signs, so the signs must construct their block entity with Buildscape's own types.
 */
class SignBlockEntityValidityTest {
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
        // Blocks and block entity types both create intrusive holders while they are constructed.
        field.set(BuiltInRegistries.BLOCK, value);
        field.set(BuiltInRegistries.BLOCK_ENTITY_TYPE, value);
    }

    @Test
    void everyBuildscapeSignCreatesAValidBlockEntity() {
        ModBlocks.init();
        int checked = 0;
        for (var supplier : Services.REGISTERED_BLOCKS) {
            if (!(supplier.get() instanceof BlockDefinition def)) continue;
            if (!(def.isBambooStandingSign() || def.isBambooWallSign() || def.isMangroveStandingSign() || def.isMangroveWallSign())) continue;
            Block block = new BlockFactory().createBlock(def);
            BlockEntity entity = ((net.minecraft.world.level.block.EntityBlock) block)
                    .newBlockEntity(BlockPos.ZERO, block.defaultBlockState());
            assertInstanceOf(SignBlockEntity.class, entity, def.getId());
            assertTrue(entity.isValidBlockState(block.defaultBlockState()), def.getId());
            checked++;
        }
        assertTrue(checked >= 4, "expected the bamboo and mangrove sign blocks, found " + checked);
    }
}
