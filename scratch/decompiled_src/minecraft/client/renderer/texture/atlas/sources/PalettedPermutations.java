package net.minecraft.client.renderer.texture.atlas.sources;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.client.resources.palette.Palette;
import net.minecraft.client.resources.palette.PaletteMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public record PalettedPermutations(List textures, Identifier paletteKey, Map permutations, String separator) implements SpriteSource {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String DEFAULT_SEPARATOR = "_";
   public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.list(Identifier.CODEC).fieldOf("textures").forGetter(PalettedPermutations::textures), Identifier.CODEC.fieldOf("palette_key").forGetter(PalettedPermutations::paletteKey), Codec.unboundedMap(Codec.STRING, Identifier.CODEC).fieldOf("permutations").forGetter(PalettedPermutations::permutations), Codec.STRING.optionalFieldOf("separator", "_").forGetter(PalettedPermutations::separator)).apply(i, PalettedPermutations::new));

   public PalettedPermutations(final List textures, final Identifier paletteKey, final Map permutations) {
      this(textures, paletteKey, permutations, "_");
   }

   public void run(final ResourceManager resourceManager, final SpriteSource.Output output) {
      Supplier paletteKeySupplier = Suppliers.memoize(() -> loadPaletteEntryFromImage(resourceManager, this.paletteKey));
      Map palettes = new HashMap();
      this.permutations.forEach((suffix, palette) -> palettes.put(suffix, Suppliers.memoize(() -> PaletteMapping.create((Palette)paletteKeySupplier.get(), loadPaletteEntryFromImage(resourceManager, palette)))));

      for(Identifier textureLocation : this.textures) {
         Identifier textureId = TEXTURE_ID_CONVERTER.idToFile(textureLocation);
         Optional resource = resourceManager.getResource(textureId);
         if (resource.isEmpty()) {
            LOGGER.warn("Unable to find texture {}", textureId);
         } else {
            LazyLoadedImage baseImage = new LazyLoadedImage(textureId, (Resource)resource.get(), palettes.size());

            for(Map.Entry entry : palettes.entrySet()) {
               Identifier permutationLocation = textureLocation.withSuffix(this.separator + (String)entry.getKey());
               output.add(permutationLocation, new PalettedPermutations.PalettedSpriteSupplier(baseImage, (Supplier)entry.getValue(), permutationLocation));
            }
         }
      }

   }

   private static Palette loadPaletteEntryFromImage(final ResourceManager resourceManager, final Identifier paletteId) {
      Optional resource = resourceManager.getResource(Palette.ID_CONVERTER.idToFile(paletteId));
      if (resource.isEmpty()) {
         LOGGER.error("Failed to load palette image {}", paletteId);
         throw new IllegalArgumentException();
      } else {
         try {
            return Palette.load((Resource)resource.get());
         } catch (Exception var4) {
            LOGGER.error("Couldn't load texture {}", paletteId, var4);
            throw new IllegalArgumentException();
         }
      }
   }

   public MapCodec codec() {
      return MAP_CODEC;
   }

   private static record PalettedSpriteSupplier(LazyLoadedImage baseImage, Supplier palette, Identifier permutationLocation) implements SpriteSource.DiscardableLoader {
      public @Nullable SpriteContents get(final SpriteResourceLoader loader) {
         Object var3;
         try {
            NativeImage image = this.baseImage.get().mappedCopy((IntUnaryOperator)this.palette.get());
            return new SpriteContents(this.permutationLocation, new FrameSize(image.getWidth(), image.getHeight()), image);
         } catch (IllegalArgumentException | IOException var7) {
            PalettedPermutations.LOGGER.error("unable to apply palette to {}", this.permutationLocation, var7);
            var3 = null;
         } finally {
            this.baseImage.release();
         }

         return (SpriteContents)var3;
      }

      public void discard() {
         this.baseImage.release();
      }
   }
}
