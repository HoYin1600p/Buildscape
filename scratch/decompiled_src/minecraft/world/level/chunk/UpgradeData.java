package net.minecraft.world.level.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.CompositeDirection;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.ticks.SavedTick;
import org.slf4j.Logger;

public class UpgradeData {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final UpgradeData EMPTY = new UpgradeData(EmptyBlockGetter.INSTANCE);
   private static final String TAG_INDICES = "Indices";
   private static final CompositeDirection.Direction8[] DIRECTIONS = CompositeDirection.Direction8.values();
   private static final Codec BLOCK_TICKS_CODEC = SavedTick.codec(BuiltInRegistries.BLOCK.byNameCodec().orElse(Blocks.AIR)).listOf();
   private static final Codec FLUID_TICKS_CODEC = SavedTick.codec(BuiltInRegistries.FLUID.byNameCodec().orElse(Fluids.EMPTY)).listOf();
   private final EnumSet sides = EnumSet.noneOf(CompositeDirection.Direction8.class);
   private final List neighborBlockTicks = Lists.newArrayList();
   private final List neighborFluidTicks = Lists.newArrayList();
   private final int[][] index;
   private static final Map MAP = new IdentityHashMap();
   private static final Set CHUNKY_FIXERS = Sets.newHashSet();

   private UpgradeData(final LevelHeightAccessor levelHeightAccessor) {
      this.index = new int[levelHeightAccessor.getSectionsCount()][];
   }

   public UpgradeData(final CompoundTag tag, final LevelHeightAccessor levelHeightAccessor) {
      this(levelHeightAccessor);
      tag.getCompound("Indices").ifPresent((indicesTag) -> {
         for(int i = 0; i < this.index.length; ++i) {
            this.index[i] = (int[])indicesTag.getIntArray(String.valueOf(i)).orElse((Object)null);
         }

      });
      int sideInt = tag.getIntOr("Sides", 0);

      for(CompositeDirection.Direction8 direction8 : CompositeDirection.Direction8.values()) {
         if ((sideInt & 1 << direction8.ordinal()) != 0) {
            this.sides.add(direction8);
         }
      }

      tag.read("neighbor_block_ticks", BLOCK_TICKS_CODEC).ifPresent(this.neighborBlockTicks::addAll);
      tag.read("neighbor_fluid_ticks", FLUID_TICKS_CODEC).ifPresent(this.neighborFluidTicks::addAll);
   }

   private UpgradeData(final UpgradeData source) {
      this.sides.addAll(source.sides);
      this.neighborBlockTicks.addAll(source.neighborBlockTicks);
      this.neighborFluidTicks.addAll(source.neighborFluidTicks);
      this.index = new int[source.index.length][];

      for(int i = 0; i < source.index.length; ++i) {
         int[] indices = source.index[i];
         this.index[i] = indices != null ? IntArrays.copy(indices) : null;
      }

   }

   public void upgrade(final LevelChunk chunk) {
      this.upgradeInside(chunk);

      for(CompositeDirection.Direction8 direction8 : DIRECTIONS) {
         upgradeSides(chunk, direction8);
      }

      Level level = chunk.getLevel();
      this.neighborBlockTicks.forEach((tick) -> {
         Block type = tick.type() == Blocks.AIR ? level.getBlockState(tick.pos()).getBlock() : (Block)tick.type();
         level.scheduleTick(tick.pos(), type, tick.delay(), tick.priority());
      });
      this.neighborFluidTicks.forEach((tick) -> {
         Fluid type = tick.type() == Fluids.EMPTY ? level.getFluidState(tick.pos()).getType() : (Fluid)tick.type();
         level.scheduleTick(tick.pos(), type, tick.delay(), tick.priority());
      });
      CHUNKY_FIXERS.forEach((fixer) -> fixer.processChunk(level));
   }

