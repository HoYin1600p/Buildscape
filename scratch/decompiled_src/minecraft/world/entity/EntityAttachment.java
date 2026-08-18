package net.minecraft.world.entity;

import java.util.List;
import net.minecraft.world.phys.Vec3;

public enum EntityAttachment {
   PASSENGER(EntityAttachment.Fallback.AT_HEIGHT),
   VEHICLE(EntityAttachment.Fallback.AT_FEET),
   NAME_TAG(EntityAttachment.Fallback.AT_HEIGHT),
   WARDEN_CHEST(EntityAttachment.Fallback.AT_CENTER);

   private final EntityAttachment.Fallback fallback;

   private EntityAttachment(final EntityAttachment.Fallback fallback) {
      this.fallback = fallback;
   }

   public List createFallbackPoints(final float width, final float height) {
      return this.fallback.create(width, height);
   }

   // $FF: synthetic method
   private static EntityAttachment[] $values() {
      return new EntityAttachment[]{PASSENGER, VEHICLE, NAME_TAG, WARDEN_CHEST};
   }

   public interface Fallback {
      List ZERO = List.of(Vec3.ZERO);
      EntityAttachment.Fallback AT_FEET = (width, height) -> ZERO;
      EntityAttachment.Fallback AT_HEIGHT = (width, height) -> List.of(new Vec3(0.0D, (double)height, 0.0D));
      EntityAttachment.Fallback AT_CENTER = (width, height) -> List.of(new Vec3(0.0D, (double)height / 2.0D, 0.0D));

      List create(float width, float height);
   }
}
