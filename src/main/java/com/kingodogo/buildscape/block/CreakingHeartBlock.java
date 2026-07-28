package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CreakingHeartBlock extends RotatedPillarBlock {
    public static final BooleanProperty ACTIVE = ModBlockProperties.ACTIVE;
    private static final Map<BlockPos, Long> COOLDOWN_MAP = new HashMap<>();

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
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            // Set blockstate to ACTIVE = true
            level.setBlock(pos, state.setValue(ACTIVE, true), 3);

            // Spawn resin clumps directly onto sides of Creaking Heart block
            spawnResinOnHeart(level, pos);

            // Play sound and particles
            level.playSound(null, pos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.levelEvent(2005, pos, 0);

            // Schedule tick to deactivate after 2 seconds (40 ticks)
            level.scheduleTick(pos, this, 40);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private void spawnResinOnHeart(Level level, BlockPos pos) {
        java.util.List<Direction> directions = new java.util.ArrayList<>(java.util.List.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN));
        java.util.Collections.shuffle(directions, level.random);

        int spawned = 0;
        for (Direction dir : directions) {
            BlockPos adjPos = pos.relative(dir);
            Direction attachFace = dir.getOpposite();
            BooleanProperty faceProp = net.minecraft.world.level.block.GlowLichenBlock.getFaceProperty(attachFace);
            BlockState adjState = level.getBlockState(adjPos);

            if (adjState.is(ModBlocks.RESIN_CLUMP.get())) {
                if (!adjState.getValue(faceProp)) {
                    level.setBlock(adjPos, adjState.setValue(faceProp, true), 3);
                    spawned++;
                }
            } else if (adjState.isAir() || adjState.getMaterial().isReplaceable()) {
                BlockState newState = ModBlocks.RESIN_CLUMP.get().defaultBlockState().setValue(faceProp, true);
                level.setBlock(adjPos, newState, 3);
                spawned++;
            }

            if (spawned >= 2) break;
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        if (state.getValue(ACTIVE)) {
            level.setBlock(pos, state.setValue(ACTIVE, false), 3);
        }
    }
}
