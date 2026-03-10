package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.block.MirrorBlock;
import com.kingodogo.buildscape.block.MirrorBlockEntity;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;

import java.util.*;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MirrorRenderer extends RenderStateShard {

    private static final List<MirrorBlockEntity> registeredMirrors = new ArrayList<>();
    private static boolean isRenderingReflection = false;
    private static RenderTarget mirrorFBO;

    public MirrorRenderer(String name, Runnable setup, Runnable clear) {
        super(name, setup, clear);
    }

    public static void registerMirror(MirrorBlockEntity mirror) {
        if (!registeredMirrors.contains(mirror)) registeredMirrors.add(mirror);
    }

    // ---------------------------------------------------------------
    // The RenderType used to blit the FBO onto the mirror face
    // This is called during BER.render() — the FBO texture must be
    // updated BEFORE this is drawn. Because BER happens first and
    // RenderLevelLastEvent happens after, we always show the PREVIOUS
    // frame. That's acceptable (1 frame lag = imperceptible at 60fps).
    // ---------------------------------------------------------------
    public static RenderType getMirrorSurfaceType() {
        ensureFBO();
        final int fboId = mirrorFBO.getColorTextureId();

        return RenderType.create("mirror_surface",
                DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
                VertexFormat.Mode.QUADS,
                256, false, false,
                RenderType.CompositeState.builder()
                        .setShaderState(new RenderType.ShaderStateShard(GameRenderer::getPositionColorTexLightmapShader))
                        .setTextureState(new RenderType.TextureStateShard(
                                new ResourceLocation("minecraft", "textures/block/white_concrete.png"), false, false) {
                            @Override
                            public void setupRenderState() {
                                // Always bind the live FBO texture when drawing the mirror quad
                                RenderSystem.setShaderTexture(0, mirrorFBO.getColorTextureId());
                            }
                        })
                        .setTransparencyState(NO_TRANSPARENCY)
                        .setDepthTestState(LEQUAL_DEPTH_TEST)
                        .setWriteMaskState(COLOR_DEPTH_WRITE)
                        .setLightmapState(NO_LIGHTMAP)
                        .createCompositeState(false));
    }

    public static RenderType getBackingRenderType(ResourceLocation tex) {
        return RenderType.create("mirror_backing",
                DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
                VertexFormat.Mode.QUADS,
                256, false, false,
                RenderType.CompositeState.builder()
                        .setShaderState(new RenderType.ShaderStateShard(GameRenderer::getPositionColorTexLightmapShader))
                        .setTextureState(new RenderType.TextureStateShard(tex, false, false))
                        .setTransparencyState(NO_TRANSPARENCY)
                        .setLightmapState(LIGHTMAP)
                        .createCompositeState(false));
    }

    // ---------------------------------------------------------------
    // Main event: capture scene into FBO after main world render
    // ---------------------------------------------------------------
    @SubscribeEvent
    public static void onRenderWorldLast(RenderLevelLastEvent event) {
        if (isRenderingReflection || registeredMirrors.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        // Pick closest mirror
        Vec3 eye = mc.gameRenderer.getMainCamera().getPosition();
        MirrorBlockEntity target = null;
        double best = Double.MAX_VALUE;
        for (MirrorBlockEntity be : registeredMirrors) {
            double d = Vec3.atCenterOf(be.getBlockPos()).distanceToSqr(eye);
            if (d < best) { best = d; target = be; }
        }
        registeredMirrors.clear();
        if (target == null || best > 400) return;

        captureReflection(target, event.getPartialTick());
    }

    // ---------------------------------------------------------------
    // Core capture: render scene from reflected POV into mirrorFBO
    // ---------------------------------------------------------------
    private static void captureReflection(MirrorBlockEntity mirror, float pt) {
        Minecraft mc = Minecraft.getInstance();
        ensureFBO();

        isRenderingReflection = true;
        RenderTarget prevFBO = mc.getMainRenderTarget();

        // ---- 1. Mirror geometry ----
        BlockState st = mirror.getBlockState();
        Direction facing = st.getValue(MirrorBlock.FACING);
        Vec3 planeNormal = Vec3.atLowerCornerOf(facing.getNormal());
        Vec3 planeCentre = Vec3.atCenterOf(mirror.getBlockPos()).add(planeNormal.scale(0.5));

        // ---- 2. Reflect eye position ----
        Camera cam = mc.gameRenderer.getMainCamera();
        Vec3 eye = cam.getPosition();
        double distToPlane = eye.subtract(planeCentre).dot(planeNormal);
        Vec3 reflEye = eye.subtract(planeNormal.scale(2.0 * distToPlane));

        // ---- 3. Reflected camera orientation ----
        // Vanilla: mulPose(YP, yaw + 180), then mulPose(XP, pitch)
        // For reflection across Z-axis: yaw_r + 180 = -yaw → yaw_r = -yaw - 180
        // For reflection across X-axis: yaw_r + 180 = 180 - yaw → yaw_r = -yaw
        float yaw = cam.getYRot();
        float pitch = cam.getXRot();
        float yawRotation; // The angle fed into YP (vanilla uses cam.getYRot() + 180)
        if (facing.getAxis() == Direction.Axis.X) {
            yawRotation = 180f - yaw; // East/West mirror
        } else {
            yawRotation = -yaw;       // North/South mirror
        }

        // ---- 4. Build camera PoseStack (EXACTLY as vanilla GameRenderer does) ----
        // In Minecraft 1.18.2, the 3D world camera transform lives in the local PoseStack,
        // NOT the ModelViewStack. ModelViewStack stays identity for 3D rendering.
        PoseStack cameraPS = new PoseStack();
        cameraPS.mulPose(Vector3f.YP.rotationDegrees(yawRotation)); // Yaw first (with +180 baked in)
        cameraPS.mulPose(Vector3f.XP.rotationDegrees(pitch));        // Pitch second

        // ---- 5. Setup FBO ----
        Vec3 skyCol = mc.level.getSkyColor(eye, pt);
        mirrorFBO.setClearColor((float)skyCol.x, (float)skyCol.y, (float)skyCol.z, 1f);
        mirrorFBO.bindWrite(true);
        RenderSystem.viewport(0, 0, mirrorFBO.width, mirrorFBO.height);
        mirrorFBO.clear(Minecraft.ON_OSX);

        // Projection
        RenderSystem.setProjectionMatrix(mc.gameRenderer.getProjectionMatrix(mc.options.fov));

        // ModelViewStack = identity for 3D world rendering (vanilla convention)
        RenderSystem.getModelViewStack().pushPose();
        RenderSystem.getModelViewStack().setIdentity();
        RenderSystem.applyModelViewMatrix();

        // ---- 6. Render scene ----
        EntityRenderDispatcher erd = mc.getEntityRenderDispatcher();
        MultiBufferSource.BufferSource bufSrc = mc.renderBuffers().bufferSource();

        // Flip winding for correct mirror chirality
        GL11.glFrontFace(GL11.GL_CW);

        // 6a. Entities
        AABB searchBox = AABB.ofSize(reflEye, 64, 64, 64);
        List<Entity> entities = mc.level.getEntities(null, searchBox);
        if (mc.player != null && !entities.contains(mc.player)) entities.add(mc.player);

        for (Entity e : entities) {
            if (e == null) continue;
            Vec3 ePos = e.getPosition(pt);
            // Cull entities behind mirror plane
            if (ePos.subtract(planeCentre).dot(planeNormal) < -0.5) continue;

            // Vanilla convention: entity position as delta from camera eye
            double dx = ePos.x - reflEye.x;
            double dy = ePos.y - reflEye.y;
            double dz = ePos.z - reflEye.z;
            erd.render(e, dx, dy, dz, e.getViewYRot(pt), pt, cameraPS, bufSrc, 15728880);
        }
        bufSrc.endBatch();

        // 6b. Terrain blocks
        BlockPos center = new BlockPos(reflEye.x, reflEye.y, reflEye.z);
        int R = 10;
        for (int x = -R; x <= R; x++) {
            for (int y = -R; y <= R; y++) {
                for (int z = -R; z <= R; z++) {
                    BlockPos bp = center.offset(x, y, z);
                    BlockState bs = mc.level.getBlockState(bp);
                    if (bs.isAir() || bs.getBlock() instanceof MirrorBlock) continue;
                    if (bs.getRenderShape() == RenderShape.INVISIBLE) continue;
                    // Cull behind mirror
                    if (Vec3.atCenterOf(bp).subtract(planeCentre).dot(planeNormal) < -0.5) continue;

                    // Vanilla convention: block position as delta from camera in the PoseStack
                    cameraPS.pushPose();
                    cameraPS.translate(bp.getX() - reflEye.x, bp.getY() - reflEye.y, bp.getZ() - reflEye.z);
                    mc.getBlockRenderer().renderSingleBlock(bs, cameraPS, bufSrc, 15728880, 0);
                    cameraPS.popPose();
                }
            }
        }
        bufSrc.endBatch();

        GL11.glFrontFace(GL11.GL_CCW);

        // ---- 7. Restore ----
        RenderSystem.getModelViewStack().popPose();
        RenderSystem.applyModelViewMatrix();

        prevFBO.bindWrite(true);
        RenderSystem.viewport(0, 0, mc.getWindow().getWidth(), mc.getWindow().getHeight());
        isRenderingReflection = false;
    }

    private static void ensureFBO() {
        if (mirrorFBO == null) {
            mirrorFBO = new TextureTarget(1024, 1024, true, Minecraft.ON_OSX);
        }
    }
}
