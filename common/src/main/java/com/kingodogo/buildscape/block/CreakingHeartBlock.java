package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
public class CreakingHeartBlock extends RotatedPillarBlock {
    public static final BooleanProperty ACTIVE = ModBlockProperties.ACTIVE;
    public static final int RESIN_COOLDOWN_TICKS = 1200;

    private final Supplier<Block> resinClumpSupplier;

    public CreakingHeartBlock(BlockBehaviour.Properties properties, Supplier<Block> resinClumpSupplier) {
        super(properties);
        this.resinClumpSupplier = resinClumpSupplier;
        this.registerDefaultState(this.defaultBlockState()
                .setValue(AXIS, Direction.Axis.Y)
                .setValue(ACTIVE, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ACTIVE);
    }

    public InteractionResult onInteract(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand) {
        boolean emptyHand = player.getItemInHand(hand).isEmpty();
        if (!emptyHand || level.isClientSide()) {
            return resinInteractionResult(emptyHand, 0, level.isClientSide());
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
        if (!emptyHand || (!clientSide && spawned == 0)) return InteractionResult.PASS;
        return clientSide ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    }

    public int spawnResinOnHeart(Level level, BlockPos pos) {
        Block resinClump = resinClumpSupplier != null ? resinClumpSupplier.get() : null;
        if (resinClump == null || resinClump == Blocks.AIR) return 0;

        List<Direction> directions = new ArrayList<>(List.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN));
        Collections.shuffle(directions);

        int spawned = 0;
        for (Direction dir : directions) {
            BlockPos adjPos = pos.relative(dir);
            Direction attachFace = dir.getOpposite();
            BooleanProperty faceProp = MultifaceBlock.getFaceProperty(attachFace);
            BlockState adjState = level.getBlockState(adjPos);

            if (adjState.is(resinClump)) {
                if (adjState.hasProperty(faceProp) && !adjState.getValue(faceProp)
                        && level.setBlock(adjPos, adjState.setValue(faceProp, true), 3)) {
                    spawned++;
                }
            } else if (adjState.isAir()) {
                BlockState newState = resinClump.defaultBlockState().setValue(faceProp, true);
                if (level.setBlock(adjPos, newState, 3)) spawned++;
            }

            if (spawned >= 2) break;
        }
        return spawned;
    }

    public void onScheduledTick(BlockState state, Level level, BlockPos pos) {
        if (state.getValue(ACTIVE)) {
            level.setBlock(pos, state.setValue(ACTIVE, false), 3);
        }
    }
}
