package net.minecraft.world.level.storage.loot.entries;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public abstract class UniformContainerBase extends LootPoolEntryContainer {
   public static final int DEFAULT_WEIGHT = 1;
   public static final int DEFAULT_QUALITY = 0;
   protected final int weight;
   protected final int quality;

   protected UniformContainerBase(final int weight, final int quality, final Optional condition, final Optional modifier) {
      super(condition, modifier);
      this.weight = weight;
      this.quality = quality;
   }

   public abstract MapCodec codec();

   protected static Products.P4 uniformFields(final RecordCodecBuilder.Instance i) {
      return i.group(Codec.INT.optionalFieldOf("weight", 1).forGetter((e) -> e.weight), Codec.INT.optionalFieldOf("quality", 0).forGetter((e) -> e.quality)).and(commonFields(i).t1()).and(LootItemFunctions.CODEC.optionalFieldOf("modifier").forGetter((e) -> e.modifier));
   }

   public void validate(final ValidationContext context) {
      super.validate(context);
      Validatable.validateHolder(context, "modifier", this.modifier);
   }

   public static UniformContainerBase.Builder simpleBuilder(final UniformContainerBase.EntryConstructor constructor) {
      return new UniformContainerBase.DummyBuilder(constructor);
   }

   public abstract static class Builder extends LootPoolEntryContainer.Builder implements FunctionUserBuilder {
      protected int weight = 1;
      protected int quality = 0;

      public UniformContainerBase.Builder setWeight(final int weight) {
         this.weight = weight;
         return (UniformContainerBase.Builder)this.getThis();
      }

      public UniformContainerBase.Builder setQuality(final int quality) {
         this.quality = quality;
         return (UniformContainerBase.Builder)this.getThis();
      }
   }

   private static class DummyBuilder extends UniformContainerBase.Builder {
      private final UniformContainerBase.EntryConstructor constructor;

      public DummyBuilder(final UniformContainerBase.EntryConstructor constructor) {
         this.constructor = constructor;
      }

      protected UniformContainerBase.DummyBuilder getThis() {
         return this;
      }

      public LootPoolEntryContainer build() {
         return this.constructor.build(this.weight, this.quality, this.getCondition(), this.getModifier());
      }
   }

   protected abstract class EntryBase implements LootPoolEntry {
      protected EntryBase() {
         Objects.requireNonNull(UniformContainerBase.this);
         super();
      }

      public int getWeight(final float luck) {
         return Math.max(Mth.floor((float)UniformContainerBase.this.weight + (float)UniformContainerBase.this.quality * luck), 0);
      }
   }

   @FunctionalInterface
   public interface EntryConstructor {
      UniformContainerBase build(int weight, int quality, Optional condition, Optional modifier);
   }
}
