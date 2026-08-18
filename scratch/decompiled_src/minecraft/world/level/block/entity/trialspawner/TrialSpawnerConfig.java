package net.minecraft.world.level.block.entity.trialspawner;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

public record TrialSpawnerConfig(int spawnRange, float totalMobs, float simultaneousMobs, float totalMobsAddedPerPlayer, float simultaneousMobsAddedPerPlayer, int ticksBetweenSpawn, WeightedList spawnPotentialsDefinition, WeightedList lootTablesToEject, ResourceKey itemsToDropWhenOminous) {
   public static final TrialSpawnerConfig DEFAULT = builder().build();
   public static final Codec DIRECT_CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.intRange(1, 128).optionalFieldOf("spawn_range", DEFAULT.spawnRange).forGetter(TrialSpawnerConfig::spawnRange), Codec.floatRange(0.0F, Float.MAX_VALUE).optionalFieldOf("total_mobs", DEFAULT.totalMobs).forGetter(TrialSpawnerConfig::totalMobs), Codec.floatRange(0.0F, Float.MAX_VALUE).optionalFieldOf("simultaneous_mobs", DEFAULT.simultaneousMobs).forGetter(TrialSpawnerConfig::simultaneousMobs), Codec.floatRange(0.0F, Float.MAX_VALUE).optionalFieldOf("total_mobs_added_per_player", DEFAULT.totalMobsAddedPerPlayer).forGetter(TrialSpawnerConfig::totalMobsAddedPerPlayer), Codec.floatRange(0.0F, Float.MAX_VALUE).optionalFieldOf("simultaneous_mobs_added_per_player", DEFAULT.simultaneousMobsAddedPerPlayer).forGetter(TrialSpawnerConfig::simultaneousMobsAddedPerPlayer), Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("ticks_between_spawn", DEFAULT.ticksBetweenSpawn).forGetter(TrialSpawnerConfig::ticksBetweenSpawn), SpawnData.LIST_CODEC.optionalFieldOf("spawn_potentials", WeightedList.of()).forGetter(TrialSpawnerConfig::spawnPotentialsDefinition), WeightedList.codec(LootTable.KEY_CODEC).optionalFieldOf("loot_tables_to_eject", DEFAULT.lootTablesToEject).forGetter(TrialSpawnerConfig::lootTablesToEject), LootTable.KEY_CODEC.optionalFieldOf("items_to_drop_when_ominous", DEFAULT.itemsToDropWhenOminous).forGetter(TrialSpawnerConfig::itemsToDropWhenOminous)).apply(i, TrialSpawnerConfig::new));
   public static final Codec CODEC = RegistryCodecs.holder(Registries.TRIAL_SPAWNER_CONFIG, DIRECT_CODEC);

   public int calculateTargetTotalMobs(final int additionalPlayers) {
      return (int)Math.floor((double)(this.totalMobs + this.totalMobsAddedPerPlayer * (float)additionalPlayers));
   }

   public int calculateTargetSimultaneousMobs(final int additionalPlayers) {
      return (int)Math.floor((double)(this.simultaneousMobs + this.simultaneousMobsAddedPerPlayer * (float)additionalPlayers));
   }

   public long ticksBetweenItemSpawners() {
      return 160L;
   }

   public static TrialSpawnerConfig.Builder builder() {
      return new TrialSpawnerConfig.Builder();
   }

   public TrialSpawnerConfig withSpawning(final EntityType type) {
      CompoundTag tag = new CompoundTag();
      tag.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(type).toString());
      SpawnData spawnData = new SpawnData(tag, Optional.empty(), Optional.empty());
      return new TrialSpawnerConfig(this.spawnRange, this.totalMobs, this.simultaneousMobs, this.totalMobsAddedPerPlayer, this.simultaneousMobsAddedPerPlayer, this.ticksBetweenSpawn, WeightedList.of(spawnData), this.lootTablesToEject, this.itemsToDropWhenOminous);
   }

   public TrialSpawnerConfig withSpawning(final TypedEntityData entityData) {
      CompoundTag tag = new CompoundTag();
      SpawnData spawnData = new SpawnData(tag, Optional.empty(), Optional.empty());
      entityData.loadInto(spawnData, BuiltInRegistries.ENTITY_TYPE);
      return new TrialSpawnerConfig(this.spawnRange, this.totalMobs, this.simultaneousMobs, this.totalMobsAddedPerPlayer, this.simultaneousMobsAddedPerPlayer, this.ticksBetweenSpawn, WeightedList.of(spawnData), this.lootTablesToEject, this.itemsToDropWhenOminous);
   }

   public static class Builder {
      private int spawnRange = 4;
      private float totalMobs = 6.0F;
      private float simultaneousMobs = 2.0F;
      private float totalMobsAddedPerPlayer = 2.0F;
      private float simultaneousMobsAddedPerPlayer = 1.0F;
      private int ticksBetweenSpawn = 40;
      private WeightedList spawnPotentialsDefinition = WeightedList.of();
      private WeightedList lootTablesToEject = WeightedList.builder().add(BuiltInLootTables.SPAWNER_TRIAL_CHAMBER_CONSUMABLES).add(BuiltInLootTables.SPAWNER_TRIAL_CHAMBER_KEY).build();
      private ResourceKey itemsToDropWhenOminous = BuiltInLootTables.SPAWNER_TRIAL_ITEMS_TO_DROP_WHEN_OMINOUS;

      public TrialSpawnerConfig.Builder spawnRange(final int spawnRange) {
         this.spawnRange = spawnRange;
         return this;
      }

      public TrialSpawnerConfig.Builder totalMobs(final float totalMobs) {
         this.totalMobs = totalMobs;
         return this;
      }

      public TrialSpawnerConfig.Builder simultaneousMobs(final float simultaneousMobs) {
         this.simultaneousMobs = simultaneousMobs;
         return this;
      }

      public TrialSpawnerConfig.Builder totalMobsAddedPerPlayer(final float totalMobsAddedPerPlayer) {
         this.totalMobsAddedPerPlayer = totalMobsAddedPerPlayer;
         return this;
      }

      public TrialSpawnerConfig.Builder simultaneousMobsAddedPerPlayer(final float simultaneousMobsAddedPerPlayer) {
         this.simultaneousMobsAddedPerPlayer = simultaneousMobsAddedPerPlayer;
         return this;
      }

      public TrialSpawnerConfig.Builder ticksBetweenSpawn(final int ticksBetweenSpawn) {
         this.ticksBetweenSpawn = ticksBetweenSpawn;
         return this;
      }

      public TrialSpawnerConfig.Builder spawnPotentialsDefinition(final WeightedList spawnPotentialsDefinition) {
         this.spawnPotentialsDefinition = spawnPotentialsDefinition;
         return this;
      }

      public TrialSpawnerConfig.Builder lootTablesToEject(final WeightedList lootTablesToEject) {
         this.lootTablesToEject = lootTablesToEject;
         return this;
      }

      public TrialSpawnerConfig.Builder itemsToDropWhenOminous(final ResourceKey itemsToDropWhenOminous) {
         this.itemsToDropWhenOminous = itemsToDropWhenOminous;
         return this;
      }

      public TrialSpawnerConfig build() {
         return new TrialSpawnerConfig(this.spawnRange, this.totalMobs, this.simultaneousMobs, this.totalMobsAddedPerPlayer, this.simultaneousMobsAddedPerPlayer, this.ticksBetweenSpawn, this.spawnPotentialsDefinition, this.lootTablesToEject, this.itemsToDropWhenOminous);
      }
   }
}
