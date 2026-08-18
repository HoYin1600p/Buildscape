package net.minecraft.client.renderer.texture.atlas;

import com.mojang.serialization.Codec;
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.client.renderer.texture.atlas.sources.SourceFilter;
import net.minecraft.client.renderer.texture.atlas.sources.Unstitcher;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;

public class SpriteSources {
   private static final ExtraCodecs.LateBoundIdMapper ID_MAPPER = new ExtraCodecs.LateBoundIdMapper();
   public static final Codec CODEC = ID_MAPPER.codec(Identifier.CODEC).dispatch(SpriteSource::codec, (c) -> c);
   public static final Codec FILE_CODEC = CODEC.listOf().fieldOf("sources").codec();

   public static void bootstrap() {
      ID_MAPPER.put(Identifier.withDefaultNamespace("single"), SingleFile.MAP_CODEC);
      ID_MAPPER.put(Identifier.withDefaultNamespace("directory"), DirectoryLister.MAP_CODEC);
      ID_MAPPER.put(Identifier.withDefaultNamespace("filter"), SourceFilter.MAP_CODEC);
      ID_MAPPER.put(Identifier.withDefaultNamespace("unstitch"), Unstitcher.MAP_CODEC);
      ID_MAPPER.put(Identifier.withDefaultNamespace("paletted_permutations"), PalettedPermutations.MAP_CODEC);
   }
}
