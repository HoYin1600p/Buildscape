package com.kingodogo.buildscape.data;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModDataGen {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        if (event.includeServer()) {
            generator.addProvider(new ModRecipeProvider(generator));
            generator.addProvider(new ModBlockTagsProvider(generator, existingFileHelper));
        }

        if (event.includeClient()) {
            // generator.addProvider(new ModItemModelProvider(generator, existingFileHelper));
        }
    }
}
