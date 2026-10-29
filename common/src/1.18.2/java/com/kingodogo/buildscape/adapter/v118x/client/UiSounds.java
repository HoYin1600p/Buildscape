package com.kingodogo.buildscape.adapter.v118x.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;

/**
 * Client-only UI sound playback. Kept out of PlatformAdapterBase so the dedicated server can
 * verify and load that class without resolving client types such as SoundInstance.
 */
public final class UiSounds {
    private UiSounds() {}

    public static void play(SoundEvent event) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(event, 1.0F));
    }
}
