package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.config.CosmeticsConfig;
import com.kingodogo.buildscape.network.PacketFactory;
import com.kingodogo.buildscape.network.TreeChopPacket;
import com.kingodogo.buildscape.util.TreeChopTraversal;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TreeChopHandler {

    private static final long BREAK_DELAY_MS = 1000;
    private static final int MAX_LOGS = 200;
    private static BlockPos targetBlockPos = null;
    private static long breakingStartTime = 0;
    private static BlockPos lastLookedAtPos = null;
    private static Set<BlockPos> connectedLogsCache = new HashSet<>();
    private static long lastCacheUpdate = 0;
    private static Level cachedLevel;

    public static void clientTick() {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.level == null) return;

        if (cachedLevel != mc.level) {
            resetTarget(mc, player);
            cachedLevel = mc.level;
        }

        if (Services.PLATFORM.isScreenOpen() || !player.isCreative() || !player.isShiftKeyDown() || mc.options.keyAttack == null || !mc.options.keyAttack.isDown()
                || !CosmeticsConfig.get().getCreativeTreeBreaker(player.getUUID())) {
            resetTarget(mc, player);
            return;
        }

        HitResult hit = mc.hitResult;
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) {
            resetTarget(mc, player);
            return;
        }

        BlockPos pos = ((BlockHitResult) hit).getBlockPos();
        if (mc.level == null) return;
        BlockState state = mc.level.getBlockState(pos);

        if (!isLog(state)) {
            resetTarget(mc, player);
            return;
        }

        if (!pos.equals(targetBlockPos)) {
            if (mc.level != null && targetBlockPos != null) {
                mc.level.destroyBlockProgress(player.getId(), targetBlockPos, -1);
            }
            targetBlockPos = pos;
            breakingStartTime = System.currentTimeMillis();
        } else {
            if (breakingStartTime > 0) {
                long elapsed = System.currentTimeMillis() - breakingStartTime;
                if (elapsed >= BREAK_DELAY_MS) {
                    PacketFactory.sendToServer(new TreeChopPacket(targetBlockPos));
                    if (mc.level != null) {
                        mc.level.destroyBlockProgress(player.getId(), targetBlockPos, -1);
                    }
                    targetBlockPos = null;
                    breakingStartTime = 0;
                    connectedLogsCache.clear();
                    lastLookedAtPos = null;
                } else {
                    int progress = (int) (elapsed * 10 / BREAK_DELAY_MS);
                    if (mc.level != null) {
                        mc.level.destroyBlockProgress(player.getId(), targetBlockPos, progress);
                    }
                }
            }
        }
    }

    public static boolean shouldCancelHighlight(Player player, HitResult hit) {
        if (player == null || !player.isCreative() || !player.isShiftKeyDown()) return false;
        if (!CosmeticsConfig.get().getCreativeTreeBreaker(player.getUUID())) return false;
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) return false;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return false;
        BlockPos pos = ((BlockHitResult) hit).getBlockPos();
        return isLog(mc.level.getBlockState(pos));
    }

    public static void renderConnectedLogHighlights(PoseStack poseStack, Object bufferSource, Camera camera, BlockPos lookedAtPos, BlockState state) {
        renderConnectedLogHighlights(poseStack, bufferSource, Services.PLATFORM.getCameraPosition(camera), lookedAtPos, state);
    }

    public static void renderConnectedLogHighlights(PoseStack poseStack, Object bufferSource, Vec3 cameraPos, BlockPos lookedAtPos, BlockState state) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        if (!lookedAtPos.equals(lastLookedAtPos) || System.currentTimeMillis() - lastCacheUpdate > 500) {
            connectedLogsCache = findConnectedLogs(mc.level, lookedAtPos, state.getBlock());
            lastLookedAtPos = lookedAtPos;
            lastCacheUpdate = System.currentTimeMillis();
        }

        double camX = cameraPos.x;
        double camY = cameraPos.y;
        double camZ = cameraPos.z;

        for (BlockPos logPos : connectedLogsCache) {
            if (!mc.level.isLoaded(logPos)) continue;
            VoxelShape shape = mc.level.getBlockState(logPos).getShape(mc.level, logPos);
            if (shape.isEmpty()) continue;

            AABB aabb = shape.bounds().move(logPos);
            Services.PLATFORM.renderLineBox(
                    poseStack,
                    bufferSource,
                    aabb.minX - camX, aabb.minY - camY, aabb.minZ - camZ,
                    aabb.maxX - camX, aabb.maxY - camY, aabb.maxZ - camZ,
                    0.0f, 1.0f, 1.0f, 0.8f
            );
        }
    }

    public static boolean shouldCancelLeftClick(Player player, HitResult hit) {
        if (player == null || mcPlayerNotEligible(player)) return false;
        if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null) {
                BlockHitResult blockHit = (BlockHitResult) hit;
                BlockState state = mc.level.getBlockState(blockHit.getBlockPos());
                return isLog(state);
            }
        }
        return false;
    }

    private static boolean mcPlayerNotEligible(Player player) {
        return !player.isCreative() || !player.isShiftKeyDown() ||
                !CosmeticsConfig.get().getCreativeTreeBreaker(player.getUUID());
    }

    public static Set<BlockPos> getConnectedLogsCache() {
        return Collections.unmodifiableSet(connectedLogsCache);
    }

    public static String getOverlayText(Player player, HitResult hit) {
        if (player == null || !player.isCreative() || !player.isShiftKeyDown()) return null;
        if (!CosmeticsConfig.get().getCreativeTreeBreaker(player.getUUID())) return null;
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) return null;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return null;
        BlockPos pos = ((BlockHitResult) hit).getBlockPos();
        BlockState state = mc.level.getBlockState(pos);
        if (!isLog(state)) return null;
        if (connectedLogsCache.isEmpty()) return null;
        return state.getBlock().getName().getString() + ": " + connectedLogsCache.size();
    }

    public static boolean isLog(BlockState state) {
        if (state == null) return false;
        return state.is(BlockTags.LOGS)
                || Services.PLATFORM.isBlockInTag(state.getBlock(), new CommonId("minecraft", "warped_stems"))
                || Services.PLATFORM.isBlockInTag(state.getBlock(), new CommonId("minecraft", "crimson_stems"));
    }

    public static Set<BlockPos> findConnectedLogs(Level level, BlockPos startPos, Block targetBlock) {
        return TreeChopTraversal.collect(
                startPos,
                candidate -> level.isLoaded(candidate)
                        && level.getBlockState(candidate).getBlock() == targetBlock,
                MAX_LOGS);
    }

    public static void resetTarget(Minecraft mc, Player player) {
        if (mc.level != null && player != null && targetBlockPos != null) {
            mc.level.destroyBlockProgress(player.getId(), targetBlockPos, -1);
        }
        targetBlockPos = null;
        breakingStartTime = 0;
        connectedLogsCache.clear();
        lastLookedAtPos = null;
        lastCacheUpdate = 0;
    }

    public static void reset() {
        Minecraft mc = Minecraft.getInstance();
        resetTarget(mc, mc.player);
        cachedLevel = null;
    }
}
