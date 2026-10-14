package com.kingodogo.buildscape.mixin.fabric;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({Creeper.class, LargeFireball.class})
public abstract class ExplosionGriefingMixin {
    @org.spongepowered.asm.mixin.Dynamic
    @Redirect(method = {"explodeCreeper", "onHit"}, at = @At(value = "INVOKE", target =
            "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"), require = 0)
    private boolean buildscape$griefing(GameRules rules, GameRules.Key<GameRules.BooleanValue> key) {
        Entity entity = (Entity)(Object)this;
        // The shared check recognises fireballs by class name, which is an intermediary name at runtime on Fabric.
        if (key == GameRules.RULE_MOBGRIEFING && entity instanceof LargeFireball
                && com.kingodogo.buildscape.world.ModGameRules.isGhastGriefingDisabled()) return false;
        if (key == GameRules.RULE_MOBGRIEFING && com.kingodogo.buildscape.event.ModCommonEvents.isMobGriefingDisabled(entity, entity.level)) return false;
        return rules.getBoolean(key);
    }
}
