package com.kingodogo.buildscape.client.renderer.layer;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.client.model.BuildersHatModel;
import com.kingodogo.buildscape.cosmetics.CosmeticManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class CosmeticLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private final BuildersHatModel<AbstractClientPlayer> buildersHatModel;

    public CosmeticLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer,
            EntityModelSet modelSet) {
        super(renderer);
        this.buildersHatModel = new BuildersHatModel<>(modelSet.bakeLayer(BuildersHatModel.LAYER_LOCATION));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player,
            float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw,
            float headPitch) {
        if (player.isInvisible())
            return;

        // Get equipped cosmetics
        Map<Integer, String> equippedCosmetics = com.kingodogo.buildscape.client.screen.tabs.supporters.SupportersTabState
                .getInstance()
                .getEquippedCosmeticsBySlot();

        if (equippedCosmetics == null || equippedCosmetics.isEmpty()) {
            equippedCosmetics = com.kingodogo.buildscape.config.CosmeticsConfig.get()
                    .getEquippedCosmetics(player.getUUID());
        }

        if (equippedCosmetics.isEmpty())
            return;

        CosmeticManager cosmeticManager = CosmeticManager.getInstance();

        // Check for head cosmetic
        String headCosmeticId = equippedCosmetics.get(0); // Slot 0 is head
        if (headCosmeticId != null && !headCosmeticId.isEmpty()) {
            CosmeticManager.CosmeticMetadata metadata = cosmeticManager.getMetadata(headCosmeticId);
            if (metadata != null && metadata.type == CosmeticManager.CosmeticType.HEAD) {
                // Render Builder's Hat (id: builders_hat)
                if ("buildscape:cosmatics/gear/builders_hat".equals(headCosmeticId)) {
                    renderBuildersHat(poseStack, buffer, packedLight, player);
                }
            }
        }
    }

    private void renderBuildersHat(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
            AbstractClientPlayer player) {
        poseStack.pushPose();

        // Attach to head bone
        this.getParentModel().head.translateAndRotate(poseStack);

        poseStack.scale(1.15F, -1.15F, -1.15F); // Fix upside down and backward, and scale up for 2nd layer

        ResourceLocation texture = new ResourceLocation(BuildScape.MODID, "textures/cosmatics/builders_hat.png");
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));

        this.buildersHatModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F,
                1.0F, 1.0F, 1.0F);

        poseStack.popPose();
    }
}
