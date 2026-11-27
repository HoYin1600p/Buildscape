package com.kingodogo.buildscape.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Reads a mob's death experience at death time, before any mod handles the drop. */
@Mixin(LivingEntity.class)
public interface LivingEntityExperienceAccessor {
    @Nullable
    @Accessor("lastHurtByPlayer")
    Player buildscape$getLastHurtByPlayer();

    @Invoker("getExperienceReward")
    int buildscape$callGetExperienceReward(@Nullable Player player);

    @Invoker("shouldDropExperience")
    boolean buildscape$callShouldDropExperience();
}
