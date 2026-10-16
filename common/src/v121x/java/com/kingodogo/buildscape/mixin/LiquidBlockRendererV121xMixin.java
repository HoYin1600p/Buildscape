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
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LiquidBlockRenderer.class)
public abstract class LiquidBlockRendererV121xMixin {
    @Dynamic
    @ModifyVariable(method = "tesselate(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)V",
            at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 0)
    private VertexConsumer buildscape$pipeOutletSpill(VertexConsumer original,
            BlockAndTintGetter level, BlockPos pos, VertexConsumer buffer, BlockState state, FluidState fluid) {
        return PipeSpillVertexConsumer.wrap(original, level, pos, state, fluid);
    }

    @Dynamic
    @Inject(method = "tesselate(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)V",
            at = @At("HEAD"), cancellable = true, require = 0)
    private void buildscape$cancelHollowChunkFluidRender(BlockAndTintGetter level, BlockPos pos,
            VertexConsumer buffer, BlockState state, FluidState fluid, CallbackInfo ci) {
        if (state.getBlock() instanceof HollowPipeBlock || state.getBlock() instanceof HollowLogBlock) ci.cancel();
    }

    @Dynamic
    @Inject(method = "getHeight(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/material/Fluid;Lnet/minecraft/core/BlockPos;)F",
            at = @At("HEAD"), cancellable = true, require = 0)
    private void buildscape$hollowFluidHeight(BlockAndTintGetter level, Fluid fluid, BlockPos pos,
            CallbackInfoReturnable<Float> cir) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof HollowPipeBlock || state.getBlock() instanceof HollowLogBlock) {
            cir.setReturnValue(-1.0F);
        }
    }
}
