package com.kingodogo.buildscape.mixin.client;

import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Particle.class)
public interface ParticleV121xAccessor {
    @Accessor("x") double buildscape$getX();
    @Accessor("y") double buildscape$getY();
    @Accessor("z") double buildscape$getZ();
    @Accessor("xd") double buildscape$getXd();
    @Accessor("zd") double buildscape$getZd();
}
