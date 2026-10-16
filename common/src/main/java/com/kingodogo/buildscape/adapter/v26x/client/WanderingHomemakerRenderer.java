package com.kingodogo.buildscape.adapter.v26x.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;

/** Both homemakers use the reference backpack, hat, folded arms and 128px texture layout. */
public final class WanderingHomemakerRenderer extends
        MobRenderer<WanderingTrader, LivingEntityRenderState, WanderingHomemakerModel> {
    private final Identifier texture;

    public WanderingHomemakerRenderer(EntityRendererProvider.Context context, boolean festive) {
        super(context, new WanderingHomemakerModel(WanderingHomemakerModel.createBodyLayer().bakeRoot()), 0.5F);
        texture = Identifier.fromNamespaceAndPath("buildscape", "textures/entity/"
                + (festive ? "festive_wandering_homemaker" : "wandering_homemaker") + ".png");
    }

    @Override public LivingEntityRenderState createRenderState() { return new LivingEntityRenderState(); }

    @Override public Identifier getTextureLocation(LivingEntityRenderState state) { return texture; }
}
