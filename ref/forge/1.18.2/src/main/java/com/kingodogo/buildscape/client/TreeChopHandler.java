package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.network.ModMessages;
import com.kingodogo.buildscape.network.TreeChopPacket;
import com.kingodogo.buildscape.util.TreeChopTraversal;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawSelectionEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(
        modid = com.kingodogo.buildscape.BuildScape.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public class TreeChopHandler {

    private static final long BREAK_DELAY_MS = 1000;
    private static final int MAX_LOGS = 200;
    private static BlockPos targetBlockPos = null;
    private static long breakingStartTime = 0;
    private static BlockPos lastLookedAtPos = null;
    private static Set<BlockPos> connectedLogsCache = new HashSet<>();
    private static long lastCacheUpdate = 0;
    private static Level cachedLevel;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.level == null) return;

        if (cachedLevel != mc.level) {
            resetTarget(mc, player);
            cachedLevel = mc.level;
        }

        if (!player.isCreative() || !player.isShiftKeyDown() || mc.options.keyAttack == null || !mc.options.keyAttack.isDown()
                || !com.kingodogo.buildscape.config.CosmeticsConfig.get().getCreativeTreeBreaker(player.getUUID())) {
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
                    ModMessages.INSTANCE.sendToServer(new TreeChopPacket(targetBlockPos));
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

    @SubscribeEvent
    public static void onRenderGameOverlay(net.minecraftforge.client.event.RenderGameOverlayEvent.Post event) {
        if (event.getType() != net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.TEXT) return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.level == null) return;
        if (!player.isCreative() || !player.isShiftKeyDown()) return;
        if (!com.kingodogo.buildscape.config.CosmeticsConfig.get().getCreativeTreeBreaker(player.getUUID()))
            return;

        HitResult hit = mc.hitResult;
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) return;

        BlockPos pos = ((BlockHitResult) hit).getBlockPos();
        BlockState state = mc.level.getBlockState(pos);
        if (!isLog(state)) return;

        if (connectedLogsCache.isEmpty()) return;

        String blockName = state.getBlock().getName().getString();
        String text = blockName + ": " + connectedLogsCache.size();

        int width = mc.font.width(text);
        int halfWidth = width / 2;
        int screenWidth = event.getWindow().getGuiScaledWidth();
        int screenHeight = event.getWindow().getGuiScaledHeight();

        int x = screenWidth / 2 - halfWidth;
        int y = screenHeight / 2 - 20;

        mc.font.draw(event.getMatrixStack(), text, x, y, 0xFF00FF00);
    }

    @SubscribeEvent
    public static void onHighlightBlock(DrawSelectionEvent.HighlightBlock event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.level == null) return;

        if (!player.isCreative() || !player.isShiftKeyDown()) return;
        if (!com.kingodogo.buildscape.config.CosmeticsConfig.get().getCreativeTreeBreaker(player.getUUID()))
            return;

        HitResult target = event.getTarget();
        if (target.getType() != HitResult.Type.BLOCK) return;

        BlockPos pos = ((BlockHitResult) target).getBlockPos();
        BlockState state = mc.level.getBlockState(pos);

        if (!isLog(state)) return;

        if (!pos.equals(lastLookedAtPos) || System.currentTimeMillis() - lastCacheUpdate > 500) {
            if (mc.level != null) {
                connectedLogsCache = findConnectedLogs(mc.level, pos, state.getBlock());
                lastLookedAtPos = pos;
                lastCacheUpdate = System.currentTimeMillis();
            }
        }

        PoseStack poseStack = event.getPoseStack();
        VertexConsumer consumer = event.getMultiBufferSource().getBuffer(RenderType.lines());
        double camX = event.getCamera().getPosition().x;
        double camY = event.getCamera().getPosition().y;
        double camZ = event.getCamera().getPosition().z;

        for (BlockPos logPos : connectedLogsCache) {
            if (mc.level != null && !mc.level.isLoaded(logPos)) continue;
            if (mc.level != null) {
                VoxelShape shape = mc.level.getBlockState(logPos).getShape(mc.level, logPos);
                if (shape.isEmpty()) continue;

                AABB aabb = shape.bounds().move(logPos);
                LevelRenderer.renderLineBox(
                        poseStack,
                        consumer,
                        aabb.minX - camX, aabb.minY - camY, aabb.minZ - camZ,
                        aabb.maxX - camX, aabb.maxY - camY, aabb.maxZ - camZ,
                        0.0f, 1.0f, 1.0f, 0.8f
                );
            }
        }

        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onLeftClick(InputEvent.ClickInputEvent event) {
        if (!event.isAttack()) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        if (mc.player.isCreative() && mc.player.isShiftKeyDown() &&
                com.kingodogo.buildscape.config.CosmeticsConfig.get().getCreativeTreeBreaker(mc.player.getUUID())) {
            HitResult hit = mc.hitResult;
            if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHit = (BlockHitResult) hit;
                BlockState state = mc.level.getBlockState(blockHit.getBlockPos());
                if (isLog(state)) {
                    event.setCanceled(true);
                    event.setSwingHand(true);
                }
            }
        }
    }

    private static boolean isLog(BlockState state) {
        return state.is(BlockTags.LOGS) || state.is(BlockTags.WARPED_STEMS) || state.is(BlockTags.CRIMSON_STEMS);
    }

    private static Set<BlockPos> findConnectedLogs(Level level, BlockPos startPos, Block targetBlock) {
        return TreeChopTraversal.collect(
                startPos,
                candidate -> level.isLoaded(candidate)
                        && level.getBlockState(candidate).getBlock() == targetBlock,
                MAX_LOGS);
    }

    private static void resetTarget(Minecraft mc, Player player) {
        if (mc.level != null && targetBlockPos != null) {
            mc.level.destroyBlockProgress(player.getId(), targetBlockPos, -1);
        }
        targetBlockPos = null;
        breakingStartTime = 0;
        connectedLogsCache.clear();
        lastLookedAtPos = null;
        lastCacheUpdate = 0;
    }
}
