package net.minecraft.network.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.component.ResolvableProfile;

public interface FontDescription {
   Codec CODEC = Identifier.CODEC.flatComapMap(FontDescription.Resource::new, (fontDescription) -> {
      if (fontDescription instanceof FontDescription.Resource resource) {
         return DataResult.success(resource.id());
      } else {
         return DataResult.error(() -> "Unsupported font description type: " + String.valueOf(fontDescription));
      }
   });
   FontDescription.Resource DEFAULT = new FontDescription.Resource(Identifier.withDefaultNamespace("default"));

   public static record AtlasSprite(Identifier atlasId, Identifier spriteId) implements FontDescription {
   }

   public static record PlayerSprite(ResolvableProfile profile, boolean hat) implements FontDescription {
   }

   public static record Resource(Identifier id) implements FontDescription {
   }
}
