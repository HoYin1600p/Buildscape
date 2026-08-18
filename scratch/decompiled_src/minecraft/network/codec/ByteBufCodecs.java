package net.minecraft.network.codec;

import com.google.common.collect.ImmutableMultimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.security.Key;
import java.time.Instant;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IdMap;
import net.minecraft.core.Registry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.Utf8String;
import net.minecraft.network.VarInt;
import net.minecraft.network.VarLong;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ARGB;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;
import net.minecraft.util.LenientJsonParser;
import net.minecraft.util.Mth;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;

public interface ByteBufCodecs {
   int MAX_INITIAL_COLLECTION_SIZE = 65536;
   StreamCodec BOOL = new StreamCodec() {
      public Boolean decode(final ByteBuf input) {
         return input.readBoolean();
      }

      public void encode(final ByteBuf output, final Boolean value) {
         output.writeBoolean(value);
      }
   };
   StreamCodec BYTE = new StreamCodec() {
      public Byte decode(final ByteBuf input) {
         return input.readByte();
      }

      public void encode(final ByteBuf output, final Byte value) {
         output.writeByte(value);
      }
   };
   StreamCodec ROTATION_BYTE = BYTE.map(Mth::unpackDegrees, Mth::packDegrees);
   StreamCodec SHORT = new StreamCodec() {
      public Short decode(final ByteBuf input) {
         return input.readShort();
      }

      public void encode(final ByteBuf output, final Short value) {
         output.writeShort(value);
      }
   };
   StreamCodec UNSIGNED_SHORT = new StreamCodec() {
      public Integer decode(final ByteBuf input) {
         return input.readUnsignedShort();
      }

      public void encode(final ByteBuf output, final Integer value) {
         output.writeShort(value);
      }
   };
   StreamCodec INT = new StreamCodec() {
      public Integer decode(final ByteBuf input) {
         return input.readInt();
      }

      public void encode(final ByteBuf output, final Integer value) {
         output.writeInt(value);
      }
   };
   StreamCodec VAR_INT = new StreamCodec() {
      public Integer decode(final ByteBuf input) {
         return VarInt.read(input);
      }

      public void encode(final ByteBuf output, final Integer value) {
         VarInt.write(output, value);
      }
   };
   StreamCodec OPTIONAL_VAR_INT = VAR_INT.map((i) -> i == 0 ? OptionalInt.empty() : OptionalInt.of(i - 1), (o) -> o.isPresent() ? o.getAsInt() + 1 : 0);
   StreamCodec LONG = new StreamCodec() {
      public Long decode(final ByteBuf input) {
         return input.readLong();
      }

      public void encode(final ByteBuf output, final Long value) {
         output.writeLong(value);
      }
   };
   StreamCodec VAR_LONG = new StreamCodec() {
      public Long decode(final ByteBuf input) {
         return VarLong.read(input);
      }

      public void encode(final ByteBuf output, final Long value) {
         VarLong.write(output, value);
      }
   };
   StreamCodec FLOAT = new StreamCodec() {
      public Float decode(final ByteBuf input) {
         return input.readFloat();
      }

      public void encode(final ByteBuf output, final Float value) {
         output.writeFloat(value);
      }
   };
   StreamCodec DOUBLE = new StreamCodec() {
      public Double decode(final ByteBuf input) {
         return input.readDouble();
      }

      public void encode(final ByteBuf output, final Double value) {
         output.writeDouble(value);
      }
   };
   StreamCodec BYTE_ARRAY = new StreamCodec() {
      public byte[] decode(final ByteBuf input) {
         return FriendlyByteBuf.readByteArray(input);
      }

      public void encode(final ByteBuf output, final byte[] value) {
         FriendlyByteBuf.writeByteArray(output, value);
      }
   };
   StreamCodec LONG_ARRAY = new StreamCodec() {
      public long[] decode(final ByteBuf input) {
         return FriendlyByteBuf.readLongArray(input);
      }

      public void encode(final ByteBuf output, final long[] value) {
         FriendlyByteBuf.writeLongArray(output, value);
      }
   };
   StreamCodec BIT_SET = new StreamCodec() {
      public BitSet decode(final ByteBuf input) {
         return BitSet.valueOf(FriendlyByteBuf.readByteArray(input));
      }

      public void encode(final ByteBuf output, final BitSet value) {
         FriendlyByteBuf.writeByteArray(output, value.toByteArray());
      }
   };
   StreamCodec STRING_UTF8 = stringUtf8(32767);
   StreamCodec TAG = tagCodec(NbtAccounter::defaultQuota);
   StreamCodec TRUSTED_TAG = tagCodec(NbtAccounter::unlimitedHeap);
   StreamCodec COMPOUND_TAG = compoundTagCodec(NbtAccounter::defaultQuota);
   StreamCodec TRUSTED_COMPOUND_TAG = compoundTagCodec(NbtAccounter::unlimitedHeap);
   StreamCodec OPTIONAL_COMPOUND_TAG = new StreamCodec() {
      public Optional decode(final ByteBuf input) {
         return Optional.ofNullable(FriendlyByteBuf.readNbt(input));
      }

      public void encode(final ByteBuf output, final Optional value) {
         FriendlyByteBuf.writeNbt(output, (Tag)value.orElse((Object)null));
      }
   };
   StreamCodec VECTOR3F = new StreamCodec() {
      public Vector3fc decode(final ByteBuf input) {
         return FriendlyByteBuf.readVector3f(input);
      }

      public void encode(final ByteBuf output, final Vector3fc value) {
         FriendlyByteBuf.writeVector3f(output, value);
      }
   };
   StreamCodec QUATERNIONF = new StreamCodec() {
      public Quaternionfc decode(final ByteBuf input) {
         return FriendlyByteBuf.readQuaternion(input);
      }

      public void encode(final ByteBuf output, final Quaternionfc value) {
         FriendlyByteBuf.writeQuaternion(output, value);
      }
   };
   StreamCodec CONTAINER_ID = new StreamCodec() {
      public Integer decode(final ByteBuf input) {
         return FriendlyByteBuf.readContainerId(input);
      }

      public void encode(final ByteBuf output, final Integer value) {
         FriendlyByteBuf.writeContainerId(output, value);
      }
   };
   StreamCodec GAME_PROFILE_PROPERTIES = new StreamCodec() {
      public PropertyMap decode(final ByteBuf input) {
         int propertyCount = ByteBufCodecs.readCount(input, 16);
         ImmutableMultimap.Builder result = ImmutableMultimap.builder();

         for(int i = 0; i < propertyCount; ++i) {
            String name = Utf8String.read(input, 64);
            String value = Utf8String.read(input, 32767);
            String signature = (String)FriendlyByteBuf.readNullable(input, (in) -> Utf8String.read(in, 1024));
            Property property = new Property(name, value, signature);
            result.put(property.name(), property);
         }

         return new PropertyMap(result.build());
      }

      public void encode(final ByteBuf output, final PropertyMap properties) {
         ByteBufCodecs.writeCount(output, properties.size(), 16);

         for(Property property : properties.values()) {
            Utf8String.write(output, property.name(), 64);
            Utf8String.write(output, property.value(), 32767);
            FriendlyByteBuf.writeNullable(output, property.signature(), (out, signature) -> Utf8String.write(out, signature, 1024));
         }

      }
   };
   StreamCodec PLAYER_NAME = stringUtf8(16);
   StreamCodec GAME_PROFILE = StreamCodec.composite(UUIDUtil.STREAM_CODEC, GameProfile::id, PLAYER_NAME, GameProfile::name, GAME_PROFILE_PROPERTIES, GameProfile::properties, GameProfile::new);
   StreamCodec RGB_COLOR = new StreamCodec() {
      public Integer decode(final ByteBuf input) {
         return ARGB.color(input.readByte() & 255, input.readByte() & 255, input.readByte() & 255);
      }

      public void encode(final ByteBuf output, final Integer value) {
         output.writeByte(ARGB.red(value));
         output.writeByte(ARGB.green(value));
         output.writeByte(ARGB.blue(value));
      }
   };
   StreamCodec INSTANT = LONG.map(Instant::ofEpochMilli, Instant::toEpochMilli);
   int PUBLIC_KEY_SIZE = 256;
   int MAX_PUBLIC_KEY_HEADER_SIZE = 256;
   int MAX_PUBLIC_KEY_LENGTH = 512;
   StreamCodec PUBLIC_KEY = byteArray(512).map((bytes) -> {
      try {
         return Crypt.byteToPublicKey(bytes);
      } catch (CryptException var2) {
         throw new DecoderException("Malformed public key bytes", var2);
      }
   }, Key::getEncoded);

