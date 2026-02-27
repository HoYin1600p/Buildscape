package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.data.DynamicDataPack;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModServerEvents {

    @SubscribeEvent
    public static void onPackFinder(AddPackFindersEvent event) {
        try {
            final PackType type = event.getPackType();
            final String packName = type == PackType.SERVER_DATA ? "data" : "resources";
            final String title = "BuildScape Dynamic " + (type == PackType.SERVER_DATA ? "Data" : "Resources");
            final String description = "Dynamic " + packName + " for vertical slabs";
            
            // Create the pack instance with the current PackType
            final DynamicDataPack packInstance = new DynamicDataPack(description, type);
            
            event.addRepositorySource((consumer, constructor) -> {
                try {
                    Pack pack = constructor.create(
                            BuildScape.MODID + "_dynamic_" + packName, 
                            new TextComponent(title),
                            true, 
                            () -> packInstance,
                            new PackMetadataSection(
                                    new TextComponent(description), 
                                    9
                            ),
                            Pack.Position.BOTTOM,
                            PackSource.BUILT_IN
                    );
                    
                    if (pack != null) {
                        consumer.accept(pack);
                    }
                } catch (Exception e) {
                    BuildScape.LOGGER.error("Failed to create dynamic pack for " + packName, e);
                }
            });
        } catch (Exception e) {
            BuildScape.LOGGER.error("Failed to register dynamic data pack finder", e);
        }
    }

}
