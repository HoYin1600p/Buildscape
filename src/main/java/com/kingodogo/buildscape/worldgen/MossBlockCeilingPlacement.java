package com.kingodogo.buildscape.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.Random;
import java.util.stream.Stream;

public class MossBlockCeilingPlacement extends PlacementModifier {

    public static final Codec<MossBlockCeilingPlacement> CODEC = Codec.unit(
            MossBlockCeilingPlacement::new
    );
    public static final MossBlockCeilingPlacement INSTANCE =
            new MossBlockCeilingPlacement();

    private MossBlockCeilingPlacement() {
    }

    @Override
    public Stream<BlockPos> getPositions(
            PlacementContext context,
            Random random,
            BlockPos pos
    ) {
        WorldGenLevel level = context.getLevel();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int y = pos.getY(); y < pos.getY() + 64; y++) {
            mutablePos.set(pos.getX(), y, pos.getZ());

            if (
                    level
                            .getBlockState(mutablePos)
                            .is(Blocks.MOSS_BLOCK)
            ) {
                BlockPos placePos = mutablePos.below();
                if (level.getBlockState(placePos).isAir()) {
                    BlockState mossState =
                            level.getBlockState(mutablePos);
                    if (mossState.isFaceSturdy(level, mutablePos, Direction.DOWN)) {
                        return Stream.of(placePos);
                    }
                }
            }
        }

        return Stream.empty();
    }

    @Override
    public PlacementModifierType<?> type() {
        return ModPlacementModifiers.MOSS_BLOCK_CEILING_PLACEMENT.get();
    }
}
