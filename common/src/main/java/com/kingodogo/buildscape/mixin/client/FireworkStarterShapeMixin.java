package com.kingodogo.buildscape.mixin.client;

import com.kingodogo.buildscape.mixinsupport.FireworkContext;
import com.kingodogo.buildscape.mixinsupport.FireworkSparkAccess;
import com.kingodogo.buildscape.mixinsupport.MixinFactory;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.particle.FireworkParticles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireworkParticles.Starter.class)
public abstract class FireworkStarterShapeMixin implements FireworkSparkAccess {
    @Inject(method = "<init>(Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDDLnet/minecraft/client/particle/ParticleEngine;Ljava/util/List;)V", at = @At("RETURN"))
    private void buildscape$rememberRocket(CallbackInfo ci) { FireworkContext.remember(this); }

    @Override
    @Invoker("createParticle")
    public abstract void buildscape$spawnSpark(double x, double y, double z, double vx, double vy, double vz,
            IntList colors, IntList fades, boolean trail, boolean flicker);

    @Invoker("createParticleBall")
    public abstract void buildscape$vanillaBall(double speed, int size, IntList colors, IntList fades, boolean trail, boolean flicker);

    @Redirect(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/FireworkParticles$Starter;createParticleBall(DILit/unimi/dsi/fastutil/ints/IntList;Lit/unimi/dsi/fastutil/ints/IntList;ZZ)V"))
    private void buildscape$customBurst(FireworkParticles.Starter starter, double speed, int size,
            IntList colors, IntList fades, boolean trail, boolean flicker) {
        if (!MixinFactory.get().renderCustomFireworkExplosion(starter, colors, fades, trail, flicker))
            buildscape$vanillaBall(speed, size, colors, fades, trail, flicker);
    }
}
