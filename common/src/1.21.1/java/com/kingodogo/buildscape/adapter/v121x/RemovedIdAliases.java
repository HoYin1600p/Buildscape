package com.kingodogo.buildscape.adapter.v121x;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.registry.VanillaReplacementAliases;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;

/** Installs saved-ID aliases after content registration without duplicate registry entries. */
public final class RemovedIdAliases {
    private RemovedIdAliases() {}

    public static void register(Registry<?> registry, BiConsumer<ResourceLocation, ResourceLocation> install) {
        boolean blocks = registry.key().equals(Registries.BLOCK);
        boolean items = registry.key().equals(Registries.ITEM);
        boolean entities = registry.key().equals(Registries.ENTITY_TYPE);
        if (!blocks && !items && !entities) {
            throw new IllegalArgumentException("Replacement aliases require the block, item or entity type registry");
        }
        Services.PLATFORM.wrapRegistryAction(() -> {
            for (var alias : VanillaReplacementAliases.forCluster("v121x")) {
                if (!(blocks ? alias.block() : items ? alias.item() : alias.entity())) continue;
                ResourceLocation source = ResourceLocation.parse(alias.source());
                if (registry.containsKey(source)) continue;
                ResourceLocation target = ResourceLocation.parse(alias.target());
                if (!registry.containsKey(target)) {
                    throw new IllegalStateException("Missing vanilla replacement for " + source + ": " + target);
                }
                install.accept(source, target);
            }
        });
    }
}