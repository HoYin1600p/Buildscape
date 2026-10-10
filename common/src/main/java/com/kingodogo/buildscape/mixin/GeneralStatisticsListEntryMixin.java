package com.kingodogo.buildscape.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.stats.Stat;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.gui.screens.achievement.StatsScreen$GeneralStatisticsList$Entry")
public abstract class GeneralStatisticsListEntryMixin {

    @Shadow(aliases = {"f_97001_"})
    @Final
    private Stat<?> stat;

    @Dynamic
    @Inject(method = "render", at = @At("HEAD"), cancellable = true, require = 0)
    public void onRender(PoseStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isHovered, float partialTick, CallbackInfo ci) {
        if (MixinFactory.renderStatsEntry(this.stat, poseStack, left, top, width)) {
            ci.cancel();
        }
    }
}
