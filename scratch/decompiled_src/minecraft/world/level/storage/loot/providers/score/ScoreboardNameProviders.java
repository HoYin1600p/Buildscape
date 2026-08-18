package net.minecraft.world.level.storage.loot.providers.score;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class ScoreboardNameProviders {
   private static final Codec TYPED_CODEC = BuiltInRegistries.LOOT_SCORE_PROVIDER_TYPE.byNameCodec().dispatch(ScoreboardNameProvider::codec, (c) -> c);
   public static final Codec CODEC = Codec.lazyInitialized(() -> Codec.either(ContextScoreboardNameProvider.INLINE_CODEC, TYPED_CODEC).xmap(Either::unwrap, (provider) -> {
         Either var10000;
         if (provider instanceof ContextScoreboardNameProvider context) {
            var10000 = Either.left(context);
         } else {
            var10000 = Either.right(provider);
         }

         return var10000;
      }));

   public static MapCodec bootstrap(final Registry registry) {
      Registry.register(registry, "fixed", FixedScoreboardNameProvider.MAP_CODEC);
      return (MapCodec)Registry.register(registry, "context", ContextScoreboardNameProvider.MAP_CODEC);
   }
}
