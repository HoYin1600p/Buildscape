package net.minecraft.client.gui.font.providers;

import com.mojang.blaze3d.font.SpaceProvider.Definition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.StringRepresentable;

public enum GlyphProviderType implements StringRepresentable {
   BITMAP("bitmap", BitmapProvider.Definition.CODEC),
   TTF("ttf", TrueTypeGlyphProviderDefinition.CODEC),
   SPACE("space", Definition.CODEC),
   UNIHEX("unihex", UnihexProvider.Definition.CODEC),
   REFERENCE("reference", ProviderReferenceDefinition.CODEC);

   public static final Codec CODEC = StringRepresentable.fromEnum(GlyphProviderType::values);
   private final String name;
   private final MapCodec codec;

   private GlyphProviderType(final String name, final MapCodec codec) {
      this.name = name;
      this.codec = codec;
   }

   public String getSerializedName() {
      return this.name;
   }

   public MapCodec mapCodec() {
      return this.codec;
   }

   // $FF: synthetic method
   private static GlyphProviderType[] $values() {
      return new GlyphProviderType[]{BITMAP, TTF, SPACE, UNIHEX, REFERENCE};
   }
}
