package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.FailedTestTracker;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.GameTestRunner;
import net.minecraft.gametest.framework.GameTestTicker;
import net.minecraft.gametest.framework.RetryOptions;
import net.minecraft.gametest.framework.StructureUtils;
import net.minecraft.gametest.framework.TestCommand;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ARGB;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.loader.TemplatePathFactory;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

public class TestInstanceBlockEntity extends BlockEntity implements BoundingBoxRenderable, BeaconBeamOwner {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component INVALID_TEST_NAME = Component.translatable("test_instance_block.invalid_test");
   private static final List BEAM_CLEARED = List.of();
   private static final List BEAM_RUNNING = List.of(new BeaconBeamOwner.Section(ARGB.color(128, 128, 128)));
   private static final List BEAM_SUCCESS = List.of(new BeaconBeamOwner.Section(ARGB.color(0, 255, 0)));
   private static final List BEAM_REQUIRED_FAILED = List.of(new BeaconBeamOwner.Section(ARGB.color(255, 0, 0)));
   private static final List BEAM_OPTIONAL_FAILED = List.of(new BeaconBeamOwner.Section(ARGB.color(255, 128, 0)));
   private static final Vec3i STRUCTURE_OFFSET = new Vec3i(0, 1, 1);
   private TestInstanceBlockEntity.Data data;
   private final List errorMarkers = new ArrayList();

