package net.minecraft.world.attribute;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public interface EnvironmentAttributeReader {
   EnvironmentAttributeReader EMPTY = new EnvironmentAttributeReader() {
      public Object getDimensionValue(final EnvironmentAttribute attribute) {
         return attribute.defaultValue();
      }

      public Object getValue(final EnvironmentAttribute attribute, final Vec3 pos, final @Nullable SpatialAttributeInterpolator biomeInterpolator) {
         return attribute.defaultValue();
      }
   };

   Object getDimensionValue(EnvironmentAttribute attribute);

   default Object getValue(final EnvironmentAttribute attribute, final BlockPos pos) {
      return this.getValue(attribute, Vec3.atCenterOf(pos));
   }

   default Object getValue(final EnvironmentAttribute attribute, final Vec3 pos) {
      return this.getValue(attribute, pos, (SpatialAttributeInterpolator)null);
   }

   Object getValue(EnvironmentAttribute attribute, Vec3 pos, @Nullable SpatialAttributeInterpolator biomeInterpolator);

   default Object getValue(final LootContext context, final EnvironmentAttribute attribute) {
      return attribute.isPositional() ? this.getValue(attribute, (Vec3)context.getParameter(LootContextParams.ORIGIN)) : this.getDimensionValue(attribute);
   }
}
