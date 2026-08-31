package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.stat.ModStats;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
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
    private Stat<ResourceLocation> stat;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void onRender(PoseStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isHovered, float partialTick, CallbackInfo ci) {
        if (this.stat == ModStats.HEADER_MINECRAFT_STAT || this.stat == ModStats.HEADER_BUILDSCAPE_STAT || this.stat == ModStats.HEADER_OTHER_STAT) {
            Minecraft mc = Minecraft.getInstance();
            Component text;
            int color;
            if (this.stat == ModStats.HEADER_MINECRAFT_STAT) {
                text = new TextComponent("§6§l═══ Minecraft Statistics ═══");
                color = 0xFFFFAA00;
            } else if (this.stat == ModStats.HEADER_BUILDSCAPE_STAT) {
                text = new TextComponent("§b§l═══ Buildscape Statistics ═══");
                color = 0xFF55FFFF;
            } else {
                text = new TextComponent("§e§l═══ Other Mod Statistics ═══");
                color = 0xFFFFFF55;
            }
            int textWidth = mc.font.width(text);
            float centerX = left + width / 2.0f - textWidth / 2.0f;
            mc.font.draw(poseStack, text, centerX, (float) (top + 1), color);
            ci.cancel();
        }
    }
}
