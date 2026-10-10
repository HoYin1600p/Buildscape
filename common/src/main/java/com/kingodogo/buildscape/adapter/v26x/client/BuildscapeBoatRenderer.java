package com.kingodogo.buildscape.adapter.v26x.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.AbstractBoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;

/** Uses the reference oak hull with the Buildscape wood texture and vanilla boat animation. */
public final class BuildscapeBoatRenderer extends AbstractBoatRenderer {
    private final BoatModel boatModel;
    private final Model.Simple waterPatch;

    public BuildscapeBoatRenderer(EntityRendererProvider.Context context, String wood) {
        super(context, Identifier.fromNamespaceAndPath("buildscape", "textures/entity/boat/" + wood + ".png"));
        boatModel = new BoatModel(context.bakeLayer(ModelLayers.OAK_BOAT));
        waterPatch = new Model.Simple(context.bakeLayer(ModelLayers.BOAT_WATER_PATCH), texture -> RenderTypes.waterMask());
    }

    @Override protected EntityModel<BoatRenderState> model() { return boatModel; }

    @Override protected void submitTypeAdditions(BoatRenderState state, PoseStack pose,
            SubmitNodeCollector collector, int light) {
        if (!state.isUnderWater) {
            collector.submitModel(waterPatch, Unit.INSTANCE, pose, texture, light,
                    OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        }
    }
}
