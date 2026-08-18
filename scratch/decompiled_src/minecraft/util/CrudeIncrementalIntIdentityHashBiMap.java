package net.minecraft.util;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import java.util.Arrays;
import java.util.Iterator;
import net.minecraft.core.IdMap;
import org.jspecify.annotations.Nullable;

public class CrudeIncrementalIntIdentityHashBiMap implements IdMap {
   private static final int NOT_FOUND = -1;
   private static final Object EMPTY_SLOT = null;
   private static final float LOADFACTOR = 0.8F;
   private @Nullable Object[] keys;
   private int[] values;
   private @Nullable Object[] byId;
   private int nextId;
   private int size;

   private CrudeIncrementalIntIdentityHashBiMap(final int capacity) {
      this.keys = new Object[capacity];
      this.values = new int[capacity];
      this.byId = new Object[capacity];
   }

   private CrudeIncrementalIntIdentityHashBiMap(final Object[] keys, final int[] values, final Object[] byId, final int nextId, final int size) {
      this.keys = keys;
      this.values = values;
      this.byId = byId;
      this.nextId = nextId;
      this.size = size;
   }

   public static CrudeIncrementalIntIdentityHashBiMap create(final int initialCapacity) {
      return new CrudeIncrementalIntIdentityHashBiMap((int)((float)initialCapacity / 0.8F));
   }

   public int getId(final @Nullable Object thing) {
      return this.getValue(this.indexOf(thing, this.hash(thing)));
   }

   public @Nullable Object byId(final int id) {
      return id >= 0 && id < this.byId.length ? this.byId[id] : null;
   }

   private int getValue(final int index) {
      return index == -1 ? -1 : this.values[index];
   }

   public boolean contains(final Object key) {
      return this.getId(key) != -1;
   }

   public boolean contains(final int id) {
      return this.byId(id) != null;
   }

   public int add(final Object key) {
      int value = this.nextId();
      this.addMapping(key, value);
      return value;
   }

   private int nextId() {
      while(this.nextId < this.byId.length && this.byId[this.nextId] != null) {
         ++this.nextId;
      }

      return this.nextId;
   }

   private void grow(final int newSize) {
      Object[] oldKeys = (K[])this.keys;
      int[] oldValues = this.values;
      CrudeIncrementalIntIdentityHashBiMap resized = new CrudeIncrementalIntIdentityHashBiMap(newSize);

      for(int i = 0; i < oldKeys.length; ++i) {
         if (oldKeys[i] != null) {
            resized.addMapping(oldKeys[i], oldValues[i]);
         }
      }

      this.keys = resized.keys;
      this.values = resized.values;
      this.byId = resized.byId;
      this.nextId = resized.nextId;
      this.size = resized.size;
   }

   public void addMapping(final Object key, final int id) {
      int minSize = Math.max(id, this.size + 1);
      if ((float)minSize >= (float)this.keys.length * 0.8F) {
         int newSize;
         for(newSize = this.keys.length << 1; newSize < id; newSize <<= 1) {
         }

         this.grow(newSize);
      }

      int index = this.findEmpty(this.hash(key));
      this.keys[index] = key;
      this.values[index] = id;
      this.byId[id] = key;
      ++this.size;
      if (id == this.nextId) {
         ++this.nextId;
      }

   }

   private int hash(final @Nullable Object key) {
      return (Mth.murmurHash3Mixer(System.identityHashCode(key)) & Integer.MAX_VALUE) % this.keys.length;
   }

   private int indexOf(final @Nullable Object key, final int startFrom) {
      for(int i = startFrom; i < this.keys.length; ++i) {
         if (this.keys[i] == key) {
            return i;
         }

         if (this.keys[i] == EMPTY_SLOT) {
            return -1;
         }
      }

      for(int i = 0; i < startFrom; ++i) {
         if (this.keys[i] == key) {
            return i;
         }

         if (this.keys[i] == EMPTY_SLOT) {
            return -1;
         }
      }

      return -1;
   }

   private int findEmpty(final int startFrom) {
      for(int i = startFrom; i < this.keys.length; ++i) {
         if (this.keys[i] == EMPTY_SLOT) {
            return i;
         }
      }

      for(int i = 0; i < startFrom; ++i) {
         if (this.keys[i] == EMPTY_SLOT) {
            return i;
         }
      }

      throw new RuntimeException("Overflowed :(");
   }

   public Iterator iterator() {
      return Iterators.filter(Iterators.forArray(this.byId), Predicates.notNull());
   }

   public void clear() {
      Arrays.fill(this.keys, (Object)null);
      Arrays.fill(this.byId, (Object)null);
      this.nextId = 0;
      this.size = 0;
   }

   public int size() {
      return this.size;
   }

   public CrudeIncrementalIntIdentityHashBiMap copy() {
      return new CrudeIncrementalIntIdentityHashBiMap(this.keys.clone(), (int[])this.values.clone(), this.byId.clone(), this.nextId, this.size);
   }
}
