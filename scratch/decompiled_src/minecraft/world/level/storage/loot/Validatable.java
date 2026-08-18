package net.minecraft.world.level.storage.loot;

import com.mojang.serialization.DataResult;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.context.ContextKeySet;

public interface Validatable {
   void validate(ValidationContext context);

   static void validate(final ValidationContext context, final String name, final Validatable v) {
      v.validate(context.forField(name));
   }

   static void validate(final ValidationContext context, final String name, final Optional optional) {
      optional.ifPresent((v) -> v.validate(context.forField(name)));
   }

   private static void validateHolder(final ValidationContext context, final Holder holder) {
      ValidationContext elementContext;
      if (holder instanceof Holder.Reference reference) {
         ResourceKey id = reference.key();
         if (context.hasVisitedElement(id)) {
            context.reportProblem(new ValidationContext.RecursiveElementReferenceProblem(id));
            return;
         }

         elementContext = context.enterElement(new ProblemReporter.ElementReferencePathElement(id), id);
      } else {
         elementContext = context;
      }

      ((Validatable)holder.value()).validate(elementContext);
   }

   static void validateHolder(final ValidationContext context, final String name, final Holder holder) {
      validateHolder(context.forField(name), holder);
   }

   static void validateHolder(final ValidationContext context, final String name, final Optional optional) {
      optional.ifPresent((v) -> validateHolder(context, name, v));
   }

   static void validateHolder(final ValidationContext context, final String name, final List list) {
      for(int i = 0; i < list.size(); ++i) {
         ((Validatable)((Holder)list.get(i)).value()).validate(context.forIndexedField(name, i));
      }

   }

   static void validateHolderSet(final ValidationContext context, final String name, final HolderSet holderSet) {
      validateHolderSet(context.forField(name), holderSet);
   }

   private static void validateHolderSet(final ValidationContext context, final HolderSet holderSet) {
      if (holderSet.isBound()) {
         ValidationContext collectionContext;
         if (holderSet instanceof HolderSet.Named) {
            HolderSet.Named reference = (HolderSet.Named)holderSet;
            TagKey id = reference.key();
            if (context.hasVisitedTag(id)) {
               context.reportProblem(new ValidationContext.RecursiveTagReferenceProblem(id));
               return;
            }

            collectionContext = context.enterTag(new ProblemReporter.CollectionReferencePathElement(id), id);
         } else {
            collectionContext = context;
         }

         for(int i = 0; i < holderSet.size(); ++i) {
            validateHolder(collectionContext.forChild(new ProblemReporter.IndexedPathElement(i)), holderSet.get(i));
         }

      }
   }

   static void validate(final ValidationContext context, final String name, final List list) {
      for(int i = 0; i < list.size(); ++i) {
         ((Validatable)list.get(i)).validate(context.forIndexedField(name, i));
      }

   }

   static void validate(final ValidationContext context, final List list) {
      for(int i = 0; i < list.size(); ++i) {
         ((Validatable)list.get(i)).validate(context.forChild(new ProblemReporter.IndexedPathElement(i)));
      }

   }

   static Function validatorForContext(final ContextKeySet params) {
      return (v) -> {
         ProblemReporter.Collector problemCollector = new ProblemReporter.Collector();
         ValidationContext validationContext = new ValidationContext(problemCollector, params);
         v.validate(validationContext);
         return !problemCollector.isEmpty() ? DataResult.error(() -> "Validation error: " + problemCollector.getReport()) : DataResult.success(v);
      };
   }

   static Function listValidatorForContext(final ContextKeySet params) {
      return (v) -> {
         ProblemReporter.Collector problemCollector = new ProblemReporter.Collector();
         ValidationContext validationContext = new ValidationContext(problemCollector, params);
         validate(validationContext, v);
         return !problemCollector.isEmpty() ? DataResult.error(() -> "Validation error: " + problemCollector.getReport()) : DataResult.success(v);
      };
   }
}
