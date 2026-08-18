package net.minecraft.world.level.block;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.IdMapper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class Block extends BlockBehaviour implements ItemLike {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Holder.Reference builtInRegistryHolder = BuiltInRegistries.BLOCK.createIntrusiveHolder(this);
   public static final IdMapper BLOCK_STATE_REGISTRY = new IdMapper();
   private static final LoadingCache SHAPE_FULL_BLOCK_CACHE = CacheBuilder.newBuilder().maximumSize(512L).weakKeys().build(new CacheLoader() {
      public Boolean load(final VoxelShape shape) {
         return !Shapes.joinIsNotEmpty(Shapes.block(), shape, BooleanOp.NOT_SAME);
      }
   });
   public static final int UPDATE_NEIGHBORS = 1;
   public static final int UPDATE_CLIENTS = 2;
   public static final int UPDATE_INVISIBLE = 4;
   public static final int UPDATE_IMMEDIATE = 8;
   public static final int UPDATE_KNOWN_SHAPE = 16;
   public static final int UPDATE_SUPPRESS_DROPS = 32;
   public static final int UPDATE_MOVE_BY_PISTON = 64;
   public static final int UPDATE_SKIP_SHAPE_UPDATE_ON_WIRE = 128;
   public static final int UPDATE_SKIP_BLOCK_ENTITY_SIDEEFFECTS = 256;
   public static final int UPDATE_SKIP_ON_PLACE = 512;
   public static final @Block.UpdateFlags int UPDATE_NONE = 260;
   public static final @Block.UpdateFlags int UPDATE_ALL = 3;
   public static final @Block.UpdateFlags int UPDATE_ALL_IMMEDIATE = 11;
   public static final @Block.UpdateFlags int UPDATE_SKIP_ALL_SIDEEFFECTS = 816;
   public static final float INDESTRUCTIBLE = -1.0F;
   public static final float INSTANT = 0.0F;
   public static final int UPDATE_LIMIT = 512;
   protected final StateDefinition stateDefinition;
   private BlockState defaultBlockState;
   private @Nullable Item item;
   private static final int CACHE_SIZE = 256;
   private static final ThreadLocal OCCLUSION_CACHE = ThreadLocal.withInitial(() -> {
      Object2ByteLinkedOpenHashMap map = new Object2ByteLinkedOpenHashMap(256, 0.25F) {
         protected void rehash(final int newN) {
         }
      };
      map.defaultReturnValue((byte)127);
      return map;
   });

   public static int getId(final @Nullable BlockState blockState) {
      if (blockState == null) {
         return 0;
      } else {
         int id = BLOCK_STATE_REGISTRY.getId(blockState);
         return id == -1 ? 0 : id;
      }
   }

   public static BlockState stateById(final int idWithData) {
      BlockState state = (BlockState)BLOCK_STATE_REGISTRY.byId(idWithData);
      return state == null ? Blocks.AIR.defaultBlockState() : state;
   }

   public static Block byItem(final @Nullable Item item) {
      if (item instanceof BlockItem blockItem) {
         return blockItem.getBlock();
      } else {
         return Blocks.AIR;
      }
   }

   public static BlockState pushEntitiesUp(final BlockState state, final BlockState newState, final LevelAccessor level, final BlockPos pos) {
      VoxelShape offsetShape = Shapes.joinUnoptimized(state.getCollisionShape(level, pos), newState.getCollisionShape(level, pos), BooleanOp.ONLY_SECOND).move(pos);
      if (offsetShape.isEmpty()) {
         return newState;
      } else {
         for(Entity collidingEntity : level.getEntities((Entity)null, offsetShape.bounds())) {
            double offset = Shapes.collide(Direction.Axis.Y, collidingEntity.getBoundingBox().move(0.0D, 1.0D, 0.0D), List.of(offsetShape), -1.0D);
            collidingEntity.teleportRelative(0.0D, 1.0D + offset, 0.0D);
         }

         return newState;
      }
   }

   public static VoxelShape box(final double minX, final double minY, final double minZ, final double maxX, final double maxY, final double maxZ) {
      return Shapes.box(minX / 16.0D, minY / 16.0D, minZ / 16.0D, maxX / 16.0D, maxY / 16.0D, maxZ / 16.0D);
   }

   public static VoxelShape[] boxes(final int endInclusive, final IntFunction voxelShapeFactory) {
      return (VoxelShape[])IntStream.rangeClosed(0, endInclusive).mapToObj(voxelShapeFactory).toArray((x$0) -> new VoxelShape[x$0]);
   }

   public static VoxelShape cube(final double size) {
      return cube(size, size, size);
   }

   public static VoxelShape cube(final double sizeX, final double sizeY, final double sizeZ) {
      double halfY = sizeY / 2.0D;
      return column(sizeX, sizeZ, 8.0D - halfY, 8.0D + halfY);
   }

   public static VoxelShape column(final double sizeXZ, final double minY, final double maxY) {
      return column(sizeXZ, sizeXZ, minY, maxY);
   }

   public static VoxelShape column(final double sizeX, final double sizeZ, final double minY, final double maxY) {
      double halfX = sizeX / 2.0D;
      double halfZ = sizeZ / 2.0D;
      return box(8.0D - halfX, minY, 8.0D - halfZ, 8.0D + halfX, maxY, 8.0D + halfZ);
   }

   public static VoxelShape boxZ(final double sizeXY, final double minZ, final double maxZ) {
      return boxZ(sizeXY, sizeXY, minZ, maxZ);
   }

   public static VoxelShape boxZ(final double sizeX, final double sizeY, final double minZ, final double maxZ) {
      double halfY = sizeY / 2.0D;
      return boxZ(sizeX, 8.0D - halfY, 8.0D + halfY, minZ, maxZ);
   }

   public static VoxelShape boxZ(final double sizeX, final double minY, final double maxY, final double minZ, final double maxZ) {
      double halfX = sizeX / 2.0D;
      return box(8.0D - halfX, minY, minZ, 8.0D + halfX, maxY, maxZ);
   }

   public static BlockState updateFromNeighbourShapes(final BlockState state, final LevelAccessor level, final BlockPos pos) {
      BlockState newState = state;
      BlockPos.MutableBlockPos neighbourPos = new BlockPos.MutableBlockPos();

      for(Direction direction : UPDATE_SHAPE_ORDER) {
         neighbourPos.setWithOffset(pos, direction);
         newState = newState.updateShape(level, level, pos, direction, neighbourPos, level.getBlockState(neighbourPos), level.getRandom());
      }

      return newState;
   }

   public static void updateOrDestroy(final BlockState blockState, final BlockState newState, final LevelAccessor level, final BlockPos blockPos, final @Block.UpdateFlags int updateFlags) {
      updateOrDestroy(blockState, newState, level, blockPos, updateFlags, 512);
   }

   public static void updateOrDestroy(final BlockState blockState, final BlockState newState, final LevelAccessor level, final BlockPos blockPos, final @Block.UpdateFlags int updateFlags, final int updateLimit) {
      if (newState != blockState) {
         if (newState.isAir()) {
            if (!level.isClientSide()) {
               level.destroyBlock(blockPos, (updateFlags & 32) == 0, (Entity)null, updateLimit);
            }
         } else {
            level.setBlock(blockPos, newState, updateFlags & -33, updateLimit);
         }
      }

   }

   public Block(final BlockBehaviour.Properties properties) {
      super(properties);
      StateDefinition.Builder builder = new StateDefinition.Builder(this);
      this.createBlockStateDefinition(builder);
      this.stateDefinition = builder.create(Block::defaultBlockState, BlockState::new);
      this.registerDefaultState((BlockState)this.stateDefinition.any());
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         String className = this.getClass().getSimpleName();
         if (!className.endsWith("Block")) {
            LOGGER.error("Block classes should end with Block and {} doesn't.", className);
         }
      }

   }

   public static boolean isExceptionForConnection(final BlockState state) {
      return state.getBlock() instanceof LeavesBlock || state.is(Blocks.BARRIER) || state.is(Blocks.CARVED_PUMPKIN) || state.is(Blocks.JACK_O_LANTERN) || state.is(Blocks.MELON) || state.is(Blocks.PUMPKIN) || state.is(BlockTags.SHULKER_BOXES);
   }

   public static boolean dropFromBlockInteractLootTable(final ServerLevel level, final ResourceKey key, final BlockPos interactedBlockPos, final BlockState interactedBlockState, final @Nullable BlockEntity interactedBlockEntity, final @Nullable ItemInstance tool, final @Nullable Entity interactingEntity, final BiConsumer consumer) {
      return dropFromLootTable(level, key, (params) -> params.withParameter(LootContextParams.BLOCK_STATE, interactedBlockState).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(interactedBlockPos)).withOptionalParameter(LootContextParams.BLOCK_ENTITY, interactedBlockEntity).withOptionalParameter(LootContextParams.INTERACTING_ENTITY, interactingEntity).withOptionalParameter(LootContextParams.TOOL, tool).create(LootContextParamSets.BLOCK_INTERACT), consumer);
   }

   protected static boolean dropFromLootTable(final ServerLevel level, final ResourceKey key, final Function paramsBuilder, final BiConsumer consumer) {
      LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(key);
      LootParams params = (LootParams)paramsBuilder.apply(new LootParams.Builder(level));
      List drops = lootTable.getRandomItems(params);
      if (!drops.isEmpty()) {
         drops.forEach((stack) -> consumer.accept(level, stack));
         return true;
      } else {
         return false;
      }
   }

   public static boolean shouldRenderFace(final BlockState state, final BlockState neighborState, final Direction direction) {
      VoxelShape occluder = neighborState.getFaceOcclusionShape(direction.getOpposite());
      if (occluder == Shapes.block()) {
         return false;
      } else if (state.skipRendering(neighborState, direction)) {
         return false;
      } else if (occluder == Shapes.empty()) {
         return true;
      } else {
         VoxelShape shape = state.getFaceOcclusionShape(direction);
         if (shape == Shapes.empty()) {
            return true;
         } else {
            Block.ShapePairKey key = new Block.ShapePairKey(shape, occluder);
            Object2ByteLinkedOpenHashMap cache = (Object2ByteLinkedOpenHashMap)OCCLUSION_CACHE.get();
            byte cached = cache.getAndMoveToFirst(key);
            if (cached != 127) {
               return cached != 0;
            } else {
               boolean result = Shapes.joinIsNotEmpty(shape, occluder, BooleanOp.ONLY_FIRST);
               if (cache.size() == 256) {
                  cache.removeLastByte();
               }

               cache.putAndMoveToFirst(key, (byte)(result ? 1 : 0));
               return result;
            }
         }
      }
   }

   public static boolean canSupportRigidBlock(final BlockGetter level, final BlockPos below) {
      return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP, SupportType.RIGID);
   }

   public static boolean canSupportCenter(final LevelReader level, final BlockPos belowPos, final Direction direction) {
      BlockState state = level.getBlockState(belowPos);
      return direction == Direction.DOWN && state.is(BlockTags.UNSTABLE_BOTTOM_CENTER) ? false : state.isFaceSturdy(level, belowPos, direction, SupportType.CENTER);
   }

   public static boolean isFaceFull(final VoxelShape shape, final Direction direction) {
      VoxelShape faceShape = shape.getFaceShape(direction);
      return isShapeFullBlock(faceShape);
   }

   public static boolean isShapeFullBlock(final VoxelShape shape) {
      return SHAPE_FULL_BLOCK_CACHE.getUnchecked(shape);
   }

   public void animateTick(final BlockState state, final Level level, final BlockPos pos, final RandomSource random) {
   }

   public void destroy(final LevelAccessor level, final BlockPos pos, final BlockState state) {
   }

   public static List getDrops(final BlockState state, final ServerLevel level, final BlockPos pos, final @Nullable BlockEntity blockEntity) {
      LootParams.Builder params = (new LootParams.Builder(level)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity);
      return state.getDrops(params);
   }

   public static List getDrops(final BlockState state, final ServerLevel level, final BlockPos pos, final @Nullable BlockEntity blockEntity, final @Nullable Entity breaker, final ItemInstance tool) {
      LootParams.Builder params = (new LootParams.Builder(level)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withParameter(LootContextParams.TOOL, tool).withOptionalParameter(LootContextParams.THIS_ENTITY, breaker).withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity);
      return state.getDrops(params);
   }

   public static void dropResources(final BlockState state, final Level level, final BlockPos pos) {
      if (level instanceof ServerLevel serverLevel) {
         getDrops(state, serverLevel, pos, (BlockEntity)null).forEach((stack) -> popResource(level, pos, stack));
         state.spawnAfterBreak(serverLevel, pos, ItemStack.EMPTY, true);
      }

   }

   public static void dropResources(final BlockState state, final LevelAccessor level, final BlockPos pos, final @Nullable BlockEntity blockEntity) {
      if (level instanceof ServerLevel serverLevel) {
         getDrops(state, serverLevel, pos, blockEntity).forEach((stack) -> popResource(serverLevel, pos, stack));
         state.spawnAfterBreak(serverLevel, pos, ItemStack.EMPTY, true);
      }

   }

   public static void dropResources(final BlockState state, final Level level, final BlockPos pos, final @Nullable BlockEntity blockEntity, final @Nullable Entity breaker, final ItemStack tool) {
      if (level instanceof ServerLevel serverLevel) {
         getDrops(state, serverLevel, pos, blockEntity, breaker, tool).forEach((stack) -> popResource(level, pos, stack));
         state.spawnAfterBreak(serverLevel, pos, tool, true);
      }

   }

   public static void popResource(final Level level, final BlockPos pos, final ItemStack itemStack) {
      double halfHeight = (double)EntityTypes.ITEM.getHeight() / 2.0D;
      RandomSource random = level.getRandom();
      double x = (double)pos.getX() + 0.5D + Mth.nextDouble(random, -0.25D, 0.25D);
      double y = (double)pos.getY() + 0.5D + Mth.nextDouble(random, -0.25D, 0.25D) - halfHeight;
      double z = (double)pos.getZ() + 0.5D + Mth.nextDouble(random, -0.25D, 0.25D);
      popResource(level, () -> new ItemEntity(level, x, y, z, itemStack), itemStack);
   }

   public static void popResourceFromFace(final Level level, final BlockPos pos, final Direction face, final ItemStack itemStack) {
      int stepX = face.getStepX();
      int stepY = face.getStepY();
      int stepZ = face.getStepZ();
      double halfWidth = (double)EntityTypes.ITEM.getWidth() / 2.0D;
      double halfHeight = (double)EntityTypes.ITEM.getHeight() / 2.0D;
      RandomSource random = level.getRandom();
      double x = (double)pos.getX() + 0.5D + (stepX == 0 ? Mth.nextDouble(random, -0.25D, 0.25D) : (double)stepX * (0.5D + halfWidth));
      double y = (double)pos.getY() + 0.5D + (stepY == 0 ? Mth.nextDouble(random, -0.25D, 0.25D) : (double)stepY * (0.5D + halfHeight)) - halfHeight;
      double z = (double)pos.getZ() + 0.5D + (stepZ == 0 ? Mth.nextDouble(random, -0.25D, 0.25D) : (double)stepZ * (0.5D + halfWidth));
      double deltaX = stepX == 0 ? Mth.nextDouble(random, -0.1D, 0.1D) : (double)stepX * 0.1D;
      double deltaY = stepY == 0 ? Mth.nextDouble(random, 0.0D, 0.1D) : (double)stepY * 0.1D + 0.1D;
      double deltaZ = stepZ == 0 ? Mth.nextDouble(random, -0.1D, 0.1D) : (double)stepZ * 0.1D;
      popResource(level, () -> new ItemEntity(level, x, y, z, itemStack, deltaX, deltaY, deltaZ), itemStack);
   }

   private static void popResource(final Level level, final Supplier entityFactory, final ItemStack itemStack) {
      if (level instanceof ServerLevel serverLevel) {
         if (!itemStack.isEmpty() && serverLevel.getGameRules().get(GameRules.BLOCK_DROPS)) {
            ItemEntity entity = (ItemEntity)entityFactory.get();
            entity.setDefaultPickUpDelay();
            level.addFreshEntity(entity);
            return;
         }
      }

   }

   protected void popExperience(final ServerLevel level, final BlockPos pos, final int amount) {
      if (level.getGameRules().get(GameRules.BLOCK_DROPS)) {
         ExperienceOrb.award(level, Vec3.atCenterOf(pos), amount);
      }

   }

   public float getExplosionResistance() {
      return this.explosionResistance;
   }

   public void wasExploded(final ServerLevel level, final BlockPos pos, final Explosion explosion) {
   }

   public void stepOn(final Level level, final BlockPos pos, final BlockState onState, final Entity entity) {
   }

   public @Nullable BlockState getStateForPlacement(final BlockPlaceContext context) {
      return this.defaultBlockState();
   }

   public void playerDestroy(final Level level, final Player player, final BlockPos pos, final BlockState state, final @Nullable BlockEntity blockEntity, final ItemStack destroyedWith) {
      player.awardStat(Stats.BLOCK_MINED.get(this));
      player.causeFoodExhaustion(0.005F);
      dropResources(state, level, pos, blockEntity, player, destroyedWith);
   }

   public void setPlacedBy(final Level level, final BlockPos pos, final BlockState state, final @Nullable LivingEntity by, final ItemStack itemStack) {
   }

   public boolean isPossibleToRespawnInThis(final BlockState state) {
      return !state.isSolid() && !state.liquid();
   }

   public MutableComponent getName() {
      return Component.translatable(this.getDescriptionId());
   }

   public void fallOn(final Level level, final BlockState state, final BlockPos pos, final Entity entity, final double fallDistance) {
      double reducedFallDistance = fallDistance * (double)(1.0F - this.getFallDistanceReduction());
      entity.causeFallDamage(reducedFallDistance, 1.0F, entity.damageSources().fall());
   }

   public void bounceOn(final Level level, final BlockState state, final BlockPos pos, final Entity entity, final double fallDistance) {
   }

   public float getBounceRestitution() {
      return this.bounceRestitution;
   }

   public float getFallDistanceReduction() {
      return this.fallDistanceReduction;
   }

   public float getFriction() {
      return this.friction;
   }

   public float getSpeedFactor() {
      return this.speedFactor;
   }

   public float getJumpFactor() {
      return this.jumpFactor;
   }

   public void spawnDestroyByEntityParticles(final Level level, final @Nullable Entity entity, final BlockPos pos, final BlockState state) {
      level.levelEvent(entity, 2001, pos, getId(state));
   }

   public void spawnDestroyParticles(final Level level, final BlockPos pos, final BlockState state) {
      this.spawnDestroyByEntityParticles(level, (Entity)null, pos, state);
   }

   public BlockState playerWillDestroy(final Level level, final BlockPos pos, final BlockState state, final Player player) {
      this.spawnDestroyByEntityParticles(level, player, pos, state);
      if (state.is(BlockTags.GUARDED_BY_PIGLINS) && level instanceof ServerLevel serverLevel) {
         PiglinAi.angerNearbyPiglins(serverLevel, player, false);
      }

      level.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(player, state));
      return state;
   }

   public void handlePrecipitation(final BlockState state, final Level level, final BlockPos pos, final Biome.Precipitation precipitation) {
   }

   public boolean dropFromExplosion(final Explosion explosion) {
      return true;
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder builder) {
   }

   public StateDefinition getStateDefinition() {
      return this.stateDefinition;
   }

   protected final void registerDefaultState(final BlockState state) {
      this.defaultBlockState = state;
   }

   public final BlockState defaultBlockState() {
      return this.defaultBlockState;
   }

   public final BlockState withPropertiesOf(final BlockState source) {
      BlockState result = this.defaultBlockState();

      for(Property property : source.getBlock().getStateDefinition().getProperties()) {
         result = BlockBehaviour.BlockStateBase.copyProperty(source, result, property);
      }

      return result;
   }

   public Item asItem() {
      if (this.item == null) {
         this.item = Item.byBlock(this);
      }

      return this.item;
   }

   public boolean hasDynamicShape() {
      return this.dynamicShape;
   }

   public String toString() {
      return "Block{" + BuiltInRegistries.BLOCK.wrapAsHolder(this).getRegisteredName() + "}";
   }

   protected Block asBlock() {
      return this;
   }

   protected Function getShapeForEachState(final Function shapeCalculator) {
      return ((ImmutableMap)this.stateDefinition.getPossibleStates().stream().collect(ImmutableMap.toImmutableMap(Function.identity(), shapeCalculator)))::get;
   }

   protected Function getShapeForEachState(final Function shapeCalculator, final Property... ignoredProperties) {
      Map defaults = (Map)Arrays.stream(ignoredProperties).collect(Collectors.toMap((k) -> k, (k) -> k.getPossibleValues().getFirst()));
      ImmutableMap map = (ImmutableMap)this.stateDefinition.getPossibleStates().stream().filter((state) -> defaults.entrySet().stream().allMatch((entry) -> state.getValue((Property)entry.getKey()) == entry.getValue())).collect(ImmutableMap.toImmutableMap(Function.identity(), shapeCalculator));
      return (blockState) -> {
         for(Map.Entry entry : defaults.entrySet()) {
            blockState = (BlockState)setValueHelper(blockState, (Property)entry.getKey(), entry.getValue());
         }

         return (VoxelShape)map.get(blockState);
      };
   }

   private static StateHolder setValueHelper(final StateHolder state, final Property property, final Object value) {
      return (StateHolder)state.setValue(property, (Comparable)value);
   }

   /** @deprecated */
   @Deprecated
   public Holder.Reference builtInRegistryHolder() {
      return this.builtInRegistryHolder;
   }

   protected void tryDropExperience(final ServerLevel level, final BlockPos pos, final ItemStack tool, final IntProvider xpRange) {
      int experience = EnchantmentHelper.processBlockExperience(level, tool, xpRange.sample(level.getRandom()));
      if (experience > 0) {
         this.popExperience(level, pos, experience);
      }

   }

   private static record ShapePairKey(VoxelShape first, VoxelShape second) {
      public boolean equals(final Object o) {
         if (o instanceof Block.ShapePairKey that) {
            if (this.first == that.first && this.second == that.second) {
               return true;
            }
         }

         return false;
      }

      public int hashCode() {
         return System.identityHashCode(this.first) * 31 + System.identityHashCode(this.second);
      }
   }

   @Retention(RetentionPolicy.CLASS)
   @Target({ElementType.TYPE_USE})
   public @interface UpdateFlags {
   }
}
