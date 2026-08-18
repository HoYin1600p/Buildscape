package net.minecraft.client.particle;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;

public class ParticleDescription {
   private final List textures;

   private ParticleDescription(final List textures) {
      this.textures = textures;
   }

   public List getTextures() {
      return this.textures;
   }

   public static ParticleDescription fromJson(final JsonObject data) {
      JsonArray texturesData = GsonHelper.getAsJsonArray(data, "textures", (JsonArray)null);
      if (texturesData == null) {
         return new ParticleDescription(List.of());
      } else {
         List textures = (List)Streams.stream(texturesData).map((element) -> GsonHelper.convertToString(element, "texture")).map(Identifier::parse).collect(ImmutableList.toImmutableList());
         return new ParticleDescription(textures);
      }
   }
}
