package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixinsupport.MixinFactory;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = GuiGraphics.class, priority = 900)
public abstract class GuiGraphicsTooltipMixin {
    @Inject(method = "renderTooltipInternal", at = @At("TAIL"))
    private void buildscape$renderPreviewOutside(Font font, List<ClientTooltipComponent> components, int mouseX, int mouseY,
            ClientTooltipPositioner positioner, CallbackInfo ci) {
        MixinFactory.get().renderCustomScreenTooltip((Object) this, components, mouseX, mouseY,
                net.minecraft.world.item.ItemStack.EMPTY, font);
    }
}
