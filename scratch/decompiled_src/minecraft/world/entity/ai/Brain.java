package net.minecraft.world.entity.ai;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.memory.ExpirableValue;
import net.minecraft.world.entity.ai.memory.MemoryMap;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemorySlot;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class Brain {
   private static final int SCHEDULE_UPDATE_DELAY = 20;
   private final Map memories = Maps.newHashMap();
   private final Map sensors = Maps.newLinkedHashMap();
   private final Map availableBehaviorsByPriority = Maps.newTreeMap();
   private @Nullable EnvironmentAttribute schedule;
   private final Map activityRequirements = Maps.newHashMap();
   private final Map activityMemoriesToEraseWhenStopped = Maps.newHashMap();
   private Set coreActivities = Sets.newHashSet();
   private final Set activeActivities = Sets.newHashSet();
   private Activity defaultActivity = Activity.IDLE;
   private long lastScheduleUpdate = -9999L;

   public static Brain.Provider provider(final Collection sensorTypes) {
      return new Brain.Provider(ImmutableList.of(), sensorTypes, (var0) -> List.of());
   }

   public static Brain.Provider provider(final Collection sensorTypes, final Brain.ActivitySupplier activities) {
      return new Brain.Provider(ImmutableList.of(), sensorTypes, activities);
   }

   /** @deprecated */
   @Deprecated
   public static Brain.Provider provider(final Collection memoryTypes, final Collection sensorTypes, final Brain.ActivitySupplier activities) {
      return new Brain.Provider(memoryTypes, sensorTypes, activities);
   }

   @VisibleForTesting
   protected Brain(final Collection memoryTypes, final Collection sensorTypes, final List activities, final MemoryMap memories, final RandomSource randomSource) {
      for(MemoryModuleType memoryType : memoryTypes) {
         this.registerMemory(memoryType);
      }

      for(SensorType sensorType : sensorTypes) {
         Sensor newSensor = sensorType.create();
         newSensor.randomlyDelayStart(randomSource);
         this.sensors.put(sensorType, newSensor);

         for(MemoryModuleType type : newSensor.requires()) {
            this.registerMemory(type);
         }
      }

      for(ActivityData activity : activities) {
         this.addActivity(activity.activityType(), activity.behaviorPriorityPairs(), activity.conditions(), activity.memoriesToEraseWhenStopped());
      }

      for(MemoryMap.Value memory : memories) {
         this.setMemoryInternal(memory);
      }

      this.setCoreActivities(ImmutableSet.of(Activity.CORE));
      this.useDefaultActivity();
   }

   private void registerMemory(final MemoryModuleType memoryType) {
      this.memories.putIfAbsent(memoryType, MemorySlot.create());
   }

   public Brain() {
      this.setCoreActivities(ImmutableSet.of(Activity.CORE));
      this.useDefaultActivity();
   }

   public Brain.Packed pack() {
      final MemoryMap.Builder builder = new MemoryMap.Builder();
      this.forEach(new Brain.Visitor(this) {
         {
            Objects.requireNonNull(this$0);
         }

         public void acceptEmpty(final MemoryModuleType type) {
         }

         public void accept(final MemoryModuleType type, final Object value, final long timeToLive) {
            if (type.canSerialize()) {
               builder.add(type, ExpirableValue.of(value, timeToLive));
            }

         }

         public void accept(final MemoryModuleType type, final Object value) {
            if (type.canSerialize()) {
               builder.add(type, ExpirableValue.of(value));
            }

         }
      });
      return new Brain.Packed(builder.build());
   }

   private @Nullable MemorySlot getMemorySlotIfPresent(final MemoryModuleType memoryType) {
      return (MemorySlot)this.memories.get(memoryType);
   }

   private MemorySlot getMemorySlot(final MemoryModuleType memoryType) {
      MemorySlot result = this.getMemorySlotIfPresent(memoryType);
      if (result == null) {
         throw new IllegalStateException("Unregistered memory fetched: " + String.valueOf(memoryType));
      } else {
         return result;
      }
   }

   public boolean hasMemoryValue(final MemoryModuleType type) {
      return this.checkMemory(type, MemoryStatus.VALUE_PRESENT);
   }

   public void clearMemories() {
      this.memories.values().forEach(MemorySlot::clear);
   }

   public void eraseMemory(final MemoryModuleType type) {
      MemorySlot slot = this.getMemorySlotIfPresent(type);
      if (slot != null) {
         slot.clear();
      }

   }

   public void setMemory(final MemoryModuleType type, final @Nullable Object value) {
      this.setMemoryInternal(type, value);
   }

   public void setMemoryWithExpiry(final MemoryModuleType type, final Object value, final long timeToLive) {
      this.setMemoryInternal(type, value, timeToLive);
   }

   public void setMemory(final MemoryModuleType type, final Optional optionalValue) {
      this.setMemoryInternal(type, optionalValue.orElse((Object)null));
   }

   private void setMemoryInternal(final MemoryMap.Value value) {
      ExpirableValue expirableValue = value.value();
      if (expirableValue.timeToLive().isPresent()) {
         this.setMemoryInternal(value.type(), expirableValue.value(), expirableValue.timeToLive().get());
      } else {
         this.setMemoryInternal(value.type(), expirableValue.value());
      }

   }

   private void setMemoryInternal(final MemoryModuleType type, Object value, final long tileToLive) {
      MemorySlot slot = this.getMemorySlotIfPresent(type);
      if (slot != null) {
         if (isEmptyCollection(value)) {
            value = (U)null;
         }

         if (value == null) {
            slot.clear();
         } else {
            slot.set(value, tileToLive);
         }
      }

   }

   private void setMemoryInternal(final MemoryModuleType type, @Nullable Object value) {
      MemorySlot slot = this.getMemorySlotIfPresent(type);
      if (slot != null) {
         if (value != null && isEmptyCollection(value)) {
            value = (U)null;
         }

         if (value == null) {
            slot.clear();
         } else {
            slot.set(value);
         }
      }

   }

   public Optional getMemory(final MemoryModuleType type) {
      return Optional.ofNullable(this.getMemorySlot(type).value());
   }

   public @Nullable Optional getMemoryInternal(final MemoryModuleType type) {
      MemorySlot slot = this.getMemorySlotIfPresent(type);
      return slot == null ? null : Optional.ofNullable(slot.value());
   }

   public long getTimeUntilExpiry(final MemoryModuleType type) {
      return this.getMemorySlot(type).timeToLive();
   }

   public void forEach(final Brain.Visitor visitor) {
      this.memories.forEach((memoryModuleType, slot) -> callVisitor(visitor, memoryModuleType, slot));
   }

   private static void callVisitor(final Brain.Visitor visitor, final MemoryModuleType memoryModuleType, final MemorySlot slot) {
      slot.visit(memoryModuleType, visitor);
   }

   public boolean isMemoryValue(final MemoryModuleType memoryType, final Object value) {
      MemorySlot slot = this.getMemorySlotIfPresent(memoryType);
      return slot != null && Objects.equals(value, slot.value());
   }

   public boolean checkMemory(final MemoryModuleType type, final MemoryStatus status) {
      MemorySlot slot = this.getMemorySlotIfPresent(type);
      if (slot == null) {
         return false;
      } else {
         return status == MemoryStatus.REGISTERED || status == MemoryStatus.VALUE_PRESENT && slot.hasValue() || status == MemoryStatus.VALUE_ABSENT && !slot.hasValue();
      }
   }

   public void setSchedule(final EnvironmentAttribute schedule) {
      this.schedule = schedule;
   }

   public void setCoreActivities(final Set activities) {
      this.coreActivities = activities;
   }

   /** @deprecated */
   @Deprecated
   @VisibleForDebug
   public Set getActiveActivities() {
      return this.activeActivities;
   }

   /** @deprecated */
   @Deprecated
   @VisibleForDebug
   public List getRunningBehaviors() {
      List runningBehaviours = new ObjectArrayList();

      for(Map behavioursByActivities : this.availableBehaviorsByPriority.values()) {
         for(Set behaviors : behavioursByActivities.values()) {
            for(BehaviorControl behavior : behaviors) {
               if (behavior.getStatus() == Behavior.Status.RUNNING) {
                  runningBehaviours.add(behavior);
               }
            }
         }
      }

      return runningBehaviours;
   }

   public void useDefaultActivity() {
      this.setActiveActivity(this.defaultActivity);
   }

   public Optional getActiveNonCoreActivity() {
      for(Activity activity : this.activeActivities) {
         if (!this.coreActivities.contains(activity)) {
            return Optional.of(activity);
         }
      }

      return Optional.empty();
   }

   public void setActiveActivityIfPossible(final Activity activity) {
      if (this.activityRequirementsAreMet(activity)) {
         this.setActiveActivity(activity);
      } else {
         this.useDefaultActivity();
      }

   }

   private void setActiveActivity(final Activity activity) {
      if (!this.isActive(activity)) {
         this.eraseMemoriesForOtherActivitesThan(activity);
         this.activeActivities.clear();
         this.activeActivities.addAll(this.coreActivities);
         this.activeActivities.add(activity);
      }
   }

   private void eraseMemoriesForOtherActivitesThan(final Activity activity) {
      for(Activity oldActivity : this.activeActivities) {
         if (oldActivity != activity) {
            Set memoryModuleTypes = (Set)this.activityMemoriesToEraseWhenStopped.get(oldActivity);
            if (memoryModuleTypes != null) {
               for(MemoryModuleType memoryModuleType : memoryModuleTypes) {
                  this.eraseMemory(memoryModuleType);
               }
            }
         }
      }

   }

   public void updateActivityFromSchedule(final EnvironmentAttributeSystem environmentAttributes, final long gameTime, final Vec3 pos) {
      if (gameTime - this.lastScheduleUpdate > 20L) {
         this.lastScheduleUpdate = gameTime;
         Activity scheduledActivity = this.schedule != null ? (Activity)environmentAttributes.getValue(this.schedule, pos) : Activity.IDLE;
         if (!this.activeActivities.contains(scheduledActivity)) {
            this.setActiveActivityIfPossible(scheduledActivity);
         }
      }

   }

   public void setActiveActivityToFirstValid(final List activities) {
      for(Activity activity : activities) {
         if (this.activityRequirementsAreMet(activity)) {
            this.setActiveActivity(activity);
            break;
         }
      }

   }

   public void setDefaultActivity(final Activity activity) {
      this.defaultActivity = activity;
   }

   public void addActivity(final Activity activity, final ImmutableList behaviorPriorityPairs, final Set conditions, final Set memoriesToEraseWhenStopped) {
      this.activityRequirements.put(activity, conditions);
      if (!memoriesToEraseWhenStopped.isEmpty()) {
         this.activityMemoriesToEraseWhenStopped.put(activity, memoriesToEraseWhenStopped);
      }

      UnmodifiableIterator var5 = behaviorPriorityPairs.iterator();

      while(var5.hasNext()) {
         Pair pair = (Pair)var5.next();
         BehaviorControl behavior = (BehaviorControl)pair.getSecond();

         for(MemoryModuleType requiredMemory : behavior.getRequiredMemories()) {
            this.registerMemory(requiredMemory);
         }

         ((Set)((Map)this.availableBehaviorsByPriority.computeIfAbsent((Integer)pair.getFirst(), (key) -> Maps.newHashMap())).computeIfAbsent(activity, (key) -> Sets.newLinkedHashSet())).add(behavior);
      }

   }

   @VisibleForTesting
   public void removeAllBehaviors() {
      this.availableBehaviorsByPriority.clear();
   }

   public boolean isActive(final Activity activity) {
      return this.activeActivities.contains(activity);
   }

   public void tick(final ServerLevel level, final LivingEntity body) {
      this.forgetOutdatedMemories();
      this.tickSensors(level, body);
      this.startEachNonRunningBehavior(level, body);
      this.tickEachRunningBehavior(level, body);
   }

   private void tickSensors(final ServerLevel level, final LivingEntity body) {
      for(Sensor sensor : this.sensors.values()) {
         sensor.tick(level, body);
      }

   }

   private void forgetOutdatedMemories() {
      this.memories.values().forEach(MemorySlot::tick);
   }

   public void stopAll(final ServerLevel level, final LivingEntity body) {
      long timestamp = body.level().getGameTime();

      for(BehaviorControl behavior : this.getRunningBehaviors()) {
         behavior.doStop(level, body, timestamp);
      }

   }

   private void startEachNonRunningBehavior(final ServerLevel level, final LivingEntity body) {
      long time = level.getGameTime();

      for(Map behavioursByActivities : this.availableBehaviorsByPriority.values()) {
         for(Map.Entry behavioursForActivity : behavioursByActivities.entrySet()) {
            Activity activity = (Activity)behavioursForActivity.getKey();
            if (this.activeActivities.contains(activity)) {
               for(BehaviorControl behavior : (Set)behavioursForActivity.getValue()) {
                  if (behavior.getStatus() == Behavior.Status.STOPPED) {
                     behavior.tryStart(level, body, time);
                  }
               }
            }
         }
      }

   }

   private void tickEachRunningBehavior(final ServerLevel level, final LivingEntity body) {
      long timestamp = level.getGameTime();

      for(BehaviorControl behavior : this.getRunningBehaviors()) {
         behavior.tickOrStop(level, body, timestamp);
      }

   }

   private boolean activityRequirementsAreMet(final Activity activity) {
      if (!this.activityRequirements.containsKey(activity)) {
         return false;
      } else {
         for(Pair memoryRequirement : (Set)this.activityRequirements.get(activity)) {
            MemoryModuleType memoryType = (MemoryModuleType)memoryRequirement.getFirst();
            MemoryStatus memoryStatus = (MemoryStatus)memoryRequirement.getSecond();
            if (!this.checkMemory(memoryType, memoryStatus)) {
               return false;
            }
         }

         return true;
      }
   }

   private static boolean isEmptyCollection(final Object object) {
      if (object instanceof Collection collection) {
         if (collection.isEmpty()) {
            return true;
         }
      }

      return false;
   }

   public boolean isBrainDead() {
      return this.memories.isEmpty() && this.sensors.isEmpty() && this.availableBehaviorsByPriority.isEmpty();
   }

   @FunctionalInterface
   public interface ActivitySupplier {
      List createActivities(LivingEntity body);
   }

   public static record Packed(MemoryMap memories) {
      public static final Brain.Packed EMPTY = new Brain.Packed(MemoryMap.EMPTY);
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(MemoryMap.CODEC.fieldOf("memories").forGetter(Brain.Packed::memories)).apply(i, Brain.Packed::new));
   }

   public static final class Provider {
      private final Collection memoryTypes;
      private final Collection sensorTypes;
      private final Brain.ActivitySupplier activities;

      private Provider(final Collection memoryTypes, final Collection sensorTypes, final Brain.ActivitySupplier activities) {
         this.memoryTypes = memoryTypes;
         this.sensorTypes = sensorTypes;
         this.activities = activities;
      }

      public Brain makeBrain(final LivingEntity body, final Brain.Packed packed) {
         List activities = this.activities.createActivities(body);
         return new Brain(this.memoryTypes, this.sensorTypes, activities, packed.memories, body.getRandom());
      }
   }

   public interface Visitor {
      void acceptEmpty(MemoryModuleType type);

      void accept(MemoryModuleType type, Object value);

      void accept(MemoryModuleType type, Object value, long timeToLive);
   }
}
