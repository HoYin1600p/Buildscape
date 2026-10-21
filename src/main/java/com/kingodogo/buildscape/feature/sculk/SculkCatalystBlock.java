package com.kingodogo.buildscape.feature.sculk;

import com.kingodogo.buildscape.block.base.ModBlockProperties;
import com.kingodogo.buildscape.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Random;

public class SculkCatalystBlock extends Block implements EntityBlock {
    public static final BooleanProperty BLOOM = ModBlockProperties.BLOOM;

    public SculkCatalystBlock(BlockBehaviour.Properties properties) {
        super(properties.noOcclusion());
        this.registerDefaultState(this.defaultBlockState().setValue(BLOOM, Boolean.FALSE));
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BLOOM);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        ensureBlockEntity(level, pos, state);
        if (state.getValue(BLOOM)) {
            level.setBlock(pos, state.setValue(BLOOM, false), 3);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        ensureBlockEntity(level, pos, state);
        if (state.getValue(BLOOM)) {
            level.scheduleTick(pos, this, 8);
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moving) {
        super.onPlace(state, level, pos, oldState, moving);
        if (!level.isClientSide && !oldState.is(this)) {
            level.scheduleTick(pos, this, 1);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighborPos,
            boolean moving) {
        if (!level.isClientSide) {
            ensureBlockEntity(level, pos, state);
            if (state.getValue(BLOOM)) {
                level.scheduleTick(pos, this, 8);
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
            BlockHitResult hit) {
        if (!level.isClientSide) {
            ensureBlockEntity(level, pos, state);
        }
        return InteractionResult.PASS;
    }

    static SculkCatalystBlockEntity ensureBlockEntity(Level level, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null) {
            blockEntity = new SculkCatalystBlockEntity(pos, state);
            level.setBlockEntity(blockEntity);
            SculkCatalystHandler.debug("created catalyst at {} in {}", pos, level.dimension().location());
        }
        return blockEntity instanceof SculkCatalystBlockEntity catalyst ? catalyst : null;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SculkCatalystBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> GameEventListener getListener(Level level, T blockEntity) {
        if (!level.isClientSide && blockEntity instanceof SculkCatalystBlockEntity) {
            SculkCatalystHandler.debug("listener requested at {} in {}", blockEntity.getBlockPos(),
                    level.dimension().location());
        }
        return !level.isClientSide && blockEntity instanceof SculkCatalystBlockEntity catalyst ? catalyst : null;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> type) {
        if (level.isClientSide || type != ModBlockEntities.SCULK_CATALYST_BLOCK_ENTITY.get()) {
            return null;
        }
        return (tickLevel, pos, tickState, blockEntity) -> ((SculkCatalystBlockEntity) blockEntity)
                .serverTick((ServerLevel) tickLevel);
    }
}
