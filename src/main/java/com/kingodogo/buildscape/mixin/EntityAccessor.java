package com.kingodogo.buildscape.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Accessor("level")
    Level getLevel();

    @Accessor("onGround")
    boolean isOnGround();

    @Accessor("onGround")
    void setOnGround(boolean onGround);

    @Invoker("blockPosition")
    BlockPos callBlockPosition();
}
