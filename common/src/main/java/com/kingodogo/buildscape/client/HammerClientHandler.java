package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.item.HammerItem;
import com.kingodogo.buildscape.network.HammerReplacePacket;
import com.kingodogo.buildscape.network.PacketFactory;
import com.kingodogo.buildscape.platform.Services;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
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

public class HammerClientHandler {

    public static boolean renderBlockHighlight(PoseStack poseStack, Camera camera, Object bufferSource, HitResult target) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null) return false;

        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.isEmpty() || !(mainHand.getItem() instanceof HammerItem hammer)) return false;

        ItemStack offHand = player.getOffhandItem();
        if (offHand.isEmpty() || !(offHand.getItem() instanceof BlockItem)) return false;

        if (target == null || target.getType() != HitResult.Type.BLOCK) return false;

        BlockPos pos = ((BlockHitResult) target).getBlockPos();
        BlockState state = mc.level.getBlockState(pos);
        if (state.isAir()) return false;

        Vec3 cameraPos = Services.PLATFORM.getCameraPosition(camera);

        poseStack.pushPose();
        poseStack.translate(pos.getX() - cameraPos.x, pos.getY() - cameraPos.y, pos.getZ() - cameraPos.z);

        VoxelShape shape = state.getShape(mc.level, pos, CollisionContext.of(player));
        if (!shape.isEmpty()) {
            AABB bounds = shape.bounds();
            float[] color = getGlowColor(hammer.getHammerTier());
            renderGlowingOutline(poseStack, bufferSource, bounds, color[0], color[1], color[2]);
        }

        poseStack.popPose();
        return true;
    }

    private static float[] getGlowColor(HammerItem.HammerTier tier) {
        return switch (tier) {
            case IRON -> new float[]{0.75f, 0.75f, 0.8f};
            case DIAMOND -> new float[]{0.3f, 0.9f, 0.95f};
            case NETHERITE -> new float[]{0.6f, 0.2f, 0.2f};
        };
    }

    private static void renderGlowingOutline(PoseStack poseStack, Object bufferSource, AABB box, float r, float g, float b) {
        Services.PLATFORM.renderLineBox(
                poseStack, bufferSource,
                box.minX, box.minY, box.minZ,
                box.maxX, box.maxY, box.maxZ,
                r, g, b, 1.0f
        );

        AABB inflated = box.inflate(0.003);
        Services.PLATFORM.renderLineBox(
                poseStack, bufferSource,
                inflated.minX, inflated.minY, inflated.minZ,
                inflated.maxX, inflated.maxY, inflated.maxZ,
                r, g, b, 0.5f
        );
    }

    public static void onRightClick() {
        Minecraft mc = Minecraft.getInstance();
        if (Services.PLATFORM.isScreenOpen() || mc.player == null || mc.level == null) return;

        LocalPlayer player = mc.player;

        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.isEmpty() || !(mainHand.getItem() instanceof HammerItem)) return;

        ItemStack offHand = player.getOffhandItem();
        if (offHand.isEmpty() || !(offHand.getItem() instanceof BlockItem)) return;

        HitResult hit = mc.hitResult;
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) return;

        BlockHitResult blockHit = (BlockHitResult) hit;
        BlockPos pos = blockHit.getBlockPos();

        PacketFactory.sendToServer(new HammerReplacePacket(pos));
    }
}
