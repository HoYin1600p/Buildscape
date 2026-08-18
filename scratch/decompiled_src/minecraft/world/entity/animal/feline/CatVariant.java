package net.minecraft.world.entity.animal.feline;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.ClientAsset;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.variant.PriorityProvider;
import net.minecraft.world.entity.variant.SpawnPrioritySelectors;

public record CatVariant(ClientAsset.ResourceTexture adultAssetInfo, ClientAsset.ResourceTexture babyAssetInfo, SpawnPrioritySelectors spawnConditions) implements PriorityProvider {
   public static final Codec DIRECT_CODEC = RecordCodecBuilder.create((i) -> i.group(ClientAsset.ResourceTexture.DEFAULT_FIELD_CODEC.forGetter(CatVariant::adultAssetInfo), ClientAsset.ResourceTexture.CODEC.fieldOf("baby_asset_id").forGetter(CatVariant::babyAssetInfo), SpawnPrioritySelectors.CODEC.fieldOf("spawn_conditions").forGetter(CatVariant::spawnConditions)).apply(i, CatVariant::new));
   public static final Codec NETWORK_CODEC = RecordCodecBuilder.create((i) -> i.group(ClientAsset.ResourceTexture.DEFAULT_FIELD_CODEC.forGetter(CatVariant::adultAssetInfo), ClientAsset.ResourceTexture.CODEC.fieldOf("baby_asset_id").forGetter(CatVariant::babyAssetInfo)).apply(i, CatVariant::new));
   public static final Codec CODEC = RegistryCodecs.holder(Registries.CAT_VARIANT);
   public static final StreamCodec STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.CAT_VARIANT);

   private CatVariant(final ClientAsset.ResourceTexture adultAssetInfo, final ClientAsset.ResourceTexture babyAssetInfo) {
      this(adultAssetInfo, babyAssetInfo, SpawnPrioritySelectors.EMPTY);
   }

   public List selectors() {
      return this.spawnConditions.selectors();
   }

   public ClientAsset.ResourceTexture assetInfo(final boolean isBaby) {
      return isBaby ? this.babyAssetInfo : this.adultAssetInfo;
   }
}
