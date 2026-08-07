package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.client.renderer.CopperChestRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Registers copper chest sprites with Minecraft's dedicated chest atlas.
 *
 * @author HoYin1600p
 */
@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class CopperChestClientEvents {
    private CopperChestClientEvents() {
    }

    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        CopperChestRenderer.registerTextures(event);
    }
}
