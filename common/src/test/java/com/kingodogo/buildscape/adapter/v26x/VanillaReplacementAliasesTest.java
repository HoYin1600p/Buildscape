package com.kingodogo.buildscape.adapter.v26x;

import com.kingodogo.buildscape.TestBootstrap;
import com.kingodogo.buildscape.registry.VanillaReplacementAliases;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class VanillaReplacementAliasesTest {
    @Test
    void everyReplacementExistsInTheCorrespondingVanillaRegistry() {
        TestBootstrap.initialize();
        var sources = new HashSet<String>();
        for (var alias : VanillaReplacementAliases.ALL) {
            assertTrue(sources.add(alias.source()), "Duplicate replacement: " + alias.source());
            assertEquals("buildscape", Identifier.parse(alias.source()).getNamespace());
            Identifier target = Identifier.parse(alias.target());
            assertEquals("minecraft", target.getNamespace(), alias.source());
            assertTrue(alias.block() || alias.item() || alias.entity(), alias.source());
            if (alias.block()) {
                assertTrue(BuiltInRegistries.BLOCK.containsKey(target),
                        () -> alias.source() + " has no vanilla block target: " + target);
            }
            if (alias.item()) {
                assertTrue(BuiltInRegistries.ITEM.containsKey(target),
                        () -> alias.source() + " has no vanilla item target: " + target);
            }
            if (alias.entity()) {
                assertTrue(BuiltInRegistries.ENTITY_TYPE.containsKey(target),
                        () -> alias.source() + " has no vanilla entity target: " + target);
            }
        }
    }

    @Test
    void secondPassPreservesAllFortyFiveSavedIdentifiers() {
        Set<String> expected = Arrays.stream("""
                mangrove_leaves mangrove_log mangrove_planks mangrove_propagule mangrove_roots
                mangrove_wood muddy_mangrove_roots potted_mangrove_propagule
                stripped_mangrove_log stripped_mangrove_wood
                cherry_leaves cherry_log cherry_planks cherry_sapling cherry_wood
                potted_cherry_sapling stripped_cherry_log stripped_cherry_wood
                pale_oak_leaves pale_oak_log pale_oak_planks pale_oak_sapling pale_oak_wood
                potted_pale_oak_sapling stripped_pale_oak_log stripped_pale_oak_wood
                bamboo_block stripped_bamboo_block
                copper_chest exposed_copper_chest weathered_copper_chest oxidized_copper_chest
                waxed_copper_chest waxed_exposed_copper_chest waxed_weathered_copper_chest
                waxed_oxidized_copper_chest mangrove_boat
                acacia_shelf birch_shelf crimson_shelf dark_oak_shelf jungle_shelf oak_shelf
                spruce_shelf warped_shelf
                """.strip().split("\\s+"))
                .map(path -> "buildscape:" + path).collect(Collectors.toSet());
        assertEquals(45, expected.size());
        var replacements = VanillaReplacementAliases.ALL.stream()
                .filter(alias -> expected.contains(alias.source())).toList();
        assertEquals(45, replacements.size());
        for (var alias : replacements) {
            assertEquals(alias.source().replace("buildscape:", "minecraft:"), alias.target());
            boolean boat = alias.source().equals("buildscape:mangrove_boat");
            assertEquals(!boat, alias.block(), alias.source());
            assertEquals(!alias.source().startsWith("buildscape:potted_"), alias.item(), alias.source());
            assertEquals(boat, alias.entity(), alias.source());
        }
    }

    @Test
    void vanillaCopperChestsAcceptSavedChestProperties() {
        TestBootstrap.initialize();
        var chests = VanillaReplacementAliases.ALL.stream()
                .filter(alias -> alias.source().endsWith("copper_chest")).toList();
        assertEquals(8, chests.size());
        for (var alias : chests) {
            var definition = BuiltInRegistries.BLOCK.getValue(Identifier.parse(alias.target()))
                    .getStateDefinition();
            for (String property : Set.of("facing", "type", "waterlogged")) {
                assertNotNull(definition.getProperty(property), alias.target() + ": " + property);
            }
        }
    }
}
