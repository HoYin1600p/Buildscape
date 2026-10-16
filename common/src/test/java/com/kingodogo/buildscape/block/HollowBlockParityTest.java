package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.TestBootstrap;
import com.kingodogo.buildscape.adapter.v26x.AtlasIds;
import com.kingodogo.buildscape.adapter.v26x.BlockFactory;
import com.kingodogo.buildscape.adapter.v26x.ValueInputData;
import com.kingodogo.buildscape.event.HollowLogCrawlHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.phys.AABB;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.IdentityHashMap;

import static org.junit.jupiter.api.Assertions.*;

class HollowBlockParityTest {
    private static HollowLogBlock log;
    private static HollowPipeBlock pipe;

    @BeforeAll static void bootstrap() throws Exception {
        TestBootstrap.initialize();
        setRegistryField("unregisteredIntrusiveHolders", new IdentityHashMap<>());
        setRegistryField("frozen", false);
        // The block entity test builds a BlockEntityType, which needs its own registry open for intrusive holders.
        setRegistryField(BuiltInRegistries.BLOCK_ENTITY_TYPE, "unregisteredIntrusiveHolders", new IdentityHashMap<>());
        setRegistryField(BuiltInRegistries.BLOCK_ENTITY_TYPE, "frozen", false);
        var factory = new BlockFactory();
        log = (HollowLogBlock) factory.createBlock(new BlockDefinition("parity_hollow_log", "HollowLogBlock", CommonBlockProperties.of()));
        pipe = (HollowPipeBlock) factory.createBlock(new BlockDefinition("parity_hollow_pipe", "HollowPipeBlock", CommonBlockProperties.of()));
    }

    @AfterAll static void restoreRegistry() throws Exception {
        setRegistryField("frozen", true);
        setRegistryField("unregisteredIntrusiveHolders", null);
        setRegistryField(BuiltInRegistries.BLOCK_ENTITY_TYPE, "frozen", true);
        setRegistryField(BuiltInRegistries.BLOCK_ENTITY_TYPE, "unregisteredIntrusiveHolders", null);
    }

    private static void setRegistryField(String name, Object value) throws Exception {
        setRegistryField(BuiltInRegistries.BLOCK, name, value);
    }

    private static void setRegistryField(Object registry, String name, Object value) throws Exception {
        var field = MappedRegistry.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(registry, value);
    }

    @Test void atlasLookupUsesAnIdRatherThanTheTexturePath() {
        assertEquals("minecraft:blocks", AtlasIds.blocks().toString());
        assertFalse(AtlasIds.blocks().getPath().contains("textures/"));
    }

    @Test void crawlEntranceRequiresShiftOrCrouchingAndAnOpenHorizontalEnd() {
        BlockState horizontal = log.defaultBlockState().setValue(HollowLogBlock.AXIS, Direction.Axis.X);
        AABB entrance = new AABB(-0.6, 0, 0.2, 0, 1.8, 0.8);
        assertTrue(entering(entrance, true, false, horizontal));
        assertTrue(entering(entrance, false, true, horizontal));
        assertFalse(entering(entrance, false, false, horizontal));
        assertFalse(entering(entrance, true, false, horizontal.setValue(HollowLogBlock.HAS_GLASS_NEG, true)));
        assertFalse(entering(entrance, true, false, log.defaultBlockState()));
        assertFalse(entering(entrance.move(-1, 0, 0), true, false, horizontal));
        assertFalse(entering(entrance.move(0, 1, 0), true, false, horizontal));
        assertTrue(entering(entrance, true, false, pipe.defaultBlockState().setValue(HollowPipeBlock.AXIS, Direction.Axis.X)));
        assertFalse(entering(entrance, true, false, pipe.defaultBlockState()));
    }

