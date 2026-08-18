package net.minecraft.world.item.equipment.trim;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record TrimMaterial(Identifier paletteId, Component description) {
   public static final Codec DIRECT_CODEC = RecordCodecBuilder.create((i) -> i.group(Identifier.CODEC.fieldOf("palette_id").forGetter(TrimMaterial::paletteId), ComponentSerialization.CODEC.fieldOf("description").forGetter(TrimMaterial::description)).apply(i, TrimMaterial::new));
   public static final StreamCodec DIRECT_STREAM_CODEC = StreamCodec.composite(Identifier.STREAM_CODEC, TrimMaterial::paletteId, ComponentSerialization.STREAM_CODEC, TrimMaterial::description, TrimMaterial::new);
   public static final Codec CODEC = RegistryCodecs.holder(Registries.TRIM_MATERIAL, DIRECT_CODEC);
   public static final StreamCodec STREAM_CODEC = ByteBufCodecs.holder(Registries.TRIM_MATERIAL, DIRECT_STREAM_CODEC);
}
