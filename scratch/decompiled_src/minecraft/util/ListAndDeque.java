package net.minecraft.util;

import java.io.Serializable;
import java.util.Deque;
import java.util.List;
import java.util.RandomAccess;
import org.jspecify.annotations.Nullable;

public interface ListAndDeque extends List, RandomAccess, Cloneable, Serializable, Deque {
   ListAndDeque reversed();

   Object getFirst();

   Object getLast();

   void addFirst(Object t);

   void addLast(Object t);

   Object removeFirst();

   Object removeLast();

   default boolean offer(final Object value) {
      return this.offerLast(value);
   }

   default Object remove() {
      return this.removeFirst();
   }

   default @Nullable Object poll() {
      return this.pollFirst();
   }

   default Object element() {
      return this.getFirst();
   }

   default @Nullable Object peek() {
      return this.peekFirst();
   }

   default void push(final Object value) {
      this.addFirst(value);
   }

   default Object pop() {
      return this.removeFirst();
   }
}
