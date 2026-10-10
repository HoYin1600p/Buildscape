package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixinsupport.MixinFactory;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
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
    @Inject(method = "extractContent(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIZF)V", at = @At("HEAD"), cancellable = true, require = 0)
    private void buildscape$extractHeader(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float partialTick, CallbackInfo ci) {
        ObjectSelectionList.Entry<?> entry = (ObjectSelectionList.Entry<?>) (Object) this;
        if (MixinFactory.get().renderStatsEntry(this.stat, graphics, entry.getContentX(), entry.getContentY(), entry.getContentWidth())) {
            ci.cancel();
        }
    }
}
