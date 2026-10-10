package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.entity.SeatEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
public class CushionBlock extends Block implements SimpleWaterloggedBlock, ICommonInteractable {
    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape BOTTOM_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D);
    protected static final VoxelShape TOP_SHAPE = Block.box(0.0D, 13.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public CushionBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(HALF, Half.BOTTOM)
                .setValue(WATERLOGGED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HALF) == Half.TOP ? TOP_SHAPE : BOTTOM_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HALF) == Half.TOP ? TOP_SHAPE : BOTTOM_SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockPos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        FluidState fluidState = context.getLevel().getFluidState(blockPos);

        BlockState state = this.defaultBlockState().setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
        if (direction == Direction.DOWN || (direction != Direction.UP && context.getClickLocation().y - (double) blockPos.getY() > 0.5D)) {
            return state.setValue(HALF, Half.TOP);
        }
        return state.setValue(HALF, Half.BOTTOM);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public InteractionResult onInteract(Level level, BlockPos pos, BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND || player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        ItemStack held = player.getItemInHand(hand);
        if (!held.isEmpty() && held.getItem() instanceof BlockItem) {
            return InteractionResult.PASS;
        }
        return sitPlayer(state, level, pos, player);
    }

    private InteractionResult sitPlayer(BlockState state, Level level, BlockPos pos, Player player) {
        if (!player.isShiftKeyDown()) {
            if (player.isPassenger() && !(player.getVehicle() instanceof SeatEntity)) {
                return InteractionResult.PASS;
            }

            List<Entity> seats = level.getEntitiesOfClass(Entity.class, new AABB(pos), seat -> seat instanceof SeatEntity && !seat.getPassengers().isEmpty());
            if (!seats.isEmpty()) {
                return InteractionResult.PASS;
            }

            if (!level.isClientSide()) {
                double sitHeight = state.getValue(HALF) == Half.TOP ? 0.725D : -0.075D;
                SeatEntity.createSeat(level, pos.getX() + 0.5D, pos.getY() + sitHeight, pos.getZ() + 0.5D, player);
            }
            return com.kingodogo.buildscape.platform.Services.PLATFORM.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }

    public void bounceUp(Entity entity) {
        Vec3 movement = entity.getDeltaMovement();
        if (movement.y < 0.0D) {
            double multiplier = entity instanceof LivingEntity ? 0.6D : 0.48D;
            entity.setDeltaMovement(movement.x, -movement.y * multiplier, movement.z);
        }
    }
}
