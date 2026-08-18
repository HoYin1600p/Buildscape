package net.minecraft.world.entity.animal.wolf;

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

public record WolfVariant(WolfVariant.AssetInfo adultInfo, WolfVariant.AssetInfo babyInfo, SpawnPrioritySelectors spawnConditions) implements PriorityProvider {
   public static final Codec DIRECT_CODEC = RecordCodecBuilder.create((i) -> i.group(WolfVariant.AssetInfo.CODEC.fieldOf("assets").forGetter(WolfVariant::adultInfo), WolfVariant.AssetInfo.CODEC.fieldOf("baby_assets").forGetter(WolfVariant::babyInfo), SpawnPrioritySelectors.CODEC.fieldOf("spawn_conditions").forGetter(WolfVariant::spawnConditions)).apply(i, WolfVariant::new));
   public static final Codec NETWORK_CODEC = RecordCodecBuilder.create((i) -> i.group(WolfVariant.AssetInfo.CODEC.fieldOf("assets").forGetter(WolfVariant::adultInfo), WolfVariant.AssetInfo.CODEC.fieldOf("baby_assets").forGetter(WolfVariant::babyInfo)).apply(i, WolfVariant::new));
   public static final Codec CODEC = RegistryCodecs.holder(Registries.WOLF_VARIANT);
   public static final StreamCodec STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.WOLF_VARIANT);

   private WolfVariant(final WolfVariant.AssetInfo adultInfo, final WolfVariant.AssetInfo babyInfo) {
      this(adultInfo, babyInfo, SpawnPrioritySelectors.EMPTY);
   }

   public List selectors() {
      return this.spawnConditions.selectors();
   }

   public static record AssetInfo(ClientAsset.ResourceTexture wild, ClientAsset.ResourceTexture tame, ClientAsset.ResourceTexture angry) {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> instance.group(ClientAsset.ResourceTexture.CODEC.fieldOf("wild").forGetter(WolfVariant.AssetInfo::wild), ClientAsset.ResourceTexture.CODEC.fieldOf("tame").forGetter(WolfVariant.AssetInfo::tame), ClientAsset.ResourceTexture.CODEC.fieldOf("angry").forGetter(WolfVariant.AssetInfo::angry)).apply(instance, WolfVariant.AssetInfo::new));
   }
}
