package net.minecraft.client.renderer;

import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import org.joml.Matrix4fc;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.joml.Vector3ic;
import org.joml.Vector4fc;

public interface UniformValue {
   Codec CODEC = UniformValue.Type.CODEC.dispatch(UniformValue::type, (t) -> t.valueCodec);

   void writeTo(Std140Builder builder);

   void addSize(Std140SizeCalculator calculator);

   UniformValue.Type type();

   public static record FloatUniform(float value) implements UniformValue {
      public static final Codec CODEC = Codec.FLOAT.xmap(UniformValue.FloatUniform::new, UniformValue.FloatUniform::value);

      public void writeTo(final Std140Builder builder) {
         builder.putFloat(this.value);
      }

      public void addSize(final Std140SizeCalculator calculator) {
         calculator.putFloat();
      }

      public UniformValue.Type type() {
         return UniformValue.Type.FLOAT;
      }
   }

   public static record IVec3Uniform(Vector3ic value) implements UniformValue {
      public static final Codec CODEC = ExtraCodecs.VECTOR3I.xmap(UniformValue.IVec3Uniform::new, UniformValue.IVec3Uniform::value);

      public void writeTo(final Std140Builder builder) {
         builder.putIVec3(this.value);
      }

      public void addSize(final Std140SizeCalculator calculator) {
         calculator.putIVec3();
      }

      public UniformValue.Type type() {
         return UniformValue.Type.IVEC3;
      }
   }

   public static record IntUniform(int value) implements UniformValue {
      public static final Codec CODEC = Codec.INT.xmap(UniformValue.IntUniform::new, UniformValue.IntUniform::value);

      public void writeTo(final Std140Builder builder) {
         builder.putInt(this.value);
      }

      public void addSize(final Std140SizeCalculator calculator) {
         calculator.putInt();
      }

      public UniformValue.Type type() {
         return UniformValue.Type.INT;
      }
   }

   public static record Matrix4x4Uniform(Matrix4fc value) implements UniformValue {
      public static final Codec CODEC = ExtraCodecs.MATRIX4F.xmap(UniformValue.Matrix4x4Uniform::new, UniformValue.Matrix4x4Uniform::value);

      public void writeTo(final Std140Builder builder) {
         builder.putMat4f(this.value);
      }

      public void addSize(final Std140SizeCalculator calculator) {
         calculator.putMat4f();
      }

      public UniformValue.Type type() {
         return UniformValue.Type.MATRIX4X4;
      }
   }

   public static enum Type implements StringRepresentable {
      INT("int", UniformValue.IntUniform.CODEC),
      IVEC3("ivec3", UniformValue.IVec3Uniform.CODEC),
      FLOAT("float", UniformValue.FloatUniform.CODEC),
      VEC2("vec2", UniformValue.Vec2Uniform.CODEC),
      VEC3("vec3", UniformValue.Vec3Uniform.CODEC),
      VEC4("vec4", UniformValue.Vec4Uniform.CODEC),
      MATRIX4X4("matrix4x4", UniformValue.Matrix4x4Uniform.CODEC);

      public static final Codec CODEC = StringRepresentable.fromEnum(UniformValue.Type::values);
      private final String name;
      private final MapCodec valueCodec;

      private Type(final String name, final Codec valueCodec) {
         this.name = name;
         this.valueCodec = valueCodec.fieldOf("value");
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static UniformValue.Type[] $values() {
         return new UniformValue.Type[]{INT, IVEC3, FLOAT, VEC2, VEC3, VEC4, MATRIX4X4};
      }
   }

   public static record Vec2Uniform(Vector2fc value) implements UniformValue {
      public static final Codec CODEC = ExtraCodecs.VECTOR2F.xmap(UniformValue.Vec2Uniform::new, UniformValue.Vec2Uniform::value);

      public void writeTo(final Std140Builder builder) {
         builder.putVec2(this.value);
      }

      public void addSize(final Std140SizeCalculator calculator) {
         calculator.putVec2();
      }

      public UniformValue.Type type() {
         return UniformValue.Type.VEC2;
      }
   }

   public static record Vec3Uniform(Vector3fc value) implements UniformValue {
      public static final Codec CODEC = ExtraCodecs.VECTOR3F.xmap(UniformValue.Vec3Uniform::new, UniformValue.Vec3Uniform::value);

      public void writeTo(final Std140Builder builder) {
         builder.putVec3(this.value);
      }

      public void addSize(final Std140SizeCalculator calculator) {
         calculator.putVec3();
      }

      public UniformValue.Type type() {
         return UniformValue.Type.VEC3;
      }
   }

   public static record Vec4Uniform(Vector4fc value) implements UniformValue {
      public static final Codec CODEC = ExtraCodecs.VECTOR4F.xmap(UniformValue.Vec4Uniform::new, UniformValue.Vec4Uniform::value);

      public void writeTo(final Std140Builder builder) {
         builder.putVec4(this.value);
      }

      public void addSize(final Std140SizeCalculator calculator) {
         calculator.putVec4();
      }

      public UniformValue.Type type() {
         return UniformValue.Type.VEC4;
      }
   }
}
