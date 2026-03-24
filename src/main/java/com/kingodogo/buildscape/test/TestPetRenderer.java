package com.kingodogo.buildscape.test;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.HumanoidArm;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

public class TestPetRenderer extends MobRenderer<TestPetEntity, TestPetModel<TestPetEntity>> {

    private static final ResourceLocation KINGO_TEXTURE = new ResourceLocation("buildscape", "textures/entity/kingo_pet.png");
    private static final float PET_SCALE = 0.4F;

    public TestPetRenderer(EntityRendererProvider.Context context) {
        super(context, new TestPetModel<>(context.bakeLayer(TestPetModel.LAYER_LOCATION)), 0.2F);
        
        final net.minecraft.client.renderer.entity.ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        this.addLayer(new RenderLayer<TestPetEntity, TestPetModel<TestPetEntity>>(this) {
            @Override
            public void render(PoseStack poseStack, net.minecraft.client.renderer.MultiBufferSource buffer, int packedLight, TestPetEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                ItemStack mainHandItem = entity.getMainHandItem();
                if (!mainHandItem.isEmpty()) {
                    poseStack.pushPose();
                    getParentModel().translateToHand(HumanoidArm.RIGHT, poseStack);
                    poseStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
                    poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                    poseStack.scale(0.8F, 0.8F, 0.8F);
                    // Center the item exactly at the wrist/hand tip (0.0625 = 1 pixel shift X, 0.125 = 2 pixels Z)
                    poseStack.translate(0.0625D, 0.125D, 0.0D);
                    itemRenderer.renderStatic(entity, mainHandItem, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, false, poseStack, buffer, entity.level, packedLight, net.minecraft.client.renderer.entity.LivingEntityRenderer.getOverlayCoords(entity, 0.0F), entity.getId());
                    poseStack.popPose();
                }

                ItemStack offHandItem = entity.getOffhandItem();
                if (!offHandItem.isEmpty()) {
                    poseStack.pushPose();
                    getParentModel().translateToHand(HumanoidArm.LEFT, poseStack);
                    poseStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
                    poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                    poseStack.scale(0.8F, 0.8F, 0.8F);
                    // Center the item exactly at the wrist/hand tip
                    poseStack.translate(-0.0625D, 0.125D, 0.0D);
                    itemRenderer.renderStatic(entity, offHandItem, ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND, true, poseStack, buffer, entity.level, packedLight, net.minecraft.client.renderer.entity.LivingEntityRenderer.getOverlayCoords(entity, 0.0F), entity.getId());
                    poseStack.popPose();
                }
            }
        });

        // ─── HOLOGRAPHIC 3D INVENTORY PROJECTION LAYER ───
        this.addLayer(new RenderLayer<TestPetEntity, TestPetModel<TestPetEntity>>(this) {
            @Override
            public void render(PoseStack poseStack, net.minecraft.client.renderer.MultiBufferSource buffer, int packedLight, TestPetEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                PetAnimation animState = entity.getCurrentAnimation();
                if (animState == PetAnimation.PROJECT_HOLOGRAM || animState == PetAnimation.SWAP_MAINHAND || animState == PetAnimation.SWAP_OFFHAND) {
                    poseStack.pushPose();
                    
                    // Translate UP (negative Y is up in model space) and FORWARD (negative Z)
                    poseStack.translate(0.0D, -1.2D, -0.7D);
                    
                    // Gentle holographic hover effect based on time
                    float hover = net.minecraft.util.Mth.sin((entity.tickCount + partialTicks) * 0.1F) * 0.05F;
                    poseStack.translate(0.0D, hover, 0.0D);
                    
                    // Tilt the projection screen back slightly so it faces the pet's eyes
                    poseStack.mulPose(Vector3f.XP.rotationDegrees(-20.0F));

                    for (int i = 0; i < 8; i++) {
                        ItemStack stack = entity.getInventory().getItem(i);
                        if (!stack.isEmpty()) {
                            poseStack.pushPose();
                            
                            // Map the 8 standard slots to a 3D grid layout
                            // Row 1 (5 slots): i = 0 to 4
                            // Row 2 (3 slots): i = 5 to 7
                            float xOff = (i < 5) ? ((2 - i) * 0.25F) : ((6 - i) * 0.35F);
                            float yOff = (i < 5) ? -0.3F : 0.1F; // Top row is higher up (-Y)
                            
                            poseStack.translate(xOff, yOff, 0.0D);
                            poseStack.scale(0.3F, 0.3F, 0.3F);
                            
                            // Make each floated item slowly rotate perfectly on the Y axis 
                            poseStack.mulPose(Vector3f.YP.rotationDegrees((entity.tickCount + partialTicks) * 3.0F));
                            
                            itemRenderer.renderStatic(entity, stack, ItemTransforms.TransformType.GROUND, false, poseStack, buffer, entity.level, packedLight, net.minecraft.client.renderer.entity.LivingEntityRenderer.getOverlayCoords(entity, 0.0F), entity.getId());
                            
                            poseStack.popPose();
                        }
                    }
                    poseStack.popPose();
                }
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(TestPetEntity entity) {
        // We baked King0Dogo's skin directly into the mod assets!
        // This guarantees EVERYONE sees the pet exactly as King0Dogo, even offline.
        return KINGO_TEXTURE;
    }

    @Override
    protected void scale(TestPetEntity entity, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(PET_SCALE, PET_SCALE, PET_SCALE);
    }

    @Mod.EventBusSubscriber(modid = "buildscape", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(TestPetModel.LAYER_LOCATION, TestPetModel::createBodyLayer);
        }

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(TestEntities.TEST_PET.get(), TestPetRenderer::new);
        }
    }
}
