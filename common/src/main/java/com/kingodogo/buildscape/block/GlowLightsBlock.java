package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
public class GlowLightsBlock extends VineBlock implements EntityBlock, SimpleWaterloggedBlock, ICommonInteractable {

    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;

    private static final VoxelShape SHAPE_NORTH = Block.box(0, 0, 0, 16, 16, 1);
    private static final VoxelShape SHAPE_SOUTH = Block.box(0, 0, 15, 16, 16, 16);
    private static final VoxelShape SHAPE_EAST = Block.box(15, 0, 0, 16, 16, 16);
    private static final VoxelShape SHAPE_WEST = Block.box(0, 0, 0, 1, 16, 16);
    private static final VoxelShape SHAPE_UP = Block.box(0, 15, 0, 16, 16, 16);
    private static final VoxelShape SHAPE_DOWN = Block.box(0, 0, 0, 16, 1, 16);

    public GlowLightsBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false)
                .setValue(WATERLOGGED, false)
                .setValue(LIT, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DOWN, WATERLOGGED, LIT);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = Shapes.empty();
        if (state.getValue(NORTH)) shape = Shapes.or(shape, SHAPE_NORTH);
        if (state.getValue(SOUTH)) shape = Shapes.or(shape, SHAPE_SOUTH);
        if (state.getValue(EAST)) shape = Shapes.or(shape, SHAPE_EAST);
        if (state.getValue(WEST)) shape = Shapes.or(shape, SHAPE_WEST);
        if (state.getValue(UP)) shape = Shapes.or(shape, SHAPE_UP);
        if (state.getValue(DOWN)) shape = Shapes.or(shape, SHAPE_DOWN);
        return shape.isEmpty() ? Shapes.block() : shape;
    }


    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return hasFaces(state);
    }

    public static boolean hasFaces(BlockState state) {
        return state.getValue(NORTH) || state.getValue(EAST) || state.getValue(SOUTH)
                || state.getValue(WEST) || state.getValue(UP) || state.getValue(DOWN);
    }

    public static boolean canAttachTo(BlockGetter level, BlockState state, BlockPos pos, Direction direction) {
        if (state.isAir()) {
            return false;
        }
        if (state.isFaceSturdy(level, pos, direction)) {
            return true;
        }
        return Block.isFaceFull(state.getCollisionShape(level, pos), direction);
    }

    public static BlockState updateGlowLightShape(BlockState state, Direction direction, BlockState neighborState, BlockGetter level, BlockPos neighborPos) {
        BooleanProperty faceProperty = direction == Direction.DOWN ? DOWN : (direction == Direction.UP ? UP : getPropertyForFace(direction));
        if (faceProperty != null && state.hasProperty(faceProperty) && state.getValue(faceProperty)) {
            if (!canAttachTo(level, neighborState, neighborPos, direction.getOpposite())) {
                state = state.setValue(faceProperty, false);
            }
        }
        if (!hasFaces(state)) {
            return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        ItemStack heldItem = context.getItemInHand();
        if (heldItem.getItem() instanceof net.minecraft.world.item.BlockItem blockItem) {
            if (blockItem.getBlock() instanceof GlowLightsBlock) {
                if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
                    return false;
                }
                Direction clickedFace = context.getClickedFace();
                Direction attachDir = clickedFace.getOpposite();
                BooleanProperty prop = attachDir == Direction.DOWN ? DOWN : (attachDir == Direction.UP ? UP : getPropertyForFace(attachDir));
                if (prop != null && state.hasProperty(prop)) {
                    return !state.getValue(prop);
                }
                return true;
            }
        }
        return super.canBeReplaced(state, context);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos clickedPos = context.getClickedPos();
        Direction clickedFace = context.getClickedFace();
        Direction attachDir = clickedFace.getOpposite();
        FluidState fluid = context.getLevel().getFluidState(clickedPos);

        BlockPos attachPos = clickedPos.relative(attachDir);
        BlockState attachState = context.getLevel().getBlockState(attachPos);
        if (!canAttachTo(context.getLevel(), attachState, attachPos, clickedFace)) {
            return null;
        }

        BlockState existing = context.getLevel().getBlockState(clickedPos);
        BlockState state;
        if (existing.getBlock() instanceof GlowLightsBlock) {
            state = existing;
        } else {
            state = this.defaultBlockState().setValue(WATERLOGGED, fluid.getType() == Fluids.WATER).setValue(LIT, true);
        }

        BooleanProperty prop = attachDir == Direction.DOWN ? DOWN : (attachDir == Direction.UP ? UP : getPropertyForFace(attachDir));
        if (prop != null) {
            state = state.setValue(prop, true);
        }
        return hasFaces(state) ? state : null;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GlowLightsBlockEntity(pos, state);
    }

    @Override
    public InteractionResult onInteract(Level level, BlockPos pos, BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (!heldItem.isEmpty()) {
            net.minecraft.world.item.DyeColor dyeColor = com.kingodogo.buildscape.platform.Services.PLATFORM.getDyeColor(heldItem);
            if (dyeColor != null) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof GlowLightsBlockEntity glowBE) {
                    int color = com.kingodogo.buildscape.platform.Services.PLATFORM.getDyeColorValue(dyeColor);
                    String hex = String.format("#%06X", (0xFFFFFF & color));
                    if (glowBE.addDyeColor(hex)) {
                        if (!player.getAbilities().instabuild) {
                            heldItem.shrink(1);
                        }
                        Services.PLATFORM.playDyeUse(level, pos);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }

        if (heldItem.is(Items.SHEARS)) {
            boolean lit = !state.getValue(LIT);
            level.setBlock(pos, state.setValue(LIT, lit), 3);
            Services.PLATFORM.playShear(level, pos);
            return InteractionResult.SUCCESS;
        }

        if (heldItem.isEmpty()) {
            boolean lit = !state.getValue(LIT);
            level.setBlock(pos, state.setValue(LIT, lit), 3);
            Services.PLATFORM.playStoneButtonClick(level, pos, lit);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