   static StreamCodec byteArray(final int maxSize) {
      return new StreamCodec() {
         public byte[] decode(final ByteBuf input) {
            return FriendlyByteBuf.readByteArray(input, maxSize);
         }

         public void encode(final ByteBuf output, final byte[] value) {
            if (value.length > maxSize) {
               throw new EncoderException("ByteArray with size " + value.length + " is bigger than allowed " + maxSize);
            } else {
               FriendlyByteBuf.writeByteArray(output, value);
            }
         }
      };
   }

   static StreamCodec stringUtf8(final int maxStringLength) {
      return new StreamCodec() {
         public String decode(final ByteBuf input) {
            return Utf8String.read(input, maxStringLength);
         }

         public void encode(final ByteBuf output, final String value) {
            Utf8String.write(output, value, maxStringLength);
         }
      };
   }

   static StreamCodec fixedBitSet(final int size) {
      return new StreamCodec() {
         public BitSet decode(final ByteBuf input) {
            return FriendlyByteBuf.readFixedBitSet(input, size);
         }

         public void encode(final ByteBuf output, final BitSet value) {
            FriendlyByteBuf.writeFixedBitSet(output, value, size);
         }
      };
   }

   static StreamCodec optionalTagCodec(final Supplier accounter) {
      return new StreamCodec() {
         public Optional decode(final ByteBuf input) {
            return Optional.ofNullable(FriendlyByteBuf.readNbt(input, (NbtAccounter)accounter.get()));
         }

         public void encode(final ByteBuf output, final Optional value) {
            FriendlyByteBuf.writeNbt(output, (Tag)value.orElse((Object)null));
         }
      };
   }

