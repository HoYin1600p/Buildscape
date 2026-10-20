package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.core.NbtKeys;
import com.kingodogo.buildscape.feature.pillar.AshenKingPillarBlock;
import com.kingodogo.buildscape.feature.pillar.PillarBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorStandItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, value = Dist.CLIENT)
public class PillarBlockEntityRenderer
        implements
            BlockEntityRenderer<PillarBlockEntity> {

    private final ItemRenderer itemRenderer;

    private static final Map<BlockPos, DisplayInfo> displayInfo = new ConcurrentHashMap<>();
    private static final Map<BlockPos, Boolean> cachedIsAshenKing = new ConcurrentHashMap<>();
    private static final Set<String> loggedRenderFailures = ConcurrentHashMap.newKeySet();

    private static final class DisplayInfo {
        final ItemStack stack;
        final long startTime;
        final boolean renderAsItem;
        final boolean isSpawnEgg;
        final boolean isFixed;
        final MobState mobState;
        final WeakReference<PillarBlockEntity> owner;

        DisplayInfo(ItemStack stack, long startTime, boolean renderAsItem, boolean isSpawnEgg,
                boolean isFixed, MobState mobState, PillarBlockEntity owner) {
            this.stack = stack;
            this.startTime = startTime;
            this.renderAsItem = renderAsItem;
            this.isSpawnEgg = isSpawnEgg;
            this.isFixed = isFixed;
            this.mobState = mobState;
            this.owner = new WeakReference<>(owner);
        }
    }

    public PillarBlockEntityRenderer(
            BlockEntityRendererProvider.Context context) {
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    private static final Map<BakedModel, AABB> modelBoundsCache = new WeakHashMap<>();

    public static void cleanupStaleEntities() {
        MobPillarRenderer.cleanupStaleEntities();
    }

    public static void clearEntityCache(BlockPos pos) {
        MobPillarRenderer.clearEntityCache(pos);
        ArmorPillarRenderer.clearCache(pos);
        displayInfo.remove(pos);
        cachedIsAshenKing.remove(pos);
    }

    public static void clearEntityCache() {
        MobPillarRenderer.clearAllEntityCaches();
        ArmorPillarRenderer.clearAllCaches();
        displayInfo.clear();
        cachedIsAshenKing.clear();
        loggedRenderFailures.clear();
        modelBoundsCache.clear();
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Level level = Minecraft.getInstance().level;
        for (Map.Entry<BlockPos, DisplayInfo> entry : displayInfo.entrySet()) {
            PillarBlockEntity owner = entry.getValue().owner.get();
            if (owner == null || owner.isRemoved() || owner.getLevel() != level
                    || !ItemStack.matches(entry.getValue().stack, owner.getDisplayedItem())) {
                clearEntityCache(entry.getKey());
            }
        }
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (!event.getWorld().isClientSide()) {
            return;
        }
        ChunkPos chunk = event.getChunk().getPos();
        for (BlockPos pos : displayInfo.keySet()) {
            if ((pos.getX() >> 4) == chunk.x && (pos.getZ() >> 4) == chunk.z) {
                clearEntityCache(pos);
            }
        }
    }

    @Override
    public void render(
            PillarBlockEntity blockEntity,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int combinedLight,
            int combinedOverlay) {
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
            if (displayInfo.containsKey(pos)) {
                clearEntityCache(pos);
            }
            return;
        }

        LocalPlayer localPlayer = Minecraft.getInstance().player;
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
        if (info == null || info.owner.get() != blockEntity || !ItemStack.matches(info.stack, displayedItem)) {
            if (info != null) {
                clearEntityCache(pos);
            }
            info = createDisplayInfo(displayedItem, currentRenderTime, blockEntity);
            displayInfo.put(pos, info);
        }

        PoseStack.Pose entryPose = poseStack.last();
        poseStack.pushPose();
        try {
            boolean isSpawnEgg = info.isSpawnEgg;

            boolean isAshenKing = cachedIsAshenKing.computeIfAbsent(
                    pos, k -> blockEntity.getBlockState().getBlock() instanceof AshenKingPillarBlock);
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

            boolean isArmor = displayedItem.getItem() instanceof ArmorItem;
            boolean isElytra = displayedItem.getItem() instanceof ElytraItem;
            boolean isArmorStand = displayedItem.getItem() instanceof ArmorStandItem;

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
                        isFixed);
                return;
            }

            if (isSpawnEgg) {
                PoseStack.Pose mobPose = poseStack.last();
                try {
                    MobPillarRenderer.renderMob(
                            (SpawnEggItem) displayedItem.getItem(),
                            info.mobState,
                            pos,
                            blockEntity.getLevel(),
                            partialTicks,
                            poseStack,
                            bufferSource,
                            combinedLight,
                            rotation,
                            gameTime,
                            blockEntity.getFacingYaw());
                } catch (Exception e) {
                    logRenderFailure(displayedItem, e);
                    restorePose(poseStack, mobPose);
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
                            model);
                }
            } else {
                if (isFixed) {
                    BakedModel model = this.itemRenderer.getModel(displayedItem, blockEntity.getLevel(), null, 0);
                    AABB bounds = getOrCalculateBounds(model);

                    double lenX = bounds.maxX - bounds.minX;
                    double lenY = bounds.maxY - bounds.minY;
                    double visualLength = Math.sqrt(lenX * lenX + lenY * lenY);
                    if (visualLength < 0.1) {
                        visualLength = 1.0;
                    }

                    float scale = 0.8f;
                    double standardLength = 0.85;

                    Item item = displayedItem.getItem();
                    boolean isSword = item instanceof SwordItem ||
                            item instanceof TridentItem ||
                            item instanceof ShovelItem;
                    boolean isAxe = item instanceof AxeItem ||
                            item instanceof PickaxeItem ||
                            item instanceof HoeItem;

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
                            model);
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
                            model);
                }
            }

        } catch (Exception e) {
            logRenderFailure(displayedItem, e);
        } finally {
            restorePose(poseStack, entryPose);
        }
    }

    private DisplayInfo createDisplayInfo(ItemStack displayedItem, long startTime, PillarBlockEntity owner) {
        boolean renderAsItem = hasItemNameTag(displayedItem);
        boolean isSpawnEgg = displayedItem.getItem() instanceof SpawnEggItem && !renderAsItem;
        boolean fixed = !isSpawnEgg && isFixed(displayedItem);
        MobState mobState = null;
        if (isSpawnEgg) {
            EntityType<?> entityType = ((SpawnEggItem) displayedItem.getItem()).getType(null);
            mobState = MobStateParser.parseStates(displayedItem, entityType);
        }
        return new DisplayInfo(displayedItem.copy(), startTime, renderAsItem, isSpawnEgg, fixed, mobState, owner);
    }

    private static void restorePose(PoseStack poseStack, PoseStack.Pose pose) {
        while (poseStack.last() != pose && !poseStack.clear()) {
            poseStack.popPose();
        }
    }

    private static void logRenderFailure(ItemStack stack, Exception e) {
        String key = String.valueOf(ForgeRegistries.ITEMS.getKey(stack.getItem()));
        if (loggedRenderFailures.add(key)) {
            BuildScape.LOGGER.warn("Failed to render {} on a pillar", key, e);
        }
    }
    private AABB getOrCalculateBounds(BakedModel model) {
        return modelBoundsCache.computeIfAbsent(model, this::calculateBounds);
    }

    private AABB calculateBounds(BakedModel model) {
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, minZ = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;

        Random rand = new Random();
        for (Direction dir : new Direction[]{null, Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH,
                Direction.WEST, Direction.EAST}) {
            rand.setSeed(42L);
            List<BakedQuad> quads = model.getQuads(null, dir, rand);
            for (BakedQuad quad : quads) {
                int[] vertices = quad.getVertices();

                int step = vertices.length / 4;
                for (int i = 0; i < 4; i++) {
                    float x = Float.intBitsToFloat(vertices[i * step]);
                    float y = Float.intBitsToFloat(vertices[i * step + 1]);
                    float z = Float.intBitsToFloat(vertices[i * step + 2]);

                    if (x < minX) {
                        minX = x;
                    }
                    if (y < minY) {
                        minY = y;
                    }
                    if (z < minZ) {
                        minZ = z;
                    }
                    if (x > maxX) {
                        maxX = x;
                    }
                    if (y > maxY) {
                        maxY = y;
                    }
                    if (z > maxZ) {
                        maxZ = z;
                    }
                }
            }
        }

        if (minX == Double.MAX_VALUE) {
            return new AABB(0, 0, 0, 1, 1, 1);
        }

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private boolean hasUpsideDownName(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        if (!(stack.getItem() instanceof SpawnEggItem)) {
            return false;
        }

        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return false;
        }

        if (!tag.contains(NbtKeys.DISPLAY, 10)) {
            return false;
        }

        CompoundTag displayTag = tag.getCompound(NbtKeys.DISPLAY);
        if (!displayTag.contains(NbtKeys.NAME, 8)) {
            return false;
        }

        String nameJson = displayTag.getString(NbtKeys.NAME);
        if (nameJson == null || nameJson.isEmpty()) {
            return false;
        }

        try {
            Component nameComponent = Component.Serializer.fromJson(nameJson);
            if (nameComponent == null) {
                return false;
            }

            String displayName = nameComponent.getString();
            if (displayName == null || displayName.isEmpty()) {
                return false;
            }

            String trimmedName = displayName.trim();
            trimmedName = ChatFormatting.stripFormatting(trimmedName);

            if (trimmedName != null && !trimmedName.isEmpty()) {
                String lowerName = trimmedName.toLowerCase(Locale.ROOT);
                return lowerName.contains("grum") || lowerName.contains("dinnerbone");
            }
        } catch (Exception e) {
            String trimmedName = nameJson.trim();
            trimmedName = ChatFormatting.stripFormatting(trimmedName);
            if (trimmedName != null && !trimmedName.isEmpty()) {
                String lowerName = trimmedName.toLowerCase(Locale.ROOT);
                return lowerName.contains("grum") || lowerName.contains("dinnerbone");
            }
        }

        return false;
    }

    private boolean hasItemNameTag(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return false;
        }

        if (!tag.contains(NbtKeys.DISPLAY, 10)) {
            return false;
        }

        CompoundTag displayTag = tag.getCompound(NbtKeys.DISPLAY);
        if (!displayTag.contains(NbtKeys.NAME, 8)) {
            return false;
        }

        String nameJson = displayTag.getString(NbtKeys.NAME);
        if (nameJson == null || nameJson.isEmpty()) {
            return false;
        }

        try {
            Component nameComponent = Component.Serializer.fromJson(nameJson);
            if (nameComponent == null) {
                return false;
            }

            String displayName = nameComponent.getString();
            if (displayName == null || displayName.isEmpty()) {
                return false;
            }

            String trimmedName = displayName.trim();
            trimmedName = ChatFormatting.stripFormatting(trimmedName);

            if (trimmedName != null && !trimmedName.isEmpty()) {
                return trimmedName.equalsIgnoreCase("item");
            }
        } catch (Exception e) {
            String trimmedName = nameJson.trim();
            trimmedName = ChatFormatting.stripFormatting(trimmedName);
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

        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return false;
        }

        if (!tag.contains(NbtKeys.DISPLAY, 10)) {
            return false;
        }

        CompoundTag displayTag = tag.getCompound(NbtKeys.DISPLAY);
        if (!displayTag.contains(NbtKeys.NAME, 8)) {
            return false;
        }

        String nameJson = displayTag.getString(NbtKeys.NAME);
        if (nameJson == null || nameJson.isEmpty()) {
            return false;
        }

        try {
            Component nameComponent = Component.Serializer.fromJson(nameJson);
            if (nameComponent == null) {
                return false;
            }

            String displayName = nameComponent.getString();
            if (displayName == null || displayName.isEmpty()) {
                return false;
            }

            String trimmedName = displayName.trim();
            trimmedName = ChatFormatting.stripFormatting(trimmedName);

            if (trimmedName != null && !trimmedName.isEmpty()) {
                if (trimmedName.toLowerCase(Locale.ROOT).contains("fixed")) {
                    Item item = stack.getItem();
                    return item instanceof SwordItem ||
                            item instanceof TridentItem ||
                            item instanceof AxeItem ||
                            item instanceof PickaxeItem ||
                            item instanceof ShovelItem ||
                            item instanceof HoeItem;
                }
            }
        } catch (Exception e) {
            String trimmedName = nameJson.trim();
            trimmedName = ChatFormatting.stripFormatting(trimmedName);
            if (trimmedName != null && !trimmedName.isEmpty()) {
                if (trimmedName.toLowerCase(Locale.ROOT).contains("fixed")) {
                    Item item = stack.getItem();
                    return item instanceof SwordItem ||
                            item instanceof TridentItem ||
                            item instanceof AxeItem ||
                            item instanceof PickaxeItem ||
                            item instanceof ShovelItem ||
                            item instanceof HoeItem;
                }
            }
        }

        return false;
    }
}
