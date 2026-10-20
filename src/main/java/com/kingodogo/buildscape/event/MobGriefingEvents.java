package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.world.ModGameRules;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MobGriefingEvents {

    @SubscribeEvent
    public void onMobGriefing(EntityMobGriefingEvent event) {
        Entity entity = event.getEntity();
        if (entity != null && entity.level != null) {
            if (entity instanceof EnderMan) {
                if (entity.level.getGameRules().getBoolean(ModGameRules.DISABLE_ENDERMAN_GRIEFING)) {
                    event.setResult(net.minecraftforge.eventbus.api.Event.Result.DENY);
                }
            } else if (entity instanceof Creeper) {
                if (entity.level.getGameRules().getBoolean(ModGameRules.DISABLE_CREEPER_GRIEFING)) {
                    event.setResult(net.minecraftforge.eventbus.api.Event.Result.DENY);
                }
            } else if (entity instanceof Ghast ||
                    entity instanceof LargeFireball) {
                if (entity.level.getGameRules().getBoolean(ModGameRules.DISABLE_GHAST_GRIEFING)) {
                    event.setResult(net.minecraftforge.eventbus.api.Event.Result.DENY);
                }
            }
        }
    }
}
