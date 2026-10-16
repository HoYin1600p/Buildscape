package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public abstract class SnowyShortGrassBlock extends BushBlock implements SinksOnFarmland, ICommonShapeUpdate {

    protected static final VoxelShape SHAPE = Block.box(
            2.0D,
            0.0D,
            2.0D,
            14.0D,
            13.0D,
            14.0D
    );

    private final Supplier<Block> snowBricksSupplier;
    private final Supplier<Block> snowyTallGrassSupplier;

    public SnowyShortGrassBlock(BlockBehaviour.Properties properties) {
        this(properties, null, null);
    }

    public SnowyShortGrassBlock(BlockBehaviour.Properties properties, Supplier<Block> snowBricksSupplier, Supplier<Block> snowyTallGrassSupplier) {
        super(properties);
        this.snowBricksSupplier = snowBricksSupplier;
        this.snowyTallGrassSupplier = snowyTallGrassSupplier;
        this.registerDefaultState(this.stateDefinition.any().setValue(ON_FARMLAND, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ON_FARMLAND);
    }

    @Override
    public VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        VoxelShape shape = SHAPE;
        if (state.getValue(ON_FARMLAND)) {
            return shape.move(0, -0.0625D, 0);
        }
        return shape;
    }

    @Override
    protected boolean mayPlaceOn(
            BlockState state,
            BlockGetter level,
            BlockPos pos
    ) {
        Block snowBricks = snowBricksSupplier != null ? snowBricksSupplier.get() : null;
        return (
                state.is(BlockTags.DIRT) ||
                state.is(Blocks.FARMLAND) ||
                state.is(Blocks.SNOW_BLOCK) ||
                (snowBricks != null && state.is(snowBricks))
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state != null) {
            return state.setValue(ON_FARMLAND, shouldSink(context.getLevel(), context.getClickedPos()));
        }
        return null;
    }

    @Override
    public BlockState onUpdateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelReader level,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        if (direction == Direction.DOWN) {
            return state.setValue(ON_FARMLAND, shouldSink(level, pos));
        }
        return state;
    }

    public boolean isValidBonemeal(BlockGetter level, BlockPos pos) {
        return level.getBlockState(pos.above()).isAir();
    }

    public void performBonemealGrowth(ServerLevel level, BlockPos pos) {
        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);
        if (aboveState.isAir()) {
            BlockPos upperPos = abovePos.above();
            BlockState upperState = level.getBlockState(upperPos);
            if (upperState.isAir()) {
                Block tallGrass = snowyTallGrassSupplier != null ? snowyTallGrassSupplier.get() : null;
                if (tallGrass != null) {
                    BlockState tallGrassState = tallGrass.defaultBlockState()
                            .setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER);
                    if (tallGrassState.canSurvive(level, pos)) {
                        level.setBlock(pos, tallGrassState, 3);
                        level.setBlock(
                                abovePos,
                                tallGrassState.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER),
                                3
                        );
                    }
                }
            }
        }
    }

    public List<ItemStack> getReferenceDrops(ItemStack tool) {
        if (tool != null && !tool.isEmpty()) {
            if (tool.is(Items.SHEARS) || Services.PLATFORM.hasSilkTouch(tool)) {
                return Collections.singletonList(new ItemStack(this));
            }
        }
        return Collections.emptyList();
    }
}
