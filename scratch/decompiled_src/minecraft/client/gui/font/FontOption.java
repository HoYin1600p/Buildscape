package net.minecraft.client.gui.font;

import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.util.StringRepresentable;

public enum FontOption implements StringRepresentable {
   UNIFORM("uniform"),
   JAPANESE_VARIANTS("jp");

   public static final Codec CODEC = StringRepresentable.fromEnum(FontOption::values);
   private final String name;

   private FontOption(final String name) {
      this.name = name;
   }

   public String getSerializedName() {
      return this.name;
   }

   // $FF: synthetic method
   private static FontOption[] $values() {
      return new FontOption[]{UNIFORM, JAPANESE_VARIANTS};
   }

   public static class Filter {
      private final Map values;
      public static final Codec CODEC = Codec.unboundedMap(FontOption.CODEC, Codec.BOOL).xmap(FontOption.Filter::new, (p) -> p.values);
      public static final FontOption.Filter ALWAYS_PASS = new FontOption.Filter(Map.of());

      public Filter(final Map values) {
         this.values = values;
      }

      public boolean apply(final Set options) {
         for(Map.Entry e : this.values.entrySet()) {
            if (options.contains(e.getKey()) != e.getValue()) {
               return false;
            }
         }

         return true;
      }

      public FontOption.Filter merge(final FontOption.Filter other) {
         Map options = new HashMap(other.values);
         options.putAll(this.values);
         return new FontOption.Filter(Map.copyOf(options));
      }
   }
}
