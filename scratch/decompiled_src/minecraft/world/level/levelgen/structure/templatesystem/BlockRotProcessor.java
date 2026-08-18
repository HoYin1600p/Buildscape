package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

public class BlockRotProcessor implements StructureProcessor {
   public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(RegistryCodecs.holderSet(Registries.BLOCK).optionalFieldOf("rottable_blocks").forGetter((t) -> t.rottableBlocks), Codec.floatRange(0.0F, 1.0F).fieldOf("integrity").forGetter((t) -> t.integrity)).apply(i, BlockRotProcessor::new));
   private final Optional rottableBlocks;
   private final float integrity;

   public BlockRotProcessor(final HolderSet tag, final float integrity) {
      this(Optional.of(tag), integrity);
   }

   public BlockRotProcessor(final float integrity) {
      this(Optional.empty(), integrity);
   }

   private BlockRotProcessor(final Optional blockTagKey, final float integrity) {
      this.integrity = integrity;
      this.rottableBlocks = blockTagKey;
   }

   public StructureTemplate.@Nullable StructureBlockInfo processBlock(final LevelReader level, final BlockPos targetPosition, final BlockPos referencePos, final BlockPos templateRelativePos, final StructureTemplate.StructureBlockInfo processedBlockInfo, final StructurePlaceSettings settings) {
      RandomSource random = settings.getRandom(processedBlockInfo.pos());
      return (!this.rottableBlocks.isPresent() || processedBlockInfo.state().is((HolderSet)this.rottableBlocks.get())) && !(random.nextFloat() <= this.integrity) ? null : processedBlockInfo;
   }

   public MapCodec codec() {
      return MAP_CODEC;
   }
}
