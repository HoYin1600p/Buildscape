package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.PotentSulfurBlock.1;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.entity.PotentSulfurBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.PotentSulfurState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jspecify.annotations.Nullable;

public class PotentSulfurBlock extends BaseEntityBlock {
   public static final int ALLOWED_WATER_BLOCKS_ABOVE = 4;
   public static final EnumProperty STATE = BlockStateProperties.POTENT_SULFUR_STATE;

   public PotentSulfurBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(STATE, PotentSulfurState.DRY));
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder builder) {
      builder.add(new Property[]{STATE});
   }

   public @Nullable BlockEntity newBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      return new PotentSulfurBlockEntity(worldPosition, blockState);
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      return validBlockState(state, level, pos);
   }

   public @Nullable BlockState getStateForPlacement(final BlockPlaceContext context) {
      return validBlockState(this.defaultBlockState(), context.getLevel(), context.getClickedPos());
   }

   private static BlockState validBlockState(final BlockState state, final LevelReader level, final BlockPos pos) {
      if (!level.getFluidState(pos.above()).isSourceOfType(Fluids.WATER)) {
         return (BlockState)state.setValue(STATE, PotentSulfurState.DRY);
      } else {
         BlockState belowState = level.getBlockState(pos.below());
         if (belowState.is(BlockTags.CAUSES_CONTINUOUS_GEYSER_ERUPTIONS) && isSourceIfFluid(belowState)) {
            return (BlockState)state.setValue(STATE, PotentSulfurState.CONTINUOUS);
         } else if (belowState.is(BlockTags.CAUSES_PERIODIC_GEYSER_ERUPTIONS) && isSourceIfFluid(belowState)) {
            boolean isGeyser = state.getValue(STATE) == PotentSulfurState.ERUPTING || state.getValue(STATE) == PotentSulfurState.DORMANT;
            if (!isGeyser) {
               BlockEntity var6 = level.getBlockEntity(pos);
               if (var6 instanceof PotentSulfurBlockEntity) {
                  PotentSulfurBlockEntity potentSulfurEntity = (PotentSulfurBlockEntity)var6;
                  potentSulfurEntity.resetCountdown();
               }
            }

            return state.getValue(STATE) == PotentSulfurState.ERUPTING ? state : (BlockState)state.setValue(STATE, PotentSulfurState.DORMANT);
         } else {
            return (BlockState)state.setValue(STATE, PotentSulfurState.WET);
         }
      }
   }

   private static boolean isSourceIfFluid(final BlockState belowState) {
      FluidState fluidState = belowState.getFluidState();
      return fluidState.isEmpty() || fluidState.isSource();
   }

   protected void onPlace(final BlockState state, final Level level, final BlockPos pos, final BlockState oldState, final boolean movedByPiston) {
      super.onPlace(state, level, pos, oldState, movedByPiston);
      if (state.getValue(STATE) == PotentSulfurState.ERUPTING || state.getValue(STATE) == PotentSulfurState.CONTINUOUS) {
         level.blockEvent(pos, this, 0, 0);
         level.playSound((Entity)null, pos, state.getValue(STATE) == PotentSulfurState.CONTINUOUS ? SoundEvents.GEYSER_CONTINUOUS_START : SoundEvents.GEYSER_ERUPTION_START, SoundSource.BLOCKS, 1.0F, 1.0F);
         level.gameEvent(GameEvent.BLOCK_ACTIVATE, pos, Context.of(state));
      }

   }

   public void animateTick(final BlockState state, final Level level, final BlockPos pos, final RandomSource random) {
      if (state.getValue(STATE) != PotentSulfurState.DRY) {
         if (level.getFluidState(pos.above()).isSourceOfType(Fluids.WATER)) {
            spawnBubbleParticlesAt(level, random, (double)pos.getX(), (double)(pos.getY() + 1), (double)pos.getZ());
            spawnBubbleParticlesAt(level, random, (double)pos.getX(), (double)(pos.getY() + 1), (double)pos.getZ());
            if (random.nextInt(10) == 0) {
               level.playLocalSound((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), SoundEvents.NOXIOUS_GAS, SoundSource.AMBIENT, 1.0F, 1.0F, false);
            }

         }
      }
   }

   private static void spawnBubbleParticlesAt(final Level level, final RandomSource random, final double x, final double y, final double z) {
      level.addAlwaysVisibleParticle(ParticleTypes.SULFUR_BUBBLES, x + (double)random.nextFloat(), y + (double)random.nextFloat(), z + (double)random.nextFloat(), 0.0D, 0.0D, 0.0D);
   }

   protected boolean triggerEvent(final BlockState state, final Level level, final BlockPos pos, final int b0, final int b1) {
      BlockEntity var7 = level.getBlockEntity(pos);
      if (var7 instanceof PotentSulfurBlockEntity entity) {
         entity.eruptionTick = level.getGameTime();
      }

      return true;
   }

   public @Nullable BlockEntityTicker getTicker(final Level level, final BlockState blockState, final BlockEntityType type) {
      boolean client = level.isClientSide();
      BlockEntityType var10001 = BlockEntityTypes.POTENT_SULFUR;
      BlockEntityTicker var10002;
      switch (1.$SwitchMap$net$minecraft$world$level$block$state$properties$PotentSulfurState[((PotentSulfurState)blockState.getValue(STATE)).ordinal()]) {
         case 1:
            var10002 = null;
            break;
         case 2:
            var10002 = client ? PotentSulfurBlockEntity.CLIENT_NOXIOUS_GAS_TICKER : PotentSulfurBlockEntity.SERVER_NAUSEA_EFFECT_TICKER;
            break;
         case 3:
            var10002 = client ? PotentSulfurBlockEntity.CLIENT_NOXIOUS_GAS_TICKER : PotentSulfurBlockEntity.SERVER_WAITING_COUNTDOWN_TICKER.andThen(PotentSulfurBlockEntity.SERVER_NAUSEA_EFFECT_TICKER);
            break;
         case 4:
            var10002 = client ? ((BlockEntityTicker)PotentSulfurBlockEntity.CLIENT_GEYSER_PLUME_TICKER.apply(SoundEvents.GEYSER_ERUPTION_ACTIVE)).andThen(PotentSulfurBlockEntity.LAUNCH_ENTITY_TICKER) : PotentSulfurBlockEntity.LAUNCH_ENTITY_TICKER.andThen(PotentSulfurBlockEntity.SERVER_WAITING_COUNTDOWN_TICKER);
            break;
         case 5:
            var10002 = client ? ((BlockEntityTicker)PotentSulfurBlockEntity.CLIENT_GEYSER_PLUME_TICKER.apply(SoundEvents.GEYSER_CONTINUOUS_ACTIVE)).andThen(PotentSulfurBlockEntity.LAUNCH_ENTITY_TICKER) : PotentSulfurBlockEntity.LAUNCH_ENTITY_TICKER;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return createTickerHelper(type, var10001, var10002);
   }
}
