package net.minecraft.world.level.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;

public class ValueInputContextHelper {
   private final HolderLookup.Provider lookup;
   private final DynamicOps ops;
   private final ValueInput.ValueInputList emptyChildList = new ValueInput.ValueInputList(this) {
      {
         Objects.requireNonNull(this$0);
      }

      public boolean isEmpty() {
         return true;
      }

      public Stream stream() {
         return Stream.empty();
      }

      public Iterator iterator() {
         return Collections.emptyIterator();
      }
   };
   private final ValueInput.TypedInputList emptyTypedList = new ValueInput.TypedInputList(this) {
      {
         Objects.requireNonNull(this$0);
      }

      public boolean isEmpty() {
         return true;
      }

      public Stream stream() {
         return Stream.empty();
      }

      public Iterator iterator() {
         return Collections.emptyIterator();
      }
   };
   private final ValueInput empty = new ValueInput() {
      {
         Objects.requireNonNull(ValueInputContextHelper.this);
      }

      public Optional read(final String name, final Codec codec) {
         return Optional.empty();
      }

      public Optional read(final MapCodec codec) {
         return Optional.empty();
      }

      public Optional child(final String name) {
         return Optional.empty();
      }

      public ValueInput childOrEmpty(final String name) {
         return this;
      }

      public Optional childrenList(final String name) {
         return Optional.empty();
      }

      public ValueInput.ValueInputList childrenListOrEmpty(final String name) {
         return ValueInputContextHelper.this.emptyChildList;
      }

      public Optional list(final String name, final Codec codec) {
         return Optional.empty();
      }

      public ValueInput.TypedInputList listOrEmpty(final String name, final Codec codec) {
         return ValueInputContextHelper.this.emptyTypedList();
      }

      public boolean getBooleanOr(final String name, final boolean defaultValue) {
         return defaultValue;
      }

      public byte getByteOr(final String name, final byte defaultValue) {
         return defaultValue;
      }

      public int getShortOr(final String name, final short defaultValue) {
         return defaultValue;
      }

      public Optional getInt(final String name) {
         return Optional.empty();
      }

      public int getIntOr(final String name, final int defaultValue) {
         return defaultValue;
      }

      public long getLongOr(final String name, final long defaultValue) {
         return defaultValue;
      }

      public Optional getLong(final String name) {
         return Optional.empty();
      }

      public float getFloatOr(final String name, final float defaultValue) {
         return defaultValue;
      }

      public double getDoubleOr(final String name, final double defaultValue) {
         return defaultValue;
      }

      public Optional getString(final String name) {
         return Optional.empty();
      }

      public String getStringOr(final String name, final String defaultValue) {
         return defaultValue;
      }

      public HolderLookup.Provider lookup() {
         return ValueInputContextHelper.this.lookup;
      }

      public Optional getIntArray(final String name) {
         return Optional.empty();
      }
   };

   public ValueInputContextHelper(final HolderLookup.Provider lookup, final DynamicOps ops) {
      this.lookup = lookup;
      this.ops = lookup.createSerializationContext(ops);
   }

   public DynamicOps ops() {
      return this.ops;
   }

   public HolderLookup.Provider lookup() {
      return this.lookup;
   }

   public ValueInput empty() {
      return this.empty;
   }

   public ValueInput.ValueInputList emptyList() {
      return this.emptyChildList;
   }

   public ValueInput.TypedInputList emptyTypedList() {
      return this.emptyTypedList;
   }
}
