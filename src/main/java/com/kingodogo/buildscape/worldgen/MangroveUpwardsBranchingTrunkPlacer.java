package com.kingodogo.buildscape.worldgen;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

public class MangroveUpwardsBranchingTrunkPlacer extends TrunkPlacer {

    public static final Codec<MangroveUpwardsBranchingTrunkPlacer> CODEC =
            RecordCodecBuilder.create(instance ->
                    trunkPlacerParts(instance)
                            .and(
                                    IntProvider.codec(0, 24)
                                            .fieldOf("extra_branch_steps")
                                            .forGetter(placer -> placer.extraBranchSteps)
                            )
                            .and(
                                    Codec.floatRange(0.0F, 1.0F)
                                            .fieldOf("place_branch_per_log_probability")
                                            .forGetter(placer -> placer.placeBranchPerLogProbability)
                            )
                            .and(
                                    IntProvider.codec(0, 24)
                                            .fieldOf("extra_branch_length")
                                            .forGetter(placer -> placer.extraBranchLength)
                            )
                            .apply(instance, MangroveUpwardsBranchingTrunkPlacer::new)
            );

    private final IntProvider extraBranchSteps;
    private final IntProvider extraBranchLength;
    private final float placeBranchPerLogProbability;

    public MangroveUpwardsBranchingTrunkPlacer(
            int baseHeight,
            int heightRandA,
            int heightRandB,
            IntProvider extraBranchSteps,
            float placeBranchPerLogProbability,
            IntProvider extraBranchLength
    ) {
        super(baseHeight, heightRandA, heightRandB);
        this.extraBranchSteps = extraBranchSteps;
        this.placeBranchPerLogProbability = placeBranchPerLogProbability;
        this.extraBranchLength = extraBranchLength;
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return ModTrunkPlacerTypes.MANGROVE_UPWARDS_BRANCHING.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(
            LevelSimulatedReader level,
            BiConsumer<BlockPos, BlockState> blockSetter,
            Random random,
            int freeTreeHeight,
            BlockPos pos,
            TreeConfiguration config
    ) {
        List<FoliagePlacer.FoliageAttachment> foliageAttachments =
                Lists.newArrayList();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        int elevationShift = 2 + random.nextInt(3);
        int elevatedStartY = pos.getY() + elevationShift;

        int trunkHeight = freeTreeHeight;
        int extraHeight = random.nextInt(4);
        trunkHeight += extraHeight;

        for (int i = 0; i < trunkHeight; i++) {
            int y = elevatedStartY + i;
            mutablePos.set(pos.getX(), y, pos.getZ());

            if (placeLog(level, blockSetter, random, mutablePos, config)) {
                if (i > 0 && random.nextFloat() < placeBranchPerLogProbability) {
                    this.placeBranch(
                            level,
                            blockSetter,
                            random,
                            mutablePos,
                            config,
                            foliageAttachments
                    );
                }
            }
        }

        foliageAttachments.add(
                new FoliagePlacer.FoliageAttachment(
                        mutablePos.set(pos.getX(), elevatedStartY + trunkHeight, pos.getZ()),
                        0,
                        false
                )
        );

        return foliageAttachments;
    }

    private void placeBranch(
            LevelSimulatedReader level,
            BiConsumer<BlockPos, BlockState> blockSetter,
            Random random,
            BlockPos.MutableBlockPos branchStart,
            TreeConfiguration config,
            List<FoliagePlacer.FoliageAttachment> foliageAttachments
    ) {
        int branchSteps = this.extraBranchSteps.sample(random);
        int branchLength = this.extraBranchLength.sample(random);

        if (branchSteps <= 0 || branchLength <= 0) {
            return;
        }

        Direction[] horizontalDirections = {
                Direction.NORTH,
                Direction.SOUTH,
                Direction.EAST,
                Direction.WEST,
        };
        Direction branchDirection =
                horizontalDirections[random.nextInt(horizontalDirections.length)];

        BlockPos.MutableBlockPos currentPos = branchStart.mutable();
        BlockPos.MutableBlockPos branchEnd = branchStart.mutable();

        for (int i = 0; i < branchLength; i++) {
            branchEnd.move(branchDirection);
            if (placeLog(level, blockSetter, random, branchEnd, config)) {
                currentPos.set(branchEnd);
            } else {
                break;
            }
        }

        for (int i = 0; i < branchSteps; i++) {
            branchEnd.move(Direction.UP);
            if (placeLog(level, blockSetter, random, branchEnd, config)) {
                currentPos.set(branchEnd);
                foliageAttachments.add(
                        new FoliagePlacer.FoliageAttachment(currentPos.immutable(), 0, false)
                );
            } else {
                break;
            }
        }
    }
}
