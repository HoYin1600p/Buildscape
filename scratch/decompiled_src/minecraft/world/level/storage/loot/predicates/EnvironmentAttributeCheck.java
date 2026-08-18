package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.MapCodec;
import java.util.Set;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public record EnvironmentAttributeCheck(EnvironmentAttribute attribute, Object value) implements LootItemCondition {
   public static final MapCodec MAP_CODEC = EnvironmentAttributes.CODEC.dispatchMap("attribute", EnvironmentAttributeCheck::attribute, EnvironmentAttributeCheck::createCodec);

   private static MapCodec createCodec(final EnvironmentAttribute attribute) {
      return attribute.valueCodec().fieldOf("value").xmap((value) -> new EnvironmentAttributeCheck(attribute, value), EnvironmentAttributeCheck::value);
   }

   public MapCodec codec() {
      return MAP_CODEC;
   }

   public Set getReferencedContextParams() {
      return this.attribute.isPositional() ? Set.of(LootContextParams.ORIGIN) : Set.of();
   }

   public boolean test(final LootContext context) {
      Object actualValue = (Value)context.getLevel().environmentAttributes().getValue(context, this.attribute);
      return this.value.equals(actualValue);
   }

   public static LootItemCondition.Builder environmentAttribute(final EnvironmentAttribute attribute, final Object value) {
      return () -> new EnvironmentAttributeCheck(attribute, value);
   }
}
