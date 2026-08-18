package net.minecraft.core.component.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.advancements.predicates.CollectionPredicate;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.advancements.predicates.SingleComponentItemPredicate;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public record AttributeModifiersPredicate(Optional modifiers) implements SingleComponentItemPredicate {
   public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(CollectionPredicate.codec(AttributeModifiersPredicate.EntryPredicate.CODEC).optionalFieldOf("modifiers").forGetter(AttributeModifiersPredicate::modifiers)).apply(i, AttributeModifiersPredicate::new));

   public DataComponentType componentType() {
      return DataComponents.ATTRIBUTE_MODIFIERS;
   }

   public boolean matches(final ItemAttributeModifiers value) {
      return !this.modifiers.isPresent() || ((CollectionPredicate)this.modifiers.get()).test(value.modifiers());
   }

   public static record EntryPredicate(Optional attribute, Optional id, MinMaxBounds.Doubles amount, Optional operation, Optional slot) implements Predicate {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(RegistryCodecs.holderSet(Registries.ATTRIBUTE).optionalFieldOf("attribute").forGetter(AttributeModifiersPredicate.EntryPredicate::attribute), Identifier.CODEC.optionalFieldOf("id").forGetter(AttributeModifiersPredicate.EntryPredicate::id), MinMaxBounds.Doubles.CODEC.optionalFieldOf("amount", MinMaxBounds.Doubles.ANY).forGetter(AttributeModifiersPredicate.EntryPredicate::amount), AttributeModifier.Operation.CODEC.optionalFieldOf("operation").forGetter(AttributeModifiersPredicate.EntryPredicate::operation), EquipmentSlotGroup.CODEC.optionalFieldOf("slot").forGetter(AttributeModifiersPredicate.EntryPredicate::slot)).apply(i, AttributeModifiersPredicate.EntryPredicate::new));

      public boolean test(final ItemAttributeModifiers.Entry value) {
         if (this.attribute.isPresent() && !((HolderSet)this.attribute.get()).contains(value.attribute())) {
            return false;
         } else if (this.id.isPresent() && !((Identifier)this.id.get()).equals(value.modifier().id())) {
            return false;
         } else if (!this.amount.matches(value.modifier().amount())) {
            return false;
         } else if (this.operation.isPresent() && this.operation.get() != value.modifier().operation()) {
            return false;
         } else {
            return !this.slot.isPresent() || this.slot.get() == value.slot();
         }
      }
   }
}
