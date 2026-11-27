package com.kingodogo.buildscape.mixin.fabric;

import com.kingodogo.buildscape.event.ModCommonEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class ItemUseFinishMixin {
    @Redirect(method = "completeUsingItem", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/world/item/ItemStack;finishUsingItem(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack buildscape$finished(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack consumed = stack.copy();
        ItemStack result = stack.finishUsingItem(level, entity);
        if (!level.isClientSide()) ModCommonEvents.onItemUseFinish(entity, consumed);
        return result;
    }
}
