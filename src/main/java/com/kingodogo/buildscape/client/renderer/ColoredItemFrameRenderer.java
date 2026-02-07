package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.entity.ColoredItemFrameEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.resources.ResourceLocation;

public class ColoredItemFrameRenderer extends ItemFrameRenderer<ColoredItemFrameEntity> {

    public ColoredItemFrameRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(ColoredItemFrameEntity entity) {
        return new ResourceLocation("minecraft", "textures/entity/item_frame.png");
    }
}
