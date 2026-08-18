package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;

public interface StringRepresentable {
   int PRE_BUILT_MAP_THRESHOLD = 16;

   String getSerializedName();

   static StringRepresentable.EnumCodec fromEnum(final Supplier values) {
      return fromEnumWithMapping(values, (s) -> s);
   }

   static StringRepresentable.EnumCodec fromEnumWithMapping(final Supplier values, final Function converter) {
      Enum[] valueArray = (E[])((Enum[])values.get());
      Function lookupFunction = createNameLookup(valueArray, (e) -> (String)converter.apply(((StringRepresentable)e).getSerializedName()));
      return new StringRepresentable.EnumCodec(valueArray, lookupFunction);
   }

   static Codec fromValues(final Supplier values) {
      StringRepresentable[] valueArray = (T[])((StringRepresentable[])values.get());
      Function lookupFunction = createNameLookup(valueArray);
      ToIntFunction indexLookup = Util.createIndexLookup(Arrays.asList(valueArray));
      return new StringRepresentable.StringRepresentableCodec(valueArray, lookupFunction, indexLookup);
   }

   static Function createNameLookup(final StringRepresentable[] valueArray) {
      return createNameLookup(valueArray, StringRepresentable::getSerializedName);
   }

   static Function createNameLookup(final Object[] valueArray, final Function converter) {
      if (valueArray.length > 16) {
         Map byName = (Map)Arrays.stream(valueArray).collect(Collectors.toMap(converter, (d) -> d));
         return byName::get;
      } else {
         return (id) -> {
            for(Object value : valueArray) {
               if (((String)converter.apply(value)).equals(id)) {
                  return value;
               }
            }

            return null;
         };
      }
   }

   static Keyable keys(final StringRepresentable[] values) {
      return new Keyable() {
         public Stream keys(final DynamicOps ops) {
            return Arrays.stream(values).map(StringRepresentable::getSerializedName).map(ops::createString);
         }
      };
   }

   public static class EnumCodec extends StringRepresentable.StringRepresentableCodec {
      private final Function resolver;

      public EnumCodec(final Enum[] valueArray, final Function nameResolver) {
         super(valueArray, nameResolver, (rec$) -> rec$.ordinal());
         this.resolver = nameResolver;
      }

      public @Nullable Enum byName(final String name) {
         return (Enum)this.resolver.apply(name);
      }

      public Enum byName(final String name, final Enum _default) {
         return (Enum)Objects.requireNonNullElse(this.byName(name), _default);
      }

      public Enum byName(final String name, final Supplier defaultSupplier) {
         return (Enum)Objects.requireNonNullElseGet(this.byName(name), defaultSupplier);
      }
   }

   public static class StringRepresentableCodec implements Codec {
      private final Codec codec;

      public StringRepresentableCodec(final StringRepresentable[] valueArray, final Function nameResolver, final ToIntFunction idResolver) {
         this.codec = ExtraCodecs.orCompressed(Codec.stringResolver(StringRepresentable::getSerializedName, nameResolver), ExtraCodecs.idResolverCodec(idResolver, (i) -> i >= 0 && i < valueArray.length ? valueArray[i] : null, -1));
      }

      public DataResult decode(final DynamicOps ops, final Object input) {
         return this.codec.decode(ops, input);
      }

      public DataResult encode(final StringRepresentable input, final DynamicOps ops, final Object prefix) {
         return this.codec.encode(input, ops, prefix);
      }
   }
}
