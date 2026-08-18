package net.minecraft.world.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.EquipmentTable;

public record SpawnData(CompoundTag entityToSpawn, Optional customSpawnRules, Optional equipment) {
   public static final String ENTITY_TAG = "entity";
   public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(CompoundTag.CODEC.fieldOf("entity").forGetter((s) -> s.entityToSpawn), SpawnData.CustomSpawnRules.CODEC.optionalFieldOf("custom_spawn_rules").forGetter((o) -> o.customSpawnRules), EquipmentTable.CODEC.optionalFieldOf("equipment").forGetter((o) -> o.equipment)).apply(i, SpawnData::new));
   public static final Codec LIST_CODEC = WeightedList.codec(CODEC);

   public SpawnData() {
      this(new CompoundTag(), Optional.empty(), Optional.empty());
   }

   public SpawnData {
      Optional id = entityToSpawn.read("id", Identifier.CODEC);
      if (id.isPresent()) {
         entityToSpawn.store("id", Identifier.CODEC, (Identifier)id.get());
      } else {
         entityToSpawn.remove("id");
      }

   }

   public CompoundTag getEntityToSpawn() {
      return this.entityToSpawn;
   }

   public Optional getCustomSpawnRules() {
      return this.customSpawnRules;
   }

   public Optional getEquipment() {
      return this.equipment;
   }

   public static record CustomSpawnRules(InclusiveRange blockLightLimit, InclusiveRange skyLightLimit) {
      private static final InclusiveRange LIGHT_RANGE = new InclusiveRange(0, 15);
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(lightLimit("block_light_limit").forGetter((o) -> o.blockLightLimit), lightLimit("sky_light_limit").forGetter((o) -> o.skyLightLimit)).apply(i, SpawnData.CustomSpawnRules::new));

      private static DataResult checkLightBoundaries(final InclusiveRange range) {
         return !LIGHT_RANGE.contains(range) ? DataResult.error(() -> "Light values must be withing range " + String.valueOf(LIGHT_RANGE)) : DataResult.success(range);
      }

      private static MapCodec lightLimit(final String name) {
         return InclusiveRange.INT.lenientOptionalFieldOf(name, LIGHT_RANGE).validate(SpawnData.CustomSpawnRules::checkLightBoundaries);
      }

      public boolean isValidPosition(final BlockPos blockSpawnPos, final ServerLevel level) {
         return this.blockLightLimit.isValueInRange(level.getBrightness(LightLayer.BLOCK, blockSpawnPos)) && this.skyLightLimit.isValueInRange(level.getEffectiveSkyBrightness(blockSpawnPos));
      }
   }
}
