package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MobPillarRenderer {

    private static final Map<String, Entity> entityCache = new ConcurrentHashMap<>();
    private static final Map<Integer, MobState> lastAppliedStates = new ConcurrentHashMap<>();
    private static final Map<String, ItemStack> cachedEggs = new ConcurrentHashMap<>();
    private static final Map<String, Integer> DYE_COLORS = new HashMap<>();

    static {
        DYE_COLORS.put("white", 0);
        DYE_COLORS.put("orange", 1);
        DYE_COLORS.put("magenta", 2);
        DYE_COLORS.put("light_blue", 3);
        DYE_COLORS.put("yellow", 4);
        DYE_COLORS.put("lime", 5);
        DYE_COLORS.put("pink", 6);
        DYE_COLORS.put("gray", 7);
        DYE_COLORS.put("light_gray", 8);
        DYE_COLORS.put("cyan", 9);
        DYE_COLORS.put("purple", 10);
        DYE_COLORS.put("blue", 11);
        DYE_COLORS.put("brown", 12);
        DYE_COLORS.put("green", 13);
        DYE_COLORS.put("red", 14);
        DYE_COLORS.put("black", 15);
    }

    public static void renderMob(
            SpawnEggItem spawnEgg,
            ItemStack spawnEggStack,
            BlockPos pos,
            Level level,
            float partialTicks,
            PoseStack poseStack,
            Object bufferSource,
            int combinedLight,

            float rotation,
            float gameTime,
            float facingYaw
    ) {
        if (spawnEgg == null || level == null || pos == null) {
            return;
        }

        MobState state = MobStateParser.parseStates(spawnEggStack);
        CommonId itemId = Services.PLATFORM.getItemId(spawnEggStack.getItem());
        String cacheKey = pos.getX() + "," + pos.getY() + "," + pos.getZ() + ":" + (itemId != null ? itemId.toString() : "");

        Entity entity = entityCache.get(cacheKey);
        boolean canBecomeGiant = itemId != null && java.util.Set.of("zombie_spawn_egg", "husk_spawn_egg", "drowned_spawn_egg")
                .contains(itemId.getPath());
        boolean wrongDisplayType = entity != null && canBecomeGiant
                && "giant".equals(Services.PLATFORM.getEntityTypeId(entity.getType()).getPath()) != state.parsedStates.contains("giant");
        ItemStack cachedEgg = cachedEggs.get(cacheKey);
        boolean changedEgg = cachedEgg == null || !ItemStack.matches(cachedEgg, spawnEggStack);
        if (entity == null || !entity.isAlive() || Services.PLATFORM.getEntityLevel(entity) != level || wrongDisplayType || changedEgg) {
            if (entity != null) {
                entity.discard();
                lastAppliedStates.remove(entity.getId());
            }

            entity = Services.PLATFORM.createMobPillarEntity(spawnEggStack, level, pos, state);
            if (entity != null) {
                entityCache.put(cacheKey, entity);
                cachedEggs.put(cacheKey, spawnEggStack.copy());
            }
        }

        if (entity != null && entity.isAlive()) {
            MobState lastState = lastAppliedStates.get(entity.getId());
            boolean needsUpdate = lastState == null || !lastState.equals(state);

            if (needsUpdate) {
                applyStates(entity, state);
                lastAppliedStates.put(entity.getId(), state);
            }

            updateEntityTransform(entity, pos, facingYaw, rotation, gameTime, state);

            CommonId typeId = Services.PLATFORM.getEntityTypeId(entity.getType());
            String typeName = typeId != null ? typeId.getPath() : "";
            boolean isJeb = "sheep".equals(typeName) && (state.parsedStates.contains("rainbow") || state.parsedStates.contains("jeb"));
            float renderPartialTicks = isJeb ? (float)((gameTime * 20.0f) % 1.0f) : 0.0f;

            renderEntity(entity, poseStack, bufferSource, combinedLight, renderPartialTicks, state);
        }
    }

    private static void applyStates(Entity entity, MobState state) {
        entity.tickCount = 0;

        if (state.fire) {
            entity.setRemainingFireTicks(20);
        }

        if (state.glowing) {
            entity.setGlowingTag(true);
        }

        if (state.invisible) {
            entity.setInvisible(true);
        }

        Services.PLATFORM.applyMobState(entity, state);
    }





    private static void updateEntityTransform(
            Entity entity,
            BlockPos pos,
            float facingYaw,
            float rotation,
            float gameTime,
            MobState state
    ) {
        float finalYaw = facingYaw;
        if (state.upsideDown) {
            finalYaw = (finalYaw + 180.0f) % 360.0f;
        }
        if (state.spin) {
            finalYaw = (finalYaw + rotation) % 360.0f;
        }
        if (finalYaw < 0) {
            finalYaw += 360.0f;
        }

        float prevYRot = entity.getYRot();
        entity.setYRot(finalYaw);
        entity.yRotO = prevYRot;

        if (entity instanceof LivingEntity livingEntity) {
            float prevBodyRot = livingEntity.yBodyRot;
            livingEntity.yBodyRot = finalYaw;
            livingEntity.yBodyRotO = prevBodyRot;

            float prevHeadRot = livingEntity.yHeadRot;
            livingEntity.yHeadRot = finalYaw;
            livingEntity.yHeadRotO = prevHeadRot;
        }

        float bobAmount = (float) Math.sin(gameTime * 2.0f) * 0.05f;
        float baseY = pos.getY() + 1.125f;

        CommonId typeId = Services.PLATFORM.getEntityTypeId(entity.getType());
        String typeName = typeId != null ? typeId.getPath() : "";

        if ("sheep".equals(typeName) && (state.parsedStates.contains("rainbow") || state.parsedStates.contains("jeb"))) {
            entity.tickCount = (int)(gameTime * 20);
        } else {
            entity.tickCount = 0;
            if (entity instanceof LivingEntity livingEntity) {
                if (!state.parsedStates.contains("hurt") && !state.parsedStates.contains("damage")) {
                    livingEntity.hurtTime = 0;
                }
                livingEntity.setSprinting(false);
                livingEntity.setShiftKeyDown(false);
                livingEntity.swingTime = 0;
                livingEntity.attackAnim = 0.0f;
                livingEntity.oAttackAnim = 0.0f;
                livingEntity.setDeltaMovement(0, 0, 0);
                livingEntity.setSpeed(0.0f);
            }
        }

        entity.setPos(pos.getX() + 0.5, baseY + bobAmount, pos.getZ() + 0.5);
    }

    private static void renderEntity(
            Entity entity,
            PoseStack poseStack,
            Object bufferSource,
            int combinedLight,
            float partialTicks,
            MobState state
    ) {
        float entityWidth = entity.getBbWidth();
        float entityHeight = entity.getBbHeight();
        float scale;

        CommonId typeId = Services.PLATFORM.getEntityTypeId(entity.getType());
        String typeName = typeId != null ? typeId.getPath() : "";

        if (state.parsedStates.contains("giant") || state.parsedStates.contains("huge")) {
            if ("rabbit".equals(typeName)) {
                scale = 1.8f;
            } else if (entityHeight > 6.0f) {
                scale = 0.4f;
            } else {
                scale = 1.2f;
            }
        } else if (state.parsedStates.contains("large")) {
            scale = 0.9f;
        } else if (state.parsedStates.contains("medium")) {
            scale = 0.7f;
        } else if (state.parsedStates.contains("small") && ("slime".equals(typeName) || "magma_cube".equals(typeName))) {
            scale = 0.8f;
        } else if (state.parsedStates.contains("tiny") && ("slime".equals(typeName) || "magma_cube".equals(typeName))) {
            scale = 1.0f;
        } else {
            float targetSize = state.baby ? 0.45f : 0.8f;

            if (entityHeight <= 1.0f) {
                float maxDimension = Math.max(entityWidth, entityHeight);
                scale = targetSize / maxDimension;
                scale = Math.min(state.baby ? 1.0f : 1.5f, scale);
            } else {
                if (entityHeight > 2.5f) {
                    scale = (targetSize * 2.25f) / entityHeight;
                } else {
                    scale = state.baby ? 0.5f : 0.9f;
                }
                scale = Math.max(0.3f, scale);
            }
        }

        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);

        if (state.upsideDown) {
            float centerOffset = entityHeight * 0.5f;
            poseStack.translate(0.0, centerOffset, 0.0);
            Services.PLATFORM.rotateX(poseStack, 180.0f);
            poseStack.translate(0.0, -centerOffset, 0.0);
        }

        Services.PLATFORM.renderEntity(
                entity,
                0.0D, 0.0D, 0.0D,
                entity.getYRot(),
                partialTicks,
                poseStack,
                bufferSource,
                combinedLight
        );

        poseStack.popPose();
    }


    public static void clearEntityCache(BlockPos pos) {
        entityCache.entrySet().removeIf(entry -> {
            if (entry.getKey().startsWith(pos.getX() + "," + pos.getY() + "," + pos.getZ() + ":")) {
                Entity entity = entry.getValue();
                if (entity != null && entity.isAlive()) {
                    lastAppliedStates.remove(entity.getId());
                    entity.discard();
                }
                return true;
            }
            return false;
        });
        cachedEggs.keySet().removeIf(key -> key.startsWith(pos.getX() + "," + pos.getY() + "," + pos.getZ() + ":"));
    }

    public static void clearAllEntityCaches() {
        entityCache.values().forEach(entity -> {
            if (entity != null && entity.isAlive()) {
                entity.discard();
            }
        });
        entityCache.clear();
        cachedEggs.clear();
        lastAppliedStates.clear();
    }

    public static void cleanupStaleEntities() {
        entityCache.entrySet().removeIf(entry -> {
            Entity entity = entry.getValue();
            boolean isStale = entity == null || !entity.isAlive();
            if (isStale && entity != null) {
                lastAppliedStates.remove(entity.getId());
            }
            return isStale;
        });
        cachedEggs.keySet().removeIf(key -> !entityCache.containsKey(key));
    }
}
