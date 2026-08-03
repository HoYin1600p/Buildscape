package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.item.ModItems;
import com.kingodogo.buildscape.network.ModMessages;
import com.kingodogo.buildscape.network.RotateBlockPacket;
import com.kingodogo.buildscape.network.RotateBlockPacket.ArrowDirection;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
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
public class WrenchClientHandler {

    @SubscribeEvent
    public static void onHighlightBlock(DrawSelectionEvent.HighlightBlock event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null) return;

        if (!player.isCrouching()) return;

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        boolean hasWrench = (!mainHand.isEmpty() && mainHand.getItem() == ModItems.WRENCH.get()) ||
                            (!offHand.isEmpty() && offHand.getItem() == ModItems.WRENCH.get());
        if (!hasWrench) return;

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

            // Render glowing highlight box around block
            renderGlowingOutline(poseStack, bufferSource, bounds);
        }

        poseStack.popPose();
    }

    private static void renderGlowingOutline(PoseStack poseStack, MultiBufferSource bufferSource, AABB box) {
        VertexConsumer lines = bufferSource.getBuffer(RenderType.lines());

        // Bright copper glowing outline (R=1.0, G=0.6, B=0.1)
        float r = 1.0f;
        float g = 0.6f;
        float b = 0.1f;
        float a = 1.0f;

        // Base bounding box
        LevelRenderer.renderLineBox(
                poseStack, lines,
                box.minX, box.minY, box.minZ,
                box.maxX, box.maxY, box.maxZ,
                r, g, b, a
        );

        // Slightly inflated outer glow line for vibrant visual feedback
        AABB inflated = box.inflate(0.003);
        LevelRenderer.renderLineBox(
                poseStack, lines,
                inflated.minX, inflated.minY, inflated.minZ,
                inflated.maxX, inflated.maxY, inflated.maxZ,
                1.0f, 0.85f, 0.4f, 0.6f
        );
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (event.getAction() != GLFW.GLFW_PRESS) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null || mc.player == null || mc.level == null) return;

        LocalPlayer player = mc.player;
        if (!player.isCrouching()) return;

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        boolean hasWrench = (!mainHand.isEmpty() && mainHand.getItem() == ModItems.WRENCH.get()) ||
                            (!offHand.isEmpty() && offHand.getItem() == ModItems.WRENCH.get());
        if (!hasWrench) return;

        HitResult hit = mc.hitResult;
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) return;

        BlockHitResult blockHit = (BlockHitResult) hit;
        BlockPos pos = blockHit.getBlockPos();

        ArrowDirection arrowDir = switch (event.getKey()) {
            case GLFW.GLFW_KEY_UP -> ArrowDirection.UP;
            case GLFW.GLFW_KEY_DOWN -> ArrowDirection.DOWN;
            case GLFW.GLFW_KEY_LEFT -> ArrowDirection.LEFT;
            case GLFW.GLFW_KEY_RIGHT -> ArrowDirection.RIGHT;
            default -> null;
        };

        if (arrowDir != null) {
            ModMessages.INSTANCE.sendToServer(new RotateBlockPacket(pos, arrowDir, player.getDirection()));
        }
    }
}
