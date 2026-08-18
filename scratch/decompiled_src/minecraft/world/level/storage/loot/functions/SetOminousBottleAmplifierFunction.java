package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.OminousBottleAmplifier;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class SetOminousBottleAmplifierFunction extends LootItemConditionalFunction {
   public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> commonFields(i).and(NumberProviders.CODEC.fieldOf("amplifier").forGetter((f) -> f.amplifier)).apply(i, SetOminousBottleAmplifierFunction::new));
   private final Holder amplifier;

   private SetOminousBottleAmplifierFunction(final Optional condition, final Holder amplifier) {
      super(condition);
      this.amplifier = amplifier;
   }

   public void validate(final ValidationContext context) {
      super.validate(context);
      Validatable.validateHolder(context, "amplifier", this.amplifier);
   }

   public MapCodec codec() {
      return MAP_CODEC;
   }

   public ItemStack run(final ItemStack itemStack, final LootContext context) {
      int amplifierValue = Mth.clamp(((NumberProvider)this.amplifier.value()).getInt(context), 0, 4);
      itemStack.set(DataComponents.OMINOUS_BOTTLE_AMPLIFIER, new OminousBottleAmplifier(amplifierValue));
      return itemStack;
   }

   public static LootItemConditionalFunction.Builder setAmplifier(final Holder amplifier) {
      return simpleBuilder((conditions) -> new SetOminousBottleAmplifierFunction(conditions, amplifier));
   }
}
