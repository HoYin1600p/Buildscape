package net.minecraft.core.component.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.predicates.SingleComponentItemPredicate;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;

public record TrimPredicate(Optional material, Optional pattern) implements SingleComponentItemPredicate {
   public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(RegistryCodecs.holderSet(Registries.TRIM_MATERIAL).optionalFieldOf("material").forGetter(TrimPredicate::material), RegistryCodecs.holderSet(Registries.TRIM_PATTERN).optionalFieldOf("pattern").forGetter(TrimPredicate::pattern)).apply(i, TrimPredicate::new));

   public DataComponentType componentType() {
      return DataComponents.TRIM;
   }

   public boolean matches(final ArmorTrim value) {
      if (this.material.isPresent() && !((HolderSet)this.material.get()).contains(value.material())) {
         return false;
      } else {
         return !this.pattern.isPresent() || ((HolderSet)this.pattern.get()).contains(value.pattern());
      }
   }
}
