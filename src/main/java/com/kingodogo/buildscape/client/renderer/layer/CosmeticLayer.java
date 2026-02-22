package com.kingodogo.buildscape.client.renderer.layer;

import com.kingodogo.buildscape.client.UniversalCosmeticRenderer;
import com.kingodogo.buildscape.cosmetics.CosmeticManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

import java.util.Map;

public class CosmeticLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public CosmeticLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer,
            EntityModelSet modelSet) {
        super(renderer);
        // Initialize the universal renderer with the available model set
        UniversalCosmeticRenderer.init(modelSet);
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

        CosmeticManager cosmeticManager = CosmeticManager.getInstance();

        // Check for wings first (before early return for empty slots 0-3)
        com.kingodogo.buildscape.client.renderer.WingRenderer.renderWingsForPlayer(player, ageInTicks);

        // Return early only if no armor cosmetics are equipped
        if (equippedCosmetics.isEmpty())
            return;

        // Check for head cosmetic (Slot 0)
        String headCosmeticId = equippedCosmetics.get(0);
        if (headCosmeticId != null && !headCosmeticId.isEmpty()) {
            CosmeticManager.CosmeticMetadata metadata = cosmeticManager.getMetadata(headCosmeticId);
            if (metadata != null && metadata.type() == CosmeticManager.CosmeticType.HEAD) {
                poseStack.pushPose();
                this.getParentModel().head.translateAndRotate(poseStack);
                UniversalCosmeticRenderer.renderHeadCosmetic(headCosmeticId, poseStack, buffer, packedLight, player);
                poseStack.popPose();
            }
        }

        // Check for chest cosmetic (Slot 1)
        String chestCosmeticId = equippedCosmetics.get(1);
        if (chestCosmeticId != null && !chestCosmeticId.isEmpty()) {
            CosmeticManager.CosmeticMetadata metadata = cosmeticManager.getMetadata(chestCosmeticId);
            if (metadata != null && metadata.type() == CosmeticManager.CosmeticType.CHEST) {
                UniversalCosmeticRenderer.renderChestCosmetic(chestCosmeticId, poseStack, buffer, packedLight, player, this.getParentModel());
            }
        }

        // Check for legs cosmetic (Slot 2)
        String legsCosmeticId = equippedCosmetics.get(2);
        if (legsCosmeticId != null && !legsCosmeticId.isEmpty()) {
            CosmeticManager.CosmeticMetadata metadata = cosmeticManager.getMetadata(legsCosmeticId);
            if (metadata != null && metadata.type() == CosmeticManager.CosmeticType.LEGS) {
                UniversalCosmeticRenderer.renderLegsCosmetic(legsCosmeticId, poseStack, buffer, packedLight, player, this.getParentModel());
            }
        }

        // Check for feet cosmetic (Slot 3)
        String feetCosmeticId = equippedCosmetics.get(3);
        if (feetCosmeticId != null && !feetCosmeticId.isEmpty()) {
            CosmeticManager.CosmeticMetadata metadata = cosmeticManager.getMetadata(feetCosmeticId);
            if (metadata != null && metadata.type() == CosmeticManager.CosmeticType.FEET) {
                UniversalCosmeticRenderer.renderFeetCosmetic(feetCosmeticId, poseStack, buffer, packedLight, player, this.getParentModel());
            }
        }
    }
}
