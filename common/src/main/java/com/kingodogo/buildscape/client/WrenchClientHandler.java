package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.network.PacketFactory;
import com.kingodogo.buildscape.network.RotateBlockPacket;
import com.kingodogo.buildscape.network.RotateBlockPacket.ArrowDirection;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WrenchClientHandler {

    public static boolean hasWrench(Player player) {
        if (player == null) return false;
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        Item wrench = Services.PLATFORM.getItem(new CommonId("buildscape", "wrench"));
        if (wrench == null) return false;
        return (!mainHand.isEmpty() && mainHand.getItem() == wrench) ||
               (!offHand.isEmpty() && offHand.getItem() == wrench);
    }

    public static boolean shouldCancelBlockHighlight(LocalPlayer player, HitResult target) {
        if (player == null || !player.isCrouching()) return false;
        if (!hasWrench(player)) return false;
        if (target == null || target.getType() != HitResult.Type.BLOCK) return false;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return false;
        BlockPos pos = ((BlockHitResult) target).getBlockPos();
        BlockState state = mc.level.getBlockState(pos);
        return !state.isAir();
    }

    public static void renderWrenchHighlight(PoseStack poseStack, Object bufferSource, Camera camera, BlockPos pos, BlockState state, LocalPlayer player) {
        renderWrenchHighlight(poseStack, bufferSource, Services.PLATFORM.getCameraPosition(camera), pos, state, player);
    }

    public static void renderWrenchHighlight(PoseStack poseStack, Object bufferSource, Vec3 cameraPos, BlockPos pos, BlockState state, LocalPlayer player) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        poseStack.pushPose();
        poseStack.translate(pos.getX() - cameraPos.x, pos.getY() - cameraPos.y, pos.getZ() - cameraPos.z);

        VoxelShape shape = state.getShape(mc.level, pos, CollisionContext.of(player));
        if (!shape.isEmpty()) {
            AABB bounds = shape.bounds();
            renderGlowingOutline(poseStack, bufferSource, bounds);
        }

        poseStack.popPose();
    }

    public static void renderGlowingOutline(PoseStack poseStack, Object bufferSource, AABB box) {
        Services.PLATFORM.renderLineBox(
                poseStack, bufferSource,
                box.minX, box.minY, box.minZ,
                box.maxX, box.maxY, box.maxZ,
                1.0f, 0.6f, 0.1f, 1.0f
        );

        AABB inflated = box.inflate(0.003);
        Services.PLATFORM.renderLineBox(
                poseStack, bufferSource,
                inflated.minX, inflated.minY, inflated.minZ,
                inflated.maxX, inflated.maxY, inflated.maxZ,
                1.0f, 0.85f, 0.4f, 0.6f
        );
    }

    public static final int GLFW_PRESS = 1;
    public static final int GLFW_KEY_RIGHT = 262;
    public static final int GLFW_KEY_LEFT = 263;
    public static final int GLFW_KEY_DOWN = 264;
    public static final int GLFW_KEY_UP = 265;

    public static boolean handleKeyInput(int key, int action) {
        if (action != GLFW_PRESS) return false;

        Minecraft mc = Minecraft.getInstance();
        if (Services.PLATFORM.isScreenOpen() || mc.player == null || mc.level == null) return false;

        LocalPlayer player = mc.player;
        if (!player.isCrouching()) return false;
        if (!hasWrench(player)) return false;

        HitResult hit = mc.hitResult;
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) return false;

        BlockHitResult blockHit = (BlockHitResult) hit;
        BlockPos pos = blockHit.getBlockPos();

        ArrowDirection arrowDir = switch (key) {
            case GLFW_KEY_UP -> ArrowDirection.UP;
            case GLFW_KEY_DOWN -> ArrowDirection.DOWN;
            case GLFW_KEY_LEFT -> ArrowDirection.LEFT;
            case GLFW_KEY_RIGHT -> ArrowDirection.RIGHT;
            default -> null;
        };

        if (arrowDir != null) {
            PacketFactory.sendToServer(new RotateBlockPacket(pos, arrowDir, player.getDirection()));
            return true;
        }
        return false;
    }
}
