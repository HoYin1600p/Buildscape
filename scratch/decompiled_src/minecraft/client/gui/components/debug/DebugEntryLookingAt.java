package net.minecraft.client.gui.components.debug;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.TypedInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.Nullable;

public abstract class DebugEntryLookingAt implements DebugScreenEntry {
   private static final int RANGE = 20;
   private static final Identifier BLOCK_GROUP = Identifier.withDefaultNamespace("looking_at_block");
   private static final Identifier FLUID_GROUP = Identifier.withDefaultNamespace("looking_at_fluid");

   public void display(final DebugScreenDisplayer displayer, final @Nullable Level serverOrClientLevel, final @Nullable LevelChunk clientChunk, final @Nullable LevelChunk serverChunk) {
      Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
      Level clientOrServerLevel = (Level)(SharedConstants.DEBUG_SHOW_SERVER_DEBUG_VALUES ? serverOrClientLevel : Minecraft.getInstance().level);
      if (cameraEntity != null && clientOrServerLevel != null) {
         HitResult block = this.getHitResult(cameraEntity);
         List result = new ArrayList();
         if (block.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = ((BlockHitResult)block).getBlockPos();
            this.extractInfo(result, clientOrServerLevel, pos);
         }

         displayer.addToGroup(this.group(), result);
      }
   }

   public abstract HitResult getHitResult(final Entity cameraEntity);

   public abstract void extractInfo(List result, Level level, BlockPos pos);

   public abstract Identifier group();

   public static void addTagEntries(final List result, final TypedInstance instance) {
      instance.tags().map((e) -> "#" + String.valueOf(e.location())).forEach(result::add);
   }

   public static class BlockStateInfo extends DebugEntryLookingAt.DebugEntryLookingAtState {
      protected BlockStateInfo() {
         super("Targeted Block");
      }

      public HitResult getHitResult(final Entity cameraEntity) {
         return cameraEntity.pick(20.0D, 0.0F, false);
      }

      public BlockState getInstance(final Level level, final BlockPos pos) {
         return level.getBlockState(pos);
      }

      public Identifier group() {
         return DebugEntryLookingAt.BLOCK_GROUP;
      }
   }

   public static class BlockTagInfo extends DebugEntryLookingAt.DebugEntryLookingAtTags {
      public HitResult getHitResult(final Entity cameraEntity) {
         return cameraEntity.pick(20.0D, 0.0F, false);
      }

      public BlockState getInstance(final Level level, final BlockPos pos) {
         return level.getBlockState(pos);
      }

      public Identifier group() {
         return DebugEntryLookingAt.BLOCK_GROUP;
      }
   }

   public abstract static class DebugEntryLookingAtState extends DebugEntryLookingAt {
      private final String prefix;

      protected DebugEntryLookingAtState(final String prefix) {
         this.prefix = prefix;
      }

      protected abstract StateHolder getInstance(Level level, BlockPos pos);

      public void extractInfo(final List result, final Level level, final BlockPos pos) {
         StateHolder stateInstance = (StateType)this.getInstance(level, pos);
         result.add(String.valueOf(ChatFormatting.UNDERLINE) + this.prefix + ": " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
         result.add(((TypedInstance)stateInstance).typeHolder().getRegisteredName());
         addStateProperties(result, stateInstance);
      }

      private static void addStateProperties(final List result, final StateHolder stateHolder) {
         stateHolder.getValues().forEach((entry) -> result.add(getPropertyValueString(entry)));
      }

      private static String getPropertyValueString(final Property.Value entry) {
         String valueString = entry.valueName();
         if (Boolean.TRUE.equals(entry.value())) {
            valueString = String.valueOf(ChatFormatting.GREEN) + valueString;
         } else if (Boolean.FALSE.equals(entry.value())) {
            valueString = String.valueOf(ChatFormatting.RED) + valueString;
         }

         return entry.property().getName() + ": " + valueString;
      }
   }

   public abstract static class DebugEntryLookingAtTags extends DebugEntryLookingAt {
      protected abstract TypedInstance getInstance(Level level, BlockPos pos);

      public void extractInfo(final List result, final Level level, final BlockPos pos) {
         TypedInstance instance = (T)this.getInstance(level, pos);
         addTagEntries(result, instance);
      }
   }

   public static class FluidStateInfo extends DebugEntryLookingAt.DebugEntryLookingAtState {
      protected FluidStateInfo() {
         super("Targeted Fluid");
      }

      public HitResult getHitResult(final Entity cameraEntity) {
         return cameraEntity.pick(20.0D, 0.0F, true);
      }

      public FluidState getInstance(final Level level, final BlockPos pos) {
         return level.getFluidState(pos);
      }

      public Identifier group() {
         return DebugEntryLookingAt.FLUID_GROUP;
      }
   }

   public static class FluidTagInfo extends DebugEntryLookingAt.DebugEntryLookingAtTags {
      public HitResult getHitResult(final Entity cameraEntity) {
         return cameraEntity.pick(20.0D, 0.0F, true);
      }

      public FluidState getInstance(final Level level, final BlockPos pos) {
         return level.getFluidState(pos);
      }

      public Identifier group() {
         return DebugEntryLookingAt.FLUID_GROUP;
      }
   }
}
