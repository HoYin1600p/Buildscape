package com.kingodogo.buildscape.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import javax.annotation.Nullable;

@Mixin(LivingEntity.class)
public interface LivingEntityExperienceAccessor {
    @Nullable
    @Accessor("lastHurtByPlayer")
    Player getLastHurtByPlayer();

    @Invoker("getExperienceReward")
    int callGetExperienceReward(@Nullable Player player);

    @Invoker("shouldDropExperience")
    boolean callShouldDropExperience();
}