   static StreamCodec tagCodec(final Supplier accounter) {
      return new StreamCodec() {
         public Tag decode(final ByteBuf input) {
            Tag result = FriendlyByteBuf.readNbt(input, (NbtAccounter)accounter.get());
            if (result == null) {
               throw new DecoderException("Expected non-null compound tag");
            } else {
               return result;
            }
         }

         public void encode(final ByteBuf output, final Tag value) {
            if (value == EndTag.INSTANCE) {
               throw new EncoderException("Expected non-null compound tag");
            } else {
               FriendlyByteBuf.writeNbt(output, value);
            }
         }
      };
   }

   static StreamCodec compoundTagCodec(final Supplier accounter) {
      return tagCodec(accounter).map((tag) -> {
         if (tag instanceof CompoundTag) {
            return (CompoundTag)tag;
         } else {
            throw new DecoderException("Not a compound tag: " + String.valueOf(tag));
         }
      }, (compoundTag) -> compoundTag);
   }

   static StreamCodec fromCodecTrusted(final Codec codec) {
      return fromCodec(codec, NbtAccounter::unlimitedHeap);
   }

   static StreamCodec fromCodec(final Codec codec) {
      return fromCodec(codec, NbtAccounter::defaultQuota);
   }

   static StreamCodec.CodecOperation fromCodec(final DynamicOps ops, final Codec codec) {
      return (original) -> new StreamCodec() {
            public Object decode(final ByteBuf input) {
               Object payload = (T)original.decode(input);
               return codec.parse(ops, payload).getOrThrow((msg) -> new DecoderException("Failed to decode: " + msg + " " + String.valueOf(payload)));
            }

            public void encode(final ByteBuf output, final Object value) {
               Object payload = (T)codec.encodeStart(ops, value).getOrThrow((msg) -> new EncoderException("Failed to encode: " + msg + " " + String.valueOf(value)));
               original.encode(output, payload);
            }
         };
   }

   static StreamCodec fromCodec(final Codec codec, final Supplier accounter) {
      return tagCodec(accounter).apply(fromCodec(NbtOps.INSTANCE, codec));
   }

