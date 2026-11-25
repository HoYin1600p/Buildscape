package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.ComponentHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
public class BoneDiceBlock extends Block implements ICommonInteractable, ICommonAnalogOutput {

    public static final IntegerProperty ROLL = IntegerProperty.create("roll", 1, 6);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    public BoneDiceBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(ROLL, 1)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ROLL, FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        int roll = 1 + context.getLevel().getRandom().nextInt(6);
        Direction facing = Direction.Plane.HORIZONTAL.getRandomDirection(context.getLevel().getRandom());
        return this.defaultBlockState().setValue(ROLL, roll).setValue(FACING, facing);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        int roll = state.getValue(ROLL);

        if (!level.isClientSide()) {
            if (placer instanceof Player player) {
                Services.PLATFORM.sendActionBarMessage(
                        player,
                        ComponentHelper.translatable("message.buildscape.bone_dice.rolled", roll)
                                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
                );
            }
            if (level instanceof ServerLevel serverLevel) {
                spawnHighlightParticles(serverLevel, pos);
            }
        }

        Services.PLATFORM.playBoneDiceSet(level, pos);
    }

    @Override
    public InteractionResult onInteract(Level level, BlockPos pos, BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }
        ItemStack heldItem = player.getItemInHand(hand);
        if (heldItem.isEmpty()) {
            return rollDice(state, level, pos, player);
        }
        return InteractionResult.PASS;
    }

    private InteractionResult rollDice(BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide()) {
            int newRoll = 1 + level.getRandom().nextInt(6);
            Direction newFacing = Direction.Plane.HORIZONTAL.getRandomDirection(level.getRandom());
            BlockState newState = state.setValue(ROLL, newRoll).setValue(FACING, newFacing);
            level.setBlock(pos, newState, 3);

            Services.PLATFORM.sendActionBarMessage(
                    player,
                    ComponentHelper.translatable("message.buildscape.bone_dice.rolled", newRoll)
                            .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
            );

            if (level instanceof ServerLevel serverLevel) {
                spawnHighlightParticles(serverLevel, pos);
            }
            Services.PLATFORM.playBoneDiceRoll(level, pos);
        }

        return Services.PLATFORM.sidedSuccess(level.isClientSide());
    }

    public static void spawnHighlightParticles(ServerLevel level, BlockPos pos) {
        double y = pos.getY() + 1.02D;
        double minX = pos.getX() + 0.15D;
        double maxX = pos.getX() + 0.85D;
        double minZ = pos.getZ() + 0.15D;
        double maxZ = pos.getZ() + 0.85D;

        for (int i = 0; i <= 4; i++) {
            double f = i / 4.0D;
            level.sendParticles(ParticleTypes.WAX_ON, minX + f * (maxX - minX), y, minZ, 1, 0, 0, 0, 0);
            level.sendParticles(ParticleTypes.WAX_ON, minX + f * (maxX - minX), y, maxZ, 1, 0, 0, 0, 0);
            if (i > 0 && i < 4) {
                level.sendParticles(ParticleTypes.WAX_ON, minX, y, minZ + f * (maxZ - minZ), 1, 0, 0, 0, 0);
                level.sendParticles(ParticleTypes.WAX_ON, maxX, y, minZ + f * (maxZ - minZ), 1, 0, 0, 0, 0);
            }
        }

        level.sendParticles(ParticleTypes.GLOW, pos.getX() + 0.5D, y + 0.05D, pos.getZ() + 0.5D, 6, 0.15D, 0.02D, 0.15D, 0.02D);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
        return state.getValue(ROLL);
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
        return state.getValue(ROLL);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutput(BlockState state, Level level, BlockPos pos) {
        return state.getValue(ROLL);
    }
}
