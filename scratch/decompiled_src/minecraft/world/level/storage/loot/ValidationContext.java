package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;

public class ValidationContext {
   private final ProblemReporter reporter;
   private final ContextKeySet contextKeySet;
   private final Optional resolver;
   private final Set visitedObjects;

   public ValidationContext(final ProblemReporter reporter, final ContextKeySet contextKeySet, final HolderGetter.Provider resolver) {
      this(reporter, contextKeySet, Optional.of(resolver), Set.of());
   }

   public ValidationContext(final ProblemReporter reporter, final ContextKeySet contextKeySet) {
      this(reporter, contextKeySet, Optional.empty(), Set.of());
   }

   private ValidationContext(final ProblemReporter reporter, final ContextKeySet contextKeySet, final Optional resolver, final Set visitedObjects) {
      this.reporter = reporter;
      this.contextKeySet = contextKeySet;
      this.resolver = resolver;
      this.visitedObjects = visitedObjects;
   }

   public ValidationContext forChild(final ProblemReporter.PathElement subContext) {
      return new ValidationContext(this.reporter.forChild(subContext), this.contextKeySet, this.resolver, this.visitedObjects);
   }

   public ValidationContext forField(final String name) {
      return this.forChild(new ProblemReporter.FieldPathElement(name));
   }

   public ValidationContext forIndexedField(final String name, final int index) {
      return this.forChild(new ProblemReporter.IndexedFieldPathElement(name, index));
   }

   public ValidationContext forMapField(final String name, final String key) {
      return this.forChild(new ProblemReporter.MapEntryPathElement(name, key));
   }

   private ValidationContext enterObject(final ProblemReporter.PathElement subContext, final Object object) {
      Set newVisitedElements = ImmutableSet.builder().addAll(this.visitedObjects).add(object).build();
      return new ValidationContext(this.reporter.forChild(subContext), this.contextKeySet, this.resolver, newVisitedElements);
   }

   public ValidationContext enterElement(final ProblemReporter.PathElement subContext, final ResourceKey element) {
      return this.enterObject(subContext, element);
   }

   public boolean hasVisitedElement(final ResourceKey element) {
      return this.visitedObjects.contains(element);
   }

   public ValidationContext enterTag(final ProblemReporter.PathElement subContext, final TagKey tag) {
      return this.enterObject(subContext, tag);
   }

   public boolean hasVisitedTag(final TagKey tag) {
      return this.visitedObjects.contains(tag);
   }

   public void reportProblem(final ProblemReporter.Problem description) {
      this.reporter.report(description);
   }

   public void validateContextUsage(final LootContextUser lootContextUser) {
      Set allReferenced = lootContextUser.getReferencedContextParams();
      Set notProvided = Sets.difference(allReferenced, this.contextKeySet.allowed());
      if (!notProvided.isEmpty()) {
         this.reporter.report(new ValidationContext.ParametersNotProvidedProblem(notProvided));
      }

   }

   public HolderGetter.Provider resolver() {
      return (HolderGetter.Provider)this.resolver.orElseThrow(() -> new UnsupportedOperationException("References not allowed"));
   }

   public ProblemReporter reporter() {
      return this.reporter;
   }

   public static record ParametersNotProvidedProblem(Set notProvided) implements ProblemReporter.Problem {
      public String description() {
         return "Parameters " + String.valueOf(this.notProvided) + " are not provided in this context";
      }
   }

   public static record RecursiveElementReferenceProblem(ResourceKey referenced) implements ProblemReporter.Problem {
      public String description() {
         return String.valueOf(this.referenced.identifier()) + " of type " + String.valueOf(this.referenced.registry()) + " is recursively called";
      }

      public boolean isFatal() {
         return true;
      }
   }

   public static record RecursiveTagReferenceProblem(TagKey referenced) implements ProblemReporter.Problem {
      public String description() {
         return "#" + String.valueOf(this.referenced.location()) + " of type " + String.valueOf(this.referenced.registry()) + " is recursively called";
      }

      public boolean isFatal() {
         return true;
      }
   }
}
