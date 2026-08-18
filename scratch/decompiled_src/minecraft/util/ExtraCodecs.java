package net.minecraft.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.primitives.UnsignedBytes;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.BaseMapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.BitSet;
import java.util.Collections;
import java.util.HexFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;
import net.minecraft.core.HolderSet;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.mutable.MutableObject;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;

public class ExtraCodecs {
   public static final Codec JSON = converter(JsonOps.INSTANCE);
   public static final Codec JAVA = converter(JavaOps.INSTANCE);
   public static final Codec NBT = converter(NbtOps.INSTANCE);
   public static final Codec VECTOR2F = Codec.FLOAT.listOf().comapFlatMap((input) -> Util.fixedSize(input, 2).map((d) -> new Vector2f(d.get(0), d.get(1))), (vec) -> List.of(vec.x(), vec.y()));
   public static final Codec VECTOR3F = Codec.FLOAT.listOf().comapFlatMap((input) -> Util.fixedSize(input, 3).map((d) -> new Vector3f(d.get(0), d.get(1), d.get(2))), (vec) -> List.of(vec.x(), vec.y(), vec.z()));
   public static final Codec VECTOR3I = Codec.INT.listOf().comapFlatMap((input) -> Util.fixedSize(input, 3).map((d) -> new Vector3i(d.get(0), d.get(1), d.get(2))), (vec) -> List.of(vec.x(), vec.y(), vec.z()));
   public static final Codec VECTOR4F = Codec.FLOAT.listOf().comapFlatMap((input) -> Util.fixedSize(input, 4).map((d) -> new Vector4f(d.get(0), d.get(1), d.get(2), d.get(3))), (vec) -> List.of(vec.x(), vec.y(), vec.z(), vec.w()));
   public static final Codec QUATERNIONF_COMPONENTS = Codec.FLOAT.listOf().comapFlatMap((input) -> Util.fixedSize(input, 4).map((d) -> (new Quaternionf(d.get(0), d.get(1), d.get(2), d.get(3))).normalize()), (q) -> List.of(q.x(), q.y(), q.z(), q.w()));
   public static final Codec AXISANGLE4F = RecordCodecBuilder.create((i) -> i.group(Codec.FLOAT.fieldOf("angle").forGetter((o) -> o.angle), VECTOR3F.fieldOf("axis").forGetter((o) -> new Vector3f(o.x, o.y, o.z))).apply(i, AxisAngle4f::new));
   public static final Codec QUATERNIONF = Codec.withAlternative(QUATERNIONF_COMPONENTS, AXISANGLE4F.xmap(Quaternionf::new, AxisAngle4f::new));
   public static final Codec MATRIX4F = Codec.FLOAT.listOf().comapFlatMap((input) -> Util.fixedSize(input, 16).map((l) -> {
         Matrix4f result = new Matrix4f();

         for(int i = 0; i < l.size(); ++i) {
            result.setRowColumn(i >> 2, i & 3, l.get(i));
         }

         return result.determineProperties();
      }), (m) -> {
      FloatList output = new FloatArrayList(16);

      for(int i = 0; i < 16; ++i) {
         output.add(m.getRowColumn(i >> 2, i & 3));
      }

      return output;
   });
   private static final String HEX_COLOR_PREFIX = "#";
   public static final Codec RGB_COLOR_CODEC = Codec.withAlternative(Codec.INT, VECTOR3F, (v) -> ARGB.colorFromFloat(1.0F, v.x(), v.y(), v.z()));
   public static final Codec ARGB_COLOR_CODEC = Codec.withAlternative(Codec.INT, VECTOR4F, (v) -> ARGB.colorFromFloat(v.w(), v.x(), v.y(), v.z()));
   public static final Codec STRING_RGB_COLOR = Codec.withAlternative(hexColor(6).xmap(ARGB::opaque, ARGB::transparent), RGB_COLOR_CODEC);
   public static final Codec STRING_ARGB_COLOR = Codec.withAlternative(hexColor(8), ARGB_COLOR_CODEC);
   public static final Codec UNSIGNED_BYTE = Codec.BYTE.flatComapMap(UnsignedBytes::toInt, (integer) -> integer > 255 ? DataResult.error(() -> "Unsigned byte was too large: " + integer + " > 255") : DataResult.success(integer.byteValue()));
   public static final Codec NON_NEGATIVE_INT = intRangeWithMessage(0, Integer.MAX_VALUE, (n) -> "Value must be non-negative: " + n);
   public static final Codec POSITIVE_INT = intRangeWithMessage(1, Integer.MAX_VALUE, (n) -> "Value must be positive: " + n);
   public static final Codec NON_NEGATIVE_LONG = longRangeWithMessage(0L, Long.MAX_VALUE, (n) -> "Value must be non-negative: " + n);
   public static final Codec POSITIVE_LONG = longRangeWithMessage(1L, Long.MAX_VALUE, (n) -> "Value must be positive: " + n);
   public static final Codec NON_NEGATIVE_FLOAT = floatRangeMinInclusiveWithMessage(0.0F, Float.MAX_VALUE, (n) -> "Value must be non-negative: " + n);
   public static final Codec POSITIVE_FLOAT = floatRangeMinExclusiveWithMessage(0.0F, Float.MAX_VALUE, (n) -> "Value must be positive: " + n);
   public static final Codec PATTERN = Codec.STRING.comapFlatMap((pattern) -> {
      try {
         return DataResult.success(Pattern.compile(pattern));
      } catch (PatternSyntaxException var2) {
         return DataResult.error(() -> "Invalid regex pattern '" + pattern + "': " + var2.getMessage());
      }
   }, Pattern::pattern);
   public static final Codec INSTANT_ISO8601 = temporalCodec(DateTimeFormatter.ISO_INSTANT).xmap(Instant::from, Function.identity());
   public static final Codec BASE64_STRING = Codec.STRING.comapFlatMap((string) -> {
      try {
         return DataResult.success(Base64.getDecoder().decode(string));
      } catch (IllegalArgumentException var2) {
         return DataResult.error(() -> "Malformed base64 string");
      }
   }, (bytes) -> Base64.getEncoder().encodeToString(bytes));
   public static final Codec ESCAPED_STRING = Codec.STRING.comapFlatMap((str) -> DataResult.success(StringEscapeUtils.unescapeJava(str)), StringEscapeUtils::escapeJava);
   public static final Codec TAG_OR_ELEMENT_ID = Codec.STRING.comapFlatMap((name) -> name.startsWith("#") ? Identifier.read(name.substring(1)).map((id) -> new ExtraCodecs.TagOrElementLocation(id, true)) : Identifier.read(name).map((id) -> new ExtraCodecs.TagOrElementLocation(id, false)), ExtraCodecs.TagOrElementLocation::decoratedId);
   public static final Function toOptionalLong = (o) -> (OptionalLong)o.map(OptionalLong::of).orElseGet(OptionalLong::empty);
   public static final Function fromOptionalLong = (l) -> l.isPresent() ? Optional.of(l.getAsLong()) : Optional.empty();
   public static final Codec BIT_SET = Codec.LONG_STREAM.xmap((longStream) -> BitSet.valueOf(longStream.toArray()), (bitSet) -> Arrays.stream(bitSet.toLongArray()));
   public static final int MAX_PROPERTY_NAME_LENGTH = 64;
   public static final int MAX_PROPERTY_VALUE_LENGTH = 32767;
   public static final int MAX_PROPERTY_SIGNATURE_LENGTH = 1024;
   public static final int MAX_PROPERTIES = 16;
   private static final Codec PROPERTY = RecordCodecBuilder.create((i) -> i.group(Codec.sizeLimitedString(64).fieldOf("name").forGetter(Property::name), Codec.sizeLimitedString(32767).fieldOf("value").forGetter(Property::value), Codec.sizeLimitedString(1024).optionalFieldOf("signature").forGetter((property) -> Optional.ofNullable(property.signature()))).apply(i, (name, value, signature) -> new Property(name, value, (String)signature.orElse((Object)null))));
   public static final Codec PROPERTY_MAP = Codec.either(Codec.unboundedMap(Codec.STRING, Codec.STRING.listOf()).validate((map) -> map.size() > 16 ? DataResult.error(() -> "Cannot have more than 16 properties, but was " + map.size()) : DataResult.success(map)), PROPERTY.sizeLimitedListOf(16)).xmap((mapListEither) -> {
      ImmutableMultimap.Builder result = ImmutableMultimap.builder();
      mapListEither.ifLeft((s) -> s.forEach((name, properties) -> {
            for(String property : properties) {
               result.put(name, new Property(name, property));
            }

         })).ifRight((properties) -> {
         for(Property property : properties) {
            result.put(property.name(), property);
         }

      });
      return new PropertyMap(result.build());
   }, (propertyMap) -> Either.right(propertyMap.values().stream().toList()));
   public static final Codec PLAYER_NAME = Codec.string(0, 16).validate((name) -> StringUtil.isValidPlayerName(name) ? DataResult.success(name) : DataResult.error(() -> "Player name contained disallowed characters: '" + name + "'"));
   public static final Codec AUTHLIB_GAME_PROFILE = gameProfileCodec(UUIDUtil.AUTHLIB_CODEC).codec();
   public static final MapCodec STORED_GAME_PROFILE = gameProfileCodec(UUIDUtil.CODEC);
   public static final Codec NON_EMPTY_STRING = Codec.STRING.validate((value) -> value.isEmpty() ? DataResult.error(() -> "Expected non-empty string") : DataResult.success(value));
   public static final Codec CODEPOINT = Codec.STRING.comapFlatMap((s) -> {
      int[] codepoint = s.codePoints().toArray();
      return codepoint.length != 1 ? DataResult.error(() -> "Expected one codepoint, got: " + s) : DataResult.success(codepoint[0]);
   }, Character::toString);
   public static final Codec RESOURCE_PATH_CODEC = Codec.STRING.validate((s) -> !Identifier.isValidPath(s) ? DataResult.error(() -> "Invalid string to use as a resource path element: " + s) : DataResult.success(s));
   public static final Codec UNTRUSTED_URI = Codec.STRING.comapFlatMap((string) -> {
      try {
         return DataResult.success(Util.parseAndValidateUntrustedUri(string));
      } catch (URISyntaxException var2) {
         return DataResult.error(var2::getMessage);
      }
   }, URI::toString);
   public static final Codec CHAT_STRING = Codec.STRING.validate((string) -> {
      for(int i = 0; i < string.length(); ++i) {
         char c = string.charAt(i);
         if (!StringUtil.isAllowedChatCharacter(c)) {
            return DataResult.error(() -> "Disallowed chat character: '" + c + "'");
         }
      }

      return DataResult.success(string);
   });

