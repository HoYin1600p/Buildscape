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
    private static final Map<String, Entity> entityCache =
            new ConcurrentHashMap<>();

    private static final Map<BlockPos, Long> clientStartTimes =
            new ConcurrentHashMap<>();
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
            clientStartTimes.remove(pos);
            itemHashes.remove(pos);
            return;
        }

        try {
            poseStack.pushPose();

            boolean isSpawnEgg = displayedItem.getItem() instanceof SpawnEggItem;

            boolean shouldSpin = false;
            if (isSpawnEgg) {
                shouldSpin = hasSpinNameTag(displayedItem);
            }

            float hoverHeight = isSpawnEgg ? 1.125f : 1.4625f;
            poseStack.translate(0.5, hoverHeight, 0.5);

            float rotationSpeed = 0.0f;
            if (!isSpawnEgg) {
                rotationSpeed = 90.0f;
            } else if (shouldSpin) {
                rotationSpeed = 54.0f;
            }

            long currentRenderTime = System.nanoTime() / 1000000L;

            int currentItemHash = displayedItem.hashCode();
            Integer previousItemHash = itemHashes.get(pos);
            if (previousItemHash == null || previousItemHash != currentItemHash) {
                clientStartTimes.put(pos, currentRenderTime);
                itemHashes.put(pos, currentItemHash);
            }

            long startTime = clientStartTimes.get(pos);

            float elapsedSeconds = (currentRenderTime - startTime) / 1000.0f;

            float rotation = (elapsedSeconds * rotationSpeed) % 360.0f;

            float gameTime = elapsedSeconds;

            if (!isSpawnEgg) {
                poseStack.mulPose(Vector3f.YP.rotationDegrees(rotation));
            }

            float bobAmount = (float) Math.sin(gameTime * 2.0f) * 0.05f;
            poseStack.translate(0, bobAmount, 0);

            if (isSpawnEgg) {
                try {
                    boolean isUpsideDown = hasUpsideDownName(displayedItem);
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

            poseStack.popPose();
        } catch (Exception e) {
            try {
                poseStack.popPose();
            } catch (Exception ignored) {
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

        Entity entity = entityCache.get(cacheKey);
        if (entity == null || entity.getType() != entityType || !entity.isAlive()) {
            if (entity != null && entity.isAlive()) {
                entity.remove(Entity.RemovalReason.DISCARDED);
            }

            entity = entityType.create(level);
            if (entity != null) {
                entity.setNoGravity(true);
                entity.setInvulnerable(true);
                entity.setSilent(true);
                entity.setInvisible(false);
                entity.setUUID(UUID.randomUUID());
                entity.setPos(pos.getX() + 0.5, pos.getY() + 1.0625, pos.getZ() + 0.5);
                entity.noPhysics = true;

                entity.tickCount = 0;

                if (entity instanceof net.minecraft.world.entity.LivingEntity) {
                    net.minecraft.world.entity.LivingEntity livingEntity =
                            (net.minecraft.world.entity.LivingEntity) entity;
                    if (entity instanceof net.minecraft.world.entity.Mob) {
                        ((net.minecraft.world.entity.Mob) entity).setNoAi(true);
                    }
                    livingEntity.hurtTime = 0;
                    livingEntity.deathTime = 0;
                    livingEntity.setSprinting(false);
                    livingEntity.setShiftKeyDown(false);
                    livingEntity.animationSpeed = 0.0f;
                    livingEntity.animationSpeedOld = 0.0f;
                    livingEntity.animationPosition = 0.0f;

                    livingEntity.swingTime = 0;
                    livingEntity.attackAnim = 0.0f;
                    livingEntity.oAttackAnim = 0.0f;

                    livingEntity.setDeltaMovement(0, 0, 0);
                    livingEntity.setSpeed(0.0f);
                }

                if (entity instanceof net.minecraft.world.entity.animal.Bee) {
                    net.minecraft.world.entity.animal.Bee bee =
                            (net.minecraft.world.entity.animal.Bee) entity;
                    bee.setRemainingPersistentAngerTime(0);
                }

                if (entity instanceof net.minecraft.world.entity.monster.Blaze) {
                }

                if (entity instanceof net.minecraft.world.entity.animal.AbstractFish) {
                }

                entityCache.put(cacheKey, entity);
            }
        }

        if (entity != null && entity.isAlive()) {
            float entityWidth = entity.getBbWidth();
            float entityHeight = entity.getBbHeight();

            float baseY = pos.getY() + 1.125f;
            float entityY = baseY;
            entity.setPos(pos.getX() + 0.5, entityY, pos.getZ() + 0.5);

            entity.tickCount = 0;

            if (entity instanceof net.minecraft.world.entity.LivingEntity) {
                net.minecraft.world.entity.LivingEntity livingEntity =
                        (net.minecraft.world.entity.LivingEntity) entity;
                if (entity instanceof net.minecraft.world.entity.Mob) {
                    ((net.minecraft.world.entity.Mob) entity).setNoAi(true);
                }
                livingEntity.hurtTime = 0;
                livingEntity.deathTime = 0;
                livingEntity.setSprinting(false);
                livingEntity.setShiftKeyDown(false);
                livingEntity.animationSpeed = 0.0f;
                livingEntity.animationSpeedOld = 0.0f;
                livingEntity.animationPosition = 0.0f;

                livingEntity.swingTime = 0;
                livingEntity.attackAnim = 0.0f;
                livingEntity.oAttackAnim = 0.0f;

                livingEntity.setDeltaMovement(0, 0, 0);
                livingEntity.setSpeed(0.0f);
            }

            if (entity instanceof net.minecraft.world.entity.animal.Bee) {
                net.minecraft.world.entity.animal.Bee bee =
                        (net.minecraft.world.entity.animal.Bee) entity;
                bee.setRemainingPersistentAngerTime(0);
            }

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
            entity.yRotO = prevYRot;

            if (entity instanceof net.minecraft.world.entity.LivingEntity) {
                net.minecraft.world.entity.LivingEntity livingEntity =
                        (net.minecraft.world.entity.LivingEntity) entity;
                float prevBodyRot = livingEntity.yBodyRot;
                livingEntity.yBodyRot = finalYaw;
                livingEntity.yBodyRotO = prevBodyRot;

                float prevHeadRot = livingEntity.yHeadRot;
                livingEntity.yHeadRot = finalYaw;
                livingEntity.yHeadRotO = prevHeadRot;
            }

            float scale;
            if (entityHeight <= 1.0f) {
                float maxDimension = Math.max(entityWidth, entityHeight);
                float targetSize = 0.8f;
                scale = targetSize / maxDimension;
                scale = Math.min(1.5f, scale);
            } else {
                if (entityHeight > 2.5f) {
                    scale = 1.8f / entityHeight;
                } else {
                    scale = 0.9f;
                }
                scale = Math.max(0.3f, scale);
            }

            net.minecraft.client.renderer.entity.EntityRenderDispatcher dispatcher =
                    Minecraft.getInstance().getEntityRenderDispatcher();
            @SuppressWarnings("unchecked")
            net.minecraft.client.renderer.entity.EntityRenderer<
                    Entity
                    > entityRenderer = (net.minecraft.client.renderer.entity.EntityRenderer<
                    Entity
                    >) dispatcher.getRenderer(entity);

            if (entityRenderer != null) {
                poseStack.pushPose();

                float bobAmount = (float) Math.sin(gameTime * 2.0f) * 0.05f;
                entity.setPos(pos.getX() + 0.5, entityY + bobAmount, pos.getZ() + 0.5);

                poseStack.scale(scale, scale, scale);

                if (isUpsideDown) {
                    float centerOffset = entityHeight * 0.5f;
                    poseStack.translate(0.0, centerOffset, 0.0);
                    poseStack.mulPose(Vector3f.XP.rotationDegrees(180.0f));
                    poseStack.translate(0.0, -centerOffset, 0.0);
                }

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

        clientStartTimes.remove(pos);
        itemHashes.remove(pos);
    }

    public static void clearEntityCache() {
        entityCache
                .values()
                .forEach(entity -> {
                    if (entity != null && entity.isAlive()) {
                        entity.remove(Entity.RemovalReason.DISCARDED);
                    }
                });
        entityCache.clear();

        clientStartTimes.clear();
        itemHashes.clear();
    }

    public static void cleanupStaleEntities() {
        entityCache
                .entrySet()
                .removeIf(entry -> {
                    Entity entity = entry.getValue();
                    if (entity == null || !entity.isAlive()) {
                        return true;
                    }
                    return false;
                });
    }

    private boolean hasSpinNameTag(ItemStack stack) {
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
                return trimmedName.toLowerCase().contains("spin");
            }
        } catch (Exception e) {
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
}