   static StreamCodec fromCodecWithRegistriesTrusted(final Codec codec) {
      return fromCodecWithRegistries(codec, NbtAccounter::unlimitedHeap);
   }

   static StreamCodec fromCodecWithRegistries(final Codec codec) {
      return fromCodecWithRegistries(codec, NbtAccounter::defaultQuota);
   }

   static StreamCodec fromCodecWithRegistries(final Codec codec, final Supplier accounter) {
      final StreamCodec tagCodec = tagCodec(accounter);
      return new StreamCodec() {
         public Object decode(final RegistryFriendlyByteBuf input) {
            Tag tag = (Tag)tagCodec.decode(input);
            RegistryOps ops = input.registryAccess().createSerializationContext(NbtOps.INSTANCE);
            return codec.parse(ops, tag).getOrThrow((msg) -> new DecoderException("Failed to decode: " + msg + " " + String.valueOf(tag)));
         }

         public void encode(final RegistryFriendlyByteBuf output, final Object value) {
            RegistryOps ops = output.registryAccess().createSerializationContext(NbtOps.INSTANCE);
            Tag tag = (Tag)codec.encodeStart(ops, value).getOrThrow((msg) -> new EncoderException("Failed to encode: " + msg + " " + String.valueOf(value)));
            tagCodec.encode(output, tag);
         }
      };
   }

   static StreamCodec optional(final StreamCodec original) {
      return new StreamCodec() {
         public Optional decode(final ByteBuf input) {
            return input.readBoolean() ? Optional.of(original.decode(input)) : Optional.empty();
         }

         public void encode(final ByteBuf output, final Optional value) {
            if (value.isPresent()) {
               output.writeBoolean(true);
               original.encode(output, value.get());
            } else {
               output.writeBoolean(false);
            }

         }
      };
   }

   static int readCount(final ByteBuf input, final int maxSize) {
      int count = VarInt.read(input);
      if (count > maxSize) {
         throw new DecoderException(count + " elements exceeded max size of: " + maxSize);
      } else {
         return count;
      }
   }

   static void writeCount(final ByteBuf output, final int count, final int maxSize) {
      if (count > maxSize) {
         throw new EncoderException(count + " elements exceeded max size of: " + maxSize);
      } else {
         VarInt.write(output, count);
      }
   }

   static StreamCodec collection(final IntFunction constructor, final StreamCodec elementCodec) {
      return collection(constructor, elementCodec, Integer.MAX_VALUE);
   }

   static StreamCodec collection(final IntFunction constructor, final StreamCodec elementCodec, final int maxSize) {
      return new StreamCodec() {
         public Collection decode(final ByteBuf input) {
            int count = ByteBufCodecs.readCount(input, maxSize);
            Collection result = (C)((Collection)constructor.apply(Math.min(count, 65536)));

            for(int i = 0; i < count; ++i) {
               result.add(elementCodec.decode(input));
            }

            return result;
         }

         public void encode(final ByteBuf output, final Collection value) {
            ByteBufCodecs.writeCount(output, value.size(), maxSize);

            for(Object element : value) {
               elementCodec.encode(output, element);
            }

         }
      };
   }

   static StreamCodec.CodecOperation collection(final IntFunction constructor) {
      return (original) -> collection(constructor, original);
   }

   static StreamCodec.CodecOperation collection(final IntFunction constructor, final int maxSize) {
      return (original) -> collection(constructor, original, maxSize);
   }

   static StreamCodec.CodecOperation list() {
      return (original) -> collection(ArrayList::new, original);
   }

   static StreamCodec.CodecOperation list(final int maxSize) {
      return (original) -> collection(ArrayList::new, original, maxSize);
   }

   static StreamCodec fixedSizeCollection(final IntFunction constructor, final StreamCodec elementCodec, final int size) {
      return new StreamCodec() {
         public Collection decode(final ByteBuf input) {
            Collection result = (C)((Collection)constructor.apply(size));

            for(int i = 0; i < size; ++i) {
               result.add(elementCodec.decode(input));
            }

            return result;
         }

         public void encode(final ByteBuf output, final Collection value) {
            if (value.size() != size) {
               throw new EncoderException("Invalid list size, expected " + size + ", but got " + value.size());
            } else {
               Iterator iterator = value.iterator();

               for(int i = 0; i < size; ++i) {
                  elementCodec.encode(output, iterator.next());
               }

            }
         }
      };
   }

