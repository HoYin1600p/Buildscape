package net.minecraft.world.level.block;

import java.util.Map;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class VineBlock extends Block {
   private static final int SEARCH_RADIUS = 4;
   private static final int MAX_NEIGHBORS_TO_GROW = 4;
   public static final BooleanProperty UP = PipeBlock.UP;
   public static final BooleanProperty NORTH = PipeBlock.NORTH;
   public static final BooleanProperty EAST = PipeBlock.EAST;
   public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
   public static final BooleanProperty WEST = PipeBlock.WEST;
   public static final Map PROPERTY_BY_DIRECTION = (Map)PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter((e) -> e.getKey() != Direction.DOWN).collect(Util.toMap());
   private final Function shapes;

   public VineBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(UP, Boolean.valueOf(false))).setValue(NORTH, Boolean.valueOf(false))).setValue(EAST, Boolean.valueOf(false))).setValue(SOUTH, Boolean.valueOf(false))).setValue(WEST, Boolean.valueOf(false)));
      this.shapes = this.makeShapes();
   }

   private Function makeShapes() {
      Map shapes = Shapes.rotateAll(Block.boxZ(16.0D, 0.0D, 1.0D));
      return this.getShapeForEachState((state) -> {
         VoxelShape shape = Shapes.empty();

         for(Map.Entry entry : PROPERTY_BY_DIRECTION.entrySet()) {
            if (state.getValue((Property)entry.getValue())) {
               shape = Shapes.or(shape, (VoxelShape)shapes.get(entry.getKey()));
            }
         }

         return shape.isEmpty() ? Shapes.block() : shape;
      });
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return (VoxelShape)this.shapes.apply(state);
   }

   protected boolean propagatesSkylightDown(final BlockState state) {
      return true;
   }

   protected boolean canSurvive(final BlockState state, final LevelReader level, final BlockPos pos) {
      return this.hasFaces(this.getUpdatedState(state, level, pos));
   }

   private boolean hasFaces(final BlockState blockState) {
      return this.countFaces(blockState) > 0;
   }

   private int countFaces(final BlockState blockState) {
      int count = 0;

      for(BooleanProperty property : PROPERTY_BY_DIRECTION.values()) {
         if (blockState.getValue(property)) {
            ++count;
         }
      }

      return count;
   }

   private boolean canSupportAtFace(final BlockGetter level, final BlockPos pos, final Direction direction) {
      if (direction == Direction.DOWN) {
         return false;
      } else {
         BlockPos relative = pos.relative(direction);
         if (isAcceptableNeighbour(level, relative, direction)) {
            return true;
         } else if (direction.getAxis() == Direction.Axis.Y) {
            return false;
         } else {
            BooleanProperty property = (BooleanProperty)PROPERTY_BY_DIRECTION.get(direction);
            BlockState aboveState = level.getBlockState(pos.above());
            return aboveState.is(this) && aboveState.getValue(property);
         }
      }
   }

   public static boolean isAcceptableNeighbour(final BlockGetter level, final BlockPos neighbourPos, final Direction directionToNeighbour) {
      return MultifaceBlock.canAttachTo(level, directionToNeighbour, neighbourPos, level.getBlockState(neighbourPos));
   }

   private BlockState getUpdatedState(BlockState state, final BlockGetter level, final BlockPos pos) {
      BlockPos abovePos = pos.above();
      if (state.getValue(UP)) {
         state = (BlockState)state.setValue(UP, Boolean.valueOf(isAcceptableNeighbour(level, abovePos, Direction.DOWN)));
      }

      BlockState aboveState = null;

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         BooleanProperty property = getPropertyForFace(direction);
         if (state.getValue(property)) {
            boolean canSupport = this.canSupportAtFace(level, pos, direction);
            if (!canSupport) {
               if (aboveState == null) {
                  aboveState = level.getBlockState(abovePos);
               }

               canSupport = aboveState.is(this) && aboveState.getValue(property);
            }

            state = (BlockState)state.setValue(property, Boolean.valueOf(canSupport));
         }
      }

      return state;
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      if (directionToNeighbour == Direction.DOWN) {
         return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
      } else {
         BlockState blockState = this.getUpdatedState(state, level, pos);
         return !this.hasFaces(blockState) ? Blocks.AIR.defaultBlockState() : blockState;
      }
   }

   protected void randomTick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      if (level.getGameRules().get(GameRules.SPREAD_VINES)) {
         if (random.nextInt(4) == 0) {
            Direction testDirection = Direction.getRandom(random);
            BlockPos abovePos = pos.above();
            if (testDirection.getAxis().isHorizontal() && !state.getValue(getPropertyForFace(testDirection))) {
               if (this.canSpread(level, pos)) {
                  BlockPos testPos = pos.relative(testDirection);
                  BlockState edgeState = level.getBlockState(testPos);
                  if (edgeState.isAir()) {
                     Direction cwDirection = testDirection.getClockWise();
                     Direction ccwDirection = testDirection.getCounterClockWise();
                     boolean cwHasConnectingFace = state.getValue(getPropertyForFace(cwDirection));
                     boolean ccwHasConnectingFace = state.getValue(getPropertyForFace(ccwDirection));
                     BlockPos cwTestPos = testPos.relative(cwDirection);
                     BlockPos ccwTestPos = testPos.relative(ccwDirection);
                     if (cwHasConnectingFace && isAcceptableNeighbour(level, cwTestPos, cwDirection)) {
                        level.setBlock(testPos, (BlockState)this.defaultBlockState().setValue(getPropertyForFace(cwDirection), Boolean.valueOf(true)), 2);
                     } else if (ccwHasConnectingFace && isAcceptableNeighbour(level, ccwTestPos, ccwDirection)) {
                        level.setBlock(testPos, (BlockState)this.defaultBlockState().setValue(getPropertyForFace(ccwDirection), Boolean.valueOf(true)), 2);
                     } else {
                        Direction opposite = testDirection.getOpposite();
                        if (cwHasConnectingFace && level.isEmptyBlock(cwTestPos) && isAcceptableNeighbour(level, pos.relative(cwDirection), opposite)) {
                           level.setBlock(cwTestPos, (BlockState)this.defaultBlockState().setValue(getPropertyForFace(opposite), Boolean.valueOf(true)), 2);
                        } else if (ccwHasConnectingFace && level.isEmptyBlock(ccwTestPos) && isAcceptableNeighbour(level, pos.relative(ccwDirection), opposite)) {
                           level.setBlock(ccwTestPos, (BlockState)this.defaultBlockState().setValue(getPropertyForFace(opposite), Boolean.valueOf(true)), 2);
                        } else if ((double)random.nextFloat() < 0.05D && isAcceptableNeighbour(level, testPos.above(), Direction.UP)) {
                           level.setBlock(testPos, (BlockState)this.defaultBlockState().setValue(UP, Boolean.valueOf(true)), 2);
                        }
                     }
                  } else if (isAcceptableNeighbour(level, testPos, testDirection)) {
                     level.setBlock(pos, (BlockState)state.setValue(getPropertyForFace(testDirection), Boolean.valueOf(true)), 2);
                  }

               }
            } else {
               if (testDirection == Direction.UP && pos.getY() < level.getMaxY()) {
                  if (this.canSupportAtFace(level, pos, testDirection)) {
                     level.setBlock(pos, (BlockState)state.setValue(UP, Boolean.valueOf(true)), 2);
                     return;
                  }

                  if (level.isEmptyBlock(abovePos)) {
                     if (!this.canSpread(level, pos)) {
                        return;
                     }

                     BlockState aboveState = state;

                     for(Direction direction : Direction.Plane.HORIZONTAL) {
                        if (random.nextBoolean() || !isAcceptableNeighbour(level, abovePos.relative(direction), direction)) {
                           aboveState = (BlockState)aboveState.setValue(getPropertyForFace(direction), Boolean.valueOf(false));
                        }
                     }

                     if (this.hasHorizontalConnection(aboveState)) {
                        level.setBlock(abovePos, aboveState, 2);
                     }

                     return;
                  }
               }

               if (pos.getY() > level.getMinY()) {
                  BlockPos belowPos = pos.below();
                  BlockState belowState = level.getBlockState(belowPos);
                  if (belowState.isAir() || belowState.is(this)) {
                     BlockState before = belowState.isAir() ? this.defaultBlockState() : belowState;
                     BlockState after = this.copyRandomFaces(state, before, random);
                     if (before != after && this.hasHorizontalConnection(after)) {
                        level.setBlock(belowPos, after, 2);
                     }
                  }
               }

            }
         }
      }
   }

   private BlockState copyRandomFaces(final BlockState from, BlockState to, final RandomSource random) {
      for(Direction direction : Direction.Plane.HORIZONTAL) {
         if (random.nextBoolean()) {
            BooleanProperty propertyForFace = getPropertyForFace(direction);
            if (from.getValue(propertyForFace)) {
               to = (BlockState)to.setValue(propertyForFace, Boolean.valueOf(true));
            }
         }
      }

      return to;
   }

   private boolean hasHorizontalConnection(final BlockState state) {
      return state.getValue(NORTH) || state.getValue(EAST) || state.getValue(SOUTH) || state.getValue(WEST);
   }

   private boolean canSpread(final LevelReader level, final BlockPos pos) {
      BlockPos minPos = pos.offset(-4, -1, -4);
      BlockPos maxPos = pos.offset(4, 1, 4);
      return level.findBlocksIn(minPos, maxPos).filterState((state) -> state.is(this)).atMostMatched(4);
   }

   protected boolean canBeReplaced(final BlockState state, final BlockPlaceContext context) {
      BlockState clickedState = context.getLevel().getBlockState(context.getClickedPos());
      if (clickedState.is(this)) {
         return this.countFaces(clickedState) < PROPERTY_BY_DIRECTION.size();
      } else {
         return super.canBeReplaced(state, context);
      }
   }

   public @Nullable BlockState getStateForPlacement(final BlockPlaceContext context) {
      BlockState clickedState = context.getLevel().getBlockState(context.getClickedPos());
      boolean clickedVine = clickedState.is(this);
      BlockState result = clickedVine ? clickedState : this.defaultBlockState();

      for(Direction direction : context.getNearestLookingDirections()) {
         if (direction != Direction.DOWN) {
            BooleanProperty face = getPropertyForFace(direction);
            boolean faceOccupied = clickedVine && clickedState.getValue(face);
            if (!faceOccupied && this.canSupportAtFace(context.getLevel(), context.getClickedPos(), direction)) {
               return (BlockState)result.setValue(face, Boolean.valueOf(true));
            }
         }
      }

      return clickedVine ? result : null;
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder builder) {
      builder.add(UP, NORTH, EAST, SOUTH, WEST);
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

   public static BooleanProperty getPropertyForFace(final Direction direction) {
      return (BooleanProperty)PROPERTY_BY_DIRECTION.get(direction);
   }
}
