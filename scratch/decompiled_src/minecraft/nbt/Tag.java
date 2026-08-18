package net.minecraft.nbt;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;

public sealed interface Tag permits CompoundTag, CollectionTag, PrimitiveTag, EndTag {
   int OBJECT_HEADER = 8;
   int ARRAY_HEADER = 12;
   int OBJECT_REFERENCE = 4;
   int STRING_SIZE = 28;
   byte TAG_END = 0;
   byte TAG_BYTE = 1;
   byte TAG_SHORT = 2;
   byte TAG_INT = 3;
   byte TAG_LONG = 4;
   byte TAG_FLOAT = 5;
   byte TAG_DOUBLE = 6;
   byte TAG_BYTE_ARRAY = 7;
   byte TAG_STRING = 8;
   byte TAG_LIST = 9;
   byte TAG_COMPOUND = 10;
   byte TAG_INT_ARRAY = 11;
   byte TAG_LONG_ARRAY = 12;
   int MAX_DEPTH = 512;

   void write(DataOutput output) throws IOException;

   String toString();

   byte getId();

   TagType getType();

   Tag copy();

   int sizeInBytes();

   void accept(TagVisitor visitor);

   StreamTagVisitor.ValueResult accept(StreamTagVisitor visitor);

   default void acceptAsRoot(final StreamTagVisitor output) {
      StreamTagVisitor.ValueResult entryResult = output.visitRootEntry(this.getType());
      if (entryResult == StreamTagVisitor.ValueResult.CONTINUE) {
         this.accept(output);
      }

   }

   default Optional asString() {
      return Optional.empty();
   }

   default Optional asNumber() {
      return Optional.empty();
   }

   default Optional asByte() {
      return this.asNumber().map(Number::byteValue);
   }

   default Optional asShort() {
      return this.asNumber().map(Number::shortValue);
   }

   default Optional asInt() {
      return this.asNumber().map(Number::intValue);
   }

   default Optional asLong() {
      return this.asNumber().map(Number::longValue);
   }

   default Optional asFloat() {
      return this.asNumber().map(Number::floatValue);
   }

   default Optional asDouble() {
      return this.asNumber().map(Number::doubleValue);
   }

   default Optional asBoolean() {
      return this.asByte().map((b) -> b != 0);
   }

   default Optional asByteArray() {
      return Optional.empty();
   }

   default Optional asIntArray() {
      return Optional.empty();
   }

   default Optional asLongArray() {
      return Optional.empty();
   }

   default Optional asCompound() {
      return Optional.empty();
   }

   default Optional asList() {
      return Optional.empty();
   }
}
