package net.minecraft.world.level.levelgen;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.world.level.dimension.DimensionType;

public interface VerticalAnchor {
   Codec CODEC = Codec.xor(VerticalAnchor.Absolute.CODEC, Codec.xor(VerticalAnchor.AboveBottom.CODEC, Codec.xor(VerticalAnchor.BelowTop.CODEC, VerticalAnchor.RelativeToSeaLevel.CODEC))).xmap(VerticalAnchor::merge, VerticalAnchor::split);
   VerticalAnchor BOTTOM = aboveBottom(0);
   VerticalAnchor TOP = belowTop(0);

   static VerticalAnchor absolute(final int value) {
      return new VerticalAnchor.Absolute(value);
   }

   static VerticalAnchor aboveBottom(final int offset) {
      return new VerticalAnchor.AboveBottom(offset);
   }

   static VerticalAnchor belowTop(final int offset) {
      return new VerticalAnchor.BelowTop(offset);
   }

   static VerticalAnchor bottom() {
      return BOTTOM;
   }

   static VerticalAnchor top() {
      return TOP;
   }

   static VerticalAnchor relativeToSeaLevel(final int offset) {
      return new VerticalAnchor.RelativeToSeaLevel(offset);
   }

   static VerticalAnchor seaLevel() {
      return relativeToSeaLevel(0);
   }

   private static VerticalAnchor merge(final Either either) {
      return (VerticalAnchor)either.map(Function.identity(), (e) -> (Record)e.map(Function.identity(), Either::unwrap));
   }

   private static Either split(final VerticalAnchor anchor) {
      if (anchor instanceof VerticalAnchor.Absolute absolute) {
         return Either.left(absolute);
      } else if (anchor instanceof VerticalAnchor.AboveBottom aboveBottom) {
         return Either.right(Either.left(aboveBottom));
      } else if (anchor instanceof VerticalAnchor.BelowTop belowTop) {
         return Either.right(Either.right(Either.left(belowTop)));
      } else {
         return Either.right(Either.right(Either.right((VerticalAnchor.RelativeToSeaLevel)anchor)));
      }
   }

   int resolveY(final WorldGenerationContext heightAccessor);

   public static record AboveBottom(int offset) implements VerticalAnchor {
      public static final Codec CODEC = Codec.intRange(DimensionType.MIN_Y, DimensionType.MAX_Y).fieldOf("above_bottom").xmap(VerticalAnchor.AboveBottom::new, VerticalAnchor.AboveBottom::offset).codec();

      public int resolveY(final WorldGenerationContext heightAccessor) {
         return heightAccessor.getMinGenY() + this.offset;
      }

      public String toString() {
         return this.offset + " above bottom";
      }
   }

   public static record Absolute(int y) implements VerticalAnchor {
      public static final Codec CODEC = Codec.intRange(DimensionType.MIN_Y, DimensionType.MAX_Y).fieldOf("absolute").xmap(VerticalAnchor.Absolute::new, VerticalAnchor.Absolute::y).codec();

      public int resolveY(final WorldGenerationContext heightAccessor) {
         return this.y;
      }

      public String toString() {
         return this.y + " absolute";
      }
   }

   public static record BelowTop(int offset) implements VerticalAnchor {
      public static final Codec CODEC = Codec.intRange(DimensionType.MIN_Y, DimensionType.MAX_Y).fieldOf("below_top").xmap(VerticalAnchor.BelowTop::new, VerticalAnchor.BelowTop::offset).codec();

      public int resolveY(final WorldGenerationContext heightAccessor) {
         return heightAccessor.getGenDepth() - 1 + heightAccessor.getMinGenY() - this.offset;
      }

      public String toString() {
         return this.offset + " below top";
      }
   }

   public static record RelativeToSeaLevel(int offset) implements VerticalAnchor {
      public static final Codec CODEC = Codec.intRange(DimensionType.MIN_Y, DimensionType.MAX_Y).fieldOf("relative_to_sea_level").xmap(VerticalAnchor.RelativeToSeaLevel::new, VerticalAnchor.RelativeToSeaLevel::offset).codec();

      public int resolveY(final WorldGenerationContext heightAccessor) {
         return heightAccessor.seaLevel() + this.offset;
      }

      public String toString() {
         return this.offset + " relative to sea level";
      }
   }
}
