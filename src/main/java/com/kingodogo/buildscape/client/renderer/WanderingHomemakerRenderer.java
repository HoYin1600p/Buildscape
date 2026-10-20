package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.client.model.WanderingHomemakerModel;
import com.kingodogo.buildscape.feature.homemaker.WanderingHomemakerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class WanderingHomemakerRenderer
        extends
            MobRenderer<WanderingHomemakerEntity, WanderingHomemakerModel<WanderingHomemakerEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(BuildScape.MODID,
            "textures/entity/wandering_homemaker.png");

    public WanderingHomemakerRenderer(EntityRendererProvider.Context context) {
        super(context, new WanderingHomemakerModel<>(context.bakeLayer(WanderingHomemakerModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(WanderingHomemakerEntity entity) {
        return TEXTURE;
    }
}
