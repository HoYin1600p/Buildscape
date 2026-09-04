package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.client.renderer.EmbeddiumSpillBuffer;
import com.kingodogo.buildscape.client.renderer.PipeSpillVertexConsumer;
import me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.ForgeHooksClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Optional, outlet-only path; ordinary Embeddium water remains untouched. Author: HoYin1600p. */
@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.client.render.pipeline.FluidRenderer", remap = false)
public class EmbeddiumPipeSpillMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true, remap = false)
    private void buildscape$renderSpill(BlockAndTintGetter level, FluidState fluid, BlockPos pos,
            BlockPos offset, ChunkModelBuilder builder, CallbackInfoReturnable<Boolean> cir) {
        BlockState state = level.getBlockState(pos);
        if (PipeSpillVertexConsumer.findOutlets(level, pos, state, fluid).isEmpty()) return;
        EmbeddiumSpillBuffer buffer = new EmbeddiumSpillBuffer(builder, pos, offset);
        Minecraft.getInstance().getBlockRenderer().renderLiquid(pos, level, buffer, state, fluid);
        for (var sprite : ForgeHooksClient.getFluidSprites(level, pos, fluid)) {
            if (sprite != null) builder.addSprite(sprite);
        }
        cir.setReturnValue(true);
    }
}
