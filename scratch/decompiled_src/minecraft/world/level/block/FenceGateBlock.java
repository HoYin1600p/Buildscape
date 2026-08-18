package net.minecraft.world.level.block;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class FenceGateBlock extends HorizontalDirectionalBlock {
   public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final BooleanProperty IN_WALL = BlockStateProperties.IN_WALL;
   private static final Map SHAPES = Shapes.rotateHorizontalAxis(Block.cube(16.0D, 16.0D, 4.0D));
   private static final Map SHAPES_WALL = Maps.newEnumMap(Util.mapValues(SHAPES, (v) -> Shapes.join(v, Block.column(16.0D, 13.0D, 16.0D), BooleanOp.ONLY_FIRST)));
   private static final Map SHAPE_COLLISION = Shapes.rotateHorizontalAxis(Block.column(16.0D, 4.0D, 0.0D, 24.0D));
   private static final Map SHAPE_SUPPORT = Shapes.rotateHorizontalAxis(Block.column(16.0D, 4.0D, 5.0D, 24.0D));
   private static final Map SHAPE_OCCLUSION = Shapes.rotateHorizontalAxis(Shapes.or(Block.box(0.0D, 5.0D, 7.0D, 2.0D, 16.0D, 9.0D), Block.box(14.0D, 5.0D, 7.0D, 16.0D, 16.0D, 9.0D)));
   private static final Map SHAPE_OCCLUSION_WALL = Maps.newEnumMap(Util.mapValues(SHAPE_OCCLUSION, (v) -> v.move(0.0D, -0.1875D, 0.0D).optimize()));
   private final WoodType type;

   public FenceGateBlock(final WoodType type, final BlockBehaviour.Properties properties) {
      super(properties.sound(type.soundType()));
      this.type = type;
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(OPEN, Boolean.valueOf(false))).setValue(POWERED, Boolean.valueOf(false))).setValue(IN_WALL, Boolean.valueOf(false)));
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      Direction.Axis axis = ((Direction)state.getValue(FACING)).getAxis();
      return (VoxelShape)(state.getValue(IN_WALL) ? SHAPES_WALL : SHAPES).get(axis);
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      Direction.Axis axis = directionToNeighbour.getAxis();
      if (((Direction)state.getValue(FACING)).getClockWise().getAxis() != axis) {
         return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
      } else {
         boolean inWall = this.isWall(neighbourState) || this.isWall(level.getBlockState(pos.relative(directionToNeighbour.getOpposite())));
         return (BlockState)state.setValue(IN_WALL, Boolean.valueOf(inWall));
      }
   }

   protected VoxelShape getBlockSupportShape(final BlockState state, final BlockGetter level, final BlockPos pos) {
      Direction.Axis axis = ((Direction)state.getValue(FACING)).getAxis();
      return state.getValue(OPEN) ? Shapes.empty() : (VoxelShape)SHAPE_SUPPORT.get(axis);
   }

   protected VoxelShape getCollisionShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      Direction.Axis axis = ((Direction)state.getValue(FACING)).getAxis();
      return state.getValue(OPEN) ? Shapes.empty() : (VoxelShape)SHAPE_COLLISION.get(axis);
   }

   protected VoxelShape getOcclusionShape(final BlockState state) {
      Direction.Axis axis = ((Direction)state.getValue(FACING)).getAxis();
      return (VoxelShape)(state.getValue(IN_WALL) ? SHAPE_OCCLUSION_WALL : SHAPE_OCCLUSION).get(axis);
   }

   protected boolean isPathfindable(final BlockState state, final PathComputationType type) {
      boolean var10000;
      switch (type) {
         case LAND:
            var10000 = state.getValue(OPEN);
            break;
         case WATER:
            var10000 = false;
            break;
         case AIR:
            var10000 = state.getValue(OPEN);
            break;
         default:
            var10000 = false;
      }

      return var10000;
   }

   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      Level level = context.getLevel();
      BlockPos pos = context.getClickedPos();
      boolean isOpen = level.hasNeighborSignal(pos);
      Direction direction = context.getHorizontalDirection();
      Direction.Axis axis = direction.getAxis();
      boolean inWall = axis == Direction.Axis.Z && (this.isWall(level.getBlockState(pos.west())) || this.isWall(level.getBlockState(pos.east()))) || axis == Direction.Axis.X && (this.isWall(level.getBlockState(pos.north())) || this.isWall(level.getBlockState(pos.south())));
      return (BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, direction)).setValue(OPEN, Boolean.valueOf(isOpen))).setValue(POWERED, Boolean.valueOf(isOpen))).setValue(IN_WALL, Boolean.valueOf(inWall));
   }

   private boolean isWall(final BlockState state) {
      return state.is(BlockTags.WALLS);
   }

   protected InteractionResult useWithoutItem(BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
      if (state.getValue(OPEN)) {
         state = (BlockState)state.setValue(OPEN, Boolean.valueOf(false));
         level.setBlock(pos, state, 10);
      } else {
         Direction direction = player.getDirection();
         if (state.getValue(FACING) == direction.getOpposite()) {
            state = (BlockState)state.setValue(FACING, direction);
         }

         state = (BlockState)state.setValue(OPEN, Boolean.valueOf(true));
         level.setBlock(pos, state, 10);
      }

      boolean opens = state.getValue(OPEN);
      level.playSound(player, pos, opens ? this.type.fenceGateOpen() : this.type.fenceGateClose(), SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
      level.gameEvent(player, opens ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
      return InteractionResult.SUCCESS;
   }

   protected void onExplosionHit(final BlockState state, final ServerLevel level, final BlockPos pos, final Explosion explosion, final BiConsumer onHit) {
      if (explosion.canTriggerBlocks() && !state.getValue(POWERED)) {
         boolean open = state.getValue(OPEN);
         level.setBlockAndUpdate(pos, (BlockState)state.setValue(OPEN, Boolean.valueOf(!open)));
         level.playSound((Entity)null, pos, open ? this.type.fenceGateClose() : this.type.fenceGateOpen(), SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
         level.gameEvent(open ? GameEvent.BLOCK_CLOSE : GameEvent.BLOCK_OPEN, pos, GameEvent.Context.of(state));
      }

      super.onExplosionHit(state, level, pos, explosion, onHit);
   }

   protected void neighborChanged(final BlockState state, final Level level, final BlockPos pos, final Block block, final @Nullable Orientation orientation, final boolean movedByPiston) {
      if (!level.isClientSide()) {
         boolean hasPower = level.hasNeighborSignal(pos);
         if (state.getValue(POWERED) != hasPower) {
            level.setBlock(pos, (BlockState)((BlockState)state.setValue(POWERED, Boolean.valueOf(hasPower))).setValue(OPEN, Boolean.valueOf(hasPower)), 2);
            if (state.getValue(OPEN) != hasPower) {
               level.playSound((Entity)null, pos, hasPower ? this.type.fenceGateOpen() : this.type.fenceGateClose(), SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
               level.gameEvent((Entity)null, hasPower ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
            }
         }

      }
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder builder) {
      builder.add(FACING, OPEN, POWERED, IN_WALL);
   }

   public static boolean connectsToDirection(final BlockState state, final Direction direction) {
      return ((Direction)state.getValue(FACING)).getAxis() == direction.getClockWise().getAxis();
   }
}
