package net.minecraft.world.level.block;

import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TripWireBlock extends Block {
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;
   public static final BooleanProperty DISARMED = BlockStateProperties.DISARMED;
   public static final BooleanProperty NORTH = PipeBlock.NORTH;
   public static final BooleanProperty EAST = PipeBlock.EAST;
   public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
   public static final BooleanProperty WEST = PipeBlock.WEST;
   private static final Map PROPERTY_BY_DIRECTION = CrossCollisionBlock.PROPERTY_BY_DIRECTION;
   private static final VoxelShape SHAPE_ATTACHED = Block.column(16.0D, 1.0D, 2.5D);
   private static final VoxelShape SHAPE_NOT_ATTACHED = Block.column(16.0D, 0.0D, 8.0D);
   private static final int RECHECK_PERIOD = 10;
   private final Block hook;

   public TripWireBlock(final Block hook, final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(POWERED, Boolean.valueOf(false))).setValue(ATTACHED, Boolean.valueOf(false))).setValue(DISARMED, Boolean.valueOf(false))).setValue(NORTH, Boolean.valueOf(false))).setValue(EAST, Boolean.valueOf(false))).setValue(SOUTH, Boolean.valueOf(false))).setValue(WEST, Boolean.valueOf(false)));
      this.hook = hook;
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return state.getValue(ATTACHED) ? SHAPE_ATTACHED : SHAPE_NOT_ATTACHED;
   }

   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      BlockGetter level = context.getLevel();
      BlockPos pos = context.getClickedPos();
      return (BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(NORTH, Boolean.valueOf(this.shouldConnectTo(level.getBlockState(pos.north()), Direction.NORTH)))).setValue(EAST, Boolean.valueOf(this.shouldConnectTo(level.getBlockState(pos.east()), Direction.EAST)))).setValue(SOUTH, Boolean.valueOf(this.shouldConnectTo(level.getBlockState(pos.south()), Direction.SOUTH)))).setValue(WEST, Boolean.valueOf(this.shouldConnectTo(level.getBlockState(pos.west()), Direction.WEST)));
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      return directionToNeighbour.getAxis().isHorizontal() ? (BlockState)state.setValue((Property)PROPERTY_BY_DIRECTION.get(directionToNeighbour), Boolean.valueOf(this.shouldConnectTo(neighbourState, directionToNeighbour))) : super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
   }

   protected void onPlace(final BlockState state, final Level level, final BlockPos pos, final BlockState oldState, final boolean movedByPiston) {
      if (!oldState.is(state.getBlock())) {
         this.updateSource(level, pos, state);
      }
   }

   protected void affectNeighborsAfterRemoval(final BlockState state, final ServerLevel level, final BlockPos pos, final boolean movedByPiston) {
      if (!movedByPiston) {
         this.updateSource(level, pos, (BlockState)state.setValue(POWERED, Boolean.valueOf(true)));
      }

   }

   public BlockState playerWillDestroy(final Level level, final BlockPos pos, final BlockState state, final Player player) {
      if (!level.isClientSide() && !player.getMainHandItem().isEmpty() && player.getMainHandItem().is(Items.SHEARS)) {
         level.setBlock(pos, (BlockState)state.setValue(DISARMED, Boolean.valueOf(true)), 260);
         level.gameEvent(player, GameEvent.SHEAR, pos);
      }

      return super.playerWillDestroy(level, pos, state, player);
   }

   private void updateSource(final Level level, final BlockPos pos, final BlockState state) {
      for(Direction direction : new Direction[]{Direction.SOUTH, Direction.WEST}) {
         for(int i = 1; i < 42; ++i) {
            BlockPos testPos = pos.relative(direction, i);
            BlockState block = level.getBlockState(testPos);
            if (block.is(this.hook)) {
               if (block.getValue(TripWireHookBlock.FACING) == direction.getOpposite()) {
                  TripWireHookBlock.calculateState(level, testPos, block, false, true, i, state);
               }
               break;
            }

            if (!block.is(this)) {
               break;
            }
         }
      }

   }

   protected VoxelShape getEntityInsideCollisionShape(final BlockState state, final BlockGetter level, final BlockPos pos, final Entity entity) {
      return state.getShape(level, pos);
   }

   protected void entityInside(final BlockState state, final Level level, final BlockPos pos, final Entity entity, final InsideBlockEffectApplier effectApplier, final boolean isPrecise) {
      if (!level.isClientSide()) {
         if (!state.getValue(POWERED) && !level.getBlockTicks().hasScheduledTick(pos, this)) {
            this.checkPressed(level, pos, List.of(entity));
         }
      }
   }

   protected void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      if (level.getBlockState(pos).getValue(POWERED)) {
         this.checkPressed(level, pos);
      }
   }

   private void checkPressed(final Level level, final BlockPos pos) {
      BlockState state = level.getBlockState(pos);
      List entities = level.getEntities((Entity)null, state.getShape(level, pos).bounds().move(pos));
      this.checkPressed(level, pos, entities);
   }

   private void checkPressed(final Level level, final BlockPos pos, final List entities) {
      BlockState state = level.getBlockState(pos);
      boolean wasPressed = state.getValue(POWERED);
      boolean shouldBePressed = false;
      if (!entities.isEmpty()) {
         for(Entity entity : entities) {
            if (!entity.isIgnoringBlockTriggers()) {
               shouldBePressed = true;
               break;
            }
         }
      }

      if (shouldBePressed != wasPressed) {
         state = (BlockState)state.setValue(POWERED, Boolean.valueOf(shouldBePressed));
         level.setBlockAndUpdate(pos, state);
         this.updateSource(level, pos, state);
      }

      if (shouldBePressed) {
         level.scheduleTick(pos, this, 10);
      } else if (wasPressed) {
         level.scheduleTick(pos, this, 0);
      }

   }

   public boolean shouldConnectTo(final BlockState blockState, final Direction direction) {
      if (blockState.is(this.hook)) {
         return blockState.getValue(TripWireHookBlock.FACING) == direction.getOpposite();
      } else {
         return blockState.is(this);
      }
   }

   protected BlockState rotate(final BlockState state, final Rotation rotation) {
      BlockState var10000;
      switch (rotation) {
         case CLOCKWISE_180:
            var10000 = (BlockState)((BlockState)((BlockState)((BlockState)state.setValue(NORTH, (Boolean)state.getValue(SOUTH))).setValue(EAST, (Boolean)state.getValue(WEST))).setValue(SOUTH, (Boolean)state.getValue(NORTH))).setValue(WEST, (Boolean)state.getValue(EAST));
            break;
         case COUNTERCLOCKWISE_90:
            var10000 = (BlockState)((BlockState)((BlockState)((BlockState)state.setValue(NORTH, (Boolean)state.getValue(EAST))).setValue(EAST, (Boolean)state.getValue(SOUTH))).setValue(SOUTH, (Boolean)state.getValue(WEST))).setValue(WEST, (Boolean)state.getValue(NORTH));
            break;
         case CLOCKWISE_90:
            var10000 = (BlockState)((BlockState)((BlockState)((BlockState)state.setValue(NORTH, (Boolean)state.getValue(WEST))).setValue(EAST, (Boolean)state.getValue(NORTH))).setValue(SOUTH, (Boolean)state.getValue(EAST))).setValue(WEST, (Boolean)state.getValue(SOUTH));
            break;
         default:
            var10000 = state;
      }

      return var10000;
   }

   protected BlockState mirror(final BlockState state, final Mirror mirror) {
      switch (mirror) {
         case LEFT_RIGHT:
            return (BlockState)((BlockState)state.setValue(NORTH, (Boolean)state.getValue(SOUTH))).setValue(SOUTH, (Boolean)state.getValue(NORTH));
         case FRONT_BACK:
            return (BlockState)((BlockState)state.setValue(EAST, (Boolean)state.getValue(WEST))).setValue(WEST, (Boolean)state.getValue(EAST));
         default:
            return super.mirror(state, mirror);
      }
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder builder) {
      builder.add(POWERED, ATTACHED, DISARMED, NORTH, EAST, WEST, SOUTH);
   }
}
