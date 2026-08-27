package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.block.HollowLogBlock;
import com.kingodogo.buildscape.block.HollowPipeBlock;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LiquidBlockRenderer.class)
public class LiquidBlockRendererMixin {

    @Inject(method = "tesselate", at = @At("HEAD"), cancellable = true)
    private void buildscape$cancelPipeChunkFluidRender(
            BlockAndTintGetter level,
            BlockPos pos,
            VertexConsumer buffer,
            BlockState state,
            FluidState fluidState,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (state.getBlock() instanceof HollowPipeBlock || state.getBlock() instanceof HollowLogBlock) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isFaceOccludedByState", at = @At("HEAD"), cancellable = true)
    private static void buildscape$occludeFluidAgainstPipe(
            BlockGetter level,
            Direction face,
            float height,
            BlockPos pos,
            BlockState state,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (state.getBlock() instanceof HollowPipeBlock) {
            if (!HollowPipeBlock.isOpenEndpoint(state, face.getOpposite())) {
                cir.setReturnValue(true);
            }
        } else if (state.getBlock() instanceof HollowLogBlock) {
            if (!HollowLogBlock.isOpenEnd(state, face.getOpposite())) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "shouldRenderFace", at = @At("HEAD"), cancellable = true)
    private static void buildscape$cullWaterAgainstPipeWalls(
            BlockAndTintGetter level,
            BlockPos pos,
            FluidState fluidState,
            BlockState blockState,
            Direction face,
            FluidState neighborFluidState,
            CallbackInfoReturnable<Boolean> cir
    ) {
        BlockPos neighborPos = pos.relative(face);
        BlockState neighborState = level.getBlockState(neighborPos);
        if (neighborState.getBlock() instanceof HollowPipeBlock) {
            if (!HollowPipeBlock.isOpenEndpoint(neighborState, face.getOpposite())) {
                cir.setReturnValue(false);
            }
        } else if (neighborState.getBlock() instanceof HollowLogBlock) {
            if (!HollowLogBlock.isOpenEnd(neighborState, face.getOpposite())) {
                cir.setReturnValue(false);
            }
        }
    }
}

