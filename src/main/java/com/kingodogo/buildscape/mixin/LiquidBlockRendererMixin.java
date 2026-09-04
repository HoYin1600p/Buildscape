package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.block.HollowLogBlock;
import com.kingodogo.buildscape.block.HollowPipeBlock;
import com.kingodogo.buildscape.client.renderer.PipeSpillVertexConsumer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LiquidBlockRenderer.class)
public class LiquidBlockRendererMixin {
    @ModifyVariable(method = "tesselate", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private VertexConsumer buildscape$pipeOutletSpill(VertexConsumer original,
            BlockAndTintGetter level, BlockPos pos, VertexConsumer buffer, BlockState state, FluidState fluid) {
        return PipeSpillVertexConsumer.wrap(original, level, pos, state, fluid);
    }

    @Inject(method = "tesselate", at = @At("HEAD"), cancellable = true)
    private void buildscape$cancelHollowChunkFluidRender(
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

    @Inject(method = "getHeight(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/material/Fluid;Lnet/minecraft/core/BlockPos;)F", at = @At("HEAD"), cancellable = true)
    private void buildscape$hollowFluidHeight(
            BlockAndTintGetter level,
            Fluid fluid,
            BlockPos pos,
            CallbackInfoReturnable<Float> cir
    ) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof HollowPipeBlock || state.getBlock() instanceof HollowLogBlock) {
            cir.setReturnValue(-1.0F);
            return;
        }
    }
}
