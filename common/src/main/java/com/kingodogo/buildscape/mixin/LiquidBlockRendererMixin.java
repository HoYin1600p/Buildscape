package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.block.HollowLogBlock;
import com.kingodogo.buildscape.block.HollowPipeBlock;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.client.renderer.block.LiquidBlockRenderer")
public class LiquidBlockRendererMixin {

    @Dynamic
    @ModifyVariable(method = {
            "tesselate(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)Z",
            "tesselate(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)V"
    }, at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 0)
    private VertexConsumer buildscape$pipeOutletSpill(
            VertexConsumer original,
            @Coerce Object level,
            BlockPos pos,
            @Coerce Object buffer,
            BlockState state,
            FluidState fluid
    ) {
        return MixinFactory.wrapPipeSpillVertexConsumer(original, level, pos, state, fluid);
    }

    @Dynamic
    @Inject(method = "tesselate(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)Z", at = @At("HEAD"), cancellable = true, require = 0)
    private void buildscape$cancelHollowChunkFluidRender118(
            @Coerce Object level,
            BlockPos pos,
            @Coerce Object buffer,
            BlockState state,
            FluidState fluidState,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (state != null && (state.getBlock() instanceof HollowPipeBlock || state.getBlock() instanceof HollowLogBlock)) {
            cir.setReturnValue(false);
        }
    }

    @Dynamic
    @Inject(method = "tesselate(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)V", at = @At("HEAD"), cancellable = true, require = 0)
    private void buildscape$cancelHollowChunkFluidRenderModern(
            @Coerce Object level,
            BlockPos pos,
            @Coerce Object buffer,
            BlockState state,
            FluidState fluidState,
            CallbackInfo ci
    ) {
        if (state != null && (state.getBlock() instanceof HollowPipeBlock || state.getBlock() instanceof HollowLogBlock)) {
            ci.cancel();
        }
    }

    @Dynamic
    @Inject(method = "getHeight(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/material/Fluid;Lnet/minecraft/core/BlockPos;)F", at = @At("HEAD"), cancellable = true, require = 0)
    private void buildscape$hollowFluidHeight(
            @Coerce Object level,
            Fluid fluid,
            BlockPos pos,
            CallbackInfoReturnable<Float> cir
    ) {
        if (level instanceof BlockGetter blockGetter) {
            BlockState state = blockGetter.getBlockState(pos);
            if (state.getBlock() instanceof HollowPipeBlock || state.getBlock() instanceof HollowLogBlock) {
                cir.setReturnValue(-1.0F);
            }
        }
    }
}