   public static Codec converter(final DynamicOps ops) {
      return Codec.PASSTHROUGH.xmap((t) -> t.convert(ops).getValue(), (t) -> new Dynamic(ops, t));
   }

   private static Codec hexColor(final int expectedDigits) {
      long maxValue = (1L << expectedDigits * 4) - 1L;
      return Codec.STRING.comapFlatMap((string) -> {
         if (!string.startsWith("#")) {
            return DataResult.error(() -> "Hex color must begin with #");
         } else {
            int digits = string.length() - "#".length();
            if (digits != expectedDigits) {
               return DataResult.error(() -> "Hex color is wrong size, expected " + expectedDigits + " digits but got " + digits);
            } else {
               try {
                  long value = HexFormat.fromHexDigitsToLong(string, "#".length(), string.length());
                  return value >= 0L && value <= maxValue ? DataResult.success((int)value) : DataResult.error(() -> "Color value out of range: " + string);
               } catch (NumberFormatException var7) {
                  return DataResult.error(() -> "Invalid color value: " + string);
               }
            }
         }
      }, (value) -> "#" + HexFormat.of().toHexDigits((long)value.intValue(), expectedDigits));
   }

   public static Codec intervalCodec(final Codec pointCodec, final String lowerBoundName, final String upperBoundName, final BiFunction makeInterval, final Function getMin, final Function getMax) {
      Codec arrayCodec = Codec.list(pointCodec).comapFlatMap((list) -> Util.fixedSize(list, 2).flatMap((l) -> {
            Object min = (P)l.get(0);
            Object max = (P)l.get(1);
            return (DataResult)makeInterval.apply(min, max);
         }), (p) -> ImmutableList.of(getMin.apply(p), getMax.apply(p)));
      Codec objectCodec = RecordCodecBuilder.create((i) -> i.group(pointCodec.fieldOf(lowerBoundName).forGetter(Pair::getFirst), pointCodec.fieldOf(upperBoundName).forGetter(Pair::getSecond)).apply(i, Pair::of)).comapFlatMap((p) -> (DataResult)makeInterval.apply(p.getFirst(), p.getSecond()), (i) -> Pair.of(getMin.apply(i), getMax.apply(i)));
      Codec arrayOrObjectCodec = Codec.withAlternative(arrayCodec, objectCodec);
      return Codec.either(pointCodec, arrayOrObjectCodec).comapFlatMap((either) -> (DataResult)either.map((min) -> (DataResult)makeInterval.apply(min, min), DataResult::success), (p) -> {
         Object min = (P)getMin.apply(p);
         Object max = (P)getMax.apply(p);
         return Objects.equals(min, max) ? Either.left(min) : Either.right(p);
      });
   }

