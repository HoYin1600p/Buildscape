package com.kingodogo.buildscape.adapter.v26x.client;
import com.kingodogo.buildscape.entity.ColoredItemFrameEntity;
import com.kingodogo.buildscape.entity.FallingIcicleEntity;
import com.kingodogo.buildscape.entity.FestiveStockingEntity;
import com.kingodogo.buildscape.entity.FestiveWanderingHomemakerEntity;
import com.kingodogo.buildscape.entity.MangroveBoatEntity;
import com.kingodogo.buildscape.entity.PoplarBoatEntity;
import com.kingodogo.buildscape.entity.SeatEntity;
import com.kingodogo.buildscape.entity.WanderingHomemakerEntity;
import com.kingodogo.buildscape.entity.WanderingHomemakerTrades;
import com.kingodogo.buildscape.platform.Services;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.time.LocalDate;
import java.util.Collections;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import com.kingodogo.buildscape.platform.IPlatformAdapter;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import com.mojang.serialization.MapCodec;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import com.kingodogo.buildscape.adapter.v26x.*;
import com.kingodogo.buildscape.platform.IPlatformAdapter.BlockColorSample;

/** Client implementations; the common platform facade contains only forwarding calls. */
public final class ClientPlatformHooks {
    private ClientPlatformHooks() {}
    private static final int COLOR_MAX_SAMPLES = 32;
    private static final java.util.Map<String, double[]> v26xSpriteCache = new java.util.concurrent.ConcurrentHashMap<>();
    private static final Identifier BIRCH_PLANKS_26 = Identifier.parse("minecraft:textures/block/birch_planks.png");

    public static int getClientParticleSetting() {
        return net.minecraft.client.Minecraft.getInstance().options.particles().get().ordinal();
    }

    public static Player getClientPlayer() {
        return net.minecraft.client.Minecraft.getInstance().player;
    }

    public static boolean hasShiftDown() {
        return net.minecraft.client.Minecraft.getInstance().hasShiftDown();
    }

    public static boolean hasControlDown() {
        return net.minecraft.client.Minecraft.getInstance().hasControlDown();
    }

    private static net.minecraft.client.input.MouseButtonEvent mouseEvent(double mouseX, double mouseY, int button) {
        return new net.minecraft.client.input.MouseButtonEvent(mouseX, mouseY,
                new net.minecraft.client.input.MouseButtonInfo(button, 0));
    }

    public static boolean widgetMouseClicked(net.minecraft.client.gui.components.AbstractWidget widget, double mouseX, double mouseY, int button) {
        return widget != null && widget.mouseClicked(mouseEvent(mouseX, mouseY, button), false);
    }

    public static boolean widgetKeyPressed(net.minecraft.client.gui.components.AbstractWidget widget, int keyCode, int scanCode, int modifiers) {
        return widget != null && widget.keyPressed(new net.minecraft.client.input.KeyEvent(keyCode, scanCode, modifiers));
    }

    public static boolean widgetCharTyped(net.minecraft.client.gui.components.AbstractWidget widget, char codePoint, int modifiers) {
        return widget != null && widget.charTyped(new net.minecraft.client.input.CharacterEvent(codePoint));
    }

    public static void setEditBoxFilter(net.minecraft.client.gui.components.EditBox editBox, java.util.function.Predicate<String> filter) {
        editBox.setResponder(value -> {
            if (!filter.test(value)) {
                StringBuilder accepted = new StringBuilder(value.length());
                for (int i = 0; i < value.length(); i++) {
                    String candidate = accepted.toString() + value.charAt(i);
                    if (filter.test(candidate)) accepted.append(value.charAt(i));
                }
                if (!accepted.toString().equals(value)) editBox.setValue(accepted.toString());
            }
        });
    }

    public static void enableScissor(Object graphics, int x, int y, int width, int height) {
        if (graphics instanceof net.minecraft.client.gui.GuiGraphicsExtractor extractor) {
            extractor.enableScissor(x, y, x + width, y + height);
        }
    }

    public static void disableScissor(Object graphics) {
        if (graphics instanceof net.minecraft.client.gui.GuiGraphicsExtractor extractor) extractor.disableScissor();
    }

    public static CommonId registerDynamicTexture(String name, net.minecraft.client.renderer.texture.DynamicTexture texture) {
        net.minecraft.resources.Identifier id = net.minecraft.resources.Identifier.fromNamespaceAndPath("buildscape", name);
        net.minecraft.client.Minecraft.getInstance().getTextureManager().register(id, texture);
        return CommonId.of(id.getNamespace(), id.getPath());
    }

    public static net.minecraft.client.renderer.texture.DynamicTexture createDynamicTexture(String name, int width, int height, boolean clear) {
        return new net.minecraft.client.renderer.texture.DynamicTexture(name, width, height, clear);
    }

    public static boolean hasPlayerPermissions(net.minecraft.world.entity.player.Player player, int level) {
        if (!(player instanceof net.minecraft.client.player.LocalPlayer)) return false;
        net.minecraft.server.permissions.Permission permission = switch (Math.max(0, Math.min(4, level))) {
            case 0 -> null;
            case 1 -> net.minecraft.server.permissions.Permissions.COMMANDS_MODERATOR;
            case 2 -> net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER;
            case 3 -> net.minecraft.server.permissions.Permissions.COMMANDS_ADMIN;
            default -> net.minecraft.server.permissions.Permissions.COMMANDS_OWNER;
        };
        return permission == null || ((net.minecraft.client.player.LocalPlayer) player).permissions().hasPermission(permission);
    }

    public static void openScreen(net.minecraft.client.gui.screens.Screen screen) { net.minecraft.client.Minecraft.getInstance().setScreenAndShow(screen); }

    public static void beginGuiOverlayRender() { ClientGuiHooks.beginOverlay(); }

