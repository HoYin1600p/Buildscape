package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CakeBlock extends Block {
   public static final int MAX_BITES = 6;
   public static final IntegerProperty BITES = BlockStateProperties.BITES;
   public static final int FULL_CAKE_SIGNAL = getOutputSignal(0);
   private static final VoxelShape[] SHAPES = Block.boxes(6, (bite) -> Block.box((double)(1 + bite * 2), 0.0D, 1.0D, 15.0D, 8.0D, 15.0D));

   protected CakeBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(BITES, Integer.valueOf(0)));
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return SHAPES[state.getValue(BITES)];
   }

   protected InteractionResult useItemOn(final ItemStack itemStack, final BlockState state, final Level level, final BlockPos pos, final Player player, final InteractionHand hand, final BlockHitResult hitResult) {
      Item item = itemStack.getItem();
      if (itemStack.is(ItemTags.CANDLES) && state.getValue(BITES) == 0) {
         Block var10 = Block.byItem(item);
         if (var10 instanceof CandleBlock) {
            CandleBlock candleBlock = (CandleBlock)var10;
            itemStack.consume(1, player);
            level.playSound((Entity)null, pos, SoundEvents.CAKE_ADD_CANDLE, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.setBlockAndUpdate(pos, CandleCakeBlock.byCandle(candleBlock));
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            player.awardStat(Stats.ITEM_USED.get(item));
            return InteractionResult.SUCCESS;
         }
      }

      return InteractionResult.TRY_WITH_EMPTY_HAND;
   }

   protected InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
      if (level.isClientSide()) {
         if (eat(level, pos, state, player).consumesAction()) {
            return InteractionResult.SUCCESS;
         }

         if (player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
            return InteractionResult.CONSUME;
         }
      }

      return eat(level, pos, state, player);
   }

   protected static InteractionResult eat(final LevelAccessor level, final BlockPos pos, final BlockState state, final Player player) {
      if (!player.canEat(false)) {
         return InteractionResult.PASS;
      } else {
         player.awardStat(Stats.EAT_CAKE_SLICE);
         player.getFoodData().eat(2, 0.1F);
         int bites = state.getValue(BITES);
         level.gameEvent(player, GameEvent.EAT, pos);
         if (bites < 6) {
            level.setBlockAndUpdate(pos, (BlockState)state.setValue(BITES, Integer.valueOf(bites + 1)));
         } else {
            level.removeBlock(pos, false);
            level.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
         }

         return InteractionResult.SUCCESS;
      }
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      return directionToNeighbour == Direction.DOWN && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
   }

   protected boolean canSurvive(final BlockState state, final LevelReader level, final BlockPos pos) {
      return level.getBlockState(pos.below()).isSolid();
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder builder) {
      builder.add(BITES);
   }

   protected int getAnalogOutputSignal(final BlockState state, final Level level, final BlockPos pos, final Direction direction) {
      return getOutputSignal(state.getValue(BITES));
   }

   public static int getOutputSignal(final int bitesTaken) {
      return (7 - bitesTaken) * 2;
   }

   protected boolean hasAnalogOutputSignal(final BlockState state) {
      return true;
   }

   protected boolean isPathfindable(final BlockState state, final PathComputationType type) {
      return false;
   }
}
