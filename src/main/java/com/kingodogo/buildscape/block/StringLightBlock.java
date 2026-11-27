package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.ComponentHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Locale;
public class StringLightBlock extends Block implements SimpleWaterloggedBlock, ICommonInteractable {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<OrnamentBlock.StringColor> STRING_COLOR = EnumProperty.create("string_color", OrnamentBlock.StringColor.class);
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE_NORTH = Block.box(0, 12, 0, 16, 15, 2);
    private static final VoxelShape SHAPE_SOUTH = Block.box(0, 12, 14, 16, 15, 16);
    private static final VoxelShape SHAPE_EAST = Block.box(14, 12, 0, 16, 15, 16);
    private static final VoxelShape SHAPE_WEST = Block.box(0, 12, 0, 2, 15, 16);

    public StringLightBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(STRING_COLOR, OrnamentBlock.StringColor.BLACK)
                .setValue(LIT, false)
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STRING_COLOR, LIT, FACING, WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        return switch (facing) {
            case SOUTH -> SHAPE_SOUTH;
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
            case NORTH -> SHAPE_NORTH;
            default -> SHAPE_NORTH;
        };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        Direction clickedFace = context.getClickedFace();

        if (clickedFace.getAxis().isHorizontal()) {
            return this.defaultBlockState()
                    .setValue(STRING_COLOR, OrnamentBlock.StringColor.BLACK)
                    .setValue(LIT, false)
                    .setValue(FACING, clickedFace.getOpposite())
                    .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
        }

        return this.defaultBlockState()
                .setValue(STRING_COLOR, OrnamentBlock.StringColor.BLACK)
                .setValue(LIT, false)
                .setValue(FACING, context.getHorizontalDirection())
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    public BlockState onUpdateShape(BlockState state, Direction direction, BlockState neighborState, LevelReader level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED) && level instanceof LevelAccessor la) {
            la.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(la));
        }
        return state;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return false;
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        return adjacentBlockState.is(this) || super.skipRendering(state, adjacentBlockState, side);
    }

    @Override
    public InteractionResult onInteract(Level level, BlockPos pos, BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ItemStack heldItem = player.getItemInHand(hand);
        if (!heldItem.isEmpty()) {
            DyeColor dyeColor = Services.PLATFORM.getDyeColor(heldItem);
            if (dyeColor != null) {
                OrnamentBlock.StringColor color = OrnamentBlock.StringColor.valueOf(dyeColor.getName().toUpperCase(Locale.ROOT));
                BlockState newState = state.setValue(STRING_COLOR, color);
                level.setBlock(pos, newState, 3);
                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }
                Services.PLATFORM.playDyeUse(level, pos);
                Component message = ComponentHelper.literal("String colored: " + color.getDisplayName()).withStyle(ChatFormatting.GREEN);
                Services.PLATFORM.sendActionBarMessage(player, message);
                return InteractionResult.SUCCESS;
            }
        }

        boolean currentLit = state.getValue(LIT);
        boolean newLit = !currentLit;
        BlockState newState = state.setValue(LIT, newLit);
        level.setBlock(pos, newState, 3);

        Services.PLATFORM.playStoneButtonClick(level, pos, newLit);

        Component message = ComponentHelper.literal(newLit ? "Turned On" : "Turned Off")
                .withStyle(newLit ? ChatFormatting.GREEN : ChatFormatting.RED);
        Services.PLATFORM.sendActionBarMessage(player, message);

        return InteractionResult.SUCCESS;
    }
}
