package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class PressurePlateBlock extends BasePressurePlateBlock {
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

   protected PressurePlateBlock(final BlockSetType type, final BlockBehaviour.Properties properties) {
      super(properties, type);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(POWERED, Boolean.valueOf(false)));
   }

   protected int getSignalForState(final BlockState state) {
      return state.getValue(POWERED) ? 15 : 0;
   }

   protected BlockState setSignalForState(final BlockState state, final int signal) {
      return (BlockState)state.setValue(POWERED, Boolean.valueOf(signal > 0));
   }

   protected int getSignalStrength(final Level level, final BlockPos pos) {
      Class<Entity> var10000;
      switch (this.type.pressurePlateSensitivity()) {
         case EVERYTHING:
            var10000 = Entity.class;
            break;
         case MOBS:
            var10000 = LivingEntity.class;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      Class entityClass = var10000;
      return getEntityCount(level, TOUCH_AABB.move(pos), entityClass) > 0 ? 15 : 0;
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder builder) {
      builder.add(POWERED);
   }
}
