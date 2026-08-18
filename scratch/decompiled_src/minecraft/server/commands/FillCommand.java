package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jspecify.annotations.Nullable;

public class FillCommand {
   private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType((max, count) -> Component.translatableEscape("commands.fill.toobig", max, count));
   private static final BlockInput HOLLOW_CORE = new BlockInput(Blocks.AIR.defaultBlockState(), Collections.emptySet(), (CompoundTag)null);
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.fill.failed"));

   public static void register(final CommandDispatcher dispatcher, final CommandBuildContext context) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("fill").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))).then(Commands.argument("from", BlockPosArgument.blockPos()).then(Commands.argument("to", BlockPosArgument.blockPos()).then(wrapWithMode(context, Commands.argument("block", BlockStateArgument.block(context)), (c) -> BlockPosArgument.getLoadedBlockPos(c, "from"), (c) -> BlockPosArgument.getLoadedBlockPos(c, "to"), (c) -> BlockStateArgument.getBlock(c, "block"), (c) -> null).then(((LiteralArgumentBuilder)Commands.literal("replace").executes((c) -> fillBlocks((CommandSourceStack)c.getSource(), BoundingBox.fromCorners(BlockPosArgument.getLoadedBlockPos(c, "from"), BlockPosArgument.getLoadedBlockPos(c, "to")), BlockStateArgument.getBlock(c, "block"), FillCommand.Mode.REPLACE, (Predicate)null, false))).then(wrapWithMode(context, Commands.argument("filter", BlockPredicateArgument.blockPredicate(context)), (c) -> BlockPosArgument.getLoadedBlockPos(c, "from"), (c) -> BlockPosArgument.getLoadedBlockPos(c, "to"), (c) -> BlockStateArgument.getBlock(c, "block"), (c) -> BlockPredicateArgument.getBlockPredicate(c, "filter")))).then(Commands.literal("keep").executes((c) -> fillBlocks((CommandSourceStack)c.getSource(), BoundingBox.fromCorners(BlockPosArgument.getLoadedBlockPos(c, "from"), BlockPosArgument.getLoadedBlockPos(c, "to")), BlockStateArgument.getBlock(c, "block"), FillCommand.Mode.REPLACE, (b) -> b.getLevel().isEmptyBlock(b.getPos()), false)))))));
   }

   private static ArgumentBuilder wrapWithMode(final CommandBuildContext context, final ArgumentBuilder builder, final InCommandFunction from, final InCommandFunction to, final InCommandFunction block, final FillCommand.NullableCommandFunction filter) {
      return builder.executes((c) -> fillBlocks((CommandSourceStack)c.getSource(), BoundingBox.fromCorners((Vec3i)from.apply(c), (Vec3i)to.apply(c)), (BlockInput)block.apply(c), FillCommand.Mode.REPLACE, (Predicate)filter.apply(c), false)).then(Commands.literal("outline").executes((c) -> fillBlocks((CommandSourceStack)c.getSource(), BoundingBox.fromCorners((Vec3i)from.apply(c), (Vec3i)to.apply(c)), (BlockInput)block.apply(c), FillCommand.Mode.OUTLINE, (Predicate)filter.apply(c), false))).then(Commands.literal("hollow").executes((c) -> fillBlocks((CommandSourceStack)c.getSource(), BoundingBox.fromCorners((Vec3i)from.apply(c), (Vec3i)to.apply(c)), (BlockInput)block.apply(c), FillCommand.Mode.HOLLOW, (Predicate)filter.apply(c), false))).then(Commands.literal("destroy").executes((c) -> fillBlocks((CommandSourceStack)c.getSource(), BoundingBox.fromCorners((Vec3i)from.apply(c), (Vec3i)to.apply(c)), (BlockInput)block.apply(c), FillCommand.Mode.DESTROY, (Predicate)filter.apply(c), false))).then(Commands.literal("strict").executes((c) -> fillBlocks((CommandSourceStack)c.getSource(), BoundingBox.fromCorners((Vec3i)from.apply(c), (Vec3i)to.apply(c)), (BlockInput)block.apply(c), FillCommand.Mode.REPLACE, (Predicate)filter.apply(c), true)));
   }

   private static int fillBlocks(final CommandSourceStack source, final BoundingBox region, final BlockInput target, final FillCommand.Mode mode, final @Nullable Predicate predicate, final boolean strict) throws CommandSyntaxException {
      long area = (long)region.getXSpan() * (long)region.getYSpan() * (long)region.getZSpan();
      int limit = source.getLevel().getGameRules().get(GameRules.MAX_BLOCK_MODIFICATIONS);
      if (area > (long)limit) {
         throw ERROR_AREA_TOO_LARGE.create(limit, area);
      } else {
         List updatePositions = Lists.newArrayList();
         ServerLevel level = source.getLevel();
         if (level.isDebug()) {
            throw ERROR_FAILED.create();
         } else {
            int count = 0;

            record UpdatedPosition(BlockPos pos, BlockState oldState) {
            }

            for(BlockPos pos : BlockPos.betweenClosed(region.minX(), region.minY(), region.minZ(), region.maxX(), region.maxY(), region.maxZ())) {
               if (predicate == null || predicate.test(new BlockInWorld(level, pos, true))) {
                  BlockState oldState = level.getBlockState(pos);
                  boolean affected = false;
                  if (mode.affector.affect(level, pos)) {
                     affected = true;
                  }

                  BlockInput block = mode.filter.filter(region, pos, target, level);
                  if (block == null) {
                     if (affected) {
                        ++count;
                     }
                  } else if (!block.place(level, pos, 2 | (strict ? 816 : 256))) {
                     if (affected) {
                        ++count;
                     }
                  } else {
                     if (!strict) {
                        updatePositions.add(new UpdatedPosition(pos.immutable(), oldState));
                     }

                     ++count;
                  }
               }
            }

            for(UpdatedPosition pos : updatePositions) {
               level.updateNeighboursOnBlockSet(pos.pos, pos.oldState);
            }

            if (count == 0) {
               throw ERROR_FAILED.create();
            } else {
               int finalCount = count;
               source.sendSuccess(() -> Component.translatable("commands.fill.success", finalCount), true);
               return count;
            }
         }
      }
   }

   @FunctionalInterface
   public interface Affector {
      FillCommand.Affector NOOP = (l, p) -> false;

      boolean affect(final ServerLevel level, final BlockPos pos);
   }

   @FunctionalInterface
   public interface Filter {
      FillCommand.Filter NOOP = (r, p, b, l) -> b;

      @Nullable BlockInput filter(final BoundingBox region, final BlockPos pos, final BlockInput block, final ServerLevel level);
   }

   private static enum Mode {
      REPLACE(FillCommand.Affector.NOOP, FillCommand.Filter.NOOP),
      OUTLINE(FillCommand.Affector.NOOP, (r, p, b, l) -> p.getX() != r.minX() && p.getX() != r.maxX() && p.getY() != r.minY() && p.getY() != r.maxY() && p.getZ() != r.minZ() && p.getZ() != r.maxZ() ? null : b),
      HOLLOW(FillCommand.Affector.NOOP, (r, p, b, l) -> p.getX() != r.minX() && p.getX() != r.maxX() && p.getY() != r.minY() && p.getY() != r.maxY() && p.getZ() != r.minZ() && p.getZ() != r.maxZ() ? FillCommand.HOLLOW_CORE : b),
      DESTROY((l, p) -> l.destroyBlock(p, true), FillCommand.Filter.NOOP);

      public final FillCommand.Filter filter;
      public final FillCommand.Affector affector;

      private Mode(final FillCommand.Affector affector, final FillCommand.Filter filter) {
         this.affector = affector;
         this.filter = filter;
      }

      // $FF: synthetic method
      private static FillCommand.Mode[] $values() {
         return new FillCommand.Mode[]{REPLACE, OUTLINE, HOLLOW, DESTROY};
      }
   }

   @FunctionalInterface
   private interface NullableCommandFunction {
      @Nullable Object apply(Object t) throws CommandSyntaxException;
   }
}
