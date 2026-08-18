package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class SetAttributesFunction extends LootItemConditionalFunction {
   public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> commonFields(i).and(i.group(SetAttributesFunction.Modifier.CODEC.listOf().fieldOf("modifiers").forGetter((f) -> f.modifiers), Codec.BOOL.optionalFieldOf("replace", true).forGetter((f) -> f.replace))).apply(i, SetAttributesFunction::new));
   private final List modifiers;
   private final boolean replace;

   private SetAttributesFunction(final Optional condition, final List modifiers, final boolean replace) {
      super(condition);
      this.modifiers = List.copyOf(modifiers);
      this.replace = replace;
   }

   public MapCodec codec() {
      return MAP_CODEC;
   }

   public void validate(final ValidationContext context) {
      super.validate(context);
      Validatable.validate(context, "modifiers", this.modifiers);
   }

   public ItemStack run(final ItemStack itemStack, final LootContext context) {
      if (this.replace) {
         itemStack.set(DataComponents.ATTRIBUTE_MODIFIERS, this.updateModifiers(context, ItemAttributeModifiers.EMPTY));
      } else {
         itemStack.update(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY, (itemModifiers) -> this.updateModifiers(context, itemModifiers));
      }

      return itemStack;
   }

   private ItemAttributeModifiers updateModifiers(final LootContext context, ItemAttributeModifiers itemModifiers) {
      RandomSource random = context.getRandom();

      for(SetAttributesFunction.Modifier modifier : this.modifiers) {
         EquipmentSlotGroup slot = (EquipmentSlotGroup)Util.getRandom(modifier.slots, random);
         itemModifiers = itemModifiers.withModifierAdded(modifier.attribute, new AttributeModifier(modifier.id, (double)((NumberProvider)modifier.amount.value()).getFloat(context), modifier.operation), slot);
      }

      return itemModifiers;
   }

   public static SetAttributesFunction.ModifierBuilder modifier(final Identifier id, final Holder attribute, final AttributeModifier.Operation operation, final Holder amount) {
      return new SetAttributesFunction.ModifierBuilder(id, attribute, operation, amount);
   }

   public static SetAttributesFunction.Builder setAttributes() {
      return new SetAttributesFunction.Builder();
   }

   public static class Builder extends LootItemConditionalFunction.Builder {
      private final boolean replace;
      private final List modifiers = Lists.newArrayList();

      public Builder(final boolean replace) {
         this.replace = replace;
      }

      public Builder() {
         this(false);
      }

      protected SetAttributesFunction.Builder getThis() {
         return this;
      }

      public SetAttributesFunction.Builder withModifier(final SetAttributesFunction.ModifierBuilder modifier) {
         this.modifiers.add(modifier.build());
         return this;
      }

      public LootItemFunction build() {
         return new SetAttributesFunction(this.getCondition(), this.modifiers, this.replace);
      }
   }

   private static record Modifier(Identifier id, Holder attribute, AttributeModifier.Operation operation, Holder amount, List slots) implements LootContextUser {
      private static final Codec SLOTS_CODEC = ExtraCodecs.nonEmptyList(ExtraCodecs.compactListCodec(EquipmentSlotGroup.CODEC));
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(Identifier.CODEC.fieldOf("id").forGetter(SetAttributesFunction.Modifier::id), Attribute.CODEC.fieldOf("attribute").forGetter(SetAttributesFunction.Modifier::attribute), AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(SetAttributesFunction.Modifier::operation), NumberProviders.CODEC.fieldOf("amount").forGetter(SetAttributesFunction.Modifier::amount), SLOTS_CODEC.fieldOf("slot").forGetter(SetAttributesFunction.Modifier::slots)).apply(i, SetAttributesFunction.Modifier::new));

      public void validate(final ValidationContext context) {
         LootContextUser.super.validate(context);
         Validatable.validateHolder(context, "amount", this.amount);
      }
   }

   public static class ModifierBuilder {
      private final Identifier id;
      private final Holder attribute;
      private final AttributeModifier.Operation operation;
      private final Holder amount;
      private final Set slots = EnumSet.noneOf(EquipmentSlotGroup.class);

      public ModifierBuilder(final Identifier id, final Holder attribute, final AttributeModifier.Operation operation, final Holder amount) {
         this.id = id;
         this.attribute = attribute;
         this.operation = operation;
         this.amount = amount;
      }

      public SetAttributesFunction.ModifierBuilder forSlot(final EquipmentSlotGroup slot) {
         this.slots.add(slot);
         return this;
      }

      public SetAttributesFunction.Modifier build() {
         return new SetAttributesFunction.Modifier(this.id, this.attribute, this.operation, this.amount, List.copyOf(this.slots));
      }
   }
}
