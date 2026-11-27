package com.kingodogo.buildscape.world;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModGameRulesTest {
    @Test
    void retainsAllSixReferenceNamesCategoriesAndDefaults() {
        var definitions = ModGameRules.definitions(true, true);
        assertEquals(List.of("fastLeafDecay", "disableEndermanGriefing", "disableCreeperGriefing",
                "disableGhastGriefing", "isCakeStack", "isWaterbottleStack"),
                definitions.stream().map(ModGameRules.Definition::name).toList());
        assertEquals(List.of(false, false, false, false, true, true),
                definitions.stream().map(ModGameRules.Definition::defaultValue).toList());
        assertTrue(definitions.stream().allMatch(definition -> definition.category().equals("MISC")));
        assertEquals(6, definitions.stream().map(ModGameRules.Definition::registryPath).distinct().count());
        assertEquals(List.of("fast_leaf_decay", "disable_enderman_griefing", "disable_creeper_griefing",
                "disable_ghast_griefing", "is_cake_stack", "is_waterbottle_stack"),
                definitions.stream().map(ModGameRules.Definition::registryPath).toList());
        assertTrue(definitions.stream().allMatch(definition -> definition.registryPath().matches("[a-z_]+")));
    }

    @Test
    void stackingDefaultsRespectEachIndependentConfigSetting() {
        for (boolean cake : List.of(false, true)) {
            for (boolean water : List.of(false, true)) {
                var definitions = ModGameRules.definitions(cake, water);
                assertEquals(cake, definitions.get(4).defaultValue());
                assertEquals(water, definitions.get(5).defaultValue());
                assertTrue(definitions.subList(0, 4).stream().noneMatch(ModGameRules.Definition::defaultValue));
            }
        }
    }

    @Test
    void rejectsUnknownUpdateNames() {
        assertNull(ModGameRules.ruleByName("mobGriefing"));
        assertNull(ModGameRules.ruleByName("buildscape:unknown"));
        assertNull(ModGameRules.ruleByName(null));
    }
}
