package net.minecraft.core;

import com.google.common.collect.Lists;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

public class NonNullList extends AbstractList {
   private final List list;
   private final @Nullable Object defaultValue;

   public static NonNullList create() {
      return new NonNullList(Lists.newArrayList(), (Object)null);
   }

   public static NonNullList createWithCapacity(final int capacity) {
      return new NonNullList(Lists.newArrayListWithCapacity(capacity), (Object)null);
   }

   public static NonNullList withSize(final int size, final Object defaultValue) {
      Objects.requireNonNull(defaultValue);
      Object[] objects = new Object[size];
      Arrays.fill(objects, defaultValue);
      return new NonNullList(Arrays.asList(objects), defaultValue);
   }

   @SafeVarargs
   public static NonNullList of(final Object defaultValue, final Object... values) {
      return new NonNullList(Arrays.asList(values), defaultValue);
   }

   protected NonNullList(final List list, final @Nullable Object defaultValue) {
      this.list = list;
      this.defaultValue = defaultValue;
   }

   public Object get(final int index) {
      return this.list.get(index);
   }

   public Object set(final int index, final Object element) {
      Objects.requireNonNull(element);
      return this.list.set(index, element);
   }

   public void add(final int index, final Object element) {
      Objects.requireNonNull(element);
      this.list.add(index, element);
   }

   public Object remove(final int index) {
      return this.list.remove(index);
   }

   public int size() {
      return this.list.size();
   }

   public void clear() {
      if (this.defaultValue == null) {
         super.clear();
      } else {
         for(int i = 0; i < this.size(); ++i) {
            this.set(i, this.defaultValue);
         }
      }

   }
}
