package com.kingodogo.buildscape.adapter.v26x;

import com.kingodogo.buildscape.TestBootstrap;
import com.kingodogo.buildscape.registry.VanillaReplacementAliases;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

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
            assertTrue(alias.block() || alias.item(), alias.source());
            if (alias.block()) {
                assertTrue(BuiltInRegistries.BLOCK.containsKey(target),
                        () -> alias.source() + " has no vanilla block target: " + target);
            }
            if (alias.item()) {
                assertTrue(BuiltInRegistries.ITEM.containsKey(target),
                        () -> alias.source() + " has no vanilla item target: " + target);
            }
        }
    }
}
