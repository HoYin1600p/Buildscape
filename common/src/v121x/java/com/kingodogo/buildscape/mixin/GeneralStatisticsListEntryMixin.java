package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixinsupport.MixinFactory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.stats.Stat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.gui.screens.achievement.StatsScreen$GeneralStatisticsList$Entry")
public abstract class GeneralStatisticsListEntryMixin {

    @Shadow
    @Final
    private Stat<?> stat;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void buildscape$renderHeader(GuiGraphics graphics, int index, int top, int left, int width, int height,
            int mouseX, int mouseY, boolean hovered, float partialTick, CallbackInfo ci) {
        if (MixinFactory.get().renderStatsEntry(this.stat, graphics, left, top, width)) {
            ci.cancel();
        }
    }
}
