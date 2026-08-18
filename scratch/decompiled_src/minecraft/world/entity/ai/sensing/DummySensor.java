package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public class DummySensor extends Sensor {
   protected void doTick(final ServerLevel level, final LivingEntity body) {
   }

   public Set requires() {
      return ImmutableSet.of();
   }
}
