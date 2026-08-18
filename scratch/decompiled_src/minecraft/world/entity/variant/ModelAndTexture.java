package net.minecraft.world.entity.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.ClientAsset;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record ModelAndTexture(Object model, ClientAsset.ResourceTexture asset) {
   public ModelAndTexture(final Object model, final Identifier assetId) {
      this(model, new ClientAsset.ResourceTexture(assetId));
   }

   public static MapCodec codec(final Codec modelCodec, final Object defaultModel) {
      return RecordCodecBuilder.mapCodec((i) -> i.group(modelCodec.optionalFieldOf("model", defaultModel).forGetter(ModelAndTexture::model), ClientAsset.ResourceTexture.DEFAULT_FIELD_CODEC.forGetter(ModelAndTexture::asset)).apply(i, ModelAndTexture::new));
   }

   public static StreamCodec streamCodec(final StreamCodec modelCodec) {
      return StreamCodec.composite(modelCodec, ModelAndTexture::model, ClientAsset.ResourceTexture.STREAM_CODEC, ModelAndTexture::asset, ModelAndTexture::new);
   }
}