   static StreamCodec.CodecOperation fixedSizeCollection(final IntFunction constructor, final int size) {
      return (original) -> fixedSizeCollection(constructor, original, size);
   }

   static StreamCodec.CodecOperation fixedSizeList(final int size) {
      return (original) -> fixedSizeCollection(ArrayList::new, original, size);
   }

   static StreamCodec map(final IntFunction constructor, final StreamCodec keyCodec, final StreamCodec valueCodec) {
      return map(constructor, keyCodec, valueCodec, Integer.MAX_VALUE);
   }

   static StreamCodec map(final IntFunction constructor, final StreamCodec keyCodec, final StreamCodec valueCodec, final int maxSize) {
      return new StreamCodec() {
         public void encode(final ByteBuf output, final Map map) {
            ByteBufCodecs.writeCount(output, map.size(), maxSize);
            map.forEach((k, v) -> {
               keyCodec.encode(output, k);
               valueCodec.encode(output, v);
            });
         }

         public Map decode(final ByteBuf input) {
            int count = ByteBufCodecs.readCount(input, maxSize);
            Map result = (M)((Map)constructor.apply(Math.min(count, 65536)));

            for(int i = 0; i < count; ++i) {
               Object key = (K)keyCodec.decode(input);
               Object value = (V)valueCodec.decode(input);
               result.put(key, value);
            }

            return result;
         }
      };
   }

   static StreamCodec either(final StreamCodec leftCodec, final StreamCodec rightCodec) {
      return new StreamCodec() {
         public Either decode(final ByteBuf input) {
            return input.readBoolean() ? Either.left(leftCodec.decode(input)) : Either.right(rightCodec.decode(input));
         }

         public void encode(final ByteBuf output, final Either value) {
            value.ifLeft((left) -> {
               output.writeBoolean(true);
               leftCodec.encode(output, left);
            }).ifRight((right) -> {
               output.writeBoolean(false);
               rightCodec.encode(output, right);
            });
         }
      };
   }

   static StreamCodec.CodecOperation lengthPrefixed(final int maxSize, final BiFunction decorator) {
      return (original) -> new StreamCodec() {
            public Object decode(final ByteBuf input) {
               int size = VarInt.read(input);
               if (size > maxSize) {
                  throw new DecoderException("Buffer size " + size + " is larger than allowed limit of " + maxSize);
               } else {
                  int index = input.readerIndex();
                  ByteBuf limitedSlice = (B)((ByteBuf)decorator.apply(input, input.slice(index, size)));
                  input.readerIndex(index + size);
                  return original.decode(limitedSlice);
               }
            }

            public void encode(final ByteBuf output, final Object value) {
               ByteBuf scratchBuffer = (B)((ByteBuf)decorator.apply(output, output.alloc().buffer()));

               try {
                  original.encode(scratchBuffer, value);
                  int size = scratchBuffer.readableBytes();
                  if (size > maxSize) {
                     throw new EncoderException("Buffer size " + size + " is  larger than allowed limit of " + maxSize);
                  }

                  VarInt.write(output, size);
                  output.writeBytes(scratchBuffer);
               } finally {
                  scratchBuffer.release();
               }

            }
         };
   }

   static StreamCodec.CodecOperation lengthPrefixed(final int maxSize) {
      return lengthPrefixed(maxSize, (parent, child) -> child);
   }

   static StreamCodec.CodecOperation registryFriendlyLengthPrefixed(final int maxSize) {
      return lengthPrefixed(maxSize, (parent, child) -> new RegistryFriendlyByteBuf(child, parent.registryAccess()));
   }

   static StreamCodec idMapper(final IntFunction byId, final ToIntFunction toId) {
      return new StreamCodec() {
         public Object decode(final ByteBuf input) {
            int id = VarInt.read(input);
            return byId.apply(id);
         }

         public void encode(final ByteBuf output, final Object value) {
            int id = toId.applyAsInt(value);
            VarInt.write(output, id);
         }
      };
   }

