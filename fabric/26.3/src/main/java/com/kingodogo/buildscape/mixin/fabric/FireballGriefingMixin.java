package com.kingodogo.buildscape.mixin.fabric;

import com.kingodogo.buildscape.event.ModCommonEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.hurtingprojectile.LargeFireball;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LargeFireball.class)
public abstract class FireballGriefingMixin {
    @Redirect(method = "onHit", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/world/level/gamerules/GameRules;get(Lnet/minecraft/world/level/gamerules/GameRule;)Ljava/lang/Object;"))
    private Object buildscape$griefing(GameRules rules, GameRule<?> rule) {
        Entity entity = (Entity) (Object) this;
        if (rule == GameRules.MOB_GRIEFING && ModCommonEvents.isMobGriefingDisabled(entity, entity.level())) return false;
        return rules.get(rule);
    }
}
