package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.config.ConfigSetting;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.recipe.framework.cache.RecipeCacheRegressionTest;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class ConfigScreenConsistencyTest {
    @BeforeClass
    public static void bootstrap() {
        RecipeCacheRegressionTest.bootstrap();
    }

    @Test
    public void everyTabAsksForTheOperationItsPacketPerforms() {
        assertEquals(ConfigSetting.ITEMS, BuildScapeConfigScreen.settingForTab("items", false));
        assertEquals(ConfigSetting.ITEMS, BuildScapeConfigScreen.settingForTab("pillars", true));
        assertEquals(ConfigSetting.PILLAR_EDIT, BuildScapeConfigScreen.settingForTab("pillars", false));
        assertEquals(ConfigSetting.WORLD_RULES, BuildScapeConfigScreen.settingForTab("world", false));
        assertEquals(ConfigSetting.PARTICLES, BuildScapeConfigScreen.settingForTab("particles", false));
        assertEquals(ConfigSetting.PARTICLES, BuildScapeConfigScreen.settingForTab("system", false));
    }

    @Test
    public void screenAndServerAgreeForPillarAndWorldControlsAtEveryModeAndLevel() {
        for (PillarParticleConfig.EditPermissionMode mode : PillarParticleConfig.EditPermissionMode.values()) {
            for (int level : new int[]{0, 2, 4}) {
                for (String tab : new String[]{"pillars", "world"}) {
                    ConfigSetting setting = BuildScapeConfigScreen.settingForTab(tab, false);
                    boolean serverAllows = PillarParticleConfig.canEdit(mode, level, setting.scope);
                    boolean screenBlocks = BuildScapeConfigScreen.hoverRefusal(true, mode, level, setting, true) != null;
                    assertEquals(mode + "/" + level + "/" + tab, serverAllows, !screenBlocks);
                }
            }
        }
        // The two cases from the review: level 2 edits pillars under STANDARD; anyone changes the World tab under OPEN.
        assertNull(BuildScapeConfigScreen.hoverRefusal(true, PillarParticleConfig.EditPermissionMode.STANDARD, 2,
                ConfigSetting.PILLAR_EDIT, true));
        assertNull(BuildScapeConfigScreen.hoverRefusal(true, PillarParticleConfig.EditPermissionMode.OPEN, 0,
                ConfigSetting.WORLD_RULES, true));
        assertNotNull(BuildScapeConfigScreen.hoverRefusal(true, PillarParticleConfig.EditPermissionMode.STANDARD, 2,
                ConfigSetting.WORLD_RULES, true));
    }

    @Test
    public void arrowsAndTabMoveBetweenTabsAndWrap() {
        assertEquals(-1, BuildScapeConfigScreen.tabKeyDirection(263));
        assertEquals(1, BuildScapeConfigScreen.tabKeyDirection(262));
        assertEquals(1, BuildScapeConfigScreen.tabKeyDirection(258));
        assertEquals(0, BuildScapeConfigScreen.tabKeyDirection(65));
        assertEquals(0, BuildScapeConfigScreen.tabKeyDirection(259));
        assertEquals(4, BuildScapeConfigScreen.moveSelection(0, BuildScapeConfigScreen.tabKeyDirection(263), 5));
        assertEquals(0, BuildScapeConfigScreen.moveSelection(4, BuildScapeConfigScreen.tabKeyDirection(258), 5));
    }

    @Test
    public void serverConfigReplacesSelectionsAfterARefusedEditAndAnotherPlayersEdit() {
        Set<Item> items = new HashSet<>();
        Set<String> tags = new HashSet<>();
        // This player clicked a stick, but the server refused it and answered with the unchanged config.
        BuildScapeConfigScreen.collectActiveSelection(List.of("minecraft:stick"), items, tags);
        assertEquals(Set.of(Items.STICK), items);
        BuildScapeConfigScreen.collectActiveSelection(List.of("minecraft:diamond", "#forge:gems"), items, tags);
        assertEquals(Set.of(Items.DIAMOND), items);
        assertEquals(Set.of("#forge:gems"), tags);
        // Another administrator removed the diamond and the tag, and added an apple.
        BuildScapeConfigScreen.collectActiveSelection(List.of("minecraft:apple", "minecraft:unknown_item"), items, tags);
        assertEquals(Set.of(Items.APPLE), items);
        assertTrue(tags.isEmpty());
    }
}
