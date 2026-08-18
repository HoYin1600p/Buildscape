package net.minecraft.commands;

import com.mojang.serialization.Codec;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.resources.Identifier;
import net.minecraft.server.ServerFunctionManager;

public class CacheableFunction {
   public static final Codec CODEC = Identifier.CODEC.xmap(CacheableFunction::new, CacheableFunction::getId);
   private final Identifier id;
   private boolean resolved;
   private Optional function = Optional.empty();

   public CacheableFunction(final Identifier id) {
      this.id = id;
   }

   public Optional get(final ServerFunctionManager manager) {
      if (!this.resolved) {
         this.function = manager.get(this.id);
         this.resolved = true;
      }

      return this.function;
   }

   public Identifier getId() {
      return this.id;
   }

   public boolean equals(final Object obj) {
      if (obj == this) {
         return true;
      } else {
         if (obj instanceof CacheableFunction) {
            CacheableFunction cacheableFunction = (CacheableFunction)obj;
            if (this.getId().equals(cacheableFunction.getId())) {
               return true;
            }
         }

         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.id});
   }
}
