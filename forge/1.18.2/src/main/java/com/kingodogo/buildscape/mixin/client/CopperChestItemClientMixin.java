package com.kingodogo.buildscape.mixin;

import net.minecraft.world.item.Item;
import net.minecraftforge.client.IItemRenderProperties;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

/** Forge's added method is called during item construction only on the physical client. */
@Mixin(Item.class)
public abstract class CopperChestItemClientMixin {
    @Dynamic
    @Inject(method = "initializeClient", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void buildscape$copperChestRenderer(Consumer<IItemRenderProperties> consumer, CallbackInfo callback) {
        if (com.kingodogo.buildscape.client.ForgeItemRenderProperties.initialize(this, consumer)) {
            callback.cancel();
        }
    }
}
