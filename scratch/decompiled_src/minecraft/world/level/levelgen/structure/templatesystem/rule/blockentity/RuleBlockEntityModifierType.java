package net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public interface RuleBlockEntityModifierType {
   RuleBlockEntityModifierType CLEAR = register("clear", Clear.CODEC);
   RuleBlockEntityModifierType PASSTHROUGH = register("passthrough", Passthrough.CODEC);
   RuleBlockEntityModifierType APPEND_STATIC = register("append_static", AppendStatic.CODEC);
   RuleBlockEntityModifierType APPEND_LOOT = register("append_loot", AppendLoot.CODEC);

   MapCodec codec();

   private static RuleBlockEntityModifierType register(final String id, final MapCodec codec) {
      return (RuleBlockEntityModifierType)Registry.register(BuiltInRegistries.RULE_BLOCK_ENTITY_MODIFIER, id, (RuleBlockEntityModifierType)() -> codec);
   }
}
