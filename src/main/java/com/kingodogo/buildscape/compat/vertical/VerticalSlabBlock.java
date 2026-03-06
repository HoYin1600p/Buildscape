package com.kingodogo.buildscape.compat.vertical;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Random;

public class VerticalSlabBlock extends SlabBlock implements SimpleWaterloggedBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<SlabType> TYPE = BlockStateProperties.SLAB_TYPE;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape NORTH_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D);
    protected static final VoxelShape SOUTH_SHAPE = Block.box(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape EAST_SHAPE = Block.box(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape WEST_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 16.0D);

    private final Block parentBlock;

    public VerticalSlabBlock(Properties properties, Block parentBlock) {
        super(properties);
        this.parentBlock = parentBlock;
        this.registerDefaultState(this.defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(TYPE, SlabType.BOTTOM)
                .setValue(WATERLOGGED, false));
    }

    public Block getParentBlock() {
        return parentBlock;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE, WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(TYPE) == SlabType.DOUBLE) {
            return Shapes.block();
        }
        switch (state.getValue(FACING)) {
            case SOUTH: return SOUTH_SHAPE;
            case EAST: return EAST_SHAPE;
            case WEST: return WEST_SHAPE;
            default: return NORTH_SHAPE;
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        BlockState existingState = context.getLevel().getBlockState(pos);
        if (existingState.is(this)) {
            if (context.replacingClickedOnBlock()) {
                // Keep the same facing when making double
                return existingState.setValue(TYPE, SlabType.DOUBLE).setValue(WATERLOGGED, false);
            }
            return null;
        }

        FluidState fluidState = context.getLevel().getFluidState(pos);
        Direction facing = context.getHorizontalDirection();

        // Logic to decide which half to place based on hit location
        double x = context.getClickLocation().x - (double)pos.getX();
        double z = context.getClickLocation().z - (double)pos.getZ();
        Direction side = context.getClickedFace();

        if (side == Direction.NORTH) facing = Direction.SOUTH;
        else if (side == Direction.SOUTH) facing = Direction.NORTH;
        else if (side == Direction.WEST) facing = Direction.EAST;
        else if (side == Direction.EAST) facing = Direction.WEST;
        else {
            // Placing on top/bottom, use horizontal direction
            facing = context.getHorizontalDirection();
        }

        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(TYPE, SlabType.BOTTOM)
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
        ItemStack itemstack = useContext.getItemInHand();
        SlabType slabtype = state.getValue(TYPE);
        if (slabtype != SlabType.DOUBLE && itemstack.getItem() == this.asItem()) {
            if (useContext.replacingClickedOnBlock()) {
                Direction direction = useContext.getClickedFace();
                Direction facing = state.getValue(FACING);
                return (facing == Direction.NORTH && direction == Direction.SOUTH) ||
                       (facing == Direction.SOUTH && direction == Direction.NORTH) ||
                       (facing == Direction.WEST && direction == Direction.EAST) ||
                       (facing == Direction.EAST && direction == Direction.WEST);
            }
            return true;
        }
        return super.canBeReplaced(state, useContext);
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        return parentBlock.defaultBlockState().getDestroyProgress(player, level, pos);
    }

    @Override
    public boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player) {
        return parentBlock.canHarvestBlock(parentBlock.defaultBlockState(), level, pos, player);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    // Delegate properties to parent block for consistency
    @Override
    public SoundType getSoundType(BlockState state) {
        return parentBlock.getSoundType(parentBlock.defaultBlockState());
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return parentBlock.getFlammability(parentBlock.defaultBlockState(), level, pos, direction);
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return parentBlock.getFireSpreadSpeed(parentBlock.defaultBlockState(), level, pos, direction);
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return parentBlock.isFlammable(parentBlock.defaultBlockState(), level, pos, direction);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return parentBlock.getPistonPushReaction(parentBlock.defaultBlockState());
    }

    @Override
    public float getFriction() {
        return parentBlock.getFriction();
    }

    @Override
    public float getExplosionResistance() {
        return parentBlock.getExplosionResistance();
    }
}
