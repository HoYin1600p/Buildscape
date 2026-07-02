package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Random;
import java.util.function.Supplier;

public class ColoredMossBlock extends ModBlock implements BonemealableBlock {

    private final Supplier<Block> carpet;
    private final Supplier<Block> overlay;
    private final Supplier<Block> layers;

    public ColoredMossBlock(Properties properties, Supplier<Block> carpet, Supplier<Block> overlay, Supplier<Block> layers) {
        super(properties);
        this.carpet = carpet;
        this.overlay = overlay;
        this.layers = layers;
    }

    @Override
    public InteractionResult use(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.getItem() instanceof BoneMealItem) {
            if (this.isValidBonemealTarget(level, pos, state, level.isClientSide)) {
                if (level instanceof ServerLevel) {
                    if (this.isBonemealSuccess(level, level.getRandom(), pos, state)) {
                        this.performBonemeal(
                                (ServerLevel) level,
                                level.getRandom(),
                                pos,
                                state
                        );
                        if (!player.getAbilities().instabuild) {
                            itemStack.shrink(1);
                        }
                        level.levelEvent(2005, pos, 0);
                    }
                }
                level.playSound(
                        player,
                        pos,
                        net.minecraft.sounds.SoundEvents.BONE_MEAL_USE,
                        net.minecraft.sounds.SoundSource.BLOCKS,
                        1.0f,
                        1.0f
                );
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
        // Can bonemeal if there is air or replaceable block above, or if any replaceable block is nearby
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, Random random, BlockPos pos, BlockState state) {
        int radius = 3;
        int spreadAttempts = 40;
        
        for (int i = 0; i < spreadAttempts; i++) {
            int offsetX = random.nextInt(radius * 2 + 1) - radius;
            int offsetY = random.nextInt(3) - 1; // vertical spread of -1 to 1
            int offsetZ = random.nextInt(radius * 2 + 1) - radius;
            
            BlockPos targetPos = pos.offset(offsetX, offsetY, offsetZ);
            BlockState targetState = level.getBlockState(targetPos);
            
            if (targetState.is(BlockTags.MOSS_REPLACEABLE)) {
                // Change the block to this colored moss block
                level.setBlock(targetPos, this.defaultBlockState(), 3);
                
                // Try to spawn carpet/overlay/layers on top
                BlockPos abovePos = targetPos.above();
                BlockState aboveState = level.getBlockState(abovePos);
                
                if (aboveState.isAir() || aboveState.getMaterial().isReplaceable()) {
                    int rand = random.nextInt(100);
                    if (rand < 25) { // 25% chance to place carpet
                        BlockState carpetState = carpet.get().defaultBlockState();
                        if (carpetState.canSurvive(level, abovePos)) {
                            level.setBlock(abovePos, carpetState, 3);
                        }
                    } else if (rand < 35) { // 10% chance to place overlay
                        BlockState overlayState = overlay.get().defaultBlockState();
                        if (overlayState.canSurvive(level, abovePos)) {
                            level.setBlock(abovePos, overlayState, 3);
                        }
                    } else if (rand < 45) { // 10% chance to place layers (1 layer)
                        BlockState layersState = layers.get().defaultBlockState();
                        if (layersState.canSurvive(level, abovePos)) {
                            level.setBlock(abovePos, layersState, 3);
                        }
                    } else if (rand < 60) { // 15% chance to place poplar sapling
                        BlockState saplingState = ModBlocks.POPLAR_SAPLING.get().defaultBlockState();
                        if (saplingState.canSurvive(level, abovePos)) {
                            level.setBlock(abovePos, saplingState, 3);
                        }
                    }
                }
            }
        }
    }
}
