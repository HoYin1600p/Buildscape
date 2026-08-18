package net.minecraft.world.attribute;

import com.mojang.serialization.Codec;
import java.util.Objects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public class EnvironmentAttribute {
   private final AttributeType type;
   private final Object defaultValue;
   private final AttributeRange valueRange;
   private final boolean isSyncable;
   private final boolean isPositional;
   private final boolean isSpatiallyInterpolated;

   private EnvironmentAttribute(final AttributeType type, final Object defaultValue, final AttributeRange valueRange, final boolean isSyncable, final boolean isPositional, final boolean isSpatiallyInterpolated) {
      this.type = type;
      this.defaultValue = defaultValue;
      this.valueRange = valueRange;
      this.isSyncable = isSyncable;
      this.isPositional = isPositional;
      this.isSpatiallyInterpolated = isSpatiallyInterpolated;
   }

   public static EnvironmentAttribute.Builder builder(final AttributeType type) {
      return new EnvironmentAttribute.Builder(type);
   }

   public AttributeType type() {
      return this.type;
   }

   public Object defaultValue() {
      return this.defaultValue;
   }

   public Codec valueCodec() {
      return this.type.valueCodec().validate(this.valueRange::validate);
   }

   public Object sanitizeValue(final Object value) {
      return this.valueRange.sanitize(value);
   }

   public boolean isSyncable() {
      return this.isSyncable;
   }

   public boolean isPositional() {
      return this.isPositional;
   }

   public boolean isSpatiallyInterpolated() {
      return this.isSpatiallyInterpolated;
   }

   public String toString() {
      return Util.getRegisteredName(BuiltInRegistries.ENVIRONMENT_ATTRIBUTE, this);
   }

   public static class Builder {
      private final AttributeType type;
      private @Nullable Object defaultValue;
      private AttributeRange valueRange = AttributeRange.any();
      private boolean isSyncable = false;
      private boolean isPositional = true;
      private boolean isSpatiallyInterpolated = false;

      public Builder(final AttributeType type) {
         this.type = type;
      }

      public EnvironmentAttribute.Builder defaultValue(final Object defaultValue) {
         this.defaultValue = defaultValue;
         return this;
      }

      public EnvironmentAttribute.Builder valueRange(final AttributeRange valueRange) {
         this.valueRange = valueRange;
         return this;
      }

      public EnvironmentAttribute.Builder syncable() {
         this.isSyncable = true;
         return this;
      }

      public EnvironmentAttribute.Builder notPositional() {
         this.isPositional = false;
         return this;
      }

      public EnvironmentAttribute.Builder spatiallyInterpolated() {
         this.isSpatiallyInterpolated = true;
         return this;
      }

      public EnvironmentAttribute build() {
         return new EnvironmentAttribute(this.type, Objects.requireNonNull(this.defaultValue, "Missing default value"), this.valueRange, this.isSyncable, this.isPositional, this.isSpatiallyInterpolated);
      }
   }
}
