package net.minecraft.world.entity.variant;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;

public interface PriorityProvider {
   List selectors();

   static Stream select(final Stream entries, final Function extractor, final Object context) {
      List unpackedEntries = new ArrayList();
      entries.forEach((entryx) -> {
         PriorityProvider provider = (PriorityProvider)extractor.apply(entryx);

         for(PriorityProvider.Selector selector : provider.selectors()) {
            unpackedEntries.add(new PriorityProvider.UnpackedEntry(entryx, selector.priority(), (PriorityProvider.SelectorCondition)DataFixUtils.orElseGet(selector.condition(), PriorityProvider.SelectorCondition::alwaysTrue)));
         }

      });
      unpackedEntries.sort(PriorityProvider.UnpackedEntry.HIGHEST_PRIORITY_FIRST);
      Iterator iterator = unpackedEntries.iterator();
      int highestMatchedPriority = Integer.MIN_VALUE;

      while(iterator.hasNext()) {
         PriorityProvider.UnpackedEntry entry = (PriorityProvider.UnpackedEntry)iterator.next();
         if (entry.priority < highestMatchedPriority) {
            iterator.remove();
         } else if (entry.condition.test(context)) {
            highestMatchedPriority = entry.priority;
         } else {
            iterator.remove();
         }
      }

      return unpackedEntries.stream().map(PriorityProvider.UnpackedEntry::entry);
   }

   static Optional pick(final Stream entries, final Function extractor, final RandomSource randomSource, final Object context) {
      List selected = select(entries, extractor, context).toList();
      return Util.getRandomSafe(selected, randomSource);
   }

   static List single(final PriorityProvider.SelectorCondition check, final int priority) {
      return List.of(new PriorityProvider.Selector(check, priority));
   }

   static List alwaysTrue(final int priority) {
      return List.of(new PriorityProvider.Selector(Optional.empty(), priority));
   }

   public static record Selector(Optional condition, int priority) {
      public Selector(final PriorityProvider.SelectorCondition condition, final int priority) {
         this(Optional.of(condition), priority);
      }

      public Selector(final int priority) {
         this(Optional.empty(), priority);
      }

      public static Codec codec(final Codec conditionCodec) {
         return RecordCodecBuilder.create((i) -> i.group(conditionCodec.optionalFieldOf("condition").forGetter(PriorityProvider.Selector::condition), Codec.INT.fieldOf("priority").forGetter(PriorityProvider.Selector::priority)).apply(i, PriorityProvider.Selector::new));
      }
   }

   @FunctionalInterface
   public interface SelectorCondition extends Predicate {
      static PriorityProvider.SelectorCondition alwaysTrue() {
         return (context) -> true;
      }
   }

   public static record UnpackedEntry(Object entry, int priority, PriorityProvider.SelectorCondition condition) {
      public static final Comparator HIGHEST_PRIORITY_FIRST = Comparator.comparingInt(PriorityProvider.UnpackedEntry::priority).reversed();
   }
}
