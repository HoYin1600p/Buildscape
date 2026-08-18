package net.minecraft.client.resources.palette;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.device.GpuDevice;
import com.mojang.renderpearl.api.textures.FilterMode;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.Dumpable;
import net.minecraft.client.renderer.texture.DynamicAtlasTree;
import net.minecraft.client.renderer.texture.DynamicAtlasTreeSlot;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.UvMapping;
import net.minecraft.client.resources.metadata.texture.PaletteMetadataSection;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ARGB;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class PalettedTextureManager implements PreparableReloadListener, AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final TextureManager textureManager;
   private BiFunction paletteMappings = (var0, var1) -> PaletteMapping.NONE;
   private final LoadingCache baseTextureCache;
   private final List atlasTextures = new ArrayList();
   private final List overflowTextures = new ArrayList();
   private final Map handles = new HashMap();
   private final PalettedTextureManager.Handle missingHandle;

   public PalettedTextureManager(final ResourceManager resourceManager, final TextureManager textureManager) {
      this.textureManager = textureManager;
      this.baseTextureCache = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(5L)).maximumSize(64L).removalListener((notification) -> ((Optional)notification.getValue()).ifPresent(PalettedTextureManager.BaseTexture::close)).build(new CacheLoader(this) {
         {
            Objects.requireNonNull(this$0);
         }

         public Optional load(final Identifier id) {
            return Optional.ofNullable(PalettedTextureManager.loadBaseTexture(id, resourceManager));
         }
      });
      this.missingHandle = new PalettedTextureManager.Handle(this) {
         {
            Objects.requireNonNull(this$0);
         }

         public Identifier textureLocation() {
            return MissingTextureAtlasSprite.getLocation();
         }

         public float getU(final float offset) {
            return offset;
         }

         public float getV(final float offset) {
            return offset;
         }
      };
   }

   private static PalettedTextureManager.@Nullable BaseTexture loadBaseTexture(final Identifier id, final ResourceManager resourceManager) {
      Identifier location = id.withPath((UnaryOperator)((path) -> "textures/" + path + ".png"));

      try {
         Resource resource = resourceManager.getResourceOrThrow(location);
         InputStream input = resource.open();

         NativeImage image;
         try {
            image = NativeImage.read(input);
         } catch (Throwable var9) {
            if (input != null) {
               try {
                  input.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (input != null) {
            input.close();
         }

         return new PalettedTextureManager.BaseTexture(image, (PaletteMetadataSection)resource.metadata().getSection(PaletteMetadataSection.TYPE).orElse((Object)null));
      } catch (IOException var10) {
         LOGGER.error("Failed to load paletted base texture at {}", location, var10);
         return null;
      }
   }

   public PalettedTextureManager.Handle getOrPrepare(final Identifier baseTexture, final Identifier paletteId) {
      PalettedTextureManager.SlotKey key = new PalettedTextureManager.SlotKey(baseTexture, paletteId);
      PalettedTextureManager.Handle existingHandle = (PalettedTextureManager.Handle)this.handles.get(key);
      if (existingHandle != null) {
         return existingHandle;
      } else {
         PalettedTextureManager.Handle handle = this.prepareSlot(baseTexture, paletteId);
         this.handles.put(key, handle);
         return handle;
      }
   }

   private PalettedTextureManager.Handle prepareSlot(final Identifier baseTextureId, final Identifier paletteId) {
      Optional maybeBaseTexture = (Optional)this.baseTextureCache.getUnchecked(baseTextureId);
      if (maybeBaseTexture.isEmpty()) {
         return this.missingHandle;
      } else {
         PalettedTextureManager.BaseTexture baseTexture = (PalettedTextureManager.BaseTexture)maybeBaseTexture.get();
         PaletteMetadataSection paletteMetadata = baseTexture.paletteMetadata();
         PaletteMapping paletteMapping = paletteMetadata != null ? (PaletteMapping)this.paletteMappings.apply(paletteMetadata.basePalette(), paletteId) : PaletteMapping.NONE;
         NativeImage newTexture = baseTexture.image().mappedCopy(paletteMapping);

         PalettedTextureManager.Slot var9;
         try {
            PalettedTextureManager.Slot slot = this.allocateSlot(newTexture.getWidth(), newTexture.getHeight());
            RenderSystem.getDevice().createCommandEncoder().writeToTexture(slot.texture.getTexture(), newTexture, 0, 0, slot.x, slot.y);
            var9 = slot;
         } catch (Throwable var11) {
            if (newTexture != null) {
               try {
                  newTexture.close();
               } catch (Throwable var10) {
                  var11.addSuppressed(var10);
               }
            }

            throw var11;
         }

         if (newTexture != null) {
            newTexture.close();
         }

         return var9;
      }
   }

   private PalettedTextureManager.Slot allocateSlot(final int width, final int height) {
      if (width <= 512 && height <= 512) {
         for(PalettedTextureManager.AtlasTexture atlas : this.atlasTextures) {
            DynamicAtlasTreeSlot atlasSlot = atlas.tryAllocateSlot(width, height);
            if (atlasSlot != null) {
               return new PalettedTextureManager.Slot(atlas, atlas.location, atlasSlot.x(), atlasSlot.y(), atlasSlot.width(), atlasSlot.height());
            }
         }

         PalettedTextureManager.AtlasTexture atlas = new PalettedTextureManager.AtlasTexture(Identifier.withDefaultNamespace("paletted/atlas_" + this.atlasTextures.size()));
         this.textureManager.register(atlas.location, atlas);
         this.atlasTextures.add(atlas);
         DynamicAtlasTreeSlot atlasSlot = atlas.tryAllocateSlot(width, height);
         if (atlasSlot == null) {
            throw new IllegalStateException("Could not allocate slot in fresh atlas for sprite with size " + width + "x" + height);
         } else {
            return new PalettedTextureManager.Slot(atlas, atlas.location, atlasSlot.x(), atlasSlot.y(), atlasSlot.width(), atlasSlot.height());
         }
      } else {
         PalettedTextureManager.OverflowTexture texture = new PalettedTextureManager.OverflowTexture(Identifier.withDefaultNamespace("paletted/overflow_" + this.overflowTextures.size()), width, height);
         this.textureManager.register(texture.location, texture);
         this.overflowTextures.add(texture);
         return new PalettedTextureManager.Slot(texture, texture.location, 0, 0, width, height);
      }
   }

   public void close() {
      for(PalettedTextureManager.AtlasTexture atlas : this.atlasTextures) {
         this.textureManager.release(atlas.location);
      }

      for(PalettedTextureManager.OverflowTexture texture : this.overflowTextures) {
         this.textureManager.release(texture.location);
      }

      this.atlasTextures.clear();
      this.overflowTextures.clear();
      this.handles.clear();
      this.paletteMappings = (var0, var1) -> PaletteMapping.NONE;
      this.baseTextureCache.invalidateAll();
   }

   public CompletableFuture reload(final PreparableReloadListener.SharedState currentReload, final Executor taskExecutor, final PreparableReloadListener.PreparationBarrier preparationBarrier, final Executor reloadExecutor) {
      return Palette.listAndLoad(currentReload.resourceManager(), taskExecutor).thenCompose(preparationBarrier::wait).thenAcceptAsync((palettes) -> {
         this.close();
         PaletteMappingCache paletteMappings = new PaletteMappingCache(palettes);
         this.paletteMappings = paletteMappings::get;
      }, reloadExecutor);
   }

   private static class AtlasTexture extends AbstractTexture implements Dumpable {
      private static final int SIZE = 512;
      private final Identifier location;
      private final DynamicAtlasTree tree = new DynamicAtlasTree(0, 0, 512, 512);

      private AtlasTexture(final Identifier location) {
         GpuDevice device = RenderSystem.getDevice();
         this.texture = device.createTexture(() -> "Paletted Atlas", 7, GpuFormat.RGBA8_UNORM, 512, 512, 1, 1);
         this.textureView = device.createTextureView(this.texture);
         this.location = location;
         this.sampler = RenderSystem.getSamplerCache().getRepeat(FilterMode.NEAREST);
      }

      public @Nullable DynamicAtlasTreeSlot tryAllocateSlot(final int width, final int height) {
         return this.tree.insert(width, height, 0);
      }

      public void dumpContents(final Identifier selfId, final Path dir) {
         if (this.texture != null) {
            String outputId = selfId.toDebugFileName();
            TextureUtil.writeAsPNG(dir, outputId, this.texture, 0, (argb) -> ARGB.alpha(argb) == 0 ? -16777216 : argb);
         }

      }
   }

   private static record BaseTexture(NativeImage image, @Nullable PaletteMetadataSection paletteMetadata) implements AutoCloseable {
      public void close() {
         this.image.close();
      }
   }

   public interface Handle extends UvMapping {
      Identifier textureLocation();
   }

   private static class OverflowTexture extends DynamicTexture {
      private final Identifier location;

      public OverflowTexture(final Identifier location, final int width, final int height) {
         super(location::toString, width, height, false);
         this.location = location;
      }
   }

   private static record Slot(AbstractTexture texture, Identifier textureLocation, int x, int y, int width, int height) implements PalettedTextureManager.Handle {
      public float getU(final float offset) {
         return ((float)this.x + offset * (float)this.width) / (float)this.texture.getTexture().getWidth(0);
      }

      public float getV(final float offset) {
         return ((float)this.y + offset * (float)this.height) / (float)this.texture.getTexture().getHeight(0);
      }
   }

   private static record SlotKey(Identifier baseTexture, Identifier paletteId) {
   }
}
