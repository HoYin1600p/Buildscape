package net.minecraft.world.level.block;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;

public interface HangingSignBlock {
   HangingSignBlock.Attachment attachmentPoint(BlockState state);

   static HangingSignBlock.Attachment getAttachmentPoint(final BlockState blockState) {
      Block var2 = blockState.getBlock();
      HangingSignBlock.Attachment var10000;
      if (var2 instanceof HangingSignBlock hangingSignBlock) {
         var10000 = hangingSignBlock.attachmentPoint(blockState);
      } else {
         var10000 = HangingSignBlock.Attachment.CEILING;
      }

      return var10000;
   }

   public static enum Attachment implements StringRepresentable {
      WALL("wall"),
      CEILING("ceiling"),
      CEILING_MIDDLE("ceiling_middle");

      public static final Codec CODEC = StringRepresentable.fromEnum(HangingSignBlock.Attachment::values);
      private final String name;

      private Attachment(final String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static HangingSignBlock.Attachment[] $values() {
         return new HangingSignBlock.Attachment[]{WALL, CEILING, CEILING_MIDDLE};
      }
   }
}
