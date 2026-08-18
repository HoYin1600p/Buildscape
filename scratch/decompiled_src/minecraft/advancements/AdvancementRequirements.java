package net.minecraft.advancements;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record AdvancementRequirements(List requirements) {
   public static final Codec CODEC = Codec.STRING.listOf().listOf().xmap(AdvancementRequirements::new, AdvancementRequirements::requirements);
   public static final StreamCodec STREAM_CODEC = ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()).apply(ByteBufCodecs.list()).map(AdvancementRequirements::new, AdvancementRequirements::requirements);
   public static final AdvancementRequirements EMPTY = new AdvancementRequirements(List.of());

   public static AdvancementRequirements allOf(final Collection criteria) {
      return new AdvancementRequirements(criteria.stream().map(List::of).toList());
   }

   public static AdvancementRequirements anyOf(final Collection criteria) {
      return new AdvancementRequirements(List.of(List.copyOf(criteria)));
   }

   public int size() {
      return this.requirements.size();
   }

   public boolean test(final Predicate predicate) {
      if (this.requirements.isEmpty()) {
         return false;
      } else {
         for(List set : this.requirements) {
            if (!anyMatch(set, predicate)) {
               return false;
            }
         }

         return true;
      }
   }

   public int count(final Predicate predicate) {
      int count = 0;

      for(List set : this.requirements) {
         if (anyMatch(set, predicate)) {
            ++count;
         }
      }

      return count;
   }

   private static boolean anyMatch(final List criteria, final Predicate predicate) {
      for(String criterion : criteria) {
         if (predicate.test(criterion)) {
            return true;
         }
      }

      return false;
   }

   public DataResult validate(final Set expectedCriteria) {
      Set referencedCriteria = new ObjectOpenHashSet();

      for(List set : this.requirements) {
         if (set.isEmpty() && expectedCriteria.isEmpty()) {
            return DataResult.error(() -> "Requirement entry cannot be empty");
         }

         referencedCriteria.addAll(set);
      }

      if (!expectedCriteria.equals(referencedCriteria)) {
         Set missingCriteria = Sets.difference(expectedCriteria, referencedCriteria);
         Set unknownCriteria = Sets.difference(referencedCriteria, expectedCriteria);
         return DataResult.error(() -> "Advancement completion requirements did not exactly match specified criteria. Missing: " + String.valueOf(missingCriteria) + ". Unknown: " + String.valueOf(unknownCriteria));
      } else {
         return DataResult.success(this);
      }
   }

   public boolean isEmpty() {
      return this.requirements.isEmpty();
   }

   public String toString() {
      return this.requirements.toString();
   }

   public Set names() {
      Set names = new ObjectOpenHashSet();

      for(List set : this.requirements) {
         names.addAll(set);
      }

      return names;
   }

   public interface Strategy {
      AdvancementRequirements.Strategy AND = AdvancementRequirements::allOf;
      AdvancementRequirements.Strategy OR = AdvancementRequirements::anyOf;

      AdvancementRequirements create(Collection criteria);
   }
}
