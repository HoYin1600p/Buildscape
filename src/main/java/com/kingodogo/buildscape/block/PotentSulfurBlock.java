package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;
import com.kingodogo.buildscape.sound.ModSounds;
import com.kingodogo.buildscape.particle.ModParticles;

import java.util.Random;

public class PotentSulfurBlock extends BaseEntityBlock {
   public static final EnumProperty<PotentSulfurState> STATE = EnumProperty.create("state", PotentSulfurState.class);

   public static final TagKey<Block> CAUSES_CONTINUOUS_GEYSER_ERUPTIONS = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("minecraft", "causes_continuous_geyser_eruptions"));
   public static final TagKey<Block> CAUSES_PERIODIC_GEYSER_ERUPTIONS = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("minecraft", "causes_periodic_geyser_eruptions"));

   public PotentSulfurBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState(this.defaultBlockState().setValue(STATE, PotentSulfurState.DRY));
   }

   @Override
   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(STATE);
   }

   @Nullable
   @Override
   public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
      return new PotentSulfurBlockEntity(pos, state);
   }

   @Override
   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   @Override
   public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
      return validBlockState(state, level, pos);
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      return validBlockState(this.defaultBlockState(), context.getLevel(), context.getClickedPos());
   }

   private static BlockState validBlockState(final BlockState state, final LevelReader level, final BlockPos pos) {
      if (!level.getFluidState(pos.above()).isSourceOfType(Fluids.WATER)) {
         return state.setValue(STATE, PotentSulfurState.DRY);
      } else {
         BlockState belowState = level.getBlockState(pos.below());
         if (belowState.is(CAUSES_CONTINUOUS_GEYSER_ERUPTIONS) && isSourceIfFluid(belowState)) {
            return state.setValue(STATE, PotentSulfurState.CONTINUOUS);
         } else if (belowState.is(CAUSES_PERIODIC_GEYSER_ERUPTIONS) && isSourceIfFluid(belowState)) {
            boolean isGeyser = state.getValue(STATE) == PotentSulfurState.ERUPTING || state.getValue(STATE) == PotentSulfurState.DORMANT;
            if (!isGeyser) {
               BlockEntity var6 = level.getBlockEntity(pos);
               if (var6 instanceof PotentSulfurBlockEntity potentSulfurEntity) {
                  potentSulfurEntity.resetCountdown();
               }
            }

            return state.getValue(STATE) == PotentSulfurState.ERUPTING ? state : state.setValue(STATE, PotentSulfurState.DORMANT);
         } else {
            return state.setValue(STATE, PotentSulfurState.WET);
         }
      }
   }

   private static boolean isSourceIfFluid(final BlockState belowState) {
      FluidState fluidState = belowState.getFluidState();
      return fluidState.isEmpty() || fluidState.isSource();
   }

   @Override
   public void onPlace(final BlockState state, final Level level, final BlockPos pos, final BlockState oldState, final boolean movedByPiston) {
      super.onPlace(state, level, pos, oldState, movedByPiston);
      if (state.getValue(STATE) == PotentSulfurState.ERUPTING || state.getValue(STATE) == PotentSulfurState.CONTINUOUS) {
         level.blockEvent(pos, this, 0, 0);
         level.playSound(null, pos, state.getValue(STATE) == PotentSulfurState.CONTINUOUS ? ModSounds.GEYSER_CONTINUOUS_START.get() : ModSounds.GEYSER_ERUPTION_START.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
      }
   }

   @Override
   public void animateTick(final BlockState state, final Level level, final BlockPos pos, final Random random) {
      if (state.getValue(STATE) != PotentSulfurState.DRY) {
         if (level.getFluidState(pos.above()).isSourceOfType(Fluids.WATER)) {
            spawnBubbleParticlesAt(level, random, pos.getX(), pos.getY() + 1, pos.getZ());
            spawnBubbleParticlesAt(level, random, pos.getX(), pos.getY() + 1, pos.getZ());
            if (random.nextInt(10) == 0) {
               level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), ModSounds.NOXIOUS_GAS.get(), SoundSource.AMBIENT, 1.0F, 1.0F, false);
            }
         }
      }
   }

   private static void spawnBubbleParticlesAt(final Level level, final Random random, final double x, final double y, final double z) {
      level.addAlwaysVisibleParticle(ModParticles.SULFUR_BUBBLES.get(), x + (double)random.nextFloat(), y + (double)random.nextFloat(), z + (double)random.nextFloat(), 0.0D, 0.0D, 0.0D);
   }

   @Override
   public boolean triggerEvent(final BlockState state, final Level level, final BlockPos pos, final int b0, final int b1) {
      BlockEntity var7 = level.getBlockEntity(pos);
      if (var7 instanceof PotentSulfurBlockEntity entity) {
         entity.eruptionTick = level.getGameTime();
      }
      return true;
   }

   @Nullable
   @Override
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(final Level level, final BlockState blockState, final BlockEntityType<T> type) {
      boolean client = level.isClientSide();
      BlockEntityTicker<PotentSulfurBlockEntity> ticker;
      switch (blockState.getValue(STATE)) {
         case DRY:
            ticker = null;
            break;
         case WET:
            ticker = client ? PotentSulfurBlockEntity.CLIENT_NOXIOUS_GAS_TICKER : PotentSulfurBlockEntity.SERVER_NAUSEA_EFFECT_TICKER;
            break;
         case DORMANT:
            ticker = client ? PotentSulfurBlockEntity.CLIENT_NOXIOUS_GAS_TICKER : (l, p, s, e) -> {
               PotentSulfurBlockEntity.SERVER_WAITING_COUNTDOWN_TICKER.tick(l, p, s, e);
               PotentSulfurBlockEntity.SERVER_NAUSEA_EFFECT_TICKER.tick(l, p, s, e);
            };
            break;
         case ERUPTING:
            ticker = client ? (l, p, s, e) -> {
               PotentSulfurBlockEntity.CLIENT_GEYSER_PLUME_TICKER_ERUPTION.tick(l, p, s, e);
               PotentSulfurBlockEntity.LAUNCH_ENTITY_TICKER.tick(l, p, s, e);
            } : (l, p, s, e) -> {
               PotentSulfurBlockEntity.LAUNCH_ENTITY_TICKER.tick(l, p, s, e);
               PotentSulfurBlockEntity.SERVER_WAITING_COUNTDOWN_TICKER.tick(l, p, s, e);
            };
            break;
         case CONTINUOUS:
            ticker = client ? (l, p, s, e) -> {
               PotentSulfurBlockEntity.CLIENT_GEYSER_PLUME_TICKER_CONTINUOUS.tick(l, p, s, e);
               PotentSulfurBlockEntity.LAUNCH_ENTITY_TICKER.tick(l, p, s, e);
            } : PotentSulfurBlockEntity.LAUNCH_ENTITY_TICKER;
            break;
         default:
            ticker = null;
      }
      return createTickerHelper(type, ModBlockEntities.POTENT_SULFUR.get(), ticker);
   }
}
