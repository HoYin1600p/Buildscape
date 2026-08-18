package net.minecraft.client.resources.metadata.language;

import com.mojang.serialization.Codec;
import java.util.Map;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.server.packs.metadata.MetadataSectionType;

public record LanguageMetadataSection(Map languages) {
   public static final Codec LANGUAGE_CODE_CODEC = Codec.string(1, 16);
   public static final Codec CODEC = Codec.unboundedMap(LANGUAGE_CODE_CODEC, LanguageInfo.CODEC).xmap(LanguageMetadataSection::new, LanguageMetadataSection::languages);
   public static final MetadataSectionType TYPE = new MetadataSectionType("language", CODEC);
}
