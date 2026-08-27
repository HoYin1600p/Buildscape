package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.block.HollowPipeBlock;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
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
        if (state.getBlock() instanceof HollowPipeBlock) {
            cir.setReturnValue(false);
        }
    }
}
