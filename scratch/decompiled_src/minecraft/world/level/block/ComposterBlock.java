package net.minecraft.world.level.block;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Compostable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class ComposterBlock extends Block implements WorldlyContainerHolder {
   public static final int READY = 8;
   public static final int MIN_LEVEL = 0;
   public static final int MAX_LEVEL = 7;
   public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_COMPOSTER;
   private static final int HOLE_WIDTH = 12;
   private static final VoxelShape[] SHAPES = (VoxelShape[])Util.make(() -> {
      VoxelShape[] shapes = Block.boxes(8, (level) -> Shapes.join(Shapes.block(), Block.column(12.0D, (double)Math.clamp((long)(1 + level * 2), 2, 16), 16.0D), BooleanOp.ONLY_FIRST));
      shapes[8] = shapes[7];
      return shapes;
   });

   public ComposterBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(LEVEL, Integer.valueOf(0)));
   }

   public static void handleFill(final Level level, final BlockPos pos, final boolean success) {
      BlockState state = level.getBlockState(pos);
      level.playLocalSound(pos, success ? SoundEvents.COMPOSTER_FILL_SUCCESS : SoundEvents.COMPOSTER_FILL, SoundSource.BLOCKS, 1.0F, 1.0F, false);
      double centerHeight = state.getShape(level, pos).max(Direction.Axis.Y, 0.5D, 0.5D) + 0.03125D;
      double sideOffsetPixels = 2.0D;
      double sideOffset = 0.1875D;
      double width = 0.625D;
      RandomSource random = level.getRandom();

      for(int i = 0; i < 10; ++i) {
         double xa = random.nextGaussian() * 0.02D;
         double ya = random.nextGaussian() * 0.02D;
         double za = random.nextGaussian() * 0.02D;
         level.addParticle(ParticleTypes.COMPOSTER, (double)pos.getX() + 0.1875D + 0.625D * (double)random.nextFloat(), (double)pos.getY() + centerHeight + (double)random.nextFloat() * (1.0D - centerHeight), (double)pos.getZ() + 0.1875D + 0.625D * (double)random.nextFloat(), xa, ya, za);
      }

   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return SHAPES[state.getValue(LEVEL)];
   }

   protected VoxelShape getInteractionShape(final BlockState state, final BlockGetter level, final BlockPos pos) {
      return Shapes.block();
   }

   protected VoxelShape getCollisionShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return SHAPES[0];
   }

   protected void onPlace(final BlockState state, final Level level, final BlockPos pos, final BlockState oldState, final boolean movedByPiston) {
      if (state.getValue(LEVEL) == 7) {
         level.scheduleTick(pos, state.getBlock(), 20);
      }

   }

   protected InteractionResult useItemOn(final ItemStack itemStack, final BlockState state, final Level level, final BlockPos pos, final Player player, final InteractionHand hand, final BlockHitResult hitResult) {
      int fillLevel = state.getValue(LEVEL);
      Compostable compostable = (Compostable)itemStack.get(DataComponents.COMPOSTABLE);
      if (fillLevel < 8 && compostable != null) {
         if (fillLevel < 7 && level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            BlockState newState = addLayer(player, state, serverLevel, pos, compostable);
            level.levelEvent(1500, pos, state != newState ? 1 : 0);
            player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
            itemStack.consume(1, player);
         }

         return InteractionResult.SUCCESS;
      } else {
         return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
      }
   }

   protected InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
      int fillLevel = state.getValue(LEVEL);
      if (fillLevel == 8) {
         extractProduce(player, state, level, pos);
         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.PASS;
      }
   }

   public static BlockState insertItem(final Entity sourceEntity, final BlockState state, final ServerLevel level, final ItemStack itemStack, final BlockPos pos) {
      int fillLevel = state.getValue(LEVEL);
      Compostable compostable = (Compostable)itemStack.get(DataComponents.COMPOSTABLE);
      if (fillLevel < 7 && compostable != null) {
         BlockState newState = addLayer(sourceEntity, state, level, pos, compostable);
         itemStack.shrink(1);
         return newState;
      } else {
         return state;
      }
   }

   public static BlockState extractProduce(final Entity sourceEntity, final BlockState state, final Level level, final BlockPos pos) {
      if (!level.isClientSide()) {
         Vec3 itemPos = Vec3.atLowerCornerWithOffset(pos, 0.5D, 1.01D, 0.5D).offsetRandomXZ(level.getRandom(), 0.7F);
         ItemEntity entity = new ItemEntity(level, itemPos.x(), itemPos.y(), itemPos.z(), new ItemStack(Items.BONE_MEAL));
         entity.setDefaultPickUpDelay();
         level.addFreshEntity(entity);
      }

      BlockState emptyState = empty(sourceEntity, state, level, pos);
      level.playSound((Entity)null, pos, SoundEvents.COMPOSTER_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
      return emptyState;
   }

   private static BlockState empty(final @Nullable Entity sourceEntity, final BlockState state, final LevelAccessor level, final BlockPos pos) {
      BlockState newState = (BlockState)state.setValue(LEVEL, Integer.valueOf(0));
      level.setBlockAndUpdate(pos, newState);
      level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(sourceEntity, newState));
      return newState;
   }

   private static BlockState addLayer(final @Nullable Entity sourceEntity, final BlockState state, final ServerLevel level, final BlockPos pos, final Compostable compostable) {
      int fillLevel = state.getValue(LEVEL);
      LootContext lootContext = (new LootContext.Builder((new LootParams.Builder(level)).withParameter(LootContextParams.BLOCK_STATE, state).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withOptionalParameter(LootContextParams.INTERACTING_ENTITY, sourceEntity).create(LootContextParamSets.BLOCK_INTERACT))).create(Optional.empty());
      int layersToAdd = compostable.layers().getInt(lootContext, 0);
      if (layersToAdd > 0) {
         int newLevel = Mth.clamp(fillLevel + layersToAdd, 0, 7);
         BlockState newState = (BlockState)state.setValue(LEVEL, Integer.valueOf(newLevel));
         level.setBlockAndUpdate(pos, newState);
         level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(sourceEntity, newState));
         if (newLevel == 7) {
            level.scheduleTick(pos, state.getBlock(), 20);
         }

         return newState;
      } else {
         return state;
      }
   }

   protected void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      if (state.getValue(LEVEL) == 7) {
         level.setBlockAndUpdate(pos, (BlockState)state.cycle(LEVEL));
         level.playSound((Entity)null, pos, SoundEvents.COMPOSTER_READY, SoundSource.BLOCKS, 1.0F, 1.0F);
      }

   }

   protected boolean hasAnalogOutputSignal(final BlockState state) {
      return true;
   }

   protected int getAnalogOutputSignal(final BlockState state, final Level level, final BlockPos pos, final Direction direction) {
      return state.getValue(LEVEL);
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder builder) {
      builder.add(LEVEL);
   }

   protected boolean isPathfindable(final BlockState state, final PathComputationType type) {
      return false;
   }

   public WorldlyContainer getContainer(final BlockState state, final LevelAccessor level, final BlockPos pos) {
      int contentLevel = state.getValue(LEVEL);
      if (contentLevel == 8) {
         return new ComposterBlock.OutputContainer(state, level, pos, new ItemStack(Items.BONE_MEAL));
      } else {
         return (WorldlyContainer)(contentLevel < 7 ? new ComposterBlock.InputContainer(state, level, pos) : new ComposterBlock.EmptyContainer());
      }
   }

   private static class EmptyContainer extends SimpleContainer implements WorldlyContainer {
      public EmptyContainer() {
         super(0);
      }

      public int[] getSlotsForFace(final Direction direction) {
         return new int[0];
      }

      public boolean canPlaceItemThroughFace(final int slot, final ItemStack itemStack, final @Nullable Direction direction) {
         return false;
      }

      public boolean canTakeItemThroughFace(final int slot, final ItemStack itemStack, final Direction direction) {
         return false;
      }
   }

   private static class InputContainer extends SimpleContainer implements WorldlyContainer {
      private final BlockState state;
      private final LevelAccessor level;
      private final BlockPos pos;
      private boolean changed;

      public InputContainer(final BlockState state, final LevelAccessor level, final BlockPos pos) {
         super(1);
         this.state = state;
         this.level = level;
         this.pos = pos;
      }

      public int getMaxStackSize() {
         return 1;
      }

      public int[] getSlotsForFace(final Direction direction) {
         return direction == Direction.UP ? new int[]{0} : new int[0];
      }

      public boolean canPlaceItemThroughFace(final int slot, final ItemStack itemStack, final @Nullable Direction direction) {
         return !this.changed && direction == Direction.UP && itemStack.has(DataComponents.COMPOSTABLE);
      }

      public boolean canTakeItemThroughFace(final int slot, final ItemStack itemStack, final Direction direction) {
         return false;
      }

      public void setChanged() {
         ItemStack contents = this.getItem(0);
         Compostable compostable = (Compostable)contents.get(DataComponents.COMPOSTABLE);
         if (!contents.isEmpty() && compostable != null) {
            LevelAccessor newState = this.level;
            if (newState instanceof ServerLevel) {
               ServerLevel serverLevel = (ServerLevel)newState;
               this.changed = true;
               BlockState newState = ComposterBlock.addLayer((Entity)null, this.state, serverLevel, this.pos, compostable);
               this.level.levelEvent(1500, this.pos, newState != this.state ? 1 : 0);
               this.removeItemNoUpdate(0);
            }
         }

      }
   }

   private static class OutputContainer extends SimpleContainer implements WorldlyContainer {
      private final BlockState state;
      private final LevelAccessor level;
      private final BlockPos pos;
      private boolean changed;

      public OutputContainer(final BlockState state, final LevelAccessor level, final BlockPos pos, final ItemStack contents) {
         super(contents);
         this.state = state;
         this.level = level;
         this.pos = pos;
      }

      public int getMaxStackSize() {
         return 1;
      }

      public int[] getSlotsForFace(final Direction direction) {
         return direction == Direction.DOWN ? new int[]{0} : new int[0];
      }

      public boolean canPlaceItemThroughFace(final int slot, final ItemStack itemStack, final @Nullable Direction direction) {
         return false;
      }

      public boolean canTakeItemThroughFace(final int slot, final ItemStack itemStack, final Direction direction) {
         return !this.changed && direction == Direction.DOWN && itemStack.is(Items.BONE_MEAL);
      }

      public void setChanged() {
         ComposterBlock.empty((Entity)null, this.state, this.level, this.pos);
         this.changed = true;
      }
   }
}