    public static void endGuiOverlayRender() { ClientGuiHooks.endOverlay(); }

    public static void resetShaderColor() { ClientGuiHooks.resetShaderColor(); }

    public static void renderPillarMarkers(com.mojang.blaze3d.vertex.PoseStack poseStack, net.minecraft.client.Camera camera) {
        ClientWorldHooks.renderPillarMarkers(poseStack, camera);
    }

    public static net.minecraft.network.chat.Component parseComponentJson(String json) {
        net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
        return minecraft.level == null
                ? null
                : net.minecraft.network.chat.ComponentSerialization.CODEC.parse(
                        net.minecraft.resources.RegistryOps.create(
                                com.mojang.serialization.JsonOps.INSTANCE,
                                minecraft.level.registryAccess()),
                        com.google.gson.JsonParser.parseString(json))
                .result()
                .orElse(null);
    }

    public static net.minecraft.client.KeyMapping createKeyMapping(String translationKey, int keyCode, String categoryTranslationKey) {
        net.minecraft.client.KeyMapping.Category category = net.minecraft.client.KeyMapping.Category.register(net.minecraft.resources.Identifier.fromNamespaceAndPath("buildscape", "buildscape"));
        return new net.minecraft.client.KeyMapping(translationKey, com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM, keyCode, category);
    }

    public static boolean hasCurrentUser() {
        return net.minecraft.client.Minecraft.getInstance().getUser() != null;
    }

    public static String getCurrentUserUuid() {
        net.minecraft.client.User user = net.minecraft.client.Minecraft.getInstance().getUser();
        java.util.UUID id = user != null ? user.getProfileId() : null;
        return id != null ? id.toString() : null;
    }

    public static void fill(Object poseStackOrGraphics, int minX, int minY, int maxX, int maxY, int color) {
        if (poseStackOrGraphics instanceof net.minecraft.client.gui.GuiGraphicsExtractor extractor) {
            extractor.fill(minX, minY, maxX, maxY, ClientGuiHooks.color(color));
        }
    }

    public static void drawShadow(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, String text, int x, int y, int color) {
        if (poseStackOrGraphics instanceof net.minecraft.client.gui.GuiGraphicsExtractor extractor) {
            extractor.text(font, text, x, y, ClientGuiHooks.color(color), true);
        }
    }

    public static void draw(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, String text, float x, float y, int color) {
        if (poseStackOrGraphics instanceof net.minecraft.client.gui.GuiGraphicsExtractor extractor) {
            extractor.text(font, text, (int) x, (int) y, ClientGuiHooks.color(color), false);
        }
    }

    public static void drawCenteredString(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, net.minecraft.network.chat.Component component, int x, int y, int color) {
        if (poseStackOrGraphics instanceof net.minecraft.client.gui.GuiGraphicsExtractor extractor) {
            String text = component.getString();
            extractor.text(font, text, (int) (x - font.width(text) / 2), y, ClientGuiHooks.color(color), true);
        }
    }

    public static void drawCenteredString(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, String text, int x, int y, int color) {
        if (poseStackOrGraphics instanceof net.minecraft.client.gui.GuiGraphicsExtractor extractor) {
            extractor.text(font, text, (int) (x - font.width(text) / 2), y, ClientGuiHooks.color(color), true);
        }
    }

    public static void blit(Object poseStackOrGraphics, CommonId texture, int x, int y, float u, float v, int width, int height, int sheetW, int sheetH) {
        if (texture != null && poseStackOrGraphics instanceof net.minecraft.client.gui.GuiGraphicsExtractor extractor) {
            net.minecraft.resources.Identifier id = net.minecraft.resources.Identifier.fromNamespaceAndPath(texture.getNamespace(), texture.getPath());
            extractor.blit(id, x, y, width, height, u / sheetW, v / sheetH, (u + width) / sheetW, (v + height) / sheetH);
        }
    }

    public static void blit(Object poseStackOrGraphics, CommonId texture, int x, int y, int width, int height, float u, float v, int uWidth, int vHeight, int sheetW, int sheetH) {
        if (texture != null && poseStackOrGraphics instanceof net.minecraft.client.gui.GuiGraphicsExtractor extractor) {
            net.minecraft.resources.Identifier id = net.minecraft.resources.Identifier.fromNamespaceAndPath(texture.getNamespace(), texture.getPath());
            extractor.blit(id, x, y, width, height, u / sheetW, v / sheetH, (u + uWidth) / sheetW, (v + vHeight) / sheetH);
        }
    }

    public static void renderComponentTooltip(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, java.util.List<net.minecraft.network.chat.Component> components, int mouseX, int mouseY) {
        if (poseStackOrGraphics instanceof net.minecraft.client.gui.GuiGraphicsExtractor extractor
                && components != null && !components.isEmpty()) {
            extractor.setComponentTooltipForNextFrame(font, components, mouseX, mouseY);
        }
    }

    public static com.mojang.blaze3d.vertex.PoseStack toPoseStack(Object poseStackOrGraphics) {
        return com.kingodogo.buildscape.adapter.v26x.GuiProvider.toPoseStack(poseStackOrGraphics);
    }

    public static void pushGuiPose(Object context) {
        if (context instanceof net.minecraft.client.gui.GuiGraphicsExtractor extractor) extractor.pose().pushMatrix();
        else if (context instanceof com.mojang.blaze3d.vertex.PoseStack pose) pose.pushPose();
    }

    public static void popGuiPose(Object context) {
        if (context instanceof net.minecraft.client.gui.GuiGraphicsExtractor extractor) extractor.pose().popMatrix();
        else if (context instanceof com.mojang.blaze3d.vertex.PoseStack pose) pose.popPose();
    }

