package com.kingodogo.buildscape.mixin.fabric.client;

import com.mojang.serialization.Lifecycle;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public abstract class ExperimentalSettingsMixin {
    // Modded world generation makes this lifecycle experimental. Skip only the
    // load-time lifecycle warning; keep the saved lifecycle and other prompts intact.
    @Redirect(
            method = "doLoadLevel(Ljava/lang/String;Ljava/util/function/Function;Ljava/util/function/Function;ZLnet/minecraft/client/Minecraft$ExperimentalDialogType;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/WorldData;worldGenSettingsLifecycle()Lcom/mojang/serialization/Lifecycle;"),
            require = 1)
    private Lifecycle buildscape$skipExperimentalSettingsWarning(WorldData worldData) {
        return Lifecycle.stable();
    }
}
