package com.kingodogo.buildscape.variantengine;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.variantengine.resources.VariantPackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class VariantEngine {

    @SubscribeEvent
    public static void onAddPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES || event.getPackType() == PackType.SERVER_DATA) {
            VariantPackResources pack = new VariantPackResources();
            event.addRepositorySource((packConsumer, packConstructor) -> {
                Pack builtPack = Pack.create(
                        "buildscape_variant_engine",
                        true,
                        () -> pack,
                        packConstructor,
                        Pack.Position.BOTTOM,
                        PackSource.BUILT_IN
                );
                if (builtPack != null) {
                    packConsumer.accept(builtPack);
                }
            });
        }
    }
}
