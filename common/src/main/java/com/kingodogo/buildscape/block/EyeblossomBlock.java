package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
public abstract class EyeblossomBlock extends BushBlock implements ICommonRemoval {
    public static final BooleanProperty WAXED = BooleanProperty.create("waxed");
    private final boolean isOpen;
    protected static final VoxelShape SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 10.0D, 13.0D);

    public EyeblossomBlock(boolean isOpen, BlockBehaviour.Properties properties) {
        super(properties);
        this.isOpen = isOpen;
        this.registerDefaultState(this.stateDefinition.any().setValue(WAXED, false));
    }

    public boolean isOpen() {
        return isOpen;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WAXED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    public void onBlockTick(BlockState state, ServerLevel level, BlockPos pos) {
        EyeblossomTransitionHandler.track(level, pos);
        synchronizeWithTime(level, pos, state, false);
    }


    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (level instanceof ServerLevel serverLevel) {
            EyeblossomTransitionHandler.track(serverLevel, pos);
            serverLevel.scheduleTick(pos, this, 1);
        }
    }

    @Override
    public void onBlockRemoved(Level level, BlockPos pos, BlockState state) {
        if (level instanceof ServerLevel serverLevel) {
            EyeblossomTransitionHandler.untrack(serverLevel, pos);
        }
    }

    public void synchronizeWithTime(ServerLevel level, BlockPos pos, BlockState state, boolean playSound) {
        boolean night = Services.PLATFORM.isNight(level);
        if (!EyeblossomTransitionHandler.shouldTransition(isOpen, state.getValue(WAXED), night)) {
            return;
        }

        String targetName = night ? "open_eyeblossom" : "closed_eyeblossom";
        Block replacementBlock = Services.PLATFORM.getBlock(new CommonId("buildscape", targetName));
        if (replacementBlock == null || replacementBlock == Blocks.AIR) return;

        BlockState replacement = replacementBlock.defaultBlockState().setValue(WAXED, false);
        level.setBlock(pos, replacement, 3);

        if (playSound) {
            Services.PLATFORM.playEyeblossomTransition(level, pos, night);
        }
    }

    public void performBonemeal(ServerLevel level, BlockPos pos) {
        net.minecraft.world.entity.item.ItemEntity itemEntity = new net.minecraft.world.entity.item.ItemEntity(
                level,
                pos.getX() + 0.5D,
                pos.getY() + 0.5D,
                pos.getZ() + 0.5D,
                new net.minecraft.world.item.ItemStack(this)
        );
        itemEntity.setDefaultPickUpDelay();
        level.addFreshEntity(itemEntity);
    }
}
