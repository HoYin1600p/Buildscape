package net.minecraft.util;

import com.google.common.annotations.VisibleForTesting;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import org.jspecify.annotations.Nullable;

public class ArrayListDeque extends AbstractList implements ListAndDeque {
   private static final int MIN_GROWTH = 1;
   private @Nullable Object[] contents;
   private int head;
   private int size;

   public ArrayListDeque() {
      this(1);
   }

   public ArrayListDeque(final int capacity) {
      this.contents = new Object[capacity];
      this.head = 0;
      this.size = 0;
   }

   public int size() {
      return this.size;
   }

   @VisibleForTesting
   public int capacity() {
      return this.contents.length;
   }

   private int getIndex(final int index) {
      return (index + this.head) % this.contents.length;
   }

   public Object get(final int index) {
      this.verifyIndexInRange(index);
      return this.getInner(this.getIndex(index));
   }

   private static void verifyIndexInRange(final int index, final int size) {
      if (index < 0 || index >= size) {
         throw new IndexOutOfBoundsException(index);
      }
   }

   private void verifyIndexInRange(final int index) {
      verifyIndexInRange(index, this.size);
   }

   private Object getInner(final int innerIndex) {
      return this.contents[innerIndex];
   }

   public Object set(final int index, final Object element) {
      this.verifyIndexInRange(index);
      Objects.requireNonNull(element);
      int innerIndex = this.getIndex(index);
      Object current = (T)this.getInner(innerIndex);
      this.contents[innerIndex] = element;
      return current;
   }

   public void add(final int index, final Object element) {
      verifyIndexInRange(index, this.size + 1);
      Objects.requireNonNull(element);
      if (this.size == this.contents.length) {
         this.grow();
      }

      int internalIndex = this.getIndex(index);
      if (index == this.size) {
         this.contents[internalIndex] = element;
      } else if (index == 0) {
         --this.head;
         if (this.head < 0) {
            this.head += this.contents.length;
         }

         this.contents[this.getIndex(0)] = element;
      } else {
         for(int i = this.size - 1; i >= index; --i) {
            this.contents[this.getIndex(i + 1)] = this.contents[this.getIndex(i)];
         }

         this.contents[internalIndex] = element;
      }

      ++this.modCount;
      ++this.size;
   }

   private void grow() {
      int newLength = this.contents.length + Math.max(this.contents.length >> 1, 1);
      Object[] newContents = new Object[newLength];
      this.copyCount(newContents, this.size);
      this.head = 0;
      this.contents = newContents;
   }

   public Object remove(final int index) {
      this.verifyIndexInRange(index);
      int innerIndex = this.getIndex(index);
      Object value = (T)this.getInner(innerIndex);
      if (index == 0) {
         this.contents[innerIndex] = null;
         ++this.head;
      } else if (index == this.size - 1) {
         this.contents[innerIndex] = null;
      } else {
         for(int i = index + 1; i < this.size; ++i) {
            this.contents[this.getIndex(i - 1)] = this.get(i);
         }

         this.contents[this.getIndex(this.size - 1)] = null;
      }

      ++this.modCount;
      --this.size;
      return value;
   }

   public boolean removeIf(final Predicate filter) {
      int removed = 0;

      for(int i = 0; i < this.size; ++i) {
         Object value = (T)this.get(i);
         if (filter.test(value)) {
            ++removed;
         } else if (removed != 0) {
            this.contents[this.getIndex(i - removed)] = value;
            this.contents[this.getIndex(i)] = null;
         }
      }

      this.modCount += removed;
      this.size -= removed;
      return removed != 0;
   }

   private void copyCount(final Object[] newContents, final int count) {
      for(int i = 0; i < count; ++i) {
         newContents[i] = this.get(i);
      }

   }

   public void replaceAll(final UnaryOperator operator) {
      for(int i = 0; i < this.size; ++i) {
         int index = this.getIndex(i);
         this.contents[index] = Objects.requireNonNull(operator.apply(this.getInner(i)));
      }

   }

   public void forEach(final Consumer action) {
      for(int i = 0; i < this.size; ++i) {
         action.accept(this.get(i));
      }

   }

   public void addFirst(final Object value) {
      this.add(0, value);
   }

   public void addLast(final Object value) {
      this.add(this.size, value);
   }

   public boolean offerFirst(final Object value) {
      this.addFirst(value);
      return true;
   }

   public boolean offerLast(final Object value) {
      this.addLast(value);
      return true;
   }

