package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Random;

public class HangingMossBlock extends Block implements BonemealableBlock {
    public static final BooleanProperty TIP = BooleanProperty.create("tip");
    public static final BooleanProperty SHEARED = ModBlockProperties.SHEARED;
    protected static final VoxelShape TIP_SHAPE = box(1.0D, 2.0D, 1.0D, 15.0D, 16.0D, 15.0D);
    protected static final VoxelShape BODY_SHAPE = box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public HangingMossBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(TIP, true).setValue(SHEARED, false));
    }

    @Override
    public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(TIP) ? TIP_SHAPE : BODY_SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);
        return aboveState.is(this) || aboveState.is(BlockTags.LEAVES) || aboveState.getBlock() instanceof LeavesBlock || aboveState.isFaceSturdy(level, abovePos, Direction.DOWN);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        LevelReader level = context.getLevel();
        if (this.canSurvive(this.defaultBlockState(), level, pos)) {
            boolean isTip = !level.getBlockState(pos.below()).is(this);
            return this.defaultBlockState().setValue(TIP, isTip).setValue(SHEARED, false);
        }
        return null;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState adjacentState, LevelAccessor level, BlockPos pos, BlockPos adjacentPos) {
        if (direction == Direction.UP && !state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        if (direction == Direction.DOWN) {
            boolean isTip = !adjacentState.is(this);
            return state.setValue(TIP, isTip);
        }
        return super.updateShape(state, direction, adjacentState, level, pos, adjacentPos);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(TIP) && !state.getValue(SHEARED);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        if (state.getValue(TIP) && !state.getValue(SHEARED)) {
            if (random.nextInt(10) == 0) {
                BlockPos growPos = pos.below();
                BlockState growState = level.getBlockState(growPos);
                if (growState.isAir() || growState.getMaterial().isReplaceable()) {
                    level.setBlock(growPos, this.defaultBlockState().setValue(TIP, true).setValue(SHEARED, false), 3);
                    level.setBlock(pos, state.setValue(TIP, false), 3);
                }
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (held.getItem() instanceof ShearsItem) {
            BlockPos tipPos = pos;
            while (level.getBlockState(tipPos.below()).is(this)) {
                tipPos = tipPos.below();
            }
            BlockState tipState = level.getBlockState(tipPos);
            if (tipState.hasProperty(SHEARED) && !tipState.getValue(SHEARED)) {
                if (!level.isClientSide) {
                    level.setBlockAndUpdate(tipPos, tipState.setValue(SHEARED, true));
                    level.playSound(null, tipPos, SoundEvents.GROWING_PLANT_CROP, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.gameEvent(player, GameEvent.SHEAR, tipPos);
                    held.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
        BlockPos tipPos = pos;
        while (level.getBlockState(tipPos.below()).is(this)) {
            tipPos = tipPos.below();
        }
        BlockState tipState = level.getBlockState(tipPos);
        if (tipState.hasProperty(SHEARED) && tipState.getValue(SHEARED)) {
            return false;
        }
        BlockPos growPos = tipPos.below();
        BlockState growState = level.getBlockState(growPos);
        return growState.isAir() || growState.getMaterial().isReplaceable();
    }

    @Override
    public boolean isBonemealSuccess(Level level, Random random, BlockPos pos, BlockState state) {
        BlockPos tipPos = pos;
        while (level.getBlockState(tipPos.below()).is(this)) {
            tipPos = tipPos.below();
        }
        BlockState tipState = level.getBlockState(tipPos);
        return !(tipState.hasProperty(SHEARED) && tipState.getValue(SHEARED));
    }

    @Override
    public void performBonemeal(ServerLevel level, Random random, BlockPos pos, BlockState state) {
        BlockPos tipPos = pos;
        while (level.getBlockState(tipPos.below()).is(this)) {
            tipPos = tipPos.below();
        }
        BlockState tipState = level.getBlockState(tipPos);
        if (tipState.hasProperty(SHEARED) && tipState.getValue(SHEARED)) {
            return;
        }
        BlockPos growPos = tipPos.below();
        BlockState growState = level.getBlockState(growPos);
        if (growState.isAir() || growState.getMaterial().isReplaceable()) {
            level.setBlock(growPos, this.defaultBlockState().setValue(TIP, true).setValue(SHEARED, false), 3);
            level.setBlock(tipPos, tipState.setValue(TIP, false), 3);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TIP, SHEARED);
    }
}

