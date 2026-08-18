package net.minecraft.core.component.predicates;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class DataComponentPredicates {
   public static final DataComponentPredicate.Type DAMAGE = register("damage", DamagePredicate.CODEC);
   public static final DataComponentPredicate.Type ENCHANTMENTS = register("enchantments", EnchantmentsPredicate.Enchantments.CODEC);
   public static final DataComponentPredicate.Type STORED_ENCHANTMENTS = register("stored_enchantments", EnchantmentsPredicate.StoredEnchantments.CODEC);
   public static final DataComponentPredicate.Type POTIONS = register("potion_contents", PotionsPredicate.CODEC);
   public static final DataComponentPredicate.Type CUSTOM_DATA = register("custom_data", CustomDataPredicate.CODEC);
   public static final DataComponentPredicate.Type CONTAINER = register("container", ContainerPredicate.CODEC);
   public static final DataComponentPredicate.Type BUNDLE_CONTENTS = register("bundle_contents", BundlePredicate.CODEC);
   public static final DataComponentPredicate.Type FIREWORK_EXPLOSION = register("firework_explosion", FireworkExplosionPredicate.CODEC);
   public static final DataComponentPredicate.Type FIREWORKS = register("fireworks", FireworksPredicate.CODEC);
   public static final DataComponentPredicate.Type WRITABLE_BOOK = register("writable_book_content", WritableBookPredicate.CODEC);
   public static final DataComponentPredicate.Type WRITTEN_BOOK = register("written_book_content", WrittenBookPredicate.CODEC);
   public static final DataComponentPredicate.Type ATTRIBUTE_MODIFIERS = register("attribute_modifiers", AttributeModifiersPredicate.CODEC);
   public static final DataComponentPredicate.Type ARMOR_TRIM = register("trim", TrimPredicate.CODEC);
   public static final DataComponentPredicate.Type JUKEBOX_PLAYABLE = register("jukebox_playable", JukeboxPlayablePredicate.CODEC);
   public static final DataComponentPredicate.Type VILLAGER_VARIANT = register("villager/variant", VillagerTypePredicate.CODEC);

   private static DataComponentPredicate.Type register(final String id, final Codec codec) {
      return (DataComponentPredicate.Type)Registry.register(BuiltInRegistries.DATA_COMPONENT_PREDICATE_TYPE, id, new DataComponentPredicate.ConcreteType(codec));
   }

   public static DataComponentPredicate.Type bootstrap(final Registry registry) {
      return DAMAGE;
   }
}
