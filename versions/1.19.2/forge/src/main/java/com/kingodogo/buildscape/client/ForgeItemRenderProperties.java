package com.kingodogo.buildscape.client;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import java.util.function.Consumer;

/** Construct extensions outside the mixin so anonymous classes never capture a mixin instance. */
public final class ForgeItemRenderProperties {
    private ForgeItemRenderProperties() {}

    public static boolean initialize(Object item, Consumer<IClientItemExtensions> consumer) {
        if (item instanceof com.kingodogo.buildscape.item.CopperChestItem) {
            consumer.accept(new IClientItemExtensions() {
                @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    return com.kingodogo.buildscape.client.renderer.CopperChestItemRenderer.INSTANCE;
                }
            });
            return true;
        }
        if (item instanceof com.kingodogo.buildscape.item.GlassJarItem) {
            consumer.accept(new IClientItemExtensions() {
                @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    return com.kingodogo.buildscape.client.renderer.GlassJarItemRenderer.INSTANCE;
                }
            });
            return true;
        }
        return false;
    }
}
