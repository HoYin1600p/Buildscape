package net.minecraft.client.resources.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;

public record EquipmentClientInfo(Map layers, List trimOverrides) {
   private static final Codec LAYER_LIST_CODEC = ExtraCodecs.nonEmptyList(EquipmentClientInfo.Layer.CODEC.listOf());
   public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(ExtraCodecs.nonEmptyMap(Codec.unboundedMap(EquipmentClientInfo.LayerType.CODEC, LAYER_LIST_CODEC)).fieldOf("layers").forGetter(EquipmentClientInfo::layers), EquipmentClientInfo.TrimOverride.CODEC.listOf().optionalFieldOf("trim_overrides", List.of()).forGetter(EquipmentClientInfo::trimOverrides)).apply(i, EquipmentClientInfo::new));

   public static EquipmentClientInfo.Builder builder() {
      return new EquipmentClientInfo.Builder();
   }

   public List getLayers(final EquipmentClientInfo.LayerType type) {
      return (List)this.layers.getOrDefault(type, List.of());
   }

   public static class Builder {
      private final Map layersByType = new EnumMap(EquipmentClientInfo.LayerType.class);
      private final ImmutableList.Builder trimOverrides = ImmutableList.builder();

      private Builder() {
      }

      public EquipmentClientInfo.Builder addHumanoidLayers(final Identifier textureId) {
         return this.addHumanoidLayers(textureId, false);
      }

      public EquipmentClientInfo.Builder addHumanoidLayers(final Identifier textureId, final boolean dyeable) {
         this.addLayers(EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS, EquipmentClientInfo.Layer.leatherDyeable(textureId, dyeable));
         this.addMainHumanoidLayer(textureId, dyeable);
         return this;
      }

      public EquipmentClientInfo.Builder addMainHumanoidLayer(final Identifier textureId, final boolean dyeable) {
         this.addLayers(EquipmentClientInfo.LayerType.HUMANOID, EquipmentClientInfo.Layer.leatherDyeable(textureId, dyeable));
         this.addLayers(EquipmentClientInfo.LayerType.HUMANOID_BABY, EquipmentClientInfo.Layer.leatherDyeable(textureId, dyeable));
         return this;
      }

      public EquipmentClientInfo.Builder addLayers(final EquipmentClientInfo.LayerType type, final EquipmentClientInfo.Layer... layers) {
         Collections.addAll((Collection)this.layersByType.computeIfAbsent(type, (t) -> new ArrayList()), layers);
         return this;
      }

      public EquipmentClientInfo.Builder replaceTrimPalette(final ResourceKey fromMaterial, final Identifier toPaletteId) {
         EquipmentClientInfo.TrimPredicate predicate = new EquipmentClientInfo.TrimPredicate(Optional.of(fromMaterial), Optional.empty());
         this.trimOverrides.add(new EquipmentClientInfo.TrimOverride(predicate, Optional.empty(), Optional.of(toPaletteId)));
         return this;
      }

      public EquipmentClientInfo build() {
         return new EquipmentClientInfo((Map)this.layersByType.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, (entry) -> List.copyOf((Collection)entry.getValue()))), this.trimOverrides.build());
      }
   }

   public static record Dyeable(Optional colorWhenUndyed) {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(ExtraCodecs.RGB_COLOR_CODEC.optionalFieldOf("color_when_undyed").forGetter(EquipmentClientInfo.Dyeable::colorWhenUndyed)).apply(i, EquipmentClientInfo.Dyeable::new));
   }

   public static record Layer(Identifier textureId, Optional dyeable, boolean usePlayerTexture) {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(Identifier.CODEC.fieldOf("texture").forGetter(EquipmentClientInfo.Layer::textureId), EquipmentClientInfo.Dyeable.CODEC.optionalFieldOf("dyeable").forGetter(EquipmentClientInfo.Layer::dyeable), Codec.BOOL.optionalFieldOf("use_player_texture", false).forGetter(EquipmentClientInfo.Layer::usePlayerTexture)).apply(i, EquipmentClientInfo.Layer::new));

      public Layer(final Identifier textureId) {
         this(textureId, Optional.empty(), false);
      }

      public static EquipmentClientInfo.Layer leatherDyeable(final Identifier textureId, final boolean dyeable) {
         return new EquipmentClientInfo.Layer(textureId, dyeable ? Optional.of(new EquipmentClientInfo.Dyeable(Optional.of(-6265536))) : Optional.empty(), false);
      }

      public static EquipmentClientInfo.Layer onlyIfDyed(final Identifier textureId, final boolean dyeable) {
         return new EquipmentClientInfo.Layer(textureId, dyeable ? Optional.of(new EquipmentClientInfo.Dyeable(Optional.empty())) : Optional.empty(), false);
      }

      public Identifier getTextureLocation(final EquipmentClientInfo.LayerType type) {
         return this.textureId.withPath((UnaryOperator)((path) -> "textures/entity/equipment/" + type.getSerializedName() + "/" + path + ".png"));
      }
   }

   public static enum LayerType implements StringRepresentable {
      HUMANOID("humanoid"),
      HUMANOID_LEGGINGS("humanoid_leggings"),
      HUMANOID_BABY("humanoid_baby"),
      WINGS("wings"),
      WOLF_BODY("wolf_body"),
      HORSE_BODY("horse_body"),
      LLAMA_BODY("llama_body"),
      PIG_SADDLE("pig_saddle"),
      STRIDER_SADDLE("strider_saddle"),
      CAMEL_SADDLE("camel_saddle"),
      CAMEL_HUSK_SADDLE("camel_husk_saddle"),
      HORSE_SADDLE("horse_saddle"),
      DONKEY_SADDLE("donkey_saddle"),
      MULE_SADDLE("mule_saddle"),
      ZOMBIE_HORSE_SADDLE("zombie_horse_saddle"),
      SKELETON_HORSE_SADDLE("skeleton_horse_saddle"),
      HAPPY_GHAST_BODY("happy_ghast_body"),
      NAUTILUS_SADDLE("nautilus_saddle"),
      NAUTILUS_BODY("nautilus_body");

      public static final Codec CODEC = StringRepresentable.fromEnum(EquipmentClientInfo.LayerType::values);
      private final String id;

      private LayerType(final String id) {
         this.id = id;
      }

      public String getSerializedName() {
         return this.id;
      }

      public String trimAssetPrefix() {
         return "trims/entity/" + this.id;
      }

      // $FF: synthetic method
      private static EquipmentClientInfo.LayerType[] $values() {
         return new EquipmentClientInfo.LayerType[]{HUMANOID, HUMANOID_LEGGINGS, HUMANOID_BABY, WINGS, WOLF_BODY, HORSE_BODY, LLAMA_BODY, PIG_SADDLE, STRIDER_SADDLE, CAMEL_SADDLE, CAMEL_HUSK_SADDLE, HORSE_SADDLE, DONKEY_SADDLE, MULE_SADDLE, ZOMBIE_HORSE_SADDLE, SKELETON_HORSE_SADDLE, HAPPY_GHAST_BODY, NAUTILUS_SADDLE, NAUTILUS_BODY};
      }
   }

   public static record TrimOverride(EquipmentClientInfo.TrimPredicate predicate, Optional textureId, Optional paletteId) {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(EquipmentClientInfo.TrimPredicate.CODEC.fieldOf("when").forGetter(EquipmentClientInfo.TrimOverride::predicate), Identifier.CODEC.optionalFieldOf("texture").forGetter(EquipmentClientInfo.TrimOverride::textureId), Identifier.CODEC.optionalFieldOf("palette").forGetter(EquipmentClientInfo.TrimOverride::paletteId)).apply(i, EquipmentClientInfo.TrimOverride::new)).validate(EquipmentClientInfo.TrimOverride::validate);

      private static DataResult validate(final EquipmentClientInfo.TrimOverride override) {
         return override.textureId.isEmpty() && override.paletteId.isEmpty() ? DataResult.error(() -> "One of texture or palette must be specified") : DataResult.success(override);
      }
   }

   public static record TrimPredicate(Optional material, Optional pattern) {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(ResourceKey.codec(Registries.TRIM_MATERIAL).optionalFieldOf("material").forGetter(EquipmentClientInfo.TrimPredicate::material), ResourceKey.codec(Registries.TRIM_PATTERN).optionalFieldOf("pattern").forGetter(EquipmentClientInfo.TrimPredicate::pattern)).apply(i, EquipmentClientInfo.TrimPredicate::new)).validate(EquipmentClientInfo.TrimPredicate::validate);

      private static DataResult validate(final EquipmentClientInfo.TrimPredicate override) {
         return override.material.isEmpty() && override.pattern.isEmpty() ? DataResult.error(() -> "One of material or pattern must be specified") : DataResult.success(override);
      }

      public boolean matches(final ArmorTrim trim) {
         return (this.material.isEmpty() || trim.material().is((ResourceKey)this.material.get())) && (this.pattern.isEmpty() || trim.pattern().is((ResourceKey)this.pattern.get()));
      }
   }
}
