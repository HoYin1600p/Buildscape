package com.kingodogo.buildscape.mixin;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockEntityRenderers.class)
public interface BlockEntityRenderersV118xInvoker {
    @Invoker("register")
    static <T extends BlockEntity> void buildscape$register(BlockEntityType<T> type, BlockEntityRendererProvider<T> provider) {
        throw new AssertionError("Mixin invoker was not applied");
    }
}
