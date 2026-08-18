package net.minecraft.world.level.block;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class ButtonBlock extends FaceAttachedHorizontalDirectionalBlock {
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   private final BlockSetType type;
   private final int ticksToStayPressed;
   private final Function shapes;

   protected ButtonBlock(final BlockSetType type, final int ticksToStayPressed, final BlockBehaviour.Properties properties) {
      super(properties.sound(type.soundType()));
      this.type = type;
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(POWERED, Boolean.valueOf(false))).setValue(FACE, AttachFace.WALL));
      this.ticksToStayPressed = ticksToStayPressed;
      this.shapes = this.makeShapes();
   }

   private Function makeShapes() {
      VoxelShape pressedShaper = Block.cube(14.0D);
      VoxelShape unpressedShaper = Block.cube(12.0D);
      Map attachFace = Shapes.rotateAttachFace(Block.boxZ(6.0D, 4.0D, 8.0D, 16.0D));
      return this.getShapeForEachState((state) -> Shapes.join((VoxelShape)((Map)attachFace.get(state.getValue(FACE))).get(state.getValue(FACING)), state.getValue(POWERED) ? pressedShaper : unpressedShaper, BooleanOp.ONLY_FIRST));
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return (VoxelShape)this.shapes.apply(state);
   }

   protected InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
      if (state.getValue(POWERED)) {
         return InteractionResult.CONSUME;
      } else {
         this.press(state, level, pos, player);
         return InteractionResult.SUCCESS;
      }
   }

   protected void onExplosionHit(final BlockState state, final ServerLevel level, final BlockPos pos, final Explosion explosion, final BiConsumer onHit) {
      if (explosion.canTriggerBlocks() && !state.getValue(POWERED)) {
         this.press(state, level, pos, (Player)null);
      }

      super.onExplosionHit(state, level, pos, explosion, onHit);
   }

   public void press(final BlockState state, final Level level, final BlockPos pos, final @Nullable Player player) {
      level.setBlockAndUpdate(pos, (BlockState)state.setValue(POWERED, Boolean.valueOf(true)));
      this.updateNeighbours(state, level, pos);
      level.scheduleTick(pos, this, this.ticksToStayPressed);
      this.playSound(player, level, pos, true);
      level.gameEvent(player, GameEvent.BLOCK_ACTIVATE, pos);
   }

   protected void playSound(final @Nullable Player player, final LevelAccessor level, final BlockPos pos, final boolean pressed) {
      level.playSound(pressed ? player : null, pos, this.getSound(pressed), SoundSource.BLOCKS);
   }

   protected SoundEvent getSound(final boolean pressed) {
      return pressed ? this.type.buttonClickOn() : this.type.buttonClickOff();
   }

   protected void affectNeighborsAfterRemoval(final BlockState state, final ServerLevel level, final BlockPos pos, final boolean movedByPiston) {
      if (!movedByPiston && state.getValue(POWERED)) {
         this.updateNeighbours(state, level, pos);
      }

   }

   protected int getDirectSignal(final BlockState state, final BlockGetter level, final BlockPos pos, final Direction direction) {
      return state.getValue(POWERED) && getConnectedDirection(state) == direction ? 15 : 0;
   }

   protected boolean isSignalSource(final BlockState state) {
      return true;
   }

   protected int ownSignal(final BlockState state, final BlockGetter level, final BlockPos pos) {
      return state.getValue(POWERED) ? 15 : 0;
   }

   protected void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      if (state.getValue(POWERED)) {
         this.checkPressed(state, level, pos);
      }
   }

   protected void entityInside(final BlockState state, final Level level, final BlockPos pos, final Entity entity, final InsideBlockEffectApplier effectApplier, final boolean isPrecise) {
      if (!level.isClientSide() && this.type.canButtonBeActivatedByArrows() && !state.getValue(POWERED)) {
         this.checkPressed(state, level, pos);
      }
   }

   protected void checkPressed(final BlockState state, final Level level, final BlockPos pos) {
      AbstractArrow firstArrow = this.type.canButtonBeActivatedByArrows() ? (AbstractArrow)level.getEntitiesOfClass(AbstractArrow.class, state.getShape(level, pos).bounds().move(pos)).stream().findFirst().orElse((Object)null) : null;
      boolean shouldBePressed = firstArrow != null;
      boolean wasPressed = state.getValue(POWERED);
      if (shouldBePressed != wasPressed) {
         level.setBlockAndUpdate(pos, (BlockState)state.setValue(POWERED, Boolean.valueOf(shouldBePressed)));
         this.updateNeighbours(state, level, pos);
         this.playSound((Player)null, level, pos, shouldBePressed);
         level.gameEvent(firstArrow, shouldBePressed ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, pos);
      }

      if (shouldBePressed) {
         level.scheduleTick(pos, this, this.ticksToStayPressed);
      }

   }

   private void updateNeighbours(final BlockState state, final Level level, final BlockPos pos) {
      Direction front = getConnectedDirection(state).getOpposite();
      Orientation orientation = ExperimentalRedstoneUtils.initialOrientation(level, front, front.getAxis().isHorizontal() ? Direction.UP : (Direction)state.getValue(FACING));
      level.updateNeighborsAt(pos, this, orientation);
      level.updateNeighborsAt(pos.relative(front), this, orientation);
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder builder) {
      builder.add(FACING, POWERED, FACE);
   }
}
