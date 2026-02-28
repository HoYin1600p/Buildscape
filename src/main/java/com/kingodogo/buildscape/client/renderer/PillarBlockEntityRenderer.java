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

    // Client-side timers for smooth rotation animation (completely independent of server)
    // Maps block position to the time when the item was first rendered on client
    private static final Map<BlockPos, Long> clientStartTimes =
            new ConcurrentHashMap<>();
    // Track the displayed item hash to detect when item changes (reset timer)
    private static final Map<BlockPos, Integer> itemHashes =
            new ConcurrentHashMap<>();

    // ── Per-item render-state caches (keyed by item NBT hash) ─────────────────
    // hasItemNameTag / isFixed / hasUpsideDownName all parse JSON every frame;
    // cache the result so we only pay that cost when the item actually changes.
    private static final Map<Integer, Boolean> cachedIsFixed      = new ConcurrentHashMap<>();
    private static final Map<Integer, Boolean> cachedIsItem       = new ConcurrentHashMap<>();
    private static final Map<Integer, Boolean> cachedIsUpsideDown = new ConcurrentHashMap<>();
    private static final Map<Integer, MobState> cachedMobState    = new ConcurrentHashMap<>();
    // isAshenKing is block-type dependent, constant per position — cache it.
    private static final Map<BlockPos, Boolean> cachedIsAshenKing = new ConcurrentHashMap<>();

    public PillarBlockEntityRenderer(
            BlockEntityRendererProvider.Context context
    ) {
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    // Cache for model bounds to avoid recalculating every frame
    private static final Map<BakedModel, net.minecraft.world.phys.AABB> modelBoundsCache = new java.util.WeakHashMap<>();

    public static void cleanupStaleEntities() {
        MobPillarRenderer.cleanupStaleEntities();
    }

    public static void clearEntityCache(BlockPos pos) {
        MobPillarRenderer.clearEntityCache(pos);
        clientStartTimes.remove(pos);
        itemHashes.remove(pos);
        cachedIsAshenKing.remove(pos); // also evict block-type cache for this pos
    }

    public static void clearEntityCache() {
        MobPillarRenderer.clearAllEntityCaches();
        clientStartTimes.clear();
        itemHashes.clear();
        cachedIsFixed.clear();
        cachedIsItem.clear();
        cachedIsUpsideDown.clear();
        cachedMobState.clear();
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
        // Safety check - ensure block entity is valid
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

        // If item is removed, clear the timer and hash
        if (displayedItem.isEmpty()) {
            clientStartTimes.remove(pos);
            itemHashes.remove(pos);
            return;
        }

        // Distance-based culling: skip rendering entities on pillars that are beyond
        // the player's current render distance. This prevents off-screen pillars from
        // burning CPU/GPU time on entity rendering.
        net.minecraft.client.player.LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer != null) {
            int renderDistanceChunks = Minecraft.getInstance().options.renderDistance;
            // Convert chunk render distance to block distance (each chunk is 16 blocks).
            // We use the block-diagonal distance so corners are checked correctly.
            double maxRenderDistBlocks = renderDistanceChunks * 16.0;
            double dx = pos.getX() + 0.5 - localPlayer.getX();
            double dy = pos.getY() + 0.5 - localPlayer.getY();
            double dz = pos.getZ() + 0.5 - localPlayer.getZ();
            double distSq = dx * dx + dy * dy + dz * dz;
            if (distSq > maxRenderDistBlocks * maxRenderDistBlocks) {
                return;
            }
        }

        try {
            poseStack.pushPose();

        // Compute hash once — reused by all three per-item caches below
        int itemHash = displayedItem.hashCode();

        // Check if the item is a spawn egg to determine position and rotation speed
        boolean isSpawnEgg = displayedItem.getItem() instanceof SpawnEggItem
                && !cachedIsItem.computeIfAbsent(itemHash, k -> hasItemNameTag(displayedItem));

        // isAshenKing is constant per position (block type never changes) — cache it
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

            // Smooth rotation animation using PURELY CLIENT-SIDE time.
            // Items: Always rotate (90 deg/sec). Mobs: only if named "spin".

            // Cache isFixed lookup per item NBT hash — avoids repeated JSON parsing
            boolean isFixed = false;
            if (!isSpawnEgg) {
                isFixed = cachedIsFixed.computeIfAbsent(itemHash, k -> isFixed(displayedItem));
            }
            float rotationSpeed = 0.0f;
            if (!isSpawnEgg) {
                rotationSpeed = 90.0f;
            } else {
                // Cache MobState parse per item NBT hash — avoids repeated JSON/NBT parsing
                EntityType<?> entityType = ((SpawnEggItem) displayedItem.getItem()).getType(null);
                MobState mobState = cachedMobState.computeIfAbsent(
                        itemHash,
                        k -> MobStateParser.parseStates(displayedItem, entityType));
                if (mobState.spin) {
                    rotationSpeed = 22.5f;
                }
            }

            // Use System.currentTimeMillis() for smooth client-side animation — avoids nanoTime division
            long currentRenderTime = System.currentTimeMillis();

            // Check if the displayed item has changed - if so, reset the timer
            int currentItemHash = displayedItem.hashCode();
            Integer previousItemHash = itemHashes.get(pos);
            if (previousItemHash == null || previousItemHash != currentItemHash) {
                // Item changed or first time rendering - reset timer
                clientStartTimes.put(pos, currentRenderTime);
                itemHashes.put(pos, currentItemHash);
            }

            long startTime = clientStartTimes.get(pos);

            // Calculate elapsed time in seconds since item was first rendered
            float elapsedSeconds = (currentRenderTime - startTime) / 1000.0f;

            // Calculate rotation based on elapsed time (completely client-side)
            float rotation = (elapsedSeconds * rotationSpeed) % 360.0f;

            // Use elapsed time for floating animation as well
            float gameTime = elapsedSeconds;

            // For items, apply rotation to pose stack
            // For mobs, rotation will be handled by entity's rotation values
            if (!isSpawnEgg) {
                if (isFixed) {
                    // Fixed items don't spin, they face the pillar's direction
                    float facingYaw = blockEntity.getFacingYaw();
                    poseStack.mulPose(Vector3f.YP.rotationDegrees(facingYaw));
                } else {
                    poseStack.mulPose(Vector3f.YP.rotationDegrees(rotation));
                }
            }

            // Add a slight floating animation (bobbing up and down)
            if (!isFixed) {
                float bobAmount = (float) Math.sin(gameTime * 2.0f) * 0.05f; // Bob up and down
                poseStack.translate(0, bobAmount, 0);
            }

            // Check if item is named "item" to render as normal small item
            boolean renderAsItem = hasItemNameTag(displayedItem);

            // 3D Gear Detection
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

                poseStack.popPose();
                return;
            }

            // Check if the item is a spawn egg
            if (isSpawnEgg) {
                try {
                    // Use the new MobPillarRenderer for modular state-based rendering
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
                    // If rendering fails, just render as regular item to prevent crashes
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
                // Render item normally
                if (isFixed) {
                    // Revert to FIXED transform to guarantee correct positioning.
                    // The "white outline" is a lighting artifact of FIXED mode, but NONE mode causes unfixable positioning errors.
                    // We prioritize the correct 70/30 positioning logic here.
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
                        // Base position for standard sword
                        double baseTransY = -0.5;

                        // Dynamic Adjustment: 70% of EXTRA length sticks OUT.
                        // We use visualLength and standardLength from outer scope
                        double extraLength = Math.max(0, visualLength - standardLength);
                        double transY = baseTransY + (extraLength * 0.7 * scale);

                        // Dynamic Collision Check
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
                        // Axes should look "chopped" into the pillar (like the sword)
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
                    // Standard floating item rendering
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

            poseStack.popPose();
        } catch (Exception e) {
            // If rendering fails, make sure to pop the pose stack to prevent issues
            try {
                poseStack.popPose();
            } catch (Exception ignored) {
                // Ignore if pop fails
            }
        }
    }

    private net.minecraft.world.phys.AABB getOrCalculateBounds(BakedModel model) {
        return modelBoundsCache.computeIfAbsent(model, this::calculateBounds);
    }

    private net.minecraft.world.phys.AABB calculateBounds(BakedModel model) {
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, minZ = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;

        java.util.Random rand = new java.util.Random();
        // Check all sides + null side
        for (net.minecraft.core.Direction dir : new net.minecraft.core.Direction[]{null, net.minecraft.core.Direction.DOWN, net.minecraft.core.Direction.UP, net.minecraft.core.Direction.NORTH, net.minecraft.core.Direction.SOUTH, net.minecraft.core.Direction.WEST, net.minecraft.core.Direction.EAST}) {
            rand.setSeed(42L);
            java.util.List<net.minecraft.client.renderer.block.model.BakedQuad> quads = model.getQuads(null, dir, rand);
            for (net.minecraft.client.renderer.block.model.BakedQuad quad : quads) {
                int[] vertices = quad.getVertices();
                // Vertex data format: [x, y, z, color, u, v, ...] (usually IVertexBuilder format)
                // Default format is usually Position (3 floats) ...
                // Unpacking raw data relies on DefaultVertexFormat.BLOCK usually.
                // Standard baked quad stores data as int array.
                // Position 3 floats * 4 bytes? No, vertices is int[].
                // DefaultVertexFormat.BLOCK: Position(3F), Color(4UB), UV(2F), UV2(2S), Normal(3B), Padding(1B) = 32 bytes = 8 ints.
                // Position is at offset 0, 1, 2.

                int step = vertices.length / 4; // 4 vertices per quad
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

        // Fallback for empty models
        if (minX == Double.MAX_VALUE) {
            return new net.minecraft.world.phys.AABB(0, 0, 0, 1, 1, 1);
        }

        return new net.minecraft.world.phys.AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private boolean hasUpsideDownName(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        // Must be a spawn egg (checked by caller, but double-check here)
        if (!(stack.getItem() instanceof SpawnEggItem)) {
            return false;
        }

        // Check NBT directly for display.Name tag (same approach as dye detection)
        net.minecraft.nbt.CompoundTag tag = stack.getTag();
        if (tag == null) {
            return false;
        }

        // Check if display compound exists
        if (!tag.contains("display", 10)) { // 10 = TAG_COMPOUND
            return false;
        }

        net.minecraft.nbt.CompoundTag displayTag = tag.getCompound("display");
        if (!displayTag.contains("Name", 8)) { // 8 = TAG_STRING
            return false;
        }

        // Get the name JSON string
        String nameJson = displayTag.getString("Name");
        if (nameJson == null || nameJson.isEmpty()) {
            return false;
        }

        // Parse the JSON text component to get plain text
        try {
            net.minecraft.network.chat.Component nameComponent =
                    net.minecraft.network.chat.Component.Serializer.fromJson(nameJson);
            if (nameComponent == null) {
                return false;
            }

            // Get plain string from component
            String displayName = nameComponent.getString();
            if (displayName == null || displayName.isEmpty()) {
                return false;
            }

            // Trim whitespace and remove formatting codes
            String trimmedName = displayName.trim();
            trimmedName = net.minecraft.ChatFormatting.stripFormatting(trimmedName);

            // Check if the name contains "Grum" or "Dinnerbone" (case-insensitive) - allows multiple words like "spin Dinnerbone"
            if (trimmedName != null && !trimmedName.isEmpty()) {
                String lowerName = trimmedName.toLowerCase();
                return lowerName.contains("grum") || lowerName.contains("dinnerbone");
            }
        } catch (Exception e) {
            // If JSON parsing fails, try checking the raw string
            // Sometimes the name might be stored as plain text
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

        // Check NBT directly for display.Name tag
        net.minecraft.nbt.CompoundTag tag = stack.getTag();
        if (tag == null) {
            return false;
        }

        // Check if display compound exists
        if (!tag.contains("display", 10)) { // 10 = TAG_COMPOUND
            return false;
        }

        net.minecraft.nbt.CompoundTag displayTag = tag.getCompound("display");
        if (!displayTag.contains("Name", 8)) { // 8 = TAG_STRING
            return false;
        }

        // Get the name JSON string
        String nameJson = displayTag.getString("Name");
        if (nameJson == null || nameJson.isEmpty()) {
            return false;
        }

        // Parse the JSON text component to get plain text
        try {
            net.minecraft.network.chat.Component nameComponent =
                    net.minecraft.network.chat.Component.Serializer.fromJson(nameJson);
            if (nameComponent == null) {
                return false;
            }

            // Get plain string from component
            String displayName = nameComponent.getString();
            if (displayName == null || displayName.isEmpty()) {
                return false;
            }

            // Trim whitespace and remove formatting codes
            String trimmedName = displayName.trim();
            trimmedName = net.minecraft.ChatFormatting.stripFormatting(trimmedName);

            // Check if the name is exactly "item" (case-insensitive)
            if (trimmedName != null && !trimmedName.isEmpty()) {
                return trimmedName.equalsIgnoreCase("item");
            }
        } catch (Exception e) {
            // If JSON parsing fails, try checking the raw string
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
                    // Renaming 'Fixed' only affects weapons/tools: Sword, Trident, Axe, Pickaxe, Shovel, Hoe
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
// Kingodogo Finished this File on 2025-12-10 20-50-05
