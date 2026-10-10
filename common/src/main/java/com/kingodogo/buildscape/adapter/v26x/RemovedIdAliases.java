package com.kingodogo.buildscape.adapter.v26x;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.registry.VanillaReplacementAliases;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

import java.util.function.BiConsumer;

/** Installs loader aliases without introducing duplicate entries or numeric IDs. */
public final class RemovedIdAliases {
    private RemovedIdAliases() {}

    public static void register(Registry<?> registry, BiConsumer<Identifier, Identifier> install) {
        boolean blocks = registry.key().equals(Registries.BLOCK);
        boolean items = registry.key().equals(Registries.ITEM);
        if (!blocks && !items) {
            throw new IllegalArgumentException("Replacement aliases require the block or item registry");
        }
        Services.PLATFORM.wrapRegistryAction(() -> {
            for (var alias : VanillaReplacementAliases.ALL) {
                if (!(blocks ? alias.block() : alias.item())) continue;
                Identifier source = Identifier.parse(alias.source());
                // A still-registered ID retains its meaning until its removal is applied.
                if (registry.containsKey(source)) continue;
                Identifier target = Identifier.parse(alias.target());
                if (!registry.containsKey(target)) {
                    throw new IllegalStateException("Missing vanilla replacement for " + source + ": " + target);
                }
                install.accept(source, target);
            }
        });
    }
}
