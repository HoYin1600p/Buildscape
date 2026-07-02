package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.block.vertical.ComplexFacing;
import com.kingodogo.buildscape.block.vertical.VerticalStairShape;
import com.kingodogo.buildscape.block.vertical.VerticalStairShapeProperty;
import com.kingodogo.buildscape.block.vertical.VerticalStairShapeState;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VerticalStairBlock extends Block implements SimpleWaterloggedBlock {
    public static final VerticalStairShapeProperty FRONT_TOP_SHAPE = new VerticalStairShapeProperty();
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final float INNER_EDGE = 0.25F;
    private static final VerticalStairShapeState DEFAULT_SHAPE = VerticalStairShapeState.of(
            ComplexFacing.NORTH_UP, VerticalStairShape.STRAIGHT);

    private final Block baseBlock;

    public VerticalStairBlock(BlockState baseState, BlockBehaviour.Properties properties) {
        super(properties);
        this.baseBlock = baseState.getBlock();
        this.registerDefaultState(this.defaultBlockState()
                .setValue(FRONT_TOP_SHAPE, DEFAULT_SHAPE)
                .setValue(WATERLOGGED, Boolean.FALSE));
    }

    public Block getBaseBlock() {
        return this.baseBlock;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FRONT_TOP_SHAPE, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        ComplexFacing facing = getFacing(context.getClickedFace(), context.getClickLocation(), context.getClickedPos());
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState()
                .setValue(FRONT_TOP_SHAPE, VerticalStairShapeState.of(
                        facing, getShape(facing, context.getLevel(), context.getClickedPos())))
                .setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level,
            BlockPos currentPos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        ComplexFacing facing = state.getValue(FRONT_TOP_SHAPE).facing;
        return state.setValue(FRONT_TOP_SHAPE, VerticalStairShapeState.of(facing, getShape(facing, level, currentPos)));
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FRONT_TOP_SHAPE,
                VerticalStairShapeState.transform(state.getValue(FRONT_TOP_SHAPE), rotation::rotate));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        VerticalStairShapeState shapeState = state.getValue(FRONT_TOP_SHAPE);
        Rotation rotation = mirror.getRotation(shapeState.facing.forward);
        return this.rotate(state, rotation);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VerticalStairShapeState shapeState = state.getValue(FRONT_TOP_SHAPE);
        return shapeState.shape.getVoxelShape(shapeState.facing);
    }

    private static ComplexFacing getFacing(Direction out, Vec3 hitPos, Vec3i blockPos) {
        return getFacing(out,
                (float) (hitPos.x - blockPos.getX() - 0.5D),
                (float) (hitPos.y - blockPos.getY() - 0.5D),
                (float) (hitPos.z - blockPos.getZ() - 0.5D));
    }

    private static ComplexFacing getFacing(Direction out, float hitX, float hitY, float hitZ) {
        return switch (out.getAxis()) {
            case X -> getFacingFromSide(hitZ, hitY, Direction.SOUTH, Direction.UP, out);
            case Y -> getFacingFromSide(hitX, hitZ, Direction.EAST, Direction.SOUTH, out);
            case Z -> getFacingFromSide(hitX, hitY, Direction.EAST, Direction.UP, out);
        };
    }

    private static ComplexFacing getFacingFromSide(float localX, float localY, Direction localRight,
            Direction localUp, Direction localOut) {
        if (localY > localX) {
            if (localY > -localX) {
                return getFacingFromQuarter(localX, localY, localRight, localUp, localOut);
            }
            return getFacingFromQuarter(localY, -localX, localUp, localRight.getOpposite(), localOut);
        }

        if (localY > -localX) {
            return getFacingFromQuarter(-localY, localX, localUp.getOpposite(), localRight, localOut);
        }
        return getFacingFromQuarter(-localX, -localY, localRight.getOpposite(), localUp.getOpposite(), localOut);
    }

    private static ComplexFacing getFacingFromQuarter(float localX, float localY, Direction localRight,
            Direction localUp, Direction localOut) {
        Direction forward;
        Direction up;
        if (localY > INNER_EDGE) {
            up = localUp.getOpposite();
            if (localX < -INNER_EDGE) {
                forward = localRight;
            } else if (localX > INNER_EDGE) {
                forward = localRight.getOpposite();
            } else {
                forward = localOut;
            }
        } else {
            up = localOut;
            forward = localUp.getOpposite();
        }
        return ComplexFacing.forFacing(forward, up);
    }

    private VerticalStairShape getShape(ComplexFacing facing, BlockGetter level, BlockPos pos) {
        boolean connectedLeft = false;
        boolean connectedRight = false;

        ComplexFacing leftFacing = getFacingOrNull(level.getBlockState(pos.relative(facing.left)));
        if (leftFacing != null && (leftFacing == facing || leftFacing.flipped() == facing)) {
            connectedLeft = true;
        }

        ComplexFacing rightFacing = getFacingOrNull(level.getBlockState(pos.relative(facing.right)));
        if (rightFacing != null && (rightFacing == facing || rightFacing.flipped() == facing)) {
            connectedRight = true;
        }

        if (connectedLeft && connectedRight) {
            return VerticalStairShape.STRAIGHT;
        }

        ComplexFacing behindFacing = getFacingOrNull(level.getBlockState(pos.relative(facing.backward)));
        if (behindFacing != null) {
            if (behindFacing.up != facing.up) {
                behindFacing = behindFacing.flipped();
            }
            if (behindFacing.up == facing.up) {
                ComplexFacing belowFacing = getFacingOrNull(level.getBlockState(pos.relative(facing.down)));
                if (belowFacing != null) {
                    if (belowFacing.forward != facing.forward) {
                        belowFacing = belowFacing.flipped();
                    }
                    if (belowFacing.forward == facing.forward) {
                        if (belowFacing.up == behindFacing.forward) {
                            if (belowFacing.up == facing.left && !connectedLeft) {
                                return VerticalStairShape.OUTER_BOTH_LEFT;
                            }
                            if (belowFacing.up == facing.right && !connectedRight) {
                                return VerticalStairShape.OUTER_BOTH_RIGHT;
                            }
                        } else if (!connectedLeft && !connectedRight && belowFacing.down == behindFacing.forward) {
                            if (belowFacing.down == facing.left) {
                                return VerticalStairShape.OUTER_BACK_RIGHT_BOTTOM_LEFT;
                            }
                            if (belowFacing.down == facing.right) {
                                return VerticalStairShape.OUTER_BACK_LEFT_BOTTOM_RIGHT;
                            }
                        }
                    }
                }

                if (behindFacing.forward == facing.left && !connectedLeft) {
                    return VerticalStairShape.OUTER_BACK_LEFT;
                }
                if (behindFacing.forward == facing.right && !connectedRight) {
                    return VerticalStairShape.OUTER_BACK_RIGHT;
                }
            }
        }

        ComplexFacing belowFacing = getFacingOrNull(level.getBlockState(pos.relative(facing.down)));
        if (belowFacing != null) {
            if (belowFacing.forward != facing.forward) {
                belowFacing = belowFacing.flipped();
            }
            if (belowFacing.forward == facing.forward) {
                if (belowFacing.up == facing.left && !connectedLeft) {
                    return VerticalStairShape.OUTER_BOTTOM_LEFT;
                }
                if (belowFacing.up == facing.right && !connectedRight) {
                    return VerticalStairShape.OUTER_BOTTOM_RIGHT;
                }
            }
        }

        ComplexFacing inFrontFacing = getFacingOrNull(level.getBlockState(pos.relative(facing.forward)));
        if (inFrontFacing != null) {
            if (inFrontFacing.up != facing.up) {
                inFrontFacing = inFrontFacing.flipped();
            }
            if (inFrontFacing.up == facing.up) {
                ComplexFacing aboveFacing = getFacingOrNull(level.getBlockState(pos.relative(facing.up)));
                if (aboveFacing != null) {
                    if (aboveFacing.forward != facing.forward) {
                        aboveFacing = aboveFacing.flipped();
                    }
                    if (aboveFacing.forward == facing.forward && aboveFacing.up == inFrontFacing.forward) {
                        if (aboveFacing.up == facing.left) {
                            return VerticalStairShape.INNER_BOTH_LEFT;
                        }
                        if (aboveFacing.up == facing.right) {
                            return VerticalStairShape.INNER_BOTH_RIGHT;
                        }
                    }
                }

                if (inFrontFacing.forward == facing.left) {
                    return VerticalStairShape.INNER_FRONT_LEFT;
                }
                if (inFrontFacing.forward == facing.right) {
                    return VerticalStairShape.INNER_FRONT_RIGHT;
                }
            }
        }

        ComplexFacing aboveFacing = getFacingOrNull(level.getBlockState(pos.relative(facing.up)));
        if (aboveFacing != null) {
            if (aboveFacing.forward != facing.forward) {
                aboveFacing = aboveFacing.flipped();
            }
            if (aboveFacing.forward == facing.forward) {
                if (aboveFacing.up == facing.left) {
                    return VerticalStairShape.INNER_TOP_LEFT;
                }
                if (aboveFacing.up == facing.right) {
                    return VerticalStairShape.INNER_TOP_RIGHT;
                }
            }
        }

        return VerticalStairShape.STRAIGHT;
    }

    private static ComplexFacing getFacingOrNull(BlockState state) {
        if (state.getBlock() instanceof VerticalStairBlock) {
            return state.getValue(FRONT_TOP_SHAPE).facing;
        }
        return null;
    }
}