   private static void upgradeSides(final LevelChunk chunk, final CompositeDirection.Direction8 direction8) {
      Level level = chunk.getLevel();
      if (chunk.getUpgradeData().sides.remove(direction8)) {
         Set directions = direction8.getDirections();
         int min = 0;
         int max = 15;
         boolean east = directions.contains(Direction.EAST);
         boolean west = directions.contains(Direction.WEST);
         boolean south = directions.contains(Direction.SOUTH);
         boolean north = directions.contains(Direction.NORTH);
         boolean singular = directions.size() == 1;
         ChunkPos chunkPos = chunk.getPos();
         int minX = chunkPos.getMinBlockX() + (!singular || !north && !south ? (west ? 0 : 15) : 1);
         int maxX = chunkPos.getMinBlockX() + (!singular || !north && !south ? (west ? 0 : 15) : 14);
         int minZ = chunkPos.getMinBlockZ() + (!singular || !east && !west ? (north ? 0 : 15) : 1);
         int maxZ = chunkPos.getMinBlockZ() + (!singular || !east && !west ? (north ? 0 : 15) : 14);
         Direction[] updateDirections = Direction.values();
         BlockPos.MutableBlockPos neighbourPos = new BlockPos.MutableBlockPos();

         for(BlockPos pos : BlockPos.betweenClosed(minX, level.getMinY(), minZ, maxX, level.getMaxY(), maxZ)) {
            BlockState state = level.getBlockState(pos);
            BlockState newState = state;

            for(Direction direction : updateDirections) {
               neighbourPos.setWithOffset(pos, direction);
               newState = updateState(newState, direction, level, pos, neighbourPos);
            }

            Block.updateOrDestroy(state, newState, level, pos, 18);
         }

      }
   }

   private static BlockState updateState(final BlockState state, final Direction direction, final LevelAccessor level, final BlockPos pos, final BlockPos neighbourPos) {
      return ((UpgradeData.BlockFixer)MAP.getOrDefault(state.getBlock(), UpgradeData.BlockFixers.DEFAULT)).updateShape(state, direction, level.getBlockState(neighbourPos), level, pos, neighbourPos);
   }

   private void upgradeInside(final LevelChunk chunk) {
      BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
      BlockPos.MutableBlockPos neighbourPos = new BlockPos.MutableBlockPos();
      ChunkPos chunkPos = chunk.getPos();
      LevelAccessor level = chunk.getLevel();

      for(int sectionIndex = 0; sectionIndex < this.index.length; ++sectionIndex) {
         LevelChunkSection chunkSection = chunk.getSection(sectionIndex);
         int[] upgradeIndex = this.index[sectionIndex];
         this.index[sectionIndex] = null;
         if (upgradeIndex != null && upgradeIndex.length > 0) {
            Direction[] directions = Direction.values();
            PalettedContainer states = chunkSection.getStates();
            int sectionY = chunk.getSectionYFromSectionIndex(sectionIndex);
            int bottomYInSection = SectionPos.sectionToBlockCoord(sectionY);

            for(int coordinate : upgradeIndex) {
               int x = coordinate & 15;
               int y = coordinate >> 8 & 15;
               int z = coordinate >> 4 & 15;
               pos.set(chunkPos.getMinBlockX() + x, bottomYInSection + y, chunkPos.getMinBlockZ() + z);
               BlockState state = (BlockState)states.get(coordinate);
               BlockState newState = state;

               for(Direction direction : directions) {
                  neighbourPos.setWithOffset(pos, direction);
                  if (SectionPos.blockToSectionCoord(pos.getX()) == chunkPos.x() && SectionPos.blockToSectionCoord(pos.getZ()) == chunkPos.z()) {
                     newState = updateState(newState, direction, level, pos, neighbourPos);
                  }
               }

               Block.updateOrDestroy(state, newState, level, pos, 18);
            }
         }
      }

      for(int i = 0; i < this.index.length; ++i) {
         if (this.index[i] != null) {
            LOGGER.warn("Discarding update data for section {} for chunk ({} {})", new Object[]{level.getSectionYFromSectionIndex(i), chunkPos.x(), chunkPos.z()});
         }

         this.index[i] = null;
      }

   }

   public boolean isEmpty() {
      for(int[] ints : this.index) {
         if (ints != null) {
            return false;
         }
      }

      return this.sides.isEmpty();
   }

   public CompoundTag write() {
      CompoundTag tag = new CompoundTag();
      CompoundTag indicesTag = new CompoundTag();

      for(int i = 0; i < this.index.length; ++i) {
         String key = String.valueOf(i);
         if (this.index[i] != null && this.index[i].length != 0) {
            indicesTag.putIntArray(key, this.index[i]);
         }
      }

      if (!indicesTag.isEmpty()) {
         tag.put("Indices", indicesTag);
      }

      int sides = 0;

      for(CompositeDirection.Direction8 side : this.sides) {
         sides |= 1 << side.ordinal();
      }

      tag.putByte("Sides", (byte)sides);
      if (!this.neighborBlockTicks.isEmpty()) {
         tag.store("neighbor_block_ticks", BLOCK_TICKS_CODEC, this.neighborBlockTicks);
      }

      if (!this.neighborFluidTicks.isEmpty()) {
         tag.store("neighbor_fluid_ticks", FLUID_TICKS_CODEC, this.neighborFluidTicks);
      }

      return tag;
   }

   public UpgradeData copy() {
      return this == EMPTY ? EMPTY : new UpgradeData(this);
   }

