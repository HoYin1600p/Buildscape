package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.block.PillarBlockEntity;
import com.kingodogo.buildscape.platform.Services;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PillarBlockEntityRenderer {

    private static final Map<BlockPos, DisplayInfo> displayInfo = new ConcurrentHashMap<>();
    private static final Map<BlockPos, Boolean> cachedIsAshenKing = new ConcurrentHashMap<>();
    private static final java.util.Set<String> loggedRenderFailures = ConcurrentHashMap.newKeySet();

    private static final class DisplayInfo {
        final ItemStack stack;
        final long startTime;
        final boolean renderAsItem;
        final boolean isSpawnEgg;
        final boolean isFixed;
        final MobState mobState;

        DisplayInfo(ItemStack stack, long startTime, boolean renderAsItem, boolean isSpawnEgg,
                    boolean isFixed, MobState mobState) {
            this.stack = stack;
            this.startTime = startTime;
            this.renderAsItem = renderAsItem;
            this.isSpawnEgg = isSpawnEgg;
            this.isFixed = isFixed;
            this.mobState = mobState;
        }
    }

    private static final Map<Object, net.minecraft.world.phys.AABB> modelBoundsCache = new java.util.WeakHashMap<>();

    public PillarBlockEntityRenderer() {
    }

    public static void cleanupStaleEntities() {
        MobPillarRenderer.cleanupStaleEntities();
    }

    public static void clearEntityCache(BlockPos pos) {
        MobPillarRenderer.clearEntityCache(pos);
        displayInfo.remove(pos);
        cachedIsAshenKing.remove(pos);
    }

    public static void clearEntityCache() {
        MobPillarRenderer.clearAllEntityCaches();
        displayInfo.clear();
        cachedIsAshenKing.clear();
    }

    public void render(
            PillarBlockEntity blockEntity,
            float partialTicks,
            PoseStack poseStack,
            Object bufferSource,
            int combinedLight,
            int combinedOverlay
    ) {

        if (blockEntity == null) {
            return;
        }

        ItemStack displayedItem = blockEntity.getDisplayedItem();
        if (displayedItem == null) {
            return;
        }

        BlockPos pos = blockEntity.getBlockPos();
        if (pos == null) {
            return;
        }

        if (displayedItem.isEmpty()) {
            displayInfo.remove(pos);
            return;
        }

        net.minecraft.world.entity.player.Player localPlayer = Services.PLATFORM.getClientPlayer();
        if (localPlayer != null) {
            int renderDistanceChunks = Services.PLATFORM.getRenderDistanceChunks();
            double maxRenderDistBlocks = renderDistanceChunks * 16.0;
            double dx = pos.getX() + 0.5 - localPlayer.getX();
            double dy = pos.getY() + 0.5 - localPlayer.getY();
            double dz = pos.getZ() + 0.5 - localPlayer.getZ();
            double distSq = dx * dx + dy * dy + dz * dz;
            if (distSq > maxRenderDistBlocks * maxRenderDistBlocks) {
                return;
            }
        }

        long currentRenderTime = System.currentTimeMillis();
        DisplayInfo info = displayInfo.get(pos);
        if (info == null || !ItemStack.matches(info.stack, displayedItem)) {
            info = createDisplayInfo(displayedItem, currentRenderTime);
            displayInfo.put(pos, info);
        }

        poseStack.pushPose();
        try {
            boolean isSpawnEgg = info.isSpawnEgg;

            boolean isAshenKing = cachedIsAshenKing.computeIfAbsent(
                    pos, k -> blockEntity.getBlockState().getBlock()
                            instanceof com.kingodogo.buildscape.block.AshenKingPillarBlock);
            float hoverHeight;
            if (isAshenKing) {
                hoverHeight = isSpawnEgg ? 0.875f : 1.0f;
            } else {
                hoverHeight = isSpawnEgg ? 1.125f : 1.4625f;
            }
            poseStack.translate(0.5, hoverHeight, 0.5);

            boolean isFixed = info.isFixed;
            float rotationSpeed = 0.0f;
            if (!isSpawnEgg) {
                rotationSpeed = 90.0f;
            } else if (info.mobState != null && info.mobState.spin) {
                rotationSpeed = 22.5f;
            }

            float elapsedSeconds = (currentRenderTime - info.startTime) / 1000.0f;

            float rotation = (elapsedSeconds * rotationSpeed) % 360.0f;

            float gameTime = elapsedSeconds;

            if (!isSpawnEgg) {
                if (isFixed) {
                    float facingYaw = blockEntity.getFacingYaw();
                    Services.PLATFORM.rotateY(poseStack, facingYaw);
                } else {
                    Services.PLATFORM.rotateY(poseStack, rotation);
                }
            }

            if (!isFixed) {
                float bobAmount = (float) Math.sin(gameTime * 2.0f) * 0.05f;
                poseStack.translate(0, bobAmount, 0);
            }

            boolean renderAsItem = info.renderAsItem;

            boolean isArmor = Services.PLATFORM.isArmor(displayedItem);
            boolean isElytra = Services.PLATFORM.isElytra(displayedItem);
            boolean isArmorStand = Services.PLATFORM.isArmorStand(displayedItem);

            if (!isSpawnEgg && (isArmor || isElytra || isArmorStand) && !renderAsItem) {
                ArmorPillarRenderer.renderArmor(
                        displayedItem,
                        pos,
                        blockEntity.getLevel(),
                        partialTicks,
                        poseStack,
                        bufferSource,
                        combinedLight,
                        rotation,
                        gameTime,
                        blockEntity.getFacingYaw(),
                        isFixed
                );
                return;
            }

            if (isSpawnEgg) {
                poseStack.pushPose();
                try {
                    MobPillarRenderer.renderMob(
                            (SpawnEggItem) displayedItem.getItem(),
                            displayedItem,
                            pos,
                            blockEntity.getLevel(),
                            partialTicks,
                            poseStack,
                            bufferSource,
                            combinedLight,
                            rotation,
                            gameTime,
                            blockEntity.getFacingYaw()
                    );
                } catch (Exception e) {
                    logRenderFailure(displayedItem, e);
                    poseStack.popPose();
                    poseStack.pushPose();
                    poseStack.scale(0.5f, 0.5f, 0.5f);
                    Level level = blockEntity.getLevel();
                    Object model = Services.PLATFORM.getItemModel(displayedItem, level, 0);
                    Services.PLATFORM.renderItemFixed(displayedItem, poseStack, bufferSource, combinedLight, combinedOverlay, model);
                }
                poseStack.popPose();
            } else {
                if (isFixed) {
                    Object model = Services.PLATFORM.getItemModel(displayedItem, blockEntity.getLevel(), 0);
                    net.minecraft.world.phys.AABB bounds = getOrCalculateBounds(model);

                    double lenX = bounds.maxX - bounds.minX;
                    double lenY = bounds.maxY - bounds.minY;
                    double visualLength = Math.sqrt(lenX * lenX + lenY * lenY);
                    if (visualLength < 0.1) visualLength = 1.0;

                    float scale = 0.8f;
                    double standardLength = 0.85;

                    boolean isSword = Services.PLATFORM.isSwordLike(displayedItem);
                    boolean isAxe = Services.PLATFORM.isAxeLike(displayedItem);

                    if (isSword) {
                        double baseTransY = -0.5;

                        double extraLength = Math.max(0, visualLength - standardLength);
                        double transY = baseTransY + (extraLength * 0.7 * scale);

                        double tipDist = (visualLength / 2.0) * scale;
                        double tipY = (1.4625 + transY) - tipDist;

                        if (tipY < 0.05) {
                            double correctiveLift = 0.05 - tipY;
                            transY += correctiveLift;
                        }

                        poseStack.translate(0, transY, 0);
                        Services.PLATFORM.rotateZ(poseStack, 135);
                        poseStack.scale(scale, scale, scale);

                    } else if (isAxe) {
                        double baseTransY = -0.55;

                        double extraLength = Math.max(0, visualLength - standardLength);
                        double transY = baseTransY + (extraLength * 0.7 * scale);

                        double tipDist = (visualLength / 2.0) * scale;
                        double tipY = (1.4625 + transY) - tipDist;

                        if (tipY < 0.05) {
                            double correctiveLift = 0.05 - tipY;
                            transY += correctiveLift;
                        }

                        poseStack.translate(0, transY, 0);
                        Services.PLATFORM.rotateZ(poseStack, 190);
                        poseStack.scale(scale, scale, scale);
                    } else {
                        poseStack.scale(0.5f, 0.5f, 0.5f);
                    }
                    boolean hasGlint = displayedItem.hasFoil();
                    Services.PLATFORM.renderItemFixed(displayedItem, poseStack, bufferSource, combinedLight, combinedOverlay, model);
                } else {
                    poseStack.scale(0.5f, 0.5f, 0.5f);

                    Level level = blockEntity.getLevel();
                    Object model = Services.PLATFORM.getItemModel(displayedItem, level, 0);
                    boolean hasGlint = displayedItem.hasFoil();

                    Services.PLATFORM.renderItemFixed(displayedItem, poseStack, bufferSource, combinedLight, combinedOverlay, model);
                }
            }

        } catch (Exception e) {
            logRenderFailure(displayedItem, e);
        } finally {
            poseStack.popPose();
        }
    }

    private DisplayInfo createDisplayInfo(ItemStack displayedItem, long startTime) {
        boolean renderAsItem = hasItemNameTag(displayedItem);
        boolean isSpawnEgg = displayedItem.getItem() instanceof SpawnEggItem && !renderAsItem;
        boolean fixed = !isSpawnEgg && isFixed(displayedItem);
        MobState mobState = null;
        if (isSpawnEgg) {
            mobState = MobStateParser.parseStates(displayedItem);
        }
        return new DisplayInfo(displayedItem.copy(), startTime, renderAsItem, isSpawnEgg, fixed, mobState);
    }

    private static void logRenderFailure(ItemStack stack, Exception e) {
        String key = String.valueOf(Services.PLATFORM.getItemId(stack.getItem()));
        if (loggedRenderFailures.add(key)) {
            BuildscapeCommon.LOGGER.warn("BuildScape: failed to render {} on a pillar: {}", key, e.getMessage());
        }
    }

    private net.minecraft.world.phys.AABB getOrCalculateBounds(Object model) {
        return modelBoundsCache.computeIfAbsent(model, key -> Services.PLATFORM.getModelBounds(key));
    }

    private boolean hasUpsideDownName(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        if (!(stack.getItem() instanceof SpawnEggItem)) return false;
        if (!Services.PLATFORM.hasCustomHoverName(stack)) return false;
        String name = net.minecraft.ChatFormatting.stripFormatting(stack.getHoverName().getString().trim().toLowerCase(java.util.Locale.ROOT));
        return name.contains("grum") || name.contains("dinnerbone");
    }

    private boolean hasItemNameTag(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        if (!Services.PLATFORM.hasCustomHoverName(stack)) return false;
        String name = net.minecraft.ChatFormatting.stripFormatting(stack.getHoverName().getString().trim());
        return name.equalsIgnoreCase("item");
    }

    private boolean isFixed(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        if (!Services.PLATFORM.hasCustomHoverName(stack)) return false;
        String name = net.minecraft.ChatFormatting.stripFormatting(stack.getHoverName().getString().trim().toLowerCase(java.util.Locale.ROOT));
        if (name.contains("fixed")) {
            return Services.PLATFORM.isWeaponOrTool(stack);
        }
        return false;
    }
}
