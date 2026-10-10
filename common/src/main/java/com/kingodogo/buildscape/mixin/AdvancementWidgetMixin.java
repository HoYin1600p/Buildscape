package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixinsupport.MixinFactory;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AdvancementWidget.class)
public abstract class AdvancementWidgetMixin {
    @Shadow @Final private AdvancementNode advancementNode;
    @Shadow @Final private DisplayInfo display;

    @Dynamic
    @Redirect(method = {
            "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V",
            "extractHover(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIFII)V"
    }, at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screens/advancements/AdvancementWidget;icon:Lnet/minecraft/world/item/ItemStack;"), require = 0)
    private ItemStack buildscape$cycleIcon(AdvancementWidget widget) {
        return MixinFactory.cycleAdvancementIcon(advancementNode, display);
    }
}
