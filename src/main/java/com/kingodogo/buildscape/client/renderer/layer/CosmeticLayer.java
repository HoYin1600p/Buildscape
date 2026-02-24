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
        // Wings can be in ANY slot, not just 0-3
        String wingsId = null;

        // Check equipped cosmetics by slot
        if (equippedCosmetics != null && !equippedCosmetics.isEmpty()) {
            for (String cosmeticId : equippedCosmetics.values()) {
                if (cosmeticId != null && !cosmeticId.isEmpty()) {
                    CosmeticManager.CosmeticMetadata meta = cosmeticManager.getMetadata(cosmeticId);
                    if (meta != null && meta.type() == CosmeticManager.CosmeticType.PARTICLE_WINGS) {
                        wingsId = cosmeticId;
                        break;
                    }
                }
            }
        }

        // Also check the direct equipped cosmetics set (wings might be stored there too)
        if (wingsId == null) {
            var equippedSet = com.kingodogo.buildscape.client.screen.tabs.supporters.SupportersTabState
                    .getInstance()
                    .getEquippedCosmetics();
            if (equippedSet != null && !equippedSet.isEmpty()) {
                for (String cosmeticId : equippedSet) {
                    if (cosmeticId != null && !cosmeticId.isEmpty()) {
                        CosmeticManager.CosmeticMetadata meta = cosmeticManager.getMetadata(cosmeticId);
                        if (meta != null && meta.type() == CosmeticManager.CosmeticType.PARTICLE_WINGS) {
                            wingsId = cosmeticId;
                            break;
                        }
                    }
                }
            }
        }

        if (wingsId != null) {
            try {
                // Get wing color from config
                float[] wingColor = new float[]{1.0f, 1.0f, 1.0f}; // Default white
                String colorHex = com.kingodogo.buildscape.config.CosmeticsConfig.get().getCosmeticColor(player.getUUID(), wingsId);
                if (colorHex != null && !colorHex.isEmpty()) {
                    try {
                        String hex = colorHex.startsWith("#") ? colorHex.substring(1) : colorHex;
                        int rgb = Integer.parseInt(hex, 16);
                        wingColor[0] = ((rgb >> 16) & 0xFF) / 255.0f;
                        wingColor[1] = ((rgb >> 8) & 0xFF) / 255.0f;
                        wingColor[2] = (rgb & 0xFF) / 255.0f;
                    } catch (NumberFormatException e) {
                        // Use default white color
                    }
                }

                // Get wing shape from cosmetic metadata
                String wingShape = cosmeticManager.getParticleShape(wingsId);

                // Render wings using the solid foundational base, attached to body
                poseStack.pushPose();
                this.getParentModel().body.translateAndRotate(poseStack);
                com.kingodogo.buildscape.client.renderer.CosmeticWingRenderer.render(
                    poseStack, buffer, packedLight, player, ageInTicks, wingColor, wingShape
                );
                poseStack.popPose();
            } catch (Exception e) {
                com.kingodogo.buildscape.BuildScape.getLogger().error("Error rendering wings: " + e.getMessage());
                e.printStackTrace();
            }
        }

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
