package net.minecraft.world.level.block;

import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class TargetBlock extends Block {
   private static final IntegerProperty OUTPUT_POWER = BlockStateProperties.POWER;
   private static final int ACTIVATION_TICKS_ARROWS = 20;
   private static final int ACTIVATION_TICKS_OTHER = 8;

   public TargetBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(OUTPUT_POWER, Integer.valueOf(0)));
   }

   protected void onProjectileHit(final Level level, final BlockState state, final BlockHitResult hitResult, final Projectile projectile) {
      int outputStrength = updateRedstoneOutput(level, state, hitResult, projectile);
      Entity owner = projectile.getOwner();
      if (owner instanceof ServerPlayer playerOwner) {
         playerOwner.awardStat(Stats.TARGET_HIT);
         CriteriaTriggers.TARGET_BLOCK_HIT.trigger(playerOwner, projectile, hitResult.getLocation(), outputStrength);
      }

   }

   private static int updateRedstoneOutput(final LevelAccessor level, final BlockState state, final BlockHitResult hitResult, final Entity entity) {
      int redstoneStrength = getRedstoneStrength(hitResult, hitResult.getLocation());
      int duration = entity instanceof AbstractArrow ? 20 : 8;
      if (!level.getBlockTicks().hasScheduledTick(hitResult.getBlockPos(), state.getBlock())) {
         setOutputPower(level, state, redstoneStrength, hitResult.getBlockPos(), duration);
      }

      return redstoneStrength;
   }

   private static int getRedstoneStrength(final BlockHitResult hitResult, final Vec3 hitLocation) {
      Direction hitDirection = hitResult.getDirection();
      double distX = Math.abs(Mth.frac(hitLocation.x) - 0.5D);
      double distY = Math.abs(Mth.frac(hitLocation.y) - 0.5D);
      double distZ = Math.abs(Mth.frac(hitLocation.z) - 0.5D);
      Direction.Axis axis = hitDirection.getAxis();
      double distance;
      if (axis == Direction.Axis.Y) {
         distance = Math.max(distX, distZ);
      } else if (axis == Direction.Axis.Z) {
         distance = Math.max(distX, distY);
      } else {
         distance = Math.max(distY, distZ);
      }

      return Math.max(1, Mth.ceil(15.0D * Mth.clamp((0.5D - distance) / 0.5D, 0.0D, 1.0D)));
   }

   private static void setOutputPower(final LevelAccessor level, final BlockState state, final int outputStrength, final BlockPos pos, final int duration) {
      level.setBlockAndUpdate(pos, (BlockState)state.setValue(OUTPUT_POWER, Integer.valueOf(outputStrength)));
      level.scheduleTick(pos, state.getBlock(), duration);
   }

   protected void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      if (state.getValue(OUTPUT_POWER) != 0) {
         level.setBlockAndUpdate(pos, (BlockState)state.setValue(OUTPUT_POWER, Integer.valueOf(0)));
      }

   }

   protected int ownSignal(final BlockState state, final BlockGetter level, final BlockPos pos) {
      return state.getValue(OUTPUT_POWER);
   }

   protected boolean isSignalSource(final BlockState state) {
      return true;
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder builder) {
      builder.add(OUTPUT_POWER);
   }

   protected void onPlace(final BlockState state, final Level level, final BlockPos pos, final BlockState oldState, final boolean movedByPiston) {
      if (!level.isClientSide() && !state.is(oldState.getBlock())) {
         if (state.getValue(OUTPUT_POWER) > 0 && !level.getBlockTicks().hasScheduledTick(pos, this)) {
            level.setBlock(pos, (BlockState)state.setValue(OUTPUT_POWER, Integer.valueOf(0)), 18);
         }

      }
   }
}
