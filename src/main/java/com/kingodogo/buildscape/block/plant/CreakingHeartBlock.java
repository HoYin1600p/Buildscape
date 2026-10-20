package com.kingodogo.buildscape.block.plant;

import com.kingodogo.buildscape.registry.blocks.CopperBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GlowLichenBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import com.kingodogo.buildscape.block.base.ModBlockProperties;

public class CreakingHeartBlock extends RotatedPillarBlock {
    public static final BooleanProperty ACTIVE = ModBlockProperties.ACTIVE;

    public CreakingHeartBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(AXIS, Direction.Axis.Y)
                .setValue(ACTIVE, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ACTIVE);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
            BlockHitResult hitResult) {
        boolean emptyHand = player.getItemInHand(hand).isEmpty();
        if (!emptyHand || level.isClientSide) {
            return resinInteractionResult(emptyHand, 0, level.isClientSide);
        }

        if (state.getValue(ACTIVE)) {
            level.setBlock(pos, state.setValue(ACTIVE, false), 3);
        }

        int spawned = spawnResinOnHeart(level, pos);
        if (spawned > 0) {
            level.playSound(null, pos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.levelEvent(2005, pos, 0);
        }

        return resinInteractionResult(emptyHand, spawned, false);
    }

    static InteractionResult resinInteractionResult(boolean emptyHand, int spawned, boolean clientSide) {
        if (!emptyHand || (!clientSide && spawned == 0)) {
            return InteractionResult.PASS;
        }
        return InteractionResult.sidedSuccess(clientSide);
    }

    private int spawnResinOnHeart(Level level, BlockPos pos) {
        List<Direction> directions = new ArrayList<>(List.of(Direction.NORTH, Direction.SOUTH, Direction.EAST,
                Direction.WEST, Direction.UP, Direction.DOWN));
        Collections.shuffle(directions, level.random);

        int spawned = 0;
        for (Direction dir : directions) {
            BlockPos adjPos = pos.relative(dir);
            Direction attachFace = dir.getOpposite();
            BooleanProperty faceProp = GlowLichenBlock.getFaceProperty(attachFace);
            BlockState adjState = level.getBlockState(adjPos);

            if (adjState.is(CopperBlocks.RESIN_CLUMP.get())) {
                if (!adjState.getValue(faceProp) && level.setBlock(adjPos, adjState.setValue(faceProp, true), 3)) {
                    spawned++;
                }
            } else if (adjState.isAir()) {
                BlockState newState = CopperBlocks.RESIN_CLUMP.get().defaultBlockState().setValue(faceProp, true);
                if (level.setBlock(adjPos, newState, 3)) {
                    spawned++;
                }
            }

            if (spawned >= 2) {
                break;
            }
        }
        return spawned;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        if (state.getValue(ACTIVE)) {
            level.setBlock(pos, state.setValue(ACTIVE, false), 3);
        }
    }
}
