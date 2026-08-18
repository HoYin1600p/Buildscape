package net.minecraft.core.component.predicates;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.predicates.SingleComponentItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.world.entity.npc.villager.VillagerType;

public record VillagerTypePredicate(HolderSet villagerTypes) implements SingleComponentItemPredicate {
   public static final Codec CODEC = RegistryCodecs.holderSet(Registries.VILLAGER_TYPE).xmap(VillagerTypePredicate::new, VillagerTypePredicate::villagerTypes);

   public DataComponentType componentType() {
      return DataComponents.VILLAGER_VARIANT;
   }

   public boolean matches(final Holder villagerType) {
      return this.villagerTypes.contains(villagerType);
   }

   public static VillagerTypePredicate villagerTypes(final HolderSet villagerTypes) {
      return new VillagerTypePredicate(villagerTypes);
   }
}
