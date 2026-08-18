package net.minecraft.network.chat;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapEncoder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.network.chat.contents.KeybindContents;
import net.minecraft.network.chat.contents.NbtContents;
import net.minecraft.network.chat.contents.ObjectContents;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.ScoreContents;
import net.minecraft.network.chat.contents.SelectorContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;

public class ComponentSerialization {
   public static final Codec CODEC = Codec.recursive("Component", ComponentSerialization::createCodec);
   public static final StreamCodec STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);
   public static final StreamCodec OPTIONAL_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs::optional);
   public static final StreamCodec TRUSTED_STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistriesTrusted(CODEC);
   public static final StreamCodec TRUSTED_OPTIONAL_STREAM_CODEC = TRUSTED_STREAM_CODEC.apply(ByteBufCodecs::optional);
   public static final StreamCodec TRUSTED_CONTEXT_FREE_STREAM_CODEC = ByteBufCodecs.fromCodecTrusted(CODEC);

   public static Codec flatRestrictedCodec(final int maxFlatSize) {
      return new Codec() {
         public DataResult decode(final DynamicOps ops, final Object input) {
            return ComponentSerialization.CODEC.decode(ops, input).flatMap((pair) -> this.isTooLarge(ops, (Component)pair.getFirst()) ? DataResult.error(() -> "Component was too large: greater than max size " + maxFlatSize) : DataResult.success(pair));
         }

         public DataResult encode(final Component input, final DynamicOps ops, final Object prefix) {
            return ComponentSerialization.CODEC.encodeStart(ops, input);
         }

         private boolean isTooLarge(final DynamicOps ops, final Component input) {
            DataResult json = ComponentSerialization.CODEC.encodeStart(asJsonOps(ops), input);
            return json.isSuccess() && GsonHelper.encodesLongerThan((JsonElement)json.getOrThrow(), maxFlatSize);
         }

         private static DynamicOps asJsonOps(final DynamicOps ops) {
            if (ops instanceof RegistryOps registryOps) {
               return registryOps.withParent(JsonOps.INSTANCE);
            } else {
               return JsonOps.INSTANCE;
            }
         }
      };
   }

   private static MutableComponent createFromList(final List list) {
      MutableComponent result = ((Component)list.get(0)).copy();

      for(int i = 1; i < list.size(); ++i) {
         result.append((Component)list.get(i));
      }

      return result;
   }

   public static MapCodec createLegacyComponentMatcher(final ExtraCodecs.LateBoundIdMapper types, final Function codecGetter, final String typeFieldName) {
      MapCodec compactCodec = new ComponentSerialization.FuzzyCodec(types.values(), codecGetter);
      MapCodec discriminatorCodec = types.codec(Codec.STRING).dispatchMap(typeFieldName, codecGetter, (c) -> c);
      MapCodec contentsCodec = new ComponentSerialization.StrictEither(typeFieldName, discriminatorCodec, compactCodec);
      return ExtraCodecs.orCompressed(contentsCodec, discriminatorCodec);
   }

   private static Codec createCodec(final Codec topSerializer) {
      ExtraCodecs.LateBoundIdMapper contentTypes = new ExtraCodecs.LateBoundIdMapper();
      bootstrap(contentTypes);
      MapCodec compressedContentsCodec = createLegacyComponentMatcher(contentTypes, ComponentContents::codec, "type");
      Codec fullCodec = RecordCodecBuilder.create((i) -> i.group(compressedContentsCodec.forGetter(Component::getContents), ExtraCodecs.nonEmptyList(topSerializer.listOf()).optionalFieldOf("extra", List.of()).forGetter(Component::getSiblings), Style.Serializer.MAP_CODEC.forGetter(Component::getStyle)).apply(i, MutableComponent::new));
      return Codec.either(Codec.either(Codec.STRING, ExtraCodecs.nonEmptyList(topSerializer.listOf())), fullCodec).xmap((specialOrComponent) -> (Component)specialOrComponent.map((special) -> (Component)special.map(Component::literal, ComponentSerialization::createFromList), (c) -> c), (component) -> {
         String text = component.tryCollapseToString();
         return text != null ? Either.left(Either.left(text)) : Either.right(component);
      });
   }

   private static void bootstrap(final ExtraCodecs.LateBoundIdMapper contentTypes) {
      contentTypes.put("text", PlainTextContents.MAP_CODEC);
      contentTypes.put("translatable", TranslatableContents.MAP_CODEC);
      contentTypes.put("keybind", KeybindContents.MAP_CODEC);
      contentTypes.put("score", ScoreContents.MAP_CODEC);
      contentTypes.put("selector", SelectorContents.MAP_CODEC);
      contentTypes.put("nbt", NbtContents.MAP_CODEC);
      contentTypes.put("object", ObjectContents.MAP_CODEC);
   }

   private static class FuzzyCodec extends MapCodec {
      private final Collection codecs;
      private final Function encoderGetter;

      public FuzzyCodec(final Collection codecs, final Function encoderGetter) {
         this.codecs = codecs;
         this.encoderGetter = encoderGetter;
      }

      public DataResult decode(final DynamicOps ops, final MapLike input) {
         for(MapDecoder codec : this.codecs) {
            DataResult result = codec.decode(ops, input);
            if (result.result().isPresent()) {
               return result;
            }
         }

         return DataResult.error(() -> "No matching codec found");
      }

      public RecordBuilder encode(final Object input, final DynamicOps ops, final RecordBuilder prefix) {
         MapEncoder encoder = (MapEncoder)this.encoderGetter.apply(input);
         return encoder.encode(input, ops, prefix);
      }

      public Stream keys(final DynamicOps ops) {
         return this.codecs.stream().flatMap((c) -> c.keys(ops)).distinct();
      }

      public String toString() {
         return "FuzzyCodec[" + String.valueOf(this.codecs) + "]";
      }
   }

   private static class StrictEither extends MapCodec {
      private final String typeFieldName;
      private final MapCodec typed;
      private final MapCodec fuzzy;

      public StrictEither(final String typeFieldName, final MapCodec typed, final MapCodec fuzzy) {
         this.typeFieldName = typeFieldName;
         this.typed = typed;
         this.fuzzy = fuzzy;
      }

      public DataResult decode(final DynamicOps ops, final MapLike input) {
         return input.get(this.typeFieldName) != null ? this.typed.decode(ops, input) : this.fuzzy.decode(ops, input);
      }

      public RecordBuilder encode(final Object input, final DynamicOps ops, final RecordBuilder prefix) {
         return this.fuzzy.encode(input, ops, prefix);
      }

      public Stream keys(final DynamicOps ops) {
         return Stream.concat(this.typed.keys(ops), this.fuzzy.keys(ops)).distinct();
      }
   }
}
