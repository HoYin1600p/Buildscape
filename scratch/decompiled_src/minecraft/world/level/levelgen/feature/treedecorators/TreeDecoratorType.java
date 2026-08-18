package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class TreeDecoratorType {
   public static final TreeDecoratorType TRUNK_VINE = register("trunk_vine", TrunkVineDecorator.CODEC);
   public static final TreeDecoratorType LEAVE_VINE = register("leave_vine", LeaveVineDecorator.CODEC);
   public static final TreeDecoratorType PALE_MOSS = register("pale_moss", PaleMossDecorator.CODEC);
   public static final TreeDecoratorType CREAKING_HEART = register("creaking_heart", CreakingHeartDecorator.CODEC);
   public static final TreeDecoratorType COCOA = register("cocoa", CocoaDecorator.CODEC);
   public static final TreeDecoratorType SHELF_MUSHROOM = register("shelf_mushroom", ShelfMushroomDecorator.CODEC);
   public static final TreeDecoratorType BEEHIVE = register("beehive", BeehiveDecorator.CODEC);
   public static final TreeDecoratorType ALTER_GROUND = register("alter_ground", AlterGroundDecorator.CODEC);
   public static final TreeDecoratorType ATTACHED_TO_LEAVES = register("attached_to_leaves", AttachedToLeavesDecorator.CODEC);
   public static final TreeDecoratorType PLACE_ON_GROUND = register("place_on_ground", PlaceOnGroundDecorator.CODEC);
   public static final TreeDecoratorType ATTACHED_TO_LOGS = register("attached_to_logs", AttachedToLogsDecorator.CODEC);
   private final MapCodec codec;

   private static TreeDecoratorType register(final String name, final MapCodec codec) {
      return (TreeDecoratorType)Registry.register(BuiltInRegistries.TREE_DECORATOR_TYPE, name, new TreeDecoratorType(codec));
   }

   private TreeDecoratorType(final MapCodec codec) {
      this.codec = codec;
   }

   public MapCodec codec() {
      return this.codec;
   }
}
