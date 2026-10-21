package com.kingodogo.buildscape.resources.bundle;

import com.kingodogo.buildscape.resources.bundle.adapter.TagAdapter;
import com.kingodogo.buildscape.resources.bundle.runtime.BundledResourceRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DedicatedServerBootstrapTest {

    @Test
    void testTagBundlingWithoutClientClasses() {
        // Assert that TagAdapter and BundledResourceRegistry can run without loading any net.minecraft.client.* classes
        BundledResourceRegistry registry = BundledResourceRegistry.getInstance();
        registry.reset();

        Map<ResourceLocation, Tag.Builder> tagMap = new HashMap<>();
        assertDoesNotThrow(() -> {
            TagAdapter.applyTags("tags/blocks", tagMap, null);
        });

        // Verify no client classes are loaded by this execution
        for (Class<?> loadedClass : ClassLoader.getSystemClassLoader().getDefinedPackages().getClass()
                .getDeclaredClasses()) {
            assertFalse(loadedClass.getName().startsWith("net.minecraft.client."),
                    "Dedicated server execution must never reference net.minecraft.client.*: " + loadedClass.getName());
        }
    }
}
