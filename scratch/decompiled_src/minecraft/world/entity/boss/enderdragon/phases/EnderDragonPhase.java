package net.minecraft.world.entity.boss.enderdragon.phases;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;

public class EnderDragonPhase {
   private static EnderDragonPhase[] phases = new EnderDragonPhase[0];
   public static final EnderDragonPhase HOLDING_PATTERN = create(DragonHoldingPatternPhase.class, "HoldingPattern");
   public static final EnderDragonPhase STRAFE_PLAYER = create(DragonStrafePlayerPhase.class, "StrafePlayer");
   public static final EnderDragonPhase LANDING_APPROACH = create(DragonLandingApproachPhase.class, "LandingApproach");
   public static final EnderDragonPhase LANDING = create(DragonLandingPhase.class, "Landing");
   public static final EnderDragonPhase TAKEOFF = create(DragonTakeoffPhase.class, "Takeoff");
   public static final EnderDragonPhase SITTING_FLAMING = create(DragonSittingFlamingPhase.class, "SittingFlaming");
   public static final EnderDragonPhase SITTING_SCANNING = create(DragonSittingScanningPhase.class, "SittingScanning");
   public static final EnderDragonPhase SITTING_ATTACKING = create(DragonSittingAttackingPhase.class, "SittingAttacking");
   public static final EnderDragonPhase CHARGING_PLAYER = create(DragonChargePlayerPhase.class, "ChargingPlayer");
   public static final EnderDragonPhase DYING = create(DragonDeathPhase.class, "Dying");
   public static final EnderDragonPhase HOVERING = create(DragonHoverPhase.class, "Hover");
   private final Class instanceClass;
   private final int id;
   private final String name;

   private EnderDragonPhase(final int id, final Class instanceClass, final String name) {
      this.id = id;
      this.instanceClass = instanceClass;
      this.name = name;
   }

   public DragonPhaseInstance createInstance(final EnderDragon dragon) {
      try {
         Constructor constructor = this.getConstructor();
         return (DragonPhaseInstance)constructor.newInstance(dragon);
      } catch (Exception var3) {
         throw new Error(var3);
      }
   }

   protected Constructor getConstructor() throws NoSuchMethodException {
      return this.instanceClass.getConstructor(EnderDragon.class);
   }

   public int getId() {
      return this.id;
   }

   public String toString() {
      return this.name + " (#" + this.id + ")";
   }

   public static EnderDragonPhase getById(final int id) {
      return id >= 0 && id < phases.length ? phases[id] : HOLDING_PATTERN;
   }

   public static int getCount() {
      return phases.length;
   }

   private static EnderDragonPhase create(final Class instanceClass, final String name) {
      EnderDragonPhase phase = new EnderDragonPhase(phases.length, instanceClass, name);
      phases = (EnderDragonPhase[])Arrays.copyOf(phases, phases.length + 1);
      phases[phase.getId()] = phase;
      return phase;
   }
}