    public static void translateGuiPose(Object context, float x, float y, float z) {
        if (context instanceof net.minecraft.client.gui.GuiGraphicsExtractor extractor) {
            extractor.pose().translate(x, y);
            if (z > 0.0f) extractor.nextStratum();
        } else if (context instanceof com.mojang.blaze3d.vertex.PoseStack pose) pose.translate(x, y, z);
    }

    public static void scaleGuiPose(Object context, float x, float y) {
        if (context instanceof net.minecraft.client.gui.GuiGraphicsExtractor extractor) extractor.pose().scale(x, y);
        else if (context instanceof com.mojang.blaze3d.vertex.PoseStack pose) pose.scale(x, y, 1.0f);
    }

    public static void renderWidget(Object poseStackOrGraphics, net.minecraft.client.gui.components.AbstractWidget widget, int mouseX, int mouseY, float partialTick) {
        com.kingodogo.buildscape.adapter.v26x.GuiProvider.renderWidget(poseStackOrGraphics, widget, mouseX, mouseY, partialTick);
    }

    public static net.minecraft.world.phys.AABB getModelBounds(Object model) {
        return model instanceof net.minecraft.client.renderer.item.ItemStackRenderState state
                ? state.getModelBoundingBox() : new AABB(0, 0, 0, 1, 1, 1);
    }

    public static void renderItemFixed(ItemStack stack, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay, Object model) {
        captureItem(stack, net.minecraft.world.item.ItemDisplayContext.FIXED, poseStack, bufferSource, combinedLight, combinedOverlay, 0);
    }

    public static void renderItemStatic(ItemStack stack, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay, int seed) {
        captureItem(stack, net.minecraft.world.item.ItemDisplayContext.NONE, poseStack, bufferSource, combinedLight, combinedOverlay, seed);
    }

    private static void captureItem(ItemStack stack, net.minecraft.world.item.ItemDisplayContext context,
            com.mojang.blaze3d.vertex.PoseStack pose, Object buffer, int light, int overlay, int seed) {
        if (!(buffer instanceof RenderCapture capture) || stack.isEmpty()) return;
        var state = new net.minecraft.client.renderer.item.ItemStackRenderState();
        var client = net.minecraft.client.Minecraft.getInstance();
        client.getItemModelResolver().updateForTopItem(state, stack, context, client.level, null, seed);
        capture.record(pose, (target, collector, camera) -> state.submit(target, collector, light, overlay, 0));
    }

    public static Object getItemModel(ItemStack stack, Level level, int seed) {
        var state = new net.minecraft.client.renderer.item.ItemStackRenderState();
        net.minecraft.client.Minecraft.getInstance().getItemModelResolver().updateForTopItem(
                state, stack, net.minecraft.world.item.ItemDisplayContext.NONE, level, null, seed);
        return state;
    }

    public static boolean isGui3dModel(Object model) {
        return model instanceof net.minecraft.client.renderer.item.ItemStackRenderState state && state.usesBlockLight();
    }

