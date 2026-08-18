package net.minecraft.world.item.equipment;

import java.util.Map;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.item.DyeColor;

public interface EquipmentAssets {
   ResourceKey ROOT_ID = ResourceKey.createRegistryKey(Identifier.withDefaultNamespace("equipment_asset"));
   ResourceKey LEATHER = createId("leather");
   ResourceKey COPPER = createId("copper");
   ResourceKey CHAINMAIL = createId("chainmail");
   ResourceKey IRON = createId("iron");
   ResourceKey GOLD = createId("gold");
   ResourceKey DIAMOND = createId("diamond");
   ResourceKey TURTLE_SCUTE = createId("turtle_scute");
   ResourceKey NETHERITE = createId("netherite");
   ResourceKey ARMADILLO_SCUTE = createId("armadillo_scute");
   ResourceKey ELYTRA = createId("elytra");
   ResourceKey SADDLE = createId("saddle");
   Map CARPETS = Util.makeEnumMap(DyeColor.class, (color) -> createId(color.getSerializedName() + "_carpet"));
   ResourceKey TRADER_LLAMA = createId("trader_llama");
   ResourceKey TRADER_LLAMA_BABY = createId("trader_llama_baby");
   Map HARNESSES = Util.makeEnumMap(DyeColor.class, (color) -> createId(color.getSerializedName() + "_harness"));

   static ResourceKey createId(final String name) {
      return ResourceKey.create(ROOT_ID, Identifier.withDefaultNamespace(name));
   }
}
