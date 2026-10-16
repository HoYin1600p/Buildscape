package com.kingodogo.buildscape.client.screen;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BuildScapeConfigContinuationTest {
    @Test
    void destructivePillarActionsRequireAnExplicitSecondActivation() {
        assertFalse(BuildScapeConfigScreen.isConfirmedAction(null, "remove:selected"));
        assertTrue(BuildScapeConfigScreen.isConfirmedAction("remove:selected", "remove:selected"));
        assertFalse(BuildScapeConfigScreen.isConfirmedAction("remove:all", "remove:selected"));
    }

    @Test
    void presetNamesAreTrimmedAndBounded() {
        assertEquals("My Build", BuildScapeConfigScreen.normalizePresetName("  My Build  "));
        assertEquals("Custom Preset", BuildScapeConfigScreen.normalizePresetName("   "));
        assertEquals(32, BuildScapeConfigScreen.normalizePresetName("x".repeat(40)).length());
    }

    @Test
    void keyboardTabNavigationWrapsInBothDirections() {
        assertEquals(0, BuildScapeConfigScreen.moveSelection(5, 1, 6));
        assertEquals(5, BuildScapeConfigScreen.moveSelection(0, -1, 6));
    }

    @Test
    void optionalBackgroundsAreNotNamedByTheMod() {
        assertEquals(List.of("none", "green_creeper", "golden_creeper", "diamond_creeper", "crimson_creeper"),
                ConfigBackgrounds.getBuiltInBackgroundIds());
        assertEquals("Sky Castle", ConfigBackgrounds.formatDisplayName("sky_castle.png"));
    }

    @Test
    void presetSideTabUsesCompactCenteredGeometry() {
        BuildScapeConfigScreen.PresetSideTabGeometry idle = BuildScapeConfigScreen.calculatePresetSideTabGeometry(100, 40, false);
        BuildScapeConfigScreen.PresetSideTabGeometry hovered = BuildScapeConfigScreen.calculatePresetSideTabGeometry(100, 40, true);

        assertEquals(98, idle.x());
        assertEquals(30, idle.width());
        assertEquals(30, idle.height());
        assertEquals(idle.x() + idle.width() / 2, idle.centerX());
        assertEquals(32, hovered.width());
        assertEquals(hovered.x() + hovered.width() / 2, hovered.centerX());
    }

    @Test
    void emptyTagDiscoveryIsRetriedInsteadOfCached() {
        assertFalse(BuildScapeConfigScreen.shouldCacheTagDiscovery(List.of()));
        assertTrue(BuildScapeConfigScreen.shouldCacheTagDiscovery(List.of("minecraft:logs")));
    }

    @Test
    void itemToolbarAlwaysFitsOnOneLine() {
        BuildScapeConfigScreen.ItemToolbarGeometry toolbar = BuildScapeConfigScreen.calculateItemToolbarGeometry(824);

        assertTrue(toolbar.filterWidth() > 0);
        assertTrue(toolbar.searchWidth() > 0);
        assertTrue(toolbar.namespaceWidth() > 0);
        assertTrue(toolbar.totalWidth() <= 824);
        assertTrue(toolbar.filterWidth() >= 88);
        assertTrue(toolbar.namespaceWidth() >= 120);
    }

    @Test
    void authoritativeRegistryTagsAreMergedWithHolderCounts() {
        assertEquals(List.of("#buildscape:pillars", "#c:gems", "#forge:ores"),
                BuildScapeConfigScreen.mergeDiscoveredTags(
                        List.of("forge:ores", "c:gems", "buildscape:pillars"),
                        List.of("#forge:ores", "#c:gems")));
    }

    @Test
    void serverSyncedPermissionLevelOverridesUnreliableClientPermissions() {
        assertEquals(4, BuildScapeConfigScreen.resolveClientPermissionLevel(4, false, false));
        assertEquals(2, BuildScapeConfigScreen.resolveClientPermissionLevel(-1, true, false));
        assertEquals(0, BuildScapeConfigScreen.resolveClientPermissionLevel(-1, false, false));
    }

    @Test
    void readOnlyFeedbackExpiresCleanly() {
        assertTrue(BuildScapeConfigScreen.readOnlyFeedbackAlpha(1_000L, 1_000L) > 0.0F);
        assertEquals(0.0F, BuildScapeConfigScreen.readOnlyFeedbackAlpha(1_700L, 1_000L));
    }

    @Test
    void worldSettingsExposeBothRuleSections() {
        assertEquals(List.of("Player Settings", "World Rules"), BuildScapeConfigScreen.getWorldSettingSections());
    }

    @Test
    void searchTreatsSpacesUnderscoresAndHyphensAsEquivalent() {
        assertTrue(BuildScapeConfigScreen.matchesConfigSearch("buildscape:smooth_sand", null, "smooth sand"));
        assertTrue(BuildScapeConfigScreen.matchesConfigSearch("buildscape:smooth-sand", null, "smooth_sand"));
    }

    @Test
    void typingResetsBothSearchScrollPositions() {
        assertEquals(List.of(0.0D, 0.0D), BuildScapeConfigScreen.resetSearchScrollOffsets(48.0D, 96.0D));
    }

    @Test
    void tagNamespacesIgnoreTheTagMarker() {
        assertEquals(List.of("buildscape", "c", "forge"),
                BuildScapeConfigScreen.discoverNamespaces(
                        List.of("#forge:ores", "#c:gems", "#buildscape:pillars")));
    }

    @Test
    void compactToolbarLabelsDoNotContainVariableCounts() {
        assertEquals(List.of("All", "Inventory", "Active"), BuildScapeConfigScreen.getCompactFilterLabels());
        assertEquals("All", BuildScapeConfigScreen.getNamespaceSelectorLabel(-1, List.of("minecraft", "forge")));
        assertEquals("forge", BuildScapeConfigScreen.getNamespaceSelectorLabel(1, List.of("minecraft", "forge")));
    }

    @Test
    void systemTabContainsOnlyInteractiveSettings() {
        assertEquals(4, BuildScapeConfigScreen.getSystemSettingRowCount());
    }

    @Test
    void cycleDescriptionsStopBeforeTheirControls() {
        int available = BuildScapeConfigScreen.calculateCycleDescriptionWidth(520, 138);
        assertTrue(available > 0);
        assertTrue(available + 138 + 20 <= 520);
    }

    @Test
    void compactToolbarControlsShareOneHeightAndFitInventory() {
        BuildScapeConfigScreen.ItemToolbarGeometry toolbar = BuildScapeConfigScreen.calculateItemToolbarGeometry(360);
        assertEquals(15, BuildScapeConfigScreen.getItemToolbarControlHeight());
        assertTrue(toolbar.filterWidth() >= 60);
    }

    @Test
    void systemRowsReserveTwoLinesForCompleteDescriptions() {
        assertEquals(36, BuildScapeConfigScreen.getSystemSettingRowHeight());
        assertEquals(153, BuildScapeConfigScreen.calculateSystemContentHeight(3));
    }

    @Test
    void selectedSearchTextIsReplacedOrClearedInOneAction() {
        assertEquals("s", BuildScapeConfigScreen.applySearchCharacter("smooth sand", true, 's'));
        assertEquals("", BuildScapeConfigScreen.applySearchBackspace("smooth sand", true));
        assertEquals("smooth san", BuildScapeConfigScreen.applySearchBackspace("smooth sand", false));
    }

    @Test
    void itemContentTransitionsFadeAndSettle() {
        assertTrue(BuildScapeConfigScreen.calculateItemContentTransitionAlpha(0L, 0L, 200) < 1.0F);
        assertEquals(1.0F, BuildScapeConfigScreen.calculateItemContentTransitionAlpha(250L, 0L, 200));
    }
}