   public Object removeFirst() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.remove(0);
      }
   }

   public Object removeLast() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.remove(this.size - 1);
      }
   }

   public ListAndDeque reversed() {
      return new ArrayListDeque.ReversedView(this);
   }

   public @Nullable Object pollFirst() {
      return this.size == 0 ? null : this.removeFirst();
   }

   public @Nullable Object pollLast() {
      return this.size == 0 ? null : this.removeLast();
   }

   public Object getFirst() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.get(0);
      }
   }

   public Object getLast() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.get(this.size - 1);
      }
   }

   public @Nullable Object peekFirst() {
      return this.size == 0 ? null : this.getFirst();
   }

   public @Nullable Object peekLast() {
      return this.size == 0 ? null : this.getLast();
   }

   public boolean removeFirstOccurrence(final Object o) {
      for(int i = 0; i < this.size; ++i) {
         Object value = (T)this.get(i);
         if (Objects.equals(o, value)) {
            this.remove(i);
            return true;
         }
      }

      return false;
   }

   public boolean removeLastOccurrence(final Object o) {
      for(int i = this.size - 1; i >= 0; --i) {
         Object value = (T)this.get(i);
         if (Objects.equals(o, value)) {
            this.remove(i);
            return true;
         }
      }

      return false;
   }

   public Iterator descendingIterator() {
      return new ArrayListDeque.DescendingIterator();
   }

   private class DescendingIterator implements Iterator {
      private int index;

      public DescendingIterator() {
         Objects.requireNonNull(ArrayListDeque.this);
         super();
         this.index = ArrayListDeque.this.size() - 1;
      }

      public boolean hasNext() {
         return this.index >= 0;
      }

      public Object next() {
         return ArrayListDeque.this.get(this.index--);
      }

      public void remove() {
         ArrayListDeque.this.remove(this.index + 1);
      }
   }

   private class ReversedView extends AbstractList implements ListAndDeque {
      private final ArrayListDeque source;

      public ReversedView(final ArrayListDeque source) {
         Objects.requireNonNull(ArrayListDeque.this);
         super();
         this.source = source;
      }

      public ListAndDeque reversed() {
         return this.source;
      }

      public Object getFirst() {
         return this.source.getLast();
      }

      public Object getLast() {
         return this.source.getFirst();
      }

      public void addFirst(final Object t) {
         this.source.addLast(t);
      }

      public void addLast(final Object t) {
         this.source.addFirst(t);
      }

      public boolean offerFirst(final Object t) {
         return this.source.offerLast(t);
      }

      public boolean offerLast(final Object t) {
         return this.source.offerFirst(t);
      }

      public @Nullable Object pollFirst() {
         return this.source.pollLast();
      }

      public @Nullable Object pollLast() {
         return this.source.pollFirst();
      }

      public @Nullable Object peekFirst() {
         return this.source.peekLast();
      }

      public @Nullable Object peekLast() {
         return this.source.peekFirst();
      }

      public Object removeFirst() {
         return this.source.removeLast();
      }

      public Object removeLast() {
         return this.source.removeFirst();
      }

      public boolean removeFirstOccurrence(final Object o) {
         return this.source.removeLastOccurrence(o);
      }

      public boolean removeLastOccurrence(final Object o) {
         return this.source.removeFirstOccurrence(o);
      }

      public Iterator descendingIterator() {
         return this.source.iterator();
      }

      public int size() {
         return this.source.size();
      }

      public boolean isEmpty() {
         return this.source.isEmpty();
      }

      public boolean contains(final Object o) {
         return this.source.contains(o);
      }

      public Object get(final int index) {
         return this.source.get(this.reverseIndex(index));
      }

      public Object set(final int index, final Object element) {
         return this.source.set(this.reverseIndex(index), element);
      }

      public void add(final int index, final Object element) {
         this.source.add(this.reverseIndex(index) + 1, element);
      }

      public Object remove(final int index) {
         return this.source.remove(this.reverseIndex(index));
      }

      public int indexOf(final Object o) {
         return this.reverseIndex(this.source.lastIndexOf(o));
      }

      public int lastIndexOf(final Object o) {
         return this.reverseIndex(this.source.indexOf(o));
      }

      public List subList(final int fromIndex, final int toIndex) {
         return this.source.subList(this.reverseIndex(toIndex) + 1, this.reverseIndex(fromIndex) + 1).reversed();
      }

      public Iterator iterator() {
         return this.source.descendingIterator();
      }

      public void clear() {
         this.source.clear();
      }

      private int reverseIndex(final int index) {
         return index == -1 ? -1 : this.source.size() - 1 - index;
      }
   }
}
