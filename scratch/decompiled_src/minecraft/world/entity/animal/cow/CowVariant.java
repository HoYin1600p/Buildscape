package net.minecraft.world.entity.animal.cow;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.ClientAsset;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.variant.ModelAndTexture;
import net.minecraft.world.entity.variant.PriorityProvider;
import net.minecraft.world.entity.variant.SpawnPrioritySelectors;

public record CowVariant(ModelAndTexture modelAndTexture, ClientAsset.ResourceTexture babyTexture, SpawnPrioritySelectors spawnConditions) implements PriorityProvider {
   public static final Codec DIRECT_CODEC = RecordCodecBuilder.create((i) -> i.group(ModelAndTexture.codec(CowVariant.ModelType.CODEC, CowVariant.ModelType.NORMAL).forGetter(CowVariant::modelAndTexture), ClientAsset.ResourceTexture.CODEC.fieldOf("baby_asset_id").forGetter(CowVariant::babyTexture), SpawnPrioritySelectors.CODEC.fieldOf("spawn_conditions").forGetter(CowVariant::spawnConditions)).apply(i, CowVariant::new));
   public static final Codec NETWORK_CODEC = RecordCodecBuilder.create((i) -> i.group(ModelAndTexture.codec(CowVariant.ModelType.CODEC, CowVariant.ModelType.NORMAL).forGetter(CowVariant::modelAndTexture), ClientAsset.ResourceTexture.CODEC.fieldOf("baby_asset_id").forGetter(CowVariant::babyTexture)).apply(i, CowVariant::new));
   public static final Codec CODEC = RegistryCodecs.holder(Registries.COW_VARIANT);
   public static final StreamCodec STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.COW_VARIANT);

   private CowVariant(final ModelAndTexture assetInfo, final ClientAsset.ResourceTexture babyTexture) {
      this(assetInfo, babyTexture, SpawnPrioritySelectors.EMPTY);
   }

   public List selectors() {
      return this.spawnConditions.selectors();
   }

   public static enum ModelType implements StringRepresentable {
      NORMAL("normal"),
      COLD("cold"),
      WARM("warm");

      public static final Codec CODEC = StringRepresentable.fromEnum(CowVariant.ModelType::values);
      private final String name;

      private ModelType(final String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static CowVariant.ModelType[] $values() {
         return new CowVariant.ModelType[]{NORMAL, COLD, WARM};
      }
   }
}
