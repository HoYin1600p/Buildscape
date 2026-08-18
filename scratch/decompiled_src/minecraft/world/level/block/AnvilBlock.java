package net.minecraft.world.level.block;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class AnvilBlock extends FallingBlock {
   public static final EnumProperty FACING = HorizontalDirectionalBlock.FACING;
   private static final Map SHAPES = Shapes.rotateHorizontalAxis(Shapes.or(Block.column(12.0D, 0.0D, 4.0D), Block.column(8.0D, 10.0D, 4.0D, 5.0D), Block.column(4.0D, 8.0D, 5.0D, 10.0D), Block.column(10.0D, 16.0D, 10.0D, 16.0D)));
   private static final Component CONTAINER_TITLE = Component.translatable("container.repair");
   private static final float FALL_DAMAGE_PER_DISTANCE = 2.0F;
   private static final int FALL_DAMAGE_MAX = 40;

   public AnvilBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
   }

   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getClockWise());
   }

   protected InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
      if (!level.isClientSide()) {
         player.openMenu(state.getMenuProvider(level, pos));
         player.awardStat(Stats.INTERACT_WITH_ANVIL);
      }

      return InteractionResult.SUCCESS;
   }

   protected @Nullable MenuProvider getMenuProvider(final BlockState state, final Level level, final BlockPos pos) {
      return new SimpleMenuProvider((containerId, inventory, player) -> new AnvilMenu(containerId, inventory, ContainerLevelAccess.create(level, pos)), CONTAINER_TITLE);
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return (VoxelShape)SHAPES.get(((Direction)state.getValue(FACING)).getAxis());
   }

   protected void falling(final FallingBlockEntity entity) {
      entity.setHurtsEntities(2.0F, 40);
   }

   public void onLand(final Level level, final BlockPos pos, final BlockState state, final BlockState replacedBlock, final FallingBlockEntity entity) {
      if (!entity.isSilent()) {
         level.levelEvent(1031, pos, 0);
      }

   }

   public void onBrokenAfterFall(final Level level, final BlockPos pos, final FallingBlockEntity entity) {
      if (!entity.isSilent()) {
         level.levelEvent(1029, pos, 0);
      }

   }

   public DamageSource getFallDamageSource(final Entity entity) {
      return entity.damageSources().anvil(entity);
   }

   public static @Nullable BlockState damage(final BlockState blockState) {
      if (blockState.is(Blocks.ANVIL)) {
         return (BlockState)Blocks.CHIPPED_ANVIL.defaultBlockState().setValue(FACING, (Direction)blockState.getValue(FACING));
      } else {
         return blockState.is(Blocks.CHIPPED_ANVIL) ? (BlockState)Blocks.DAMAGED_ANVIL.defaultBlockState().setValue(FACING, (Direction)blockState.getValue(FACING)) : null;
      }
   }

   protected BlockState rotate(final BlockState state, final Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder builder) {
      builder.add(FACING);
   }

   protected boolean isPathfindable(final BlockState state, final PathComputationType type) {
      return false;
   }

   public int getDustColor(final BlockState blockState, final BlockGetter level, final BlockPos pos) {
      return blockState.getMapColor(level, pos).col;
   }
}
