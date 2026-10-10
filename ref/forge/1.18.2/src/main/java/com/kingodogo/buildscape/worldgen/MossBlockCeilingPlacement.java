package com.kingodogo.buildscape.worldgen;

import com.mojang.serialization.Codec;

import java.util.Random;
import java.util.stream.Stream;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

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
        net.minecraft.world.level.WorldGenLevel level = context.getLevel();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int y = pos.getY(); y < pos.getY() + 64; y++) {
            mutablePos.set(pos.getX(), y, pos.getZ());

            if (
                    level
                            .getBlockState(mutablePos)
                            .is(net.minecraft.world.level.block.Blocks.MOSS_BLOCK)
            ) {
                BlockPos placePos = mutablePos.below();
                if (level.getBlockState(placePos).isAir()) {
                    net.minecraft.world.level.block.state.BlockState mossState =
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
