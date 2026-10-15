package com.kingodogo.buildscape.adapter.v121x.client;

import com.kingodogo.buildscape.entity.ColoredItemFrameEntity;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.Direction;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.AABB;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import com.kingodogo.buildscape.platform.IPlatformAdapter.BlockColorSample;

/** Client bodies are isolated so dedicated servers can verify the platform adapter. */
public final class ClientAccess {
    private static final ThreadLocal<net.minecraft.client.gui.GuiGraphics> GUI_GRAPHICS = new ThreadLocal<>();

    public static void withGuiGraphics(net.minecraft.client.gui.GuiGraphics graphics, Runnable action) {
        var previous = GUI_GRAPHICS.get();
        GUI_GRAPHICS.set(graphics);
        try { action.run(); }
        finally {
            if (previous == null) GUI_GRAPHICS.remove();
            else GUI_GRAPHICS.set(previous);
        }
    }

    private static Object graphics(Object context) {
        return context instanceof com.mojang.blaze3d.vertex.PoseStack && GUI_GRAPHICS.get() != null
                ? GUI_GRAPHICS.get() : context;
    }
    public static com.mojang.blaze3d.vertex.PoseStack toPoseStack(Object context) {
        return GuiProvider.toPoseStack(context);
    }

    private ClientAccess() {}
    private static final net.minecraft.resources.ResourceLocation BIRCH_PLANKS_121 = net.minecraft.resources.ResourceLocation.tryParse("minecraft:textures/block/birch_planks.png");
    private static final int COLOR_MAX_SAMPLES = 32;

    public static int getClientParticleSetting() {
        return net.minecraft.client.Minecraft.getInstance().options.particles().get().ordinal();
    }

    public static Player getClientPlayer() {
        return net.minecraft.client.Minecraft.getInstance().player;
    }

    public static boolean hasShiftDown() {
        return net.minecraft.client.gui.screens.Screen.hasShiftDown();
    }

    public static boolean hasControlDown() {
        return net.minecraft.client.gui.screens.Screen.hasControlDown();
    }

    public static boolean widgetMouseClicked(net.minecraft.client.gui.components.AbstractWidget widget, double mouseX, double mouseY, int button) {
        return widget != null && widget.mouseClicked(mouseX, mouseY, button);
    }

    public static boolean widgetKeyPressed(net.minecraft.client.gui.components.AbstractWidget widget, int keyCode, int scanCode, int modifiers) {
        return widget != null && widget.keyPressed(keyCode, scanCode, modifiers);
    }

    public static boolean widgetCharTyped(net.minecraft.client.gui.components.AbstractWidget widget, char codePoint, int modifiers) {
        return widget != null && widget.charTyped(codePoint, modifiers);
    }

    public static void setEditBoxFilter(net.minecraft.client.gui.components.EditBox editBox, java.util.function.Predicate<String> filter) {
        editBox.setFilter(filter);
    }

    public static void enableScissor(Object graphics, int x, int y, int width, int height) {
        if (graphics instanceof net.minecraft.client.gui.GuiGraphics gg) gg.enableScissor(x, y, x + width, y + height);
        else com.mojang.blaze3d.systems.RenderSystem.enableScissor(x, y, width, height);
    }

    public static void disableScissor(Object graphics) {
        if (graphics instanceof net.minecraft.client.gui.GuiGraphics gg) gg.disableScissor();
        else com.mojang.blaze3d.systems.RenderSystem.disableScissor();
    }

    public static CommonId registerDynamicTexture(String name, net.minecraft.client.renderer.texture.DynamicTexture texture) {
        net.minecraft.resources.ResourceLocation id = net.minecraft.client.Minecraft.getInstance().getTextureManager().register(name, texture);
        return CommonId.of(id.getNamespace(), id.getPath());
    }

    public static CommonId registerExternalTexture(String name, java.io.InputStream stream) {
        try {
            com.mojang.blaze3d.platform.NativeImage image = com.mojang.blaze3d.platform.NativeImage.read(stream);
            net.minecraft.client.renderer.texture.DynamicTexture texture = new net.minecraft.client.renderer.texture.DynamicTexture(image);
            texture.upload();
            return registerDynamicTexture(name, texture);
        } catch (Throwable t) {
            return null;
        }
    }

    public static net.minecraft.client.renderer.texture.DynamicTexture createDynamicTexture(String name, int width, int height, boolean clear) {
        net.minecraft.client.renderer.texture.DynamicTexture texture = new net.minecraft.client.renderer.texture.DynamicTexture(width, height, clear);
        texture.setFilter(false, false);
        return texture;
    }

    public static void openScreen(net.minecraft.client.gui.screens.Screen screen) { net.minecraft.client.Minecraft.getInstance().setScreen(screen); }

    public static void beginGuiOverlayRender() { com.mojang.blaze3d.systems.RenderSystem.disableDepthTest(); com.mojang.blaze3d.systems.RenderSystem.enableBlend(); com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc(); }

    public static void endGuiOverlayRender() { com.mojang.blaze3d.systems.RenderSystem.disableBlend(); com.mojang.blaze3d.systems.RenderSystem.enableDepthTest(); }

