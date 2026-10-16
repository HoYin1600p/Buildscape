package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.adapter.v118x.client.CustomGeometryAccess;
import net.minecraft.client.renderer.block.model.BlockModel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockModel.class)
public abstract class ForgeBlockModelGeometryMixin implements CustomGeometryAccess {
    @Override
    public boolean buildscape$hasCustomGeometry() {
        var data = ((BlockModel) (Object) this).customData;
        return data != null && data.hasCustomGeometry();
    }
}
