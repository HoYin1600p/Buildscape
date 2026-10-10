package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.TestBootstrap;
import com.kingodogo.buildscape.registry.VanillaReplacementAliases;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Saved Buildscape sign identifiers now resolve to vanilla signs. Each replacement must create a sign block entity
 * whose type accepts its block state.
 */
class SignBlockEntityValidityTest {
    @BeforeAll
    static void bootstrap() {
        TestBootstrap.initialize();
    }

    @Test
    void everyReplacementSignCreatesAValidBlockEntity() {
        int checked = 0;
        for (var alias : VanillaReplacementAliases.ALL) {
            if (!alias.block()) continue;
            Block block = BuiltInRegistries.BLOCK.getValue(Identifier.parse(alias.target()));
            if (!(block instanceof SignBlock)) continue;
            BlockEntity entity = ((net.minecraft.world.level.block.EntityBlock) block)
                    .newBlockEntity(BlockPos.ZERO, block.defaultBlockState());
            assertInstanceOf(SignBlockEntity.class, entity, alias.source());
            assertTrue(entity.isValidBlockState(block.defaultBlockState()), alias.source());
            checked++;
        }
        assertEquals(4, checked, "expected the four vanilla standing/wall sign replacements");
    }
}
