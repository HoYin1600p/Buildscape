package com.kingodogo.buildscape.core;

import com.kingodogo.buildscape.block.FestiveStockingItem;
import com.kingodogo.buildscape.client.renderer.MobPillarRenderer;
import com.kingodogo.buildscape.entity.ColoredItemFrameEntity;
import com.kingodogo.buildscape.entity.FestiveStockingEntity;
import com.kingodogo.buildscape.feature.pillar.ItemFrameParticleEvents;
import com.kingodogo.buildscape.registry.ModBlocks;
import com.kingodogo.buildscape.registry.ModItems;
import com.kingodogo.buildscape.testutil.RegistryTestSupport;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * The per-colour tables that used to be copied into five classes must agree with {@link DyeColors}. The expected
 * values below are the old hand-written tables, so the test passes both before and after the classes use the shared
 * enum.
 */
public class DyeTablesTest {
    /** Item frame id colour codes as the old switch in ItemFrameParticleEvents spelled them. */
    private static final Map<String, String> OLD_CODES = new LinkedHashMap<>();

    static {
        String[] table = {"white", "W", "orange", "O", "magenta", "M", "light_blue", "LB", "yellow", "Y", "lime", "L",
                "pink", "P", "gray", "GR", "light_gray", "LG", "cyan", "C", "purple", "PU", "blue", "B", "brown", "BR",
                "green", "G", "red", "R", "black", "BL"};
        for (int i = 0; i < table.length; i += 2) {
            OLD_CODES.put(table[i], table[i + 1]);
        }
    }

    private static Map<String, Block> stockingBlocks;
    private static Map<String, Item> stockingItems;
    private static Map<String, Item> frameItems;

    @BeforeClass
    public static void bootstrap() {
        RegistryTestSupport.bootstrap();
        stockingBlocks = RegistryTestSupport.bind(ModBlocks.BLOCKS, path -> path.endsWith("festive_stocking"));
        stockingItems = RegistryTestSupport.bind(ModItems.ITEMS, path -> path.endsWith("festive_stocking"));
        frameItems = RegistryTestSupport.bind(ModItems.ITEMS, path -> path.endsWith("item_frame"));
    }

    @AfterClass
    public static void unbind() {
        RegistryTestSupport.unbind(ModBlocks.BLOCKS, path -> path.endsWith("festive_stocking"));
        RegistryTestSupport.unbind(ModItems.ITEMS,
                path -> path.endsWith("festive_stocking") || path.endsWith("item_frame"));
    }

    @Test
    public void sharedEnumCoversTheSixteenColoursInVanillaOrder() {
        assertEquals(16, DyeColors.values().length);
        for (DyeColors color : DyeColors.values()) {
            assertSame(color, DyeColors.fromName(color.serializedName()));
            assertEquals(net.minecraft.world.item.DyeColor.byId(color.ordinal()).getName(), color.serializedName());
        }
        assertNull(DyeColors.fromName("invisible"));
        assertNull(DyeColors.fromName(null));
    }

    @Test
    public void itemFrameColourCodesMatchTheOldSwitch() throws ReflectiveOperationException {
        Method code = ItemFrameParticleEvents.class.getDeclaredMethod("getColorCode", String.class);
        code.setAccessible(true);
        for (DyeColors color : DyeColors.values()) {
            String expected = OLD_CODES.get(color.serializedName());
            assertEquals(color.serializedName(), expected, color.shortCode());
            assertEquals(color.serializedName(), expected, code.invoke(null, color.serializedName()));
        }
        assertEquals("I", code.invoke(null, "invisible"));
        assertEquals("W", code.invoke(null, ""));
        assertEquals("W", code.invoke(null, (Object) null));
        assertEquals("W", code.invoke(null, "unknown"));
        assertEquals("LB", code.invoke(null, "Light_Blue"));
    }

    @Test
    public void mobPillarDyeIdsMatchTheOldTable() throws ReflectiveOperationException {
        Field field = MobPillarRenderer.class.getDeclaredField("DYE_COLORS");
        field.setAccessible(true);
        Map<?, ?> table = (Map<?, ?>) field.get(null);
        assertEquals(16, table.size());
        for (DyeColors color : DyeColors.values()) {
            assertEquals(color.serializedName(), color.ordinal(), table.get(color.serializedName()));
        }
    }

    @Test
    public void festiveStockingBlocksResolveForEveryColour() {
        assertEquals(17, stockingBlocks.size());
        for (DyeColors color : DyeColors.values()) {
            Item item = stockingItems.get(color.serializedName() + "_festive_stocking");
            assertTrue(color.serializedName(), item instanceof FestiveStockingItem);
            assertSame(color.serializedName(), stockingBlocks.get(color.serializedName() + "_festive_stocking"),
                    ((FestiveStockingItem) item).getBlock());
        }
    }

    @Test
    public void festiveStockingItemsResolveForEveryColour() throws ReflectiveOperationException {
        Object entity = allocate(FestiveStockingEntity.class);
        Method lookup = FestiveStockingEntity.class.getDeclaredMethod("getStockingItemForColor", String.class);
        lookup.setAccessible(true);
        for (DyeColors color : DyeColors.values()) {
            assertSame(color.serializedName(), stockingItems.get(color.serializedName() + "_festive_stocking"),
                    ((ItemStack) lookup.invoke(entity, color.serializedName())).getItem());
        }
        assertSame(stockingItems.get("festive_stocking"), ((ItemStack) lookup.invoke(entity, "festive")).getItem());
    }

    @Test
    public void coloredItemFramesResolveForEveryColour() throws ReflectiveOperationException {
        Object entity = allocate(ColoredItemFrameEntity.class);
        Method lookup = ColoredItemFrameEntity.class.getDeclaredMethod("getFrameItemForColor", String.class);
        lookup.setAccessible(true);
        for (DyeColors color : DyeColors.values()) {
            assertSame(color.serializedName(), frameItems.get(color.serializedName() + "_item_frame"),
                    ((ItemStack) lookup.invoke(entity, color.serializedName())).getItem());
        }
        assertSame(frameItems.get("invisible_item_frame"), ((ItemStack) lookup.invoke(entity, "invisible")).getItem());
        assertSame(frameItems.get("white_item_frame"), ((ItemStack) lookup.invoke(entity, "unknown")).getItem());
    }

    /** Creates an entity without running its constructor: the lookups under test use no instance state. */
    private static Object allocate(Class<?> type) throws ReflectiveOperationException {
        Field unsafeField = Class.forName("sun.misc.Unsafe").getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        Object unsafe = unsafeField.get(null);
        return unsafe.getClass().getMethod("allocateInstance", Class.class).invoke(unsafe, type);
    }
}
