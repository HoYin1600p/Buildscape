package net.minecraft.world.level.biome;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import net.minecraft.core.QuartPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunctions;
import org.jspecify.annotations.Nullable;

public class Climate {
   private static final boolean DEBUG_SLOW_BIOME_SEARCH = false;
   private static final float QUANTIZATION_FACTOR = 10000.0F;
   @VisibleForTesting
   protected static final int PARAMETER_COUNT = 7;

   public static Climate.TargetPoint target(final float temperature, final float humidity, final float continentalness, final float erosion, final float depth, final float weirdness) {
      return new Climate.TargetPoint(quantizeCoord(temperature), quantizeCoord(humidity), quantizeCoord(continentalness), quantizeCoord(erosion), quantizeCoord(depth), quantizeCoord(weirdness));
   }

   public static Climate.ParameterPoint parameters(final float temperature, final float humidity, final float continentalness, final float erosion, final float depth, final float weirdness, final float offset) {
      return new Climate.ParameterPoint(Climate.Parameter.point(temperature), Climate.Parameter.point(humidity), Climate.Parameter.point(continentalness), Climate.Parameter.point(erosion), Climate.Parameter.point(depth), Climate.Parameter.point(weirdness), quantizeCoord(offset));
   }

   public static Climate.ParameterPoint parameters(final Climate.Parameter temperature, final Climate.Parameter humidity, final Climate.Parameter continentalness, final Climate.Parameter erosion, final Climate.Parameter depth, final Climate.Parameter weirdness, final float offset) {
      return new Climate.ParameterPoint(temperature, humidity, continentalness, erosion, depth, weirdness, quantizeCoord(offset));
   }

   public static long quantizeCoord(final float coord) {
      return (long)(coord * 10000.0F);
   }

   public static float unquantizeCoord(final long coord) {
      return (float)coord / 10000.0F;
   }

   public static Climate.Sampler empty() {
      DensityFunction zero = DensityFunctions.zero();
      return new Climate.Sampler(zero, zero, zero, zero, zero, zero);
   }

   @VisibleForTesting
   interface DistanceMetric {
      long distance(Climate.RTree.Node node, long[] target);
   }

   public static record Parameter(long min, long max) {
      public static final Codec CODEC = ExtraCodecs.intervalCodec(Codec.floatRange(-2.0F, 2.0F), "min", "max", (min, max) -> min.compareTo(max) > 0 ? DataResult.error(() -> "Cannon construct interval, min > max (" + min + " > " + max + ")") : DataResult.success(new Climate.Parameter(Climate.quantizeCoord(min), Climate.quantizeCoord(max))), (p) -> Climate.unquantizeCoord(p.min()), (p) -> Climate.unquantizeCoord(p.max()));

      public static Climate.Parameter point(final float min) {
         return span(min, min);
      }

      public static Climate.Parameter span(final float min, final float max) {
         if (min > max) {
            throw new IllegalArgumentException("min > max: " + min + " " + max);
         } else {
            return new Climate.Parameter(Climate.quantizeCoord(min), Climate.quantizeCoord(max));
         }
      }

      public static Climate.Parameter span(final Climate.Parameter min, final Climate.Parameter max) {
         if (min.min() > max.max()) {
            throw new IllegalArgumentException("min > max: " + String.valueOf(min) + " " + String.valueOf(max));
         } else {
            return new Climate.Parameter(min.min(), max.max());
         }
      }

      public String toString() {
         return this.min == this.max ? String.format(Locale.ROOT, "%d", this.min) : String.format(Locale.ROOT, "[%d-%d]", this.min, this.max);
      }

      public long distance(final long target) {
         long above = target - this.max;
         long below = this.min - target;
         return above > 0L ? above : Math.max(below, 0L);
      }

      public Climate.Parameter span(final Climate.@Nullable Parameter other) {
         return other == null ? this : new Climate.Parameter(Math.min(this.min, other.min()), Math.max(this.max, other.max()));
      }
   }

   public static class ParameterList {
      private final List values;
      private final Climate.RTree index;

