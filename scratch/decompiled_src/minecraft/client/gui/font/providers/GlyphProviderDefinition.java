package net.minecraft.client.gui.font.providers;

import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import net.minecraft.client.gui.font.FontOption;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

public interface GlyphProviderDefinition {
   MapCodec MAP_CODEC = GlyphProviderType.CODEC.dispatchMap(GlyphProviderDefinition::type, GlyphProviderType::mapCodec);

   GlyphProviderType type();

   Either unpack();

   public static record Conditional(GlyphProviderDefinition definition, FontOption.Filter filter) {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(GlyphProviderDefinition.MAP_CODEC.forGetter(GlyphProviderDefinition.Conditional::definition), FontOption.Filter.CODEC.optionalFieldOf("filter", FontOption.Filter.ALWAYS_PASS).forGetter(GlyphProviderDefinition.Conditional::filter)).apply(i, GlyphProviderDefinition.Conditional::new));
   }

   public interface Loader {
      GlyphProvider load(ResourceManager resourceManager) throws IOException;
   }

   public static record Reference(Identifier id) {
   }
}
