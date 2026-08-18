package net.minecraft.core.component.predicates;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface DataComponentPredicate {
   Codec CODEC = Codec.dispatchedMap(DataComponentPredicate.Type.CODEC, DataComponentPredicate.Type::codec);
   StreamCodec SINGLE_STREAM_CODEC = DataComponentPredicate.Type.STREAM_CODEC.dispatch(DataComponentPredicate.Single::type, DataComponentPredicate.Type::singleStreamCodec);
   StreamCodec STREAM_CODEC = SINGLE_STREAM_CODEC.apply(ByteBufCodecs.list(64)).map((singles) -> (Map)singles.stream().collect(Collectors.toMap(DataComponentPredicate.Single::type, DataComponentPredicate.Single::predicate)), (map) -> map.entrySet().stream().map(DataComponentPredicate.Single::fromEntry).toList());

   static MapCodec singleCodec(final String name) {
      return DataComponentPredicate.Type.CODEC.dispatchMap(name, DataComponentPredicate.Single::type, DataComponentPredicate.Type::wrappedCodec);
   }

   boolean matches(DataComponentGetter components);

   public static final class AnyValueType extends DataComponentPredicate.TypeBase {
      private final AnyValue predicate;

      public AnyValueType(final AnyValue predicate) {
         super(MapCodec.unitCodec(predicate));
         this.predicate = predicate;
      }

      public AnyValue predicate() {
         return this.predicate;
      }

      public DataComponentType componentType() {
         return this.predicate.type();
      }

      public static DataComponentPredicate.AnyValueType create(final DataComponentType componentType) {
         return new DataComponentPredicate.AnyValueType(new AnyValue(componentType));
      }
   }

   public static final class ConcreteType extends DataComponentPredicate.TypeBase {
      public ConcreteType(final Codec codec) {
         super(codec);
      }
   }

   public static record Single(DataComponentPredicate.Type type, DataComponentPredicate predicate) {
      private static MapCodec wrapCodec(final DataComponentPredicate.Type type, final Codec codec) {
         return RecordCodecBuilder.mapCodec((i) -> i.group(codec.fieldOf("value").forGetter(DataComponentPredicate.Single::predicate)).apply(i, (predicate) -> new DataComponentPredicate.Single(type, predicate)));
      }

      private static DataComponentPredicate.Single fromEntry(final Map.Entry e) {
         return new DataComponentPredicate.Single((DataComponentPredicate.Type)e.getKey(), (DataComponentPredicate)e.getValue());
      }
   }

   public interface Type {
      Codec CODEC = Codec.either(BuiltInRegistries.DATA_COMPONENT_PREDICATE_TYPE.byNameCodec(), BuiltInRegistries.DATA_COMPONENT_TYPE.byNameCodec()).xmap(DataComponentPredicate.Type::copyOrCreateType, DataComponentPredicate.Type::unpackType);
      StreamCodec STREAM_CODEC = ByteBufCodecs.either(ByteBufCodecs.registry(Registries.DATA_COMPONENT_PREDICATE_TYPE), ByteBufCodecs.registry(Registries.DATA_COMPONENT_TYPE)).map(DataComponentPredicate.Type::copyOrCreateType, DataComponentPredicate.Type::unpackType);

      private static Either unpackType(final DataComponentPredicate.Type type) {
         Either var10000;
         if (type instanceof DataComponentPredicate.AnyValueType anyCheck) {
            var10000 = Either.right(anyCheck.componentType());
         } else {
            var10000 = Either.left(type);
         }

         return var10000;
      }

      private static DataComponentPredicate.Type copyOrCreateType(final Either concreteTypeOrComponent) {
         return (DataComponentPredicate.Type)concreteTypeOrComponent.map((concrete) -> concrete, DataComponentPredicate.AnyValueType::create);
      }

      Codec codec();

      MapCodec wrappedCodec();

      StreamCodec singleStreamCodec();
   }

   public abstract static class TypeBase implements DataComponentPredicate.Type {
      private final Codec codec;
      private final MapCodec wrappedCodec;
      private final StreamCodec singleStreamCodec;

      public TypeBase(final Codec codec) {
         this.codec = codec;
         this.wrappedCodec = DataComponentPredicate.Single.wrapCodec(this, codec);
         this.singleStreamCodec = ByteBufCodecs.fromCodecWithRegistries(codec).map((v) -> new DataComponentPredicate.Single(this, v), DataComponentPredicate.Single::predicate);
      }

      public Codec codec() {
         return this.codec;
      }

      public MapCodec wrappedCodec() {
         return this.wrappedCodec;
      }

      public StreamCodec singleStreamCodec() {
         return this.singleStreamCodec;
      }
   }
}
