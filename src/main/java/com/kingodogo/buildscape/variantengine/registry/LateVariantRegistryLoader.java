package com.kingodogo.buildscape.variantengine.registry;

import net.minecraftforge.fml.common.Mod;

/**
 * This mod class exists solely to provide a late-loading event bus.
 * By using a mod ID that starts with 'zz_', we ensure that Forge fires
 * its registry events after almost all other mods have finished theirs.
 */
@Mod("zz_buildscape_variants")
public class LateVariantRegistryLoader {
    public LateVariantRegistryLoader() {
        // No logic needed here; the @EventBusSubscriber classes will attach to this mod's bus.
    }
}