   static StreamCodec idMapper(final IdMap mapper) {
      return idMapper(mapper::byIdOrThrow, mapper::getIdOrThrow);
   }

   private static StreamCodec registry(final ResourceKey registryKey, final Function mapExtractor) {
      return new StreamCodec() {
         private IdMap getRegistryOrThrow(final RegistryFriendlyByteBuf input) {
            return (IdMap)mapExtractor.apply(input.registryAccess().lookupOrThrow(registryKey));
         }

         public Object decode(final RegistryFriendlyByteBuf input) {
            int id = VarInt.read(input);
            return this.getRegistryOrThrow(input).byIdOrThrow(id);
         }

         public void encode(final RegistryFriendlyByteBuf output, final Object value) {
            int id = this.getRegistryOrThrow(output).getIdOrThrow(value);
            VarInt.write(output, id);
         }
      };
   }

   static StreamCodec registry(final ResourceKey registryKey) {
      return registry(registryKey, (r) -> r);
   }

   static StreamCodec holderRegistry(final ResourceKey registryKey) {
      return registry(registryKey, Registry::asHolderIdMap);
   }

   static StreamCodec holder(final ResourceKey registryKey, final StreamCodec directCodec) {
      return new StreamCodec() {
         private static final int DIRECT_HOLDER_ID = 0;

         private IdMap getRegistryOrThrow(final RegistryFriendlyByteBuf input) {
            return input.registryAccess().lookupOrThrow(registryKey).asHolderIdMap();
         }

         public Holder decode(final RegistryFriendlyByteBuf input) {
            int id = VarInt.read(input);
            return id == 0 ? Holder.direct(directCodec.decode(input)) : (Holder)this.getRegistryOrThrow(input).byIdOrThrow(id - 1);
         }

         public void encode(final RegistryFriendlyByteBuf output, final Holder holder) {
            switch (holder.kind()) {
               case REFERENCE:
                  int id = this.getRegistryOrThrow(output).getIdOrThrow(holder);
                  VarInt.write(output, id + 1);
                  break;
               case DIRECT:
                  VarInt.write(output, 0);
                  directCodec.encode(output, holder.value());
            }

         }
      };
   }

   static StreamCodec holderSet(final ResourceKey registryKey) {
      return new StreamCodec() {
         private static final int NAMED_SET = -1;
         private final StreamCodec holderCodec = ByteBufCodecs.holderRegistry(registryKey);

         public HolderSet decode(final RegistryFriendlyByteBuf input) {
            int count = VarInt.read(input) - 1;
            if (count == -1) {
               Registry registry = input.registryAccess().lookupOrThrow(registryKey);
               return (HolderSet)registry.get(TagKey.create(registryKey, (Identifier)Identifier.STREAM_CODEC.decode(input))).orElseThrow();
            } else {
               List holders = new ArrayList(Math.min(count, 65536));

               for(int i = 0; i < count; ++i) {
                  holders.add((Holder)this.holderCodec.decode(input));
               }

               return HolderSet.direct(holders);
            }
         }

         public void encode(final RegistryFriendlyByteBuf output, final HolderSet value) {
            Optional key = value.unwrapKey();
            if (key.isPresent()) {
               VarInt.write(output, 0);
               Identifier.STREAM_CODEC.encode(output, ((TagKey)key.get()).location());
            } else {
               VarInt.write(output, value.size() + 1);

               for(Holder holder : value) {
                  this.holderCodec.encode(output, holder);
               }
            }

         }
      };
   }

   static StreamCodec lenientJson(final int maxStringLength) {
      return new StreamCodec() {
         private static final Gson GSON = (new GsonBuilder()).disableHtmlEscaping().create();

         public JsonElement decode(final ByteBuf input) {
            String payload = Utf8String.read(input, maxStringLength);

            try {
               return LenientJsonParser.parse(payload);
            } catch (JsonSyntaxException var4) {
               throw new DecoderException("Failed to parse JSON", var4);
            }
         }

         public void encode(final ByteBuf output, final JsonElement value) {
            String payload = GSON.toJson(value);
            Utf8String.write(output, payload, maxStringLength);
         }
      };
   }
}
