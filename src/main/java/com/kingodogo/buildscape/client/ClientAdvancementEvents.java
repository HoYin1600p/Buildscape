package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientAdvancementEvents {

    private static Field selectedTabField = null;
    private static boolean selectedTabLookupFailed;

    private static AdvancementTab getSelectedTab(AdvancementsScreen screen) {
        if (selectedTabLookupFailed) {
            return null;
        }
        try {
            if (selectedTabField == null) {
                selectedTabField = ObfuscationReflectionHelper.findField(AdvancementsScreen.class, "f_97336_");
                selectedTabField.setAccessible(true);
            }
            return (AdvancementTab) selectedTabField.get(screen);
        } catch (Exception e) {
            // reflection on a private vanilla field; any failure ends further lookups
            selectedTabLookupFailed = true;
            return null;
        }
    }

    @SubscribeEvent
    public static void onMouseScroll(ScreenEvent.MouseScrollEvent.Pre event) {
        if (event.getScreen() instanceof AdvancementsScreen screen) {
            double delta = event.getScrollDelta();
            AdvancementTab tab = getSelectedTab(screen);
            if (tab == null) {
                return;
            }
            if (Screen.hasShiftDown()) {
                tab.scroll(delta * 32.0, 0.0);
            } else {
                tab.scroll(0.0, delta * 32.0);
            }
            event.setCanceled(true);
        }
    }
}
