package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.BuildScape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEvents {
    // We have moved to a DynamicDataPack approach for vertical blocks.
    // This file is now primarily for client-side events that aren't handled by the dynamic pack.
}
