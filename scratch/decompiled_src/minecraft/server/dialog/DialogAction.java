package net.minecraft.server.dialog;

import com.mojang.serialization.Codec;
import java.util.function.IntFunction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public enum DialogAction implements StringRepresentable {
   CLOSE(0, "close"),
   NONE(1, "none"),
   WAIT_FOR_RESPONSE(2, "wait_for_response");

   public static final IntFunction BY_ID = ByIdMap.continuous((s) -> s.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
   public static final Codec CODEC = StringRepresentable.fromEnum(DialogAction::values);
   public static final StreamCodec STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, (s) -> s.id);
   private final int id;
   private final String name;

   private DialogAction(final int id, final String name) {
      this.id = id;
      this.name = name;
   }

   public String getSerializedName() {
      return this.name;
   }

   public boolean willUnpause() {
      return this == CLOSE || this == WAIT_FOR_RESPONSE;
   }

   // $FF: synthetic method
   private static DialogAction[] $values() {
      return new DialogAction[]{CLOSE, NONE, WAIT_FOR_RESPONSE};
   }
}
