package net.minecraft.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public interface ProblemReporter {
   ProblemReporter DISCARDING = new ProblemReporter() {
      public ProblemReporter forChild(final ProblemReporter.PathElement path) {
         return this;
      }

      public void report(final ProblemReporter.Problem problem) {
      }
   };

   ProblemReporter forChild(ProblemReporter.PathElement path);

   void report(ProblemReporter.Problem problem);

   public static record CollectionReferencePathElement(TagKey id) implements ProblemReporter.PathElement {
      public String get() {
         return "->{#" + String.valueOf(this.id.location()) + "@" + String.valueOf(this.id.registry()) + "}";
      }
   }

   public static class Collector implements ProblemReporter {
      public static final ProblemReporter.PathElement EMPTY_ROOT = () -> "";
      private final ProblemReporter.@Nullable Collector parent;
      private final ProblemReporter.PathElement element;
      private final Set problems;

      public Collector() {
         this(EMPTY_ROOT);
      }

      public Collector(final ProblemReporter.PathElement root) {
         this.parent = null;
         this.problems = new LinkedHashSet();
         this.element = root;
      }

      private Collector(final ProblemReporter.Collector parent, final ProblemReporter.PathElement path) {
         this.problems = parent.problems;
         this.parent = parent;
         this.element = path;
      }

      public ProblemReporter forChild(final ProblemReporter.PathElement path) {
         return new ProblemReporter.Collector(this, path);
      }

      public void report(final ProblemReporter.Problem problem) {
         this.problems.add(new ProblemReporter.Collector.Entry(this, problem));
      }

      public boolean isEmpty() {
         return this.problems.isEmpty();
      }

      public boolean hasFatalProblems() {
         for(ProblemReporter.Collector.Entry entry : this.problems) {
            if (entry.problem.isFatal()) {
               return true;
            }
         }

         return false;
      }

      public void forEach(final BiConsumer output) {
         List pathElements = new ArrayList();
         StringBuilder pathString = new StringBuilder();

         for(ProblemReporter.Collector.Entry entry : this.problems) {
            for(ProblemReporter.Collector current = entry.source; current != null; current = current.parent) {
               pathElements.add(current.element);
            }

            for(int i = pathElements.size() - 1; i >= 0; --i) {
               pathString.append(((ProblemReporter.PathElement)pathElements.get(i)).get());
            }

            output.accept(pathString.toString(), entry.problem());
            pathString.setLength(0);
            pathElements.clear();
         }

      }

      public String getReport() {
         Multimap groupedProblems = HashMultimap.create();
         this.forEach(groupedProblems::put);
         return (String)groupedProblems.asMap().entrySet().stream().map((entry) -> " at " + (String)entry.getKey() + ": " + (String)((Collection)entry.getValue()).stream().map(ProblemReporter.Problem::description).collect(Collectors.joining("; "))).collect(Collectors.joining("\n"));
      }

      public String getTreeReport() {
         List pathElements = new ArrayList();
         ProblemReporter.Collector.ProblemTreeNode root = new ProblemReporter.Collector.ProblemTreeNode(this.element);

         for(ProblemReporter.Collector.Entry entry : this.problems) {
            for(ProblemReporter.Collector current = entry.source; current != this; current = current.parent) {
               pathElements.add(current.element);
            }

            ProblemReporter.Collector.ProblemTreeNode node = root;

            for(int i = pathElements.size() - 1; i >= 0; --i) {
               node = node.child((ProblemReporter.PathElement)pathElements.get(i));
            }

            pathElements.clear();
            node.problems.add(entry.problem);
         }

         return String.join("\n", root.getLines());
      }

      private static record Entry(ProblemReporter.Collector source, ProblemReporter.Problem problem) {
      }

      private static record ProblemTreeNode(ProblemReporter.PathElement element, List problems, Map children) {
         public ProblemTreeNode(final ProblemReporter.PathElement pathElement) {
            this(pathElement, new ArrayList(), new LinkedHashMap());
         }

         public ProblemReporter.Collector.ProblemTreeNode child(final ProblemReporter.PathElement id) {
            return (ProblemReporter.Collector.ProblemTreeNode)this.children.computeIfAbsent(id, ProblemReporter.Collector.ProblemTreeNode::new);
         }

         public List getLines() {
            int problemCount = this.problems.size();
            int childrenCount = this.children.size();
            if (problemCount == 0 && childrenCount == 0) {
               return List.of();
            } else if (problemCount == 0 && childrenCount == 1) {
               List lines = new ArrayList();
               this.children.forEach((element, child) -> lines.addAll(child.getLines()));
               lines.set(0, this.element.get() + (String)lines.get(0));
               return lines;
            } else if (problemCount == 1 && childrenCount == 0) {
               return List.of(this.element.get() + ": " + ((ProblemReporter.Problem)this.problems.getFirst()).description());
            } else {
               List lines = new ArrayList();
               this.children.forEach((element, child) -> lines.addAll(child.getLines()));
               lines.replaceAll((s) -> "  " + s);

               for(ProblemReporter.Problem problem : this.problems) {
                  lines.add("  " + problem.description());
               }

               lines.addFirst(this.element.get() + ":");
               return lines;
            }
         }
      }
   }

   public static record ElementReferencePathElement(ResourceKey id) implements ProblemReporter.PathElement {
      public String get() {
         return "->{" + String.valueOf(this.id.identifier()) + "@" + String.valueOf(this.id.registry()) + "}";
      }
   }

   public static record FieldPathElement(String name) implements ProblemReporter.PathElement {
      public String get() {
         return "." + this.name;
      }
   }

   public static record IndexedFieldPathElement(String name, int index) implements ProblemReporter.PathElement {
      public String get() {
         return "." + this.name + "[" + this.index + "]";
      }
   }

   public static record IndexedPathElement(int index) implements ProblemReporter.PathElement {
      public String get() {
         return "[" + this.index + "]";
      }
   }

   public static record MapEntryPathElement(String name, String key) implements ProblemReporter.PathElement {
      public String get() {
         return "." + this.name + "[" + this.key + "]";
      }
   }

   @FunctionalInterface
   public interface PathElement {
      String get();
   }

   public interface Problem {
      String description();

      default boolean isFatal() {
         return false;
      }
   }

   public static record RootElementPathElement(ResourceKey id) implements ProblemReporter.PathElement {
      public String get() {
         return "{" + String.valueOf(this.id.identifier()) + "@" + String.valueOf(this.id.registry()) + "}";
      }
   }

   public static record RootFieldPathElement(String name) implements ProblemReporter.PathElement {
      public String get() {
         return this.name;
      }
   }

   public static class ScopedCollector extends ProblemReporter.Collector implements AutoCloseable {
      private final Logger logger;

      public ScopedCollector(final Logger logger) {
         this.logger = logger;
      }

      public ScopedCollector(final ProblemReporter.PathElement root, final Logger logger) {
         super(root);
         this.logger = logger;
      }

      public void close() {
         if (!this.isEmpty()) {
            this.logger.warn("[{}] Serialization errors:\n{}", this.logger.getName(), this.getTreeReport());
         }

      }
   }
}