    public static void playButtonClick() {
        net.minecraft.client.Minecraft.getInstance().getSoundManager().play(
                net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public static void playNoteBlockBell() {
        net.minecraft.client.Minecraft.getInstance().getSoundManager().play(
                net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(SoundEvents.NOTE_BLOCK_BELL, 1.0F));
    }

    public static void playNoteBlockDidgeridoo() {
        net.minecraft.client.Minecraft.getInstance().getSoundManager().play(
                net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(SoundEvents.NOTE_BLOCK_DIDGERIDOO, 1.0F));
    }

    public static void renderModelPart(net.minecraft.client.model.geom.ModelPart part, com.mojang.blaze3d.vertex.PoseStack poseStack, com.mojang.blaze3d.vertex.VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (part != null) {
            int a = Math.round(alpha * 255.0F);
            int r = Math.round(red * 255.0F);
            int g = Math.round(green * 255.0F);
            int b = Math.round(blue * 255.0F);
            int color = (a << 24) | (r << 16) | (g << 8) | b;
            part.render(poseStack, buffer, packedLight, packedOverlay, color);
        }
    }

    public static int getRenderDistanceChunks() {
        return net.minecraft.client.Minecraft.getInstance().options.renderDistance().get();
    }

    public static void renderLineBox(com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float r, float g, float b, float a) {
        ClientWorldHooks.renderLineBox(poseStack, bufferSource, minX, minY, minZ, maxX, maxY, maxZ, r, g, b, a);
    }

    public static boolean isScreenOpen() {
        return net.minecraft.client.Minecraft.getInstance() != null && net.minecraft.client.Minecraft.getInstance().gui != null && net.minecraft.client.Minecraft.getInstance().gui.screen() != null;
    }

    public static net.minecraft.world.phys.Vec3 getCameraPosition(net.minecraft.client.Camera camera) {
        return camera != null ? camera.position() : net.minecraft.world.phys.Vec3.ZERO;
    }

    public static void renderFallingIcicleBlock(net.minecraft.world.level.Level level, net.minecraft.world.level.block.state.BlockState blockState, net.minecraft.core.BlockPos blockPos, net.minecraft.core.BlockPos startPos, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource) {
        renderBlockModelWithTint(blockState, blockPos, level, poseStack, bufferSource,
                Services.PLATFORM.getLightColor(level, blockPos), net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);
    }

    private static void renderQuadWithUV26(com.mojang.blaze3d.vertex.VertexConsumer consumer, org.joml.Matrix4f pose, com.mojang.blaze3d.vertex.PoseStack.Pose lastPose, int packedLight,
                                     float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4,
                                     float u1, float v1, float u2, float v2, float nx, float ny, float nz) {
        consumer.addVertex(pose, x1, y1, z1).setColor(255, 255, 255, 255).setUv(u1, v1).setOverlay(0).setLight(packedLight).setNormal(lastPose, nx, ny, nz);
        consumer.addVertex(pose, x2, y2, z2).setColor(255, 255, 255, 255).setUv(u2, v1).setOverlay(0).setLight(packedLight).setNormal(lastPose, nx, ny, nz);
        consumer.addVertex(pose, x3, y3, z3).setColor(255, 255, 255, 255).setUv(u2, v2).setOverlay(0).setLight(packedLight).setNormal(lastPose, nx, ny, nz);
        consumer.addVertex(pose, x4, y4, z4).setColor(255, 255, 255, 255).setUv(u1, v2).setOverlay(0).setLight(packedLight).setNormal(lastPose, nx, ny, nz);
    }

    private static void renderBoxFaces26(com.mojang.blaze3d.vertex.VertexConsumer consumer, org.joml.Matrix4f pose, com.mojang.blaze3d.vertex.PoseStack.Pose lastPose, int packedLight,
                                   float x1, float y1, float z1, float x2, float y2, float z2) {
        renderQuadWithUV26(consumer, pose, lastPose, packedLight, x2, y1, z1, x1, y1, z1, x1, y2, z1, x2, y2, z1, x1, 1F - y2, x2, 1F - y1, 0, 0, -1);
        renderQuadWithUV26(consumer, pose, lastPose, packedLight, x1, y1, z2, x2, y1, z2, x2, y2, z2, x1, y2, z2, x1, 1F - y2, x2, 1F - y1, 0, 0, 1);
        renderQuadWithUV26(consumer, pose, lastPose, packedLight, x1, y1, z2, x2, y1, z2, x2, y1, z1, x1, y1, z1, x1, z1, x2, z2, 0, -1, 0);
        renderQuadWithUV26(consumer, pose, lastPose, packedLight, x1, y2, z1, x2, y2, z1, x2, y2, z2, x1, y2, z2, x1, z1, x2, z2, 0, 1, 0);
        renderQuadWithUV26(consumer, pose, lastPose, packedLight, x1, y1, z1, x1, y1, z2, x1, y2, z2, x1, y2, z1, z1, 1F - y2, z2, 1F - y1, -1, 0, 0);
        renderQuadWithUV26(consumer, pose, lastPose, packedLight, x2, y1, z2, x2, y1, z1, x2, y2, z1, x2, y2, z2, z1, 1F - y2, z2, 1F - y1, 1, 0, 0);
    }

    public static void renderColoredFrame(com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int packedLight, Object backTexture, boolean hasMap) {
        if (bufferSource instanceof RenderCapture capture) {
            CommonId texture = (CommonId) backTexture;
            renderColoredFrameGeometry(poseStack,
                    capture.geometry(net.minecraft.client.renderer.rendertype.RenderTypes.entityCutout(
                            Identifier.fromNamespaceAndPath(texture.getNamespace(), texture.getPath()))),
                    capture.geometry(net.minecraft.client.renderer.rendertype.RenderTypes.entityCutout(BIRCH_PLANKS_26)),
                    packedLight, hasMap);
            return;
        }
        if (!(bufferSource instanceof com.mojang.blaze3d.vertex.VertexConsumer consumer)) return;
        renderColoredFrameGeometry(poseStack, consumer, consumer, packedLight, hasMap);
    }

    private static void renderColoredFrameGeometry(com.mojang.blaze3d.vertex.PoseStack poseStack,
            VertexConsumer consumer, VertexConsumer frameConsumer, int packedLight, boolean hasMap) {
        poseStack.pushPose();
        poseStack.translate(-0.5D, -0.5D, -0.5D);

        org.joml.Matrix4f pose = poseStack.last().pose();
        com.mojang.blaze3d.vertex.PoseStack.Pose lastPose = poseStack.last();

        float backZ1 = hasMap ? 15.001F / 16F : 15.5F / 16F;
        float backZ2 = 1.0F;
        float frameZ1 = hasMap ? 15.001F / 16F : 15F / 16F;
        float frameZ2 = 1.0F;

        float x1 = hasMap ? 1F / 16F : 3F / 16F;
        float x2 = hasMap ? 15F / 16F : 13F / 16F;
        float y1 = hasMap ? 1F / 16F : 3F / 16F;
        float y2 = hasMap ? 15F / 16F : 13F / 16F;

        renderQuadWithUV26(consumer, pose, lastPose, packedLight, x1, y1, backZ1, x2, y1, backZ1, x2, y2, backZ1, x1, y2, backZ1, x1, y2, x2, y1, 0, 0, -1);
        renderQuadWithUV26(consumer, pose, lastPose, packedLight, x2, y1, backZ2, x1, y1, backZ2, x1, y2, backZ2, x2, y2, backZ2, x1, y2, x2, y1, 0, 0, 1);

        if (hasMap) {
            renderBoxFaces26(frameConsumer, pose, lastPose, packedLight, 1F / 16F, 0F, frameZ1, 15F / 16F, 1F / 16F, frameZ2);
            renderBoxFaces26(frameConsumer, pose, lastPose, packedLight, 1F / 16F, 15F / 16F, frameZ1, 15F / 16F, 1F, frameZ2);
            renderBoxFaces26(frameConsumer, pose, lastPose, packedLight, 0F, 0F, frameZ1, 1F / 16F, 1F, frameZ2);
            renderBoxFaces26(frameConsumer, pose, lastPose, packedLight, 15F / 16F, 0F, frameZ1, 1F, 1F, frameZ2);
        } else {
            renderBoxFaces26(frameConsumer, pose, lastPose, packedLight, 3F / 16F, 2F / 16F, frameZ1, 13F / 16F, 3F / 16F, frameZ2);
            renderBoxFaces26(frameConsumer, pose, lastPose, packedLight, 3F / 16F, 13F / 16F, frameZ1, 13F / 16F, 14F / 16F, frameZ2);
            renderBoxFaces26(frameConsumer, pose, lastPose, packedLight, 2F / 16F, 2F / 16F, frameZ1, 3F / 16F, 14F / 16F, frameZ2);
            renderBoxFaces26(frameConsumer, pose, lastPose, packedLight, 13F / 16F, 2F / 16F, frameZ1, 14F / 16F, 14F / 16F, frameZ2);
        }
        poseStack.popPose();
    }

    public static void renderColoredFrameItem(com.kingodogo.buildscape.entity.ColoredItemFrameEntity entity, net.minecraft.world.item.ItemStack itemStack, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int packedLight, boolean isMap, boolean isInvisible) {
        if (!(bufferSource instanceof RenderCapture capture)) return;
        poseStack.pushPose();
        try {
            poseStack.translate(0, 0, isInvisible ? 0.5 : 0.4375);
            int rotation = entity.getRotation();
            poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees((isMap ? rotation % 4 * 2 : rotation) * 45.0F));
            if (isMap) {
                var data = net.minecraft.world.item.MapItem.getSavedData(itemStack, net.minecraft.client.Minecraft.getInstance().level);
                var id = itemStack.get(net.minecraft.core.component.DataComponents.MAP_ID);
                if (data != null && id != null) {
                    var map = new net.minecraft.client.renderer.state.MapRenderState();
                    var renderer = net.minecraft.client.Minecraft.getInstance().getMapRenderer();
                    renderer.extractRenderState(id, data, map);
                    poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(180));
                    poseStack.scale(1.0F / 128, 1.0F / 128, 1.0F / 128);
                    poseStack.translate(-64, -64, 0);
                    capture.record(poseStack, (target, collector, camera) -> renderer.render(map, target, collector, true, packedLight));
                }
            } else {
                poseStack.scale(0.5F, 0.5F, 0.5F);
                captureItem(itemStack, net.minecraft.world.item.ItemDisplayContext.FIXED, poseStack, bufferSource,
                        packedLight, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, 0);
            }
        } finally {
            poseStack.popPose();
        }
    }

