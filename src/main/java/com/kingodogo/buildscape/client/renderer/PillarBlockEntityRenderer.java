package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.block.PillarBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PillarBlockEntityRenderer
        implements BlockEntityRenderer<PillarBlockEntity> {

    private final ItemRenderer itemRenderer;

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

    public PillarBlockEntityRenderer(
            BlockEntityRendererProvider.Context context
    ) {
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    private static final Map<BakedModel, net.minecraft.world.phys.AABB> modelBoundsCache = new java.util.WeakHashMap<>();

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

    @Override
    public void render(
            PillarBlockEntity blockEntity,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
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

        net.minecraft.client.player.LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer != null) {
            int renderDistanceChunks = Minecraft.getInstance().options.renderDistance;
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

        PoseStack.Pose entryPose = poseStack.last();
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
                    poseStack.mulPose(Vector3f.YP.rotationDegrees(facingYaw));
                } else {
                    poseStack.mulPose(Vector3f.YP.rotationDegrees(rotation));
                }
            }

            if (!isFixed) {
                float bobAmount = (float) Math.sin(gameTime * 2.0f) * 0.05f;
                poseStack.translate(0, bobAmount, 0);
            }

            boolean renderAsItem = info.renderAsItem;

            boolean isArmor = displayedItem.getItem() instanceof net.minecraft.world.item.ArmorItem;
            boolean isElytra = displayedItem.getItem() instanceof net.minecraft.world.item.ElytraItem;
            boolean isArmorStand = displayedItem.getItem() instanceof net.minecraft.world.item.ArmorStandItem;

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
                PoseStack.Pose mobPose = poseStack.last();
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
                    restorePose(poseStack, mobPose);
                    poseStack.scale(0.5f, 0.5f, 0.5f);
                    Level level = blockEntity.getLevel();
                    BakedModel model =
                            this.itemRenderer.getModel(displayedItem, level, null, 0);
                    boolean hasGlint = displayedItem.hasFoil();
                    this.itemRenderer.render(
                            displayedItem,
                            ItemTransforms.TransformType.FIXED,
                            hasGlint,
                            poseStack,
                            bufferSource,
                            combinedLight,
                            combinedOverlay,
                            model
                    );
                }
            } else {
                if (isFixed) {
                    BakedModel model = this.itemRenderer.getModel(displayedItem, blockEntity.getLevel(), null, 0);
                    net.minecraft.world.phys.AABB bounds = getOrCalculateBounds(model);

                    double lenX = bounds.maxX - bounds.minX;
                    double lenY = bounds.maxY - bounds.minY;
                    double visualLength = Math.sqrt(lenX * lenX + lenY * lenY);
                    if (visualLength < 0.1) visualLength = 1.0;

                    float scale = 0.8f;
                    double standardLength = 0.85;

                    net.minecraft.world.item.Item item = displayedItem.getItem();
                    boolean isSword = item instanceof net.minecraft.world.item.SwordItem ||
                            item instanceof net.minecraft.world.item.TridentItem ||
                            item instanceof net.minecraft.world.item.ShovelItem;
                    boolean isAxe = item instanceof net.minecraft.world.item.AxeItem ||
                            item instanceof net.minecraft.world.item.PickaxeItem ||
                            item instanceof net.minecraft.world.item.HoeItem;

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
                        poseStack.mulPose(Vector3f.ZP.rotationDegrees(135));
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
                        poseStack.mulPose(Vector3f.ZP.rotationDegrees(190));
                        poseStack.scale(scale, scale, scale);
                    } else {
                        poseStack.scale(0.5f, 0.5f, 0.5f);
                    }
                    boolean hasGlint = displayedItem.hasFoil();
                    this.itemRenderer.render(
                            displayedItem,
                            ItemTransforms.TransformType.FIXED,
                            hasGlint,
                            poseStack,
                            bufferSource,
                            combinedLight,
                            combinedOverlay,
                            model
                    );
                } else {
                    poseStack.scale(0.5f, 0.5f, 0.5f);

                    Level level = blockEntity.getLevel();
                    BakedModel model = this.itemRenderer.getModel(displayedItem, level, null, 0);
                    boolean hasGlint = displayedItem.hasFoil();

                    this.itemRenderer.render(
                            displayedItem,
                            ItemTransforms.TransformType.FIXED,
                            hasGlint,
                            poseStack,
                            bufferSource,
                            combinedLight,
                            combinedOverlay,
                            model
                    );
                }
            }

        } catch (Exception e) {
            logRenderFailure(displayedItem, e);
        } finally {
            restorePose(poseStack, entryPose);
        }
    }

    private DisplayInfo createDisplayInfo(ItemStack displayedItem, long startTime) {
        boolean renderAsItem = hasItemNameTag(displayedItem);
        boolean isSpawnEgg = displayedItem.getItem() instanceof SpawnEggItem && !renderAsItem;
        boolean fixed = !isSpawnEgg && isFixed(displayedItem);
        MobState mobState = null;
        if (isSpawnEgg) {
            EntityType<?> entityType = ((SpawnEggItem) displayedItem.getItem()).getType(null);
            mobState = MobStateParser.parseStates(displayedItem, entityType);
        }
        return new DisplayInfo(displayedItem.copy(), startTime, renderAsItem, isSpawnEgg, fixed, mobState);
    }

    private static void restorePose(PoseStack poseStack, PoseStack.Pose pose) {
        while (poseStack.last() != pose && !poseStack.clear()) {
            poseStack.popPose();
        }
    }

    private static void logRenderFailure(ItemStack stack, Exception e) {
        String key = String.valueOf(net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(stack.getItem()));
        if (loggedRenderFailures.add(key)) {
            com.kingodogo.buildscape.BuildScape.LOGGER.warn("BuildScape: failed to render {} on a pillar", key, e);
        }
    }
    private net.minecraft.world.phys.AABB getOrCalculateBounds(BakedModel model) {
        return modelBoundsCache.computeIfAbsent(model, this::calculateBounds);
    }

    private net.minecraft.world.phys.AABB calculateBounds(BakedModel model) {
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, minZ = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;

        java.util.Random rand = new java.util.Random();
        for (net.minecraft.core.Direction dir : new net.minecraft.core.Direction[]{null, net.minecraft.core.Direction.DOWN, net.minecraft.core.Direction.UP, net.minecraft.core.Direction.NORTH, net.minecraft.core.Direction.SOUTH, net.minecraft.core.Direction.WEST, net.minecraft.core.Direction.EAST}) {
            rand.setSeed(42L);
            java.util.List<net.minecraft.client.renderer.block.model.BakedQuad> quads = model.getQuads(null, dir, rand);
            for (net.minecraft.client.renderer.block.model.BakedQuad quad : quads) {
                int[] vertices = quad.getVertices();

                int step = vertices.length / 4;
                for (int i = 0; i < 4; i++) {
                    float x = Float.intBitsToFloat(vertices[i * step]);
                    float y = Float.intBitsToFloat(vertices[i * step + 1]);
                    float z = Float.intBitsToFloat(vertices[i * step + 2]);

                    if (x < minX) minX = x;
                    if (y < minY) minY = y;
                    if (z < minZ) minZ = z;
                    if (x > maxX) maxX = x;
                    if (y > maxY) maxY = y;
                    if (z > maxZ) maxZ = z;
                }
            }
        }

        if (minX == Double.MAX_VALUE) {
            return new net.minecraft.world.phys.AABB(0, 0, 0, 1, 1, 1);
        }

        return new net.minecraft.world.phys.AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private boolean hasUpsideDownName(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        if (!(stack.getItem() instanceof SpawnEggItem)) {
            return false;
        }

        net.minecraft.nbt.CompoundTag tag = stack.getTag();
        if (tag == null) {
            return false;
        }

        if (!tag.contains("display", 10)) {
            return false;
        }

        net.minecraft.nbt.CompoundTag displayTag = tag.getCompound("display");
        if (!displayTag.contains("Name", 8)) {
            return false;
        }

        String nameJson = displayTag.getString("Name");
        if (nameJson == null || nameJson.isEmpty()) {
            return false;
        }

        try {
            net.minecraft.network.chat.Component nameComponent =
                    net.minecraft.network.chat.Component.Serializer.fromJson(nameJson);
            if (nameComponent == null) {
                return false;
            }

            String displayName = nameComponent.getString();
            if (displayName == null || displayName.isEmpty()) {
                return false;
            }

            String trimmedName = displayName.trim();
            trimmedName = net.minecraft.ChatFormatting.stripFormatting(trimmedName);

            if (trimmedName != null && !trimmedName.isEmpty()) {
                String lowerName = trimmedName.toLowerCase();
                return lowerName.contains("grum") || lowerName.contains("dinnerbone");
            }
        } catch (Exception e) {
            String trimmedName = nameJson.trim();
            trimmedName = net.minecraft.ChatFormatting.stripFormatting(trimmedName);
            if (trimmedName != null && !trimmedName.isEmpty()) {
                String lowerName = trimmedName.toLowerCase();
                return lowerName.contains("grum") || lowerName.contains("dinnerbone");
            }
        }

        return false;
    }

    private boolean hasItemNameTag(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        net.minecraft.nbt.CompoundTag tag = stack.getTag();
        if (tag == null) {
            return false;
        }

        if (!tag.contains("display", 10)) {
            return false;
        }

        net.minecraft.nbt.CompoundTag displayTag = tag.getCompound("display");
        if (!displayTag.contains("Name", 8)) {
            return false;
        }

        String nameJson = displayTag.getString("Name");
        if (nameJson == null || nameJson.isEmpty()) {
            return false;
        }

        try {
            net.minecraft.network.chat.Component nameComponent =
                    net.minecraft.network.chat.Component.Serializer.fromJson(nameJson);
            if (nameComponent == null) {
                return false;
            }

            String displayName = nameComponent.getString();
            if (displayName == null || displayName.isEmpty()) {
                return false;
            }

            String trimmedName = displayName.trim();
            trimmedName = net.minecraft.ChatFormatting.stripFormatting(trimmedName);

            if (trimmedName != null && !trimmedName.isEmpty()) {
                return trimmedName.equalsIgnoreCase("item");
            }
        } catch (Exception e) {
            String trimmedName = nameJson.trim();
            trimmedName = net.minecraft.ChatFormatting.stripFormatting(trimmedName);
            if (trimmedName != null && !trimmedName.isEmpty()) {
                return trimmedName.equalsIgnoreCase("item");
            }
        }

        return false;
    }

    private boolean isFixed(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        net.minecraft.nbt.CompoundTag tag = stack.getTag();
        if (tag == null) {
            return false;
        }

        if (!tag.contains("display", 10)) {
            return false;
        }

        net.minecraft.nbt.CompoundTag displayTag = tag.getCompound("display");
        if (!displayTag.contains("Name", 8)) {
            return false;
        }

        String nameJson = displayTag.getString("Name");
        if (nameJson == null || nameJson.isEmpty()) {
            return false;
        }

        try {
            net.minecraft.network.chat.Component nameComponent =
                    net.minecraft.network.chat.Component.Serializer.fromJson(nameJson);
            if (nameComponent == null) {
                return false;
            }

            String displayName = nameComponent.getString();
            if (displayName == null || displayName.isEmpty()) {
                return false;
            }

            String trimmedName = displayName.trim();
            trimmedName = net.minecraft.ChatFormatting.stripFormatting(trimmedName);

            if (trimmedName != null && !trimmedName.isEmpty()) {
                if (trimmedName.toLowerCase(java.util.Locale.ROOT).contains("fixed")) {
                    net.minecraft.world.item.Item item = stack.getItem();
                    return item instanceof net.minecraft.world.item.SwordItem ||
                            item instanceof net.minecraft.world.item.TridentItem ||
                            item instanceof net.minecraft.world.item.AxeItem ||
                            item instanceof net.minecraft.world.item.PickaxeItem ||
                            item instanceof net.minecraft.world.item.ShovelItem ||
                            item instanceof net.minecraft.world.item.HoeItem;
                }
            }
        } catch (Exception e) {
            String trimmedName = nameJson.trim();
            trimmedName = net.minecraft.ChatFormatting.stripFormatting(trimmedName);
            if (trimmedName != null && !trimmedName.isEmpty()) {
                if (trimmedName.toLowerCase(java.util.Locale.ROOT).contains("fixed")) {
                    net.minecraft.world.item.Item item = stack.getItem();
                    return item instanceof net.minecraft.world.item.SwordItem ||
                            item instanceof net.minecraft.world.item.TridentItem ||
                            item instanceof net.minecraft.world.item.AxeItem ||
                            item instanceof net.minecraft.world.item.PickaxeItem ||
                            item instanceof net.minecraft.world.item.ShovelItem ||
                            item instanceof net.minecraft.world.item.HoeItem;
                }
            }
        }

        return false;
    }
}
