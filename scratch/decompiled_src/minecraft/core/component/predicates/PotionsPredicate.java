package net.minecraft.core.component.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.predicates.CollectionPredicate;
import net.minecraft.advancements.predicates.MobEffectsPredicate;
import net.minecraft.advancements.predicates.SingleComponentItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;

public record PotionsPredicate(Optional potions, Optional effects) implements SingleComponentItemPredicate {
   public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(RegistryCodecs.holderSet(Registries.POTION).optionalFieldOf("potions").forGetter(PotionsPredicate::potions), CollectionPredicate.codec(MobEffectsPredicate.CODEC).optionalFieldOf("effects").forGetter(PotionsPredicate::effects)).apply(i, PotionsPredicate::new));
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.optional(ByteBufCodecs.holderSet(Registries.POTION)), PotionsPredicate::potions, ByteBufCodecs.optional(ByteBufCodecs.fromCodecTrusted(CollectionPredicate.codec(MobEffectsPredicate.CODEC))), PotionsPredicate::effects, PotionsPredicate::new);

   public DataComponentType componentType() {
      return DataComponents.POTION_CONTENTS;
   }

   public boolean matches(final PotionContents potionContents) {
      Optional potion = potionContents.potion();
      if (!this.potions.isPresent() || !potion.isEmpty() && ((HolderSet)this.potions.get()).contains((Holder)potion.get())) {
         return this.effects.isPresent() ? ((CollectionPredicate)this.effects.get()).test(potionContents.getAllEffects()) : true;
      } else {
         return false;
      }
   }

   public static PotionsPredicate ofPotions(final HolderSet potions) {
      return new PotionsPredicate(Optional.of(potions), Optional.empty());
   }

   public static PotionsPredicate ofPotion(final Holder potion) {
      return ofPotions(HolderSet.direct(potion));
   }
}
