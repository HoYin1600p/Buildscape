package net.minecraft.server.dedicated;

import com.google.common.base.MoreObjects;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import net.minecraft.core.RegistryAccess;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public abstract class Settings {
   private static final Logger LOGGER = LogUtils.getLogger();
   protected final Properties properties;

   public Settings(final Properties properties) {
      this.properties = properties;
   }

   public static Properties loadFromFile(final Path file) {
      try {
         try {
            InputStream is = Files.newInputStream(file);

            Properties var13;
            try {
               CharsetDecoder reportingUtf8Decoder = StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
               Properties properties = new Properties();
               properties.load(new InputStreamReader(is, reportingUtf8Decoder));
               var13 = properties;
            } catch (Throwable var8) {
               if (is != null) {
                  try {
                     is.close();
                  } catch (Throwable var6) {
                     var8.addSuppressed(var6);
                  }
               }

               throw var8;
            }

            if (is != null) {
               is.close();
            }

            return var13;
         } catch (CharacterCodingException var9) {
            LOGGER.info("Failed to load properties as UTF-8 from file {}, trying ISO_8859_1", file);
            Reader reader = Files.newBufferedReader(file, StandardCharsets.ISO_8859_1);

            Properties var4;
            try {
               Properties properties = new Properties();
               properties.load(reader);
               var4 = properties;
            } catch (Throwable var7) {
               if (reader != null) {
                  try {
                     reader.close();
                  } catch (Throwable var5) {
                     var7.addSuppressed(var5);
                  }
               }

               throw var7;
            }

            if (reader != null) {
               reader.close();
            }

            return var4;
         }
      } catch (IOException var10) {
         LOGGER.error("Failed to load properties from file: {}", file, var10);
         return new Properties();
      }
   }

   public void store(final Path output) {
      try {
         Writer os = Files.newBufferedWriter(output, StandardCharsets.UTF_8);

         try {
            this.properties.store(os, "Minecraft server properties");
         } catch (Throwable var6) {
            if (os != null) {
               try {
                  os.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (os != null) {
            os.close();
         }
      } catch (IOException var7) {
         LOGGER.error("Failed to store properties to file: {}", output);
      }

   }

   private static Function wrapNumberDeserializer(final Function inner) {
      return (s) -> {
         try {
            return (Number)inner.apply(s);
         } catch (NumberFormatException var3) {
            return null;
         }
      };
   }

   protected static Function dispatchNumberOrString(final IntFunction intDeserializer, final Function stringDeserializer) {
      return (s) -> {
         try {
            return intDeserializer.apply(Integer.parseInt(s));
         } catch (NumberFormatException var4) {
            return stringDeserializer.apply(s);
         }
      };
   }

   private @Nullable String getStringRaw(final String key) {
      return (String)this.properties.get(key);
   }

   protected @Nullable Object getLegacy(final String key, final Function deserializer) {
      String value = this.getStringRaw(key);
      if (value == null) {
         return null;
      } else {
         this.properties.remove(key);
         return deserializer.apply(value);
      }
   }

   protected Object get(final String key, final Function deserializer, final Function serializer, final Object defaultValue) {
      String value = this.getStringRaw(key);
      Object result = (V)MoreObjects.firstNonNull(value != null ? deserializer.apply(value) : null, defaultValue);
      this.properties.put(key, serializer.apply(result));
      return result;
   }

   protected Settings.MutableValue getMutable(final String key, final Function deserializer, final Function serializer, final Object defaultValue) {
      String value = this.getStringRaw(key);
      Object result = (V)MoreObjects.firstNonNull(value != null ? deserializer.apply(value) : null, defaultValue);
      this.properties.put(key, serializer.apply(result));
      return new Settings.MutableValue(key, result, serializer);
   }

   protected Object get(final String key, final Function deserializer, final UnaryOperator validator, final Function serializer, final Object defaultValue) {
      return this.get(key, (s) -> {
         Object result = (V)deserializer.apply(s);
         return result != null ? validator.apply(result) : null;
      }, serializer, defaultValue);
   }

   protected Object get(final String key, final Function deserializer, final Object defaultValue) {
      return this.get(key, deserializer, Objects::toString, defaultValue);
   }

   protected Settings.MutableValue getMutable(final String key, final Function deserializer, final Object defaultValue) {
      return this.getMutable(key, deserializer, Objects::toString, defaultValue);
   }

   protected String get(final String key, final String defaultValue) {
      return (String)this.get(key, Function.identity(), Function.identity(), defaultValue);
   }

   protected @Nullable String getLegacyString(final String key) {
      return (String)this.getLegacy(key, Function.identity());
   }

   protected int get(final String key, final int defaultValue) {
      return this.get(key, wrapNumberDeserializer(Integer::parseInt), Integer.valueOf(defaultValue));
   }

   protected Settings.MutableValue getMutable(final String key, final int defaultValue) {
      return this.getMutable(key, wrapNumberDeserializer(Integer::parseInt), defaultValue);
   }

   protected Settings.MutableValue getMutable(final String key, final String defaultValue) {
      return this.getMutable(key, String::new, defaultValue);
   }

   protected int get(final String key, final UnaryOperator validator, final int defaultValue) {
      return this.get(key, wrapNumberDeserializer(Integer::parseInt), validator, Objects::toString, defaultValue);
   }

   protected long get(final String key, final long defaultValue) {
      return this.get(key, wrapNumberDeserializer(Long::parseLong), defaultValue);
   }

   protected boolean get(final String key, final boolean defaultValue) {
      return this.get(key, Boolean::valueOf, defaultValue);
   }

   protected Settings.MutableValue getMutable(final String key, final boolean defaultValue) {
      return this.getMutable(key, Boolean::valueOf, defaultValue);
   }

   protected @Nullable Boolean getLegacyBoolean(final String key) {
      return (Boolean)this.getLegacy(key, Boolean::valueOf);
   }

   protected Properties cloneProperties() {
      Properties result = new Properties();
      result.putAll(this.properties);
      return result;
   }

   protected abstract Settings reload(final RegistryAccess registryAccess, final Properties properties);

   public class MutableValue implements Supplier {
      private final String key;
      private final Object value;
      private final Function serializer;

      private MutableValue(final String key, final Object value, final Function serializer) {
         Objects.requireNonNull(Settings.this);
         super();
         this.key = key;
         this.value = value;
         this.serializer = serializer;
      }

      public Object get() {
         return this.value;
      }

      public Settings update(final RegistryAccess registryAccess, final Object value) {
         Properties properties = Settings.this.cloneProperties();
         properties.put(this.key, this.serializer.apply(value));
         return Settings.this.reload(registryAccess, properties);
      }
   }
}
