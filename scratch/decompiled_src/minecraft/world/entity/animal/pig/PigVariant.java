package net.minecraft.world.entity.animal.pig;

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

public record PigVariant(ModelAndTexture modelAndTexture, ClientAsset.ResourceTexture babyTexture, SpawnPrioritySelectors spawnConditions) implements PriorityProvider {
   public static final Codec DIRECT_CODEC = RecordCodecBuilder.create((i) -> i.group(ModelAndTexture.codec(PigVariant.ModelType.CODEC, PigVariant.ModelType.NORMAL).forGetter(PigVariant::modelAndTexture), ClientAsset.ResourceTexture.CODEC.fieldOf("baby_asset_id").forGetter(PigVariant::babyTexture), SpawnPrioritySelectors.CODEC.fieldOf("spawn_conditions").forGetter(PigVariant::spawnConditions)).apply(i, PigVariant::new));
   public static final Codec NETWORK_CODEC = RecordCodecBuilder.create((i) -> i.group(ModelAndTexture.codec(PigVariant.ModelType.CODEC, PigVariant.ModelType.NORMAL).forGetter(PigVariant::modelAndTexture), ClientAsset.ResourceTexture.CODEC.fieldOf("baby_asset_id").forGetter(PigVariant::babyTexture)).apply(i, PigVariant::new));
   public static final Codec CODEC = RegistryCodecs.holder(Registries.PIG_VARIANT);
   public static final StreamCodec STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.PIG_VARIANT);

   private PigVariant(final ModelAndTexture assetInfo, final ClientAsset.ResourceTexture babyTexture) {
      this(assetInfo, babyTexture, SpawnPrioritySelectors.EMPTY);
   }

   public List selectors() {
      return this.spawnConditions.selectors();
   }

   public static enum ModelType implements StringRepresentable {
      NORMAL("normal"),
      COLD("cold");

      public static final Codec CODEC = StringRepresentable.fromEnum(PigVariant.ModelType::values);
      private final String name;

      private ModelType(final String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static PigVariant.ModelType[] $values() {
         return new PigVariant.ModelType[]{NORMAL, COLD};
      }
   }
}
