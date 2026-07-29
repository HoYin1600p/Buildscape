package com.kingodogo.buildscape.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.WanderingTraderRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.WanderingTrader;

public class WanderingHomemakerRenderer extends WanderingTraderRenderer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(com.kingodogo.buildscape.BuildScape.MODID, "textures/entity/wandering_homemaker.png");

    public WanderingHomemakerRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(WanderingTrader entity) {
        return TEXTURE;
    }
}
