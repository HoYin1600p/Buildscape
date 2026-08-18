package net.minecraft.world.item;

import com.mojang.serialization.Codec;
import java.util.function.IntFunction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public enum ItemUseAnimation implements StringRepresentable {
   NONE(0, "none"),
   EAT(1, "eat", true),
   DRINK(2, "drink", true),
   BLOCK(3, "block"),
   BOW(4, "bow"),
   TRIDENT(5, "trident"),
   CROSSBOW(6, "crossbow"),
   SPYGLASS(7, "spyglass"),
   TOOT_HORN(8, "toot_horn"),
   BRUSH(9, "brush"),
   BUNDLE(10, "bundle"),
   SPEAR(11, "spear", true);

   private static final IntFunction BY_ID = ByIdMap.continuous(ItemUseAnimation::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
   public static final Codec CODEC = StringRepresentable.fromEnum(ItemUseAnimation::values);
   public static final StreamCodec STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, ItemUseAnimation::getId);
   private final int id;
   private final String name;
   private final boolean customArmTransform;

   private ItemUseAnimation(final int id, final String name) {
      this(id, name, false);
   }

   private ItemUseAnimation(final int id, final String name, final boolean customArmTransform) {
      this.id = id;
      this.name = name;
      this.customArmTransform = customArmTransform;
   }

   public int getId() {
      return this.id;
   }

   public String getSerializedName() {
      return this.name;
   }

   public boolean hasCustomArmTransform() {
      return this.customArmTransform;
   }

   // $FF: synthetic method
   private static ItemUseAnimation[] $values() {
      return new ItemUseAnimation[]{NONE, EAT, DRINK, BLOCK, BOW, TRIDENT, CROSSBOW, SPYGLASS, TOOT_HORN, BRUSH, BUNDLE, SPEAR};
   }
}
