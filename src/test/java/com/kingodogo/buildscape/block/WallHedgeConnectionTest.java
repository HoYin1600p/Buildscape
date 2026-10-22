package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.block.decor.OrnamentBlock;
import com.kingodogo.buildscape.block.plant.LeafHedgeBlock;
import com.kingodogo.buildscape.mixin.WallBlockMixin;
import com.kingodogo.buildscape.registry.blocks.BlocksRegister;
import com.kingodogo.buildscape.testutil.RegistryTestSupport;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WallSide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class WallHedgeConnectionTest {

    private static Method connectsToMethod;
    private static Block hedgeBlock;
    private static Block wallBlock;
    private static Block ornamentBlock;

    @BeforeAll
    static void setup() throws Exception {
        RegistryTestSupport.bootstrap();
        connectsToMethod = WallBlockMixin.class.getDeclaredMethod(
                "connectsTo",
                BlockState.class,
                boolean.class,
                Direction.class,
                CallbackInfoReturnable.class);
        connectsToMethod.setAccessible(true);

        Map<String, Block> bound = RegistryTestSupport.bind(
                BlocksRegister.blocks(),
                path -> "oak_leaf_hedge".equals(path) || "white_ornament".equals(path));
        hedgeBlock = bound.get("oak_leaf_hedge");
        ornamentBlock = bound.get("white_ornament");
        wallBlock = Blocks.COBBLESTONE_WALL;
    }

    @AfterAll
    static void tearDown() {
        RegistryTestSupport.unbind(
                BlocksRegister.blocks(),
                path -> "oak_leaf_hedge".equals(path) || "white_ornament".equals(path));
    }

    private static boolean testConnectsTo(BlockState state, boolean sideSolidFullSquare, Direction direction)
            throws Exception {
        WallBlockMixin mixin = new WallBlockMixin();
        CallbackInfoReturnable<Boolean> cir = new CallbackInfoReturnable<>("connectsTo", true);
        connectsToMethod.invoke(mixin, state, sideSolidFullSquare, direction, cir);
        if (cir.isCancelled()) {
            return cir.getReturnValue();
        }
        return false;
    }

    @Test
    void testHedgeExtendsWallBlock() {
        assertTrue(WallBlock.class.isAssignableFrom(LeafHedgeBlock.class),
                "LeafHedgeBlock must extend WallBlock for polymorphically consistent wall mechanics");
        assertNotNull(hedgeBlock, "oak_leaf_hedge should be bound");
        assertTrue(hedgeBlock instanceof LeafHedgeBlock);
    }

    @ParameterizedTest
    @EnumSource(value = Direction.class, names = {"NORTH", "EAST", "SOUTH", "WEST"})
    void testHorizontalDirectionsConnectWallAndHedge(Direction direction) throws Exception {
        BlockState hedgeState = hedgeBlock.defaultBlockState();
        BlockState wallState = wallBlock.defaultBlockState();
        BlockState ornamentState = ornamentBlock.defaultBlockState();
        BlockState airState = Blocks.AIR.defaultBlockState();
        BlockState dirtState = Blocks.DIRT.defaultBlockState();

        // 1. Wall connecting to Hedge
        assertTrue(testConnectsTo(hedgeState, false, direction),
                "Wall must connect to Hedge in direction " + direction);

        // 2. Hedge connecting to Wall
        assertTrue(testConnectsTo(wallState, false, direction),
                "Hedge must connect to Wall in direction " + direction);

        // 3. Hedge connecting to Hedge
        assertTrue(testConnectsTo(hedgeState, false, direction),
                "Hedge must connect to another Hedge in direction " + direction);

        // 4. Wall connecting to Wall
        assertTrue(testConnectsTo(wallState, false, direction),
                "Wall must connect to another Wall in direction " + direction);

        // 5. Connecting to OrnamentBlock
        assertTrue(testConnectsTo(ornamentState, false, direction),
                "Wall/Hedge must connect to OrnamentBlock in direction " + direction);

        // 6. Non-wall, non-solid blocks must NOT force connection via mixin
        assertFalse(testConnectsTo(airState, false, direction),
                "Air must not force connection in direction " + direction);
        assertFalse(testConnectsTo(dirtState, false, direction),
                "Non-solid Dirt must not force connection via WallBlockMixin in direction " + direction);
    }

    @Test
    void testBothPlacementOrders() throws Exception {
        BlockState hedgeState = hedgeBlock.defaultBlockState();
        BlockState wallState = wallBlock.defaultBlockState();

        // Placement Order 1: Wall placed first at pos (0,0), then Hedge placed at east neighbor (1,0)
        // Hedge placement checks neighbor at WEST -> wallState
        assertTrue(testConnectsTo(wallState, false, Direction.WEST),
                "Hedge placed second must connect to existing Wall to its west");
        // Wall receives neighbor update from EAST -> hedgeState
        assertTrue(testConnectsTo(hedgeState, false, Direction.EAST),
                "Existing Wall must update and connect to new Hedge to its east");

        // Placement Order 2: Hedge placed first at pos (0,0), then Wall placed at south neighbor (0,1)
        // Wall placement checks neighbor at NORTH -> hedgeState
        assertTrue(testConnectsTo(hedgeState, false, Direction.NORTH),
                "Wall placed second must connect to existing Hedge to its north");
        // Hedge receives neighbor update from SOUTH -> wallState
        assertTrue(testConnectsTo(wallState, false, Direction.SOUTH),
                "Existing Hedge must update and connect to new Wall to its south");
    }

    @Test
    void testHedgeShapeStateProperties() {
        BlockState state = hedgeBlock.defaultBlockState();

        assertTrue(state.hasProperty(WallBlock.UP), "Hedge must have UP property");
        assertTrue(state.hasProperty(WallBlock.NORTH_WALL), "Hedge must have NORTH_WALL property");
        assertTrue(state.hasProperty(WallBlock.EAST_WALL), "Hedge must have EAST_WALL property");
        assertTrue(state.hasProperty(WallBlock.SOUTH_WALL), "Hedge must have SOUTH_WALL property");
        assertTrue(state.hasProperty(WallBlock.WEST_WALL), "Hedge must have WEST_WALL property");
        assertTrue(state.hasProperty(WallBlock.WATERLOGGED), "Hedge must have WATERLOGGED property");

        assertEquals(WallSide.NONE, state.getValue(WallBlock.NORTH_WALL));
        assertEquals(WallSide.NONE, state.getValue(WallBlock.EAST_WALL));
        assertEquals(WallSide.NONE, state.getValue(WallBlock.SOUTH_WALL));
        assertEquals(WallSide.NONE, state.getValue(WallBlock.WEST_WALL));
        assertTrue(state.getValue(WallBlock.UP));
        assertFalse(state.getValue(WallBlock.WATERLOGGED));

        // Test mutating WallSide variants
        BlockState updatedState = state
                .setValue(WallBlock.NORTH_WALL, WallSide.LOW)
                .setValue(WallBlock.SOUTH_WALL, WallSide.TALL)
                .setValue(WallBlock.UP, false);

        assertEquals(WallSide.LOW, updatedState.getValue(WallBlock.NORTH_WALL));
        assertEquals(WallSide.TALL, updatedState.getValue(WallBlock.SOUTH_WALL));
        assertFalse(updatedState.getValue(WallBlock.UP));
    }
}
