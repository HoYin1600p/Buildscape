package com.kingodogo.buildscape.client.event;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.fluid.ModFluids;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientFluidEvents {

    @SubscribeEvent
    public static void onFogColors(EntityViewRenderEvent.FogColors event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.player != null) {
            FluidState fluidState = mc.level.getFluidState(event.getCamera().getBlockPosition());
            if (fluidState.getType() == ModFluids.EXPERIENCE_STILL.get() || fluidState.getType() == ModFluids.EXPERIENCE_FLOWING.get()) {
                // Lime fog colors
                event.setRed(0.3F);
                event.setGreen(0.9F);
                event.setBlue(0.1F);
            }
        }
    }

    @SubscribeEvent
    public static void onRenderFog(EntityViewRenderEvent.RenderFogEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.player != null) {
            FluidState fluidState = mc.level.getFluidState(event.getCamera().getBlockPosition());
            if (fluidState.getType() == ModFluids.EXPERIENCE_STILL.get() || fluidState.getType() == ModFluids.EXPERIENCE_FLOWING.get()) {
                event.setFarPlaneDistance(12.0F);
                event.setNearPlaneDistance(0.5F);
            }
        }
    }
}
