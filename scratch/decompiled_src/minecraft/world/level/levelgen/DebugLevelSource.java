package net.minecraft.world.level.levelgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.jspecify.annotations.Nullable;

public class DebugLevelSource extends ChunkGenerator {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(RegistryOps.retrieveElement(Biomes.PLAINS)).apply(i, i.stable(DebugLevelSource::new)));
   private static final int BLOCK_MARGIN = 2;
   private static final List ALL_BLOCKS = (List)StreamSupport.stream(BuiltInRegistries.BLOCK.spliterator(), false).flatMap((b) -> b.getStateDefinition().getPossibleStates().stream()).collect(Collectors.toList());
   private static final int GRID_WIDTH = Mth.ceil(Mth.sqrt((float)ALL_BLOCKS.size()));
   private static final int GRID_HEIGHT = Mth.ceil((float)ALL_BLOCKS.size() / (float)GRID_WIDTH);
   protected static final BlockState AIR = Blocks.AIR.defaultBlockState();
   protected static final BlockState BARRIER = Blocks.BARRIER.defaultBlockState();
   public static final int HEIGHT = 70;
   public static final int BARRIER_HEIGHT = 60;

   public DebugLevelSource(final Holder.Reference plains) {
      super(new FixedBiomeSource(plains));
   }

   protected MapCodec codec() {
      return CODEC;
   }

   public void buildSurface(final StructureManager structureManager, final RandomState randomState, final ChunkAccess protoChunk, final BiomeManager biomeManager, final Blender blender, final Set possibleBiomes) {
   }

   public void applyBiomeDecoration(final WorldGenLevel level, final ChunkAccess chunk, final StructureManager structureManager) {
      BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
      ChunkPos centerPos = chunk.getPos();
      int chunkX = centerPos.x();
      int chunkZ = centerPos.z();

      for(int x = 0; x < 16; ++x) {
         for(int z = 0; z < 16; ++z) {
            int worldX = SectionPos.sectionToBlockCoord(chunkX, x);
            int worldZ = SectionPos.sectionToBlockCoord(chunkZ, z);
            level.setBlock(blockPos.set(worldX, 60, worldZ), BARRIER, 2);
            BlockState state = getBlockStateFor(worldX, worldZ);
            level.setBlock(blockPos.set(worldX, 70, worldZ), state, 2);
         }
      }

   }

   public CompletableFuture fillFromNoise(final Blender blender, final RandomState randomState, final StructureManager structureManager, final ChunkAccess centerChunk) {
      return CompletableFuture.completedFuture(centerChunk);
   }

   public int getBaseHeight(final int x, final int z, final Heightmap.Types type, final LevelHeightAccessor heightAccessor, final RandomState randomState) {
      return 0;
   }

   public NoiseColumn getBaseColumn(final int x, final int z, final LevelHeightAccessor heightAccessor, final RandomState randomState) {
      return new NoiseColumn(0, new BlockState[0]);
   }

   public void addDebugScreenInfo(final List result, final RandomState randomState, final BlockPos feetPos) {
   }

   public static BlockState getBlockStateFor(int worldX, int worldZ) {
      BlockState state = AIR;
      if (worldX > 0 && worldZ > 0 && worldX % 2 != 0 && worldZ % 2 != 0) {
         worldX /= 2;
         worldZ /= 2;
         if (worldX <= GRID_WIDTH && worldZ <= GRID_HEIGHT) {
            int index = Mth.abs(worldX * GRID_WIDTH + worldZ);
            if (index < ALL_BLOCKS.size()) {
               state = (BlockState)ALL_BLOCKS.get(index);
            }
         }
      }

      return state;
   }

   public void applyCarvers(final @Nullable WorldGenRegion biomeRegion, final RandomState randomState, final BiomeManager biomeManager, final StructureManager structureManager, final ChunkAccess chunk, final Blender blender) {
   }

   public void spawnOriginalMobs(final WorldGenRegion worldGenRegion) {
   }

   public int getMinY() {
      return 0;
   }

   public int getGenDepth() {
      return 384;
   }

   public int getSeaLevel() {
      return 63;
   }
}