    public static void resetShaderColor() { com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F); }


    public static net.minecraft.network.chat.Component parseComponentJson(String json) { return net.minecraft.network.chat.Component.Serializer.fromJson(json, net.minecraft.client.Minecraft.getInstance().level.registryAccess()); }

    public static net.minecraft.client.KeyMapping createKeyMapping(String translationKey, int keyCode, String categoryTranslationKey) { return new net.minecraft.client.KeyMapping(translationKey, com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM, keyCode, categoryTranslationKey); }

    public static boolean hasCurrentUser() {
        return net.minecraft.client.Minecraft.getInstance().getUser() != null;
    }

    public static String getCurrentUserUuid() {
        net.minecraft.client.User user = net.minecraft.client.Minecraft.getInstance().getUser();
        java.util.UUID id = user != null ? user.getProfileId() : null;
        return id != null ? id.toString() : null;
    }

    public static void rotateZ(com.mojang.blaze3d.vertex.PoseStack poseStack, float degrees) {
        poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(degrees));
    }

    public static void fill(Object poseStackOrGraphics, int minX, int minY, int maxX, int maxY, int color) {
        if (graphics(poseStackOrGraphics) instanceof net.minecraft.client.gui.GuiGraphics gg) {
            gg.fill(minX, minY, maxX, maxY, color);
        }
    }

    public static void drawShadow(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, String text, int x, int y, int color) {
        if (graphics(poseStackOrGraphics) instanceof net.minecraft.client.gui.GuiGraphics gg) {
            gg.drawString(font, text, x, y, color, true);
        }
    }

    public static void draw(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, String text, float x, float y, int color) {
        if (graphics(poseStackOrGraphics) instanceof net.minecraft.client.gui.GuiGraphics gg) {
            gg.drawString(font, text, (int) x, (int) y, color, false);
        }
    }

    public static void drawCenteredString(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, net.minecraft.network.chat.Component component, int x, int y, int color) {
        if (graphics(poseStackOrGraphics) instanceof net.minecraft.client.gui.GuiGraphics gg) {
            gg.drawCenteredString(font, component, x, y, color);
        }
    }

    public static void drawCenteredString(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, String text, int x, int y, int color) {
        if (graphics(poseStackOrGraphics) instanceof net.minecraft.client.gui.GuiGraphics gg) {
            gg.drawCenteredString(font, text, x, y, color);
        }
    }

    public static void bindTexture(CommonId id) {
        if (id != null) {
            com.mojang.blaze3d.systems.RenderSystem.setShaderTexture(0, ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath()));
        }
    }

    public static void blit(Object poseStackOrGraphics, CommonId texture, int x, int y, float u, float v, int width, int height, int sheetW, int sheetH) {
        if (texture != null) {
            ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(texture.getNamespace(), texture.getPath());
            if (graphics(poseStackOrGraphics) instanceof net.minecraft.client.gui.GuiGraphics gg) {
                gg.blit(rl, x, y, (int) u, (int) v, width, height, sheetW, sheetH);
            }
        }
    }

    public static void blit(Object poseStackOrGraphics, CommonId texture, int x, int y, int width, int height, float u, float v, int uWidth, int vHeight, int sheetW, int sheetH) {
        if (texture != null) {
            ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(texture.getNamespace(), texture.getPath());
            if (graphics(poseStackOrGraphics) instanceof net.minecraft.client.gui.GuiGraphics gg) {
                gg.blit(rl, x, y, width, height, u, v, uWidth, vHeight, sheetW, sheetH);
            }
        }
    }

    public static void renderComponentTooltip(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, java.util.List<net.minecraft.network.chat.Component> components, int mouseX, int mouseY) {
        if (graphics(poseStackOrGraphics) instanceof net.minecraft.client.gui.GuiGraphics gg) {
            gg.renderComponentTooltip(font, components, mouseX, mouseY);
        }
    }

    public static void renderClientOverlay(Object context, int width, int height) { com.mojang.blaze3d.vertex.PoseStack pose = toPoseStack(context); if (pose != null) com.kingodogo.buildscape.client.ClientEvents.renderOverlay(pose, width, height); }

    public static void renderWidget(Object poseStackOrGraphics, net.minecraft.client.gui.components.AbstractWidget widget, int mouseX, int mouseY, float partialTick) {
        com.kingodogo.buildscape.adapter.v121x.client.GuiProvider.renderWidget(poseStackOrGraphics, widget, mouseX, mouseY, partialTick);
    }

    public static net.minecraft.world.phys.AABB getModelBounds(Map<Object, AABB> modelBoundsCache, Object model) {
        if (!(model instanceof net.minecraft.client.resources.model.BakedModel bakedModel)) return new net.minecraft.world.phys.AABB(0, 0, 0, 1, 1, 1);
        return modelBoundsCache.computeIfAbsent(bakedModel, key -> {
            net.minecraft.client.resources.model.BakedModel m = (net.minecraft.client.resources.model.BakedModel) key;
            double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, minZ = Double.MAX_VALUE;
            double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;
            net.minecraft.util.RandomSource rand = net.minecraft.util.RandomSource.create(42L);
            for (net.minecraft.core.Direction dir : new net.minecraft.core.Direction[]{null, net.minecraft.core.Direction.DOWN, net.minecraft.core.Direction.UP, net.minecraft.core.Direction.NORTH, net.minecraft.core.Direction.SOUTH, net.minecraft.core.Direction.WEST, net.minecraft.core.Direction.EAST}) {
                java.util.List<net.minecraft.client.renderer.block.model.BakedQuad> quads = m.getQuads(null, dir, rand);
                for (net.minecraft.client.renderer.block.model.BakedQuad quad : quads) {
                    int[] vertices = quad.getVertices();
                    int step = vertices.length / 4;
                    for (int i = 0; i < 4; i++) {
                        float x = Float.intBitsToFloat(vertices[i * step]);
                        float y = Float.intBitsToFloat(vertices[i * step + 1]);
                        float z = Float.intBitsToFloat(vertices[i * step + 2]);
                        if (x < minX) minX = x;
                        if (y < minY) minY = y;
                        if (z < minZ) minZ = z;
                        if (x > maxX) maxX = x;
                        if (y > maxY) maxY = y;
                        if (z > maxZ) maxZ = z;
                    }
                }
            }
            if (minX == Double.MAX_VALUE) return new net.minecraft.world.phys.AABB(0, 0, 0, 1, 1, 1);
            return new net.minecraft.world.phys.AABB(minX, minY, minZ, maxX, maxY, maxZ);
        });
    }

    public static void renderItemFixed(ItemStack stack, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay, Object model) {
        if (bufferSource instanceof net.minecraft.client.renderer.MultiBufferSource mbs && model instanceof net.minecraft.client.resources.model.BakedModel bm) {
            net.minecraft.client.Minecraft.getInstance().getItemRenderer().render(
                    stack, net.minecraft.world.item.ItemDisplayContext.FIXED, stack.hasFoil(), poseStack, mbs, combinedLight, combinedOverlay, bm);
        }
    }

    public static void renderItemStatic(ItemStack stack, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay, int seed) {
        if (bufferSource instanceof net.minecraft.client.renderer.MultiBufferSource mbs) {
            net.minecraft.client.Minecraft.getInstance().getItemRenderer().renderStatic(
                    stack, net.minecraft.world.item.ItemDisplayContext.NONE, combinedLight, combinedOverlay, poseStack, mbs, null, seed);
        }
    }

    public static void playButtonClick() {
        net.minecraft.client.Minecraft.getInstance().getSoundManager().play(
                net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F));
    }

    public static void playNoteBlockBell() {
        net.minecraft.client.Minecraft.getInstance().getSoundManager().play(
                net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(SoundEvents.NOTE_BLOCK_BELL.value(), 1.0F));
    }

    public static void playNoteBlockDidgeridoo() {
        net.minecraft.client.Minecraft.getInstance().getSoundManager().play(
                net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(SoundEvents.NOTE_BLOCK_DIDGERIDOO.value(), 1.0F));
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
        if (bufferSource instanceof net.minecraft.client.renderer.MultiBufferSource mbs) {
            com.mojang.blaze3d.vertex.VertexConsumer consumer = mbs.getBuffer(net.minecraft.client.renderer.RenderType.lines());
            net.minecraft.client.renderer.LevelRenderer.renderLineBox(poseStack, consumer, minX, minY, minZ, maxX, maxY, maxZ, r, g, b, a);
        }
    }

    public static boolean isScreenOpen() {
        return net.minecraft.client.Minecraft.getInstance().screen != null;
    }

    public static net.minecraft.world.phys.Vec3 getCameraPosition(net.minecraft.client.Camera camera) {
        return camera != null ? camera.getPosition() : net.minecraft.world.phys.Vec3.ZERO;
    }

    public static void renderFallingIcicleBlock(net.minecraft.world.level.Level level, net.minecraft.world.level.block.state.BlockState blockState, net.minecraft.core.BlockPos blockPos, net.minecraft.core.BlockPos startPos, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource) {
        if (bufferSource instanceof net.minecraft.client.renderer.MultiBufferSource mbs) {
            net.minecraft.client.renderer.block.BlockRenderDispatcher blockRenderer = net.minecraft.client.Minecraft.getInstance().getBlockRenderer();
            net.minecraft.client.renderer.RenderType renderType = net.minecraft.client.renderer.RenderType.cutout();
            blockRenderer.getModelRenderer().tesselateBlock(
                    level,
                    blockRenderer.getBlockModel(blockState),
                    blockState,
                    blockPos,
                    poseStack,
                    mbs.getBuffer(renderType),
                    false,
                    level.random,
                    blockState.getSeed(startPos),
                    net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY
            );
        }
    }

    public static int getLightColor(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos) {
        return net.minecraft.client.renderer.LevelRenderer.getLightColor(level, pos);
    }

    private static void renderQuadWithUV121(com.mojang.blaze3d.vertex.VertexConsumer consumer, org.joml.Matrix4f pose, com.mojang.blaze3d.vertex.PoseStack.Pose lastPose, int packedLight,
                                     float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4,
                                     float u1, float v1, float u2, float v2, float nx, float ny, float nz) {
        consumer.addVertex(pose, x1, y1, z1).setColor(255, 255, 255, 255).setUv(u1, v1).setOverlay(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(lastPose, nx, ny, nz);
        consumer.addVertex(pose, x2, y2, z2).setColor(255, 255, 255, 255).setUv(u2, v1).setOverlay(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(lastPose, nx, ny, nz);
        consumer.addVertex(pose, x3, y3, z3).setColor(255, 255, 255, 255).setUv(u2, v2).setOverlay(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(lastPose, nx, ny, nz);
        consumer.addVertex(pose, x4, y4, z4).setColor(255, 255, 255, 255).setUv(u1, v2).setOverlay(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(lastPose, nx, ny, nz);
    }

    private static void renderBoxFaces121(com.mojang.blaze3d.vertex.VertexConsumer consumer, org.joml.Matrix4f pose, com.mojang.blaze3d.vertex.PoseStack.Pose lastPose, int packedLight,
                                   float x1, float y1, float z1, float x2, float y2, float z2) {
        renderQuadWithUV121(consumer, pose, lastPose, packedLight, x2, y1, z1, x1, y1, z1, x1, y2, z1, x2, y2, z1, x1, 1F - y2, x2, 1F - y1, 0, 0, -1);
        renderQuadWithUV121(consumer, pose, lastPose, packedLight, x1, y1, z2, x2, y1, z2, x2, y2, z2, x1, y2, z2, x1, 1F - y2, x2, 1F - y1, 0, 0, 1);
        renderQuadWithUV121(consumer, pose, lastPose, packedLight, x1, y1, z2, x2, y1, z2, x2, y1, z1, x1, y1, z1, x1, z1, x2, z2, 0, -1, 0);
        renderQuadWithUV121(consumer, pose, lastPose, packedLight, x1, y2, z1, x2, y2, z1, x2, y2, z2, x1, y2, z2, x1, z1, x2, z2, 0, 1, 0);
        renderQuadWithUV121(consumer, pose, lastPose, packedLight, x1, y1, z1, x1, y1, z2, x1, y2, z2, x1, y2, z1, z1, 1F - y2, z2, 1F - y1, -1, 0, 0);
        renderQuadWithUV121(consumer, pose, lastPose, packedLight, x2, y1, z2, x2, y1, z1, x2, y2, z1, x2, y2, z2, z1, 1F - y2, z2, 1F - y1, 1, 0, 0);
    }

    public static void renderColoredFrame(com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int packedLight, Object backTexture, boolean hasMap) {
        if (!(bufferSource instanceof net.minecraft.client.renderer.MultiBufferSource buffer && backTexture instanceof net.minecraft.resources.ResourceLocation resLoc)) return;
        poseStack.pushPose();
        poseStack.translate(-0.5D, -0.5D, -0.5D);

        org.joml.Matrix4f pose = poseStack.last().pose();
        com.mojang.blaze3d.vertex.PoseStack.Pose lastPose = poseStack.last();

        float backZ1 = hasMap ? 15.001F / 16F : 15.5F / 16F;
        float backZ2 = 1.0F;
        float frameZ1 = hasMap ? 15.001F / 16F : 15F / 16F;
        float frameZ2 = 1.0F;

        com.mojang.blaze3d.vertex.VertexConsumer backConsumer = buffer.getBuffer(net.minecraft.client.renderer.RenderType.entityCutoutNoCull(resLoc));

        float x1 = hasMap ? 1F / 16F : 3F / 16F;
        float x2 = hasMap ? 15F / 16F : 13F / 16F;
        float y1 = hasMap ? 1F / 16F : 3F / 16F;
        float y2 = hasMap ? 15F / 16F : 13F / 16F;

        renderQuadWithUV121(backConsumer, pose, lastPose, packedLight, x1, y1, backZ1, x2, y1, backZ1, x2, y2, backZ1, x1, y2, backZ1, x1, y2, x2, y1, 0, 0, -1);
        renderQuadWithUV121(backConsumer, pose, lastPose, packedLight, x2, y1, backZ2, x1, y1, backZ2, x1, y2, backZ2, x2, y2, backZ2, x1, y2, x2, y1, 0, 0, 1);

        com.mojang.blaze3d.vertex.VertexConsumer frameConsumer = buffer.getBuffer(net.minecraft.client.renderer.RenderType.entityCutoutNoCull(BIRCH_PLANKS_121));
        if (hasMap) {
            renderBoxFaces121(frameConsumer, pose, lastPose, packedLight, 1F / 16F, 0F, frameZ1, 15F / 16F, 1F / 16F, frameZ2);
            renderBoxFaces121(frameConsumer, pose, lastPose, packedLight, 1F / 16F, 15F / 16F, frameZ1, 15F / 16F, 1F, frameZ2);
            renderBoxFaces121(frameConsumer, pose, lastPose, packedLight, 0F, 0F, frameZ1, 1F / 16F, 1F, frameZ2);
            renderBoxFaces121(frameConsumer, pose, lastPose, packedLight, 15F / 16F, 0F, frameZ1, 1F, 1F, frameZ2);
        } else {
            renderBoxFaces121(frameConsumer, pose, lastPose, packedLight, 3F / 16F, 2F / 16F, frameZ1, 13F / 16F, 3F / 16F, frameZ2);
            renderBoxFaces121(frameConsumer, pose, lastPose, packedLight, 3F / 16F, 13F / 16F, frameZ1, 13F / 16F, 14F / 16F, frameZ2);
            renderBoxFaces121(frameConsumer, pose, lastPose, packedLight, 2F / 16F, 2F / 16F, frameZ1, 3F / 16F, 14F / 16F, frameZ2);
            renderBoxFaces121(frameConsumer, pose, lastPose, packedLight, 13F / 16F, 2F / 16F, frameZ1, 14F / 16F, 14F / 16F, frameZ2);
        }
        poseStack.popPose();
    }

    public static void renderColoredFrameItem(com.kingodogo.buildscape.entity.ColoredItemFrameEntity entity, ItemStack itemStack, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int packedLight, boolean isMap, boolean isInvisible) {
        if (!(bufferSource instanceof net.minecraft.client.renderer.MultiBufferSource buffer)) return;
        net.minecraft.world.level.saveddata.maps.MapItemSavedData mapData = isMap ? net.minecraft.world.item.MapItem.getSavedData(itemStack, ((net.minecraft.world.entity.Entity) entity).level()) : null;
        poseStack.translate(0.0D, 0.0D, isInvisible ? 0.5D : 0.4375D);

        int rotation = (mapData != null) ? (entity.getRotation() % 4 * 2) : entity.getRotation();
        rotateZ(poseStack, (float) rotation * 360.0F / 8.0F);

        if (mapData != null) {
            rotateZ(poseStack, 180.0F);
            poseStack.scale(0.0078125F, 0.0078125F, 0.0078125F);
            poseStack.translate(-64.0D, -64.0D, 0.0D);
            poseStack.translate(0.0D, 0.0D, -1.0D);
            net.minecraft.world.level.saveddata.maps.MapId mapId = itemStack.get(net.minecraft.core.component.DataComponents.MAP_ID);
            if (mapId != null) {
                net.minecraft.client.Minecraft.getInstance().gameRenderer.getMapRenderer().render(poseStack, buffer, mapId, mapData, true, packedLight);
            }
        } else {
            poseStack.scale(0.5F, 0.5F, 0.5F);
            net.minecraft.client.Minecraft.getInstance().getItemRenderer().renderStatic(
                    itemStack,
                    net.minecraft.world.item.ItemDisplayContext.FIXED,
                    packedLight,
                    net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,
                    poseStack,
                    buffer,
                    ((net.minecraft.world.entity.Entity) entity).level(),
                    entity.getId()
            );
        }
    }

    public static void renderStockingQuad(com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int packedLight, Object texture, boolean flipped) {
        if (!(bufferSource instanceof net.minecraft.client.renderer.MultiBufferSource buffer && texture instanceof net.minecraft.resources.ResourceLocation resLoc)) return;
        com.mojang.blaze3d.vertex.VertexConsumer vertexConsumer = buffer.getBuffer(net.minecraft.client.renderer.RenderType.entityCutoutNoCull(resLoc));
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

    public static void renderBlockModel(net.minecraft.world.level.block.state.BlockState blockState, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int light, int overlay) {
        if (!(bufferSource instanceof net.minecraft.client.renderer.MultiBufferSource buffer)) return;
        net.minecraft.client.renderer.block.BlockRenderDispatcher dispatcher = net.minecraft.client.Minecraft.getInstance().getBlockRenderer();
        net.minecraft.client.resources.model.BakedModel model = dispatcher.getBlockModel(blockState);
        net.minecraft.client.renderer.RenderType renderType = net.minecraft.client.renderer.ItemBlockRenderTypes.getRenderType(blockState, false);
        dispatcher.getModelRenderer().renderModel(poseStack.last(), buffer.getBuffer(renderType), blockState, model, 1.0F, 1.0F, 1.0F, light, overlay);
    }

    public static void renderBlockModelWithTint(net.minecraft.world.level.block.state.BlockState state, net.minecraft.core.BlockPos pos, net.minecraft.world.level.Level level, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int light, int overlay) {
        if (!(bufferSource instanceof net.minecraft.client.renderer.MultiBufferSource buffer)) return;
        net.minecraft.client.renderer.block.BlockRenderDispatcher dispatcher = net.minecraft.client.Minecraft.getInstance().getBlockRenderer();
        net.minecraft.client.resources.model.BakedModel model = dispatcher.getBlockModel(state);
        net.minecraft.client.renderer.RenderType renderType = net.minecraft.client.renderer.ItemBlockRenderTypes.getRenderType(state, false);
        float r = 1.0F, g = 1.0F, b = 1.0F;
        int color = (level != null && pos != null)
                ? net.minecraft.client.Minecraft.getInstance().getBlockColors().getColor(state, level, pos, 0)
                : net.minecraft.client.Minecraft.getInstance().getBlockColors().getColor(state, null, null, 0);
        if (color != -1) {
            r = ((color >> 16) & 0xFF) / 255.0F;
            g = ((color >> 8) & 0xFF) / 255.0F;
            b = (color & 0xFF) / 255.0F;
        }
        dispatcher.getModelRenderer().renderModel(poseStack.last(), buffer.getBuffer(renderType), state, model, r, g, b, light, overlay);
    }

    public static void renderJarFluid(com.kingodogo.buildscape.block.GlassJarBlockEntity blockEntity, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int light, int overlay) {
        if (!(bufferSource instanceof net.minecraft.client.renderer.MultiBufferSource mbs)) return;
        ItemStack liquidItem = blockEntity.getStoredLiquidItem();
        int level = blockEntity.getLiquidLevel();
        if (liquidItem == null || liquidItem.isEmpty() || level <= 0) return;

        net.minecraft.resources.ResourceLocation textureLoc = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_still");
        int color = 0xFF3F76E4;

        if (com.kingodogo.buildscape.block.GlassJarBlockEntity.isXpLiquid(liquidItem)) {
            color = 0xFFFFFFFF;
            textureLoc = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("buildscape", "fluid/experience_flow");
        } else if (liquidItem.getItem() instanceof net.minecraft.world.item.PotionItem) {
            net.minecraft.world.item.alchemy.PotionContents contents = liquidItem.get(DataComponents.POTION_CONTENTS);
            if (contents != null) {
                color = contents.getColor();
            }
            if ((color & 0xFF000000) == 0) color |= 0xFF000000;
        } else if (liquidItem.getItem() instanceof net.minecraft.world.item.HoneyBottleItem) {
            color = 0xFFFF9600;
            textureLoc = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("minecraft", "block/honey_block_top");
        } else if (liquidItem.is(net.minecraft.world.item.Items.MILK_BUCKET)) {
            color = 0xFFFFFFFF;
            textureLoc = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("minecraft", "block/white_concrete");
        } else if (liquidItem.is(net.minecraft.world.item.Items.LAVA_BUCKET)) {
            color = 0xFFFFFFFF;
            textureLoc = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("minecraft", "block/lava_still");
        } else if (liquidItem.getItem() instanceof net.minecraft.world.item.BucketItem) {
            net.minecraft.world.level.material.Fluid fluid = Services.PLATFORM.getBucketFluid(liquidItem.getItem());
            if (fluid == net.minecraft.world.level.material.Fluids.LAVA || fluid == net.minecraft.world.level.material.Fluids.FLOWING_LAVA) {
                color = 0xFFFFFFFF;
                textureLoc = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("minecraft", "block/lava_still");
            } else {
                color = 0xFF3F76E4;
                textureLoc = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_still");
            }
        }

        net.minecraft.client.renderer.texture.TextureAtlasSprite sprite = net.minecraft.client.Minecraft.getInstance().getTextureAtlas(net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS).apply(textureLoc);
        com.mojang.blaze3d.vertex.VertexConsumer vc = mbs.getBuffer(net.minecraft.client.renderer.RenderType.translucent());

        int maxLevel = com.kingodogo.buildscape.block.GlassJarBlockEntity.isXpLiquid(liquidItem) ? com.kingodogo.buildscape.block.GlassJarBlockEntity.XP_BOTTLE_MAX : 16;
        float fillRatio = Math.min(maxLevel, level) / (float) maxLevel;
        float y1 = 0.07F;
        float y2 = y1 + (fillRatio * 0.65F);
        float x1 = 0.27F, x2 = 0.73F, z1 = 0.27F, z2 = 0.73F;

        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        float a = ((color >> 24) & 0xFF) / 255.0F;
        if (a == 0.0F) a = 0.88F;

        float u0 = sprite.getU0(), u1 = sprite.getU1(), v0 = sprite.getV0(), v1 = sprite.getV1();
        org.joml.Matrix4f matrix = poseStack.last().pose();
        com.mojang.blaze3d.vertex.PoseStack.Pose lastPose = poseStack.last();

        vc.addVertex(matrix, x1, y2, z1).setColor(r, g, b, a).setUv(u0, v0).setOverlay(overlay).setLight(light).setNormal(lastPose, 0, 1, 0);
        vc.addVertex(matrix, x1, y2, z2).setColor(r, g, b, a).setUv(u0, v1).setOverlay(overlay).setLight(light).setNormal(lastPose, 0, 1, 0);
        vc.addVertex(matrix, x2, y2, z2).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(lastPose, 0, 1, 0);
        vc.addVertex(matrix, x2, y2, z1).setColor(r, g, b, a).setUv(u1, v0).setOverlay(overlay).setLight(light).setNormal(lastPose, 0, 1, 0);

        vc.addVertex(matrix, x1, y2, z1).setColor(r, g, b, a).setUv(u0, v0).setOverlay(overlay).setLight(light).setNormal(lastPose, 0, 0, -1);
        vc.addVertex(matrix, x2, y2, z1).setColor(r, g, b, a).setUv(u1, v0).setOverlay(overlay).setLight(light).setNormal(lastPose, 0, 0, -1);
        vc.addVertex(matrix, x2, y1, z1).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(lastPose, 0, 0, -1);
        vc.addVertex(matrix, x1, y1, z1).setColor(r, g, b, a).setUv(u0, v1).setOverlay(overlay).setLight(light).setNormal(lastPose, 0, 0, -1);

        vc.addVertex(matrix, x1, y1, z2).setColor(r, g, b, a).setUv(u0, v1).setOverlay(overlay).setLight(light).setNormal(lastPose, 0, 0, 1);
        vc.addVertex(matrix, x2, y1, z2).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(lastPose, 0, 0, 1);
        vc.addVertex(matrix, x2, y2, z2).setColor(r, g, b, a).setUv(u1, v0).setOverlay(overlay).setLight(light).setNormal(lastPose, 0, 0, 1);
        vc.addVertex(matrix, x1, y2, z2).setColor(r, g, b, a).setUv(u0, v0).setOverlay(overlay).setLight(light).setNormal(lastPose, 0, 0, 1);

        vc.addVertex(matrix, x1, y1, z1).setColor(r, g, b, a).setUv(u0, v1).setOverlay(overlay).setLight(light).setNormal(lastPose, -1, 0, 0);
        vc.addVertex(matrix, x1, y1, z2).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(lastPose, -1, 0, 0);
        vc.addVertex(matrix, x1, y2, z2).setColor(r, g, b, a).setUv(u1, v0).setOverlay(overlay).setLight(light).setNormal(lastPose, -1, 0, 0);
        vc.addVertex(matrix, x1, y2, z1).setColor(r, g, b, a).setUv(u0, v0).setOverlay(overlay).setLight(light).setNormal(lastPose, -1, 0, 0);

        vc.addVertex(matrix, x2, y2, z1).setColor(r, g, b, a).setUv(u0, v0).setOverlay(overlay).setLight(light).setNormal(lastPose, 1, 0, 0);
        vc.addVertex(matrix, x2, y2, z2).setColor(r, g, b, a).setUv(u1, v0).setOverlay(overlay).setLight(light).setNormal(lastPose, 1, 0, 0);
        vc.addVertex(matrix, x2, y1, z2).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay).setLight(light).setNormal(lastPose, 1, 0, 0);
        vc.addVertex(matrix, x2, y1, z1).setColor(r, g, b, a).setUv(u0, v1).setOverlay(overlay).setLight(light).setNormal(lastPose, 1, 0, 0);
    }

    public static void renderGlassJar(com.kingodogo.buildscape.block.GlassJarBlockEntity blockEntity, float partialTicks, com.mojang.blaze3d.vertex.PoseStack poseStack, Object bufferSource, int combinedLight, int combinedOverlay) {
        if (blockEntity == null || !(bufferSource instanceof net.minecraft.client.renderer.MultiBufferSource mbs)) return;

        Level level = blockEntity.getLevel();
        BlockPos pos = blockEntity.getBlockPos();
        BlockState blockState = blockEntity.getBlockState();
        int light = (level != null) ? net.minecraft.client.renderer.LevelRenderer.getLightColor(level, pos) : combinedLight;

        long currentTick = (level != null) ? level.getGameTime() : 0;
        long wobbleStartTick = blockEntity.getWobbleStartedAtTick();

        float wobbleProgress = 0.0F;
        float wobbleDuration = 10.0F;
        if (wobbleStartTick > 0) {
            float ticksSinceWobble = (float) (currentTick - wobbleStartTick) + partialTicks;
            if (ticksSinceWobble < wobbleDuration) {
                wobbleProgress = ticksSinceWobble / wobbleDuration;
            }
        }

        poseStack.pushPose();

        if (wobbleProgress > 0.0F && wobbleProgress < 1.0F) {
            float dampening = 1.0F - wobbleProgress;
            float oscillation = (float) Math.sin(wobbleProgress * Math.PI * 6);
            float rotationAngle = 8.0F * dampening * oscillation;

            poseStack.translate(0.5D, 0.0D, 0.5D);
            poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(rotationAngle));
            poseStack.translate(-0.5D, 0.0D, -0.5D);
        }

        if (blockEntity.hasLiquid()) {
            renderJarFluid(blockEntity, poseStack, mbs, light, combinedOverlay);
        } else if (!blockEntity.isEmpty()) {
            ItemStack storedItem = blockEntity.getStoredItem();
            if (storedItem != null && !storedItem.isEmpty()) {
                int count = storedItem.getCount();
                int renderCount = Math.min(32, count);

                float startY = 0.07F;
                float yStep = 0.019F;
                net.minecraft.client.renderer.entity.ItemRenderer itemRenderer = net.minecraft.client.Minecraft.getInstance().getItemRenderer();
                net.minecraft.client.resources.model.BakedModel itemModel = itemRenderer.getModel(storedItem, level, null, 0);

                for (int i = 0; i < renderCount; i++) {
                    poseStack.pushPose();

                    long seed = (pos.getX() * 3129871L) ^ (pos.getZ() * 116129781L) ^ (pos.getY() * 9999991L) + i * 10007L;
                    long h1 = (seed ^ (seed >>> 16)) * 0x45d9f3bL;
                    h1 = (h1 ^ (h1 >>> 16)) * 0x45d9f3bL;
                    h1 = h1 ^ (h1 >>> 16);
                    float randomAngle = (float) (Math.abs(h1 % 360));

                    long seedX = seed + 17L;
                    long hx = (seedX ^ (seedX >>> 16)) * 0x45d9f3bL;
                    hx = (hx ^ (hx >>> 16)) * 0x45d9f3bL;
                    hx = hx ^ (hx >>> 16);
                    float offsetX = (((hx % 100) - 50) / 50.0F) * 0.025F;

                    long seedZ = seed + 34L;
                    long hz = (seedZ ^ (seedZ >>> 16)) * 0x45d9f3bL;
                    hz = (hz ^ (hz >>> 16)) * 0x45d9f3bL;
                    hz = hz ^ (hz >>> 16);
                    float offsetZ = (((hz % 100) - 50) / 50.0F) * 0.025F;

                    float currentY = startY + (i * yStep);

                    poseStack.translate(0.5D + offsetX, currentY, 0.5D + offsetZ);
                    poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(randomAngle));
                    poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90.0F));
                    poseStack.scale(0.42F, 0.42F, 0.42F);

                    itemRenderer.render(storedItem, net.minecraft.world.item.ItemDisplayContext.FIXED, false, poseStack, mbs, light, combinedOverlay, itemModel);
                    poseStack.popPose();
                }
            }
        }

        renderBlockModel(blockState, poseStack, mbs, light, combinedOverlay);
        poseStack.popPose();
    }

    public static boolean isTranslucent(net.minecraft.world.level.block.state.BlockState state) {
        try {
            return net.minecraft.client.renderer.ItemBlockRenderTypes.getChunkRenderType(state)
                    == net.minecraft.client.renderer.RenderType.translucent();
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static BlockColorSample sampleBlockColor(Map<String, double[]> v121xSpriteCache, java.util.function.Function<net.minecraft.client.renderer.texture.SpriteContents, com.mojang.blaze3d.platform.NativeImage> spriteImage, net.minecraft.world.level.block.state.BlockState state) {
        if (state == null) return null;
        try {
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            net.minecraft.client.resources.model.BakedModel model = mc.getBlockRenderer().getBlockModel(state);
            net.minecraft.client.color.block.BlockColors blockColors = mc.getBlockColors();
            double red = 0, green = 0, blue = 0;
            int weight = 0;
            boolean transparent = false;
            java.util.Set<String> textures = new java.util.HashSet<>();
            for (net.minecraft.core.Direction side : new net.minecraft.core.Direction[]{
                    net.minecraft.core.Direction.DOWN, net.minecraft.core.Direction.UP,
                    net.minecraft.core.Direction.NORTH, net.minecraft.core.Direction.SOUTH,
                    net.minecraft.core.Direction.WEST, net.minecraft.core.Direction.EAST, null}) {
                java.util.List<net.minecraft.client.renderer.block.model.BakedQuad> quads =
                        model.getQuads(state, side, net.minecraft.util.RandomSource.create(42L));
                for (net.minecraft.client.renderer.block.model.BakedQuad quad : quads) {
                    net.minecraft.client.renderer.texture.TextureAtlasSprite sprite = quad.getSprite();
                    String spriteName = sprite.contents().name().toString();
                    if (spriteName.contains("missingno")) continue;
                    textures.add(spriteName);
                    int tint = 0xFFFFFF;
                    if (quad.isTinted()) {
                        int resolved = blockColors.getColor(state, null, null, quad.getTintIndex());
                        if (resolved != -1) tint = resolved & 0xFFFFFF;
                    }
                    int resolvedTint = tint;
                    double[] sample = v121xSpriteCache.computeIfAbsent(spriteName + "#" + tint, key -> {
                        com.mojang.blaze3d.platform.NativeImage image = spriteImage.apply(sprite.contents());
                        int width = sprite.contents().width(), height = sprite.contents().height();
                        int stepX = Math.max(1, (width + COLOR_MAX_SAMPLES - 1) / COLOR_MAX_SAMPLES);
                        int stepY = Math.max(1, (height + COLOR_MAX_SAMPLES - 1) / COLOR_MAX_SAMPLES);
                        double r = 0, g = 0, b = 0, alphaWeight = 0;
                        boolean translucent = false;
                        for (int y = 0; y < height; y += stepY) for (int x = 0; x < width; x += stepX) {
                            int alpha = image.getLuminanceOrAlpha(x, y) & 255;
                            if (alpha < 250) translucent = true;
                            if (alpha < 16) continue;
                            double aw = alpha / 255.0;
                            r += srgbToLinearColor(((image.getRedOrLuminance(x, y) & 255)
                                    * ((resolvedTint >> 16) & 255) / 255) / 255.0) * aw;
                            g += srgbToLinearColor(((image.getGreenOrLuminance(x, y) & 255)
                                    * ((resolvedTint >> 8) & 255) / 255) / 255.0) * aw;
                            b += srgbToLinearColor(((image.getBlueOrLuminance(x, y) & 255)
                                    * (resolvedTint & 255) / 255) / 255.0) * aw;
                            alphaWeight += aw;
                        }
                        return alphaWeight == 0 ? new double[]{0, 0, 0, 0, translucent ? 1 : 0}
                                : new double[]{r / alphaWeight, g / alphaWeight, b / alphaWeight, 1, translucent ? 1 : 0};
                    });
                    if (sample[3] > 0) {
                        red += sample[0]; green += sample[1]; blue += sample[2];
                        transparent |= sample[4] > 0.5;
                        weight++;
                    }
                }
            }
            if (weight == 0) return null;
            return new BlockColorSample((linearToSrgbColor(red / weight) << 16)
                    | (linearToSrgbColor(green / weight) << 8) | linearToSrgbColor(blue / weight),
                    transparent, textures.size() == 1);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static double srgbToLinearColor(double value) {
        return value <= 0.04045 ? value / 12.92 : Math.pow((value + 0.055) / 1.055, 2.4);
    }

    private static int linearToSrgbColor(double value) {
        double clamped = Math.max(0, Math.min(1, value));
        double srgb = clamped <= 0.0031308 ? clamped * 12.92 : 1.055 * Math.pow(clamped, 1.0 / 2.4) - 0.055;
        return Math.max(0, Math.min(255, (int) Math.round(srgb * 255)));
    }

    public static net.minecraft.client.gui.screens.Screen createConfigScreen(net.minecraft.client.gui.screens.Screen parent) {
        return com.kingodogo.buildscape.adapter.v121x.client.GuiProvider.createConfigScreen(parent);
    }

    public static net.minecraft.client.gui.components.Button createButton(int x, int y, int width, int height, net.minecraft.network.chat.Component message, net.minecraft.client.gui.components.Button.OnPress onPress) {
        return com.kingodogo.buildscape.adapter.v121x.client.GuiProvider.createButton(x, y, width, height, message, onPress);
    }

    public static net.minecraft.client.gui.components.Button createCustomButton(int x, int y, int width, int height, net.minecraft.network.chat.Component message, net.minecraft.client.gui.components.Button.OnPress onPress, com.kingodogo.buildscape.client.screen.widget.CustomButtonRenderer renderer) {
        return com.kingodogo.buildscape.adapter.v121x.client.GuiProvider.createCustomButton(x, y, width, height, message, onPress, renderer);
    }

    public static net.minecraft.client.gui.screens.Screen wrapScreen(net.minecraft.network.chat.Component title, net.minecraft.client.gui.screens.Screen parent, com.kingodogo.buildscape.client.screen.IScreenDelegate delegate) {
        return com.kingodogo.buildscape.adapter.v121x.client.GuiProvider.wrapScreen(title, parent, delegate);
    }

    public static void renderGuiItemDecorations(Object poseStackOrGraphics, net.minecraft.client.gui.Font font, net.minecraft.world.item.ItemStack stack, int x, int y) {
        com.kingodogo.buildscape.adapter.v121x.client.GuiProvider.renderGuiItemDecorations(poseStackOrGraphics, font, stack, x, y);
    }

    public static net.minecraft.client.gui.components.AbstractWidget wrapCustomWidget(int x, int y, int width, int height, net.minecraft.network.chat.Component message, com.kingodogo.buildscape.client.screen.widget.ICustomWidget customWidget) {
        return com.kingodogo.buildscape.adapter.v121x.client.GuiProvider.wrapCustomWidget(x, y, width, height, message, customWidget);
    }

}

