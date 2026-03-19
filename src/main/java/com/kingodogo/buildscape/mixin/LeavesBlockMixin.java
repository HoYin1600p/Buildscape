package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.world.ModGameRules;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LeavesBlock.class)
public abstract class LeavesBlockMixin extends Block implements SimpleWaterloggedBlock {

    public LeavesBlockMixin(Properties properties) {
        super(properties);
    }

    // --- Waterlogging ---

    @Inject(method = "createBlockStateDefinition", at = @At("TAIL"))
    private void buildscape$addWaterlogged(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(BlockStateProperties.WATERLOGGED);
    }


    @Inject(method = "getStateForPlacement", at = @At("RETURN"), cancellable = true)
    private void buildscape$waterloggedPlacement(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        BlockState state = cir.getReturnValue();
        if (state != null && state.hasProperty(BlockStateProperties.WATERLOGGED)) {
            FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
            cir.setReturnValue(state.setValue(BlockStateProperties.WATERLOGGED, fluidState.getType() == Fluids.WATER));
        }
    }

    @Inject(method = "updateShape", at = @At("HEAD"))
    private void buildscape$scheduleFluidTick(BlockState state, Direction direction, BlockState neighborState,
                                              LevelAccessor level, BlockPos pos, BlockPos neighborPos,
                                              CallbackInfoReturnable<BlockState> cir) {
        if (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)
                ? Fluids.WATER.getSource(false)
                : super.getFluidState(state);
    }

    // --- Fast Leaf Decay ---

    @Inject(method = "tick", at = @At("TAIL"))
    private void buildscape$onTick(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            java.util.Random random,
            CallbackInfo ci
    ) {
        if (level.getGameRules().getBoolean(ModGameRules.FAST_LEAF_DECAY)) {
            BlockState currentState = level.getBlockState(pos);
            if (currentState.getBlock() instanceof LeavesBlock) {
                BooleanProperty PERSISTENT = LeavesBlock.PERSISTENT;
                if (currentState.hasProperty(PERSISTENT) && !currentState.getValue(PERSISTENT)) {
                    LeavesBlock leavesBlock = (LeavesBlock) (Object) this;
                    leavesBlock.randomTick(currentState, level, pos, random);
                    BlockState afterTick = level.getBlockState(pos);
                    if (afterTick.getBlock() instanceof LeavesBlock &&
                            afterTick.hasProperty(PERSISTENT) &&
                        !afterTick.getValue(PERSISTENT)) {
                        level.scheduleTick(pos, leavesBlock, 6);
                    }
                }
            }
        }
    }
}
