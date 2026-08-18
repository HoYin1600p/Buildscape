package net.minecraft.world.level.saveddata;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.minecraft.resources.Identifier;
import net.minecraft.util.datafix.DataFixTypes;

public record SavedDataType(Identifier id, Supplier constructor, Codec codec, DataFixTypes dataFixType) {
   public boolean equals(final Object obj) {
      if (obj instanceof SavedDataType type) {
         if (this.id.equals(type.id)) {
            return true;
         }
      }

      return false;
   }

   public int hashCode() {
      return this.id.hashCode();
   }

   public String toString() {
      return "SavedDataType[" + String.valueOf(this.id) + "]";
   }
}
