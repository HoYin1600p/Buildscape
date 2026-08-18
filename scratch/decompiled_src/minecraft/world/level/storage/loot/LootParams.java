package net.minecraft.world.level.storage.loot;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class LootParams {
   private final ServerLevel level;
   private final ContextMap params;
   private final Map dynamicDrops;
   private final float luck;

   public LootParams(final ServerLevel level, final ContextMap params, final Map dynamicDrops, final float luck) {
      this.level = level;
      this.params = params;
      this.dynamicDrops = dynamicDrops;
      this.luck = luck;
   }

   public ServerLevel getLevel() {
      return this.level;
   }

   public ContextMap contextMap() {
      return this.params;
   }

   public void addDynamicDrops(final Identifier location, final Consumer output) {
      LootParams.DynamicDrop dynamicDrop = (LootParams.DynamicDrop)this.dynamicDrops.get(location);
      if (dynamicDrop != null) {
         dynamicDrop.add(output);
      }

   }

   public float getLuck() {
      return this.luck;
   }

   public static class Builder {
      private final ServerLevel level;
      private final ContextMap.Builder params = ContextMap.builder();
      private final Map dynamicDrops = Maps.newHashMap();
      private float luck;

      public Builder(final ServerLevel level) {
         this.level = level;
      }

      public ServerLevel getLevel() {
         return this.level;
      }

      public LootParams.Builder withParameter(final ContextKey param, final Object value) {
         this.params.set(param, value);
         return this;
      }

      public LootParams.Builder withOptionalParameter(final ContextKey param, final @Nullable Object value) {
         this.params.set(param, value);
         return this;
      }

      public Object getParameter(final ContextKey param) {
         Object value = (T)this.params.get(param);
         if (value == null) {
            throw new NoSuchElementException(param.name().toString());
         } else {
            return value;
         }
      }

      public @Nullable Object getOptionalParameter(final ContextKey param) {
         return this.params.get(param);
      }

      public LootParams.Builder withDynamicDrop(final Identifier location, final LootParams.DynamicDrop dynamicDrop) {
         LootParams.DynamicDrop prev = (LootParams.DynamicDrop)this.dynamicDrops.put(location, dynamicDrop);
         if (prev != null) {
            throw new IllegalStateException("Duplicated dynamic drop '" + String.valueOf(this.dynamicDrops) + "'");
         } else {
            return this;
         }
      }

      public LootParams.Builder withLuck(final float luck) {
         this.luck = luck;
         return this;
      }

      public LootParams create(final ContextKeySet contextKeySet) {
         ContextMap keySet = this.params.buildAndValidate(contextKeySet);
         return new LootParams(this.level, keySet, this.dynamicDrops, this.luck);
      }
   }

   @FunctionalInterface
   public interface DynamicDrop {
      void add(Consumer output);
   }
}
