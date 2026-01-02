package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.config.CosmeticsConfig;
import com.kingodogo.buildscape.cosmetics.CosmeticRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.Map;

@Mod.EventBusSubscriber(modid = com.kingodogo.buildscape.BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class CosmeticRenderHandler {
    
    private static boolean renderCosmeticsOnly = false;
    
    private static final ThreadLocal<Boolean> isRenderingCosmetics = new ThreadLocal<>();
    
    private static final ThreadLocal<Map<Integer, String>> cosmeticsToRender = new ThreadLocal<>();
    
    public static void setRenderCosmeticsOnly(boolean only) {
        renderCosmeticsOnly = only;
    }
    
    public static boolean isRenderCosmeticsOnly() {
        return renderCosmeticsOnly;
    }
    
    @SubscribeEvent
    public static void onRenderPlayerPre(RenderLivingEvent.Pre<? extends LivingEntity, ?> event) {
        if (!(event.getEntity() instanceof AbstractClientPlayer player)) {
            return;
        }

        renderCosmeticsOnly = false;

        if (Boolean.TRUE.equals(isRenderingCosmetics.get())) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (player != mc.player) {
            return;
        }

        Map<Integer, String> equippedCosmetics = com.kingodogo.buildscape.client.screen.tabs.supporters.SupportersTabState
                .getInstance()
                .getEquippedCosmeticsBySlot();
        if (equippedCosmetics == null || equippedCosmetics.isEmpty()) {
            CosmeticsConfig config = CosmeticsConfig.get();
            equippedCosmetics = config.getEquippedCosmetics(player.getUUID());
        }
        
        if (equippedCosmetics.isEmpty()) {
            return;
        }

        cosmeticsToRender.set(equippedCosmetics);
    }
    
    @SubscribeEvent
    public static void onRenderPlayerPost(RenderLivingEvent.Post<? extends LivingEntity, ?> event) {
        if (!(event.getEntity() instanceof AbstractClientPlayer player)) {
            return;
        }

        if (Boolean.TRUE.equals(isRenderingCosmetics.get())) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (player != mc.player) {
            return;
        }

        Map<Integer, String> equippedCosmetics = cosmeticsToRender.get();
        if (equippedCosmetics == null || equippedCosmetics.isEmpty()) {
            cosmeticsToRender.remove();
            return;
        }

        renderCosmeticsOverlay(event, player, equippedCosmetics);
        
        cosmeticsToRender.remove();
    }
    
    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
    }
    
    private static void renderCosmeticsOverlay(RenderLivingEvent.Post<? extends LivingEntity, ?> event,
                                               AbstractClientPlayer player,
                                               Map<Integer, String> equippedCosmetics) {
        Minecraft mc = Minecraft.getInstance();
        net.minecraft.client.renderer.entity.EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        PlayerRenderer playerRenderer = (PlayerRenderer) dispatcher.getRenderer(player);
        if (playerRenderer == null) return;

        ItemStack[] original = new ItemStack[4];
        original[0] = player.getItemBySlot(EquipmentSlot.HEAD).copy();
        original[1] = player.getItemBySlot(EquipmentSlot.CHEST).copy();
        original[2] = player.getItemBySlot(EquipmentSlot.LEGS).copy();
        original[3] = player.getItemBySlot(EquipmentSlot.FEET).copy();

        CosmeticRegistry registry = CosmeticRegistry.getInstance();
        com.kingodogo.buildscape.cosmetics.CosmeticManager cosmeticManager = com.kingodogo.buildscape.cosmetics.CosmeticManager.getInstance();

        String customHeadCosmetic = null;

        for (Map.Entry<Integer, String> entry : equippedCosmetics.entrySet()) {
            String cosmeticId = entry.getValue();
            if (cosmeticId == null || cosmeticId.isEmpty()) continue;

            com.kingodogo.buildscape.cosmetics.CosmeticManager.CosmeticMetadata metadata = cosmeticManager.getMetadata(cosmeticId);
            if (metadata != null && metadata.type == com.kingodogo.buildscape.cosmetics.CosmeticManager.CosmeticType.HEAD) {
                if (entry.getKey() == 0) {
                    customHeadCosmetic = cosmeticId;
                    setItemSlotSilent(player, EquipmentSlot.HEAD, ItemStack.EMPTY);
                    break;
                }
            }
        }

        for (Map.Entry<Integer, String> entry : equippedCosmetics.entrySet()) {
            String cosmeticId = entry.getValue();
            if (cosmeticId == null || cosmeticId.isEmpty()) continue;

            com.kingodogo.buildscape.cosmetics.CosmeticManager.CosmeticMetadata metadata = cosmeticManager.getMetadata(cosmeticId);
            if (metadata != null && metadata.type == com.kingodogo.buildscape.cosmetics.CosmeticManager.CosmeticType.HEAD) {
                continue;
            }
            
            ItemStack cosmeticStack = registry.resolveToItemStack(cosmeticId);
            if (cosmeticStack == null || cosmeticStack.isEmpty()) continue;
            EquipmentSlot slot = getSlotForIndex(entry.getKey());
            if (slot != null) {
                setItemSlotSilent(player, slot, cosmeticStack);
            }
        }
        
        try {
            isRenderingCosmetics.set(true);
            
            PoseStack poseStack = event.getPoseStack();
            MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
            int packedLight = dispatcher.getPackedLightCoords(player, event.getPartialTick());
            float partialTick = event.getPartialTick();
            
            poseStack.pushPose();

            com.mojang.blaze3d.systems.RenderSystem.enableDepthTest();
            com.mojang.blaze3d.systems.RenderSystem.depthMask(true);
            com.mojang.blaze3d.systems.RenderSystem.enableBlend();
            com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
            
            playerRenderer.render(player, player.getYRot(), partialTick, poseStack, bufferSource, packedLight);

            if (customHeadCosmetic != null) {
                com.kingodogo.buildscape.BuildScape.getLogger().debug("Rendering custom head cosmetic: " + customHeadCosmetic);
                renderCustomHeadCosmetic(player, customHeadCosmetic, poseStack, bufferSource, packedLight, partialTick);
            }
            
            bufferSource.endBatch();
            poseStack.popPose();
            
            com.mojang.blaze3d.systems.RenderSystem.disableBlend();
            com.mojang.blaze3d.systems.RenderSystem.disableDepthTest();
            
        } catch (Exception e) {
            com.kingodogo.buildscape.BuildScape.getLogger().debug("Failed to render cosmetic overlay: " + e.getMessage());
        } finally {
            isRenderingCosmetics.remove();
            setItemSlotSilent(player, EquipmentSlot.HEAD, original[0]);
            setItemSlotSilent(player, EquipmentSlot.CHEST, original[1]);
            setItemSlotSilent(player, EquipmentSlot.LEGS, original[2]);
            setItemSlotSilent(player, EquipmentSlot.FEET, original[3]);
        }
    }
    
    private static void setItemSlotSilent(AbstractClientPlayer player, EquipmentSlot slot, ItemStack stack) {
        if (player == null) return;

        net.minecraft.world.entity.player.Inventory inventory = player.getInventory();
        
        switch (slot) {
            case HEAD:
                inventory.armor.set(3, stack);
                break;
            case CHEST:
                inventory.armor.set(2, stack);
                break;
            case LEGS:
                inventory.armor.set(1, stack);
                break;
            case FEET:
                inventory.armor.set(0, stack);
                break;
            case MAINHAND:
                inventory.setItem(inventory.selected, stack);
                break;
            default:
                break;
        }
    }
    
    private static EquipmentSlot getSlotForIndex(int slotIndex) {
        switch (slotIndex) {
            case 0: return EquipmentSlot.HEAD;
            case 1: return EquipmentSlot.CHEST;
            case 2: return EquipmentSlot.LEGS;
            case 3: return EquipmentSlot.FEET;
            default: return null;
        }
    }
    
    private static net.minecraft.client.model.geom.ModelPart buildersHatModelPart = null;
    
    private static void renderCustomHeadCosmetic(
            AbstractClientPlayer player,
            String cosmeticId,
            com.mojang.blaze3d.vertex.PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            float partialTick
    ) {
        try {
            Minecraft mc = Minecraft.getInstance();
            net.minecraft.client.renderer.entity.EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();

            if (buildersHatModelPart == null) {
                com.kingodogo.buildscape.BuildScape.getLogger().info("Creating builders hat model part...");
                try {
                    net.minecraft.client.renderer.entity.player.PlayerRenderer playerRenderer =
                            (net.minecraft.client.renderer.entity.player.PlayerRenderer) dispatcher.getRenderer(player);
                    if (playerRenderer != null) {
                        com.kingodogo.buildscape.BuildScape.getLogger().info("PlayerRenderer found, attempting to create model...");
                        try {
                            java.lang.reflect.Field entityModelsField = null;
                            try {
                                entityModelsField = net.minecraft.client.renderer.entity.player.PlayerRenderer.class
                                        .getDeclaredField("entityModels");
                                entityModelsField.setAccessible(true);
                            } catch (NoSuchFieldException e) {
                                try {
                                    entityModelsField = net.minecraft.client.renderer.entity.player.PlayerRenderer.class
                                            .getDeclaredField("modelSet");
                                    entityModelsField.setAccessible(true);
                                } catch (NoSuchFieldException e2) {
                                }
                            }
                            
                            if (entityModelsField != null) {
                                net.minecraft.client.model.geom.EntityModelSet entityModels = 
                                        (net.minecraft.client.model.geom.EntityModelSet) entityModelsField.get(playerRenderer);
                                if (entityModels != null) {
                                    net.minecraft.client.model.geom.ModelPart root = entityModels.bakeLayer(
                                            com.kingodogo.buildscape.client.model.BuildersHatModel.LAYER_LOCATION);
                                    if (root != null) {
                                        buildersHatModelPart = root.getChild("Head");
                                    }
                                }
                            }

                            if (buildersHatModelPart == null) {
                                try {
                                    net.minecraft.client.model.geom.builders.MeshDefinition meshdefinition = 
                                            new net.minecraft.client.model.geom.builders.MeshDefinition();
                                    PartDefinition partdefinition = meshdefinition.getRoot();
                                    
                                    PartDefinition Head = partdefinition.addOrReplaceChild("Head", 
                                            net.minecraft.client.model.geom.builders.CubeListBuilder.create(), 
                                            net.minecraft.client.model.geom.PartPose.offset(0.0F, 0.0F, 0.0F));
                                    
                                    Head.addOrReplaceChild("bone", 
                                            net.minecraft.client.model.geom.builders.CubeListBuilder.create()
                                                    .texOffs(0, 0).addBox(-14.0F, -5.0F, 0.0F, 11.0F, 0.0F, 16.0F, 
                                                            new net.minecraft.client.model.geom.builders.CubeDeformation(0.0F))
                                                    .texOffs(0, 17).addBox(-13.0F, -10.0F, 5.0F, 9.0F, 5.0F, 9.0F, 
                                                            new net.minecraft.client.model.geom.builders.CubeDeformation(0.0F))
                                                    .texOffs(0, 32).addBox(-10.0F, -11.0F, 4.0F, 3.0F, 6.0F, 11.0F, 
                                                            new net.minecraft.client.model.geom.builders.CubeDeformation(0.0F)), 
                                            net.minecraft.client.model.geom.PartPose.offset(8.5F, -1.0F, -9.75F));

                                    net.minecraft.client.model.geom.ModelPart root = partdefinition.bake(64, 64);
                                    if (root != null) {
                                        buildersHatModelPart = root.getChild("Head");
                                        if (buildersHatModelPart != null) {
                                            com.kingodogo.buildscape.BuildScape.getLogger().info("Successfully created builders hat model part!");
                                        } else {
                                            com.kingodogo.buildscape.BuildScape.getLogger().warn("Root ModelPart created but 'Head' child not found!");
                                        }
                                    } else {
                                        com.kingodogo.buildscape.BuildScape.getLogger().warn("Failed to bake root ModelPart!");
                                    }
                                } catch (Exception e3) {
                                    com.kingodogo.buildscape.BuildScape.getLogger().error(
                                            "Failed to create builders hat model: " + e3.getMessage(), e3);
                                }
                            }
                        } catch (Exception e2) {
                            com.kingodogo.buildscape.BuildScape.getLogger().warn(
                                    "Failed to create builders hat model: " + e2.getMessage());
                            return;
                        }
                    } else {
                        return;
                    }
                } catch (Exception e) {
                    com.kingodogo.buildscape.BuildScape.getLogger().debug(
                            "Builders hat model layer not registered, cannot render custom head cosmetic: " + e.getMessage());
                    return;
                }
            }
            
            if (buildersHatModelPart == null) {
                com.kingodogo.buildscape.BuildScape.getLogger().warn("buildersHatModelPart is null, cannot render!");
                return;
            }

            net.minecraft.resources.ResourceLocation texture = new net.minecraft.resources.ResourceLocation(
                    com.kingodogo.buildscape.BuildScape.MODID,
                    "textures/cosmatics/builders_hat.png"
            );
            com.kingodogo.buildscape.BuildScape.getLogger().debug("Rendering builders hat with texture: " + texture);

            com.mojang.blaze3d.vertex.VertexConsumer vertexConsumer = bufferSource.getBuffer(
                    net.minecraft.client.renderer.RenderType.entityCutoutNoCull(texture)
            );

            poseStack.pushPose();

            float headYaw = player.getYHeadRot();
            float headPitch = player.getXRot();

            poseStack.translate(0.0, 0.0, 0.0);

            poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(headYaw));
            poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(headPitch));

            buildersHatModelPart.render(poseStack, vertexConsumer, packedLight,
                    net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,
                    1.0f, 1.0f, 1.0f, 1.0f);
            
            poseStack.popPose();
        } catch (Exception e) {
            com.kingodogo.buildscape.BuildScape.getLogger().debug("Failed to render custom head cosmetic: " + e.getMessage());
        }
    }
}

