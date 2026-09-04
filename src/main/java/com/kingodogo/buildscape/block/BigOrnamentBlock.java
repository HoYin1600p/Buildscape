package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;

public class BigOrnamentBlock extends AbstractGlassBlock implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    private final boolean isTinted;

    public BigOrnamentBlock(BlockBehaviour.Properties properties) {
        this(properties, false);
    }

    public BigOrnamentBlock(BlockBehaviour.Properties properties, boolean isTinted) {
        super(properties);
        this.isTinted = isTinted;
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(LIT, true)
                        .setValue(WATERLOGGED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState()
                .setValue(LIT, true)
                .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED)
                ? Fluids.WATER.getSource(false)
                : super.getFluidState(state);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        if (!state.getValue(LIT)) {
            return 0;
        }
        return this.isTinted ? 4 : 15;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        if (this.isTinted) {
            if (level instanceof Level) {
                try {
                    boolean isBeacon = StackWalker.getInstance().walk(s ->
                            s.limit(20).anyMatch(f -> {
                                String name = f.getClassName();
                                return name.contains("Beacon") || name.contains("beacon");
                            })
                    );
                    if (isBeacon) {
                        return 15;
                    }
                } catch (Throwable ignore) {
                }
            }
            return 0;
        }
        return super.getLightBlock(state, level, pos);
    }

    @Override
    public float[] getBeaconColorMultiplier(
            BlockState state,
            LevelReader level,
            BlockPos pos,
            BlockPos beaconPos
    ) {
        if (this.isTinted) {
            return null;
        }
        int color = state.getMapColor(level, pos).col;
        return new float[]{
                ((color >> 16) & 0xFF) / 255.0f,
                ((color >> 8) & 0xFF) / 255.0f,
                (color & 0xFF) / 255.0f
        };
    }

    @Override
    public boolean skipRendering(
            BlockState state,
            BlockState adjacentBlockState,
            Direction side
    ) {
        return adjacentBlockState.is(this) || super.skipRendering(state, adjacentBlockState, side);
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
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        boolean currentLit = state.getValue(LIT);
        BlockState newState = state.setValue(LIT, !currentLit);
        level.setBlock(pos, newState, 3);

        level.playSound(
                null,
                pos,
                currentLit ? SoundEvents.STONE_BUTTON_CLICK_OFF : SoundEvents.STONE_BUTTON_CLICK_ON,
                SoundSource.BLOCKS,
                0.3f,
                1.0f
        );

        return InteractionResult.CONSUME;
    }
}
