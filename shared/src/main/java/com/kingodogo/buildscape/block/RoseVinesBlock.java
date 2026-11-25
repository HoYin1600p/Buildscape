package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
public class RoseVinesBlock extends VineBlock implements ICommonInteractable {

    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final BooleanProperty SHEARED = ModBlockProperties.SHEARED;

    private static final VoxelShape UP_SHAPE = Block.box(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape DOWN_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    private static final VoxelShape NORTH_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
    private static final VoxelShape SOUTH_SHAPE = Block.box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape EAST_SHAPE = Block.box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape WEST_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);

    public RoseVinesBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(UP, false)
                .setValue(DOWN, false)
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false)
                .setValue(SHEARED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UP, DOWN, NORTH, SOUTH, EAST, WEST, SHEARED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos clickedPos = context.getClickedPos();
        BlockState blockState = context.getLevel().getBlockState(clickedPos);
        boolean isExistingVine = blockState.getBlock() instanceof RoseVinesBlock;

        Direction clickedFace = context.getClickedFace();
        Direction attachDirection = clickedFace.getOpposite();

        if (isExistingVine && context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            return null;
        }

        if (isExistingVine && !(context.getPlayer() != null && context.getPlayer().isShiftKeyDown())) {
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

                if (originalState.hasProperty(attachProperty) && originalState.getValue(attachProperty)) {
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
                    state = copyExistingFaces(blockState);
                }
            } else {
                state = this.defaultBlockState();
            }

            return state.setValue(DOWN, true);
        }

        if (clickedFace == Direction.DOWN) {
            BlockState state;
            if (isExistingVine) {
                if (blockState.getBlock() == this) {
                    state = blockState;
                } else {
                    state = copyExistingFaces(blockState);
                }
            } else {
                state = this.defaultBlockState();
            }

            return state.setValue(UP, true);
        }

        BlockState state;
        if (isExistingVine) {
            if (blockState.getBlock() == this) {
                BooleanProperty property = getPropertyForFace(attachDirection);
                if (blockState.hasProperty(property) && blockState.getValue(property)) {
                    return null;
                }
                state = blockState;
            } else {
                state = copyExistingFaces(blockState);
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

    private BlockState copyExistingFaces(BlockState blockState) {
        BlockState state = this.defaultBlockState();
        if (blockState.hasProperty(UP)) state = state.setValue(UP, blockState.getValue(UP));
        if (blockState.hasProperty(DOWN)) state = state.setValue(DOWN, blockState.getValue(DOWN));
        if (blockState.hasProperty(NORTH)) state = state.setValue(NORTH, blockState.getValue(NORTH));
        if (blockState.hasProperty(SOUTH)) state = state.setValue(SOUTH, blockState.getValue(SOUTH));
        if (blockState.hasProperty(EAST)) state = state.setValue(EAST, blockState.getValue(EAST));
        if (blockState.hasProperty(WEST)) state = state.setValue(WEST, blockState.getValue(WEST));
        return state;
    }

    @Override
    public InteractionResult onInteract(Level level, BlockPos pos, BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
        if (state.getValue(SHEARED)) {
            return InteractionResult.PASS;
        }

        ItemStack heldItem = player.getItemInHand(hand);
        if (heldItem.is(Items.SHEARS)) {
            level.setBlockAndUpdate(pos, state.setValue(SHEARED, true));
            level.playSound(null, pos, SoundEvents.GROWING_PLANT_CROP, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(player, GameEvent.SHEAR, pos);
            Services.PLATFORM.hurtAndBreak(heldItem, 1, player, hand);
            return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        net.minecraft.world.item.Item heldItem = context.getItemInHand().getItem();
        if (heldItem instanceof net.minecraft.world.item.BlockItem blockItem) {
            Block heldBlock = blockItem.getBlock();
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
                return !state.hasProperty(property) || !state.getValue(property);
            }

            return true;
        }

        return false;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = Shapes.empty();

        if (state.getValue(UP)) shape = Shapes.or(shape, UP_SHAPE);
        if (state.getValue(DOWN)) shape = Shapes.or(shape, DOWN_SHAPE);
        if (state.getValue(NORTH)) shape = Shapes.or(shape, NORTH_SHAPE);
        if (state.getValue(SOUTH)) shape = Shapes.or(shape, SOUTH_SHAPE);
        if (state.getValue(EAST)) shape = Shapes.or(shape, EAST_SHAPE);
        if (state.getValue(WEST)) shape = Shapes.or(shape, WEST_SHAPE);

        return shape.isEmpty() ? Shapes.block() : shape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return this.hasAnyFace(state);
    }

    public boolean canClimb(LevelReader level, BlockPos pos) {
        return this.hasAnyFace(level.getBlockState(pos));
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return !state.getValue(SHEARED);
    }

    public void onRandomTick(BlockState state, ServerLevel level, BlockPos pos) {
        if (state.getValue(SHEARED)) {
            return;
        }

        if (level.getRandom().nextInt(4) != 0) {
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

        BlockState newState = this.defaultBlockState()
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

    public boolean hasAnyFace(BlockState state) {
        return (state.getValue(UP)
                || state.getValue(DOWN)
                || state.getValue(NORTH)
                || state.getValue(SOUTH)
                || state.getValue(EAST)
                || state.getValue(WEST));
    }

    private boolean canAttachTo(BlockGetter level, BlockPos pos, Direction direction) {
        BlockPos attachedPos = pos.relative(direction);
        BlockState attachedState = level.getBlockState(attachedPos);
        Block block = attachedState.getBlock();

        if (attachedState.isAir()) {
            return false;
        }

        return block instanceof RoseVinesBlock || block instanceof VineBlock || true;
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
