package net.minecraft.world.entity.ai.sensing;

import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.animal.frog.FrogAi;
import net.minecraft.world.entity.animal.nautilus.NautilusAi;

public class SensorType {
   public static final SensorType DUMMY = register("dummy", DummySensor::new);
   public static final SensorType NEAREST_ITEMS = register("nearest_items", NearestItemSensor::new);
   public static final SensorType NEAREST_LIVING_ENTITIES = register("nearest_living_entities", NearestLivingEntitySensor::new);
   public static final SensorType NEAREST_PLAYERS = register("nearest_players", PlayerSensor::new);
   public static final SensorType NEAREST_BED = register("nearest_bed", NearestBedSensor::new);
   public static final SensorType HURT_BY = register("hurt_by", HurtBySensor::new);
   public static final SensorType VILLAGER_HOSTILES = register("villager_hostiles", VillagerHostilesSensor::new);
   public static final SensorType VILLAGER_BABIES = register("villager_babies", VillagerBabiesSensor::new);
   public static final SensorType SECONDARY_POIS = register("secondary_pois", SecondaryPoiSensor::new);
   public static final SensorType GOLEM_DETECTED = register("golem_detected", GolemSensor::new);
   public static final SensorType ARMADILLO_SCARE_DETECTED = register("armadillo_scare_detected", () -> new MobSensor(5, Armadillo::isScaredBy, Armadillo::canStayRolledUp, MemoryModuleType.DANGER_DETECTED_RECENTLY, 80));
   public static final SensorType PIGLIN_SPECIFIC_SENSOR = register("piglin_specific_sensor", PiglinSpecificSensor::new);
   public static final SensorType PIGLIN_BRUTE_SPECIFIC_SENSOR = register("piglin_brute_specific_sensor", PiglinBruteSpecificSensor::new);
   public static final SensorType HOGLIN_SPECIFIC_SENSOR = register("hoglin_specific_sensor", HoglinSpecificSensor::new);
   public static final SensorType NEAREST_ADULT = register("nearest_adult", AdultSensor::new);
   public static final SensorType NEAREST_ADULT_ANY_TYPE = register("nearest_adult_any_type", AdultSensorAnyType::new);
   public static final SensorType AXOLOTL_ATTACKABLES = register("axolotl_attackables", AxolotlAttackablesSensor::new);
   public static final SensorType FOOD_TEMPTATIONS = register("food_temptations", TemptingSensor::forAnimal);
   public static final SensorType FROG_TEMPTATIONS = register("frog_temptations", () -> new TemptingSensor(FrogAi.getTemptations()));
   public static final SensorType NAUTILUS_TEMPTATIONS = register("nautilus_temptations", () -> new TemptingSensor(NautilusAi.getTemptations()));
   public static final SensorType FROG_ATTACKABLES = register("frog_attackables", FrogAttackablesSensor::new);
   public static final SensorType IS_IN_WATER = register("is_in_water", IsInWaterSensor::new);
   public static final SensorType WARDEN_ENTITY_SENSOR = register("warden_entity_sensor", WardenEntitySensor::new);
   public static final SensorType BREEZE_ATTACK_ENTITY_SENSOR = register("breeze_attack_entity_sensor", BreezeAttackEntitySensor::new);
   private final Supplier factory;

   private SensorType(final Supplier factory) {
      this.factory = factory;
   }

   public Sensor create() {
      return (Sensor)this.factory.get();
   }

   private static SensorType register(final String name, final Supplier factory) {
      return (SensorType)Registry.register(BuiltInRegistries.SENSOR_TYPE, Identifier.withDefaultNamespace(name), new SensorType(factory));
   }
}
