package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixinsupport.MixinFactory;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.FireworkStarItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(FireworkStarItem.class)
public abstract class FireworkStarItemV121xMixin {

    @Inject(method = "appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/Item$TooltipContext;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V",
            at = @At("HEAD"), cancellable = true)
    private void buildscape$customShapeTooltip(ItemStack stack, Item.TooltipContext context, List<Component> tooltip,
            TooltipFlag flag, CallbackInfo ci) {
        var data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return;
        var explosion = data.copyTag().getCompound("Explosion");
        if (explosion.isEmpty()) return;
        if (MixinFactory.appendFireworkCustomHoverText(explosion, tooltip)) ci.cancel();
    }
}