   public static Codec.ResultFunction orElsePartial(final Object value) {
      return new Codec.ResultFunction() {
         public DataResult apply(final DynamicOps ops, final Object input, final DataResult a) {
            MutableObject message = new MutableObject();
            Optional result = a.resultOrPartial(message::setValue);
            return result.isPresent() ? a : DataResult.error(() -> "(" + (String)message.get() + " -> using default)", Pair.of(value, input));
         }

         public DataResult coApply(final DynamicOps ops, final Object input, final DataResult t) {
            return t;
         }

         public String toString() {
            return "OrElsePartial[" + String.valueOf(value) + "]";
         }
      };
   }

   public static Codec idResolverCodec(final ToIntFunction toInt, final IntFunction fromInt, final int unknownId) {
      return Codec.INT.flatXmap((id) -> (DataResult)Optional.ofNullable(fromInt.apply(id)).map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Unknown element id: " + id)), (e) -> {
         int id = toInt.applyAsInt(e);
         return id == unknownId ? DataResult.error(() -> "Element with unknown id: " + String.valueOf(e)) : DataResult.success(id);
      });
   }

   public static Codec idResolverCodec(final Codec value, final Function fromId, final Function toId) {
      return value.flatXmap((id) -> {
         Object element = (E)fromId.apply(id);
         return element == null ? DataResult.error(() -> "Unknown element id: " + String.valueOf(id)) : DataResult.success(element);
      }, (e) -> {
         Object id = (I)toId.apply(e);
         return id == null ? DataResult.error(() -> "Element with unknown id: " + String.valueOf(e)) : DataResult.success(id);
      });
   }

   public static Codec orCompressed(final Codec normal, final Codec compressed) {
      return new Codec() {
         public DataResult encode(final Object input, final DynamicOps ops, final Object prefix) {
            return ops.compressMaps() ? compressed.encode(input, ops, prefix) : normal.encode(input, ops, prefix);
         }

         public DataResult decode(final DynamicOps ops, final Object input) {
            return ops.compressMaps() ? compressed.decode(ops, input) : normal.decode(ops, input);
         }

         public String toString() {
            return String.valueOf(normal) + " orCompressed " + String.valueOf(compressed);
         }
      };
   }

   public static MapCodec orCompressed(final MapCodec normal, final MapCodec compressed) {
      return new MapCodec() {
         public RecordBuilder encode(final Object input, final DynamicOps ops, final RecordBuilder prefix) {
            return ops.compressMaps() ? compressed.encode(input, ops, prefix) : normal.encode(input, ops, prefix);
         }

         public DataResult decode(final DynamicOps ops, final MapLike input) {
            return ops.compressMaps() ? compressed.decode(ops, input) : normal.decode(ops, input);
         }

         public Stream keys(final DynamicOps ops) {
            return compressed.keys(ops);
         }

         public String toString() {
            return String.valueOf(normal) + " orCompressed " + String.valueOf(compressed);
         }
      };
   }

   public static Codec overrideLifecycle(final Codec codec, final Function decodeLifecycle, final Function encodeLifecycle) {
      return codec.mapResult(new Codec.ResultFunction() {
         public DataResult apply(final DynamicOps ops, final Object input, final DataResult a) {
            return (DataResult)a.result().map((r) -> a.setLifecycle((Lifecycle)decodeLifecycle.apply(r.getFirst()))).orElse(a);
         }

         public DataResult coApply(final DynamicOps ops, final Object input, final DataResult t) {
            return t.setLifecycle((Lifecycle)encodeLifecycle.apply(input));
         }

         public String toString() {
            return "WithLifecycle[" + String.valueOf(decodeLifecycle) + " " + String.valueOf(encodeLifecycle) + "]";
         }
      });
   }

   public static Codec overrideLifecycle(final Codec codec, final Function lifecycleGetter) {
      return overrideLifecycle(codec, lifecycleGetter, lifecycleGetter);
   }

   public static ExtraCodecs.StrictUnboundedMapCodec strictUnboundedMap(final Codec keyCodec, final Codec elementCodec) {
      return new ExtraCodecs.StrictUnboundedMapCodec(keyCodec, elementCodec);
   }

   public static Codec compactListCodec(final Codec elementCodec) {
      return compactListCodec(elementCodec, elementCodec.listOf());
   }

   public static Codec compactListCodec(final Codec elementCodec, final Codec listCodec) {
      return Codec.either(listCodec, elementCodec).xmap((e) -> (List)e.map((l) -> l, List::of), (v) -> v.size() == 1 ? Either.right(v.getFirst()) : Either.left(v));
   }

   private static Codec intRangeWithMessage(final int minInclusive, final int maxInclusive, final Function error) {
      return Codec.INT.validate((value) -> value.compareTo(minInclusive) >= 0 && value.compareTo(maxInclusive) <= 0 ? DataResult.success(value) : DataResult.error(() -> (String)error.apply(value)));
   }

   public static Codec intRange(final int minInclusive, final int maxInclusive) {
      return intRangeWithMessage(minInclusive, maxInclusive, (n) -> "Value must be within range [" + minInclusive + ";" + maxInclusive + "]: " + n);
   }

   private static Codec longRangeWithMessage(final long minInclusive, final long maxInclusive, final Function error) {
      return Codec.LONG.validate((value) -> (long)value.compareTo(minInclusive) >= 0L && (long)value.compareTo(maxInclusive) <= 0L ? DataResult.success(value) : DataResult.error(() -> (String)error.apply(value)));
   }

   public static Codec longRange(final int minInclusive, final int maxInclusive) {
      return longRangeWithMessage((long)minInclusive, (long)maxInclusive, (n) -> "Value must be within range [" + minInclusive + ";" + maxInclusive + "]: " + n);
   }

   private static Codec floatRangeMinInclusiveWithMessage(final float minInclusive, final float maxInclusive, final Function error) {
      return Codec.FLOAT.validate((value) -> value.compareTo(minInclusive) >= 0 && value.compareTo(maxInclusive) <= 0 ? DataResult.success(value) : DataResult.error(() -> (String)error.apply(value)));
   }

   private static Codec floatRangeMinExclusiveWithMessage(final float minExclusive, final float maxInclusive, final Function error) {
      return Codec.FLOAT.validate((value) -> value.compareTo(minExclusive) > 0 && value.compareTo(maxInclusive) <= 0 ? DataResult.success(value) : DataResult.error(() -> (String)error.apply(value)));
   }

   public static Codec floatRange(final float minInclusive, final float maxInclusive) {
      return floatRangeMinInclusiveWithMessage(minInclusive, maxInclusive, (n) -> "Value must be within range [" + minInclusive + ";" + maxInclusive + "]: " + n);
   }

   public static Codec nonEmptyList(final Codec listCodec) {
      return listCodec.validate((list) -> list.isEmpty() ? DataResult.error(() -> "List must have contents") : DataResult.success(list));
   }

   public static Codec nonEmptyHolderSet(final Codec listCodec) {
      return listCodec.validate((list) -> list.unwrap().right().filter(List::isEmpty).isPresent() ? DataResult.error(() -> "List must have contents") : DataResult.success(list));
   }

   public static Codec nonEmptyMap(final Codec mapCodec) {
      return mapCodec.validate((map) -> map.isEmpty() ? DataResult.error(() -> "Map must have contents") : DataResult.success(map));
   }

   public static MapCodec retrieveContext(final Function getter) {
      class ContextRetrievalCodec extends MapCodec {
         public RecordBuilder encode(final Object input, final DynamicOps ops, final RecordBuilder prefix) {
            return prefix;
         }

         public DataResult decode(final DynamicOps ops, final MapLike input) {
            return (DataResult)getter.apply(ops);
         }

         public String toString() {
            return "ContextRetrievalCodec[" + String.valueOf(getter) + "]";
         }

         public Stream keys(final DynamicOps ops) {
            return Stream.empty();
         }
      }

      return new ContextRetrievalCodec();
   }

   public static Function ensureHomogenous(final Function typeGetter) {
      return (container) -> {
         Iterator it = container.iterator();
         if (it.hasNext()) {
            Object firstType = (T)typeGetter.apply(it.next());

            while(it.hasNext()) {
               Object next = (E)it.next();
               Object nextType = (T)typeGetter.apply(next);
               if (nextType != firstType) {
                  return DataResult.error(() -> "Mixed type list: element " + String.valueOf(next) + " had type " + String.valueOf(nextType) + ", but list is of type " + String.valueOf(firstType));
               }
            }
         }

         return DataResult.success(container, Lifecycle.stable());
      };
   }

   public static Codec catchDecoderException(final Codec codec) {
      return Codec.of(codec, new Decoder() {
         public DataResult decode(final DynamicOps ops, final Object input) {
            try {
               return codec.decode(ops, input);
            } catch (Exception var4) {
               return DataResult.error(() -> "Caught exception decoding " + String.valueOf(input) + ": " + var4.getMessage());
            }
         }
      });
   }

   public static Codec temporalCodec(final DateTimeFormatter formatter) {
      return Codec.STRING.comapFlatMap((s) -> {
         try {
            return DataResult.success(formatter.parse(s));
         } catch (Exception var3) {
            return DataResult.error(var3::getMessage);
         }
      }, formatter::format);
   }

   public static MapCodec asOptionalLong(final MapCodec fieldCodec) {
      return fieldCodec.xmap(toOptionalLong, fromOptionalLong);
   }

   private static MapCodec gameProfileCodec(final Codec uuidCodec) {
      return RecordCodecBuilder.mapCodec((i) -> i.group(uuidCodec.fieldOf("id").forGetter(GameProfile::id), PLAYER_NAME.fieldOf("name").forGetter(GameProfile::name), PROPERTY_MAP.optionalFieldOf("properties", PropertyMap.EMPTY).forGetter(GameProfile::properties)).apply(i, GameProfile::new));
   }

   public static Codec sizeLimitedMap(final Codec codec, final int maxSizeInclusive) {
      return codec.validate((map) -> map.size() > maxSizeInclusive ? DataResult.error(() -> "Map is too long: " + map.size() + ", expected range [0-" + maxSizeInclusive + "]") : DataResult.success(map));
   }

   public static Codec object2BooleanMap(final Codec keyCodec) {
      return Codec.unboundedMap(keyCodec, Codec.BOOL).xmap(Object2BooleanOpenHashMap::new, Object2ObjectOpenHashMap::new);
   }

   /** @deprecated */
   @Deprecated
   public static MapCodec dispatchOptionalValue(final String typeKey, final String valueKey, final Codec typeCodec, final Function typeGetter, final Function valueCodec) {
      return new MapCodec() {
         public Stream keys(final DynamicOps ops) {
            return Stream.of(ops.createString(typeKey), ops.createString(valueKey));
         }

         public DataResult decode(final DynamicOps ops, final MapLike input) {
            Object typeName = (T)input.get(typeKey);
            return typeName == null ? DataResult.error(() -> "Missing \"" + typeKey + "\" in: " + String.valueOf(input)) : typeCodec.decode(ops, typeName).flatMap((type) -> {
               Object value = (T)Objects.requireNonNullElseGet(input.get(valueKey), ops::emptyMap);
               return ((Codec)valueCodec.apply(type.getFirst())).decode(ops, value).map(Pair::getFirst);
            });
         }

         public RecordBuilder encode(final Object input, final DynamicOps ops, final RecordBuilder builder) {
            Object type = (K)typeGetter.apply(input);
            builder.add(typeKey, typeCodec.encodeStart(ops, type));
            DataResult parameters = this.encode((Codec)valueCodec.apply(type), input, ops);
            if (parameters.result().isEmpty() || !Objects.equals(parameters.result().get(), ops.emptyMap())) {
               builder.add(valueKey, parameters);
            }

            return builder;
         }

         private DataResult encode(final Codec codec, final Object input, final DynamicOps ops) {
            return codec.encodeStart(ops, input);
         }
      };
   }

   public static Codec optionalEmptyMap(final Codec codec) {
      return new Codec() {
         public DataResult decode(final DynamicOps ops, final Object input) {
            return isEmptyMap(ops, input) ? DataResult.success(Pair.of(Optional.empty(), input)) : codec.decode(ops, input).map((pair) -> pair.mapFirst(Optional::of));
         }

         private static boolean isEmptyMap(final DynamicOps ops, final Object input) {
            Optional map = ops.getMap(input).result();
            return map.isPresent() && ((MapLike)map.get()).entries().findAny().isEmpty();
         }

         public DataResult encode(final Optional input, final DynamicOps ops, final Object prefix) {
            return input.isEmpty() ? DataResult.success(ops.emptyMap()) : codec.encode(input.get(), ops, prefix);
         }
      };
   }

   /** @deprecated */
   @Deprecated
   public static Codec legacyEnum(final Function valueOf) {
      return Codec.STRING.comapFlatMap((key) -> {
         try {
            return DataResult.success((Enum)valueOf.apply(key));
         } catch (IllegalArgumentException var3) {
            return DataResult.error(() -> "No value with id: " + key);
         }
      }, Enum::toString);
   }

   public static Codec pathCodec(final Function pathFactory) {
      return Codec.STRING.xmap(pathFactory, (path) -> FilenameUtils.separatorsToUnix(path.toString()));
   }

   public static Codec relaiveNormalizedSubPathCodec(final Function pathFactory) {
      return pathCodec(pathFactory).xmap(Path::normalize, Path::normalize).validate((path) -> {
         if (path.isAbsolute()) {
            return DataResult.error(() -> "Illegal absolute path: " + String.valueOf(path));
         } else {
            return !path.startsWith("..") && !path.startsWith(".") && !FileUtil.isEmptyPath(path) ? DataResult.success(path) : DataResult.error(() -> "Illegal path traversal: " + String.valueOf(path));
         }
      });
   }

   public static Codec guardedPathCodec(final Path baseFolder) {
      FileSystem var10000 = baseFolder.getFileSystem();
      Objects.requireNonNull(var10000);
      FileSystem var1 = var10000;
      return relaiveNormalizedSubPathCodec((x$0) -> var1.getPath(x$0)).xmap(baseFolder::resolve, baseFolder::relativize);
   }

   public static MapCodec optionalAlwaysPresentFieldOf(final Codec elementCodec, final String name, final Object defaultValue, final boolean lenient) {
      return Codec.optionalField(name, elementCodec, lenient).xmap((o) -> o.orElse(defaultValue), Optional::of);
   }

   public static MapCodec optionalAlwaysPresentFieldOf(final Codec elementCodec, final String name, final Object defaultValue) {
      return optionalAlwaysPresentFieldOf(elementCodec, name, defaultValue, false);
   }

   public static class LateBoundIdMapper {
      private final BiMap idToValue = HashBiMap.create();

      public Codec codec(final Codec idCodec) {
         BiMap valueToId = this.idToValue.inverse();
         return ExtraCodecs.idResolverCodec(idCodec, this.idToValue::get, valueToId::get);
      }

      public ExtraCodecs.LateBoundIdMapper put(final Object id, final Object value) {
         Objects.requireNonNull(value, () -> "Value for " + String.valueOf(id) + " is null");
         this.idToValue.put(id, value);
         return this;
      }

      public Set values() {
         return Collections.unmodifiableSet(this.idToValue.values());
      }
   }

   public static record StrictUnboundedMapCodec(Codec keyCodec, Codec elementCodec) implements BaseMapCodec, Codec {
      public DataResult decode(final DynamicOps ops, final MapLike input) {
         ImmutableMap.Builder read = ImmutableMap.builder();

         for(Pair pair : input.entries().toList()) {
            DataResult k = this.keyCodec().parse(ops, pair.getFirst());
            DataResult v = this.elementCodec().parse(ops, pair.getSecond());
            DataResult entry = k.apply2stable(Pair::of, v);
            Optional error = entry.error();
            if (error.isPresent()) {
               String errorMessage = ((DataResult.Error)error.get()).message();
               return DataResult.error(() -> k.result().isPresent() ? "Map entry '" + String.valueOf(k.result().get()) + "' : " + errorMessage : errorMessage);
            }

            if (!entry.result().isPresent()) {
               return DataResult.error(() -> "Empty or invalid map contents are not allowed");
            }

            Pair kvPair = (Pair)entry.result().get();
            read.put(kvPair.getFirst(), kvPair.getSecond());
         }

         Map elements = read.build();
         return DataResult.success(elements);
      }

      public DataResult decode(final DynamicOps ops, final Object input) {
         return ops.getMap(input).setLifecycle(Lifecycle.stable()).flatMap((map) -> this.decode(ops, map)).map((r) -> Pair.of(r, input));
      }

      public DataResult encode(final Map input, final DynamicOps ops, final Object prefix) {
         return this.encode(input, ops, ops.mapBuilder()).build(prefix);
      }

      public String toString() {
         return "StrictUnboundedMapCodec[" + String.valueOf(this.keyCodec) + " -> " + String.valueOf(this.elementCodec) + "]";
      }
   }

   public static record TagOrElementLocation(Identifier id, boolean tag) {
      public String toString() {
         return this.decoratedId();
      }

      private String decoratedId() {
         return this.tag ? "#" + String.valueOf(this.id) : this.id.toString();
      }
   }
}
