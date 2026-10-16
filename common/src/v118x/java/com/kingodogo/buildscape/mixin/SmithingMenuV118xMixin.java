package com.kingodogo.buildscape.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmithingMenu.class)
public abstract class SmithingMenuV118xMixin {
    @Unique
    private ItemStack buildscape$savedTemplate = ItemStack.EMPTY;

    @Inject(method = "onTake", at = @At("HEAD"))
    private void buildscape$preserveTemplateHead(Player player, ItemStack stack, CallbackInfo ci) {
        ItemStack rightSlot = ((ItemCombinerMenuAccessor) this).buildscape$getInputSlots().getItem(1);
        buildscape$savedTemplate = isOrnamentTemplate(rightSlot)
                ? rightSlot.copy() : ItemStack.EMPTY;
    }

    @Unique
    private static boolean isOrnamentTemplate(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        com.kingodogo.buildscape.util.CommonId id = com.kingodogo.buildscape.platform.Services.PLATFORM.getItemId(stack.getItem());
        return id != null && "buildscape".equals(id.getNamespace()) && "big_ornament_template".equals(id.getPath());
    }

    @Inject(method = "onTake", at = @At("RETURN"))
    private void buildscape$preserveTemplateReturn(Player player, ItemStack stack, CallbackInfo ci) {
        if (!buildscape$savedTemplate.isEmpty()) {
            ((ItemCombinerMenuAccessor) this).buildscape$getInputSlots().setItem(1, buildscape$savedTemplate);
            ((SmithingMenu) (Object) this).broadcastChanges();
            buildscape$savedTemplate = ItemStack.EMPTY;
        }
    }
}
