package com.kingodogo.buildscape.client.screen;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class ConfigBackgroundsTest {

    @Test
    public void allBuiltInBackgroundsAreRegisteredAndNamedProperly() {
        List<ConfigBackgrounds.Background> list = ConfigBackgrounds.getAllBackgrounds();
        assertNotNull(list);
        assertEquals("Expected exactly 10 built-in backgrounds", 10, list.size());

        Set<String> ids = new HashSet<>();
        for (ConfigBackgrounds.Background bg : list) {
            assertNotNull(bg);
            assertNotNull(bg.id);
            assertFalse(bg.id.isBlank());
            assertTrue("ID should be lowercase snake_case: " + bg.id, bg.id.matches("[a-z0-9_]+"));
            assertNotNull(bg.displayName);
            assertFalse(bg.displayName.isBlank());

            // Ensure no duplicate IDs
            assertTrue("Duplicate background ID: " + bg.id, ids.add(bg.id));

            // Verify formatDisplayName produces expected title casing
            assertEquals(bg.displayName, ConfigBackgrounds.formatDisplayName(bg.id));
        }
    }

    @Test
    public void creeperMaterialVariantsExist() {
        assertNotNull(ConfigBackgrounds.getById("green_creeper"));
        assertNotNull(ConfigBackgrounds.getById("golden_creeper"));
        assertNotNull(ConfigBackgrounds.getById("crimson_creeper"));
        assertNotNull(ConfigBackgrounds.getById("warped_creeper"));

        // Removed creeper variants safely fall back to NONE
        assertEquals(ConfigBackgrounds.NONE, ConfigBackgrounds.getById("diamond_creeper"));
        assertEquals(ConfigBackgrounds.NONE, ConfigBackgrounds.getById("netherite_creeper"));
        assertEquals(ConfigBackgrounds.NONE, ConfigBackgrounds.getById("amethyst_creeper"));
        assertEquals(ConfigBackgrounds.NONE, ConfigBackgrounds.getById("copper_creeper"));
        assertEquals(ConfigBackgrounds.NONE, ConfigBackgrounds.getById("sculk_creeper"));

        // Legacy/convenience alias
        assertEquals(ConfigBackgrounds.GREEN_CREEPER, ConfigBackgrounds.getById("creeper"));
    }

    @Test
    public void iconicEntityVariantsExist() {
        assertEquals(ConfigBackgrounds.ENDERMAN, ConfigBackgrounds.getById("enderman"));
        assertEquals(ConfigBackgrounds.WOLF, ConfigBackgrounds.getById("wolf"));
        assertEquals(ConfigBackgrounds.FOX, ConfigBackgrounds.getById("fox"));
        assertEquals(ConfigBackgrounds.SKELETON, ConfigBackgrounds.getById("skeleton"));
        assertEquals(ConfigBackgrounds.SLIME, ConfigBackgrounds.getById("slime"));

        // Ensure removed entities fall back to NONE
        assertEquals(ConfigBackgrounds.NONE, ConfigBackgrounds.getById("warden"));
        assertEquals(ConfigBackgrounds.NONE, ConfigBackgrounds.getById("bee"));
        assertEquals(ConfigBackgrounds.NONE, ConfigBackgrounds.getById("wither_skeleton"));
        assertEquals(ConfigBackgrounds.NONE, ConfigBackgrounds.getById("snow_fox"));
        assertEquals(ConfigBackgrounds.NONE, ConfigBackgrounds.getById("angry_wolf"));
        assertEquals(ConfigBackgrounds.NONE, ConfigBackgrounds.getById("ghast"));
    }

    @Test
    public void everyBackgroundDrawsWithoutException() {
        PoseStack ps = new PoseStack();
        ConfigThemes.Theme darkTheme = ConfigThemes.getById("dark");
        ConfigThemes.Theme lightTheme = ConfigThemes.getById("light");

        for (ConfigBackgrounds.Background bg : ConfigBackgrounds.getAllBackgrounds()) {
            // Render on dark theme
            bg.draw(ps, 0, 0, 320, 240, 1.0f, darkTheme);
            drainBatch();

            // Render on light theme
            bg.draw(ps, 0, 0, 320, 240, 1.0f, lightTheme);
            drainBatch();

            // Render at minimum size
            bg.draw(ps, 0, 0, 60, 60, 0.5f, darkTheme);
            drainBatch();
        }
    }

    private static void drainBatch() {
        BufferBuilder bb = ConfigGuiBatch.finishPending();
        if (bb != null) {
            bb.popNextBuffer();
        }
    }

    @Test
    public void cycleNextAndPrevNavigatesAndWraps() {
        ConfigBackgrounds.setBackgroundById("none");
        assertEquals("none", ConfigBackgrounds.getCurrentBackground().id);

        ConfigBackgrounds.Background next = ConfigBackgrounds.cycleNextBackground();
        assertNotEquals("none", next.id);

        ConfigBackgrounds.Background prev = ConfigBackgrounds.cyclePrevBackground();
        assertEquals("none", prev.id);

        // Wrapping prev from 0
        ConfigBackgrounds.Background last = ConfigBackgrounds.cyclePrevBackground();
        List<ConfigBackgrounds.Background> all = ConfigBackgrounds.getAllBackgrounds();
        assertEquals(all.get(all.size() - 1).id, last.id);
    }

    @Test
    public void colorMathAndGradientsWorkAccurately() {
        int opaqueRed = 0xFFFF0000;
        int halfAlpha = ConfigBackgrounds.applyAlpha(opaqueRed, 0.5f);
        assertEquals(0x7F, (halfAlpha >> 24) & 0xFF);
        assertEquals(0xFF0000, halfAlpha & 0x00FFFFFF);

        int lerped = ConfigBackgrounds.lerpColor(0xFF000000, 0xFFFFFFFF, 0.5f);
        assertEquals(0xFF, (lerped >> 24) & 0xFF);
        assertEquals(127, (lerped >> 16) & 0xFF);
        assertEquals(127, (lerped >> 8) & 0xFF);
        assertEquals(127, lerped & 0xFF);

        // Hardware gradient submits cleanly
        PoseStack ps = new PoseStack();
        ConfigBackgrounds.drawVerticalGradientRect(ps, 0, 0, 100, 100, 0xFF000000, 0xFF777777, 0xFFFFFFFF, 1.0f);
        BufferBuilder ready = ConfigGuiBatch.finishPending();
        assertNotNull(ready);
        ready.popNextBuffer();
    }
}
