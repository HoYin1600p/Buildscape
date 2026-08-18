package net.minecraft.client.resources.model.geometry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.List;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public class QuadCollection {
   public static final QuadCollection EMPTY = new QuadCollection(List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), List.of());
   private static final int FLAGS_NOT_COMPUTED = -1;
   private final List all;
   private final List unculled;
   private final List north;
   private final List south;
   private final List east;
   private final List west;
   private final List up;
   private final List down;
   private int materialFlags = -1;

   private QuadCollection(final List all, final List unculled, final List north, final List south, final List east, final List west, final List up, final List down) {
      this.all = all;
      this.unculled = unculled;
      this.north = north;
      this.south = south;
      this.east = east;
      this.west = west;
      this.up = up;
      this.down = down;
   }

   private static @BakedQuad.MaterialFlags int computeMaterialFlags(final List quads) {
      int flags = 0;

      for(BakedQuad quad : quads) {
         flags |= quad.materialInfo().flags();
      }

      return flags;
   }

   public List getQuads(final @Nullable Direction direction) {
      byte var3 = 0;
      List var10000;
      switch (direction.enumSwitch<invokedynamic>(direction, var3)) {
         case -1:
            var10000 = this.unculled;
            break;
         case 0:
            var10000 = this.north;
            break;
         case 1:
            var10000 = this.south;
            break;
         case 2:
            var10000 = this.east;
            break;
         case 3:
            var10000 = this.west;
            break;
         case 4:
            var10000 = this.up;
            break;
         case 5:
            var10000 = this.down;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public List getAll() {
      return this.all;
   }

   public @BakedQuad.MaterialFlags int materialFlags() {
      if (this.materialFlags == -1) {
         this.materialFlags = computeMaterialFlags(this.all);
      }

      return this.materialFlags;
   }

   public boolean hasMaterialFlag(final @BakedQuad.MaterialFlags int flag) {
      return (this.materialFlags() & flag) != 0;
   }

   public static class Builder {
      private final ImmutableList.Builder unculledFaces = ImmutableList.builder();
      private final Multimap culledFaces = ArrayListMultimap.create();

      public QuadCollection.Builder addCulledFace(final Direction direction, final BakedQuad quad) {
         this.culledFaces.put(direction, quad);
         return this;
      }

      public QuadCollection.Builder addUnculledFace(final BakedQuad quad) {
         this.unculledFaces.add(quad);
         return this;
      }

      public QuadCollection.Builder addAll(final QuadCollection quadCollection) {
         this.culledFaces.putAll(Direction.UP, quadCollection.up);
         this.culledFaces.putAll(Direction.DOWN, quadCollection.down);
         this.culledFaces.putAll(Direction.NORTH, quadCollection.north);
         this.culledFaces.putAll(Direction.SOUTH, quadCollection.south);
         this.culledFaces.putAll(Direction.EAST, quadCollection.east);
         this.culledFaces.putAll(Direction.WEST, quadCollection.west);
         this.unculledFaces.addAll(quadCollection.unculled);
         return this;
      }

      private static QuadCollection createFromSublists(final List all, final int unculledCount, final int northCount, final int southCount, final int eastCount, final int westCount, final int upCount, final int downCount) {
         int index = 0;
         int var16;
         List unculled = all.subList(index, var16 = index + unculledCount);
         List north = all.subList(var16, index = var16 + northCount);
         int var18;
         List south = all.subList(index, var18 = index + southCount);
         List east = all.subList(var18, index = var18 + eastCount);
         int var20;
         List west = all.subList(index, var20 = index + westCount);
         List up = all.subList(var20, index = var20 + upCount);
         List down = all.subList(index, index + downCount);
         return new QuadCollection(all, unculled, north, south, east, west, up, down);
      }

      public QuadCollection build() {
         ImmutableList unculledFaces = this.unculledFaces.build();
         if (this.culledFaces.isEmpty()) {
            return unculledFaces.isEmpty() ? QuadCollection.EMPTY : new QuadCollection(unculledFaces, unculledFaces, List.of(), List.of(), List.of(), List.of(), List.of(), List.of());
         } else {
            ImmutableList.Builder quads = ImmutableList.builder();
            quads.addAll(unculledFaces);
            Collection north = this.culledFaces.get(Direction.NORTH);
            quads.addAll(north);
            Collection south = this.culledFaces.get(Direction.SOUTH);
            quads.addAll(south);
            Collection east = this.culledFaces.get(Direction.EAST);
            quads.addAll(east);
            Collection west = this.culledFaces.get(Direction.WEST);
            quads.addAll(west);
            Collection up = this.culledFaces.get(Direction.UP);
            quads.addAll(up);
            Collection down = this.culledFaces.get(Direction.DOWN);
            quads.addAll(down);
            return createFromSublists(quads.build(), unculledFaces.size(), north.size(), south.size(), east.size(), west.size(), up.size(), down.size());
         }
      }
   }
}
