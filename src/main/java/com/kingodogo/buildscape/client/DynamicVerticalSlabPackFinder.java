package com.kingodogo.buildscape.client;

import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.RepositorySource;

import java.util.function.Consumer;

public class DynamicVerticalSlabPackFinder implements RepositorySource {
    @Override
    public void loadPacks(Consumer<Pack> consumer, Pack.PackConstructor packConstructor) {
        Pack pack = Pack.create(
                "buildscape_dynamic_vertical_slabs",
                true,
                () -> new DynamicVerticalSlabPack(),
                packConstructor,
                Pack.Position.TOP,
                net.minecraft.server.packs.repository.PackSource.BUILT_IN
        );
        if (pack != null) {
            consumer.accept(pack);
        }
    }
}
