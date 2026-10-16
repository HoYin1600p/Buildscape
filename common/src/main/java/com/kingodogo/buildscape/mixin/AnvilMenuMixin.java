package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.item.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.Container;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {

    @Shadow
    @Final
    private DataSlot cost;

    @Unique
    private ItemStack buildscape$savedShard = ItemStack.EMPTY;

    @Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true)
    private void buildscape$mayPickupZeroCost(Player player, boolean hasStack, CallbackInfoReturnable<Boolean> cir) {
        if (hasStack && this.cost.get() == 0) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "onTake", at = @At("HEAD"))
    private void buildscape$preserveShardHead(Player player, ItemStack stack, CallbackInfo ci) {
        ItemStack rightSlot = ((ItemCombinerMenuAccessor) this).buildscape$getInputSlots().getItem(1);
        if (!rightSlot.isEmpty() && isFestiveGlintShard(rightSlot)) {
            this.buildscape$savedShard = rightSlot.copy();
        } else {
            this.buildscape$savedShard = ItemStack.EMPTY;
        }
    }

    @Unique
    private static boolean isFestiveGlintShard(ItemStack stack) {
        com.kingodogo.buildscape.util.CommonId id = com.kingodogo.buildscape.platform.Services.PLATFORM.getItemId(stack.getItem());
        return id != null && "buildscape".equals(id.getNamespace()) && "festive_glint_shard".equals(id.getPath());
    }

    @Inject(method = "onTake", at = @At("RETURN"))
    private void buildscape$preserveShardReturn(Player player, ItemStack stack, CallbackInfo ci) {
        if (!this.buildscape$savedShard.isEmpty()) {
            ((ItemCombinerMenuAccessor) this).buildscape$getInputSlots().setItem(1, this.buildscape$savedShard);
            ((AnvilMenu) (Object) this).broadcastChanges();
            this.buildscape$savedShard = ItemStack.EMPTY;
        }
    }
}
