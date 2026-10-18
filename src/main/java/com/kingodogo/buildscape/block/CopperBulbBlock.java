package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.block.copper.BuildscapeWeatheringCopper;

import com.kingodogo.buildscape.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public abstract class CopperBulbBlock extends Block {

    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    public static final BooleanProperty POWERED = BooleanProperty.create(
            "powered"
    );
    private final int lightLevel;

    public CopperBulbBlock(Properties properties, int lightLevel) {
        super(properties);
        this.lightLevel = lightLevel;
        this.registerDefaultState(
                this.stateDefinition.any().setValue(LIT, false).setValue(POWERED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(LIT, POWERED);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return CopperOxidationHandler.canOxidize(state);
    }

    @Override
    public void randomTick(BlockState state, net.minecraft.server.level.ServerLevel level, BlockPos pos, java.util.Random random) {
        if (this instanceof BuildscapeWeatheringCopper copper) {
            copper.onRandomTick(state, level, pos, random);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(LIT, false)
                .setValue(POWERED, false);
    }

    @Override
    public int getLightEmission(
            BlockState state,
            BlockGetter level,
            BlockPos pos
    ) {
        return state.getValue(LIT) ? lightLevel : 0;
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return false;
    }

    @Override
    public int getSignal(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            Direction direction
    ) {
        return 0;
    }

    @Override
    public boolean canConnectRedstone(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            Direction direction
    ) {
        return true;
    }

    @Override
    public void neighborChanged(
            BlockState state,
            net.minecraft.world.level.Level level,
            BlockPos pos,
            Block block,
            BlockPos fromPos,
            boolean isMoving
    ) {
        if (!level.isClientSide) {
            boolean powered = level.hasNeighborSignal(pos);
            if (powered != state.getValue(POWERED)) {
                BlockState newState = state;
                if (!state.getValue(POWERED)) {
                    newState = state.cycle(LIT);
                    level.playSound(
                            null,
                            pos,
                            ModSounds.COPPER_BULB_TOGGLE.get(),
                            SoundSource.BLOCKS,
                            0.5f,
                            1.0f
                    );
                }
                level.setBlock(pos, newState.setValue(POWERED, powered), 3);
            }
        }
    }

    @Override
    public void onPlace(
            BlockState state,
            net.minecraft.world.level.Level level,
            BlockPos pos,
            BlockState oldState,
            boolean isMoving
    ) {
        if (!oldState.is(state.getBlock())) {
            if (!level.isClientSide) {
                boolean powered = level.hasNeighborSignal(pos);
                if (powered != state.getValue(POWERED)) {
                    level.setBlock(pos, state.setValue(POWERED, powered), 3);
                }
            }
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(
            BlockState state,
            net.minecraft.world.level.Level level,
            BlockPos pos
    ) {
        return state.getValue(LIT) ? 15 : 0;
    }

}