      public static Codec codec(final MapCodec valueCodec) {
         return ExtraCodecs.nonEmptyList(RecordCodecBuilder.create((i) -> i.group(Climate.ParameterPoint.CODEC.fieldOf("parameters").forGetter(Pair::getFirst), valueCodec.forGetter(Pair::getSecond)).apply(i, Pair::of)).listOf()).xmap(Climate.ParameterList::new, Climate.ParameterList::values);
      }

      public ParameterList(final List values) {
         this(values, 19);
      }

      private ParameterList(final List values, final int childrenPerNode) {
         this.values = values;
         this.index = Climate.RTree.create(values, childrenPerNode);
      }

      @VisibleForTesting
      public Climate.ParameterList rebuildWithChildrenPerNode(final int childrenPerNode) {
         return new Climate.ParameterList(this.values, childrenPerNode);
      }

      public List values() {
         return this.values;
      }

      public Object findValue(final Climate.TargetPoint target) {
         return this.findValueIndex(target);
      }

      @VisibleForTesting
      public Object findValueBruteForce(final Climate.TargetPoint target) {
         Iterator iterator = this.values().iterator();
         Pair first = (Pair)iterator.next();
         long bestFitness = ((Climate.ParameterPoint)first.getFirst()).fitness(target);
         Object best = (T)first.getSecond();

         while(iterator.hasNext()) {
            Pair parameter = (Pair)iterator.next();
            long fitness = ((Climate.ParameterPoint)parameter.getFirst()).fitness(target);
            if (fitness < bestFitness) {
               bestFitness = fitness;
               best = (T)parameter.getSecond();
            }
         }

         return best;
      }

      public Object findValueIndex(final Climate.TargetPoint target) {
         return this.findValueIndex(target, Climate.RTree.Node::distance);
      }

      protected Object findValueIndex(final Climate.TargetPoint target, final Climate.DistanceMetric distanceMetric) {
         return this.index.search(target, distanceMetric);
      }
   }

   public static record ParameterPoint(Climate.Parameter temperature, Climate.Parameter humidity, Climate.Parameter continentalness, Climate.Parameter erosion, Climate.Parameter depth, Climate.Parameter weirdness, long offset) {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(Climate.Parameter.CODEC.fieldOf("temperature").forGetter((p) -> p.temperature), Climate.Parameter.CODEC.fieldOf("humidity").forGetter((p) -> p.humidity), Climate.Parameter.CODEC.fieldOf("continentalness").forGetter((p) -> p.continentalness), Climate.Parameter.CODEC.fieldOf("erosion").forGetter((p) -> p.erosion), Climate.Parameter.CODEC.fieldOf("depth").forGetter((p) -> p.depth), Climate.Parameter.CODEC.fieldOf("weirdness").forGetter((p) -> p.weirdness), Codec.floatRange(0.0F, 1.0F).fieldOf("offset").xmap(Climate::quantizeCoord, Climate::unquantizeCoord).forGetter((p) -> p.offset)).apply(i, Climate.ParameterPoint::new));

      public long fitness(final Climate.TargetPoint target) {
         return Mth.square(this.temperature.distance(target.temperature)) + Mth.square(this.humidity.distance(target.humidity)) + Mth.square(this.continentalness.distance(target.continentalness)) + Mth.square(this.erosion.distance(target.erosion)) + Mth.square(this.depth.distance(target.depth)) + Mth.square(this.weirdness.distance(target.weirdness)) + Mth.square(this.offset);
      }

