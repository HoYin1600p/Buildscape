package net.minecraft.world.item;

import java.util.function.IntFunction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

public enum SwingAnimationType implements StringRepresentable {
   WHACK(0, "whack"),
   STAB(1, "stab");

   private static final IntFunction BY_ID = ByIdMap.continuous(SwingAnimationType::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
   public static final StringRepresentable.EnumCodec CODEC = StringRepresentable.fromEnum(SwingAnimationType::values);
   public static final StreamCodec STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, SwingAnimationType::getId);
   private final int id;
   private final String name;

   private SwingAnimationType(final int id, final String name) {
      this.id = id;
      this.name = name;
   }

   public int getId() {
      return this.id;
   }

   public String getSerializedName() {
      return this.name;
   }

   @Contract("_,!null->!null;_,null->_")
   public static @Nullable SwingAnimationType byName(final String name, final @Nullable SwingAnimationType defaultAnimation) {
      SwingAnimationType result = (SwingAnimationType)CODEC.byName(name);
      return result != null ? result : defaultAnimation;
   }

   // $FF: synthetic method
   private static SwingAnimationType[] $values() {
      return new SwingAnimationType[]{WHACK, STAB};
   }
}
