package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.block.MirrorBlock;
import com.kingodogo.buildscape.block.MirrorBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class MirrorBlockEntityRenderer implements BlockEntityRenderer<MirrorBlockEntity> {
    private static final ResourceLocation WHITE_CONCRETE = new ResourceLocation("minecraft", "textures/block/white_concrete.png");

    public MirrorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(MirrorBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        Minecraft mc = Minecraft.getInstance();
        BlockState state = blockEntity.getBlockState();
        if (!(state.getBlock() instanceof MirrorBlock)) return;
        Direction facing = state.getValue(MirrorBlock.FACING);

        // Register this mirror for the next frame's world capture pass
        MirrorRenderer.registerMirror(blockEntity);

        poseStack.pushPose();
        
        // 1. Center in the block
        poseStack.translate(0.5, 0.5, 0.5);
        
        // 2. Rotate to face outwards
        float rotation = switch (facing) {
            case NORTH -> 0;
            case SOUTH -> 180;
            case WEST -> 90;
            case EAST -> 270;
            default -> 0;
        };
        poseStack.mulPose(Vector3f.YP.rotationDegrees(rotation));
        
        // 3. CRITICAL: Translate TO THE FRONT of the block model
        // The block face is at local -0.5. To be visible and avoid Z-fighting, 
        // we MUST be at exactly -0.501 or slightly more. 
        // Logic: -0.5 is edge, -0.505 is 0.005 blocks in front of the surface.
        poseStack.translate(0, 0, -0.505); 

        // 4. Render the Foundation (White Concrete backing)
        renderBacking(poseStack, bufferSource);

        // 5. Render the Real-Time Mirror Portal (Perspective Projection)
        renderPortal(poseStack, bufferSource);

        poseStack.popPose();
    }

    private void renderBacking(PoseStack poseStack, MultiBufferSource bufferSource) {
        RenderType t = MirrorRenderer.getBackingRenderType(WHITE_CONCRETE);
        VertexConsumer vc = bufferSource.getBuffer(t);
        Matrix4f pose = poseStack.last().pose();
        float s = 0.5f; // Full 16x16 face for seamless wall connection

        // Drawing at Z=0 relative to our -0.505 offset
        vc.vertex(pose, -s, -s, 0).color(1f, 1f, 1f, 1f).uv(0, 1).uv2(15728880).endVertex();
        vc.vertex(pose, s, -s, 0).color(1f, 1f, 1f, 1f).uv(1, 1).uv2(15728880).endVertex();
        vc.vertex(pose, s, s, 0).color(1f, 1f, 1f, 1f).uv(1, 0).uv2(15728880).endVertex();
        vc.vertex(pose, -s, s, 0).color(1f, 1f, 1f, 1f).uv(0, 0).uv2(15728880).endVertex();
    }

    private void renderPortal(PoseStack poseStack, MultiBufferSource bufferSource) {
        RenderType reflectionType = MirrorRenderer.getMirrorSurfaceType();
        VertexConsumer vc = bufferSource.getBuffer(reflectionType);
        Matrix4f pose = poseStack.last().pose();
        float s = 0.5f;

        // Positioned 0.001 units IN FRONT of the backing (Z = -0.001 relative to -0.505)
        // Horizontal UV flip (1 to 0) for physically correct mirror projection
        vc.vertex(pose, -s, -s, 0.001f).color(1f, 1f, 1f, 1.0f).uv(1, 1).uv2(15728880).endVertex();
        vc.vertex(pose, s, -s, 0.001f).color(1f, 1f, 1f, 1.0f).uv(0, 1).uv2(15728880).endVertex();
        vc.vertex(pose, s, s, 0.001f).color(1f, 1f, 1f, 1.0f).uv(0, 0).uv2(15728880).endVertex();
        vc.vertex(pose, -s, s, 0.001f).color(1f, 1f, 1f, 1.0f).uv(1, 0).uv2(15728880).endVertex();
    }
}
