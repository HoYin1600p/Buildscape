package com.kingodogo.buildscape.adapter;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MaterialColor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.function.Function;
import java.util.function.ToIntFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Sculk blocks break, resist and glow like vanilla 1.19. */
@SuppressWarnings("PMD.AvoidAccessibilityAlteration")
class SculkBlockPropertiesTest {
    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void sculkBuildingBlocksAreSoftDarkAndUnlit() {
        for (String id : new String[]{"sculk", "sculk_slab", "sculk_stairs", "sculk_wall", "sculk_vertical_slab"}) {
            BlockBehaviour.Properties properties = properties(id);
            assertStrength(id, properties, 0.2F, 0.2F, 0);
            assertEquals(MaterialColor.COLOR_BLACK, mapColor(properties), id);
        }
    }

    @Test
    void catalystShriekerVeinAndSensorUseVanillaStrengthAndLight() {
        assertStrength("sculk_catalyst", properties("sculk_catalyst"), 3.0F, 3.0F, 6);
        assertStrength("sculk_shrieker", properties("sculk_shrieker"), 3.0F, 3.0F, 0);
        assertStrength("sculk_vein", properties("sculk_vein"), 0.2F, 0.2F, 0);
        assertStrength("sculk_sensor", properties("sculk_sensor"), 1.5F, 1.5F, 1);
    }

    @Test
    void catalystRandomTicksSoOldCatalystsGainTheirBlockEntity() throws ReflectiveOperationException {
        assertTrue((Boolean) field("isRandomlyTicking").get(properties("sculk_catalyst")));
    }

    private static BlockBehaviour.Properties properties(String id) {
        BlockBehaviour.Properties properties = ReferenceBlockProperties.create(id, path -> Blocks.STONE);
        assertNotNull(properties, id);
        return properties;
    }

    @SuppressWarnings("unchecked")
    private static void assertStrength(String id, BlockBehaviour.Properties properties, float destroyTime,
            float resistance, int light) {
        try {
            assertEquals(destroyTime, field("destroyTime").getFloat(properties), 0.0F, id);
            assertEquals(resistance, field("explosionResistance").getFloat(properties), 0.0F, id);
            ToIntFunction<BlockState> emission = (ToIntFunction<BlockState>) field("lightEmission").get(properties);
            assertEquals(light, emission.applyAsInt(Blocks.STONE.defaultBlockState()), id);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static MaterialColor mapColor(BlockBehaviour.Properties properties) {
        try {
            return ((Function<BlockState, MaterialColor>) field("materialColor").get(properties))
                    .apply(Blocks.STONE.defaultBlockState());
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    private static Field field(String name) throws NoSuchFieldException {
        Field field = BlockBehaviour.Properties.class.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }
}
