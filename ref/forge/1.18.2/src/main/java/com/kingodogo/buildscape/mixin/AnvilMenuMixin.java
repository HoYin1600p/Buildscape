package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.item.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {

    @Shadow
    @Final
    private DataSlot cost;

    @Unique
    private ItemStack buildscape$savedShard = ItemStack.EMPTY;

    public AnvilMenuMixin() {
        super(null, 0, null, null);
    }

    @Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true)
    private void buildscape$mayPickupZeroCost(Player player, boolean hasStack, CallbackInfoReturnable<Boolean> cir) {
        if (hasStack && this.cost.get() == 0) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "onTake", at = @At("HEAD"))
    private void buildscape$preserveShardHead(Player player, ItemStack stack, CallbackInfo ci) {
        ItemStack rightSlot = this.inputSlots.getItem(1);
        if (!rightSlot.isEmpty() && rightSlot.is(ModItems.FESTIVE_GLINT_SHARD.get())) {
            this.buildscape$savedShard = rightSlot.copy();
        } else {
            this.buildscape$savedShard = ItemStack.EMPTY;
        }
    }

    @Inject(method = "onTake", at = @At("RETURN"))
    private void buildscape$preserveShardReturn(Player player, ItemStack stack, CallbackInfo ci) {
        if (!this.buildscape$savedShard.isEmpty()) {
            this.inputSlots.setItem(1, this.buildscape$savedShard);
            this.broadcastChanges();
            this.buildscape$savedShard = ItemStack.EMPTY;
        }
    }
}
