package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;

public final class ClientAdvancementEvents {
    private ClientAdvancementEvents() {}

    public static void pan(AdvancementTab tab, double horizontal, double vertical) {
        if (tab != null) {
            if (Services.PLATFORM.hasShiftDown()) {
                tab.scroll(vertical * 32.0D, horizontal * 32.0D);
            } else {
                tab.scroll(horizontal * 32.0D, vertical * 32.0D);
            }
        }
    }
}