      List parameterSpace() {
         return ImmutableList.of(this.temperature, this.humidity, this.continentalness, this.erosion, this.depth, this.weirdness, new Climate.Parameter(this.offset, this.offset));
      }
   }

   protected static final class RTree {
      private static final int CHILDREN_PER_NODE = 19;
      private final Climate.RTree.Node root;
      private final ThreadLocal lastResult = new ThreadLocal();

      private RTree(final Climate.RTree.Node root) {
         this.root = root;
      }

      public static Climate.RTree create(final List values) {
         return create(values, 19);
      }

      public static Climate.RTree create(final List values, final int childrenPerNode) {
         if (values.isEmpty()) {
            throw new IllegalArgumentException("Need at least one value to build the search tree.");
         } else {
            int dimensions = ((Climate.ParameterPoint)((Pair)values.get(0)).getFirst()).parameterSpace().size();
            if (dimensions != 7) {
               throw new IllegalStateException("Expecting parameter space to be 7, got " + dimensions);
            } else {
               List leaves = (List)values.stream().map((p) -> new Climate.RTree.Leaf((Climate.ParameterPoint)p.getFirst(), p.getSecond())).collect(Collectors.toCollection(ArrayList::new));
               return new Climate.RTree(build(dimensions, leaves, childrenPerNode));
            }
         }
      }

      private static Climate.RTree.Node build(final int dimensions, final List children, final int childrenPerNode) {
         if (children.isEmpty()) {
            throw new IllegalStateException("Need at least one child to build a node");
         } else if (children.size() == 1) {
            return (Climate.RTree.Node)children.get(0);
         } else if (children.size() <= childrenPerNode) {
            children.sort(Comparator.comparingLong((leaf) -> {
               long totalMagnitude = 0L;

               for(int d = 0; d < dimensions; ++d) {
                  Climate.Parameter parameter = leaf.parameterSpace[d];
                  totalMagnitude += Math.abs((parameter.min() + parameter.max()) / 2L);
               }

               return totalMagnitude;
            }));
            return new Climate.RTree.SubTree(children);
         } else {
            long minCost = Long.MAX_VALUE;
            int minDimension = -1;
            List minBuckets = null;

            for(int d = 0; d < dimensions; ++d) {
               sort(children, dimensions, d, false);
               List buckets = bucketize(children, childrenPerNode);
               long totalCost = 0L;

               for(Climate.RTree.SubTree bucket : buckets) {
                  totalCost += cost(bucket.parameterSpace);
               }

               if (minCost > totalCost) {
                  minCost = totalCost;
                  minDimension = d;
                  minBuckets = buckets;
               }
            }

            sort(minBuckets, dimensions, minDimension, true);
            return new Climate.RTree.SubTree((List)minBuckets.stream().map((b) -> build(dimensions, Arrays.asList(b.children), childrenPerNode)).collect(Collectors.toList()));
         }
      }

      private static void sort(final List children, final int dimensions, final int dimension, final boolean absolute) {
         Comparator comparator = comparator(dimension, absolute);

         for(int d = 1; d < dimensions; ++d) {
            comparator = comparator.thenComparing(comparator((dimension + d) % dimensions, absolute));
         }

         children.sort(comparator);
      }

      private static Comparator comparator(final int dimension, final boolean absolute) {
         return Comparator.comparingLong((leaf) -> {
            Climate.Parameter parameter = leaf.parameterSpace[dimension];
            long center = (parameter.min() + parameter.max()) / 2L;
            return absolute ? Math.abs(center) : center;
         });
      }

      private static List bucketize(final List nodes, final int childrenPerNode) {
         List buckets = Lists.newArrayList();
         List children = Lists.newArrayList();
         int expectedChildrenCount = (int)Math.pow((double)childrenPerNode, Math.floor(Math.log((double)nodes.size() - 0.01D) / Math.log((double)childrenPerNode)));

         for(Climate.RTree.Node child : nodes) {
            children.add(child);
            if (children.size() >= expectedChildrenCount) {
               buckets.add(new Climate.RTree.SubTree(children));
               children = Lists.newArrayList();
            }
         }

         if (!children.isEmpty()) {
            buckets.add(new Climate.RTree.SubTree(children));
         }

         return buckets;
      }

      private static long cost(final Climate.Parameter[] parameterSpace) {
         long result = 0L;

         for(Climate.Parameter parameter : parameterSpace) {
            result += Math.abs(parameter.max() - parameter.min());
         }

         return result;
      }

      private static List buildParameterSpace(final List children) {
         if (children.isEmpty()) {
            throw new IllegalArgumentException("SubTree needs at least one child");
         } else {
            int dimensions = 7;
            List bounds = Lists.newArrayList();

            for(int d = 0; d < 7; ++d) {
               bounds.add((Object)null);
            }

            for(Climate.RTree.Node child : children) {
               for(int d = 0; d < 7; ++d) {
                  bounds.set(d, child.parameterSpace[d].span((Climate.Parameter)bounds.get(d)));
               }
            }

            return bounds;
         }
      }

      public Object search(final Climate.TargetPoint target, final Climate.DistanceMetric distanceMetric) {
         long[] targetArray = target.toParameterArray();
         Climate.RTree.Leaf leaf = this.root.search(targetArray, (Climate.RTree.Leaf)this.lastResult.get(), distanceMetric);
         this.lastResult.set(leaf);
         return leaf.value;
      }

      private static final class Leaf extends Climate.RTree.Node {
         private final Object value;

         private Leaf(final Climate.ParameterPoint parameterPoint, final Object value) {
            super(parameterPoint.parameterSpace());
            this.value = value;
         }

         protected Climate.RTree.Leaf search(final long[] target, final Climate.RTree.@Nullable Leaf candidate, final Climate.DistanceMetric distanceMetric) {
            return this;
         }
      }

      @VisibleForTesting
      abstract static class Node {
         protected final Climate.Parameter[] parameterSpace;

         protected Node(final List parameterSpace) {
            this.parameterSpace = (Climate.Parameter[])parameterSpace.toArray(new Climate.Parameter[0]);
         }

         protected abstract Climate.RTree.Leaf search(final long[] target, final Climate.RTree.@Nullable Leaf candidate, final Climate.DistanceMetric distanceMetric);

         protected long distance(final long[] target) {
            long distance = 0L;

            for(int i = 0; i < 7; ++i) {
               distance += Mth.square(this.parameterSpace[i].distance(target[i]));
            }

            return distance;
         }

         public String toString() {
            return Arrays.toString(this.parameterSpace);
         }
      }

      private static final class SubTree extends Climate.RTree.Node {
         private final Climate.RTree.Node[] children;

         public SubTree(final List children) {
            this(Climate.RTree.buildParameterSpace(children), children);
         }

         public SubTree(final List parameterSpace, final List children) {
            super(parameterSpace);
            this.children = (Climate.RTree.Node[])children.toArray(new Climate.RTree.Node[0]);
         }

         protected Climate.RTree.Leaf search(final long[] target, final Climate.RTree.@Nullable Leaf candidate, final Climate.DistanceMetric distanceMetric) {
            long minDistance = candidate == null ? Long.MAX_VALUE : distanceMetric.distance(candidate, target);
            Climate.RTree.Leaf closestLeaf = candidate;

            for(Climate.RTree.Node child : this.children) {
               long childDistance = distanceMetric.distance(child, target);
               if (minDistance > childDistance) {
                  Climate.RTree.Leaf leaf = child.search(target, closestLeaf, distanceMetric);
                  long leafDistance = child == leaf ? childDistance : distanceMetric.distance(leaf, target);
                  if (minDistance > leafDistance) {
                     minDistance = leafDistance;
                     closestLeaf = leaf;
                  }
               }
            }

            return closestLeaf;
         }
      }
   }

   public static record Sampler(DensityFunction temperature, DensityFunction humidity, DensityFunction continentalness, DensityFunction erosion, DensityFunction depth, DensityFunction weirdness) {
      public Climate.TargetPoint sample(final int quartX, final int quartY, final int quartZ) {
         int blockX = QuartPos.toBlock(quartX);
         int blockY = QuartPos.toBlock(quartY);
         int blockZ = QuartPos.toBlock(quartZ);
         DensityFunction.SinglePointContext context = new DensityFunction.SinglePointContext(blockX, blockY, blockZ);
         return Climate.target(this.temperature.compute(context), this.humidity.compute(context), this.continentalness.compute(context), this.erosion.compute(context), this.depth.compute(context), this.weirdness.compute(context));
      }
   }

   public static record TargetPoint(long temperature, long humidity, long continentalness, long erosion, long depth, long weirdness) {
      @VisibleForTesting
      long[] toParameterArray() {
         return new long[]{this.temperature, this.humidity, this.continentalness, this.erosion, this.depth, this.weirdness, 0L};
      }
   }
}
