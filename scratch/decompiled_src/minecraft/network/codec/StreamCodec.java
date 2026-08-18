package net.minecraft.network.codec;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.Function10;
import com.mojang.datafixers.util.Function11;
import com.mojang.datafixers.util.Function12;
import com.mojang.datafixers.util.Function13;
import com.mojang.datafixers.util.Function14;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Function5;
import com.mojang.datafixers.util.Function6;
import com.mojang.datafixers.util.Function7;
import com.mojang.datafixers.util.Function8;
import com.mojang.datafixers.util.Function9;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public interface StreamCodec extends StreamEncoder, StreamDecoder {
   static StreamCodec of(final StreamEncoder encoder, final StreamDecoder decoder) {
      return new StreamCodec() {
         public Object decode(final Object input) {
            return decoder.decode(input);
         }

         public void encode(final Object output, final Object value) {
            encoder.encode(output, value);
         }
      };
   }

   static StreamCodec ofMember(final StreamMemberEncoder encoder, final StreamDecoder decoder) {
      return new StreamCodec() {
         public Object decode(final Object input) {
            return decoder.decode(input);
         }

         public void encode(final Object output, final Object value) {
            encoder.encode(value, output);
         }
      };
   }

   static StreamCodec unit(final Object instance) {
      return new StreamCodec() {
         public Object decode(final Object input) {
            return instance;
         }

         public void encode(final Object output, final Object value) {
            if (!value.equals(instance)) {
               throw new IllegalStateException("Can't encode '" + String.valueOf(value) + "', expected '" + String.valueOf(instance) + "'");
            }
         }
      };
   }

   default StreamCodec apply(final StreamCodec.CodecOperation operation) {
      return operation.apply(this);
   }

   default StreamCodec map(final Function to, final Function from) {
      return new StreamCodec() {
         {
            Objects.requireNonNull(StreamCodec.this);
         }

         public Object decode(final Object input) {
            return to.apply(StreamCodec.this.decode(input));
         }

         public void encode(final Object output, final Object value) {
            StreamCodec.this.encode(output, from.apply(value));
         }
      };
   }

   default StreamCodec mapStream(final Function operation) {
      return new StreamCodec() {
         {
            Objects.requireNonNull(StreamCodec.this);
         }

         public Object decode(final ByteBuf input) {
            Object wrappedStream = (B)operation.apply(input);
            return StreamCodec.this.decode(wrappedStream);
         }

         public void encode(final ByteBuf output, final Object value) {
            Object wrappedStream = (B)operation.apply(output);
            StreamCodec.this.encode(wrappedStream, value);
         }
      };
   }

   default StreamCodec dispatch(final Function type, final Function codec) {
      return new StreamCodec() {
         {
            Objects.requireNonNull(StreamCodec.this);
         }

         public Object decode(final Object input) {
            Object key = (V)StreamCodec.this.decode(input);
            StreamCodec valueCodec = (StreamCodec)codec.apply(key);
            return valueCodec.decode(input);
         }

         public void encode(final Object output, final Object value) {
            Object key = (V)type.apply(value);
            StreamCodec valueCodec = (StreamCodec)codec.apply(key);
            StreamCodec.this.encode(output, key);
            valueCodec.encode(output, value);
         }
      };
   }

   static StreamCodec composite(final StreamCodec codec1, final Function getter1, final Function constructor) {
      return new StreamCodec() {
         public Object decode(final Object input) {
            Object v1 = (T1)codec1.decode(input);
            return constructor.apply(v1);
         }

         public void encode(final Object output, final Object value) {
            codec1.encode(output, getter1.apply(value));
         }
      };
   }

   static StreamCodec composite(final StreamCodec codec1, final Function getter1, final StreamCodec codec2, final Function getter2, final BiFunction constructor) {
      return new StreamCodec() {
         public Object decode(final Object input) {
            Object v1 = (T1)codec1.decode(input);
            Object v2 = (T2)codec2.decode(input);
            return constructor.apply(v1, v2);
         }

         public void encode(final Object output, final Object value) {
            codec1.encode(output, getter1.apply(value));
            codec2.encode(output, getter2.apply(value));
         }
      };
   }

   static StreamCodec composite(final StreamCodec codec1, final Function getter1, final StreamCodec codec2, final Function getter2, final StreamCodec codec3, final Function getter3, final Function3 constructor) {
      return new StreamCodec() {
         public Object decode(final Object input) {
            Object v1 = (T1)codec1.decode(input);
            Object v2 = (T2)codec2.decode(input);
            Object v3 = (T3)codec3.decode(input);
            return constructor.apply(v1, v2, v3);
         }

         public void encode(final Object output, final Object value) {
            codec1.encode(output, getter1.apply(value));
            codec2.encode(output, getter2.apply(value));
            codec3.encode(output, getter3.apply(value));
         }
      };
   }

   static StreamCodec composite(final StreamCodec codec1, final Function getter1, final StreamCodec codec2, final Function getter2, final StreamCodec codec3, final Function getter3, final StreamCodec codec4, final Function getter4, final Function4 constructor) {
      return new StreamCodec() {
         public Object decode(final Object input) {
            Object v1 = (T1)codec1.decode(input);
            Object v2 = (T2)codec2.decode(input);
            Object v3 = (T3)codec3.decode(input);
            Object v4 = (T4)codec4.decode(input);
            return constructor.apply(v1, v2, v3, v4);
         }

         public void encode(final Object output, final Object value) {
            codec1.encode(output, getter1.apply(value));
            codec2.encode(output, getter2.apply(value));
            codec3.encode(output, getter3.apply(value));
            codec4.encode(output, getter4.apply(value));
         }
      };
   }

   static StreamCodec composite(final StreamCodec codec1, final Function getter1, final StreamCodec codec2, final Function getter2, final StreamCodec codec3, final Function getter3, final StreamCodec codec4, final Function getter4, final StreamCodec codec5, final Function getter5, final Function5 constructor) {
      return new StreamCodec() {
         public Object decode(final Object input) {
            Object v1 = (T1)codec1.decode(input);
            Object v2 = (T2)codec2.decode(input);
            Object v3 = (T3)codec3.decode(input);
            Object v4 = (T4)codec4.decode(input);
            Object v5 = (T5)codec5.decode(input);
            return constructor.apply(v1, v2, v3, v4, v5);
         }

         public void encode(final Object output, final Object value) {
            codec1.encode(output, getter1.apply(value));
            codec2.encode(output, getter2.apply(value));
            codec3.encode(output, getter3.apply(value));
            codec4.encode(output, getter4.apply(value));
            codec5.encode(output, getter5.apply(value));
         }
      };
   }

   static StreamCodec composite(final StreamCodec codec1, final Function getter1, final StreamCodec codec2, final Function getter2, final StreamCodec codec3, final Function getter3, final StreamCodec codec4, final Function getter4, final StreamCodec codec5, final Function getter5, final StreamCodec codec6, final Function getter6, final Function6 constructor) {
      return new StreamCodec() {
         public Object decode(final Object input) {
            Object v1 = (T1)codec1.decode(input);
            Object v2 = (T2)codec2.decode(input);
            Object v3 = (T3)codec3.decode(input);
            Object v4 = (T4)codec4.decode(input);
            Object v5 = (T5)codec5.decode(input);
            Object v6 = (T6)codec6.decode(input);
            return constructor.apply(v1, v2, v3, v4, v5, v6);
         }

         public void encode(final Object output, final Object value) {
            codec1.encode(output, getter1.apply(value));
            codec2.encode(output, getter2.apply(value));
            codec3.encode(output, getter3.apply(value));
            codec4.encode(output, getter4.apply(value));
            codec5.encode(output, getter5.apply(value));
            codec6.encode(output, getter6.apply(value));
         }
      };
   }

   static StreamCodec composite(final StreamCodec codec1, final Function getter1, final StreamCodec codec2, final Function getter2, final StreamCodec codec3, final Function getter3, final StreamCodec codec4, final Function getter4, final StreamCodec codec5, final Function getter5, final StreamCodec codec6, final Function getter6, final StreamCodec codec7, final Function getter7, final Function7 constructor) {
      return new StreamCodec() {
         public Object decode(final Object input) {
            Object v1 = (T1)codec1.decode(input);
            Object v2 = (T2)codec2.decode(input);
            Object v3 = (T3)codec3.decode(input);
            Object v4 = (T4)codec4.decode(input);
            Object v5 = (T5)codec5.decode(input);
            Object v6 = (T6)codec6.decode(input);
            Object v7 = (T7)codec7.decode(input);
            return constructor.apply(v1, v2, v3, v4, v5, v6, v7);
         }

         public void encode(final Object output, final Object value) {
            codec1.encode(output, getter1.apply(value));
            codec2.encode(output, getter2.apply(value));
            codec3.encode(output, getter3.apply(value));
            codec4.encode(output, getter4.apply(value));
            codec5.encode(output, getter5.apply(value));
            codec6.encode(output, getter6.apply(value));
            codec7.encode(output, getter7.apply(value));
         }
      };
   }

   static StreamCodec composite(final StreamCodec codec1, final Function getter1, final StreamCodec codec2, final Function getter2, final StreamCodec codec3, final Function getter3, final StreamCodec codec4, final Function getter4, final StreamCodec codec5, final Function getter5, final StreamCodec codec6, final Function getter6, final StreamCodec codec7, final Function getter7, final StreamCodec codec8, final Function getter8, final Function8 constructor) {
      return new StreamCodec() {
         public Object decode(final Object input) {
            Object v1 = (T1)codec1.decode(input);
            Object v2 = (T2)codec2.decode(input);
            Object v3 = (T3)codec3.decode(input);
            Object v4 = (T4)codec4.decode(input);
            Object v5 = (T5)codec5.decode(input);
            Object v6 = (T6)codec6.decode(input);
            Object v7 = (T7)codec7.decode(input);
            Object v8 = (T8)codec8.decode(input);
            return constructor.apply(v1, v2, v3, v4, v5, v6, v7, v8);
         }

         public void encode(final Object output, final Object value) {
            codec1.encode(output, getter1.apply(value));
            codec2.encode(output, getter2.apply(value));
            codec3.encode(output, getter3.apply(value));
            codec4.encode(output, getter4.apply(value));
            codec5.encode(output, getter5.apply(value));
            codec6.encode(output, getter6.apply(value));
            codec7.encode(output, getter7.apply(value));
            codec8.encode(output, getter8.apply(value));
         }
      };
   }

   static StreamCodec composite(final StreamCodec codec1, final Function getter1, final StreamCodec codec2, final Function getter2, final StreamCodec codec3, final Function getter3, final StreamCodec codec4, final Function getter4, final StreamCodec codec5, final Function getter5, final StreamCodec codec6, final Function getter6, final StreamCodec codec7, final Function getter7, final StreamCodec codec8, final Function getter8, final StreamCodec codec9, final Function getter9, final Function9 constructor) {
      return new StreamCodec() {
         public Object decode(final Object input) {
            Object v1 = (T1)codec1.decode(input);
            Object v2 = (T2)codec2.decode(input);
            Object v3 = (T3)codec3.decode(input);
            Object v4 = (T4)codec4.decode(input);
            Object v5 = (T5)codec5.decode(input);
            Object v6 = (T6)codec6.decode(input);
            Object v7 = (T7)codec7.decode(input);
            Object v8 = (T8)codec8.decode(input);
            Object v9 = (T9)codec9.decode(input);
            return constructor.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9);
         }

         public void encode(final Object output, final Object value) {
            codec1.encode(output, getter1.apply(value));
            codec2.encode(output, getter2.apply(value));
            codec3.encode(output, getter3.apply(value));
            codec4.encode(output, getter4.apply(value));
            codec5.encode(output, getter5.apply(value));
            codec6.encode(output, getter6.apply(value));
            codec7.encode(output, getter7.apply(value));
            codec8.encode(output, getter8.apply(value));
            codec9.encode(output, getter9.apply(value));
         }
      };
   }

   static StreamCodec composite(final StreamCodec codec1, final Function getter1, final StreamCodec codec2, final Function getter2, final StreamCodec codec3, final Function getter3, final StreamCodec codec4, final Function getter4, final StreamCodec codec5, final Function getter5, final StreamCodec codec6, final Function getter6, final StreamCodec codec7, final Function getter7, final StreamCodec codec8, final Function getter8, final StreamCodec codec9, final Function getter9, final StreamCodec codec10, final Function getter10, final Function10 constructor) {
      return new StreamCodec() {
         public Object decode(final Object input) {
            Object v1 = (T1)codec1.decode(input);
            Object v2 = (T2)codec2.decode(input);
            Object v3 = (T3)codec3.decode(input);
            Object v4 = (T4)codec4.decode(input);
            Object v5 = (T5)codec5.decode(input);
            Object v6 = (T6)codec6.decode(input);
            Object v7 = (T7)codec7.decode(input);
            Object v8 = (T8)codec8.decode(input);
            Object v9 = (T9)codec9.decode(input);
            Object v10 = (T10)codec10.decode(input);
            return constructor.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10);
         }

         public void encode(final Object output, final Object value) {
            codec1.encode(output, getter1.apply(value));
            codec2.encode(output, getter2.apply(value));
            codec3.encode(output, getter3.apply(value));
            codec4.encode(output, getter4.apply(value));
            codec5.encode(output, getter5.apply(value));
            codec6.encode(output, getter6.apply(value));
            codec7.encode(output, getter7.apply(value));
            codec8.encode(output, getter8.apply(value));
            codec9.encode(output, getter9.apply(value));
            codec10.encode(output, getter10.apply(value));
         }
      };
   }

   static StreamCodec composite(final StreamCodec codec1, final Function getter1, final StreamCodec codec2, final Function getter2, final StreamCodec codec3, final Function getter3, final StreamCodec codec4, final Function getter4, final StreamCodec codec5, final Function getter5, final StreamCodec codec6, final Function getter6, final StreamCodec codec7, final Function getter7, final StreamCodec codec8, final Function getter8, final StreamCodec codec9, final Function getter9, final StreamCodec codec10, final Function getter10, final StreamCodec codec11, final Function getter11, final Function11 constructor) {
      return new StreamCodec() {
         public Object decode(final Object input) {
            Object v1 = (T1)codec1.decode(input);
            Object v2 = (T2)codec2.decode(input);
            Object v3 = (T3)codec3.decode(input);
            Object v4 = (T4)codec4.decode(input);
            Object v5 = (T5)codec5.decode(input);
            Object v6 = (T6)codec6.decode(input);
            Object v7 = (T7)codec7.decode(input);
            Object v8 = (T8)codec8.decode(input);
            Object v9 = (T9)codec9.decode(input);
            Object v10 = (T10)codec10.decode(input);
            Object v11 = (T11)codec11.decode(input);
            return constructor.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11);
         }

         public void encode(final Object output, final Object value) {
            codec1.encode(output, getter1.apply(value));
            codec2.encode(output, getter2.apply(value));
            codec3.encode(output, getter3.apply(value));
            codec4.encode(output, getter4.apply(value));
            codec5.encode(output, getter5.apply(value));
            codec6.encode(output, getter6.apply(value));
            codec7.encode(output, getter7.apply(value));
            codec8.encode(output, getter8.apply(value));
            codec9.encode(output, getter9.apply(value));
            codec10.encode(output, getter10.apply(value));
            codec11.encode(output, getter11.apply(value));
         }
      };
   }

   static StreamCodec composite(final StreamCodec codec1, final Function getter1, final StreamCodec codec2, final Function getter2, final StreamCodec codec3, final Function getter3, final StreamCodec codec4, final Function getter4, final StreamCodec codec5, final Function getter5, final StreamCodec codec6, final Function getter6, final StreamCodec codec7, final Function getter7, final StreamCodec codec8, final Function getter8, final StreamCodec codec9, final Function getter9, final StreamCodec codec10, final Function getter10, final StreamCodec codec11, final Function getter11, final StreamCodec codec12, final Function getter12, final Function12 constructor) {
      return new StreamCodec() {
         public Object decode(final Object input) {
            Object v1 = (T1)codec1.decode(input);
            Object v2 = (T2)codec2.decode(input);
            Object v3 = (T3)codec3.decode(input);
            Object v4 = (T4)codec4.decode(input);
            Object v5 = (T5)codec5.decode(input);
            Object v6 = (T6)codec6.decode(input);
            Object v7 = (T7)codec7.decode(input);
            Object v8 = (T8)codec8.decode(input);
            Object v9 = (T9)codec9.decode(input);
            Object v10 = (T10)codec10.decode(input);
            Object v11 = (T11)codec11.decode(input);
            Object v12 = (T12)codec12.decode(input);
            return constructor.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12);
         }

         public void encode(final Object output, final Object value) {
            codec1.encode(output, getter1.apply(value));
            codec2.encode(output, getter2.apply(value));
            codec3.encode(output, getter3.apply(value));
            codec4.encode(output, getter4.apply(value));
            codec5.encode(output, getter5.apply(value));
            codec6.encode(output, getter6.apply(value));
            codec7.encode(output, getter7.apply(value));
            codec8.encode(output, getter8.apply(value));
            codec9.encode(output, getter9.apply(value));
            codec10.encode(output, getter10.apply(value));
            codec11.encode(output, getter11.apply(value));
            codec12.encode(output, getter12.apply(value));
         }
      };
   }

   static StreamCodec composite(final StreamCodec codec1, final Function getter1, final StreamCodec codec2, final Function getter2, final StreamCodec codec3, final Function getter3, final StreamCodec codec4, final Function getter4, final StreamCodec codec5, final Function getter5, final StreamCodec codec6, final Function getter6, final StreamCodec codec7, final Function getter7, final StreamCodec codec8, final Function getter8, final StreamCodec codec9, final Function getter9, final StreamCodec codec10, final Function getter10, final StreamCodec codec11, final Function getter11, final StreamCodec codec12, final Function getter12, final StreamCodec codec13, final Function getter13, final Function13 constructor) {
      return new StreamCodec() {
         public Object decode(final Object input) {
            Object v1 = (T1)codec1.decode(input);
            Object v2 = (T2)codec2.decode(input);
            Object v3 = (T3)codec3.decode(input);
            Object v4 = (T4)codec4.decode(input);
            Object v5 = (T5)codec5.decode(input);
            Object v6 = (T6)codec6.decode(input);
            Object v7 = (T7)codec7.decode(input);
            Object v8 = (T8)codec8.decode(input);
            Object v9 = (T9)codec9.decode(input);
            Object v10 = (T10)codec10.decode(input);
            Object v11 = (T11)codec11.decode(input);
            Object v12 = (T12)codec12.decode(input);
            Object v13 = (T13)codec13.decode(input);
            return constructor.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13);
         }

         public void encode(final Object output, final Object value) {
            codec1.encode(output, getter1.apply(value));
            codec2.encode(output, getter2.apply(value));
            codec3.encode(output, getter3.apply(value));
            codec4.encode(output, getter4.apply(value));
            codec5.encode(output, getter5.apply(value));
            codec6.encode(output, getter6.apply(value));
            codec7.encode(output, getter7.apply(value));
            codec8.encode(output, getter8.apply(value));
            codec9.encode(output, getter9.apply(value));
            codec10.encode(output, getter10.apply(value));
            codec11.encode(output, getter11.apply(value));
            codec12.encode(output, getter12.apply(value));
            codec13.encode(output, getter13.apply(value));
         }
      };
   }

   static StreamCodec composite(final StreamCodec codec1, final Function getter1, final StreamCodec codec2, final Function getter2, final StreamCodec codec3, final Function getter3, final StreamCodec codec4, final Function getter4, final StreamCodec codec5, final Function getter5, final StreamCodec codec6, final Function getter6, final StreamCodec codec7, final Function getter7, final StreamCodec codec8, final Function getter8, final StreamCodec codec9, final Function getter9, final StreamCodec codec10, final Function getter10, final StreamCodec codec11, final Function getter11, final StreamCodec codec12, final Function getter12, final StreamCodec codec13, final Function getter13, final StreamCodec codec14, final Function getter14, final Function14 constructor) {
      return new StreamCodec() {
         public Object decode(final Object input) {
            Object v1 = (T1)codec1.decode(input);
            Object v2 = (T2)codec2.decode(input);
            Object v3 = (T3)codec3.decode(input);
            Object v4 = (T4)codec4.decode(input);
            Object v5 = (T5)codec5.decode(input);
            Object v6 = (T6)codec6.decode(input);
            Object v7 = (T7)codec7.decode(input);
            Object v8 = (T8)codec8.decode(input);
            Object v9 = (T9)codec9.decode(input);
            Object v10 = (T10)codec10.decode(input);
            Object v11 = (T11)codec11.decode(input);
            Object v12 = (T12)codec12.decode(input);
            Object v13 = (T13)codec13.decode(input);
            Object v14 = (T14)codec14.decode(input);
            return constructor.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14);
         }

         public void encode(final Object output, final Object value) {
            codec1.encode(output, getter1.apply(value));
            codec2.encode(output, getter2.apply(value));
            codec3.encode(output, getter3.apply(value));
            codec4.encode(output, getter4.apply(value));
            codec5.encode(output, getter5.apply(value));
            codec6.encode(output, getter6.apply(value));
            codec7.encode(output, getter7.apply(value));
            codec8.encode(output, getter8.apply(value));
            codec9.encode(output, getter9.apply(value));
            codec10.encode(output, getter10.apply(value));
            codec11.encode(output, getter11.apply(value));
            codec12.encode(output, getter12.apply(value));
            codec13.encode(output, getter13.apply(value));
            codec14.encode(output, getter14.apply(value));
         }
      };
   }

   static StreamCodec recursive(final UnaryOperator factory) {
      return new StreamCodec() {
         private final Supplier inner = Suppliers.memoize(() -> (StreamCodec)factory.apply(this));

         public Object decode(final Object input) {
            return ((StreamCodec)this.inner.get()).decode(input);
         }

         public void encode(final Object output, final Object value) {
            ((StreamCodec)this.inner.get()).encode(output, value);
         }
      };
   }

   default StreamCodec cast() {
      return this;
   }

   @FunctionalInterface
   public interface CodecOperation {
      StreamCodec apply(StreamCodec original);
   }
}
