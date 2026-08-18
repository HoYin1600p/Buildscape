package net.minecraft.data.info;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import org.slf4j.Logger;

public class BiomeParametersDumpReport implements DataProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Path topPath;
   private final CompletableFuture registries;
   private static final MapCodec ENTRY_CODEC = ResourceKey.codec(Registries.BIOME).fieldOf("biome");
   private static final Codec CODEC = Climate.ParameterList.codec(ENTRY_CODEC).fieldOf("biomes").codec();

   public BiomeParametersDumpReport(final PackOutput output, final CompletableFuture registries) {
      this.topPath = output.getOutputFolder(PackOutput.Target.REPORTS).resolve("biome_parameters");
      this.registries = registries;
   }

   public CompletableFuture run(final CachedOutput cache) {
      return this.registries.thenCompose((registryAccess) -> {
         DynamicOps registryOps = registryAccess.createSerializationContext(JsonOps.INSTANCE);
         List result = new ArrayList();
         MultiNoiseBiomeSourceParameterList.knownPresets().forEach((preset, parameterList) -> result.add(dumpValue(this.createPath(preset.id()), cache, registryOps, CODEC, parameterList)));
         return CompletableFuture.allOf((CompletableFuture[])result.toArray((x$0) -> new CompletableFuture[x$0]));
      });
   }

   private static CompletableFuture dumpValue(final Path path, final CachedOutput cache, final DynamicOps ops, final Encoder codec, final Object value) {
      Optional result = codec.encodeStart(ops, value).resultOrPartial((e) -> LOGGER.error("Couldn't serialize element {}: {}", path, e));
      return result.isPresent() ? DataProvider.saveStable(cache, (JsonElement)result.get(), path) : CompletableFuture.completedFuture((Object)null);
   }

   private Path createPath(final Identifier element) {
      return element.withSuffix(".json").resolveAgainst(this.topPath);
   }

   public final String getName() {
      return "Biome Parameters";
   }
}
