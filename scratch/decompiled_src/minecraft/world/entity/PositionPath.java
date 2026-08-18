package net.minecraft.world.entity;

import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.function.IntFunction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.phys.Vec3;

public sealed interface PositionPath {
   StreamCodec STREAM_CODEC = PositionPath.Type.STREAM_CODEC.dispatch(PositionPath::type, PositionPath.Type::streamCodec);

   Vec3 endPosition();

   PositionPath.Type type();

   static PositionPath of(final Vec3 position) {
      return new PositionPath.Linear(position);
   }

   static PositionPath stepped(final List steps) {
      return new PositionPath.Stepped(steps);
   }

   public static record Linear(Vec3 endPosition) implements PositionPath {
      public static final StreamCodec STREAM_CODEC = Vec3.STREAM_CODEC.map(PositionPath.Linear::new, PositionPath.Linear::endPosition);

      public PositionPath.Type type() {
         return PositionPath.Type.LINEAR;
      }
   }

   public static record Stepped(Vec3 endPosition, List steps) implements PositionPath {
      public static final StreamCodec STREAM_CODEC = PositionStep.STREAM_CODEC.apply(ByteBufCodecs.list()).map(PositionPath.Stepped::new, PositionPath.Stepped::steps);

      public Stepped(final List steps) {
         this(((PositionStep)steps.getLast()).position(), steps);
      }

      public PositionPath.Type type() {
         return PositionPath.Type.STEPPED;
      }
   }

   public static enum Type {
      LINEAR(PositionPath.Linear.STREAM_CODEC),
      STEPPED(PositionPath.Stepped.STREAM_CODEC);

      public static final IntFunction BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      public static final StreamCodec STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Enum::ordinal);
      private final StreamCodec streamCodec;

      private Type(final StreamCodec streamCodec) {
         this.streamCodec = streamCodec;
      }

      public StreamCodec streamCodec() {
         return this.streamCodec;
      }

      // $FF: synthetic method
      private static PositionPath.Type[] $values() {
         return new PositionPath.Type[]{LINEAR, STEPPED};
      }
   }
}