    public static void renderStockingQuad(com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int packedLight, Object texture, boolean flipped) {
        if (bufferSource instanceof RenderCapture capture) {
            CommonId id = (CommonId) texture;
            renderStockingQuad(poseStack, capture.geometry(net.minecraft.client.renderer.rendertype.RenderTypes.entityCutout(
                    Identifier.fromNamespaceAndPath(id.getNamespace(), id.getPath()))), packedLight, texture, flipped);
            return;
        }
        if (bufferSource instanceof com.mojang.blaze3d.vertex.VertexConsumer vertexConsumer) {
            float width = 0.5F;
            float height = 0.5F;
            float uMin = flipped ? 1.0F : 0.0F;
            float uMax = flipped ? 0.0F : 1.0F;
            float vMin = 0.0F;
            float vMax = 1.0F;

            org.joml.Matrix4f pose = poseStack.last().pose();
            com.mojang.blaze3d.vertex.PoseStack.Pose lastPose = poseStack.last();

            vertexConsumer.addVertex(pose, -width, -height, 0.0F).setColor(255, 255, 255, 255).setUv(uMin, vMax).setOverlay(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(lastPose, 0.0F, 0.0F, 1.0F);
            vertexConsumer.addVertex(pose, width, -height, 0.0F).setColor(255, 255, 255, 255).setUv(uMax, vMax).setOverlay(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(lastPose, 0.0F, 0.0F, 1.0F);
            vertexConsumer.addVertex(pose, width, height, 0.0F).setColor(255, 255, 255, 255).setUv(uMax, vMin).setOverlay(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(lastPose, 0.0F, 0.0F, 1.0F);
            vertexConsumer.addVertex(pose, -width, height, 0.0F).setColor(255, 255, 255, 255).setUv(uMin, vMin).setOverlay(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(lastPose, 0.0F, 0.0F, 1.0F);
        }
    }

    public static void renderBlockModel(net.minecraft.world.level.block.state.BlockState blockState, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int light, int overlay) {
        renderBlockModelWithTint(blockState, null, null, poseStack, bufferSource, light, overlay);
    }

    public static void renderBlockModelWithTint(net.minecraft.world.level.block.state.BlockState state, net.minecraft.core.BlockPos pos, net.minecraft.world.level.Level level, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int light, int overlay) {
        if (!(bufferSource instanceof RenderCapture capture) || state.isAir()) return;
        var client = net.minecraft.client.Minecraft.getInstance();
        var model = new net.minecraft.client.renderer.block.BlockModelRenderState();
        new net.minecraft.client.renderer.block.BlockModelResolver(client.getModelManager()).update(
                model, state, net.minecraft.client.renderer.block.model.BlockDisplayContext.create());
        var tints = model.tintLayers();
        tints.clear();
        var view = new net.minecraft.client.renderer.block.MovingBlockRenderState();
        if (level != null && pos != null) {
            view.blockState = state;
            view.blockPos = pos;
            view.randomSeedPos = pos;
            view.biome = level.getBiome(pos);
        }
        for (var source : client.getBlockColors().getTintSources(state)) {
            tints.add(level != null && pos != null ? source.colorInWorld(state, view, pos) : source.color(state));
        }
        capture.record(poseStack, (target, collector, camera) -> model.submit(target, collector, light, overlay, 0));
    }

    public static void renderColoredQuad(com.mojang.blaze3d.vertex.PoseStack poseStack, Object buffer,
                                  float x0, float y0, float z0, float u0, float v0,
                                  float x1, float y1, float z1, float u1, float v1,
                                  float x2, float y2, float z2, float u2, float v2,
                                  float x3, float y3, float z3, float u3, float v3,
                                  float r, float g, float b, float a,
                                  int light, int overlay,
                                  float nx, float ny, float nz) {
        if (buffer instanceof RenderCapture capture) buffer = capture.translucent();
        if (!(buffer instanceof com.mojang.blaze3d.vertex.VertexConsumer vc)) return;
        org.joml.Matrix4f matrix = poseStack.last().pose();
        com.mojang.blaze3d.vertex.PoseStack.Pose lastPose = poseStack.last();
        vc.addVertex(matrix, x0, y0, z0).setColor(r, g, b, a).setUv(u0, v0).setOverlay(overlay).setLight(light).setNormal(lastPose, nx, ny, nz);
        vc.addVertex(matrix, x1, y1, z1).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(lastPose, nx, ny, nz);
        vc.addVertex(matrix, x2, y2, z2).setColor(r, g, b, a).setUv(u2, v2).setOverlay(overlay).setLight(light).setNormal(lastPose, nx, ny, nz);
        vc.addVertex(matrix, x3, y3, z3).setColor(r, g, b, a).setUv(u3, v3).setOverlay(overlay).setLight(light).setNormal(lastPose, nx, ny, nz);
        if (ny > 0) {
            vc.addVertex(matrix, x3, y3, z3).setColor(r, g, b, a).setUv(u3, v3).setOverlay(overlay).setLight(light).setNormal(lastPose, -nx, -ny, -nz);
            vc.addVertex(matrix, x2, y2, z2).setColor(r, g, b, a).setUv(u2, v2).setOverlay(overlay).setLight(light).setNormal(lastPose, -nx, -ny, -nz);
            vc.addVertex(matrix, x1, y1, z1).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(lastPose, -nx, -ny, -nz);
            vc.addVertex(matrix, x0, y0, z0).setColor(r, g, b, a).setUv(u0, v0).setOverlay(overlay).setLight(light).setNormal(lastPose, -nx, -ny, -nz);
        }
    }

    public static void renderJarFluid(com.kingodogo.buildscape.block.GlassJarBlockEntity blockEntity, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int light, int overlay) {
        if (blockEntity == null || !blockEntity.hasLiquid()) return;
        var state = new GlassJarRenderer.State();
        GlassJarRenderer.extractContents(blockEntity, state, 0, false);
        if (state.liquidSprite == null) return;
        if (bufferSource instanceof RenderCapture capture) {
            capture.record(poseStack, (pose, collector, camera) -> collector.submitCustomGeometry(pose,
                    net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(
                            net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS),
                    (transform, consumer) -> GlassJarRenderer.submitLiquid(state, transform, consumer, light, overlay)));
        } else if (bufferSource instanceof com.mojang.blaze3d.vertex.VertexConsumer consumer) {
            GlassJarRenderer.submitLiquid(state, poseStack.last(), consumer, light, overlay);
        } else if (bufferSource instanceof net.minecraft.client.renderer.SubmitNodeCollector collector) {
            collector.submitCustomGeometry(poseStack,
                    net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(
                            net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS),
                    (transform, consumer) -> GlassJarRenderer.submitLiquid(state, transform, consumer, light, overlay));
        }
    }

    public static void renderGlassJar(com.kingodogo.buildscape.block.GlassJarBlockEntity blockEntity, float partialTicks, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay) {
        if (blockEntity == null) return;
        var state = new GlassJarRenderer.State();
        GlassJarRenderer.extractContents(blockEntity, state, partialTicks, true);
        if (bufferSource instanceof RenderCapture capture) {
            capture.record(poseStack, (pose, collector, camera) ->
                    GlassJarRenderer.submitContents(state, pose, collector, combinedLight, combinedOverlay, 0));
        } else if (bufferSource instanceof net.minecraft.client.renderer.SubmitNodeCollector collector) {
            GlassJarRenderer.submitContents(state, poseStack, collector, combinedLight, combinedOverlay, 0);
        }
    }

    public static void renderWobblyBlock(net.minecraft.world.level.block.state.BlockState state, long currentTick, long wobbleStartTick, boolean hasWobble, float partialTicks, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int light, int overlay) {
        poseStack.pushPose();
        if (hasWobble && wobbleStartTick > 0) {
            float ticksSinceWobble = (float) (currentTick - wobbleStartTick) + partialTicks;
            if (ticksSinceWobble < 10.0F) {
                float wobbleProgress = ticksSinceWobble / 10.0F;
                float dampening = 1.0F - wobbleProgress;
                float oscillation = (float) Math.sin(wobbleProgress * Math.PI * 6);
                float rotationAngle = 8.0F * dampening * oscillation;

                poseStack.translate(0.5D, 0.0D, 0.5D);
                poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(rotationAngle));
                poseStack.translate(-0.5D, 0.0D, -0.5D);
            }
        }
        renderBlockModel(state, poseStack, bufferSource, light, overlay);
        poseStack.popPose();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void renderCopperChest(com.kingodogo.buildscape.block.CopperChestBlockEntity blockEntity, float partialTicks, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay) {
        if (blockEntity == null) return;
        net.minecraft.client.renderer.blockentity.BlockEntityRenderer renderer = net.minecraft.client.Minecraft.getInstance()
                .getBlockEntityRenderDispatcher().getRenderer(blockEntity);
        if (renderer == null) return;
        var state = renderer.createRenderState();
        renderer.extractRenderState(blockEntity, state, partialTicks, Vec3.ZERO, null);
        state.lightCoords = combinedLight;
        if (bufferSource instanceof RenderCapture capture) {
            capture.record(poseStack, (pose, collector, camera) -> renderer.submit(state, pose, collector, camera));
        } else if (bufferSource instanceof net.minecraft.client.renderer.SubmitNodeCollector collector) {
            renderer.submit(state, poseStack, collector, null);
        }
    }

    public static void renderClientOverlay(Object context, int width, int height) {
        if (!(context instanceof net.minecraft.client.gui.GuiGraphicsExtractor graphics)) return;
        var message = com.kingodogo.buildscape.client.ClientEvents.getOverlayMessage();
        long elapsed = System.currentTimeMillis() - com.kingodogo.buildscape.client.ClientEvents.getOverlayMessageTime();
        if (message == null) return;
        if (elapsed > 5000) { com.kingodogo.buildscape.client.ClientEvents.setOverlayMessage(null); return; }
        float seconds = elapsed / 1000.0F;
        float scale = seconds < 0.25F ? seconds / 0.25F * 1.2F
                : seconds < 0.4F ? 1.2F - (seconds - 0.25F) / 0.15F * 0.2F : 1;
        ClientGuiHooks.withGraphics(graphics, () -> {
            beginGuiOverlayRender();
            try {
                int x = width / 2, y = height / 2 + 30;
                graphics.pose().translate(x, y);
                graphics.pose().scale(scale, scale);
                graphics.pose().translate(-x, -y);
                graphics.centeredText(net.minecraft.client.Minecraft.getInstance().font, message, x, y, 0xFFFF5555);
            } finally { endGuiOverlayRender(); }
        });
    }

    public static void registerMenuScreens() {
        if (!Services.PLATFORM.isClient()) return;
        com.kingodogo.buildscape.adapter.v26x.GuiProvider.registerMenuScreens();
        registerRenderers();
    }

    public static void registerRenderers() {
        if (!Services.PLATFORM.isClient()) return;
        RenderFactory.registerRenderers();
    }

    public static void renderEntity(net.minecraft.world.entity.Entity entity, double x, double y, double z, float yaw, float partialTicks, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int packedLight) {
        RenderFactory.renderEntity(entity, x, y, z, yaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    public static com.mojang.blaze3d.vertex.VertexConsumer getTranslucentBuffer(Object bufferSource) {
        return RenderFactory.getTranslucentBuffer(bufferSource);
    }

    public static net.minecraft.client.renderer.texture.TextureAtlasSprite getBlockAtlasSprite(CommonId id) {
        return net.minecraft.client.Minecraft.getInstance().getAtlasManager()
                .getAtlasOrThrow(net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS)
                .getSprite(Identifier.fromNamespaceAndPath(id.getNamespace(), id.getPath()));
    }

    public static boolean isTranslucent(net.minecraft.world.level.block.state.BlockState state) {
        if (state == null) return false;
        try {
            net.minecraft.client.renderer.block.dispatch.BlockStateModel model = net.minecraft.client.Minecraft.getInstance()
                    .getModelManager().getBlockStateModelSet().get(state);
            java.util.List<net.minecraft.client.renderer.block.dispatch.BlockStateModelPart> parts = new java.util.ArrayList<>();
            model.collectParts(net.minecraft.util.RandomSource.create(42L), parts);
            for (net.minecraft.client.renderer.block.dispatch.BlockStateModelPart part : parts) {
                if (part.particleMaterial().forceTranslucent()) return true;
                for (net.minecraft.core.Direction side : net.minecraft.core.Direction.values()) {
                    for (net.minecraft.client.resources.model.geometry.BakedQuad quad : part.getQuads(side)) {
                        if (quad.materialInfo().layer().translucent()) return true;
                    }
                }
            }
        } catch (Throwable exception) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Failed in 26.x isTranslucent", exception);}
        return false;
    }

    public static BlockColorSample sampleBlockColor(net.minecraft.world.level.block.state.BlockState state) {
        if (state == null) return null;
        try {
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            net.minecraft.client.renderer.block.dispatch.BlockStateModel model = mc.getModelManager()
                    .getBlockStateModelSet().get(state);
            java.util.List<net.minecraft.client.renderer.block.dispatch.BlockStateModelPart> parts = new java.util.ArrayList<>();
            model.collectParts(net.minecraft.util.RandomSource.create(42L), parts);
            double red = 0, green = 0, blue = 0;
            int weight = 0;
            boolean transparent = false;
            java.util.Set<String> textures = new java.util.HashSet<>();
            for (net.minecraft.client.renderer.block.dispatch.BlockStateModelPart part : parts) {
                for (net.minecraft.core.Direction side : net.minecraft.core.Direction.values()) {
                    for (net.minecraft.client.resources.model.geometry.BakedQuad quad : part.getQuads(side)) {
                        net.minecraft.client.renderer.texture.TextureAtlasSprite sprite = quad.materialInfo().sprite();
                        String spriteName = sprite.contents().name().toString();
                        if (spriteName.contains("missingno")) continue;
                        textures.add(spriteName);
                        int tint = 0xFFFFFF;
                        if (quad.materialInfo().isTinted()) {
                            net.minecraft.client.color.block.BlockTintSource source = mc.getBlockColors()
                                    .getTintSource(state, quad.materialInfo().tintIndex());
                            if (source != null) tint = source.color(state) & 0xFFFFFF;
                        }
                        int resolvedTint = tint;
                        double[] sample = v26xSpriteCache.computeIfAbsent(spriteName + "#" + tint, key -> {
                            com.mojang.blaze3d.platform.NativeImage image = getV26SpriteImage(sprite.contents());
                            if (image == null) return new double[]{0, 0, 0, 0, 0};
                            int width = sprite.contents().width(), height = sprite.contents().height();
                            int stepX = Math.max(1, (width + COLOR_MAX_SAMPLES - 1) / COLOR_MAX_SAMPLES);
                            int stepY = Math.max(1, (height + COLOR_MAX_SAMPLES - 1) / COLOR_MAX_SAMPLES);
                            double r = 0, g = 0, b = 0, alphaWeight = 0;
                            boolean translucent = false;
                            for (int y = 0; y < height; y += stepY) for (int x = 0; x < width; x += stepX) {
                                int pixel = image.getPixel(x, y);
                                int alpha = net.minecraft.util.ARGB.alpha(pixel);
                                if (alpha < 250) translucent = true;
                                if (alpha < 16) continue;
                                double aw = alpha / 255.0;
                                int tinted = net.minecraft.util.ARGB.multiply(pixel, 0xFF000000 | resolvedTint);
                                r += net.minecraft.util.ARGB.srgbToLinearChannel(net.minecraft.util.ARGB.red(tinted)) * aw;
                                g += net.minecraft.util.ARGB.srgbToLinearChannel(net.minecraft.util.ARGB.green(tinted)) * aw;
                                b += net.minecraft.util.ARGB.srgbToLinearChannel(net.minecraft.util.ARGB.blue(tinted)) * aw;
                                alphaWeight += aw;
                            }
                            return alphaWeight == 0 ? new double[]{0, 0, 0, 0, translucent ? 1 : 0}
                                    : new double[]{r / alphaWeight, g / alphaWeight, b / alphaWeight, 1, translucent ? 1 : 0};
                        });
                        if (sample[3] > 0) {
                            red += sample[0]; green += sample[1]; blue += sample[2];
                            transparent |= sample[4] > 0.5 || quad.materialInfo().layer().translucent();
                            weight++;
                        }
                    }
                }
            }
            if (weight == 0) return null;
            int rgb = (net.minecraft.util.ARGB.linearToSrgbChannel((float) (red / weight)) << 16)
                    | (net.minecraft.util.ARGB.linearToSrgbChannel((float) (green / weight)) << 8)
                    | net.minecraft.util.ARGB.linearToSrgbChannel((float) (blue / weight));
            return new BlockColorSample(rgb, transparent, textures.size() == 1);
        } catch (Throwable exception) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Failed in 26.x sampleBlockColor", exception);
            return null;
        }
    }

    private static com.mojang.blaze3d.platform.NativeImage getV26SpriteImage(
            net.minecraft.client.renderer.texture.SpriteContents contents) {
        try {
            java.lang.reflect.Field image = net.minecraft.client.renderer.texture.SpriteContents.class
                    .getDeclaredField("originalImage");
            image.setAccessible(true);
            return (com.mojang.blaze3d.platform.NativeImage) image.get(contents);
        } catch (ReflectiveOperationException exception) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Failed to read sprite image on 26.x", exception);
            return null;
        }
    }

    public static net.minecraft.client.gui.screens.Screen createConfigScreen(net.minecraft.client.gui.screens.Screen parent) {
        return com.kingodogo.buildscape.adapter.v26x.GuiProvider.createConfigScreen(parent);
    }

    public static net.minecraft.client.gui.components.Button createButton(int x, int y, int width, int height, net.minecraft.network.chat.Component message, net.minecraft.client.gui.components.Button.OnPress onPress) {
        return com.kingodogo.buildscape.adapter.v26x.GuiProvider.createButton(x, y, width, height, message, onPress);
    }

    public static net.minecraft.client.gui.components.Button createCustomButton(int x, int y, int width, int height, net.minecraft.network.chat.Component message, net.minecraft.client.gui.components.Button.OnPress onPress, com.kingodogo.buildscape.client.screen.widget.CustomButtonRenderer renderer) {
        return com.kingodogo.buildscape.adapter.v26x.GuiProvider.createCustomButton(x, y, width, height, message, onPress, renderer);
    }

    public static net.minecraft.client.gui.screens.Screen wrapScreen(net.minecraft.network.chat.Component title, net.minecraft.client.gui.screens.Screen parent, com.kingodogo.buildscape.client.screen.IScreenDelegate delegate) {
        return com.kingodogo.buildscape.adapter.v26x.GuiProvider.wrapScreen(title, parent, delegate);
    }

    public static net.minecraft.client.gui.screens.Screen createGuiEditorScreen(net.minecraft.client.gui.screens.Screen parent, String tabName, com.kingodogo.buildscape.client.screen.AbstractConfigTab sourceTab) {
        return com.kingodogo.buildscape.adapter.v26x.GuiProvider.createGuiEditorScreen(parent, tabName, sourceTab);
    }

    public static net.minecraft.client.gui.screens.Screen createInventoryItemSelectorScreen(net.minecraft.client.gui.screens.Screen parent, com.kingodogo.buildscape.client.screen.PillarItemsConfigTab configTab) {
        return com.kingodogo.buildscape.adapter.v26x.GuiProvider.createInventoryItemSelectorScreen(parent, configTab);
    }

    public static void renderGuiItem(Object poseStackOrGraphics, net.minecraft.world.item.ItemStack stack, int x, int y) {
        com.kingodogo.buildscape.adapter.v26x.GuiProvider.renderGuiItem(poseStackOrGraphics, stack, x, y);
    }

    public static void renderGuiItemDecorations(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, net.minecraft.world.item.ItemStack stack, int x, int y) {
        com.kingodogo.buildscape.adapter.v26x.GuiProvider.renderGuiItemDecorations(poseStackOrGraphics, font, stack, x, y);
    }

    public static java.util.List<net.minecraft.network.chat.Component> getTooltipFromItem(net.minecraft.world.item.ItemStack stack) {
        return com.kingodogo.buildscape.adapter.v26x.GuiProvider.getTooltipFromItem(stack);
    }

    public static net.minecraft.client.gui.components.AbstractWidget wrapCustomWidget(int x, int y, int width, int height, net.minecraft.network.chat.Component message, com.kingodogo.buildscape.client.screen.widget.ICustomWidget customWidget) {
        return com.kingodogo.buildscape.adapter.v26x.GuiProvider.wrapCustomWidget(x, y, width, height, message, customWidget);
    }
}
