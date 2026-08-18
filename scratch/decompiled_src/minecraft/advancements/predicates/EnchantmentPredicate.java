package net.minecraft.advancements.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public record EnchantmentPredicate(Optional enchantments, MinMaxBounds.Ints level) {
   public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(RegistryCodecs.holderSet(Registries.ENCHANTMENT).optionalFieldOf("enchantments").forGetter(EnchantmentPredicate::enchantments), MinMaxBounds.Ints.CODEC.optionalFieldOf("levels", MinMaxBounds.Ints.ANY).forGetter(EnchantmentPredicate::level)).apply(i, EnchantmentPredicate::new));

   public EnchantmentPredicate(final Holder enchantment, final MinMaxBounds.Ints level) {
      this(Optional.of(HolderSet.direct(enchantment)), level);
   }

   public EnchantmentPredicate(final HolderSet enchantments, final MinMaxBounds.Ints level) {
      this(Optional.of(enchantments), level);
   }

   public boolean containedIn(final ItemEnchantments itemEnchantments) {
      if (this.enchantments.isPresent()) {
         for(Holder enchantment : (HolderSet)this.enchantments.get()) {
            if (this.matchesEnchantment(itemEnchantments, enchantment)) {
               return true;
            }
         }

         return false;
      } else if (this.level != MinMaxBounds.Ints.ANY) {
         for(Object2IntMap.Entry entry : itemEnchantments.entrySet()) {
            if (this.level.matches(entry.getIntValue())) {
               return true;
            }
         }

         return false;
      } else {
         return !itemEnchantments.isEmpty();
      }
   }

   private boolean matchesEnchantment(final ItemEnchantments itemEnchantments, final Holder enchantment) {
      int level = itemEnchantments.getLevel(enchantment);
      if (level == 0) {
         return false;
      } else {
         return this.level == MinMaxBounds.Ints.ANY ? true : this.level.matches(level);
      }
   }
}
