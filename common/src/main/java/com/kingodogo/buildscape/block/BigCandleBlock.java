package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.DoubleSupplier;
import java.util.function.IntFunction;
import java.util.function.Supplier;
public class BigCandleBlock extends Block implements SimpleWaterloggedBlock, ICommonInteractable {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public static final int LIGHT_LEVEL = 12;

    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(5.5D, 0.0D, 5.5D, 10.5D, 7.0D, 10.5D),
            Block.box(5.25D, 7.0D, 5.25D, 10.75D, 7.5D, 10.75D),
            Block.box(7.625D, 7.0D, 8.0D, 8.375D, 10.0D, 8.0D)
    );

    private static final VoxelShape SHAPE_ON_CAKE = Shapes.or(
            Block.box(5.5D, 0.0D, 5.5D, 10.5D, 7.0D, 10.5D),
            Block.box(5.25D, 7.0D, 5.25D, 10.75D, 7.5D, 10.75D),
            Block.box(7.625D, 7.0D, 8.0D, 8.375D, 10.0D, 8.0D)
    );

    public BigCandleBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(LIT, false)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = context.getLevel().getBlockState(clickedPos);

        if (clickedState.is(Blocks.CAKE) && context.getClickedFace() == Direction.UP) {
            BlockPos placePos = clickedPos.above();
            if (context.getLevel().getBlockState(placePos).canBeReplaced(context)) {
                return this.defaultBlockState()
                        .setValue(LIT, false)
                        .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
            }
        }

        return this.defaultBlockState()
                .setValue(LIT, false)
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return true;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public InteractionResult onInteract(Level level, BlockPos pos, BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ItemStack heldItem = player.getItemInHand(hand);
        boolean currentlyLit = state.getValue(LIT);
        boolean isWaterlogged = state.getValue(WATERLOGGED);

        if (heldItem.is(Items.FLINT_AND_STEEL) || heldItem.is(Items.FIRE_CHARGE)) {
            if (!currentlyLit && !isWaterlogged) {
                level.setBlock(pos, state.setValue(LIT, true), 3);
                Services.PLATFORM.playFlintAndSteelSound(level, pos);
                return InteractionResult.SUCCESS;
            }
        } else if (heldItem.isEmpty() || heldItem.is(Items.WATER_BUCKET)) {
            if (currentlyLit) {
                level.setBlock(pos, state.setValue(LIT, false), 3);
                Services.PLATFORM.playCandleExtinguishSound(level, pos);
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    public void onAnimateTick(BlockState state, Level level, BlockPos pos, DoubleSupplier nextDouble, Supplier<Float> nextFloat, IntFunction<Integer> nextInt) {
        if (state.getValue(LIT)) {
            BlockState belowState = level.getBlockState(pos.below());
            boolean onCake = belowState.is(Blocks.CAKE);

            double wickX = pos.getX() + 0.5D;
            double wickY = pos.getY() + (onCake ? 0.125D : 0.625D);
            double wickZ = pos.getZ() + 0.5D;

            level.addParticle(
                    ParticleTypes.FLAME,
                    wickX + (nextDouble.getAsDouble() - 0.5D) * 0.05D,
                    wickY,
                    wickZ + (nextDouble.getAsDouble() - 0.5D) * 0.05D,
                    0.0D,
                    0.0D,
                    0.0D
            );

            if (nextInt.apply(10) == 0) {
                level.addParticle(
                        ParticleTypes.SMOKE,
                        wickX + (nextDouble.getAsDouble() - 0.5D) * 0.1D,
                        wickY,
                        wickZ + (nextDouble.getAsDouble() - 0.5D) * 0.1D,
                        0.0D,
                        0.05D,
                        0.0D
                );
            }

            if (level.isClientSide() && nextInt.apply(35) == 0) {
                Services.PLATFORM.playCandleAmbientLocalSound(
                        level,
                        pos.getX() + 0.5D,
                        pos.getY() + 0.5D,
                        pos.getZ() + 0.5D,
                        0.8F + nextFloat.get() * 0.2F,
                        0.8F + nextFloat.get() * 0.4F
                );
            }
        }
    }

    public void onRandomTick(BlockState state, ServerLevel level, BlockPos pos, IntFunction<Integer> nextInt, Supplier<Float> nextFloat) {
        if (state.getValue(LIT)) {
            if (nextInt.apply(35) == 0) {
                Services.PLATFORM.playCandleAmbientSound(
                        level,
                        pos,
                        0.8F + nextFloat.get() * 0.4F
                );
            }
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        BlockState belowState = level.getBlockState(pos.below());
        if (belowState.is(Blocks.CAKE)) {
            return SHAPE_ON_CAKE;
        }
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        return adjacentBlockState.is(this) || super.skipRendering(state, adjacentBlockState, side);
    }
}
