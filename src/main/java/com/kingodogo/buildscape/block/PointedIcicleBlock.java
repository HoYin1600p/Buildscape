package com.kingodogo.buildscape.block;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PointedIcicleBlock extends PointedDripstoneBlock {

    public PointedIcicleBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction direction = state.getValue(
                BlockStateProperties.VERTICAL_DIRECTION
        );
        BlockPos supportPos = direction == Direction.DOWN
                ? pos.above()
                : pos.below();
        BlockState supportState = level.getBlockState(supportPos);

        if (supportState.getBlock() instanceof SlabBlock) {
            return true;
        }

        if (
                supportState.isFaceSturdy(
                        level,
                        supportPos,
                        direction == Direction.DOWN ? Direction.DOWN : Direction.UP
                )
        ) {
            return true;
        }

        if (isDripstoneBlock(supportState)) {
            Direction supportDirection = supportState.getValue(
                    BlockStateProperties.VERTICAL_DIRECTION
            );
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
            verticalDirection = clickedState.getValue(
                    BlockStateProperties.VERTICAL_DIRECTION
            );
        } else if (clickedFace == Direction.UP) {
            verticalDirection = Direction.UP;
        } else if (clickedFace == Direction.DOWN) {
            verticalDirection = Direction.DOWN;
        } else if (
                isIcicleBlock(belowState) &&
                        belowState.getValue(BlockStateProperties.VERTICAL_DIRECTION) ==
                                Direction.UP
        ) {
            verticalDirection = Direction.UP;
        } else if (
                isIcicleBlock(aboveState) &&
                        aboveState.getValue(BlockStateProperties.VERTICAL_DIRECTION) ==
                                Direction.DOWN
        ) {
            verticalDirection = Direction.DOWN;
        } else {
            BlockState supportAbove = level.getBlockState(pos.above());
            BlockState supportBelow = level.getBlockState(pos.below());

            boolean hasAboveSupport = supportAbove.isFaceSturdy(
                    level,
                    pos.above(),
                    Direction.DOWN
            );
            boolean hasBelowSupport = supportBelow.isFaceSturdy(
                    level,
                    pos.below(),
                    Direction.UP
            );

            if (hasAboveSupport && !hasBelowSupport) {
                verticalDirection = Direction.DOWN;
            } else if (hasBelowSupport && !hasAboveSupport) {
                verticalDirection = Direction.UP;
            } else {
                verticalDirection = Direction.DOWN;
            }
        }

        BlockState state =
                this.defaultBlockState()
                        .setValue(BlockStateProperties.VERTICAL_DIRECTION, verticalDirection);

        state = calculateCustomThickness(level, pos, state);

        BlockPos adjacentPos = verticalDirection == Direction.DOWN
                ? pos.below()
                : pos.above();
        BlockState adjacentState = level.getBlockState(adjacentPos);

        if (isIcicleBlock(adjacentState)) {
            DripstoneThickness mergeThickness = checkForMerge(level, pos, state);
            if (mergeThickness != state.getValue(THICKNESS)) {
                state = state.setValue(THICKNESS, mergeThickness);
            }
        }

        return state;
    }

    @Override
    public void onPlace(
            BlockState state,
            Level level,
            BlockPos pos,
            BlockState oldState,
            boolean isMoving
    ) {
        if (!level.isClientSide) {
            BlockState validatedState = state;
            DripstoneThickness thickness = state.getValue(THICKNESS);

            if (thickness == DripstoneThickness.TIP_MERGE) {
                DripstoneThickness correctThickness = checkForMerge(level, pos, state);
                if (correctThickness != thickness) {
                    validatedState = state.setValue(THICKNESS, correctThickness);
                    level.setBlock(pos, validatedState, 2);
                }
            }

            if (level instanceof ServerLevel) {
                level.scheduleTick(pos, this, 2);
            }
            updateDripstoneNeighbors(level, pos);
        }
    }

    @Override
    public void tick(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            Random random
    ) {
        if (!canSurvive(state, level, pos)) {
            Direction direction = state.getValue(
                    BlockStateProperties.VERTICAL_DIRECTION
            );

            // Check if the support block is actually gone
            BlockPos supportPos = direction == Direction.DOWN
                    ? pos.above()
                    : pos.below();
            BlockState supportState = level.getBlockState(supportPos);
            boolean supportGone =
                    !supportState.isFaceSturdy(
                            level,
                            supportPos,
                            direction == Direction.DOWN ? Direction.DOWN : Direction.UP
                    ) &&
                            !isDripstoneBlock(supportState) &&
                            !(supportState.getBlock() instanceof SlabBlock);

            if (direction == Direction.DOWN) {
                // If support is gone, force falling regardless of what's below
                // Otherwise, only fall if the space below is free
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
        com.kingodogo.buildscape.entity.FallingIcicleEntity fallingEntity =
                new com.kingodogo.buildscape.entity.FallingIcicleEntity(
                        level,
                        (double) pos.getX() + 0.5D,
                        pos.getY(),
                        (double) pos.getZ() + 0.5D,
                        state
                );

        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        level.addFreshEntity(fallingEntity);
    }

    private boolean isFree(Level level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        // Allow falling through replaceable blocks and water (water is replaceable)
        return belowState.getMaterial().isReplaceable();
    }

    @Override
    public void onRemove(
            BlockState state,
            Level level,
            BlockPos pos,
            BlockState newState,
            boolean isMoving
    ) {
        if (!level.isClientSide && !isMoving && !newState.is(this)) {
            Direction direction = state.getValue(
                    BlockStateProperties.VERTICAL_DIRECTION
            );

            java.util.Set<BlockPos> fallingBlocks = new java.util.HashSet<>();
            fallingBlocks.add(pos);

            if (direction == Direction.DOWN) {
                BlockPos currentPos = pos.below();
                while (true) {
                    if (fallingBlocks.contains(currentPos)) {
                        break;
                    }
                    BlockState currentState = level.getBlockState(currentPos);
                    if (
                            currentState.is(this) &&
                                    currentState.getValue(BlockStateProperties.VERTICAL_DIRECTION) ==
                                            direction
                    ) {
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
                    if (
                            currentState.is(this) &&
                                    currentState.getValue(BlockStateProperties.VERTICAL_DIRECTION) ==
                                            direction
                    ) {
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
        super.onRemove(state, level, pos, newState, isMoving);
    }

    private void updateRemainingStack(
            Level level,
            BlockPos startPos,
            Direction direction
    ) {
        BlockState startState = level.getBlockState(startPos);
        if (!startState.is(this)) {
            return;
        }

        BlockPos basePos = startPos;
        if (direction == Direction.DOWN) {
            while (true) {
                BlockPos nextPos = basePos.above();
                BlockState nextState = level.getBlockState(nextPos);
                if (
                        nextState.is(this) &&
                                nextState.getValue(BlockStateProperties.VERTICAL_DIRECTION) ==
                                        direction
                ) {
                    basePos = nextPos;
                } else {
                    break;
                }
            }
        } else {
            while (true) {
                BlockPos nextPos = basePos.below();
                BlockState nextState = level.getBlockState(nextPos);
                if (
                        nextState.is(this) &&
                                nextState.getValue(BlockStateProperties.VERTICAL_DIRECTION) ==
                                        direction
                ) {
                    basePos = nextPos;
                } else {
                    break;
                }
            }
        }

        BlockPos currentPos = basePos;
        Direction traverseDir = direction == Direction.DOWN
                ? Direction.DOWN
                : Direction.UP;
        while (true) {
            BlockState currentState = level.getBlockState(currentPos);
            if (
                    currentState.is(this) &&
                            currentState.getValue(BlockStateProperties.VERTICAL_DIRECTION) ==
                                    direction
            ) {
                BlockState updatedState = calculateCustomThickness(
                        level,
                        currentPos,
                        currentState
                );

                DripstoneThickness mergeThickness = checkForMerge(
                        level,
                        currentPos,
                        updatedState
                );
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

    private void breakAllConnectedBlocks(
            Level level,
            BlockPos pos,
            Direction direction
    ) {
        java.util.Set<BlockPos> breakingBlocks = new java.util.HashSet<>();
        breakingBlocks.add(pos);

        Direction tipDirection = direction == Direction.DOWN
                ? Direction.DOWN
                : Direction.UP;
        BlockPos currentPos = pos.relative(tipDirection);

        while (true) {
            if (breakingBlocks.contains(currentPos)) {
                break;
            }
            BlockState currentState = level.getBlockState(currentPos);
            if (
                    currentState.is(this) &&
                            currentState.getValue(BlockStateProperties.VERTICAL_DIRECTION) ==
                                    direction
            ) {
                breakingBlocks.add(currentPos);
                level.destroyBlock(currentPos, true);
                currentPos = currentPos.relative(tipDirection);
            } else {
                break;
            }
        }

        BlockPos baseCheckPos = direction == Direction.DOWN
                ? pos.above()
                : pos.below();
        updateRemainingStack(level, baseCheckPos, direction);
    }

    private void updateDripstoneNeighbors(Level level, BlockPos pos) {
        BlockState currentState = level.getBlockState(pos);
        if (!currentState.is(this)) {
            return;
        }

        BlockState aboveState = level.getBlockState(pos.above());
        BlockState belowState = level.getBlockState(pos.below());
        Direction direction = currentState.getValue(
                BlockStateProperties.VERTICAL_DIRECTION
        );

        if (
                aboveState.getBlock() instanceof PointedDripstoneBlock &&
                        !(aboveState.getBlock() instanceof PointedIcicleBlock)
        ) {
            aboveState.getBlock().neighborChanged(
                    aboveState,
                    level,
                    pos.above(),
                    this,
                    pos,
                    false
            );
        }

        if (
                belowState.getBlock() instanceof PointedDripstoneBlock &&
                        !(belowState.getBlock() instanceof PointedIcicleBlock)
        ) {
            belowState.getBlock().neighborChanged(
                    belowState,
                    level,
                    pos.below(),
                    this,
                    pos,
                    false
            );
        }
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
        if (direction == Direction.UP || direction == Direction.DOWN) {
            BlockState updatedState = calculateCustomThickness(level, pos, state);

            DripstoneThickness correctThickness = checkForMerge(
                    level,
                    pos,
                    updatedState
            );
            updatedState = updatedState.setValue(THICKNESS, correctThickness);

            if (level instanceof Level world) {
                updateNeighborThickness(world, pos.above());
                updateNeighborThickness(world, pos.below());
                updateDripstoneNeighbors(world, pos);
            }

            return updatedState;
        }

        return state;
    }

    @Override
    public void neighborChanged(
            BlockState state,
            Level level,
            BlockPos pos,
            Block neighborBlock,
            BlockPos neighborPos,
            boolean isMoving
    ) {
        if (!level.isClientSide) {
            Direction direction = state.getValue(
                    BlockStateProperties.VERTICAL_DIRECTION
            );
            BlockPos supportPos = direction == Direction.DOWN
                    ? pos.above()
                    : pos.below();

            if (neighborPos.equals(supportPos)) {
                if (!canSurvive(state, level, pos)) {
                    if (level instanceof ServerLevel) {
                        level.scheduleTick(pos, this, 2);
                    }
                } else if (level instanceof ServerLevel) {
                    level.scheduleTick(pos, this, 2);
                }
            } else if (level instanceof ServerLevel) {
                level.scheduleTick(pos, this, 2);
            }
        }

        if (neighborPos.equals(pos.above()) || neighborPos.equals(pos.below())) {
            BlockState currentState = level.getBlockState(pos);
            if (currentState.is(this)) {
                BlockState updatedState = calculateCustomThickness(
                        level,
                        pos,
                        currentState
                );

                DripstoneThickness mergeThickness = checkForMerge(
                        level,
                        pos,
                        updatedState
                );
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

    private BlockState calculateCustomThickness(
            LevelAccessor level,
            BlockPos pos,
            BlockState state
    ) {
        Direction direction = state.getValue(
                BlockStateProperties.VERTICAL_DIRECTION
        );

        int stackHeight = countStackHeight(level, pos, direction);

        int positionInStack = getPositionInStack(level, pos, direction);

        DripstoneThickness thickness = calculateThicknessForPosition(
                stackHeight,
                positionInStack
        );

        return state.setValue(THICKNESS, thickness);
    }

    private int countStackHeight(
            LevelAccessor level,
            BlockPos pos,
            Direction direction
    ) {
        int height = 1;
        BlockPos currentPos = pos;

        while (true) {
            BlockPos nextPos = currentPos.relative(Direction.UP);
            BlockState nextState = level.getBlockState(nextPos);
            if (
                    isDripstoneBlock(nextState) &&
                            nextState.getValue(BlockStateProperties.VERTICAL_DIRECTION) == direction
            ) {
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
            if (
                    isDripstoneBlock(nextState) &&
                            nextState.getValue(BlockStateProperties.VERTICAL_DIRECTION) == direction
            ) {
                height++;
                currentPos = nextPos;
            } else {
                break;
            }
        }

        return height;
    }

    private int getPositionInStack(
            LevelAccessor level,
            BlockPos pos,
            Direction direction
    ) {
        int position = 0;
        BlockPos currentPos = pos;

        if (direction == Direction.DOWN) {
            while (true) {
                BlockPos nextPos = currentPos.relative(Direction.UP);
                BlockState nextState = level.getBlockState(nextPos);
                if (
                        isDripstoneBlock(nextState) &&
                                nextState.getValue(BlockStateProperties.VERTICAL_DIRECTION) ==
                                        direction
                ) {
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
                if (
                        isDripstoneBlock(nextState) &&
                                nextState.getValue(BlockStateProperties.VERTICAL_DIRECTION) ==
                                        direction
                ) {
                    position++;
                    currentPos = nextPos;
                } else {
                    break;
                }
            }
        }

        return position;
    }

    private DripstoneThickness calculateThicknessForPosition(
            int stackHeight,
            int position
    ) {
        if (stackHeight == 1) {
            return DripstoneThickness.TIP;
        }

        if (stackHeight == 2) {
            return position == 0
                    ? DripstoneThickness.FRUSTUM
                    : DripstoneThickness.TIP;
        }

        if (stackHeight == 3) {
            if (position == 0) {
                return DripstoneThickness.BASE;
            } else if (position == 1) {
                return DripstoneThickness.FRUSTUM;
            } else {
                return DripstoneThickness.TIP;
            }
        }

        if (position == 0) {
            return DripstoneThickness.BASE;
        } else if (position == stackHeight - 1) {
            return DripstoneThickness.TIP;
        } else if (position == stackHeight - 2) {
            return DripstoneThickness.FRUSTUM;
        } else {
            return DripstoneThickness.MIDDLE;
        }
    }

    private boolean isDripstoneBlock(BlockState bs) {
        return bs.is(this) || bs.getBlock() instanceof PointedDripstoneBlock;
    }

    private boolean isIcicleBlock(BlockState bs) {
        return bs.is(this);
    }

    private DripstoneThickness checkForMerge(
            LevelAccessor level,
            BlockPos pos,
            BlockState state
    ) {
        Direction currentDirection = state.getValue(
                BlockStateProperties.VERTICAL_DIRECTION
        );
        DripstoneThickness currentThickness = state.getValue(THICKNESS);

        boolean isTip =
                currentThickness == DripstoneThickness.TIP ||
                        currentThickness == DripstoneThickness.TIP_MERGE;

        if (!isTip) {
            return currentThickness;
        }

        BlockPos adjacentPos = currentDirection == Direction.DOWN
                ? pos.below()
                : pos.above();
        BlockState adjacentState = level.getBlockState(adjacentPos);

        if (adjacentState.getBlock() != this) {
            return DripstoneThickness.TIP;
        }

        Direction adjacentDirection = adjacentState.getValue(
                BlockStateProperties.VERTICAL_DIRECTION
        );
        if (currentDirection == adjacentDirection) {
            return DripstoneThickness.TIP;
        }

        boolean isOpposite =
                (currentDirection == Direction.DOWN &&
                        adjacentDirection == Direction.UP) ||
                        (currentDirection == Direction.UP && adjacentDirection == Direction.DOWN);
        if (!isOpposite) {
            return DripstoneThickness.TIP;
        }

        DripstoneThickness adjacentThickness = adjacentState.getValue(THICKNESS);
        if (
                adjacentThickness != DripstoneThickness.TIP &&
                        adjacentThickness != DripstoneThickness.TIP_MERGE
        ) {
            return DripstoneThickness.TIP;
        }

        return DripstoneThickness.TIP_MERGE;
    }

    private void updateNeighborThickness(Level level, BlockPos pos) {
        BlockState neighborState = level.getBlockState(pos);
        if (neighborState.is(this)) {
            BlockState updatedState = calculateCustomThickness(
                    level,
                    pos,
                    neighborState
            );

            DripstoneThickness correctThickness = checkForMerge(
                    level,
                    pos,
                    updatedState
            );
            updatedState = updatedState.setValue(THICKNESS, correctThickness);

            if (updatedState != neighborState) {
                level.setBlock(pos, updatedState, 2);
            }
        }
    }

    @Override
    public void randomTick(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            Random random
    ) {
        Direction direction = state.getValue(
                BlockStateProperties.VERTICAL_DIRECTION
        );

        if (direction == Direction.DOWN) {
            tryCauldronRecipe(level, pos, random);
            tryStalactiteGrowth(level, pos, random);
        } else {
            tryStalagmiteGrowth(level, pos, random);
        }
    }

    private void tryCauldronRecipe(
            ServerLevel level,
            BlockPos iciclePos,
            Random random
    ) {
        BlockState icicleState = level.getBlockState(iciclePos);

        if (
                icicleState.getValue(BlockStateProperties.VERTICAL_DIRECTION) !=
                        Direction.DOWN
        ) {
            return;
        }

        DripstoneThickness thickness = icicleState.getValue(THICKNESS);
        if (
                thickness != DripstoneThickness.TIP &&
                        thickness != DripstoneThickness.TIP_MERGE
        ) {
            return;
        }

        BlockPos cauldronPos = iciclePos.below();
        BlockState cauldronState = level.getBlockState(cauldronPos);
        if (!cauldronState.is(Blocks.CAULDRON)) {
            return;
        }

        BlockPos icicleBlockPos = iciclePos.above();
        BlockState icicleBlockState = level.getBlockState(icicleBlockPos);
        Block icicleBlock = icicleBlockState.getBlock();
        // Only accept Packed Icicle Block as the source block
        boolean isPackedIcicleBlock =
                icicleBlock == ModBlocks.PACKED_ICICLE_BLOCK.get() ||
                        icicleBlock instanceof PackedIcicleBlock;

        if (!isPackedIcicleBlock) {
            return;
        }

        BlockPos waterPos = icicleBlockPos.above();
        BlockState waterState = level.getBlockState(waterPos);
        if (!waterState.getFluidState().is(FluidTags.WATER)) {
            return;
        }

        if (random.nextInt(50) == 0) {
            level.setBlock(
                    cauldronPos,
                    ModBlocks.ICICLE_CAULDRON.get().defaultBlockState(),
                    3
            );

            net.minecraft.world.level.block.entity.BlockEntity blockEntity =
                    level.getBlockEntity(cauldronPos);
            if (blockEntity instanceof IcicleCauldronBlockEntity cauldronEntity) {
                // Store Icicle Block in the cauldron (not Packed Icicle)
                net.minecraft.world.item.Item icicleItem =
                        com.kingodogo.buildscape.item.ModItems.ICICLE_BLOCK.get();
                cauldronEntity.setStoredIcicle(
                        new net.minecraft.world.item.ItemStack(icicleItem, 1)
                );
            }

            level.playSound(
                    null,
                    cauldronPos,
                    net.minecraft.sounds.SoundEvents.POINTED_DRIPSTONE_DRIP_WATER_INTO_CAULDRON,
                    net.minecraft.sounds.SoundSource.BLOCKS,
                    1.0f,
                    1.0f
            );
        }
    }

    private void tryStalactiteGrowth(
            ServerLevel level,
            BlockPos pos,
            Random random
    ) {
        BlockPos rootPos = pos;
        for (int i = 0; i < 11; i++) {
            BlockPos above = rootPos.above();
            BlockState aboveState = level.getBlockState(above);
            if (
                    aboveState.is(this) &&
                            aboveState.getValue(BlockStateProperties.VERTICAL_DIRECTION) ==
                                    Direction.DOWN
            ) {
                rootPos = above;
            } else {
                break;
            }
        }

        BlockPos attachmentPos = rootPos.above();
        BlockState attachmentState = level.getBlockState(attachmentPos);
        Block attachmentBlock = attachmentState.getBlock();

        // Only accept Icicle Block, not Packed Icicle (for consistency with cauldron feature)
        boolean validAttachment =
                attachmentBlock == ModBlocks.ICICLE_BLOCK.get() ||
                        attachmentBlock instanceof IcicleBlock;

        if (!validAttachment) {
            return;
        }

        BlockPos waterCheckPos = attachmentPos.above();
        BlockState waterState = level.getBlockState(waterCheckPos);
        if (!waterState.getFluidState().is(FluidTags.WATER)) {
            return;
        }

        if (random.nextFloat() >= 0.011F) {
            return;
        }

        BlockPos tipPos = rootPos;
        for (int i = 0; i < 11; i++) {
            BlockPos below = tipPos.below();
            BlockState belowState = level.getBlockState(below);
            if (
                    belowState.is(this) &&
                            belowState.getValue(BlockStateProperties.VERTICAL_DIRECTION) ==
                                    Direction.DOWN
            ) {
                tipPos = below;
            } else {
                break;
            }
        }

        BlockState tipState = level.getBlockState(tipPos);
        if (tipState.getValue(THICKNESS) == DripstoneThickness.TIP_MERGE) {
            return;
        }

        BlockPos growPos = tipPos.below();
        if (level.getBlockState(growPos).isAir()) {
            BlockState newTip =
                    this.defaultBlockState()
                            .setValue(BlockStateProperties.VERTICAL_DIRECTION, Direction.DOWN)
                            .setValue(THICKNESS, DripstoneThickness.TIP);

            level.setBlock(growPos, newTip, 3);
            level.setBlock(
                    tipPos,
                    tipState.setValue(THICKNESS, DripstoneThickness.FRUSTUM),
                    2
            );
        }
    }

    private void tryStalagmiteGrowth(
            ServerLevel level,
            BlockPos pos,
            Random random
    ) {
        BlockPos tipPos = pos;
        for (int i = 0; i < 11; i++) {
            BlockPos above = tipPos.above();
            BlockState aboveState = level.getBlockState(above);
            if (
                    aboveState.is(this) &&
                            aboveState.getValue(BlockStateProperties.VERTICAL_DIRECTION) ==
                                    Direction.UP
            ) {
                tipPos = above;
            } else {
                break;
            }
        }

        BlockState tipState = level.getBlockState(tipPos);
        if (tipState.getValue(THICKNESS) == DripstoneThickness.TIP_MERGE) {
            return;
        }

        BlockPos searchPos = tipPos.above();
        BlockPos stalactiteTipPos = null;

        for (int i = 0; i < 10; i++) {
            BlockState searchState = level.getBlockState(searchPos);

            if (searchState.isAir()) {
                searchPos = searchPos.above();
                continue;
            }

            if (
                    searchState.is(this) &&
                            searchState.getValue(BlockStateProperties.VERTICAL_DIRECTION) ==
                                    Direction.DOWN
            ) {
                stalactiteTipPos = searchPos;
            }
            break;
        }

        if (stalactiteTipPos == null) {
            return;
        }

        BlockPos stalactiteRoot = stalactiteTipPos;
        for (int i = 0; i < 11; i++) {
            BlockPos above = stalactiteRoot.above();
            BlockState aboveState = level.getBlockState(above);
            if (
                    aboveState.is(this) &&
                            aboveState.getValue(BlockStateProperties.VERTICAL_DIRECTION) ==
                                    Direction.DOWN
            ) {
                stalactiteRoot = above;
            } else {
                break;
            }
        }

        BlockPos attachmentPos = stalactiteRoot.above();
        BlockState attachmentState = level.getBlockState(attachmentPos);
        Block attachmentBlock = attachmentState.getBlock();

        // Only accept Icicle Block, not Packed Icicle (for consistency with cauldron feature)
        boolean validAttachment =
                attachmentBlock == ModBlocks.ICICLE_BLOCK.get() ||
                        attachmentBlock instanceof IcicleBlock;

        if (!validAttachment) {
            return;
        }

        BlockPos waterCheckPos = attachmentPos.above();
        BlockState waterState = level.getBlockState(waterCheckPos);
        if (!waterState.getFluidState().is(FluidTags.WATER)) {
            return;
        }

        if (random.nextFloat() >= 0.022F) {
            return;
        }

        BlockPos growPos = tipPos.above();
        if (level.getBlockState(growPos).isAir()) {
            BlockState newTip =
                    this.defaultBlockState()
                            .setValue(BlockStateProperties.VERTICAL_DIRECTION, Direction.UP)
                            .setValue(THICKNESS, DripstoneThickness.TIP);

            level.setBlock(growPos, newTip, 3);
            level.setBlock(
                    tipPos,
                    tipState.setValue(THICKNESS, DripstoneThickness.FRUSTUM),
                    2
            );
        }
    }

    private boolean isIcicleSourceBlock(BlockState state) {
        Block block = state.getBlock();
        // Only accept Icicle Block, not Packed Icicle (for consistency with cauldron feature)
        return (
                block == ModBlocks.ICICLE_BLOCK.get() || block instanceof IcicleBlock
        );
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void animateTick(
            BlockState state,
            Level level,
            BlockPos pos,
            Random random
    ) {
        if (!level.isClientSide) {
            return;
        }

        Direction direction = state.getValue(
                BlockStateProperties.VERTICAL_DIRECTION
        );
        DripstoneThickness thickness = state.getValue(THICKNESS);

        if (
                direction == Direction.DOWN &&
                        (thickness == DripstoneThickness.TIP ||
                                thickness == DripstoneThickness.TIP_MERGE)
        ) {
            boolean hasWaterAboveIcicle = checkWaterAboveIcicleChain(level, pos);

            if (hasWaterAboveIcicle) {
                double tipX = pos.getX() + 0.5D;
                double tipY = pos.getY() + 0.1D;
                double tipZ = pos.getZ() + 0.5D;

                if (random.nextInt(3) == 0) {
                    level.addParticle(
                            ParticleTypes.DRIPPING_WATER,
                            tipX,
                            tipY,
                            tipZ,
                            0.0D,
                            0.0D,
                            0.0D
                    );
                }
            }
        }
    }

    private boolean checkWaterAboveIcicleChain(
            LevelAccessor level,
            BlockPos tipPos
    ) {
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

        return (
                waterState.getFluidState().is(FluidTags.WATER) ||
                        waterState.is(Blocks.WATER)
        );
    }

    @Override
    public VoxelShape getVisualShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return Shapes.empty();
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return false;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }

    @Override
    public boolean propagatesSkylightDown(
            BlockState state,
            BlockGetter reader,
            BlockPos pos
    ) {
        return true;
    }

    @Override
    public float getShadeBrightness(
            BlockState state,
            BlockGetter level,
            BlockPos pos
    ) {
        return 1.0F;
    }

    @Override
    public boolean skipRendering(
            BlockState state,
            BlockState adjacentBlockState,
            Direction side
    ) {
        return (
                adjacentBlockState.is(this) ||
                        super.skipRendering(state, adjacentBlockState, side)
        );
    }
}
// Kingodogo Finished this File on 2025-12-10 20-50-05
