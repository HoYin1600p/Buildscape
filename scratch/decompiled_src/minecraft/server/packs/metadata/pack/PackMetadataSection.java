package net.minecraft.server.packs.metadata.pack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.util.InclusiveRange;

public record PackMetadataSection(Component description, InclusiveRange supportedFormats) {
   private static final Codec FALLBACK_CODEC = RecordCodecBuilder.create((i) -> i.group(ComponentSerialization.CODEC.fieldOf("description").forGetter(PackMetadataSection::description)).apply(i, (description) -> new PackMetadataSection(description, new InclusiveRange(PackFormat.of(Integer.MAX_VALUE)))));
   public static final MetadataSectionType CLIENT_TYPE = new MetadataSectionType("pack", codecForPackType(PackType.CLIENT_RESOURCES));
   public static final MetadataSectionType SERVER_TYPE = new MetadataSectionType("pack", codecForPackType(PackType.SERVER_DATA));
   public static final MetadataSectionType FALLBACK_TYPE = new MetadataSectionType("pack", FALLBACK_CODEC);

   private static Codec codecForPackType(final PackType packType) {
      return RecordCodecBuilder.create((i) -> i.group(ComponentSerialization.CODEC.fieldOf("description").forGetter(PackMetadataSection::description), PackFormat.packCodec(packType).forGetter(PackMetadataSection::supportedFormats)).apply(i, PackMetadataSection::new));
   }

   public static MetadataSectionType forPackType(final PackType packType) {
      MetadataSectionType var10000;
      switch (packType) {
         case CLIENT_RESOURCES:
            var10000 = CLIENT_TYPE;
            break;
         case SERVER_DATA:
            var10000 = SERVER_TYPE;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }
}
