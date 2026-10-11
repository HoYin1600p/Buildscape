package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixinsupport.HazeBushBonemeal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoneMealItem.class)
public abstract class BoneMealItemMixin {
    @Dynamic
    @Inject(method = "growCrop", at = @At("HEAD"), cancellable = true, require = 0)
    private static void buildscape$growHazeBush(ItemStack stack, Level level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (HazeBushBonemeal.grow(stack, level, pos)) cir.setReturnValue(true);
    }
}
