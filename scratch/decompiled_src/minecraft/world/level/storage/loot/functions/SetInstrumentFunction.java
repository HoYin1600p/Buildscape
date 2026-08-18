package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.InstrumentComponent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetInstrumentFunction extends LootItemConditionalFunction {
   public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> commonFields(i).and(RegistryCodecs.holderSet(Registries.INSTRUMENT).fieldOf("options").forGetter((f) -> f.options)).apply(i, SetInstrumentFunction::new));
   private final HolderSet options;

   private SetInstrumentFunction(final Optional condition, final HolderSet options) {
      super(condition);
      this.options = options;
   }

   public MapCodec codec() {
      return MAP_CODEC;
   }

   public ItemStack run(final ItemStack itemStack, final LootContext context) {
      this.options.getRandomElement(context.getRandom()).ifPresent((instrumentHolder) -> itemStack.set(DataComponents.INSTRUMENT, new InstrumentComponent(instrumentHolder)));
      return itemStack;
   }

   public static LootItemConditionalFunction.Builder setInstrumentOptions(final HolderSet options) {
      return simpleBuilder((conditions) -> new SetInstrumentFunction(conditions, options));
   }
}
