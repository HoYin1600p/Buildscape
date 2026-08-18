package net.minecraft.core;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

public class IdMapper implements IdMap {
   private int nextId;
   private final Reference2IntMap tToId;
   private final List idToT;

   public IdMapper() {
      this(512);
   }

   public IdMapper(final int expectedSize) {
      this.idToT = Lists.newArrayListWithExpectedSize(expectedSize);
      this.tToId = new Reference2IntOpenHashMap(expectedSize);
      this.tToId.defaultReturnValue(-1);
   }

   public void addMapping(final Object thing, final int id) {
      this.tToId.put(thing, id);

      while(this.idToT.size() <= id) {
         this.idToT.add((Object)null);
      }

      this.idToT.set(id, thing);
      if (this.nextId <= id) {
         this.nextId = id + 1;
      }

   }

   public void add(final Object thing) {
      this.addMapping(thing, this.nextId);
   }

   public int getId(final Object thing) {
      return this.tToId.getInt(thing);
   }

   public final @Nullable Object byId(final int id) {
      return id >= 0 && id < this.idToT.size() ? this.idToT.get(id) : null;
   }

   public Iterator iterator() {
      return Iterators.filter(this.idToT.iterator(), Objects::nonNull);
   }

   public boolean contains(final int id) {
      return this.byId(id) != null;
   }

   public int size() {
      return this.tToId.size();
   }
}
