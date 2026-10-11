package com.kingodogo.buildscape.mixin;

import net.minecraft.client.particle.FireworkParticles;
import net.minecraft.nbt.ListTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FireworkParticles.Starter.class)
public interface FireworkStarterV118xAccessor {
    @Accessor("explosions") ListTag buildscape$getExplosions();
    @Accessor("explosions") void buildscape$setExplosions(ListTag explosions);
    @Invoker("createParticle")
    void buildscape$createParticle(double x, double y, double z, double vx, double vy, double vz,
                                 int[] colors, int[] fadeColors, boolean trail, boolean flicker);
}
