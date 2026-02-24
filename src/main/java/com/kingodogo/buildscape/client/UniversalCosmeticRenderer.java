package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.cosmetics.CosHead;
import com.kingodogo.buildscape.cosmetics.CosChest;
import com.kingodogo.buildscape.cosmetics.CosLegs;
import com.kingodogo.buildscape.cosmetics.CosFeet;
import com.kingodogo.buildscape.cosmetics.CosmeticManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Universal renderer for cosmetics.
 * Handles baking and rendering of all registered CosHead models.
 */
public class UniversalCosmeticRenderer {
    
    private static final Map<String, CosHead<?>> bakedHeadModels = new HashMap<>();
    private static final Map<String, CosChest<?>> bakedChestModels = new HashMap<>();
    private static final Map<String, CosLegs<?>> bakedLegsModels = new HashMap<>();
    private static final Map<String, CosFeet<?>> bakedFeetModels = new HashMap<>();

    private static net.minecraft.client.model.geom.ModelPart playerHeadPart = null;
    private static net.minecraft.client.model.geom.ModelPart playerHatPart = null;
    private static boolean initialized = false;

    /**
     * Initializes and bakes all registered head cosmetics.
     */
    public static void init(EntityModelSet modelSet) {
        if (initialized) return;
        
        try {
            net.minecraft.client.model.geom.ModelPart root = modelSet.bakeLayer(net.minecraft.client.model.geom.ModelLayers.PLAYER);
            playerHeadPart = root.getChild("head");
            playerHatPart = root.getChild("hat");
        } catch (Exception e) {
            com.kingodogo.buildscape.BuildScape.getLogger().error("Failed to bake player head for preview", e);
        }

        bakeAll(modelSet);
        initialized = true;
    }

    private static void bakeAll(EntityModelSet modelSet) {
        try {
            com.kingodogo.buildscape.client.model.BuildersHatModel<?> hat = new com.kingodogo.buildscape.client.model.BuildersHatModel<>(
                modelSet.bakeLayer(com.kingodogo.buildscape.client.model.BuildersHatModel.LAYER_LOCATION)
            );
            bakedHeadModels.put("buildscape:cosmatics/gear/builders_hat", hat);
        } catch (Exception e) {
            // Already logged in init usually, but good to be safe
        }
    }

    public static void renderPlayerHeadOnly(PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (playerHeadPart == null) return;
        
        // Use a more visible alpha for the ghost head preview
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(new net.minecraft.resources.ResourceLocation("textures/entity/steve.png")));
        
        // Player head model units: 8x8x8 box, origin is neck.
        // We render it at 50% opacity to act as a clear reference for the cosmetic.
        playerHeadPart.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.5F);
    }

    public static void renderStandardPlayerHead(PoseStack poseStack, MultiBufferSource buffer, int packedLight, net.minecraft.client.player.AbstractClientPlayer player) {
        if (playerHeadPart == null) return;
        
        net.minecraft.resources.ResourceLocation skin = player != null ? player.getSkinTextureLocation() : new net.minecraft.resources.ResourceLocation("textures/entity/steve.png");
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutout(skin));
        
        playerHeadPart.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        
        // Render the hat layer (outer skin)
        if (playerHatPart != null) {
            VertexConsumer hatConsumer = buffer.getBuffer(RenderType.entityTranslucent(skin));
            playerHatPart.render(poseStack, hatConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public static <T extends Entity> void renderHeadCosmetic(
            String id, 
            PoseStack poseStack, 
            MultiBufferSource buffer, 
            int packedLight, 
            T entity) {
        
        CosHead<T> model = (CosHead<T>) bakedHeadModels.get(id);
        if (model == null) return;

        poseStack.pushPose();
        
        // Apply model-specific transforms (e.g. sit on top of head)
        model.applyTransform(poseStack);

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(model.getTexture()));
        model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
    }

    public static <T extends Entity> void renderChestCosmetic(
            String id, 
            PoseStack poseStack, 
            MultiBufferSource buffer, 
            int packedLight, 
            T entity,
            net.minecraft.client.model.PlayerModel<net.minecraft.client.player.AbstractClientPlayer> playerModel) {
        
        CosChest<T> model = (CosChest<T>) bakedChestModels.get(id);
        if (model == null) return;

        poseStack.pushPose();
        model.applyTransform(poseStack);
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(model.getTexture()));
        
        // Render torso
        poseStack.pushPose();
        playerModel.body.translateAndRotate(poseStack);
        model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();

        poseStack.popPose();
    }

    public static <T extends Entity> void renderLegsCosmetic(
            String id, 
            PoseStack poseStack, 
            MultiBufferSource buffer, 
            int packedLight, 
            T entity,
            net.minecraft.client.model.PlayerModel<net.minecraft.client.player.AbstractClientPlayer> playerModel) {
        
        CosLegs<T> model = (CosLegs<T>) bakedLegsModels.get(id);
        if (model == null) return;

        poseStack.pushPose();
        model.applyTransform(poseStack);
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(model.getTexture()));
        
        poseStack.pushPose();
        playerModel.body.translateAndRotate(poseStack); // Legs often translate with body/waist
        model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();

        poseStack.popPose();
    }

    public static <T extends Entity> void renderFeetCosmetic(
            String id, 
            PoseStack poseStack, 
            MultiBufferSource buffer, 
            int packedLight, 
            T entity,
            net.minecraft.client.model.PlayerModel<net.minecraft.client.player.AbstractClientPlayer> playerModel) {
        
        CosFeet<T> model = (CosFeet<T>) bakedFeetModels.get(id);
        if (model == null) return;

        poseStack.pushPose();
        model.applyTransform(poseStack);
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(model.getTexture()));
        
        model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
    }
}
