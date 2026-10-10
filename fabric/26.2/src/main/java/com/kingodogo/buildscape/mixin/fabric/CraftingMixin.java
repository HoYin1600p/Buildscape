package com.kingodogo.buildscape.mixin.fabric;

import com.kingodogo.buildscape.event.ModCommonEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ResultSlot.class)
public abstract class CraftingMixin {
    @Shadow @Final private CraftingContainer craftSlots;
    @Shadow @Final private Player player;
    @Shadow private int removeCount;

    // Vanilla also calls this after quick crafting; removeCount is reset before onTake.
    @Inject(method = "checkTakeAchievements", at = @At("HEAD"))
    private void buildscape$crafted(ItemStack stack, CallbackInfo ci) {
        if (removeCount > 0 && !player.level().isClientSide()) {
            ModCommonEvents.onItemCrafted(player, stack, craftSlots, removeCount);
        }
    }
}
