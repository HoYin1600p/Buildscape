package com.kingodogo.buildscape.block;

import java.util.Collections;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
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
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StarBlock extends BushBlock implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED =
            BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final net.minecraft.world.level.block.state.properties.DirectionProperty VERTICAL_DIRECTION =
            BlockStateProperties.VERTICAL_DIRECTION;

    protected static final VoxelShape SHAPE_UP = Block.box(
            2.0D,
            0.0D,
            2.0D,
            14.0D,
            13.0D,
            14.0D
    );
    protected static final VoxelShape SHAPE_DOWN = Block.box(
            2.0D,
            3.0D,
            2.0D,
            14.0D,
            16.0D,
            14.0D
    );

    public StarBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(LIT, false)
                        .setValue(WATERLOGGED, false)
                        .setValue(VERTICAL_DIRECTION, Direction.UP)
        );
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(LIT, WATERLOGGED, VERTICAL_DIRECTION);
    }

    @Override
    public VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        Direction direction = state.getValue(VERTICAL_DIRECTION);
        return direction == Direction.DOWN ? SHAPE_DOWN : SHAPE_UP;
    }

    @Override
    protected boolean mayPlaceOn(
            BlockState state,
            BlockGetter level,
            BlockPos pos
    ) {
        return !state.isAir();
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace();
        BlockPos clickedPos = context.getClickedPos();
        Level level = context.getLevel();
        BlockPos placePos = clickedPos.relative(clickedFace);

        BlockState placeState = level.getBlockState(placePos);
        if (!placeState.canBeReplaced(context)) {
            return null;
        }

        FluidState fluidstate = level.getFluidState(placePos);

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

            boolean hasAboveSupport =
                    !supportAbove.isAir() &&
                            supportAbove.isFaceSturdy(level, placePos.above(), Direction.DOWN);
            boolean hasBelowSupport =
                    !supportBelow.isAir() &&
                            supportBelow.isFaceSturdy(level, placePos.below(), Direction.UP);

            if (hasAboveSupport && !hasBelowSupport) {
                verticalDirection = Direction.DOWN;
            } else if (hasBelowSupport && !hasAboveSupport) {
                verticalDirection = Direction.UP;
            } else if (hasAboveSupport && hasBelowSupport) {
                verticalDirection = Direction.DOWN;
            } else {
                if (!supportAbove.isAir()) {
                    verticalDirection = Direction.DOWN;
                } else {
                    verticalDirection = Direction.UP;
                }
            }
        }

        return this.defaultBlockState()
                .setValue(LIT, false)
                .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER)
                .setValue(VERTICAL_DIRECTION, verticalDirection);
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
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(
                state,
                direction,
                neighborState,
                level,
                pos,
                neighborPos
        );
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED)
                ? Fluids.WATER.getSource(false)
                : super.getFluidState(state);
    }

    @Override
    public int getLightEmission(
            BlockState state,
            BlockGetter level,
            BlockPos pos
    ) {
        return state.getValue(LIT) ? 15 : 0;
    }

    @Override
    public InteractionResult use(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        boolean currentLit = state.getValue(LIT);
        boolean newLit = !currentLit;
        BlockState newState = state.setValue(LIT, newLit);
        level.setBlock(pos, newState, 3);

        level.playSound(
                null,
                pos,
                currentLit
                        ? net.minecraft.sounds.SoundEvents.STONE_BUTTON_CLICK_OFF
                        : net.minecraft.sounds.SoundEvents.STONE_BUTTON_CLICK_ON,
                net.minecraft.sounds.SoundSource.BLOCKS,
                0.3f,
                1.0f
        );

        net.minecraft.network.chat.TextComponent message =
                new net.minecraft.network.chat.TextComponent(
                        newLit ? "Turned On" : "Turned Off"
                );
        message.withStyle(
                newLit
                        ? net.minecraft.ChatFormatting.GREEN
                        : net.minecraft.ChatFormatting.RED
        );
        player.displayClientMessage(message, true);

        return InteractionResult.SUCCESS;
    }

    @Override
    public void entityInside(
            BlockState state,
            Level level,
            BlockPos pos,
            Entity entity
    ) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (player.isOnGround() && level.isClientSide) {
                float stepInterval = 2.0f;
                int currentStep = (int) (player.walkDist / stepInterval);
                int lastStep = (int) (player.walkDistO / stepInterval);

                if (currentStep != lastStep && player.walkDist > player.walkDistO) {
                    float volume = 0.15f;
                    float pitch = 1.0f;

                    net.minecraft.sounds.SoundEvent amethystStepSound =
                            net.minecraft.world.level.block.SoundType.AMETHYST.getStepSound();

                    level.playLocalSound(
                            pos.getX() + 0.5,
                            pos.getY() + 0.5,
                            pos.getZ() + 0.5,
                            amethystStepSound,
                            SoundSource.BLOCKS,
                            volume,
                            pitch,
                            false
                    );
                }
            }
        }
        super.entityInside(state, level, pos, entity);
    }

    @Override
    public List<ItemStack> getDrops(
            BlockState state,
            LootContext.Builder builder
    ) {
        return Collections.singletonList(new ItemStack(this));
    }
}
