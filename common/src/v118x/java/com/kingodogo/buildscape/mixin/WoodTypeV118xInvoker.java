package com.kingodogo.buildscape.mixin;

import net.minecraft.world.level.block.state.properties.WoodType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WoodType.class)
public interface WoodTypeV118xInvoker {
    @Invoker("register")
    static WoodType buildscape$register(WoodType type) {
        throw new AssertionError("Mixin invoker was not applied");
    }
}
