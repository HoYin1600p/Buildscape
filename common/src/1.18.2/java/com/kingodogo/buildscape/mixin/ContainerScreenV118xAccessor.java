package com.kingodogo.buildscape.mixin;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface ContainerScreenV118xAccessor {
    @Accessor("imageWidth") int buildscape$getImageWidth();
}
