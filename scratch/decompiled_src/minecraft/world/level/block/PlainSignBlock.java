package net.minecraft.world.level.block;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;

public interface PlainSignBlock {
   PlainSignBlock.Attachment attachmentPoint(BlockState state);

   static PlainSignBlock.Attachment getAttachmentPoint(final BlockState blockState) {
      Block var2 = blockState.getBlock();
      PlainSignBlock.Attachment var10000;
      if (var2 instanceof PlainSignBlock plainSignBlock) {
         var10000 = plainSignBlock.attachmentPoint(blockState);
      } else {
         var10000 = PlainSignBlock.Attachment.GROUND;
      }

      return var10000;
   }

   public static enum Attachment implements StringRepresentable {
      WALL("wall"),
      GROUND("ground");

      public static final Codec CODEC = StringRepresentable.fromEnum(PlainSignBlock.Attachment::values);
      private final String name;

      private Attachment(final String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static PlainSignBlock.Attachment[] $values() {
         return new PlainSignBlock.Attachment[]{WALL, GROUND};
      }
   }
}
