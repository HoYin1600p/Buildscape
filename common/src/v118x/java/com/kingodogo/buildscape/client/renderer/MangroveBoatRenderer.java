package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.entity.MangroveBoatEntity;
import com.mojang.datafixers.util.Pair;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;

public class MangroveBoatRenderer extends EntityRenderer<Boat> {

    private static final ResourceLocation MANGROVE_BOAT_TEXTURE =
            new ResourceLocation(BuildscapeCommon.MOD_ID, "textures/entity/boat/mangrove.png");

    private final BoatModel boatModel;

    public MangroveBoatRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.8F;
        this.boatModel = new BoatModel(
                context.bakeLayer(ModelLayers.createBoatModelName(Boat.Type.OAK))
        );
    }

    public Pair<ResourceLocation, BoatModel> getModelWithLocation(Boat boat) {
        return Pair.of(MANGROVE_BOAT_TEXTURE, this.boatModel);
    }

    @Override
    public void render(Boat boat, float yaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffers, int light) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.375D, 0.0D);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F - yaw));
        float hurtTime = boat.getHurtTime() - partialTicks;
        float damage = Math.max(0.0F, boat.getDamage() - partialTicks);
        if (hurtTime > 0.0F) {
            poseStack.mulPose(Vector3f.XP.rotationDegrees(
                    Mth.sin(hurtTime) * hurtTime * damage / 10.0F * boat.getHurtDir()));
        }
        float bubbleAngle = boat.getBubbleAngle(partialTicks);
        if (!Mth.equal(bubbleAngle, 0.0F)) {
            poseStack.mulPose(new Quaternion(new Vector3f(1.0F, 0.0F, 1.0F), bubbleAngle, true));
        }
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(90.0F));
        boatModel.setupAnim(boat, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
        VertexConsumer vertices = buffers.getBuffer(boatModel.renderType(MANGROVE_BOAT_TEXTURE));
        boatModel.renderToBuffer(poseStack, vertices, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        if (!boat.isUnderWater()) {
            boatModel.waterPatch().render(poseStack, buffers.getBuffer(RenderType.waterMask()), light, OverlayTexture.NO_OVERLAY);
        }
        poseStack.popPose();
        super.render(boat, yaw, partialTicks, poseStack, buffers, light);
    }

    @Override
    @Deprecated
    public ResourceLocation getTextureLocation(Boat boat) {
        return MANGROVE_BOAT_TEXTURE;
    }
}
