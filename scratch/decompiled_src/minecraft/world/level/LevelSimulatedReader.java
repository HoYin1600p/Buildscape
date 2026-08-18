package net.minecraft.world.level;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.Heightmap;

public interface LevelSimulatedReader {
   boolean isStateAtPosition(final BlockPos pos, final Predicate predicate);

   boolean isFluidAtPosition(final BlockPos pos, final Predicate predicate);

   Optional getBlockEntity(BlockPos pos, BlockEntityType type);

   BlockPos getHeightmapPos(final Heightmap.Types type, final BlockPos pos);
}
