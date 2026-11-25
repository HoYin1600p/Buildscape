package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.item.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.Container;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmithingMenu.class)
public abstract class SmithingMenuMixin {

    @Unique
    private ItemStack buildscape$savedTemplate = ItemStack.EMPTY;
    @Unique
    private int buildscape$templateSlot = -1;

    @Inject(method = "onTake", at = @At("HEAD"))
    private void buildscape$preserveTemplateHead(Player player, ItemStack stack, CallbackInfo ci) {
        buildscape$savedTemplate = ItemStack.EMPTY;
        buildscape$templateSlot = -1;
        Container inputSlots = ((ItemCombinerMenuAccessor) this).buildscape$getInputSlots();
        for (int i = 0; i < inputSlots.getContainerSize(); i++) {
            ItemStack inSlot = inputSlots.getItem(i);
            if (isOrnamentTemplate(inSlot)) {
                buildscape$savedTemplate = inSlot.copy();
                buildscape$templateSlot = i;
                break;
            }
        }
    }

    @Unique
    private static boolean isOrnamentTemplate(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        com.kingodogo.buildscape.util.CommonId id = com.kingodogo.buildscape.platform.Services.PLATFORM.getItemId(stack.getItem());
        return id != null && "buildscape".equals(id.getNamespace()) && "big_ornament_template".equals(id.getPath());
    }

    @Inject(method = "onTake", at = @At("RETURN"))
    private void buildscape$preserveTemplateReturn(Player player, ItemStack stack, CallbackInfo ci) {
        if (!buildscape$savedTemplate.isEmpty() && buildscape$templateSlot >= 0) {
            ((ItemCombinerMenuAccessor) this).buildscape$getInputSlots()
                    .setItem(buildscape$templateSlot, buildscape$savedTemplate);
            ((net.minecraft.world.inventory.SmithingMenu) (Object) this).broadcastChanges();
            buildscape$savedTemplate = ItemStack.EMPTY;
            buildscape$templateSlot = -1;
        }
    }
}
