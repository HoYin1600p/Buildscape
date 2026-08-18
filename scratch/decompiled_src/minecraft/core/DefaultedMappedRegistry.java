package net.minecraft.core;

import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;

public class DefaultedMappedRegistry extends MappedRegistry implements DefaultedRegistry {
   private final Identifier defaultKey;
   private Holder.Reference defaultValue;

   public DefaultedMappedRegistry(final String defaultKey, final ResourceKey key, final Lifecycle lifecycle, final boolean intrusiveHolders) {
      super(key, lifecycle, intrusiveHolders);
      this.defaultKey = Identifier.parse(defaultKey);
   }

   public Holder.Reference register(final ResourceKey key, final Object value, final RegistrationInfo registrationInfo) {
      Holder.Reference result = super.register(key, value, registrationInfo);
      if (this.defaultKey.equals(key.identifier())) {
         this.defaultValue = result;
      }

      return result;
   }

   public int getId(final @Nullable Object thing) {
      int id = super.getId(thing);
      return id == -1 ? super.getId(this.defaultValue.value()) : id;
   }

   public Identifier getKey(final Object thing) {
      Identifier k = super.getKey(thing);
      return k == null ? this.defaultKey : k;
   }

   public Object getValue(final @Nullable Identifier key) {
      Object t = (T)super.getValue(key);
      return t == null ? this.defaultValue.value() : t;
   }

   public Optional getOptional(final @Nullable Identifier key) {
      return Optional.ofNullable(super.getValue(key));
   }

   public Optional getAny() {
      return Optional.ofNullable(this.defaultValue);
   }

   public Object byId(final int id) {
      Object t = (T)super.byId(id);
      return t == null ? this.defaultValue.value() : t;
   }

   public Optional getRandom(final RandomSource random) {
      return super.getRandom(random).or(() -> Optional.of(this.defaultValue));
   }

   public Identifier getDefaultKey() {
      return this.defaultKey;
   }
}
