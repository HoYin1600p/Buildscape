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

    @Test
    public void worldSettingsRowHeightAndContentHeightAlignWithSystemTab() {
        assertEquals(36, BuildScapeConfigScreen.getWorldSettingRowHeight());
        int gap = 3;
        int expectedHeight = 14 + 14 + 9 * (36 + gap) - gap;
        assertEquals(expectedHeight, BuildScapeConfigScreen.calculateWorldContentHeight(gap));
        int rowWidth = 230;
        int btnWidth = 38;
        int descWidth = BuildScapeConfigScreen.calculateToggleDescriptionWidth(rowWidth, btnWidth);
        assertTrue(descWidth < rowWidth - btnWidth - 8);
        assertEquals(rowWidth - btnWidth - 20, descWidth);
    }

    @Test
    public void navItemsAndLinksAndSubtabsHaveValidTextures() {
        List<BuildScapeConfigScreen.NavItem> navItems = BuildScapeConfigScreen.createDefaultNavItems();
        assertEquals(6, navItems.size());
        for (BuildScapeConfigScreen.NavItem navItem : navItems) {
            assertNotNull(navItem.id);
            assertNotNull(navItem.title);
            assertNotNull(navItem.iconTexture);
            assertTrue(navItem.iconTexture.getPath().startsWith("textures/client/ui/nav_"));
            assertTrue(navItem.iconTexture.getPath().endsWith(".png"));
        }

        List<BuildScapeConfigScreen.ConfigLink> links = BuildScapeConfigScreen.getConfigLinks();
        assertEquals(5, links.size());
        for (BuildScapeConfigScreen.ConfigLink link : links) {
            assertNotNull(link.title());
            assertNotNull(link.url());
            assertNotNull(link.icon());
            assertTrue(link.icon().getPath().startsWith("textures/client/ui/link_"));
            assertTrue(link.icon().getPath().endsWith(".png"));
        }

        assertEquals("textures/client/ui/subtab_items.png", BuildScapeConfigScreen.SUBTAB_ITEMS_TEXTURE.getPath());
        assertEquals("textures/client/ui/subtab_tags.png", BuildScapeConfigScreen.SUBTAB_TAGS_TEXTURE.getPath());
        assertEquals("textures/client/ui/panel_pillar.png", BuildScapeConfigScreen.PANEL_PILLAR_TEXTURE.getPath());
        assertEquals("textures/client/ui/panel_color.png", BuildScapeConfigScreen.PANEL_COLOR_TEXTURE.getPath());
    }
}
