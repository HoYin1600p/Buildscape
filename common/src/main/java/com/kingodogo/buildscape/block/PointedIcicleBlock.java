package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class PointedIcicleBlock extends Block implements ICommonShapeUpdate, SimpleWaterloggedBlock {

    public enum Thickness implements StringRepresentable {
        TIP_MERGE("tip_merge"),
        TIP("tip"),
        FRUSTUM("frustum"),
        MIDDLE("middle"),
        BASE("base");

        private final String name;

        Thickness(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public static final EnumProperty<Direction> VERTICAL_DIRECTION = BlockStateProperties.VERTICAL_DIRECTION;
    public static final EnumProperty<Thickness> THICKNESS = EnumProperty.create("thickness", Thickness.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape TIP_MERGE_SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 16.0D, 11.0D);
    protected static final VoxelShape TIP_SHAPE_UP = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 11.0D, 11.0D);
    protected static final VoxelShape TIP_SHAPE_DOWN = Block.box(5.0D, 5.0D, 5.0D, 11.0D, 16.0D, 11.0D);
    protected static final VoxelShape FRUSTUM_SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);
    protected static final VoxelShape MIDDLE_SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 16.0D, 13.0D);
    protected static final VoxelShape BASE_SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

    public PointedIcicleBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(VERTICAL_DIRECTION, Direction.UP)
                .setValue(THICKNESS, Thickness.TIP)
                .setValue(WATERLOGGED, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(VERTICAL_DIRECTION, THICKNESS, WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Thickness thickness = state.getValue(THICKNESS);
        Direction direction = state.getValue(VERTICAL_DIRECTION);
        if (thickness == Thickness.TIP_MERGE) {
            return TIP_MERGE_SHAPE;
        } else if (thickness == Thickness.TIP) {
            return direction == Direction.DOWN ? TIP_SHAPE_DOWN : TIP_SHAPE_UP;
        } else if (thickness == Thickness.FRUSTUM) {
            return FRUSTUM_SHAPE;
        } else if (thickness == Thickness.MIDDLE) {
            return MIDDLE_SHAPE;
        } else {
            return BASE_SHAPE;
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction direction = state.getValue(VERTICAL_DIRECTION);
        BlockPos supportPos = direction == Direction.DOWN ? pos.above() : pos.below();
        BlockState supportState = level.getBlockState(supportPos);

        if (supportState.getBlock() instanceof SlabBlock) {
            return true;
        }

        if (supportState.isFaceSturdy(level, supportPos, direction == Direction.DOWN ? Direction.DOWN : Direction.UP)) {
            return true;
        }

        if (isDripstoneBlock(supportState)) {
            Direction supportDirection = supportState.getValue(VERTICAL_DIRECTION);
            return supportDirection == direction;
        }

        return false;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        Direction clickedFace = context.getClickedFace();
        BlockPos pos = clickedPos.relative(clickedFace);

        Direction verticalDirection;
        BlockState clickedState = level.getBlockState(clickedPos);
        BlockState aboveState = level.getBlockState(pos.above());
        BlockState belowState = level.getBlockState(pos.below());

        if (isIcicleBlock(clickedState)) {
            verticalDirection = clickedState.getValue(VERTICAL_DIRECTION);
        } else if (clickedFace == Direction.UP) {
            verticalDirection = Direction.UP;
        } else if (clickedFace == Direction.DOWN) {
            verticalDirection = Direction.DOWN;
        } else if (isIcicleBlock(belowState) && belowState.getValue(VERTICAL_DIRECTION) == Direction.UP) {
            verticalDirection = Direction.UP;
        } else if (isIcicleBlock(aboveState) && aboveState.getValue(VERTICAL_DIRECTION) == Direction.DOWN) {
            verticalDirection = Direction.DOWN;
        } else {
            BlockState supportAbove = level.getBlockState(pos.above());
            BlockState supportBelow = level.getBlockState(pos.below());

            boolean hasAboveSupport = supportAbove.isFaceSturdy(level, pos.above(), Direction.DOWN);
            boolean hasBelowSupport = supportBelow.isFaceSturdy(level, pos.below(), Direction.UP);

            if (hasAboveSupport && !hasBelowSupport) {
                verticalDirection = Direction.DOWN;
            } else if (hasBelowSupport && !hasAboveSupport) {
                verticalDirection = Direction.UP;
            } else {
                verticalDirection = Direction.DOWN;
            }
        }

        boolean isWater = level.getFluidState(pos).getType() == Fluids.WATER;
        BlockState state = this.defaultBlockState()
                .setValue(VERTICAL_DIRECTION, verticalDirection)
                .setValue(WATERLOGGED, isWater);
        state = calculateCustomThickness(level, pos, state);

        BlockPos adjacentPos = verticalDirection == Direction.DOWN ? pos.below() : pos.above();
        BlockState adjacentState = level.getBlockState(adjacentPos);

        if (isIcicleBlock(adjacentState)) {
            Thickness mergeThickness = checkForMerge(level, pos, state);
            if (mergeThickness != state.getValue(THICKNESS)) {
                state = state.setValue(THICKNESS, mergeThickness);
            }
        }

        return state;
    }

    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!level.isClientSide()) {
            BlockState validatedState = state;
            Thickness thickness = state.getValue(THICKNESS);

            if (thickness == Thickness.TIP_MERGE) {
                Thickness correctThickness = checkForMerge(level, pos, state);
                if (correctThickness != thickness) {
                    validatedState = state.setValue(THICKNESS, correctThickness);
                    level.setBlock(pos, validatedState, 2);
                }
            }

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.scheduleTick(pos, this, 2);
            }
            updateDripstoneNeighbors(level, pos);
        }
    }

    public void onIcicleTick(BlockState state, ServerLevel level, BlockPos pos) {
        if (!canSurvive(state, level, pos)) {
            Direction direction = state.getValue(VERTICAL_DIRECTION);
            BlockPos supportPos = direction == Direction.DOWN ? pos.above() : pos.below();
            BlockState supportState = level.getBlockState(supportPos);
            boolean supportGone = !supportState.isFaceSturdy(level, supportPos, direction == Direction.DOWN ? Direction.DOWN : Direction.UP)
                    && !isDripstoneBlock(supportState)
                    && !(supportState.getBlock() instanceof SlabBlock);

            if (direction == Direction.DOWN) {
                if (supportGone || isFree(level, pos)) {
                    makeIcicleFall(level, pos, state);
                } else {
                    breakAllConnectedBlocks(level, pos, direction);
                }
            } else {
                breakAllConnectedBlocks(level, pos, direction);
            }
        }
    }

    private void makeIcicleFall(Level level, BlockPos pos, BlockState state) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        Entity falling = Services.PLATFORM.createFallingIcicleEntity(
                level,
                (double) pos.getX() + 0.5D,
                (double) pos.getY(),
                (double) pos.getZ() + 0.5D,
                state
        );
        if (falling != null) {
            level.addFreshEntity(falling);
        }
    }

    private boolean isFree(Level level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        return Services.PLATFORM.isReplaceable(belowState);
    }

    public void onIcicleRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!level.isClientSide() && !isMoving && !newState.is(this)) {
            Direction direction = state.getValue(VERTICAL_DIRECTION);
            Set<BlockPos> fallingBlocks = new HashSet<>();
            fallingBlocks.add(pos);

            if (direction == Direction.DOWN) {
                BlockPos currentPos = pos.below();
                while (true) {
                    if (fallingBlocks.contains(currentPos)) {
                        break;
                    }
                    BlockState currentState = level.getBlockState(currentPos);
                    if (currentState.is(this) && currentState.getValue(VERTICAL_DIRECTION) == direction) {
                        fallingBlocks.add(currentPos);
                        makeIcicleFall(level, currentPos, currentState);
                        currentPos = currentPos.below();
                    } else {
                        break;
                    }
                }
                updateRemainingStack(level, pos.above(), direction);
            } else {
                BlockPos currentPos = pos.above();
                while (true) {
                    if (fallingBlocks.contains(currentPos)) {
                        break;
                    }
                    BlockState currentState = level.getBlockState(currentPos);
                    if (currentState.is(this) && currentState.getValue(VERTICAL_DIRECTION) == direction) {
                        fallingBlocks.add(currentPos);
                        level.destroyBlock(currentPos, true);
                        currentPos = currentPos.above();
                    } else {
                        break;
                    }
                }
                updateRemainingStack(level, pos.below(), direction);
            }
        }
    }

    private void updateRemainingStack(Level level, BlockPos startPos, Direction direction) {
        BlockState startState = level.getBlockState(startPos);
        if (!startState.is(this)) {
            return;
        }

        BlockPos basePos = startPos;
        if (direction == Direction.DOWN) {
            while (true) {
                BlockPos nextPos = basePos.above();
                BlockState nextState = level.getBlockState(nextPos);
                if (nextState.is(this) && nextState.getValue(VERTICAL_DIRECTION) == direction) {
                    basePos = nextPos;
                } else {
                    break;
                }
            }
        } else {
            while (true) {
                BlockPos nextPos = basePos.below();
                BlockState nextState = level.getBlockState(nextPos);
                if (nextState.is(this) && nextState.getValue(VERTICAL_DIRECTION) == direction) {
                    basePos = nextPos;
                } else {
                    break;
                }
            }
        }

        BlockPos currentPos = basePos;
        Direction traverseDir = direction == Direction.DOWN ? Direction.DOWN : Direction.UP;
        while (true) {
            BlockState currentState = level.getBlockState(currentPos);
            if (currentState.is(this) && currentState.getValue(VERTICAL_DIRECTION) == direction) {
                BlockState updatedState = calculateCustomThickness(level, currentPos, currentState);
                Thickness mergeThickness = checkForMerge(level, currentPos, updatedState);
                if (mergeThickness != updatedState.getValue(THICKNESS)) {
                    updatedState = updatedState.setValue(THICKNESS, mergeThickness);
                }
                if (updatedState != currentState) {
                    level.setBlock(currentPos, updatedState, 2);
                }
                currentPos = currentPos.relative(traverseDir);
            } else {
                break;
            }
        }
    }

    private void breakAllConnectedBlocks(Level level, BlockPos pos, Direction direction) {
        Set<BlockPos> breakingBlocks = new HashSet<>();
        breakingBlocks.add(pos);

        Direction tipDirection = direction == Direction.DOWN ? Direction.DOWN : Direction.UP;
        BlockPos currentPos = pos.relative(tipDirection);

        while (true) {
            if (breakingBlocks.contains(currentPos)) {
                break;
            }
            BlockState currentState = level.getBlockState(currentPos);
            if (currentState.is(this) && currentState.getValue(VERTICAL_DIRECTION) == direction) {
                breakingBlocks.add(currentPos);
                level.destroyBlock(currentPos, true);
                currentPos = currentPos.relative(tipDirection);
            } else {
                break;
            }
        }

        BlockPos baseCheckPos = direction == Direction.DOWN ? pos.above() : pos.below();
        updateRemainingStack(level, baseCheckPos, direction);
    }

    private void updateDripstoneNeighbors(Level level, BlockPos pos) {
        BlockState currentState = level.getBlockState(pos);
        if (!currentState.is(this)) {
            return;
        }

        BlockState aboveState = level.getBlockState(pos.above());
        BlockState belowState = level.getBlockState(pos.below());

        if (aboveState.getBlock() instanceof PointedDripstoneBlock && !(aboveState.getBlock() instanceof PointedIcicleBlock)) {
            Services.PLATFORM.neighborChanged(level, pos.above(), aboveState, this, pos);
        }

        if (belowState.getBlock() instanceof PointedDripstoneBlock && !(belowState.getBlock() instanceof PointedIcicleBlock)) {
            Services.PLATFORM.neighborChanged(level, pos.below(), belowState, this, pos);
        }
    }

    @Override
    public BlockState onUpdateShape(BlockState state, Direction direction, BlockState neighborState, LevelReader level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            if (level instanceof LevelAccessor accessor) {
                accessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
            }
        }
        if (direction == Direction.UP || direction == Direction.DOWN) {
            if (level instanceof LevelAccessor accessor) {
                BlockState updatedState = calculateCustomThickness(accessor, pos, state);
                Thickness correctThickness = checkForMerge(accessor, pos, updatedState);
                updatedState = updatedState.setValue(THICKNESS, correctThickness);

                if (level instanceof Level world) {
                    updateNeighborThickness(world, pos.above());
                    updateNeighborThickness(world, pos.below());
                    updateDripstoneNeighbors(world, pos);
                }

                return updatedState;
            }
        }
        return state;
    }

    public void onNeighborUpdate(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        if (!level.isClientSide()) {
            Direction direction = state.getValue(VERTICAL_DIRECTION);
            BlockPos supportPos = direction == Direction.DOWN ? pos.above() : pos.below();

            if (neighborPos.equals(supportPos)) {
                if (!canSurvive(state, level, pos)) {
                    if (level instanceof ServerLevel sl) {
                        sl.scheduleTick(pos, this, 2);
                    }
                } else if (level instanceof ServerLevel sl) {
                    sl.scheduleTick(pos, this, 2);
                }
            } else if (level instanceof ServerLevel sl) {
                sl.scheduleTick(pos, this, 2);
            }
        }

        if (neighborPos.equals(pos.above()) || neighborPos.equals(pos.below())) {
            BlockState currentState = level.getBlockState(pos);
            if (currentState.is(this)) {
                BlockState updatedState = calculateCustomThickness(level, pos, currentState);
                Thickness mergeThickness = checkForMerge(level, pos, updatedState);
                if (mergeThickness != updatedState.getValue(THICKNESS)) {
                    updatedState = updatedState.setValue(THICKNESS, mergeThickness);
                }
                if (updatedState != currentState) {
                    level.setBlock(pos, updatedState, 2);
                }
                updateNeighborThickness(level, pos.above());
                updateNeighborThickness(level, pos.below());
                updateDripstoneNeighbors(level, pos);
            }
        }
    }

    private BlockState calculateCustomThickness(LevelAccessor level, BlockPos pos, BlockState state) {
        Direction direction = state.getValue(VERTICAL_DIRECTION);
        int stackHeight = countStackHeight(level, pos, direction);
        int positionInStack = getPositionInStack(level, pos, direction);
        Thickness thickness = calculateThicknessForPosition(stackHeight, positionInStack);
        return state.setValue(THICKNESS, thickness);
    }

    private int countStackHeight(LevelAccessor level, BlockPos pos, Direction direction) {
        int height = 1;
        BlockPos currentPos = pos;
        while (true) {
            BlockPos nextPos = currentPos.relative(Direction.UP);
            BlockState nextState = level.getBlockState(nextPos);
            if (isDripstoneBlock(nextState) && nextState.getValue(VERTICAL_DIRECTION) == direction) {
                height++;
                currentPos = nextPos;
            } else {
                break;
            }
        }

        currentPos = pos;
        while (true) {
            BlockPos nextPos = currentPos.relative(Direction.DOWN);
            BlockState nextState = level.getBlockState(nextPos);
            if (isDripstoneBlock(nextState) && nextState.getValue(VERTICAL_DIRECTION) == direction) {
                height++;
                currentPos = nextPos;
            } else {
                break;
            }
        }
        return height;
    }

    private int getPositionInStack(LevelAccessor level, BlockPos pos, Direction direction) {
        int position = 0;
        BlockPos currentPos = pos;
        if (direction == Direction.DOWN) {
            while (true) {
                BlockPos nextPos = currentPos.relative(Direction.UP);
                BlockState nextState = level.getBlockState(nextPos);
                if (isDripstoneBlock(nextState) && nextState.getValue(VERTICAL_DIRECTION) == direction) {
                    position++;
                    currentPos = nextPos;
                } else {
                    break;
                }
            }
        } else {
            while (true) {
                BlockPos nextPos = currentPos.relative(Direction.DOWN);
                BlockState nextState = level.getBlockState(nextPos);
                if (isDripstoneBlock(nextState) && nextState.getValue(VERTICAL_DIRECTION) == direction) {
                    position++;
                    currentPos = nextPos;
                } else {
                    break;
                }
            }
        }
        return position;
    }

    private Thickness calculateThicknessForPosition(int stackHeight, int position) {
        if (stackHeight == 1) return Thickness.TIP;
        if (stackHeight == 2) return position == 0 ? Thickness.FRUSTUM : Thickness.TIP;
        if (stackHeight == 3) {
            if (position == 0) return Thickness.BASE;
            if (position == 1) return Thickness.FRUSTUM;
            return Thickness.TIP;
        }
        if (position == 0) return Thickness.BASE;
        if (position == stackHeight - 1) return Thickness.TIP;
        if (position == stackHeight - 2) return Thickness.FRUSTUM;
        return Thickness.MIDDLE;
    }

    private boolean isDripstoneBlock(BlockState bs) {
        return bs.is(this) || bs.getBlock() instanceof PointedDripstoneBlock;
    }

    private boolean isIcicleBlock(BlockState bs) {
        return bs.is(this);
    }

    private Thickness checkForMerge(LevelAccessor level, BlockPos pos, BlockState state) {
        Direction currentDirection = state.getValue(VERTICAL_DIRECTION);
        Thickness currentThickness = state.getValue(THICKNESS);

        boolean isTip = currentThickness == Thickness.TIP || currentThickness == Thickness.TIP_MERGE;
        if (!isTip) return currentThickness;

        BlockPos adjacentPos = currentDirection == Direction.DOWN ? pos.below() : pos.above();
        BlockState adjacentState = level.getBlockState(adjacentPos);

        if (adjacentState.getBlock() != this) return Thickness.TIP;

        Direction adjacentDirection = adjacentState.getValue(VERTICAL_DIRECTION);
        if (currentDirection == adjacentDirection) return Thickness.TIP;

        boolean isOpposite = (currentDirection == Direction.DOWN && adjacentDirection == Direction.UP)
                || (currentDirection == Direction.UP && adjacentDirection == Direction.DOWN);
        if (!isOpposite) return Thickness.TIP;

        Thickness adjacentThickness = adjacentState.getValue(THICKNESS);
        if (adjacentThickness != Thickness.TIP && adjacentThickness != Thickness.TIP_MERGE) {
            return Thickness.TIP;
        }

        return Thickness.TIP_MERGE;
    }

    private void updateNeighborThickness(Level level, BlockPos pos) {
        BlockState neighborState = level.getBlockState(pos);
        if (neighborState.is(this)) {
            BlockState updatedState = calculateCustomThickness(level, pos, neighborState);
            Thickness correctThickness = checkForMerge(level, pos, updatedState);
            updatedState = updatedState.setValue(THICKNESS, correctThickness);
            if (updatedState != neighborState) {
                level.setBlock(pos, updatedState, 2);
            }
        }
    }

    public void onRandomTick(BlockState state, ServerLevel level, BlockPos pos) {
        Direction direction = state.getValue(VERTICAL_DIRECTION);
        Random random = new Random();
        if (direction == Direction.DOWN) {
            tryCauldronRecipe(level, pos, random);
            tryStalactiteGrowth(level, pos, random);
        } else {
            tryStalagmiteGrowth(level, pos, random);
        }
    }

    private void tryCauldronRecipe(ServerLevel level, BlockPos iciclePos, Random random) {
        BlockState icicleState = level.getBlockState(iciclePos);
        if (icicleState.getValue(VERTICAL_DIRECTION) != Direction.DOWN) return;

        Thickness thickness = icicleState.getValue(THICKNESS);
        if (thickness != Thickness.TIP && thickness != Thickness.TIP_MERGE) return;

        BlockPos cauldronPos = iciclePos.below();
        BlockState cauldronState = level.getBlockState(cauldronPos);
        if (!cauldronState.is(Blocks.CAULDRON)) return;

        BlockPos icicleBlockPos = iciclePos.above();
        for (int i = 0; i < 11; i++) {
            BlockState checkState = level.getBlockState(icicleBlockPos);
            if (checkState.is(this) && checkState.getValue(VERTICAL_DIRECTION) == Direction.DOWN) {
                icicleBlockPos = icicleBlockPos.above();
            } else {
                break;
            }
        }

        BlockState icicleBlockState = level.getBlockState(icicleBlockPos);
        Block icicleBlock = icicleBlockState.getBlock();
        boolean isPackedIcicleBlock = (ModBlocks.PACKED_ICICLE_BLOCK != null && icicleBlock == ModBlocks.PACKED_ICICLE_BLOCK.get().getBlock())
                || icicleBlock instanceof PackedIcicleBlock
                || icicleBlock == Blocks.PACKED_ICE;

        if (!isPackedIcicleBlock) return;

        BlockPos waterPos = icicleBlockPos.above();
        BlockState waterState = level.getBlockState(waterPos);
        if (!waterState.getFluidState().is(FluidTags.WATER)) return;

        if (random.nextInt(10) == 0) {
            Block icicleCauldron = ModBlocks.ICICLE_CAULDRON != null && ModBlocks.ICICLE_CAULDRON.get() != null ? ModBlocks.ICICLE_CAULDRON.get().getBlock() : null;
            if (icicleCauldron != null) {
                level.setBlock(cauldronPos, icicleCauldron.defaultBlockState(), 3);
            }

            BlockEntity blockEntity = level.getBlockEntity(cauldronPos);
            if (blockEntity instanceof IcicleCauldronBlockEntity cauldronEntity) {
                cauldronEntity.setStoredIcicle(new ItemStack(Services.PLATFORM.getItem(new CommonId("buildscape", "packed_icicle_block")), 1));
            }

            Services.PLATFORM.playBlockSound(level, cauldronPos, new CommonId("minecraft", "block.pointed_dripstone.drip_water_into_cauldron"));
        }
    }

    private void tryStalactiteGrowth(ServerLevel level, BlockPos pos, Random random) {
        BlockPos rootPos = pos;
        for (int i = 0; i < 11; i++) {
            BlockPos above = rootPos.above();
            BlockState aboveState = level.getBlockState(above);
            if (aboveState.is(this) && aboveState.getValue(VERTICAL_DIRECTION) == Direction.DOWN) {
                rootPos = above;
            } else {
                break;
            }
        }

        BlockPos attachmentPos = rootPos.above();
        BlockState attachmentState = level.getBlockState(attachmentPos);
        Block attachmentBlock = attachmentState.getBlock();

        boolean validAttachment = (ModBlocks.ICICLE_BLOCK != null && attachmentBlock == ModBlocks.ICICLE_BLOCK.get().getBlock())
                || attachmentBlock instanceof IcicleBlock;

        if (!validAttachment) return;

        BlockPos waterCheckPos = attachmentPos.above();
        BlockState waterState = level.getBlockState(waterCheckPos);
        if (!waterState.getFluidState().is(FluidTags.WATER)) return;

        if (random.nextFloat() >= 0.06F) return;

        BlockPos tipPos = rootPos;
        for (int i = 0; i < 11; i++) {
            BlockPos below = tipPos.below();
            BlockState belowState = level.getBlockState(below);
            if (belowState.is(this) && belowState.getValue(VERTICAL_DIRECTION) == Direction.DOWN) {
                tipPos = below;
            } else {
                break;
            }
        }

        BlockState tipState = level.getBlockState(tipPos);
        if (tipState.getValue(THICKNESS) == Thickness.TIP_MERGE) return;

        BlockPos growPos = tipPos.below();
        if (level.getBlockState(growPos).isAir()) {
            BlockState newTip = this.defaultBlockState()
                    .setValue(VERTICAL_DIRECTION, Direction.DOWN)
                    .setValue(THICKNESS, Thickness.TIP);

            level.setBlock(growPos, newTip, 3);
            level.setBlock(tipPos, tipState.setValue(THICKNESS, Thickness.FRUSTUM), 2);
        }
    }

    private void tryStalagmiteGrowth(ServerLevel level, BlockPos pos, Random random) {
        BlockPos tipPos = pos;
        for (int i = 0; i < 11; i++) {
            BlockPos above = tipPos.above();
            BlockState aboveState = level.getBlockState(above);
            if (aboveState.is(this) && aboveState.getValue(VERTICAL_DIRECTION) == Direction.UP) {
                tipPos = above;
            } else {
                break;
            }
        }

        BlockState tipState = level.getBlockState(tipPos);
        if (tipState.getValue(THICKNESS) == Thickness.TIP_MERGE) return;

        BlockPos searchPos = tipPos.above();
        BlockPos stalactiteTipPos = null;

        for (int i = 0; i < 10; i++) {
            BlockState searchState = level.getBlockState(searchPos);
            if (searchState.isAir()) {
                searchPos = searchPos.above();
                continue;
            }
            if (searchState.is(this) && searchState.getValue(VERTICAL_DIRECTION) == Direction.DOWN) {
                stalactiteTipPos = searchPos;
            }
            break;
        }

        if (stalactiteTipPos == null) return;

        BlockPos stalactiteRoot = stalactiteTipPos;
        for (int i = 0; i < 11; i++) {
            BlockPos above = stalactiteRoot.above();
            BlockState aboveState = level.getBlockState(above);
            if (aboveState.is(this) && aboveState.getValue(VERTICAL_DIRECTION) == Direction.DOWN) {
                stalactiteRoot = above;
            } else {
                break;
            }
        }

        BlockPos attachmentPos = stalactiteRoot.above();
        BlockState attachmentState = level.getBlockState(attachmentPos);
        Block attachmentBlock = attachmentState.getBlock();

        boolean validAttachment = (ModBlocks.ICICLE_BLOCK != null && attachmentBlock == ModBlocks.ICICLE_BLOCK.get().getBlock())
                || attachmentBlock instanceof IcicleBlock;

        if (!validAttachment) return;

        BlockPos waterCheckPos = attachmentPos.above();
        BlockState waterState = level.getBlockState(waterCheckPos);
        if (!waterState.getFluidState().is(FluidTags.WATER)) return;

        if (random.nextFloat() >= 0.12F) return;

        BlockPos growPos = tipPos.above();
        if (level.getBlockState(growPos).isAir()) {
            BlockState newTip = this.defaultBlockState()
                    .setValue(VERTICAL_DIRECTION, Direction.UP)
                    .setValue(THICKNESS, Thickness.TIP);

            level.setBlock(growPos, newTip, 3);
            level.setBlock(tipPos, tipState.setValue(THICKNESS, Thickness.FRUSTUM), 2);
        }
    }

    private boolean isIcicleSourceBlock(BlockState state) {
        Block block = state.getBlock();
        return ((ModBlocks.ICICLE_BLOCK != null && block == ModBlocks.ICICLE_BLOCK.get().getBlock()) || block instanceof IcicleBlock
                || (ModBlocks.PACKED_ICICLE_BLOCK != null && block == ModBlocks.PACKED_ICICLE_BLOCK.get().getBlock()) || block instanceof PackedIcicleBlock
                || block == Blocks.ICE || block == Blocks.PACKED_ICE || block == Blocks.BLUE_ICE);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    public void onAnimateTick(BlockState state, Level level, BlockPos pos) {
        if (!level.isClientSide()) return;

        Direction direction = state.getValue(VERTICAL_DIRECTION);
        Thickness thickness = state.getValue(THICKNESS);

        if (direction == Direction.DOWN && (thickness == Thickness.TIP || thickness == Thickness.TIP_MERGE)) {
            if (checkWaterAboveIcicleChain(level, pos)) {
                double tipX = pos.getX() + 0.5D;
                double tipY = pos.getY() + 0.1D;
                double tipZ = pos.getZ() + 0.5D;

                if (new Random().nextInt(3) == 0) {
                    level.addParticle(ParticleTypes.DRIPPING_WATER, tipX, tipY, tipZ, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

    private boolean checkWaterAboveIcicleChain(LevelAccessor level, BlockPos tipPos) {
        BlockPos checkPos = tipPos.above();
        BlockState checkState = level.getBlockState(checkPos);

        int maxSearch = 20;
        while (maxSearch > 0 && checkState.is(this)) {
            checkPos = checkPos.above();
            checkState = level.getBlockState(checkPos);
            maxSearch--;
        }

        if (!isIcicleSourceBlock(checkState)) {
            return false;
        }

        BlockPos waterPos = checkPos.above();
        BlockState waterState = level.getBlockState(waterPos);

        return (waterState.getFluidState().is(FluidTags.WATER) || waterState.is(Blocks.WATER));
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return false;
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        return (adjacentBlockState.is(this) || super.skipRendering(state, adjacentBlockState, side));
    }
}
