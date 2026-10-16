package com.kingodogo.buildscape.mixin.forge121;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.adapter.v121x.client.BlockEntityItemRenderer;
import com.kingodogo.buildscape.item.GlassJarItem;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Resolve registry ids and render dispatchers only when the client requests them. */
@Mixin(Item.class)
public abstract class ItemClientExtensionsMixin {
    @Unique private IClientItemExtensions buildscape$extensions;

    @Dynamic
    @Inject(method = "getRenderPropertiesInternal", at = @At("HEAD"), cancellable = true,
            remap = false, require = 0)
    private void buildscape$itemRenderer(CallbackInfoReturnable<Object> callback) {
        Item item = (Item) (Object) this;
        var id = BuiltInRegistries.ITEM.getKey(item);
        boolean chest = BuildscapeCommon.MOD_ID.equals(id.getNamespace()) && id.getPath().endsWith("copper_chest");
        if (!chest && !(item instanceof GlassJarItem)) return;
        if (buildscape$extensions == null) {
            buildscape$extensions = new IClientItemExtensions() {
                private BlockEntityWithoutLevelRenderer renderer;
                @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    if (renderer == null) renderer = new BlockEntityItemRenderer(chest);
                    return renderer;
                }
            };
        }
        callback.setReturnValue(buildscape$extensions);
    }
}
