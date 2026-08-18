package net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity;

import com.mojang.serialization.MapCodec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;

public class Passthrough implements RuleBlockEntityModifier {
   public static final Passthrough INSTANCE = new Passthrough();
   public static final MapCodec CODEC = MapCodec.unit(INSTANCE);

   public @Nullable CompoundTag apply(final RandomSource random, final @Nullable CompoundTag existingTag) {
      return existingTag;
   }

   public RuleBlockEntityModifierType getType() {
      return RuleBlockEntityModifierType.PASSTHROUGH;
   }
}
