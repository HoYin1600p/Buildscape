package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.block.ModBlocks;
import com.kingodogo.buildscape.block.MuffBlock;
import com.kingodogo.buildscape.item.ModItems;
import com.kingodogo.buildscape.platform.Services;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public class MuffBlockRenderer {

    private static final double MAX_RENDER_DISTANCE = 128.0;

    public static void renderMuffOutlines(PoseStack poseStack, Camera camera, Object bufferSource) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return;
        }

        if (!mc.player.getOffhandItem().is(ModItems.MUFF_BLOCK.get().asItem())) {
            return;
        }

        Level level = mc.level;
        if (camera == null) return;

        Vec3 cameraPos = Services.PLATFORM.getCameraPosition(camera);

        Set<BlockPos> activeMuffs = MuffBlockManager.getActiveMuffs();
        if (activeMuffs == null || activeMuffs.isEmpty()) {
            return;
        }

        poseStack.pushPose();

        double offsetX = -cameraPos.x;
        double offsetY = -cameraPos.y;
        double offsetZ = -cameraPos.z;

        for (BlockPos pos : activeMuffs) {
            double dx = pos.getX() + 0.5 - cameraPos.x;
            double dy = pos.getY() + 0.5 - cameraPos.y;
            double dz = pos.getZ() + 0.5 - cameraPos.z;
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (distance > MAX_RENDER_DISTANCE) {
                continue;
            }

            BlockState state = level.getBlockState(pos);
            if (state.is(ModBlocks.MUFF_BLOCK.get().getBlock())) {
                int radius = state.getValue(MuffBlock.RADIUS);
                double minX = pos.getX() + 0.5 - radius + offsetX;
                double minY = pos.getY() + 0.5 - radius + offsetY;
                double minZ = pos.getZ() + 0.5 - radius + offsetZ;
                double maxX = pos.getX() + 0.5 + radius + offsetX;
                double maxY = pos.getY() + 0.5 + radius + offsetY;
                double maxZ = pos.getZ() + 0.5 + radius + offsetZ;

                Services.PLATFORM.renderLineBox(
                        poseStack, bufferSource,
                        minX, minY, minZ,
                        maxX, maxY, maxZ,
                        0.0f, 220.0f / 255.0f, 1.0f, 180.0f / 255.0f
                );
            }
        }

        poseStack.popPose();
    }
}
