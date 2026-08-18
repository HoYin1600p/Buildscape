package net.minecraft.core.component.predicates;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Function;
import net.minecraft.advancements.predicates.EnchantmentPredicate;
import net.minecraft.advancements.predicates.SingleComponentItemPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public abstract class EnchantmentsPredicate implements SingleComponentItemPredicate {
   private final List enchantments;

   protected EnchantmentsPredicate(final List enchantments) {
      this.enchantments = enchantments;
   }

   public static Codec codec(final Function constructor) {
      return EnchantmentPredicate.CODEC.listOf().xmap(constructor, EnchantmentsPredicate::enchantments);
   }

   protected List enchantments() {
      return this.enchantments;
   }

   public boolean matches(final ItemEnchantments appliedEnchantments) {
      for(EnchantmentPredicate enchantment : this.enchantments) {
         if (!enchantment.containedIn(appliedEnchantments)) {
            return false;
         }
      }

      return true;
   }

   public static EnchantmentsPredicate.Enchantments enchantments(final List predicates) {
      return new EnchantmentsPredicate.Enchantments(predicates);
   }

   public static EnchantmentsPredicate.StoredEnchantments storedEnchantments(final List predicates) {
      return new EnchantmentsPredicate.StoredEnchantments(predicates);
   }

   public static class Enchantments extends EnchantmentsPredicate {
      public static final Codec CODEC = codec(EnchantmentsPredicate.Enchantments::new);

      protected Enchantments(final List enchantments) {
         super(enchantments);
      }

      public DataComponentType componentType() {
         return DataComponents.ENCHANTMENTS;
      }
   }

   public static class StoredEnchantments extends EnchantmentsPredicate {
      public static final Codec CODEC = codec(EnchantmentsPredicate.StoredEnchantments::new);

      protected StoredEnchantments(final List enchantments) {
         super(enchantments);
      }

      public DataComponentType componentType() {
         return DataComponents.STORED_ENCHANTMENTS;
      }
   }
}
