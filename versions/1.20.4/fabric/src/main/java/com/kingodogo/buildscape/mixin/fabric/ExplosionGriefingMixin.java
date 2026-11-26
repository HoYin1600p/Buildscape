package com.kingodogo.buildscape.mixin.fabric;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Level.class)
public abstract class ExplosionGriefingMixin {
    // The particle overload delegates here; only this overload checks mob griefing.
    @Redirect(method = "explode(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;DDDFZLnet/minecraft/world/level/Level$ExplosionInteraction;ZLnet/minecraft/core/particles/ParticleOptions;Lnet/minecraft/core/particles/ParticleOptions;Lnet/minecraft/core/Holder;)Lnet/minecraft/world/level/Explosion;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"))
    private boolean buildscape$griefing(GameRules rules, GameRules.Key<GameRules.BooleanValue> key,
            Entity source, net.minecraft.world.damagesource.DamageSource damage,
            net.minecraft.world.level.ExplosionDamageCalculator calculator, double x, double y, double z,
            float radius, boolean fire, Level.ExplosionInteraction interaction, boolean spawnParticles,
            net.minecraft.core.particles.ParticleOptions small, net.minecraft.core.particles.ParticleOptions large,
            net.minecraft.core.Holder<net.minecraft.sounds.SoundEvent> sound) {
        if (key == GameRules.RULE_MOBGRIEFING && source != null
                && com.kingodogo.buildscape.event.ModCommonEvents.isMobGriefingDisabled(source, (Level) (Object) this)) return false;
        return rules.getBoolean(key);
    }
}
