package com.kingodogo.buildscape.block;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RoseVinesBlock extends VineBlock {

    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;

    private static final VoxelShape UP_SHAPE = Block.box(
            0.0D,
            15.0D,
            0.0D,
            16.0D,
            16.0D,
            16.0D
    );
    private static final VoxelShape DOWN_SHAPE = Block.box(
            0.0D,
            0.0D,
            0.0D,
            16.0D,
            1.0D,
            16.0D
    );
    private static final VoxelShape NORTH_SHAPE = Block.box(
            0.0D,
            0.0D,
            0.0D,
            16.0D,
            16.0D,
            1.0D
    );
    private static final VoxelShape SOUTH_SHAPE = Block.box(
            0.0D,
            0.0D,
            15.0D,
            16.0D,
            16.0D,
            16.0D
    );
    private static final VoxelShape EAST_SHAPE = Block.box(
            15.0D,
            0.0D,
            0.0D,
            16.0D,
            16.0D,
            16.0D
    );
    private static final VoxelShape WEST_SHAPE = Block.box(
            0.0D,
            0.0D,
            0.0D,
            1.0D,
            16.0D,
            16.0D
    );

    public RoseVinesBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(UP, false)
                        .setValue(DOWN, false)
                        .setValue(NORTH, false)
                        .setValue(SOUTH, false)
                        .setValue(EAST, false)
                        .setValue(WEST, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(UP, DOWN, NORTH, SOUTH, EAST, WEST);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos clickedPos = context.getClickedPos();
        BlockState blockState = context.getLevel().getBlockState(clickedPos);
        boolean isExistingVine = blockState.getBlock() instanceof RoseVinesBlock;

        Direction clickedFace = context.getClickedFace();
        Direction attachDirection = clickedFace.getOpposite();

        if (
                isExistingVine &&
                        context.getPlayer() != null &&
                        context.getPlayer().isShiftKeyDown()
        ) {
            return null;
        }

        if (
                isExistingVine &&
                        !(context.getPlayer() != null && context.getPlayer().isShiftKeyDown())
        ) {
            if (blockState.getBlock() == this) {
                BooleanProperty property = getPropertyForFace(attachDirection);
                if (blockState.hasProperty(property) && blockState.getValue(property)) {
                    return null;
                }
            }
        }

        if (!isExistingVine) {
            BlockPos originalPos = clickedPos.relative(clickedFace.getOpposite());
            BlockState originalState = context.getLevel().getBlockState(originalPos);
            if (originalState.getBlock() instanceof RoseVinesBlock) {
                Direction attachToVine = clickedFace.getOpposite();
                BooleanProperty attachProperty = getPropertyForFace(attachToVine);

                if (
                        originalState.hasProperty(attachProperty) &&
                                originalState.getValue(attachProperty)
                ) {
                    return null;
                }

                BlockState state = this.defaultBlockState();
                if (state.hasProperty(attachProperty)) {
                    state = state.setValue(attachProperty, true);
                }
                return state;
            }
        }

        if (clickedFace == Direction.UP) {
            BlockState state;
            if (isExistingVine) {
                if (blockState.getBlock() == this) {
                    state = blockState;
                } else {
                    state = this.defaultBlockState();
                    if (blockState.hasProperty(UP)) state = state.setValue(
                            UP,
                            blockState.getValue(UP)
                    );
                    if (blockState.hasProperty(DOWN)) state = state.setValue(
                            DOWN,
                            blockState.getValue(DOWN)
                    );
                    if (blockState.hasProperty(NORTH)) state = state.setValue(
                            NORTH,
                            blockState.getValue(NORTH)
                    );
                    if (blockState.hasProperty(SOUTH)) state = state.setValue(
                            SOUTH,
                            blockState.getValue(SOUTH)
                    );
                    if (blockState.hasProperty(EAST)) state = state.setValue(
                            EAST,
                            blockState.getValue(EAST)
                    );
                    if (blockState.hasProperty(WEST)) state = state.setValue(
                            WEST,
                            blockState.getValue(WEST)
                    );
                }
            } else {
                state = this.defaultBlockState();
            }

            state = state.setValue(DOWN, true);
            return state;
        }

        if (clickedFace == Direction.DOWN) {
            BlockState state;
            if (isExistingVine) {
                if (blockState.getBlock() == this) {
                    state = blockState;
                } else {
                    state = this.defaultBlockState();
                    if (blockState.hasProperty(UP)) state = state.setValue(
                            UP,
                            blockState.getValue(UP)
                    );
                    if (blockState.hasProperty(DOWN)) state = state.setValue(
                            DOWN,
                            blockState.getValue(DOWN)
                    );
                    if (blockState.hasProperty(NORTH)) state = state.setValue(
                            NORTH,
                            blockState.getValue(NORTH)
                    );
                    if (blockState.hasProperty(SOUTH)) state = state.setValue(
                            SOUTH,
                            blockState.getValue(SOUTH)
                    );
                    if (blockState.hasProperty(EAST)) state = state.setValue(
                            EAST,
                            blockState.getValue(EAST)
                    );
                    if (blockState.hasProperty(WEST)) state = state.setValue(
                            WEST,
                            blockState.getValue(WEST)
                    );
                }
            } else {
                state = this.defaultBlockState();
            }

            state = state.setValue(UP, true);
            return state;
        }

        BlockState state;
        if (isExistingVine) {
            if (blockState.getBlock() == this) {
                BooleanProperty property = getPropertyForFace(attachDirection);
                if (blockState.hasProperty(property) && blockState.getValue(property)) {
                    return null;
                }
            }

            if (blockState.getBlock() == this) {
                state = blockState;
            } else {
                state = this.defaultBlockState();
                if (blockState.hasProperty(UP)) state = state.setValue(
                        UP,
                        blockState.getValue(UP)
                );
                if (blockState.hasProperty(DOWN)) state = state.setValue(
                        DOWN,
                        blockState.getValue(DOWN)
                );
                if (blockState.hasProperty(NORTH)) state = state.setValue(
                        NORTH,
                        blockState.getValue(NORTH)
                );
                if (blockState.hasProperty(SOUTH)) state = state.setValue(
                        SOUTH,
                        blockState.getValue(SOUTH)
                );
                if (blockState.hasProperty(EAST)) state = state.setValue(
                        EAST,
                        blockState.getValue(EAST)
                );
                if (blockState.hasProperty(WEST)) state = state.setValue(
                        WEST,
                        blockState.getValue(WEST)
                );
            }

            BooleanProperty property = getPropertyForFace(attachDirection);
            if (state.hasProperty(property)) {
                if (!state.getValue(property)) {
                    state = state.setValue(property, true);
                } else {
                    return null;
                }
            }

            if (!this.hasAnyFace(state)) {
                return null;
            }
        } else {
            state = this.defaultBlockState();
            BlockState parentState = super.getStateForPlacement(context);
            if (parentState != null && parentState.is(this)) {
                state = parentState;
            }
        }

        return state;
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        net.minecraft.world.item.Item heldItem = context.getItemInHand().getItem();
        if (heldItem instanceof net.minecraft.world.item.BlockItem) {
            net.minecraft.world.level.block.Block heldBlock =
                    ((net.minecraft.world.item.BlockItem) heldItem).getBlock();

            if (!(heldBlock instanceof RoseVinesBlock)) {
                return false;
            }

            if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
                return false;
            }

            Direction clickedFace = context.getClickedFace();
            Direction attachDirection = clickedFace.getOpposite();
            BooleanProperty property = getPropertyForFace(attachDirection);

            if (state.getBlock() == heldBlock) {
                if (state.hasProperty(property) && state.getValue(property)) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        VoxelShape shape = Shapes.empty();

        if (state.getValue(UP)) {
            shape = Shapes.or(shape, UP_SHAPE);
        }
        if (state.getValue(DOWN)) {
            shape = Shapes.or(shape, DOWN_SHAPE);
        }
        if (state.getValue(NORTH)) {
            shape = Shapes.or(shape, NORTH_SHAPE);
        }
        if (state.getValue(SOUTH)) {
            shape = Shapes.or(shape, SOUTH_SHAPE);
        }
        if (state.getValue(EAST)) {
            shape = Shapes.or(shape, EAST_SHAPE);
        }
        if (state.getValue(WEST)) {
            shape = Shapes.or(shape, WEST_SHAPE);
        }

        return shape.isEmpty() ? Shapes.block() : shape;
    }

    @Override
    public VoxelShape getCollisionShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return Shapes.empty();
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
        return state;
    }

    private boolean isTopVine(LevelReader level, BlockPos pos) {
        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);
        return !(aboveState.getBlock() instanceof RoseVinesBlock);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return this.hasAnyFace(state);
    }

    @Override
    public boolean isLadder(
            BlockState state,
            LevelReader level,
            BlockPos pos,
            LivingEntity entity
    ) {
        return this.hasAnyFace(state);
    }

    @Override
    public void onPlace(
            BlockState state,
            Level level,
            BlockPos pos,
            BlockState oldState,
            boolean isMoving
    ) {
        if (
                !oldState.is(state.getBlock()) &&
                        this.getSoundType(state) instanceof
                                com.kingodogo.buildscape.block.CustomSoundType customSound
        ) {
            level.playSound(
                    null,
                    pos,
                    this.getSoundType(state).getPlaceSound(),
                    SoundSource.BLOCKS,
                    customSound.getPlaceVolume(),
                    customSound.getPlacePitch()
            );
        }
        super.onPlace(state, level, pos, oldState, isMoving);
    }

    @Override
    public List<ItemStack> getDrops(
            BlockState state,
            LootContext.Builder builder
    ) {
        LootContext ctx = builder
                .withParameter(
                        net.minecraft.world.level.storage.loot.parameters.LootContextParams.BLOCK_STATE,
                        state
                )
                .create(
                        net.minecraft.world.level.storage.loot.parameters.LootContextParamSets.BLOCK
                );
        ItemStack tool = ctx.getParamOrNull(
                net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL
        );

        if (tool != null && !tool.isEmpty()) {
            if (tool.getItem() instanceof ShearsItem) {
                return Collections.singletonList(new ItemStack(this));
            }
            if (
                    net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(
                            Enchantments.SILK_TOUCH,
                            tool
                    ) >
                            0
            ) {
                return Collections.singletonList(new ItemStack(this));
            }
        }

        return Collections.emptyList();
    }

    private boolean hasAnyFace(BlockState state) {
        return (
                state.getValue(UP) ||
                        state.getValue(DOWN) ||
                        state.getValue(NORTH) ||
                        state.getValue(SOUTH) ||
                        state.getValue(EAST) ||
                        state.getValue(WEST)
        );
    }

    private boolean canAttachTo(
            BlockGetter level,
            BlockPos pos,
            Direction direction
    ) {
        BlockPos attachedPos = pos.relative(direction);
        BlockState attachedState = level.getBlockState(attachedPos);
        Block block = attachedState.getBlock();

        if (attachedState.isAir()) {
            return false;
        }

        if (
                block instanceof RoseVinesBlock ||
                        block instanceof net.minecraft.world.level.block.VineBlock
        ) {
            return true;
        }

        return true;
    }

    private boolean isFullBlock(
            BlockGetter level,
            BlockPos pos,
            Direction direction
    ) {
        BlockPos checkPos = pos.relative(direction);
        BlockState checkState = level.getBlockState(checkPos);
        return checkState.isFaceSturdy(level, checkPos, direction.getOpposite());
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            Random random
    ) {
        if (random.nextInt(4) != 0) {
            return;
        }

        BlockState topState = this.findTopState(level, pos);

        Direction facingDir = this.getFacingDirection(topState);
        if (facingDir == null) {
            return;
        }

        BlockPos downPos = pos.below();
        BlockState downState = level.getBlockState(downPos);

        if (!downState.isAir()) {
            return;
        }

        if (!this.canAttachTo(level, downPos, Direction.UP)) {
            return;
        }

        BlockState newState =
                this.defaultBlockState()
                        .setValue(UP, false)
                        .setValue(DOWN, false)
                        .setValue(NORTH, false)
                        .setValue(SOUTH, false)
                        .setValue(EAST, false)
                        .setValue(WEST, false);

        BooleanProperty facingProperty = getPropertyForFace(facingDir);
        if (newState.hasProperty(facingProperty)) {
            newState = newState.setValue(facingProperty, true);
        }

        if (this.canSurvive(newState, level, downPos)) {
            level.setBlock(downPos, newState, 2);
        }
    }

    private BlockState findTopState(LevelReader level, BlockPos startPos) {
        BlockPos currentPos = startPos;
        BlockPos abovePos = currentPos.above();
        BlockState aboveState = level.getBlockState(abovePos);

        while (aboveState.getBlock() instanceof RoseVinesBlock) {
            currentPos = abovePos;
            abovePos = currentPos.above();
            aboveState = level.getBlockState(abovePos);
        }

        return level.getBlockState(currentPos);
    }

    private Direction getFacingDirection(BlockState state) {
        if (state.getValue(NORTH)) return Direction.NORTH;
        if (state.getValue(SOUTH)) return Direction.SOUTH;
        if (state.getValue(EAST)) return Direction.EAST;
        if (state.getValue(WEST)) return Direction.WEST;

        if (state.getValue(DOWN)) return Direction.DOWN;

        return null;
    }

    public static BooleanProperty getPropertyForFace(Direction direction) {
        return switch (direction) {
            case UP -> UP;
            case DOWN -> DOWN;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
        };
    }
}
