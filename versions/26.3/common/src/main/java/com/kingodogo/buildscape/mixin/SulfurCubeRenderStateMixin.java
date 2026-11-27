package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixinsupport.ISulfurCubeCushionRenderState;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.entity.state.SulfurCubeRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SulfurCubeRenderState.class)
public abstract class SulfurCubeRenderStateMixin implements ISulfurCubeCushionRenderState {

    @Unique
    private final BlockModelRenderState buildscape$cushionBlock = new BlockModelRenderState();

    @Override
    public BlockModelRenderState buildscape$getCushionBlock() {
        return this.buildscape$cushionBlock;
    }
}
