package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.equipment.trim.TrimMaterial;

public record ProvidesTrimMaterial(Holder material) {
   public static final Codec CODEC = TrimMaterial.CODEC.xmap(ProvidesTrimMaterial::new, ProvidesTrimMaterial::material);
   public static final StreamCodec STREAM_CODEC = TrimMaterial.STREAM_CODEC.map(ProvidesTrimMaterial::new, ProvidesTrimMaterial::material);
}
