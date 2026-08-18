package net.minecraft.world.item.slot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;

public abstract class CompositeSlotSource implements SlotSource {
   protected final HolderSet terms;
   private final Function compositeSlotSource;

   protected CompositeSlotSource(final HolderSet terms) {
      this.terms = terms;
      this.compositeSlotSource = group(terms);
   }

   private static Function group(final HolderSet terms) {
      if (!terms.isBound()) {
         return (context) -> {
            List collections = new ArrayList();

            for(Holder term : terms) {
               collections.add(((SlotSource)term.value()).provide(context));
            }

            return SlotCollection.concat(collections);
         };
      } else {
         Function var10000;
         switch (terms.size()) {
            case 0:
               var10000 = (var0) -> SlotCollection.EMPTY;
               break;
            case 1:
               Holder term = terms.get(0);
               var10000 = (context) -> ((SlotSource)term.value()).provide(context);
               break;
            case 2:
               Holder first = terms.get(0);
               Holder second = terms.get(1);
               var10000 = (context) -> SlotCollection.concat(((SlotSource)first.value()).provide(context), ((SlotSource)second.value()).provide(context));
               break;
            default:
               var10000 = (context) -> {
                  List collections = new ArrayList();

                  for(Holder term : terms) {
                     collections.add(((SlotSource)term.value()).provide(context));
                  }

                  return SlotCollection.concat(collections);
               };
         }

         return var10000;
      }
   }

   protected static MapCodec createCodec(final Function factory) {
      return RecordCodecBuilder.mapCodec((i) -> i.group(SlotSources.LIST_CODEC.fieldOf("terms").forGetter((t) -> t.terms)).apply(i, factory));
   }

   protected static Codec createInlineCodec(final Function factory) {
      return SlotSources.LIST_CODEC.xmap(factory, (t) -> t.terms);
   }

   public abstract MapCodec codec();

   public SlotCollection provide(final LootContext context) {
      return (SlotCollection)this.compositeSlotSource.apply(context);
   }

   public void validate(final ValidationContext context) {
      SlotSource.super.validate(context);
      Validatable.validateHolderSet(context, "terms", this.terms);
   }
}
