package net.minecraft.client.gui.font.providers;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

public record ProviderReferenceDefinition(Identifier id) implements GlyphProviderDefinition {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Identifier.CODEC.fieldOf("id").forGetter(ProviderReferenceDefinition::id)).apply(i, ProviderReferenceDefinition::new));

   public GlyphProviderType type() {
      return GlyphProviderType.REFERENCE;
   }

   public Either unpack() {
      return Either.right(new GlyphProviderDefinition.Reference(this.id));
   }
}
