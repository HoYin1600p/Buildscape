package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.ComponentHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
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

import java.util.Collections;
import java.util.List;
public abstract class StarBlock extends BushBlock implements SinksOnFarmland, SimpleWaterloggedBlock, ICommonInteractable, ICommonEntityInside {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final EnumProperty<Direction> VERTICAL_DIRECTION = BlockStateProperties.VERTICAL_DIRECTION;

    protected static final VoxelShape SHAPE_UP = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);
    protected static final VoxelShape SHAPE_DOWN = Block.box(2.0D, 3.0D, 2.0D, 14.0D, 16.0D, 14.0D);

    public StarBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(LIT, false)
                .setValue(WATERLOGGED, false)
                .setValue(VERTICAL_DIRECTION, Direction.UP)
                .setValue(ON_FARMLAND, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, WATERLOGGED, VERTICAL_DIRECTION, ON_FARMLAND);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(VERTICAL_DIRECTION);
        VoxelShape shape = direction == Direction.DOWN ? SHAPE_DOWN : SHAPE_UP;
        if (state.getValue(ON_FARMLAND) && direction == Direction.UP) {
            return shape.move(0, -0.0625D, 0);
        }
        return shape;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return !state.isAir() || state.is(net.minecraft.world.level.block.Blocks.FARMLAND);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace();
        BlockPos placePos = context.getClickedPos();
        Level level = context.getLevel();

        FluidState fluidState = level.getFluidState(placePos);
        Direction verticalDirection;

        if (clickedFace == Direction.DOWN) {
            verticalDirection = Direction.DOWN;
        } else if (clickedFace == Direction.UP) {
            verticalDirection = Direction.UP;
        } else {
            BlockState supportAbove = level.getBlockState(placePos.above());
            BlockState supportBelow = level.getBlockState(placePos.below());

            if (supportAbove.isAir() && supportBelow.isAir()) {
                return null;
            }

            boolean hasAboveSupport = !supportAbove.isAir() && supportAbove.isFaceSturdy(level, placePos.above(), Direction.DOWN);
            boolean hasBelowSupport = !supportBelow.isAir() && supportBelow.isFaceSturdy(level, placePos.below(), Direction.UP);

            if (hasAboveSupport && !hasBelowSupport) {
                verticalDirection = Direction.DOWN;
            } else if (hasBelowSupport && !hasAboveSupport) {
                verticalDirection = Direction.UP;
            } else if (hasAboveSupport && hasBelowSupport) {
                verticalDirection = Direction.DOWN;
            } else {
                verticalDirection = !supportAbove.isAir() ? Direction.DOWN : Direction.UP;
            }
        }

        return this.defaultBlockState()
                .setValue(LIT, false)
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER)
                .setValue(VERTICAL_DIRECTION, verticalDirection)
                .setValue(ON_FARMLAND, verticalDirection == Direction.UP && shouldSink(level, placePos));
    }

    public BlockState onUpdateShape(BlockState state, Direction direction, BlockState neighborState, LevelReader level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED) && level instanceof LevelAccessor la) {
            la.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(la));
        }
        if (direction == Direction.DOWN) {
            return state.setValue(ON_FARMLAND, state.getValue(VERTICAL_DIRECTION) == Direction.UP && shouldSink(level, pos));
        }
        return state;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public InteractionResult onInteract(Level level, BlockPos pos, BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
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

    @Override
    public void onEntityInside(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof Player player) {
            if (Services.PLATFORM.isOnGround(player) && level.isClientSide()) {
                float stepInterval = 2.0F;
                int currentStep = (int) (Services.PLATFORM.getWalkDist(player) / stepInterval);
                int lastStep = (int) (Services.PLATFORM.getWalkDistO(player) / stepInterval);

                if (currentStep != lastStep && Services.PLATFORM.getWalkDist(player) > Services.PLATFORM.getWalkDistO(player)) {
                    float volume = 0.15F;
                    float pitch = 1.0F;
                    level.playLocalSound(
                            pos.getX() + 0.5D,
                            pos.getY() + 0.5D,
                            pos.getZ() + 0.5D,
                            SoundType.AMETHYST.getStepSound(),
                            SoundSource.BLOCKS,
                            volume,
                            pitch,
                            false
                    );
                }
            }
        }
    }

    public List<ItemStack> getReferenceDrops() {
        return Collections.singletonList(new ItemStack(this));
    }
}
