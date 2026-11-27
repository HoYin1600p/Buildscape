package com.kingodogo.buildscape.mixin.fabric;

import com.kingodogo.buildscape.event.AdvancementEvents;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancements.class)
public abstract class AdvancementCompletionMixin {
    @Shadow private ServerPlayer player;

    // This call occurs only when progress changes from incomplete to complete.
    @Inject(method = "award", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/advancements/AdvancementRewards;grant(Lnet/minecraft/server/level/ServerPlayer;)V",
            shift = At.Shift.AFTER))
    private void buildscape$earned(AdvancementHolder holder, String criterion, CallbackInfoReturnable<Boolean> cir) {
        var id = holder.id();
        AdvancementEvents.onAdvancementEarned(player, new CommonId(id.getNamespace(), id.getPath()));
    }
}
