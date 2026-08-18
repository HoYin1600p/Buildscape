package net.minecraft.world.entity.ai.behavior.declarative;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public interface Trigger {
   boolean trigger(final ServerLevel level, final LivingEntity body, final long timestamp);
}
