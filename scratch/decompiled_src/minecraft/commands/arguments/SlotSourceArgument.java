package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Util;
import net.minecraft.world.inventory.SlotRange;
import net.minecraft.world.inventory.SlotRanges;
import net.minecraft.world.item.slot.RangeSlotSource;
import net.minecraft.world.item.slot.SlotSource;
import net.minecraft.world.item.slot.SlotSources;

public class SlotSourceArgument implements ArgumentType {
   private static final Collection EXAMPLES = Util.join(ResourceOrIdArgument.EXAMPLES, SlotsArgument.EXAMPLES);
   private final ArgumentType holderArgument;

   private SlotSourceArgument(final CommandBuildContext context) {
      this.holderArgument = new ResourceOrIdArgument(context, Registries.SLOT_SOURCE, SlotSources.DIRECT_CODEC);
   }

   public static SlotSourceArgument slotSource(final CommandBuildContext context) {
      return new SlotSourceArgument(context);
   }

   public static SlotSourceArgument.Result getSlotSource(final CommandContext context, final String name) {
      return (SlotSourceArgument.Result)context.getArgument(name, SlotSourceArgument.Result.class);
   }

   public SlotSourceArgument.Result parse(final StringReader reader) throws CommandSyntaxException {
      int start = reader.getCursor();
      SlotRange slotRange = SlotRanges.tryRead(reader);
      if (slotRange != null) {
         return new SlotSourceArgument.LiteralResult(slotRange);
      } else {
         reader.setCursor(start);
         return new SlotSourceArgument.HolderResult((Holder)this.holderArgument.parse(reader));
      }
   }

   public CompletableFuture listSuggestions(final CommandContext contextBuilder, final SuggestionsBuilder builder) {
      SuggestionsBuilder sub = builder.restart();
      SharedSuggestionProvider.suggest(SlotRanges.allNames(), sub);
      builder.add(sub);
      return this.holderArgument.listSuggestions(contextBuilder, builder);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   public static record HolderResult(Holder holder) implements SlotSourceArgument.Result {
      public SlotSource value() {
         return (SlotSource)this.holder.value();
      }

      public Optional name() {
         return this.holder.getRegisteredNameIfPresent();
      }
   }

   public static record LiteralResult(SlotRange slotRange) implements SlotSourceArgument.Result {
      public SlotSource value() {
         return RangeSlotSource.slotRange(this.slotRange);
      }

      public Optional name() {
         return Optional.of(this.slotRange.getSerializedName());
      }
   }

   public sealed interface Result permits SlotSourceArgument.HolderResult, SlotSourceArgument.LiteralResult {
      SlotSource value();

      Optional name();
   }
}
