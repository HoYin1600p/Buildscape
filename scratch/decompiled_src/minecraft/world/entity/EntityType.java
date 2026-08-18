package net.minecraft.world.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.DependantName;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class EntityType implements EntityTypeTest, FeatureElement {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Holder.Reference builtInRegistryHolder = BuiltInRegistries.ENTITY_TYPE.createIntrusiveHolder(this);
   public static final Codec CODEC = BuiltInRegistries.ENTITY_TYPE.byNameCodec();
   public static final StreamCodec STREAM_CODEC = ByteBufCodecs.registry(Registries.ENTITY_TYPE);
   public static final int NO_UPDATE_INTERVAL = Integer.MAX_VALUE;
   private final EntityType.EntityFactory factory;
   private final MobCategory category;
   private final TagKey immuneTo;
   private final boolean serialize;
   private final boolean summon;
   private final boolean fireImmune;
   private final boolean canSpawnFarFromPlayer;
   private final int clientTrackingRange;
   private final int updateInterval;
   private final String descriptionId;
   private @Nullable Component description;
   private final Optional lootTable;
   private final EntityDimensions dimensions;
   private final float spawnDimensionsScale;
   private final FeatureFlagSet requiredFeatures;
   private final boolean allowedInPeaceful;
   private final boolean trackDeltas;

   public static Identifier getKey(final EntityType type) {
      return BuiltInRegistries.ENTITY_TYPE.getKey(type);
   }

   public EntityType(final EntityType.EntityFactory factory, final MobCategory category, final boolean serialize, final boolean summon, final boolean fireImmune, final boolean canSpawnFarFromPlayer, final TagKey immuneTo, final EntityDimensions dimensions, final float spawnDimensionsScale, final int clientTrackingRange, final int updateInterval, final String descriptionId, final Optional lootTable, final FeatureFlagSet requiredFeatures, final boolean allowedInPeaceful, final boolean trackDeltas) {
      this.factory = factory;
      this.category = category;
      this.canSpawnFarFromPlayer = canSpawnFarFromPlayer;
      this.serialize = serialize;
      this.summon = summon;
      this.fireImmune = fireImmune;
      this.immuneTo = immuneTo;
      this.dimensions = dimensions;
      this.spawnDimensionsScale = spawnDimensionsScale;
      this.clientTrackingRange = clientTrackingRange;
      this.updateInterval = updateInterval;
      this.descriptionId = descriptionId;
      this.lootTable = lootTable;
      this.requiredFeatures = requiredFeatures;
      this.allowedInPeaceful = allowedInPeaceful;
      this.trackDeltas = trackDeltas;
   }

   public @Nullable Entity spawn(final ServerLevel level, final @Nullable ItemStack itemStack, final @Nullable LivingEntity user, final BlockPos spawnPos, final EntitySpawnReason spawnReason, final boolean tryMoveDown, final boolean movedUp) {
      PostSpawnProcessor postSpawnConfig;
      if (itemStack != null) {
         postSpawnConfig = createDefaultStackConfig(level, itemStack, user);
      } else {
         postSpawnConfig = PostSpawnProcessor.nop();
      }

      return this.spawn(level, postSpawnConfig, spawnPos, spawnReason, tryMoveDown, movedUp);
   }

   public static PostSpawnProcessor createDefaultStackConfig(final Level level, final ItemStack itemStack, final @Nullable LivingEntity user) {
      return appendDefaultStackConfig(PostSpawnProcessor.nop(), level, itemStack, user);
   }

   public static PostSpawnProcessor appendDefaultStackConfig(final PostSpawnProcessor initialConfig, final Level level, final ItemStack itemStack, final @Nullable LivingEntity user) {
      return appendCustomEntityStackConfig(appendComponentsConfig(initialConfig, itemStack), level, itemStack, user);
   }

   public static PostSpawnProcessor appendComponentsConfig(final PostSpawnProcessor initialConfig, final ItemStack itemStack) {
      return initialConfig.andThen((entity) -> entity.applyComponentsFromItemStack(itemStack));
   }

   public static PostSpawnProcessor appendCustomEntityStackConfig(final PostSpawnProcessor initialConfig, final Level level, final ItemStack itemStack, final @Nullable LivingEntity user) {
      TypedEntityData entityData = (TypedEntityData)itemStack.get(DataComponents.ENTITY_DATA);
      return entityData != null ? initialConfig.andThen((entity) -> updateCustomEntityTag(level, user, entity, entityData)) : initialConfig;
   }

   public @Nullable Entity spawn(final ServerLevel level, final BlockPos spawnPos, final EntitySpawnReason spawnReason) {
      return this.spawn(level, (PostSpawnProcessor)null, spawnPos, spawnReason, false, false);
   }

   public @Nullable Entity spawn(final ServerLevel level, final @Nullable PostSpawnProcessor postSpawnConfig, final BlockPos spawnPos, final EntitySpawnReason spawnReason, final boolean tryMoveDown, final boolean movedUp) {
      Entity entity = (T)this.create(level, postSpawnConfig, spawnPos, spawnReason, tryMoveDown, movedUp);
      if (entity != null) {
         level.addFreshEntityWithPassengers(entity);
         if (entity instanceof Mob) {
            Mob mob = (Mob)entity;
            mob.playAmbientSound();
         }
      }

      return entity;
   }

   public @Nullable Entity create(final ServerLevel level, final @Nullable PostSpawnProcessor postSpawnConfig, final BlockPos spawnPos, final EntitySpawnReason spawnReason, final boolean tryMoveDown, final boolean movedUp) {
      Entity entity = (T)this.create(level, spawnReason);
      if (entity == null) {
         return null;
      } else {
         double yOff;
         if (tryMoveDown) {
            entity.setPos((double)spawnPos.getX() + 0.5D, (double)(spawnPos.getY() + 1), (double)spawnPos.getZ() + 0.5D);
            yOff = getYOffset(level, spawnPos, movedUp, entity.getBoundingBox());
         } else {
            yOff = 0.0D;
         }

         entity.snapTo((double)spawnPos.getX() + 0.5D, (double)spawnPos.getY() + yOff, (double)spawnPos.getZ() + 0.5D, Mth.wrapDegrees(level.getRandom().nextFloat() * 360.0F), 0.0F);
         if (entity instanceof Mob) {
            Mob mob = (Mob)entity;
            mob.yHeadRot = mob.getYRot();
            mob.yBodyRot = mob.getYRot();
            mob.finalizeSpawn(level, level.getCurrentDifficultyAt(mob.blockPosition()), spawnReason, (SpawnGroupData)null);
         }

         if (postSpawnConfig != null) {
            postSpawnConfig.apply(entity);
         }

         return entity;
      }
   }

   protected static double getYOffset(final LevelReader level, final BlockPos spawnPos, final boolean movedUp, final AABB entityBox) {
      AABB aabb = new AABB(spawnPos);
      if (movedUp) {
         aabb = aabb.expandTowards(0.0D, -1.0D, 0.0D);
      }

      Iterable shapes = level.getCollisions((Entity)null, aabb);
      return 1.0D + Shapes.collide(Direction.Axis.Y, entityBox, shapes, movedUp ? -2.0D : -1.0D);
   }

   public static void updateCustomEntityTag(final Level level, final @Nullable LivingEntity user, final @Nullable Entity entity, final TypedEntityData entityData) {
      MinecraftServer server = level.getServer();
      if (server != null && entity != null) {
         if (entity.getType() == entityData.type()) {
            if (!level.isClientSide() && entity.getType().onlyOpCanSetNbt()) {
               if (!(user instanceof Player)) {
                  return;
               }

               Player player = (Player)user;
               if (!server.getPlayerList().isOp(player.nameAndId())) {
                  return;
               }
            }

            entityData.loadInto(entity);
         }
      }
   }

   public boolean canSerialize() {
      return this.serialize;
   }

   public boolean canSummon() {
      return this.summon;
   }

   public boolean fireImmune() {
      return this.fireImmune;
   }

   public boolean canSpawnFarFromPlayer() {
      return this.canSpawnFarFromPlayer;
   }

   public MobCategory getCategory() {
      return this.category;
   }

   public String getDescriptionId() {
      return this.descriptionId;
   }

   public Component getDescription() {
      if (this.description == null) {
         this.description = Component.translatable(this.getDescriptionId());
      }

      return this.description;
   }

   public String toString() {
      return this.getDescriptionId();
   }

   public String toShortString() {
      int dot = this.getDescriptionId().lastIndexOf(46);
      return dot == -1 ? this.getDescriptionId() : this.getDescriptionId().substring(dot + 1);
   }

   public Optional getDefaultLootTable() {
      return this.lootTable;
   }

   public float getWidth() {
      return this.dimensions.width();
   }

   public float getHeight() {
      return this.dimensions.height();
   }

   public FeatureFlagSet requiredFeatures() {
      return this.requiredFeatures;
   }

   public boolean canSpawn(final Level level) {
      if (!this.isEnabled(level.enabledFeatures())) {
         return false;
      } else {
         return this.isAllowedInPeaceful() || level.getDifficulty() != Difficulty.PEACEFUL;
      }
   }

   public @Nullable Entity create(final Level level, final EntitySpawnReason reason) {
      return this.create(level, new EntitySpawnRequest(reason, false));
   }

   public @Nullable Entity create(final Level level, final EntitySpawnRequest request) {
      return !request.ignoreChecks() && !this.canSpawn(level) ? null : this.factory.create(this, level);
   }

   public static Optional create(final ValueInput input, final Level level, final EntitySpawnRequest request) {
      return Util.ifElse(by(input).map((type) -> type.create(level, request)), (entity) -> entity.load(input), () -> LOGGER.warn("Skipping Entity with id {}", input.getStringOr("id", "[invalid]")));
   }

   public static Optional create(final EntityType type, final ValueInput input, final Level level, final EntitySpawnReason reason) {
      Optional entity = Optional.ofNullable(type.create(level, reason));
      entity.ifPresent((e) -> e.load(input));
      return entity;
   }

   public AABB getSpawnAABB(final Vec3 at) {
      return this.getSpawnAABB(at.x, at.y, at.z);
   }

   public AABB getSpawnAABB(final double x, final double y, final double z) {
      float halfWidth = this.spawnDimensionsScale * this.getWidth() / 2.0F;
      float height = this.spawnDimensionsScale * this.getHeight();
      return new AABB(x - (double)halfWidth, y, z - (double)halfWidth, x + (double)halfWidth, y + (double)height, z + (double)halfWidth);
   }

   public boolean isBlockDangerous(final BlockState state) {
      if (state.is(this.immuneTo)) {
         return false;
      } else if (!this.fireImmune && NodeEvaluator.isBurningBlock(state)) {
         return true;
      } else {
         return state.is(Blocks.WITHER_ROSE) || state.is(Blocks.SWEET_BERRY_BUSH) || state.is(Blocks.CACTUS) || state.is(Blocks.POWDER_SNOW);
      }
   }

   public EntityDimensions getDimensions() {
      return this.dimensions;
   }

   public static Optional by(final ValueInput input) {
      return input.read("id", CODEC);
   }

   public static @Nullable Entity loadEntityRecursive(final CompoundTag tag, final Level level, final EntitySpawnRequest request, final EntityProcessor postLoad) {
      try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(LOGGER)) {
         return loadEntityRecursive(TagValueInput.create(reporter, level.registryAccess(), tag), level, request, postLoad);
      }
   }

   public static @Nullable Entity loadEntityRecursive(final EntityType type, final CompoundTag tag, final Level level, final EntitySpawnReason reason, final EntityProcessor postLoad) {
      try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(LOGGER)) {
         return loadEntityRecursive(type, TagValueInput.create(reporter, level.registryAccess(), tag), level, reason, postLoad);
      }
   }

   public static @Nullable Entity loadEntityRecursive(final ValueInput input, final Level level, final EntitySpawnReason reason, final EntityProcessor postLoad) {
      return loadEntityRecursive(input, level, new EntitySpawnRequest(reason, false), postLoad);
   }

   public static @Nullable Entity loadEntityRecursive(final ValueInput input, final Level level, final EntitySpawnRequest request, final EntityProcessor postLoad) {
      return (Entity)loadStaticEntity(input, level, request).map(postLoad::process).map((entity) -> loadPassengersRecursive(entity, input, level, request, postLoad)).orElse((Object)null);
   }

   public static @Nullable Entity loadEntityRecursive(final EntityType type, final ValueInput input, final Level level, final EntitySpawnReason reason, final EntityProcessor postLoad) {
      return (Entity)loadStaticEntity(type, input, level, reason).map(postLoad::process).map((entity) -> loadPassengersRecursive(entity, input, level, new EntitySpawnRequest(reason, false), postLoad)).orElse((Object)null);
   }

   private static Entity loadPassengersRecursive(final Entity entity, final ValueInput input, final Level level, final EntitySpawnRequest request, final EntityProcessor postLoad) {
      for(ValueInput passengerTag : input.childrenListOrEmpty("Passengers")) {
         Entity passenger = loadEntityRecursive(passengerTag, level, request, postLoad);
         if (passenger != null) {
            passenger.startRiding(entity, true, false);
         }
      }

      return entity;
   }

   public static Stream loadEntitiesRecursive(final ValueInput.ValueInputList entities, final Level level, final EntitySpawnReason reason) {
      return entities.stream().mapMulti((tag, output) -> loadEntityRecursive(tag, level, reason, (entity) -> {
            output.accept(entity);
            return entity;
         }));
   }

   private static Optional loadStaticEntity(final ValueInput input, final Level level, final EntitySpawnRequest request) {
      try {
         return create(input, level, request);
      } catch (RuntimeException var4) {
         LOGGER.warn("Exception loading entity: ", var4);
         return Optional.empty();
      }
   }

   private static Optional loadStaticEntity(final EntityType type, final ValueInput input, final Level level, final EntitySpawnReason reason) {
      try {
         return create(type, input, level, reason);
      } catch (RuntimeException var5) {
         LOGGER.warn("Exception loading entity: ", var5);
         return Optional.empty();
      }
   }

   public int clientTrackingRange() {
      return this.clientTrackingRange;
   }

   public int updateInterval() {
      return this.updateInterval;
   }

   public boolean hasUpdateInterval() {
      return this.updateInterval != Integer.MAX_VALUE;
   }

   public boolean trackDeltas() {
      return this.trackDeltas;
   }

   public @Nullable Entity tryCast(final Entity entity) {
      return entity.getType() == this ? entity : null;
   }

   public Class getBaseClass() {
      return Entity.class;
   }

   /** @deprecated */
   @Deprecated
   public Holder.Reference builtInRegistryHolder() {
      return this.builtInRegistryHolder;
   }

   public boolean isAllowedInPeaceful() {
      return this.allowedInPeaceful;
   }

   public boolean onlyOpCanSetNbt() {
      return EntityTypes.OP_ONLY_CUSTOM_DATA.contains(this);
   }

   public static class Builder {
      private final EntityType.EntityFactory factory;
      private final MobCategory category;
      private TagKey immuneTo = BlockTags.DEFAULT_IMMUNE_TO;
      private boolean serialize = true;
      private boolean summon = true;
      private boolean fireImmune;
      private boolean canSpawnFarFromPlayer;
      private int clientTrackingRange = 5;
      private int updateInterval = 3;
      private EntityDimensions dimensions = EntityDimensions.scalable(0.6F, 1.8F);
      private float spawnDimensionsScale = 1.0F;
      private EntityAttachments.Builder attachments = EntityAttachments.builder();
      private FeatureFlagSet requiredFeatures = FeatureFlags.VANILLA_SET;
      private DependantName lootTable = (id) -> Optional.of(ResourceKey.create(Registries.LOOT_TABLE, id.identifier().withPrefix("entities/")));
      private final DependantName descriptionId = (id) -> Util.makeDescriptionId("entity", id.identifier());
      private boolean allowedInPeaceful = true;
      private boolean trackDeltas = true;

      private Builder(final EntityType.EntityFactory factory, final MobCategory category) {
         this.factory = factory;
         this.category = category;
         this.canSpawnFarFromPlayer = category == MobCategory.CREATURE || category == MobCategory.MISC;
      }

      public static EntityType.Builder of(final EntityType.EntityFactory factory, final MobCategory category) {
         return new EntityType.Builder(factory, category);
      }

      public static EntityType.Builder createNothing(final MobCategory category) {
         return new EntityType.Builder((t, l) -> null, category);
      }

      public EntityType.Builder sized(final float width, final float height) {
         this.dimensions = EntityDimensions.scalable(width, height);
         return this;
      }

      public EntityType.Builder spawnDimensionsScale(final float scale) {
         this.spawnDimensionsScale = scale;
         return this;
      }

      public EntityType.Builder eyeHeight(final float eyeHeight) {
         this.dimensions = this.dimensions.withEyeHeight(eyeHeight);
         return this;
      }

      public EntityType.Builder passengerAttachments(final float... offsetYs) {
         for(float offsetY : offsetYs) {
            this.attachments = this.attachments.attach(EntityAttachment.PASSENGER, 0.0F, offsetY, 0.0F);
         }

         return this;
      }

      public EntityType.Builder passengerAttachments(final Vec3... points) {
         for(Vec3 point : points) {
            this.attachments = this.attachments.attach(EntityAttachment.PASSENGER, point);
         }

         return this;
      }

      public EntityType.Builder vehicleAttachment(final Vec3 point) {
         return this.attach(EntityAttachment.VEHICLE, point);
      }

      public EntityType.Builder ridingOffset(final float ridingOffset) {
         return this.attach(EntityAttachment.VEHICLE, 0.0F, -ridingOffset, 0.0F);
      }

      public EntityType.Builder nameTagOffset(final float nameTagOffset) {
         return this.attach(EntityAttachment.NAME_TAG, 0.0F, nameTagOffset, 0.0F);
      }

      public EntityType.Builder attach(final EntityAttachment attachment, final float x, final float y, final float z) {
         this.attachments = this.attachments.attach(attachment, x, y, z);
         return this;
      }

      public EntityType.Builder attach(final EntityAttachment attachment, final Vec3 point) {
         this.attachments = this.attachments.attach(attachment, point);
         return this;
      }

      public EntityType.Builder noSummon() {
         this.summon = false;
         return this;
      }

      public EntityType.Builder noSave() {
         this.serialize = false;
         return this;
      }

      public EntityType.Builder fireImmune() {
         this.fireImmune = true;
         return this;
      }

      public EntityType.Builder immuneTo(final TagKey tag) {
         this.immuneTo = tag;
         return this;
      }

      public EntityType.Builder canSpawnFarFromPlayer() {
         this.canSpawnFarFromPlayer = true;
         return this;
      }

      public EntityType.Builder clientTrackingRange(final int clientChunkRange) {
         this.clientTrackingRange = clientChunkRange;
         return this;
      }

      public EntityType.Builder updateInterval(final int updateInterval) {
         this.updateInterval = updateInterval;
         return this;
      }

      public EntityType.Builder noUpdateInterval() {
         return this.updateInterval(Integer.MAX_VALUE);
      }

      public EntityType.Builder requiredFeatures(final FeatureFlag... flags) {
         this.requiredFeatures = FeatureFlags.REGISTRY.subset(flags);
         return this;
      }

      public EntityType.Builder noLootTable() {
         this.lootTable = DependantName.fixed(Optional.empty());
         return this;
      }

      public EntityType.Builder notInPeaceful() {
         this.allowedInPeaceful = false;
         return this;
      }

      public EntityType.Builder dontTrackDeltas() {
         this.trackDeltas = false;
         return this;
      }

      public EntityType build(final ResourceKey name) {
         if (this.serialize) {
            Util.fetchChoiceType(References.ENTITY_TREE, name.identifier().toString());
         }

         return new EntityType(this.factory, this.category, this.serialize, this.summon, this.fireImmune, this.canSpawnFarFromPlayer, this.immuneTo, this.dimensions.withAttachments(this.attachments), this.spawnDimensionsScale, this.clientTrackingRange, this.updateInterval, (String)this.descriptionId.get(name), (Optional)this.lootTable.get(name), this.requiredFeatures, this.allowedInPeaceful, this.trackDeltas);
      }
   }

   @FunctionalInterface
   public interface EntityFactory {
      @Nullable Entity create(final EntityType entityType, final Level level);
   }
}
