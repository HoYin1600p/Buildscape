package com.kingodogo.buildscape.worldgen;

import com.kingodogo.buildscape.adapter.v118x.PlantPlacements;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.event.world.BiomeLoadingEvent;

public final class ModBiomeModifications {
    private ModBiomeModifications() {}

    public static void onBiomeLoading(BiomeLoadingEvent event) {
        if (event.getName() == null) return;
        for (BiomePlacements.Entry entry : BiomePlacements.ENTRIES) {
            if (!PlantPlacements.biomes(entry).contains(event.getName().toString())) continue;
            for (String feature : entry.features()) {
                event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                        Holder.direct(PlantPlacements.create(feature)));
            }
        }
    }
}
