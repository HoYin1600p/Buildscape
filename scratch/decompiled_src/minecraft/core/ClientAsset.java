package net.minecraft.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.UnaryOperator;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public interface ClientAsset {
   Identifier id();

   public static record DownloadedTexture(Identifier texturePath, String url) implements ClientAsset.Texture {
      public Identifier id() {
         return this.texturePath;
      }
   }

   public static record ResourceTexture(Identifier id, Identifier texturePath) implements ClientAsset.Texture {
      public static final Codec CODEC = Identifier.CODEC.xmap(ClientAsset.ResourceTexture::new, ClientAsset.ResourceTexture::id);
      public static final MapCodec DEFAULT_FIELD_CODEC = CODEC.fieldOf("asset_id");
      public static final StreamCodec STREAM_CODEC = Identifier.STREAM_CODEC.map(ClientAsset.ResourceTexture::new, ClientAsset.ResourceTexture::id);

      public ResourceTexture(final Identifier texture) {
         this(texture, texture.withPath((UnaryOperator)((path) -> "textures/" + path + ".png")));
      }
   }

   public interface Texture extends ClientAsset {
      Identifier texturePath();
   }
}
