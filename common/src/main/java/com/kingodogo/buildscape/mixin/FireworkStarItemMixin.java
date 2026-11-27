package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixinsupport.MixinFactory;

import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;

@Mixin(targets = "net.minecraft.world.item.component.FireworkExplosion")
public abstract class FireworkStarItemMixin {

    @org.spongepowered.asm.mixin.Dynamic
    @Inject(method = "addToTooltip(Lnet/minecraft/world/item/Item$TooltipContext;Ljava/util/function/Consumer;Lnet/minecraft/world/item/TooltipFlag;Lnet/minecraft/core/component/DataComponentGetter;)V",
            at = @At("HEAD"), cancellable = true, require = 0)
    private void buildscape$customComponentTooltip(Item.TooltipContext context, Consumer<Component> tooltip,
            TooltipFlag flags, DataComponentGetter components, CallbackInfo ci) {
        var data = components.get(DataComponents.CUSTOM_DATA);
        if (data == null) return;
        var explosion = data.copyTag().getCompound("Explosion");
        if (explosion.isEmpty()) return;
        List<Component> lines = new ArrayList<>();
        if (MixinFactory.appendFireworkCustomHoverText(explosion.get(), lines)) {
            lines.forEach(tooltip);
            ci.cancel();
        }
    }

    @Inject(method = "appendHoverText(Lnet/minecraft/nbt/CompoundTag;Ljava/util/List;)V", at = @At("HEAD"), cancellable = true, require = 0)
    private static void buildscape$appendCustomShapeHoverText(@Coerce Object tag, List<Component> tooltip, CallbackInfo ci) {
        if (MixinFactory.appendFireworkCustomHoverText(tag, tooltip)) {
            ci.cancel();
        }
    }
}
