package net.minecraft.world.level.block.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record BannerPattern(Identifier assetId, String translationKey) {
   public static final Codec DIRECT_CODEC = RecordCodecBuilder.create((i) -> i.group(Identifier.CODEC.fieldOf("asset_id").forGetter(BannerPattern::assetId), Codec.STRING.fieldOf("translation_key").forGetter(BannerPattern::translationKey)).apply(i, BannerPattern::new));
   public static final StreamCodec DIRECT_STREAM_CODEC = StreamCodec.composite(Identifier.STREAM_CODEC, BannerPattern::assetId, ByteBufCodecs.STRING_UTF8, BannerPattern::translationKey, BannerPattern::new);
   public static final Codec CODEC = RegistryCodecs.holder(Registries.BANNER_PATTERN, DIRECT_CODEC);
   public static final StreamCodec STREAM_CODEC = ByteBufCodecs.holder(Registries.BANNER_PATTERN, DIRECT_STREAM_CODEC);
}
