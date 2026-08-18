package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public interface PosRuleTestType {
   PosRuleTestType ALWAYS_TRUE_TEST = register("always_true", PosAlwaysTrueTest.CODEC);
   PosRuleTestType LINEAR_POS_TEST = register("linear_pos", LinearPosTest.CODEC);
   PosRuleTestType AXIS_ALIGNED_LINEAR_POS_TEST = register("axis_aligned_linear_pos", AxisAlignedLinearPosTest.CODEC);

   MapCodec codec();

   static PosRuleTestType register(final String id, final MapCodec codec) {
      return (PosRuleTestType)Registry.register(BuiltInRegistries.POS_RULE_TEST, id, (PosRuleTestType)() -> codec);
   }
}
