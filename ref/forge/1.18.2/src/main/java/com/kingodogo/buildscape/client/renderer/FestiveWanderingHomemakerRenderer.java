package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.client.model.WanderingHomemakerModel;
import com.kingodogo.buildscape.entity.FestiveWanderingHomemakerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class FestiveWanderingHomemakerRenderer extends MobRenderer<FestiveWanderingHomemakerEntity, WanderingHomemakerModel<FestiveWanderingHomemakerEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(com.kingodogo.buildscape.BuildScape.MODID, "textures/entity/festive_wandering_homemaker.png");

    public FestiveWanderingHomemakerRenderer(EntityRendererProvider.Context context) {
        super(context, new WanderingHomemakerModel<>(context.bakeLayer(WanderingHomemakerModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(FestiveWanderingHomemakerEntity entity) {
        return TEXTURE;
    }
}
