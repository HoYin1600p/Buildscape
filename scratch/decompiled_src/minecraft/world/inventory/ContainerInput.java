package net.minecraft.world.inventory;

import java.util.function.IntFunction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;

public enum ContainerInput {
   PICKUP(0),
   QUICK_MOVE(1),
   SWAP(2),
   CLONE(3),
   THROW(4),
   QUICK_CRAFT(5),
   PICKUP_ALL(6);

   private static final IntFunction BY_ID = ByIdMap.continuous(ContainerInput::id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
   public static final StreamCodec STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, ContainerInput::id);
   private final int id;

   private ContainerInput(final int id) {
      this.id = id;
   }

   public int id() {
      return this.id;
   }

   // $FF: synthetic method
   private static ContainerInput[] $values() {
      return new ContainerInput[]{PICKUP, QUICK_MOVE, SWAP, CLONE, THROW, QUICK_CRAFT, PICKUP_ALL};
   }
}
