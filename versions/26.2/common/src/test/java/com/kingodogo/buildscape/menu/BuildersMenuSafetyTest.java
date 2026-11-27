package com.kingodogo.buildscape.menu;

import com.kingodogo.buildscape.TestBootstrap;
import net.minecraft.world.entity.player.Inventory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BuildersMenuSafetyTest {

    @BeforeAll
    public static void setUp() {
        TestBootstrap.initialize();
    }

    private static Inventory createTestInventory() {
        try {
            for (java.lang.reflect.Constructor<?> c : Inventory.class.getConstructors()) {
                if (c.getParameterCount() == 1) {
                    return (Inventory) c.newInstance((Object) null);
                } else if (c.getParameterCount() == 2) {
                    return (Inventory) c.newInstance(null, null);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new IllegalStateException("No suitable Inventory constructor found");
    }

    @Test
    public void testWorkbenchMenuClientFallbackButtons() {
        Inventory playerInv = createTestInventory();

        BuildersWorkbenchMenu menu = new BuildersWorkbenchMenu(1, playerInv);
        assertEquals(0, menu.getActiveTab());

        // Switch to gradient tab
        assertTrue(menu.clickMenuButton(null, 1));
        assertEquals(1, menu.getActiveTab());

        // Increment offset
        assertEquals(0, menu.getResultOffset(1, 2));
        assertTrue(menu.clickMenuButton(null, 12));
        assertEquals(1, menu.getResultOffset(1, 2));

        // Reset offsets
        assertTrue(menu.clickMenuButton(null, 30));
        assertEquals(0, menu.getResultOffset(1, 2));

        // Switch back to color tab
        assertTrue(menu.clickMenuButton(null, 0));
        assertEquals(0, menu.getActiveTab());
    }
}
