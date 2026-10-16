package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.block.CushionBlock;
import com.kingodogo.buildscape.mixinsupport.ISulfurCubeCushionRenderState;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.entity.SulfurCubeRenderer;
import net.minecraft.client.renderer.entity.state.SulfurCubeRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.cubemob.SulfurCube;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SulfurCubeRenderer.class)
public abstract class SulfurCubeRendererMixin {

    @Shadow
    @Final
    private BlockModelResolver blockModelResolver;

    @Shadow
    @Final
    public static BlockDisplayContext BLOCK_DISPLAY_CONTEXT;

    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/monster/cubemob/SulfurCube;Lnet/minecraft/client/renderer/entity/state/SulfurCubeRenderState;F)V",
            at = @At("TAIL")
    )
    private void buildscape$extractCushionRenderState(
            SulfurCube sulfurCube,
            SulfurCubeRenderState renderState,
            float partialTick,
            CallbackInfo ci
    ) {
        if (renderState instanceof ISulfurCubeCushionRenderState cushionStateHolder) {
            BlockModelRenderState cushionRenderState = cushionStateHolder.buildscape$getCushionBlock();
            cushionRenderState.clear();

            ItemStack headItem = sulfurCube.getItemBySlot(EquipmentSlot.HEAD);
            if (!headItem.isEmpty()) {
                Block block = Block.byItem(headItem.getItem());
                if (block instanceof CushionBlock) {
                    BlockState state = block.defaultBlockState();
                    this.blockModelResolver.update(cushionRenderState, state, BLOCK_DISPLAY_CONTEXT);
                }
            }
        }
    }
}
