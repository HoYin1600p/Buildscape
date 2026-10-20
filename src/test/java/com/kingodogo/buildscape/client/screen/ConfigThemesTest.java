package com.kingodogo.buildscape.client.screen;

import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class ConfigThemesTest {

    @Test
    public void testBaseThemesAreAllDarkAndNamedProperly() {
        List<ConfigThemes.Theme> baseThemes = ConfigThemes.getBaseThemes();
        assertNotNull(baseThemes);
        assertFalse(baseThemes.isEmpty());

        Set<String> ids = new HashSet<>();
        for (ConfigThemes.Theme base : baseThemes) {
            assertNotNull(base);
            assertNotNull(base.id);
            assertNotNull(base.displayName);

            assertTrue("Base theme must be dark: " + base.id, base.isDark());
            assertFalse("Base theme cannot be light: " + base.id, base.isLight());
            assertFalse("Base theme display name must not say Light: " + base.displayName,
                    base.displayName.toLowerCase().contains("light"));

            assertTrue("Duplicate base theme id: " + base.id, ids.add(base.id));
        }
    }

    @Test
    public void testEveryBaseThemeHasALightAndDarkVariant() {
        for (ConfigThemes.Theme base : ConfigThemes.getBaseThemes()) {
            ConfigThemes.Theme dark = ConfigThemes.getDarkVariant(base);
            ConfigThemes.Theme light = ConfigThemes.getLightVariant(base);

            assertNotNull("Dark variant missing for " + base.id, dark);
            assertNotNull("Light variant missing for " + base.id, light);

            assertSame(base, dark);
            assertTrue("Dark variant must be dark: " + dark.id, dark.isDark());
            assertTrue("Light variant must be light: " + light.id, light.isLight());

            // Light variant must map back to the base theme
            assertSame("Light variant " + light.id + " should map back to base " + base.id,
                    base, ConfigThemes.getBaseTheme(light));

            // getVariant test
            assertSame(dark, ConfigThemes.getVariant(base, false));
            assertSame(light, ConfigThemes.getVariant(base, true));

            // getOppositeVariant test
            assertSame(light, ConfigThemes.getOppositeVariant(dark));
            assertSame(dark, ConfigThemes.getOppositeVariant(light));
        }
    }

    @Test
    public void testPinkAndCyanAndMagentaAndSpecialPairs() {
        ConfigThemes.Theme pinkDark = ConfigThemes.getById("pink");
        ConfigThemes.Theme pinkLight = ConfigThemes.getById("light_pink");
        assertNotNull(pinkDark);
        assertNotNull(pinkLight);
        assertTrue(pinkDark.isDark());
        assertTrue(pinkLight.isLight());
        assertEquals("Pink", pinkDark.displayName);
        assertEquals("Light Pink", pinkLight.displayName);
        assertSame(pinkDark, ConfigThemes.getBaseTheme(pinkLight));

        ConfigThemes.Theme cyanDark = ConfigThemes.getById("cyan");
        ConfigThemes.Theme cyanLight = ConfigThemes.getById("light_cyan");
        assertNotNull(cyanDark);
        assertNotNull(cyanLight);
        assertTrue(cyanDark.isDark());
        assertTrue(cyanLight.isLight());
        assertSame(cyanDark, ConfigThemes.getBaseTheme(cyanLight));

        ConfigThemes.Theme magentaDark = ConfigThemes.getById("magenta");
        ConfigThemes.Theme magentaLight = ConfigThemes.getById("light_magenta");
        assertNotNull(magentaDark);
        assertNotNull(magentaLight);
        assertTrue(magentaDark.isDark());
        assertTrue(magentaLight.isLight());
        assertSame(magentaDark, ConfigThemes.getBaseTheme(magentaLight));

        // Test Silver and Gold
        for (String baseId : new String[]{"silver", "gold"}) {
            ConfigThemes.Theme d = ConfigThemes.getById(baseId);
            ConfigThemes.Theme l = ConfigThemes.getById("light_" + baseId);
            assertNotNull("Missing dark theme: " + baseId, d);
            assertNotNull("Missing light theme: light_" + baseId, l);
            assertTrue(d.id + " must be dark", d.isDark());
            assertTrue(l.id + " must be light", l.isLight());
            assertSame(d, ConfigThemes.getBaseTheme(l));
        }
    }

    @Test
    public void testCycleBaseThemes() {
        List<ConfigThemes.Theme> baseThemes = ConfigThemes.getBaseThemes();
        for (int i = 0; i < baseThemes.size(); i++) {
            ConfigThemes.Theme curr = baseThemes.get(i);
            ConfigThemes.Theme next = ConfigThemes.cycleNextBaseTheme(curr);
            ConfigThemes.Theme prev = ConfigThemes.cyclePrevBaseTheme(curr);

            ConfigThemes.Theme expectedNext = baseThemes.get((i + 1) % baseThemes.size());
            ConfigThemes.Theme expectedPrev = baseThemes.get((i - 1 + baseThemes.size()) % baseThemes.size());

            assertSame(expectedNext, next);
            assertSame(expectedPrev, prev);
        }
    }
}
