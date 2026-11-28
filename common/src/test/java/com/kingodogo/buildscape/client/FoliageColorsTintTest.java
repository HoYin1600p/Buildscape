package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.block.OrnamentBlock;
import com.kingodogo.buildscape.block.StringLightBlock;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("PMD.AvoidAccessibilityAlteration")
class FoliageColorsTintTest {
    @BeforeAll
    static void bootstrap() { com.kingodogo.buildscape.TestBootstrap.initialize(); }

    @Test
    void stringTintsDeclareTheirStateDependency() throws Exception {
        var method = FoliageColors.class.getDeclaredMethod("stringColor", EnumProperty.class);
        method.setAccessible(true);
        for (var property : java.util.List.of(OrnamentBlock.STRING_COLOR, StringLightBlock.STRING_COLOR)) {
            var source = (BlockTintSource) method.invoke(null, property);
            assertTrue(source.relevantProperties().contains(property));
            assertEquals(-1, source.color(null));
        }
    }

    @Test
    void petalsHaveTheReleaseFallbackColor() throws Exception {
        var method = FoliageColors.class.getDeclaredMethod("petalGrass");
        method.setAccessible(true);
        assertEquals(0xff7fa832, ((BlockTintSource) method.invoke(null)).color(null));
    }
}
