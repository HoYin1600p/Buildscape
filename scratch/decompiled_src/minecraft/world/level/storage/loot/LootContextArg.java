package net.minecraft.world.level.storage.loot;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.Nullable;

public interface LootContextArg {
   Codec ENTITY_OR_BLOCK = createArgCodec((builder) -> builder.anyOf(LootContext.EntityTarget.values()).anyOf(LootContext.BlockEntityTarget.values()));

   @Nullable Object get(LootContext context);

   ContextKey contextParam();

   static LootContextArg cast(final LootContextArg original) {
      return original;
   }

   static LootContextArg of(final ContextKey contextParam) {
      return () -> contextParam;
   }

   static Codec createArgCodec(final UnaryOperator consumer) {
      return ((LootContextArg.ArgCodecBuilder)consumer.apply(new LootContextArg.ArgCodecBuilder())).build();
   }

   public static final class ArgCodecBuilder {
      private final ExtraCodecs.LateBoundIdMapper sources = new ExtraCodecs.LateBoundIdMapper();

      private ArgCodecBuilder() {
      }

      public LootContextArg.ArgCodecBuilder anyOf(final Object[] targets, final Function nameGetter, final Function argFactory) {
         for(Object target : targets) {
            this.sources.put((String)nameGetter.apply(target), (LootContextArg)argFactory.apply(target));
         }

         return this;
      }

      public LootContextArg.ArgCodecBuilder anyOf(final StringRepresentable[] targets, final Function argFactory) {
         return this.anyOf(targets, StringRepresentable::getSerializedName, argFactory);
      }

      public LootContextArg.ArgCodecBuilder anyOf(final StringRepresentable[] targets) {
         return this.anyOf(targets, (x$0) -> LootContextArg.cast((LootContextArg)x$0));
      }

      public LootContextArg.ArgCodecBuilder anyEntity(final Function function) {
         return this.anyOf(LootContext.EntityTarget.values(), (target) -> (LootContextArg)function.apply(target.contextParam()));
      }

      public LootContextArg.ArgCodecBuilder anyBlockEntity(final Function function) {
         return this.anyOf(LootContext.BlockEntityTarget.values(), (target) -> (LootContextArg)function.apply(target.contextParam()));
      }

      public LootContextArg.ArgCodecBuilder anyItemStack(final Function function) {
         return this.anyOf(LootContext.ItemStackTarget.values(), (target) -> (LootContextArg)function.apply(target.contextParam()));
      }

      public LootContextArg.ArgCodecBuilder or(final String name, final LootContextArg arg) {
         this.sources.put(name, arg);
         return this;
      }

      private Codec build() {
         return this.sources.codec(Codec.STRING);
      }
   }

   public interface Getter extends LootContextArg {
      @Nullable Object get(Object value);

      ContextKey contextParam();

      default @Nullable Object get(final LootContext context) {
         Object value = (T)context.getOptionalParameter(this.contextParam());
         return value != null ? this.get(value) : null;
      }
   }

   public interface SimpleGetter extends LootContextArg {
      ContextKey contextParam();

      default @Nullable Object get(final LootContext context) {
         return context.getOptionalParameter(this.contextParam());
      }
   }
}
