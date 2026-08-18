package net.minecraft;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;

public class Optionull {
   /** @deprecated */
   @Deprecated
   public static Object orElse(final @Nullable Object t, final Object defaultValue) {
      return Objects.requireNonNullElse(t, defaultValue);
   }

   public static @Nullable Object map(final @Nullable Object t, final Function map) {
      return t == null ? null : map.apply(t);
   }

   public static Object mapOrDefault(final @Nullable Object t, final Function map, final Object defaultValue) {
      return t == null ? defaultValue : map.apply(t);
   }

   public static Object mapOrElse(final @Nullable Object t, final Function map, final Supplier elseSupplier) {
      return t == null ? elseSupplier.get() : map.apply(t);
   }

   public static @Nullable Object first(final Collection collection) {
      Iterator iterator = collection.iterator();
      return iterator.hasNext() ? iterator.next() : null;
   }

   public static Object firstOrDefault(final Collection collection, final Object defaultValue) {
      Iterator iterator = collection.iterator();
      return iterator.hasNext() ? iterator.next() : defaultValue;
   }

   public static Object firstOrElse(final Collection collection, final Supplier elseSupplier) {
      Iterator iterator = collection.iterator();
      return iterator.hasNext() ? iterator.next() : elseSupplier.get();
   }

   public static boolean isNullOrEmpty(final Object @Nullable [] t) {
      return t == null || t.length == 0;
   }

   public static boolean isNullOrEmpty(final boolean @Nullable [] t) {
      return t == null || t.length == 0;
   }

   public static boolean isNullOrEmpty(final byte @Nullable [] t) {
      return t == null || t.length == 0;
   }

   public static boolean isNullOrEmpty(final char @Nullable [] t) {
      return t == null || t.length == 0;
   }

   public static boolean isNullOrEmpty(final short @Nullable [] t) {
      return t == null || t.length == 0;
   }

   public static boolean isNullOrEmpty(final int @Nullable [] t) {
      return t == null || t.length == 0;
   }

   public static boolean isNullOrEmpty(final long @Nullable [] t) {
      return t == null || t.length == 0;
   }

   public static boolean isNullOrEmpty(final float @Nullable [] t) {
      return t == null || t.length == 0;
   }

   public static boolean isNullOrEmpty(final double @Nullable [] t) {
      return t == null || t.length == 0;
   }
}
