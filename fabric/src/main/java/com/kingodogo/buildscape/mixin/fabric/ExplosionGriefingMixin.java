package com.kingodogo.buildscape.mixin.fabric;

import com.kingodogo.buildscape.event.ModCommonEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ExplosionParticleInfo;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerLevel.class)
public abstract class ExplosionGriefingMixin {
    @Redirect(method = "explode", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/world/level/gamerules/GameRules;get(Lnet/minecraft/world/level/gamerules/GameRule;)Ljava/lang/Object;"))
    private Object buildscape$griefing(GameRules rules, GameRule<?> rule, Entity source,
            DamageSource damage, ExplosionDamageCalculator calculator, double x, double y, double z,
            float radius, boolean fire, Level.ExplosionInteraction interaction,
            ParticleOptions smallParticles, ParticleOptions largeParticles,
            WeightedList<ExplosionParticleInfo> blockParticles, Holder<SoundEvent> sound) {
        if (rule == GameRules.MOB_GRIEFING && ModCommonEvents.isMobGriefingDisabled(source, (ServerLevel) (Object) this)) return false;
        return rules.get(rule);
    }
}
