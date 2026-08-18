package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public abstract class CompositeEntryBase extends LootPoolEntryContainer {
   public static final ProblemReporter.Problem NO_CHILDREN_PROBLEM = new ProblemReporter.Problem() {
      public String description() {
         return "Empty children list";
      }
   };
   protected final List children;
   private final ComposableEntryContainer composedChildren;

   protected CompositeEntryBase(final List children, final Optional condition, final Optional modifier) {
      super(condition, modifier);
      this.children = children;
      this.composedChildren = this.compose(children);
   }

   public abstract MapCodec codec();

   public void validate(final ValidationContext context) {
      super.validate(context);
      if (this.children.isEmpty()) {
         context.reportProblem(NO_CHILDREN_PROBLEM);
      }

      Validatable.validate(context, "children", this.children);
   }

   protected abstract ComposableEntryContainer compose(List entries);

   public final boolean expandRaw(final LootContext context, final Consumer output) {
      return this.composedChildren.expand(context, output);
   }

   public static MapCodec createCodec(final CompositeEntryBase.CompositeEntryConstructor constructor) {
      return RecordCodecBuilder.mapCodec((i) -> i.group(LootPoolEntries.CODEC.listOf().optionalFieldOf("children", List.of()).forGetter((e) -> e.children)).and(commonFields(i)).apply(i, constructor::create));
   }

   public abstract static class Builder extends LootPoolEntryContainer.Builder {
      private final ImmutableList.Builder entries = ImmutableList.builder();

      public Builder(final LootPoolEntryContainer.Builder... entries) {
         for(LootPoolEntryContainer.Builder entry : entries) {
            this.entries.add(entry.build());
         }

      }

      protected ImmutableList.Builder addEntry(final LootPoolEntryContainer.Builder entry) {
         return this.entries.add(entry.build());
      }

      protected CompositeEntryBase build(final CompositeEntryBase.CompositeEntryConstructor constructor) {
         return constructor.create(this.entries.build(), this.getCondition(), this.getModifier());
      }
   }

   @FunctionalInterface
   public interface CompositeEntryConstructor {
      CompositeEntryBase create(List children, Optional condition, Optional modifier);
   }
}