    @Test void clientUpdateDataIncludesDecorationsAndCovers() {
        BlockState state = pipe.defaultBlockState();
        var entity = new HollowLogBlockEntity(BlockPos.ZERO, state);
        entity.setDecorationState(Blocks.POTTED_OAK_SAPLING.defaultBlockState());
        entity.setGlassCoverNeg(Blocks.GLASS.defaultBlockState());
        entity.setGlassCoverPos(BuiltInRegistries.BLOCK.getValue(net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "blue_stained_glass")).defaultBlockState());
        var lookup = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
        var update = entity.getUpdateTag(lookup);
        assertTrue(update.contains("DecorationState"));
        assertTrue(update.contains("GlassCoverNeg"));
        assertTrue(update.contains("GlassCoverPos"));
        var loaded = new HollowLogBlockEntity(BlockPos.ZERO, state);
        loaded.readData(new ValueInputData(TagValueInput.create(ProblemReporter.DISCARDING, lookup, update)));
        assertEquals(entity.getDecorationState(), loaded.getDecorationState());
        assertEquals(entity.getGlassCoverNeg(), loaded.getGlassCoverNeg());
        assertEquals(entity.getGlassCoverPos(), loaded.getGlassCoverPos());
    }

    @Test void insertsAndPotsKeepReferenceAcceptanceRules() {
        assertTrue(HollowLogBlock.isValidFullBlockDecoration(Blocks.STONE));
        assertFalse(HollowLogBlock.isValidFullBlockDecoration(Blocks.OAK_SLAB));
        assertFalse(HollowLogBlock.isValidFullBlockDecoration(Blocks.CHEST));
        assertFalse(HollowLogBlock.isValidFullBlockDecoration(log));
        assertFalse(HollowLogBlock.isValidFullBlockDecoration(pipe));
        assertTrue(HollowLogBlock.isFullGlassBlock(new ItemStack(Items.GLASS)));
        assertTrue(HollowLogBlock.isFullGlassBlock(new ItemStack(BuiltInRegistries.ITEM.getValue(net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "blue_stained_glass")))));
        assertFalse(HollowLogBlock.isFullGlassBlock(new ItemStack(Items.GLASS_PANE)));
        assertFalse(HollowLogBlock.isFullGlassBlock(new ItemStack(Items.STONE)));
        assertTrue(HollowLogBlock.isPotAllowed(Direction.Axis.X, false));
        assertTrue(HollowLogBlock.isPotAllowed(Direction.Axis.Z, false));
        assertFalse(HollowLogBlock.isPotAllowed(Direction.Axis.Y, false));
        assertTrue(HollowLogBlock.isPotAllowed(Direction.Axis.Y, true));
        assertEquals(Blocks.POTTED_OAK_SAPLING.defaultBlockState(), HollowLogBlock.getPottedBlockState(Items.OAK_SAPLING));
        assertEquals(Blocks.POTTED_POPPY.defaultBlockState(), HollowLogBlock.getPottedBlockState(Items.POPPY));
        assertNull(HollowLogBlock.getPottedBlockState(Items.STONE));
    }

    private static boolean entering(AABB box, boolean shift, boolean crouch, BlockState state) {
        return HollowLogCrawlHandler.isEnteringHollowBlock(box, shift, crouch, world(state, null));
    }

    private static BlockGetter world(BlockState state, HollowLogBlockEntity entity) {
        return (BlockGetter) Proxy.newProxyInstance(BlockGetter.class.getClassLoader(), new Class<?>[]{BlockGetter.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getBlockState" -> BlockPos.ZERO.equals(args[0]) ? state : Blocks.AIR.defaultBlockState();
                    case "getBlockEntity" -> BlockPos.ZERO.equals(args[0]) ? entity : null;
                    case "getFluidState" -> (BlockPos.ZERO.equals(args[0]) ? state : Blocks.AIR.defaultBlockState()).getFluidState();
                    case "getHeight" -> 384;
                    default -> method.getReturnType() == int.class ? -64 : null;
                });
    }
}
