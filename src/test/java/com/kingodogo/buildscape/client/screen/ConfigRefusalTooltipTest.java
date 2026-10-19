package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.recipe.framework.cache.RecipeCacheRegressionTest;
import com.kingodogo.buildscape.config.ConfigSetting;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConfigRefusalTooltipTest {
    @BeforeClass
    public static void bootstrap() {
        RecipeCacheRegressionTest.bootstrap();
    }

    @Test
    public void lockedControlUsesItsSettingScopeAndSharedClickRefusalText() {
        Component text = BuildScapeConfigScreen.hoverRefusal(true, PillarParticleConfig.EditPermissionMode.STANDARD,
                2, ConfigSetting.PARTICLES, true);
        assertNotNull(text);
        assertEquals(BuildScapeConfigScreen.refusalText(ConfigSetting.PARTICLES.scope, false, true), text);
        assertNull(BuildScapeConfigScreen.hoverRefusal(true, PillarParticleConfig.EditPermissionMode.STANDARD,
                2, ConfigSetting.ITEMS, true));
        assertNotNull(BuildScapeConfigScreen.hoverRefusal(true, PillarParticleConfig.EditPermissionMode.OPEN,
                2, ConfigSetting.POLICY, true));
    }

    @Test
    public void protectedFileControlUsesSharedReadOnlyRefusalText() {
        Component text = BuildScapeConfigScreen.hoverRefusal(true, PillarParticleConfig.EditPermissionMode.STANDARD,
                2, ConfigSetting.ITEMS, false);
        assertEquals(new TranslatableComponent("buildscape.config.error.read_only"), text);
        assertEquals(BuildScapeConfigScreen.refusalText(ConfigSetting.ITEMS.scope, true, false), text);
    }

    @Test
    public void leavingControlOrHavingAccessAllowsNormalHoverText() {
        assertNull(BuildScapeConfigScreen.hoverRefusal(false, PillarParticleConfig.EditPermissionMode.STRICT,
                0, ConfigSetting.ITEMS, false));
        assertNull(BuildScapeConfigScreen.hoverRefusal(true, PillarParticleConfig.EditPermissionMode.STRICT,
                4, ConfigSetting.PARTICLES, true));
        assertNull(BuildScapeConfigScreen.hoverRefusal(true, PillarParticleConfig.EditPermissionMode.OPEN,
                0, ConfigSetting.ITEMS, true));
    }
}
