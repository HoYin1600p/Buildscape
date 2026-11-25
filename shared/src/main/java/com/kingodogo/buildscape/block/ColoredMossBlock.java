package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;
public class ColoredMossBlock extends ModBlock {

    private final Supplier<Block> carpet;
    private final Supplier<Block> overlay;
    private final Supplier<Block> layers;
    private final Supplier<Block> sapling;
    private final Supplier<Block> flower;

    public ColoredMossBlock(BlockBehaviour.Properties properties) {
        this(properties, null, null, null, null, null);
    }

    public ColoredMossBlock(BlockBehaviour.Properties properties, Supplier<Block> carpet, Supplier<Block> overlay, Supplier<Block> layers) {
        this(properties, carpet, overlay, layers, null, null);
    }

    public ColoredMossBlock(BlockBehaviour.Properties properties, Supplier<Block> carpet, Supplier<Block> overlay, Supplier<Block> layers, Supplier<Block> sapling, Supplier<Block> flower) {
        super(properties);
        this.carpet = carpet;
        this.overlay = overlay;
        this.layers = layers;
        this.sapling = sapling;
        this.flower = flower;
    }

    public InteractionResult onInteract(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (held.is(Items.BONE_MEAL)) {
            if (level instanceof ServerLevel serverLevel) {
                growMoss(serverLevel, pos, state, serverLevel.getRandom()::nextInt);
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }
                level.levelEvent(2005, pos, 0);
            }
            Services.PLATFORM.playBoneMealUse(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
            return Services.PLATFORM.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }

    public void growMoss(ServerLevel level, BlockPos pos, BlockState state, IntUnaryOperator nextInt) {
        int radius = 3;
        int spreadAttempts = 40;

        for (int i = 0; i < spreadAttempts; i++) {
            int offsetX = nextInt.applyAsInt(radius * 2 + 1) - radius;
            int offsetY = nextInt.applyAsInt(3) - 1;
            int offsetZ = nextInt.applyAsInt(radius * 2 + 1) - radius;

            BlockPos targetPos = pos.offset(offsetX, offsetY, offsetZ);
            BlockState targetState = level.getBlockState(targetPos);

            boolean isMoss = targetState.is(this);
            boolean isReplaceable = targetState.is(BlockTags.MOSS_REPLACEABLE);

            if (isMoss || isReplaceable) {
                if (isReplaceable) {
                    level.setBlock(targetPos, this.defaultBlockState(), 3);
                }

                BlockPos abovePos = targetPos.above();
                BlockState aboveState = level.getBlockState(abovePos);

                if (aboveState.isAir() || Services.PLATFORM.isReplaceable(aboveState)) {
                    int rand = nextInt.applyAsInt(100);
                    if (rand < 25) {
                        Block b = carpet != null ? carpet.get() : null;
                        if (b != null) {
                            BlockState carpetState = b.defaultBlockState();
                            if (carpetState.canSurvive(level, abovePos)) {
                                level.setBlock(abovePos, carpetState, 3);
                            }
                        }
                    } else if (rand < 35) {
                        Block b = overlay != null ? overlay.get() : null;
                        if (b != null) {
                            BlockState overlayState = b.defaultBlockState();
                            if (overlayState.canSurvive(level, abovePos)) {
                                level.setBlock(abovePos, overlayState, 3);
                            }
                        }
                    } else if (rand < 45) {
                        Block b = layers != null ? layers.get() : null;
                        if (b != null) {
                            BlockState layersState = b.defaultBlockState();
                            if (layersState.canSurvive(level, abovePos)) {
                                level.setBlock(abovePos, layersState, 3);
                            }
                        }
                    } else if (rand < 58) {
                        Block b = sapling != null ? sapling.get() : null;
                        if (b != null) {
                            BlockState saplingState = b.defaultBlockState();
                            if (saplingState.canSurvive(level, abovePos)) {
                                level.setBlock(abovePos, saplingState, 3);
                            }
                        }
                    } else if (rand < 70) {
                        Block b = flower != null ? flower.get() : null;
                        if (b != null) {
                            BlockState flowerState = b.defaultBlockState();
                            if (flowerState.canSurvive(level, abovePos)) {
                                level.setBlock(abovePos, flowerState, 3);
                            }
                        }
                    }
                }
            }
        }
    }
}
