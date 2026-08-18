package net.minecraft.world.level.levelgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.biome.MobSpawnSettings;

public record StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType boundingBox, WeightedList spawns) {
   public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(StructureSpawnOverride.BoundingBoxType.CODEC.fieldOf("bounding_box").forGetter(StructureSpawnOverride::boundingBox), WeightedList.codec(MobSpawnSettings.SpawnerData.CODEC).fieldOf("spawns").forGetter(StructureSpawnOverride::spawns)).apply(i, StructureSpawnOverride::new));

   public static enum BoundingBoxType implements StringRepresentable {
      PIECE("piece"),
      STRUCTURE("full");

      public static final Codec CODEC = StringRepresentable.fromEnum(StructureSpawnOverride.BoundingBoxType::values);
      private final String id;

      private BoundingBoxType(final String id) {
         this.id = id;
      }

      public String getSerializedName() {
         return this.id;
      }

      // $FF: synthetic method
      private static StructureSpawnOverride.BoundingBoxType[] $values() {
         return new StructureSpawnOverride.BoundingBoxType[]{PIECE, STRUCTURE};
      }
   }
}
