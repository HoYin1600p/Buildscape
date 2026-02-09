package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.block.PillarBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;

public class PillarBlockEntityRenderer
        implements BlockEntityRenderer<PillarBlockEntity> {

    private final ItemRenderer itemRenderer;
    // Cache for entity instances to avoid creating new ones every frame
    private static final Map<String, Entity> entityCache =
            new ConcurrentHashMap<>();

    // Client-side timers for smooth rotation animation (completely independent of server)
    // Maps block position to the time when the item was first rendered on client
    private static final Map<BlockPos, Long> clientStartTimes =
            new ConcurrentHashMap<>();
    // Track the displayed item hash to detect when item changes (reset timer)
    private static final Map<BlockPos, Integer> itemHashes =
            new ConcurrentHashMap<>();

    public PillarBlockEntityRenderer(
            BlockEntityRendererProvider.Context context
    ) {
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
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

        try {
            poseStack.pushPose();

            // Check if the item is a spawn egg to determine position and rotation speed
            boolean isSpawnEgg = displayedItem.getItem() instanceof SpawnEggItem;

            // Check if the item has a name tag with "spin" - only then will mobs rotate
            // Use same logic as dye detection - check NBT directly
            boolean shouldSpin = false;
            if (isSpawnEgg) {
                shouldSpin = hasSpinNameTag(displayedItem);
            }

            // Position: items and mobs both hover just above pillar with 1-2 pixel gap
            // Items: 1.4625 blocks (1.15 + 5 pixels = 0.3125 blocks)
            // Mobs: 1.125 blocks (1.0 + 2 pixels = 0.125 blocks) - right on top with 2 pixel gap
            float hoverHeight = isSpawnEgg ? 1.125f : 1.4625f;
            poseStack.translate(0.5, hoverHeight, 0.5);

            // Smooth rotation animation using PURELY CLIENT-SIDE time
            // CRITICAL: Use RenderSystem.getTime() which is completely independent of server sync
            // This ensures smooth rotation even when connected to servers with network lag
            // Block entity renderers are ONLY called on client, so this is safe
            // Items: Always rotate (360 degrees every 4 seconds, 90 degrees per second)
            // Mobs: Only rotate if name tag is "spin" (40% slower than items: 54 degrees per second)
            // If mob doesn't have "spin" name tag, rotation stays at 0 (no rotation)
            float rotationSpeed = 0.0f;
            if (!isSpawnEgg) {
                // Items always rotate
                rotationSpeed = 90.0f; // degrees per second
            } else if (shouldSpin) {
                // Mobs only rotate if name tag is "spin"
                rotationSpeed = 54.0f; // 40% slower than items
            }

            // Use client-side system time for completely smooth animation independent of server
            // System.nanoTime() returns time in nanoseconds since some arbitrary point
            // This is completely independent of server sync and provides smooth animation
            long currentRenderTime = System.nanoTime() / 1000000L; // Convert nanoseconds to milliseconds

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
                poseStack.mulPose(Vector3f.YP.rotationDegrees(rotation));
            }

            // Add a slight floating animation (bobbing up and down)
            float bobAmount = (float) Math.sin(gameTime * 2.0f) * 0.05f; // Bob up and down
            poseStack.translate(0, bobAmount, 0);

            // Check if the item is a spawn egg
            if (isSpawnEgg) {
                try {
                    // Check if spawn egg has "Grum" or "Dinnerbone" name for upside-down rendering
                    boolean isUpsideDown = hasUpsideDownName(displayedItem);
                    // Render mob instead of item
                    renderMob(
                            (SpawnEggItem) displayedItem.getItem(),
                            displayedItem,
                            blockEntity,
                            partialTicks,
                            poseStack,
                            bufferSource,
                            combinedLight,
                            rotation,
                            gameTime,
                            shouldSpin,
                            isUpsideDown
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
                poseStack.scale(0.5f, 0.5f, 0.5f);

                // Get the item model
                Level level = blockEntity.getLevel();
                BakedModel model =
                        this.itemRenderer.getModel(displayedItem, level, null, 0);

                // Check if item has enchantments for glint rendering (hasFoil() checks for glint effect)
                boolean hasGlint = displayedItem.hasFoil();

                // Render the item with glint if it has enchantments
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

    private void renderMob(
            SpawnEggItem spawnEgg,
            ItemStack spawnEggStack,
            PillarBlockEntity blockEntity,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int combinedLight,
            float rotation,
            float gameTime,
            boolean shouldSpin,
            boolean isUpsideDown
    ) {
        // Block entity renderers are ONLY called on client side, but double-check for safety
        if (spawnEgg == null || blockEntity == null || spawnEggStack == null) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.level == null || !mc.level.isClientSide) {
            return;
        }

        Level level = blockEntity.getLevel();
        if (level == null) {
            return;
        }

        BlockPos pos = blockEntity.getBlockPos();
        if (pos == null) {
            return;
        }

        EntityType<?> entityType = spawnEgg.getType(null);
        if (entityType == null) {
            return;
        }

        // Create a cache key based on block position and entity type
        String cacheKey =
                pos.getX() +
                        "," +
                        pos.getY() +
                        "," +
                        pos.getZ() +
                        ":" +
                        net.minecraftforge.registries.ForgeRegistries.ENTITIES.getKey(
                                entityType
                        ).toString();

        // Get or create cached entity instance
        Entity entity = entityCache.get(cacheKey);
        if (entity == null || entity.getType() != entityType || !entity.isAlive()) {
            // Remove old entity from cache if it exists
            if (entity != null && entity.isAlive()) {
                entity.remove(Entity.RemovalReason.DISCARDED);
            }

            // Create new entity instance
            entity = entityType.create(level);
            if (entity != null) {
                // Set entity properties for display
                entity.setNoGravity(true);
                entity.setInvulnerable(true);
                entity.setSilent(true);
                entity.setInvisible(false);
                // Set a unique UUID to avoid conflicts
                entity.setUUID(UUID.randomUUID());
                // Position entity (will be transformed by poseStack, at top of pillar with minimal gap)
                entity.setPos(pos.getX() + 0.5, pos.getY() + 1.0625, pos.getZ() + 0.5);
                // Set entity to not tick (we don't want it to update)
                entity.noPhysics = true;

                // Prevent animations and hurt/dying states for static display
                // Set tickCount to 0 to prevent time-based animations (bees, blazes, cod, etc.)
                entity.tickCount = 0;

                if (entity instanceof net.minecraft.world.entity.LivingEntity livingEntity) {
                    // Disable AI to prevent any AI-driven animations (only available on Mob, not all LivingEntity)
                    if (entity instanceof net.minecraft.world.entity.Mob) {
                        ((net.minecraft.world.entity.Mob) entity).setNoAi(true);
                    }
                    // Reset hurt and death timers to prevent hurt/dying animations
                    livingEntity.hurtTime = 0;
                    livingEntity.deathTime = 0;
                    // Set to idle pose - prevent walking/running animations
                    livingEntity.setSprinting(false);
                    livingEntity.setShiftKeyDown(false);
                    // Prevent entity from updating animations
                    livingEntity.animationSpeed = 0.0f;
                    livingEntity.animationSpeedOld = 0.0f;
                    livingEntity.animationPosition = 0.0f;

                    // Prevent attack/swing animations
                    livingEntity.swingTime = 0;
                    livingEntity.attackAnim = 0.0f;
                    livingEntity.oAttackAnim = 0.0f;

                    // Freeze all movement
                    livingEntity.setDeltaMovement(0, 0, 0);
                    livingEntity.setSpeed(0.0f);
                }

                // Entity-specific animation prevention
                // Bees: prevent wing flapping and other animations
                if (entity instanceof net.minecraft.world.entity.animal.Bee bee) {
                    bee.setRemainingPersistentAngerTime(0);
                }

                // Blazes: prevent rod rotation animations
                if (entity instanceof net.minecraft.world.entity.monster.Blaze) {
                    // Blaze animations are controlled by tickCount, which we already set to 0
                }

                // Fish (Cod, Salmon, etc.): prevent fin animations
                if (entity instanceof net.minecraft.world.entity.animal.AbstractFish) {
                    // Fish animations are also controlled by tickCount
                }

                // Cache the entity
                entityCache.put(cacheKey, entity);
            }
        }

        if (entity != null && entity.isAlive()) {
            // Calculate scale based on entity's actual dimensions to maintain proper proportions
            float entityWidth = entity.getBbWidth();
            float entityHeight = entity.getBbHeight();

            // Update entity position and rotation BEFORE rendering
            // Position: at the top of the pillar with minimal gap (1.125 blocks above block top = 2 pixels)
            // For normal entities: The entity's position is where its feet will be
            // For upside-down entities: Position at baseY, then rotate around head so head stays at baseY
            float baseY = pos.getY() + 1.125f;
            float entityY = baseY; // Always position at baseY, rotation will handle upside-down positioning
            entity.setPos(pos.getX() + 0.5, entityY, pos.getZ() + 0.5);

            // Keep entity in static state - prevent animations and hurt/dying states
            // Reset tickCount every frame to prevent time-based animations (bees, blazes, cod, etc.)
            entity.tickCount = 0;

            if (entity instanceof net.minecraft.world.entity.LivingEntity livingEntity) {
                // Disable AI every frame to prevent any AI-driven animations (only available on Mob, not all LivingEntity)
                if (entity instanceof net.minecraft.world.entity.Mob) {
                    ((net.minecraft.world.entity.Mob) entity).setNoAi(true);
                }
                // Reset hurt and death timers every frame to prevent hurt/dying animations
                livingEntity.hurtTime = 0;
                livingEntity.deathTime = 0;
                // Keep entity in idle pose
                livingEntity.setSprinting(false);
                livingEntity.setShiftKeyDown(false);
                // Prevent animation updates
                livingEntity.animationSpeed = 0.0f;
                livingEntity.animationSpeedOld = 0.0f;
                livingEntity.animationPosition = 0.0f;

                // Prevent attack/swing animations every frame
                livingEntity.swingTime = 0;
                livingEntity.attackAnim = 0.0f;
                livingEntity.oAttackAnim = 0.0f;

                // Freeze all movement every frame
                livingEntity.setDeltaMovement(0, 0, 0);
                livingEntity.setSpeed(0.0f);
            }

            // Entity-specific animation prevention every frame
            // Bees: prevent wing flapping and other animations
            if (entity instanceof net.minecraft.world.entity.animal.Bee bee) {
                bee.setRemainingPersistentAngerTime(0);
            }

            // Update entity rotation for smooth animation - THIS IS CRITICAL FOR ROTATION
            // Entity renderers use the entity's rotation values, so we need to set them every frame
            // Base rotation is the facing yaw (direction player was looking when placing)
            // If shouldSpin is true, add the spinning rotation on top
            // If isUpsideDown is true, add 180 degrees to face the opposite direction
            float baseYaw = blockEntity.getFacingYaw();
            float finalYaw = baseYaw;
            if (isUpsideDown) {
                finalYaw = (finalYaw + 180.0f) % 360.0f;
            }
            if (shouldSpin) {
                finalYaw = (finalYaw + rotation) % 360.0f;
            }
            if (finalYaw < 0) {
                finalYaw += 360.0f;
            }

            float prevYRot = entity.getYRot();
            entity.setYRot(finalYaw);
            entity.yRotO = prevYRot; // Previous rotation for smooth interpolation

            // Update head/body rotation for living entities to match the rotation
            if (entity instanceof net.minecraft.world.entity.LivingEntity livingEntity) {
                // Update body and head rotation to match the rotation animation
                float prevBodyRot = livingEntity.yBodyRot;
                livingEntity.yBodyRot = finalYaw;
                livingEntity.yBodyRotO = prevBodyRot;

                float prevHeadRot = livingEntity.yHeadRot;
                livingEntity.yHeadRot = finalYaw;
                livingEntity.yHeadRotO = prevHeadRot;
            }

            float scale;
            if (entityHeight <= 1.0f) {
                // Small entities (1 block or less): scale to fit nicely above pillar
                float maxDimension = Math.max(entityWidth, entityHeight);
                float targetSize = 0.8f;
                scale = targetSize / maxDimension;
                scale = Math.min(1.5f, scale);
            } else {
                // Taller entities: render at closer to full size
                if (entityHeight > 2.5f) {
                    scale = 1.8f / entityHeight;
                } else {
                    scale = 0.9f; // 90% of actual size
                }
                scale = Math.max(0.3f, scale);
            }

            // Get entity renderer
            net.minecraft.client.renderer.entity.EntityRenderDispatcher dispatcher =
                    Minecraft.getInstance().getEntityRenderDispatcher();
            @SuppressWarnings("unchecked")
            net.minecraft.client.renderer.entity.EntityRenderer<
                    Entity
                    > entityRenderer = (net.minecraft.client.renderer.entity.EntityRenderer<
                    Entity
                    >) dispatcher.getRenderer(entity);

            if (entityRenderer != null) {
                // Entity renderers translate to entity.getX(), getY(), getZ() and use entity.getYRot()
                // The pose stack in block entity renderers starts at block origin (0,0,0 relative to block)
                // Entity renderer will translate from camera to entity's world position
                poseStack.pushPose();

                // Add floating animation to entity position
                float bobAmount = (float) Math.sin(gameTime * 2.0f) * 0.05f;
                entity.setPos(pos.getX() + 0.5, entityY + bobAmount, pos.getZ() + 0.5);

                // Apply scale - entity renderer will translate and rotate, then our scale will be applied
                poseStack.scale(scale, scale, scale);

                // Apply upside-down rotation if spawn egg is named "Grum" or "Dinnerbone"
                if (isUpsideDown) {
                    // Entity is positioned at baseY (feet at baseY, head at baseY + entityHeight)
                    // Entity center is at baseY + entityHeight/2
                    // Rotate 180° around center: head (at baseY + entityHeight) rotates to baseY
                    // Translate to center, rotate, translate back
                    float centerOffset = entityHeight * 0.5f;
                    poseStack.translate(0.0, centerOffset, 0.0);
                    poseStack.mulPose(Vector3f.XP.rotationDegrees(180.0f));
                    poseStack.translate(0.0, -centerOffset, 0.0);
                }

                // Render the entity
                // Entity renderer will:
                // 1. Translate from camera to entity position (entity.getX(), getY(), getZ())
                // 2. Rotate by entity.getYRot() (which we've set to 'finalYaw' - this is CRITICAL for rotation!)
                // 3. Render the entity
                // Our scale is applied to the pose stack, so it will scale the entire entity
                // Use 0.0f for partialTicks to prevent animation interpolation that causes "bugging out"
                entityRenderer.render(
                        entity,
                        finalYaw,
                        0.0f,
                        poseStack,
                        bufferSource,
                        combinedLight
                );

                poseStack.popPose();
            }
        }
    }

    public static void clearEntityCache(BlockPos pos) {
        // Remove all entities cached for this position
        entityCache
                .entrySet()
                .removeIf(entry -> {
                    if (
                            entry
                                    .getKey()
                                    .startsWith(pos.getX() + "," + pos.getY() + "," + pos.getZ() + ":")
                    ) {
                        Entity entity = entry.getValue();
                        if (entity != null && entity.isAlive()) {
                            entity.remove(Entity.RemovalReason.DISCARDED);
                        }
                        return true;
                    }
                    return false;
                });

        // Also clear the client-side timer and item hash for this position
        clientStartTimes.remove(pos);
        itemHashes.remove(pos);
    }

    public static void clearEntityCache() {
        // Remove all entities and discard them
        entityCache
                .values()
                .forEach(entity -> {
                    if (entity != null && entity.isAlive()) {
                        entity.remove(Entity.RemovalReason.DISCARDED);
                    }
                });
        entityCache.clear();

        // Clear all client-side timers and item hashes
        clientStartTimes.clear();
        itemHashes.clear();
    }

    public static void cleanupStaleEntities() {
        entityCache
                .entrySet()
                .removeIf(entry -> {
                    Entity entity = entry.getValue();
                    return entity == null || !entity.isAlive();
                    // Remove entities that are too old (older than 5 minutes of game time)
                    // This prevents memory leaks if blocks are removed without calling clearEntityCache
// For now, keep all alive entities
                });
    }

    private boolean hasSpinNameTag(ItemStack stack) {
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

            // Check if the name contains "spin" (case-insensitive) - allows multiple words like "spin Dinnerbone"
            if (trimmedName != null && !trimmedName.isEmpty()) {
                return trimmedName.toLowerCase().contains("spin");
            }
        } catch (Exception e) {
            // If JSON parsing fails, try checking the raw string
            // Sometimes the name might be stored as plain text
            String trimmedName = nameJson.trim();
            trimmedName = net.minecraft.ChatFormatting.stripFormatting(trimmedName);
            if (trimmedName != null && !trimmedName.isEmpty()) {
                return trimmedName.toLowerCase().contains("spin");
            }
        }

        return false;
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
}
// Kingodogo Finished this File on 2025-12-10 20-50-05
