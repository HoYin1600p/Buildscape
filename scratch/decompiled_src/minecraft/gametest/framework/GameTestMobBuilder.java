package net.minecraft.gametest.framework;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

public class GameTestMobBuilder extends GameTestEntityBuilder {
   private boolean freeWill = true;

   public GameTestMobBuilder(final GameTestHelper testHelper, final EntityType entityType, final Vec3 position) {
      super(testHelper, entityType, position);
   }

   public GameTestMobBuilder withNoFreeWill() {
      this.freeWill = false;
      return this;
   }

   public Mob spawn() {
      Mob entity = (E)((Mob)super.spawn());
      if (!this.freeWill) {
         entity.removeFreeWill();
      }

      return entity;
   }
}
