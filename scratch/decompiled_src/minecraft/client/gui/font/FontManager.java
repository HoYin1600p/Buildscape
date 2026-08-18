package net.minecraft.client.gui.font;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GlyphSource;
import net.minecraft.client.gui.font.glyphs.EffectGlyph;
import net.minecraft.client.gui.font.providers.GlyphProviderDefinition;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.DependencySorter;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class FontManager implements AutoCloseable, PreparableReloadListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String FONTS_PATH = "fonts.json";
   public static final Identifier MISSING_FONT = Identifier.withDefaultNamespace("missing");
   private static final FileToIdConverter FONT_DEFINITIONS = FileToIdConverter.json("font");
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
   private final FontSet missingFontSet;
   private final List providersToClose = new ArrayList();
   private final Map fontSets = new HashMap();
   private final TextureManager textureManager;
   private final FontManager.CachedFontProvider anyGlyphs = new FontManager.CachedFontProvider(false);
   private final FontManager.CachedFontProvider nonFishyGlyphs = new FontManager.CachedFontProvider(true);
   private final AtlasManager atlasManager;
   private final Map atlasProviders = new HashMap();
   private final PlayerGlyphProvider playerProvider;

   public FontManager(final TextureManager textureManager, final AtlasManager atlasManager, final PlayerSkinRenderCache playerSkinRenderCache) {
      this.textureManager = textureManager;
      this.atlasManager = atlasManager;
      this.missingFontSet = this.createFontSet(MISSING_FONT, List.of(createFallbackProvider()), Set.of());
      this.playerProvider = new PlayerGlyphProvider(playerSkinRenderCache);
   }

   private FontSet createFontSet(final Identifier id, final List providers, final Set options) {
      GlyphStitcher stitcher = new GlyphStitcher(this.textureManager, id);
      FontSet result = new FontSet(stitcher);
      result.reload(providers, options);
      return result;
   }

   private static GlyphProvider.Conditional createFallbackProvider() {
      return new GlyphProvider.Conditional(new AllMissingGlyphProvider(), FontOption.Filter.ALWAYS_PASS);
   }

   public CompletableFuture reload(final PreparableReloadListener.SharedState currentReload, final Executor taskExecutor, final PreparableReloadListener.PreparationBarrier preparationBarrier, final Executor reloadExecutor) {
      return this.prepare(currentReload.resourceManager(), taskExecutor).thenCompose(preparationBarrier::wait).thenAcceptAsync((preparations) -> this.apply(preparations, Profiler.get()), reloadExecutor);
   }

   private CompletableFuture prepare(final ResourceManager manager, final Executor executor) {
      List builderFutures = new ArrayList();

      for(Map.Entry fontStack : FONT_DEFINITIONS.listMatchingResourceStacks(manager).entrySet()) {
         Identifier fontName = FONT_DEFINITIONS.fileToId((Identifier)fontStack.getKey());
         builderFutures.add(CompletableFuture.supplyAsync(() -> {
            List builderStack = loadResourceStack((List)fontStack.getValue(), fontName);
            FontManager.UnresolvedBuilderBundle bundle = new FontManager.UnresolvedBuilderBundle(fontName);

            for(Pair stackEntry : builderStack) {
               FontManager.BuilderId id = (FontManager.BuilderId)stackEntry.getFirst();
               FontOption.Filter options = ((GlyphProviderDefinition.Conditional)stackEntry.getSecond()).filter();
               ((GlyphProviderDefinition.Conditional)stackEntry.getSecond()).definition().unpack().ifLeft((provider) -> {
                  CompletableFuture loadResult = this.safeLoad(id, provider, manager, executor);
                  bundle.add(id, options, loadResult);
               }).ifRight((reference) -> bundle.add(id, options, reference));
            }

            return bundle;
         }, executor));
      }

      return Util.sequence(builderFutures).thenCompose((builders) -> {
         List allProviderFutures = (List)builders.stream().flatMap(FontManager.UnresolvedBuilderBundle::listBuilders).collect(Util.toMutableList());
         GlyphProvider.Conditional fallback = createFallbackProvider();
         allProviderFutures.add(CompletableFuture.completedFuture(Optional.of(fallback.provider())));
         return Util.sequence(allProviderFutures).thenCompose((allProviders) -> {
            Map resolved = this.resolveProviders(builders);
            CompletableFuture[] finalizers = (CompletableFuture[])resolved.values().stream().map((providers) -> CompletableFuture.runAsync(() -> this.finalizeProviderLoading(providers, fallback), executor)).toArray((x$0) -> new CompletableFuture[x$0]);
            return CompletableFuture.allOf(finalizers).thenApply((ignored) -> {
               List providersToClose = allProviders.stream().flatMap(Optional::stream).toList();
               return new FontManager.Preparation(resolved, providersToClose);
            });
         });
      });
   }

   private CompletableFuture safeLoad(final FontManager.BuilderId id, final GlyphProviderDefinition.Loader provider, final ResourceManager manager, final Executor executor) {
      return CompletableFuture.supplyAsync(() -> {
         try {
            return Optional.of(provider.load(manager));
         } catch (Exception var4) {
            LOGGER.warn("Failed to load builder {}, rejecting", id, var4);
            return Optional.empty();
         }
      }, executor);
   }

   private Map resolveProviders(final List unresolvedProviders) {
      Map result = new HashMap();
      DependencySorter sorter = new DependencySorter();
      unresolvedProviders.forEach((e) -> sorter.addEntry(e.fontId, e));
      sorter.orderByDependencies((id, bundle) -> bundle.resolve(result::get).ifPresent((r) -> result.put(id, r)));
      return result;
   }

   private void finalizeProviderLoading(final List list, final GlyphProvider.Conditional fallback) {
      list.add(0, fallback);
      IntSet supportedGlyphs = new IntOpenHashSet();

      for(GlyphProvider.Conditional provider : list) {
         supportedGlyphs.addAll(provider.provider().getSupportedGlyphs());
      }

      supportedGlyphs.forEach((codepoint) -> {
         if (codepoint != 32) {
            for(GlyphProvider.Conditional provider : Lists.reverse(list)) {
               if (provider.provider().getGlyph(codepoint) != null) {
                  break;
               }
            }

         }
      });
   }

   private static Set getFontOptions(final Options options) {
      Set result = EnumSet.noneOf(FontOption.class);
      if (options.forceUnicodeFont().get()) {
         result.add(FontOption.UNIFORM);
      }

      if (options.japaneseGlyphVariants().get()) {
         result.add(FontOption.JAPANESE_VARIANTS);
      }

      return result;
   }

   private void apply(final FontManager.Preparation preparations, final ProfilerFiller profiler) {
      profiler.push("closing");
      this.anyGlyphs.invalidate();
      this.nonFishyGlyphs.invalidate();
      this.fontSets.values().forEach(FontSet::close);
      this.fontSets.clear();
      this.providersToClose.forEach(GlyphProvider::close);
      this.providersToClose.clear();
      Set fontOptions = getFontOptions(Minecraft.getInstance().options);
      profiler.popPush("reloading");
      preparations.fontSets().forEach((id, newProviders) -> this.fontSets.put(id, this.createFontSet(id, Lists.reverse(newProviders), fontOptions)));
      this.providersToClose.addAll(preparations.allProviders);
      profiler.pop();
      if (!this.fontSets.containsKey(Minecraft.DEFAULT_FONT)) {
         throw new IllegalStateException("Default font failed to load");
      } else {
         this.atlasProviders.clear();
         this.atlasManager.forEach((atlasId, atlasTexture) -> this.atlasProviders.put(atlasId, new AtlasGlyphProvider(atlasTexture)));
      }
   }

   public void updateOptions(final Options options) {
      Set fontOptions = getFontOptions(options);

      for(FontSet value : this.fontSets.values()) {
         value.reload(fontOptions);
      }

      this.anyGlyphs.invalidate();
      this.nonFishyGlyphs.invalidate();
   }

   private static List loadResourceStack(final List resourceStack, final Identifier fontName) {
      List builderStack = new ArrayList();

      for(Resource resource : resourceStack) {
         try {
            Reader reader = resource.openAsReader();

            try {
               JsonElement jsonContents = (JsonElement)GSON.fromJson(reader, JsonElement.class);
               FontManager.FontDefinitionFile definition = (FontManager.FontDefinitionFile)FontManager.FontDefinitionFile.CODEC.parse(JsonOps.INSTANCE, jsonContents).getOrThrow(JsonParseException::new);
               List providers = definition.providers;

               for(int i = providers.size() - 1; i >= 0; --i) {
                  FontManager.BuilderId id = new FontManager.BuilderId(fontName, resource.sourcePackId(), i);
                  builderStack.add(Pair.of(id, (GlyphProviderDefinition.Conditional)providers.get(i)));
               }
            } catch (Throwable var12) {
               if (reader != null) {
                  try {
                     reader.close();
                  } catch (Throwable var11) {
                     var12.addSuppressed(var11);
                  }
               }

               throw var12;
            }

            if (reader != null) {
               reader.close();
            }
         } catch (Exception var13) {
            LOGGER.warn("Unable to load font '{}' in {} in resourcepack: '{}'", new Object[]{fontName, "fonts.json", resource.sourcePackId(), var13});
         }
      }

      return builderStack;
   }

   public Font createFont() {
      return new Font(this.anyGlyphs);
   }

   public Font createFontFilterFishy() {
      return new Font(this.nonFishyGlyphs);
   }

   private FontSet getFontSetRaw(final Identifier id) {
      return (FontSet)this.fontSets.getOrDefault(id, this.missingFontSet);
   }

   private GlyphSource getSpriteFont(final FontDescription.AtlasSprite contents) {
      AtlasGlyphProvider provider = (AtlasGlyphProvider)this.atlasProviders.get(contents.atlasId());
      return provider == null ? this.missingFontSet.source(false) : provider.sourceForSprite(contents.spriteId());
   }

   public void close() {
      this.anyGlyphs.close();
      this.nonFishyGlyphs.close();
      this.fontSets.values().forEach(FontSet::close);
      this.providersToClose.forEach(GlyphProvider::close);
      this.missingFontSet.close();
   }

   private static record BuilderId(Identifier fontId, String pack, int index) {
      public String toString() {
         return "(" + String.valueOf(this.fontId) + ": builder #" + this.index + " from pack " + this.pack + ")";
      }
   }

   private static record BuilderResult(FontManager.BuilderId id, FontOption.Filter filter, Either result) {
      public Optional resolve(final Function resolver) {
         return (Optional)this.result.map((provider) -> ((Optional)provider.join()).map((p) -> List.of(new GlyphProvider.Conditional(p, this.filter))), (reference) -> {
            List resolvedReferences = (List)resolver.apply(reference);
            if (resolvedReferences == null) {
               FontManager.LOGGER.warn("Can't find font {} referenced by builder {}, either because it's missing, failed to load or is part of loading cycle", reference, this.id);
               return Optional.empty();
            } else {
               return Optional.of(resolvedReferences.stream().map(this::mergeFilters).toList());
            }
         });
      }

      private GlyphProvider.Conditional mergeFilters(final GlyphProvider.Conditional original) {
         return new GlyphProvider.Conditional(original.provider(), this.filter.merge(original.filter()));
      }
   }

   private class CachedFontProvider implements Font.Provider, AutoCloseable {
      private final boolean nonFishyOnly;
      private volatile FontManager.CachedFontProvider.@Nullable CachedEntry lastEntry;
      private volatile @Nullable EffectGlyph whiteGlyph;

      private CachedFontProvider(final boolean nonFishyOnly) {
         Objects.requireNonNull(FontManager.this);
         super();
         this.nonFishyOnly = nonFishyOnly;
      }

      public void invalidate() {
         this.lastEntry = null;
         this.whiteGlyph = null;
      }

      public void close() {
         this.invalidate();
      }

      private GlyphSource getGlyphSource(final FontDescription description) {
         Objects.requireNonNull(description);
         byte var3 = 0;
         GlyphSource var10000;
         switch (description.typeSwitch<invokedynamic>(description, var3)) {
            case 0:
               FontDescription.Resource resource = (FontDescription.Resource)description;
               var10000 = FontManager.this.getFontSetRaw(resource.id()).source(this.nonFishyOnly);
               break;
            case 1:
               FontDescription.AtlasSprite sprite = (FontDescription.AtlasSprite)description;
               var10000 = FontManager.this.getSpriteFont(sprite);
               break;
            case 2:
               FontDescription.PlayerSprite player = (FontDescription.PlayerSprite)description;
               var10000 = FontManager.this.playerProvider.sourceForPlayer(player);
               break;
            default:
               var10000 = FontManager.this.missingFontSet.source(this.nonFishyOnly);
         }

         return var10000;
      }

      public GlyphSource glyphs(final FontDescription description) {
         FontManager.CachedFontProvider.CachedEntry lastEntry = this.lastEntry;
         if (lastEntry != null && description.equals(lastEntry.description)) {
            return lastEntry.source;
         } else {
            GlyphSource result = this.getGlyphSource(description);
            this.lastEntry = new FontManager.CachedFontProvider.CachedEntry(description, result);
            return result;
         }
      }

      public EffectGlyph effect() {
         EffectGlyph whiteGlyph = this.whiteGlyph;
         if (whiteGlyph == null) {
            whiteGlyph = FontManager.this.getFontSetRaw(FontDescription.DEFAULT.id()).whiteGlyph();
            this.whiteGlyph = whiteGlyph;
         }

         return whiteGlyph;
      }

      private static record CachedEntry(FontDescription description, GlyphSource source) {
      }
   }

   private static record FontDefinitionFile(List providers) {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(GlyphProviderDefinition.Conditional.CODEC.listOf().fieldOf("providers").forGetter(FontManager.FontDefinitionFile::providers)).apply(i, FontManager.FontDefinitionFile::new));
   }

   private static record Preparation(Map fontSets, List allProviders) {
   }

   private static record UnresolvedBuilderBundle(Identifier fontId, List builders, Set dependencies) implements DependencySorter.Entry {
      public UnresolvedBuilderBundle(final Identifier fontId) {
         this(fontId, new ArrayList(), new HashSet());
      }

      public void add(final FontManager.BuilderId builderId, final FontOption.Filter filter, final GlyphProviderDefinition.Reference reference) {
         this.builders.add(new FontManager.BuilderResult(builderId, filter, Either.right(reference.id())));
         this.dependencies.add(reference.id());
      }

      public void add(final FontManager.BuilderId builderId, final FontOption.Filter filter, final CompletableFuture provider) {
         this.builders.add(new FontManager.BuilderResult(builderId, filter, Either.left(provider)));
      }

      private Stream listBuilders() {
         return this.builders.stream().flatMap((e) -> e.result.left().stream());
      }

      public Optional resolve(final Function resolver) {
         List resolved = new ArrayList();

         for(FontManager.BuilderResult builder : this.builders) {
            Optional resolvedBuilder = builder.resolve(resolver);
            if (!resolvedBuilder.isPresent()) {
               return Optional.empty();
            }

            resolved.addAll((Collection)resolvedBuilder.get());
         }

         return Optional.of(resolved);
      }

      public void visitRequiredDependencies(final Consumer output) {
         this.dependencies.forEach(output);
      }

      public void visitOptionalDependencies(final Consumer output) {
      }
   }
}