   public interface BlockFixer {
      BlockState updateShape(final BlockState state, final Direction direction, final BlockState neighbour, final LevelAccessor level, final BlockPos pos, final BlockPos neighbourPos);

      default void processChunk(final LevelAccessor level) {
      }
   }

   private static enum BlockFixers implements UpgradeData.BlockFixer {
      BLACKLIST(Blocks.OBSERVER, Blocks.NETHER_PORTAL, (Block)Blocks.CONCRETE_POWDER.white(), (Block)Blocks.CONCRETE_POWDER.orange(), (Block)Blocks.CONCRETE_POWDER.magenta(), (Block)Blocks.CONCRETE_POWDER.lightBlue(), (Block)Blocks.CONCRETE_POWDER.yellow(), (Block)Blocks.CONCRETE_POWDER.lime(), (Block)Blocks.CONCRETE_POWDER.pink(), (Block)Blocks.CONCRETE_POWDER.gray(), (Block)Blocks.CONCRETE_POWDER.lightGray(), (Block)Blocks.CONCRETE_POWDER.cyan(), (Block)Blocks.CONCRETE_POWDER.purple(), (Block)Blocks.CONCRETE_POWDER.blue(), (Block)Blocks.CONCRETE_POWDER.brown(), (Block)Blocks.CONCRETE_POWDER.green(), (Block)Blocks.CONCRETE_POWDER.red(), (Block)Blocks.CONCRETE_POWDER.black(), Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL, Blocks.DRAGON_EGG, Blocks.GRAVEL, Blocks.SAND, Blocks.RED_SAND, Blocks.OAK_SIGN, Blocks.SPRUCE_SIGN, Blocks.BIRCH_SIGN, Blocks.ACACIA_SIGN, Blocks.CHERRY_SIGN, Blocks.JUNGLE_SIGN, Blocks.DARK_OAK_SIGN, Blocks.PALE_OAK_SIGN, Blocks.POPLAR_SIGN, Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_WALL_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.ACACIA_WALL_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN, Blocks.PALE_OAK_WALL_SIGN, Blocks.POPLAR_WALL_SIGN, Blocks.OAK_HANGING_SIGN, Blocks.SPRUCE_HANGING_SIGN, Blocks.BIRCH_HANGING_SIGN, Blocks.ACACIA_HANGING_SIGN, Blocks.JUNGLE_HANGING_SIGN, Blocks.DARK_OAK_HANGING_SIGN, Blocks.PALE_OAK_HANGING_SIGN, Blocks.POPLAR_HANGING_SIGN, Blocks.OAK_WALL_HANGING_SIGN, Blocks.SPRUCE_WALL_HANGING_SIGN, Blocks.BIRCH_WALL_HANGING_SIGN, Blocks.ACACIA_WALL_HANGING_SIGN, Blocks.JUNGLE_WALL_HANGING_SIGN, Blocks.DARK_OAK_WALL_HANGING_SIGN, Blocks.PALE_OAK_WALL_HANGING_SIGN, Blocks.POPLAR_WALL_HANGING_SIGN) {
         public BlockState updateShape(final BlockState state, final Direction direction, final BlockState neighbour, final LevelAccessor level, final BlockPos pos, final BlockPos neighbourPos) {
            return state;
         }
      },
      DEFAULT {
         public BlockState updateShape(final BlockState state, final Direction direction, final BlockState neighbour, final LevelAccessor level, final BlockPos pos, final BlockPos neighbourPos) {
            return state.updateShape(level, level, pos, direction, neighbourPos, level.getBlockState(neighbourPos), level.getRandom());
         }
      },
      CHEST(Blocks.CHEST, Blocks.TRAPPED_CHEST) {
         public BlockState updateShape(final BlockState state, final Direction direction, final BlockState neighbour, final LevelAccessor level, final BlockPos pos, final BlockPos neighbourPos) {
            if (neighbour.is(state.getBlock()) && direction.getAxis().isHorizontal() && state.getValue(ChestBlock.TYPE) == ChestType.SINGLE && neighbour.getValue(ChestBlock.TYPE) == ChestType.SINGLE) {
               Direction facing = (Direction)state.getValue(ChestBlock.FACING);
               if (direction.getAxis() != facing.getAxis() && facing == neighbour.getValue(ChestBlock.FACING)) {
                  ChestType newType = direction == facing.getClockWise() ? ChestType.LEFT : ChestType.RIGHT;
                  level.setBlock(neighbourPos, (BlockState)neighbour.setValue(ChestBlock.TYPE, newType.getOpposite()), 18);
                  if (facing == Direction.NORTH || facing == Direction.EAST) {
                     BlockEntity one = level.getBlockEntity(pos);
                     BlockEntity two = level.getBlockEntity(neighbourPos);
                     if (one instanceof ChestBlockEntity) {
                        ChestBlockEntity firstChestBlockEntity = (ChestBlockEntity)one;
                        if (two instanceof ChestBlockEntity) {
                           ChestBlockEntity secondChestBlockEntity = (ChestBlockEntity)two;
                           ChestBlockEntity.swapContents(firstChestBlockEntity, secondChestBlockEntity);
                        }
                     }
                  }

                  return (BlockState)state.setValue(ChestBlock.TYPE, newType);
               }
            }

            return state;
         }
      },
      LEAVES(true, Blocks.ACACIA_LEAVES, Blocks.CHERRY_LEAVES, Blocks.BIRCH_LEAVES, Blocks.PALE_OAK_LEAVES, Blocks.RED_POPLAR_LEAVES, Blocks.ORANGE_POPLAR_LEAVES, Blocks.YELLOW_POPLAR_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES) {
         private final ThreadLocal queue = ThreadLocal.withInitial(() -> Lists.newArrayListWithCapacity(7));

         public BlockState updateShape(final BlockState state, final Direction direction, final BlockState neighbour, final LevelAccessor level, final BlockPos pos, final BlockPos neighbourPos) {
            BlockState newState = state.updateShape(level, level, pos, direction, neighbourPos, level.getBlockState(neighbourPos), level.getRandom());
            if (state != newState) {
               int distance = newState.getValue(BlockStateProperties.DISTANCE);
               List queue = (List)this.queue.get();
               if (queue.isEmpty()) {
                  for(int i = 0; i < 7; ++i) {
                     queue.add(new ObjectOpenHashSet());
                  }
               }

               ((ObjectSet)queue.get(distance)).add(pos.immutable());
            }

            return state;
         }

         public void processChunk(final LevelAccessor level) {
            BlockPos.MutableBlockPos neighborPos = new BlockPos.MutableBlockPos();
            List queue = (List)this.queue.get();

            for(int neighborDistance = 2; neighborDistance < queue.size(); ++neighborDistance) {
               int currentDistance = neighborDistance - 1;
               ObjectSet set = (ObjectSet)queue.get(currentDistance);
               ObjectSet newSet = (ObjectSet)queue.get(neighborDistance);
               ObjectIterator var8 = set.iterator();

               while(var8.hasNext()) {
                  BlockPos pos = (BlockPos)var8.next();
                  BlockState state = level.getBlockState(pos);
                  if (state.getValue(BlockStateProperties.DISTANCE) >= currentDistance) {
                     level.setBlock(pos, (BlockState)state.setValue(BlockStateProperties.DISTANCE, Integer.valueOf(currentDistance)), 18);
                     if (neighborDistance != 7) {
                        for(Direction direction : DIRECTIONS) {
                           neighborPos.setWithOffset(pos, direction);
                           BlockState neighbor = level.getBlockState(neighborPos);
                           if (neighbor.hasProperty(BlockStateProperties.DISTANCE) && state.getValue(BlockStateProperties.DISTANCE) > neighborDistance) {
                              newSet.add(neighborPos.immutable());
                           }
                        }
                     }
                  }
               }
            }

            queue.clear();
         }
      },
      STEM_BLOCK(Blocks.MELON_STEM, Blocks.PUMPKIN_STEM) {
         public BlockState updateShape(final BlockState state, final Direction direction, final BlockState neighbour, final LevelAccessor level, final BlockPos pos, final BlockPos neighbourPos) {
            if (state.getValue(StemBlock.AGE) == 7) {
               Block fruit = state.is(Blocks.PUMPKIN_STEM) ? Blocks.PUMPKIN : Blocks.MELON;
               if (neighbour.is(fruit)) {
                  return (BlockState)(state.is(Blocks.PUMPKIN_STEM) ? Blocks.ATTACHED_PUMPKIN_STEM : Blocks.ATTACHED_MELON_STEM).defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, direction);
               }
            }

            return state;
         }
      };

      public static final Direction[] DIRECTIONS = Direction.values();

      private BlockFixers(final Block... blocks) {
         this(false, blocks);
      }

      private BlockFixers(final boolean chunky, final Block... blocks) {
         for(Block block : blocks) {
            UpgradeData.MAP.put(block, this);
         }

         if (chunky) {
            UpgradeData.CHUNKY_FIXERS.add(this);
         }

      }

      // $FF: synthetic method
      private static UpgradeData.BlockFixers[] $values() {
         return new UpgradeData.BlockFixers[]{BLACKLIST, DEFAULT, CHEST, LEAVES, STEM_BLOCK};
      }
   }
}
