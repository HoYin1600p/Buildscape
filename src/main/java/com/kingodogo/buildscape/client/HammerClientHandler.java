package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.item.HammerItem;
import com.kingodogo.buildscape.network.HammerReplacePacket;
import com.kingodogo.buildscape.network.ModMessages;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawSelectionEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(
        modid = BuildScape.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public class HammerClientHandler {

    @SubscribeEvent
    public static void onHighlightBlock(DrawSelectionEvent.HighlightBlock event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null) return;

        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.isEmpty() || !(mainHand.getItem() instanceof HammerItem hammer)) return;

        // Must have a block in offhand
        ItemStack offHand = player.getOffhandItem();
        if (offHand.isEmpty() || !(offHand.getItem() instanceof BlockItem)) return;

        HitResult target = event.getTarget();
        if (target.getType() != HitResult.Type.BLOCK) return;

        BlockPos pos = ((BlockHitResult) target).getBlockPos();
        BlockState state = mc.level.getBlockState(pos);
        if (state.isAir()) return;

        // Cancel default outline box
        event.setCanceled(true);

        PoseStack poseStack = event.getPoseStack();
        Camera camera = event.getCamera();
        Vec3 cameraPos = camera.getPosition();

        poseStack.pushPose();
        poseStack.translate(pos.getX() - cameraPos.x, pos.getY() - cameraPos.y, pos.getZ() - cameraPos.z);

        VoxelShape shape = state.getShape(mc.level, pos, CollisionContext.of(player));
        if (!shape.isEmpty()) {
            MultiBufferSource bufferSource = event.getMultiBufferSource();
            AABB bounds = shape.bounds();

            // Pick color based on hammer tier
            float[] color = getGlowColor(hammer.getHammerTier());
            renderGlowingOutline(poseStack, bufferSource, bounds, color[0], color[1], color[2]);
        }

        poseStack.popPose();
    }

    private static float[] getGlowColor(HammerItem.HammerTier tier) {
        return switch (tier) {
            case IRON -> new float[]{0.75f, 0.75f, 0.8f};       // Steel silver
            case DIAMOND -> new float[]{0.3f, 0.9f, 0.95f};     // Cyan diamond
            case NETHERITE -> new float[]{0.6f, 0.2f, 0.2f};    // Dark red
        };
    }

    private static void renderGlowingOutline(PoseStack poseStack, MultiBufferSource bufferSource, AABB box, float r, float g, float b) {
        VertexConsumer lines = bufferSource.getBuffer(RenderType.lines());

        // Base bounding box
        LevelRenderer.renderLineBox(
                poseStack, lines,
                box.minX, box.minY, box.minZ,
                box.maxX, box.maxY, box.maxZ,
                r, g, b, 1.0f
        );

        // Slightly inflated outer glow for vibrant visual feedback
        AABB inflated = box.inflate(0.003);
        LevelRenderer.renderLineBox(
                poseStack, lines,
                inflated.minX, inflated.minY, inflated.minZ,
                inflated.maxX, inflated.maxY, inflated.maxZ,
                r, g, b, 0.5f
        );
    }

    @SubscribeEvent
    public static void onMouseInput(InputEvent.MouseInputEvent event) {
        // Right-click (button 1) to replace block
        if (event.getButton() != GLFW.GLFW_MOUSE_BUTTON_RIGHT) return;
        if (event.getAction() != GLFW.GLFW_PRESS) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null || mc.player == null || mc.level == null) return;

        LocalPlayer player = mc.player;

        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.isEmpty() || !(mainHand.getItem() instanceof HammerItem)) return;

        // Must have a block in offhand
        ItemStack offHand = player.getOffhandItem();
        if (offHand.isEmpty() || !(offHand.getItem() instanceof BlockItem)) return;

        HitResult hit = mc.hitResult;
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) return;

        BlockHitResult blockHit = (BlockHitResult) hit;
        BlockPos pos = blockHit.getBlockPos();

        ModMessages.INSTANCE.sendToServer(new HammerReplacePacket(pos));
    }
}