   public TestInstanceBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      super(BlockEntityTypes.TEST_INSTANCE_BLOCK, worldPosition, blockState);
      this.data = new TestInstanceBlockEntity.Data(Optional.empty(), Vec3i.ZERO, Rotation.NONE, false, TestInstanceBlockEntity.Status.CLEARED, Optional.empty());
   }

   public void set(final TestInstanceBlockEntity.Data data) {
      this.data = data;
      this.setChanged();
   }

   public static Optional getStructureSize(final ServerLevel level, final ResourceKey testKey) {
      return getStructureTemplate(level, testKey).map(StructureTemplate::getSize);
   }

   public BoundingBox getStructureBoundingBox() {
      BlockPos corner1 = this.getStructurePos();
      BlockPos corner2 = corner1.offset(this.getTransformedSize()).offset(-1, -1, -1);
      return BoundingBox.fromCorners(corner1, corner2);
   }

   public BoundingBox getTestBoundingBox() {
      return this.getStructureBoundingBox().inflatedBy(this.getPadding());
   }

   public AABB getStructureBounds() {
      return AABB.of(this.getStructureBoundingBox());
   }

   public AABB getTestBounds() {
      return this.getStructureBounds().inflate((double)this.getPadding());
   }

   private static Optional getStructureTemplate(final ServerLevel level, final ResourceKey testKey) {
      return level.registryAccess().get(testKey).map((test) -> ((GameTestInstance)test.value()).structure()).flatMap((template) -> level.getStructureManager().get(template));
   }

   public Optional test() {
      return this.data.test();
   }

   public Component getTestName() {
      return (Component)this.test().map((key) -> Component.literal(key.identifier().toString())).orElse(INVALID_TEST_NAME);
   }

   private Optional getTestHolder() {
      return this.test().flatMap(this.level.registryAccess()::get);
   }

   public boolean ignoreEntities() {
      return this.data.ignoreEntities();
   }

   public Vec3i getSize() {
      return this.data.size();
   }

   public Rotation getRotation() {
      return ((Rotation)this.getTestHolder().map(Holder::value).map(GameTestInstance::rotation).orElse(Rotation.NONE)).getRotated(this.data.rotation());
   }

   public Optional errorMessage() {
      return this.data.errorMessage();
   }

   public void setErrorMessage(final Component errorMessage) {
      this.set(this.data.withError(errorMessage));
   }

   public void setSuccess() {
      this.set(this.data.withStatus(TestInstanceBlockEntity.Status.FINISHED));
   }

   public void setRunning() {
      this.set(this.data.withStatus(TestInstanceBlockEntity.Status.RUNNING));
   }

   public void setChanged() {
      super.setChanged();
      if (this.level instanceof ServerLevel) {
         this.level.sendBlockUpdated(this.getBlockPos(), Blocks.AIR.defaultBlockState(), this.getBlockState(), 3);
      }

   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(final HolderLookup.Provider registries) {
      return this.saveCustomOnly(registries);
   }

   protected void loadAdditional(final ValueInput input) {
      input.read("data", TestInstanceBlockEntity.Data.CODEC).ifPresent(this::set);
      this.errorMarkers.clear();
      this.errorMarkers.addAll((Collection)input.read("errors", TestInstanceBlockEntity.ErrorMarker.LIST_CODEC).orElse(List.of()));
   }

   protected void saveAdditional(final ValueOutput output) {
      output.store("data", TestInstanceBlockEntity.Data.CODEC, this.data);
      if (!this.errorMarkers.isEmpty()) {
         output.store("errors", TestInstanceBlockEntity.ErrorMarker.LIST_CODEC, this.errorMarkers);
      }

   }

   public BoundingBoxRenderable.Mode renderMode() {
      return BoundingBoxRenderable.Mode.BOX;
   }

   public BlockPos getStructurePos() {
      int padding = this.getPadding();
      return getStructurePos(this.getBlockPos().offset(padding, padding, padding));
   }

   public static BlockPos getStructurePos(final BlockPos blockPos) {
      return blockPos.offset(STRUCTURE_OFFSET);
   }

   public BoundingBoxRenderable.RenderableBox getRenderableBox() {
      int padding = this.getPadding();
      BlockPos offsetPos = new BlockPos(STRUCTURE_OFFSET.getX() + padding, STRUCTURE_OFFSET.getY() + padding, STRUCTURE_OFFSET.getZ() + padding);
      return new BoundingBoxRenderable.RenderableBox(offsetPos, this.getTransformedSize());
   }

   public List getBeamSections() {
      List var10000;
      switch (this.data.status().ordinal()) {
         case 0:
            var10000 = BEAM_CLEARED;
            break;
         case 1:
            var10000 = BEAM_RUNNING;
            break;
         case 2:
            var10000 = this.errorMessage().isEmpty() ? BEAM_SUCCESS : (this.getTestHolder().map(Holder::value).map(GameTestInstance::required).orElse(true) ? BEAM_REQUIRED_FAILED : BEAM_OPTIONAL_FAILED);
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   private Vec3i getTransformedSize() {
      Vec3i size = this.getSize();
      Rotation rotation = this.getRotation();
      boolean axesSwitched = rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90;
      int xSize = axesSwitched ? size.getZ() : size.getX();
      int zSize = axesSwitched ? size.getX() : size.getZ();
      return new Vec3i(xSize, size.getY(), zSize);
   }

   public void resetTest(final Consumer feedbackOutput) {
      this.removeBarriers();
      this.clearErrorMarkers();
      boolean placed = this.placeStructure();
      if (placed) {
         feedbackOutput.accept(Component.translatable("test_instance_block.reset_success", this.getTestName()).withStyle(ChatFormatting.GREEN));
      }

      this.set(this.data.withStatus(TestInstanceBlockEntity.Status.CLEARED));
   }

   public Optional saveTest(final Consumer feedbackOutput) {
      Optional test = this.getTestHolder();
      Optional identifier;
      if (test.isPresent()) {
         identifier = Optional.of(((GameTestInstance)((Holder.Reference)test.get()).value()).structure());
      } else {
         identifier = this.test().map(ResourceKey::identifier);
      }

      if (identifier.isEmpty()) {
         BlockPos pos = this.getBlockPos();
         feedbackOutput.accept(Component.translatable("test_instance_block.error.unable_to_save", pos.getX(), pos.getY(), pos.getZ()).withStyle(ChatFormatting.RED));
         return identifier;
      } else {
         Level var5 = this.level;
         if (var5 instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)var5;
            StructureBlockEntity.saveStructure(serverLevel, (Identifier)identifier.get(), this.getStructurePos(), this.getSize(), this.ignoreEntities(), "", true, List.of(Blocks.AIR));
         }

         return identifier;
      }
   }

   public boolean exportTest(final Consumer feedbackOutput) {
      Optional saved = this.saveTest(feedbackOutput);
      if (!saved.isEmpty()) {
         Level var4 = this.level;
         if (var4 instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)var4;
            return export(serverLevel, (Identifier)saved.get(), feedbackOutput);
         }
      }

      return false;
   }

   public static boolean export(final ServerLevel level, final Identifier structureId, final Consumer feedbackOutput) {
      StructureTemplateManager structureManager = level.getStructureManager();
      TemplatePathFactory testTemplatePathFactory = structureManager.testTemplates();
      if (testTemplatePathFactory == null) {
         feedbackOutput.accept(Component.literal("Test structure exporting is disabled").withStyle(ChatFormatting.RED));
         return true;
      } else {
         Optional structureTemplate = structureManager.get(structureId);
         if (structureTemplate.isEmpty()) {
            feedbackOutput.accept(Component.literal("Could not find structure " + String.valueOf(structureId)).withStyle(ChatFormatting.RED));
            return true;
         } else {
            Path outputFile = testTemplatePathFactory.createAndValidatePathToStructure(structureId, StructureTemplateManager.RESOURCE_TEXT_STRUCTURE_LISTER);

            try {
               StructureTemplateManager.save(outputFile, (StructureTemplate)structureTemplate.get(), true);
            } catch (Exception var8) {
               LOGGER.error("Failed to save structure file {} to {}", new Object[]{structureId, outputFile, var8});
               feedbackOutput.accept(Component.literal("Failed to save structure file " + String.valueOf(structureId) + " to " + String.valueOf(outputFile)).withStyle(ChatFormatting.RED));
               return true;
            }

            feedbackOutput.accept(Component.literal("Exported " + String.valueOf(structureId) + " to " + String.valueOf(outputFile.toAbsolutePath())));
            return false;
         }
      }
   }

   public void runTest(final Consumer feedbackOutput) {
      Level test = this.level;
      if (test instanceof ServerLevel serverLevel) {
         Optional var7 = this.getTestHolder();
         BlockPos pos = this.getBlockPos();
         if (var7.isEmpty()) {
            feedbackOutput.accept(Component.translatable("test_instance_block.error.no_test", pos.getX(), pos.getY(), pos.getZ()).withStyle(ChatFormatting.RED));
         } else if (!this.placeStructure()) {
            feedbackOutput.accept(Component.translatable("test_instance_block.error.no_test_structure", pos.getX(), pos.getY(), pos.getZ()).withStyle(ChatFormatting.RED));
         } else {
            this.clearErrorMarkers();
            GameTestTicker.SINGLETON.clear();
            FailedTestTracker.forgetFailedTests();
            feedbackOutput.accept(Component.translatable("test_instance_block.starting", ((Holder.Reference)var7.get()).getRegisteredName()));
            GameTestInfo gameTestInfo = new GameTestInfo((Holder.Reference)var7.get(), this.data.rotation(), serverLevel, RetryOptions.noRetries());
            gameTestInfo.setTestBlockPos(pos);
            GameTestRunner runner = GameTestRunner.Builder.fromInfo(List.of(gameTestInfo), serverLevel.getServer()).build();
            TestCommand.trackAndStartRunner(serverLevel.getServer().createCommandSourceStack(), runner);
         }
      }
   }

   public boolean placeStructure() {
      Level template = this.level;
      if (template instanceof ServerLevel serverLevel) {
         Optional template = this.data.test().flatMap((test) -> getStructureTemplate(serverLevel, test));
         if (template.isPresent()) {
            this.placeStructure(serverLevel, (StructureTemplate)template.get());
            return true;
         }
      }

      return false;
   }

   private void placeStructure(final ServerLevel level, final StructureTemplate template) {
      StructurePlaceSettings placeSettings = (new StructurePlaceSettings()).setRotation(this.getRotation()).setIgnoreEntities(this.data.ignoreEntities()).setKnownShape(true);
      BlockPos pos = this.getStartCorner();
      this.forceLoadChunks();
      int padding = this.getPadding();
      StructureUtils.clearSpaceForStructure(this.getTestBoundingBox(), level);
      this.removeEntities();
      template.placeInWorld(level, pos, pos, placeSettings, level.getRandom(), 818);
   }

   private int getPadding() {
      return this.getTestHolder().map((r) -> ((GameTestInstance)r.value()).padding()).orElse(0);
   }

   private void removeEntities() {
      this.level.getEntities((Entity)null, this.getTestBounds()).stream().filter((entity) -> !(entity instanceof Player)).forEach(Entity::discard);
   }

   private void forceLoadChunks() {
      Level var2 = this.level;
      if (var2 instanceof ServerLevel serverLevel) {
         this.getStructureBoundingBox().intersectingChunks().forEach((pos) -> serverLevel.setChunkForced(pos.x(), pos.z(), true));
      }

   }

   public BlockPos getStartCorner() {
      Vec3i structureSize = this.getSize();
      Rotation rotation = this.getRotation();
      BlockPos northWestCorner = this.getStructurePos();
      BlockPos var10000;
      switch (rotation) {
         case NONE:
            var10000 = northWestCorner;
            break;
         case CLOCKWISE_90:
            var10000 = northWestCorner.offset(structureSize.getZ() - 1, 0, 0);
            break;
         case CLOCKWISE_180:
            var10000 = northWestCorner.offset(structureSize.getX() - 1, 0, structureSize.getZ() - 1);
            break;
         case COUNTERCLOCKWISE_90:
            var10000 = northWestCorner.offset(0, 0, structureSize.getX() - 1);
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public void encaseStructure() {
      this.processStructureBoundary((blockPos) -> {
         if (!this.level.getBlockState(blockPos).is(Blocks.TEST_INSTANCE_BLOCK)) {
            this.level.setBlockAndUpdate(blockPos, Blocks.BARRIER.defaultBlockState());
         }

      });
   }

   public void removeBarriers() {
      this.processStructureBoundary((blockPos) -> {
         if (this.level.getBlockState(blockPos).is(Blocks.BARRIER)) {
            this.level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
         }

      });
   }

   public void processStructureBoundary(final Consumer action) {
      AABB bounds = this.getStructureBounds();
      boolean hasCeiling = !this.getTestHolder().map((h) -> ((GameTestInstance)h.value()).skyAccess()).orElse(false);
      BlockPos low = BlockPos.containing(bounds.minX, bounds.minY, bounds.minZ).offset(-1, -1, -1);
      BlockPos high = BlockPos.containing(bounds.maxX, bounds.maxY, bounds.maxZ);
      BlockPos.betweenClosedStream(low, high).forEach((blockPos) -> {
         boolean isNonCeilingEdge = blockPos.getX() == low.getX() || blockPos.getX() == high.getX() || blockPos.getZ() == low.getZ() || blockPos.getZ() == high.getZ() || blockPos.getY() == low.getY();
         boolean isCeiling = blockPos.getY() == high.getY();
         if (isNonCeilingEdge || isCeiling && hasCeiling) {
            action.accept(blockPos);
         }

      });
   }

   public void markError(final BlockPos pos, final Component text) {
      this.errorMarkers.add(new TestInstanceBlockEntity.ErrorMarker(pos, text));
      this.setChanged();
   }

   public void clearErrorMarkers() {
      if (!this.errorMarkers.isEmpty()) {
         this.errorMarkers.clear();
         this.setChanged();
      }

   }

   public List getErrorMarkers() {
      return this.errorMarkers;
   }

   public static record Data(Optional test, Vec3i size, Rotation rotation, boolean ignoreEntities, TestInstanceBlockEntity.Status status, Optional errorMessage) {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(ResourceKey.codec(Registries.TEST_INSTANCE).optionalFieldOf("test").forGetter(TestInstanceBlockEntity.Data::test), Vec3i.CODEC.fieldOf("size").forGetter(TestInstanceBlockEntity.Data::size), Rotation.CODEC.fieldOf("rotation").forGetter(TestInstanceBlockEntity.Data::rotation), Codec.BOOL.fieldOf("ignore_entities").forGetter(TestInstanceBlockEntity.Data::ignoreEntities), TestInstanceBlockEntity.Status.CODEC.fieldOf("status").forGetter(TestInstanceBlockEntity.Data::status), ComponentSerialization.CODEC.optionalFieldOf("error_message").forGetter(TestInstanceBlockEntity.Data::errorMessage)).apply(i, TestInstanceBlockEntity.Data::new));
      public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.optional(ResourceKey.streamCodec(Registries.TEST_INSTANCE)), TestInstanceBlockEntity.Data::test, Vec3i.STREAM_CODEC, TestInstanceBlockEntity.Data::size, Rotation.STREAM_CODEC, TestInstanceBlockEntity.Data::rotation, ByteBufCodecs.BOOL, TestInstanceBlockEntity.Data::ignoreEntities, TestInstanceBlockEntity.Status.STREAM_CODEC, TestInstanceBlockEntity.Data::status, ByteBufCodecs.optional(ComponentSerialization.STREAM_CODEC), TestInstanceBlockEntity.Data::errorMessage, TestInstanceBlockEntity.Data::new);

      public TestInstanceBlockEntity.Data withSize(final Vec3i size) {
         return new TestInstanceBlockEntity.Data(this.test, size, this.rotation, this.ignoreEntities, this.status, this.errorMessage);
      }

      public TestInstanceBlockEntity.Data withStatus(final TestInstanceBlockEntity.Status status) {
         return new TestInstanceBlockEntity.Data(this.test, this.size, this.rotation, this.ignoreEntities, status, Optional.empty());
      }

      public TestInstanceBlockEntity.Data withError(final Component error) {
         return new TestInstanceBlockEntity.Data(this.test, this.size, this.rotation, this.ignoreEntities, TestInstanceBlockEntity.Status.FINISHED, Optional.of(error));
      }
   }

   public static record ErrorMarker(BlockPos pos, Component text) {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(BlockPos.CODEC.fieldOf("pos").forGetter(TestInstanceBlockEntity.ErrorMarker::pos), ComponentSerialization.CODEC.fieldOf("text").forGetter(TestInstanceBlockEntity.ErrorMarker::text)).apply(i, TestInstanceBlockEntity.ErrorMarker::new));
      public static final Codec LIST_CODEC = CODEC.listOf();
   }

   public static enum Status implements StringRepresentable {
      CLEARED("cleared", 0),
      RUNNING("running", 1),
      FINISHED("finished", 2);

      private static final IntFunction ID_MAP = ByIdMap.continuous((s) -> s.index, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      public static final Codec CODEC = StringRepresentable.fromEnum(TestInstanceBlockEntity.Status::values);
      public static final StreamCodec STREAM_CODEC = ByteBufCodecs.idMapper(TestInstanceBlockEntity.Status::byIndex, (s) -> s.index);
      private final String id;
      private final int index;

      private Status(final String id, final int index) {
         this.id = id;
         this.index = index;
      }

      public String getSerializedName() {
         return this.id;
      }

      public static TestInstanceBlockEntity.Status byIndex(final int index) {
         return (TestInstanceBlockEntity.Status)ID_MAP.apply(index);
      }

      // $FF: synthetic method
      private static TestInstanceBlockEntity.Status[] $values() {
         return new TestInstanceBlockEntity.Status[]{CLEARED, RUNNING, FINISHED};
      }
   }
}
