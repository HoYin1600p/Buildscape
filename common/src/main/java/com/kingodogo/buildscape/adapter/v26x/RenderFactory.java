package com.kingodogo.buildscape.adapter.v26x;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.platform.Services;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.WanderingTraderRenderer;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;

public final class RenderFactory {

    private static final Identifier HOMEMAKER_TEXTURE = Identifier.fromNamespaceAndPath(
            BuildscapeCommon.MOD_ID, "textures/entity/wandering_homemaker.png");
    private static final Identifier FESTIVE_HOMEMAKER_TEXTURE = Identifier.fromNamespaceAndPath(
            BuildscapeCommon.MOD_ID, "textures/entity/festive_wandering_homemaker.png");

    private RenderFactory() {}

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void registerRenderers() {
        try {
            java.lang.reflect.Field eField = net.minecraft.client.renderer.entity.EntityRenderers.class.getDeclaredField("PROVIDERS");
            eField.setAccessible(true);
            java.util.Map<EntityType<?>, Object> eProviders =
                    (java.util.Map<EntityType<?>, Object>) eField.get(null);

            var homemakerType = (EntityType<WanderingTrader>) Services.PLATFORM.getWanderingHomemakerEntityType();
            if (homemakerType != null) {
                eProviders.put(homemakerType, (EntityRendererProvider<WanderingTrader>) context ->
                        new WanderingTraderRenderer(context) {
                            @Override
                            public Identifier getTextureLocation(VillagerRenderState state) {
                                return HOMEMAKER_TEXTURE;
                            }
                        }
                );
            }

            var festiveType = (EntityType<WanderingTrader>) Services.PLATFORM.getFestiveWanderingHomemakerEntityType();
            if (festiveType != null) {
                eProviders.put(festiveType, (EntityRendererProvider<WanderingTrader>) context ->
                        new WanderingTraderRenderer(context) {
                            @Override
                            public Identifier getTextureLocation(VillagerRenderState state) {
                                return FESTIVE_HOMEMAKER_TEXTURE;
                            }
                        }
                );
            }
        } catch (Throwable ignored) {}
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void renderEntity(Entity entity, double x, double y, double z, float yaw, float partialTicks, PoseStack poseStack, Object bufferSource, int packedLight) {
        if (entity == null) return;
        try {
            var dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
            EntityRenderer renderer = dispatcher.getRenderer(entity);
            if (renderer != null && bufferSource instanceof net.minecraft.client.renderer.SubmitNodeCollector collector) {
                var state = renderer.createRenderState(entity, (float) partialTicks);
                renderer.submit(state, poseStack, collector, null);
            }
        } catch (Throwable ignored) {}
    }

    public static VertexConsumer getTranslucentBuffer(Object bufferSource) {
        return null;
    }
}
