package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import javax.annotation.Nullable;
public abstract class CopperBulbBlock extends Block implements ICommonNeighborAware, ICommonAnalogOutput {

    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    private final int lightLevel;

    public CopperBulbBlock(BlockBehaviour.Properties properties, int lightLevel) {
        super(properties.lightLevel(state -> state.getValue(LIT) ? lightLevel : 0));
        this.lightLevel = lightLevel;
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false).setValue(POWERED, false));
    }

    public int getLightLevel() {
        return lightLevel;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, POWERED);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    public void onRandomTick(BlockState state, ServerLevel level, BlockPos pos) {
        CopperOxidationHandler.tryOxidize(level, pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(LIT, false)
                .setValue(POWERED, false);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return false;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 0;
    }

    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        return true;
    }

    @Override
    public void onNeighborUpdate(Level level, BlockPos pos, BlockState state, Block neighborBlock, BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide()) {
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
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!oldState.is(state.getBlock())) {
            if (!level.isClientSide()) {
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
    public int getAnalogOutput(BlockState state, Level level, BlockPos pos) {
        return state.getValue(LIT) ? 15 : 0;
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        float destroySpeed = state.getDestroySpeed(level, pos);
        if (destroySpeed == -1.0F) {
            return 0.0F;
        }

        int efficiencyLevel = com.kingodogo.buildscape.platform.Services.PLATFORM.getEfficiencyLevel(player.getMainHandItem());
        ItemStack tool = player.getMainHandItem();
        float speedMultiplier = tool.isEmpty() ? 1.0F : tool.getDestroySpeed(state);
        if (speedMultiplier > 1.0F) {
            int efficiencyBonus = efficiencyLevel > 0 ? efficiencyLevel * efficiencyLevel + 1 : 0;
            speedMultiplier += (float) efficiencyBonus;
        }

        float difficultyModifier = player.hasCorrectToolForDrops(state) ? 30.0F : 100.0F;
        return speedMultiplier / destroySpeed / difficultyModifier;
    }
}
