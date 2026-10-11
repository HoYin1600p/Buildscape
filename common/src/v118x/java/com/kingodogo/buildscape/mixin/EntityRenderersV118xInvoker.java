package com.kingodogo.buildscape.mixin;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityRenderers.class)
public interface EntityRenderersV118xInvoker {
    @Invoker("register")
    static <T extends Entity> void buildscape$register(EntityType<T> type, EntityRendererProvider<T> provider) {
        throw new AssertionError("Mixin invoker was not applied");
    }
}
