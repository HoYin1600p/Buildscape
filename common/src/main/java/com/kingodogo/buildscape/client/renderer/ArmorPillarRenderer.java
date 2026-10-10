package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.platform.Services;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ArmorPillarRenderer {

    private static final Map<BlockPos, ArmorStand> armorStandCache = new ConcurrentHashMap<>();
    private static final Map<BlockPos, ItemStack> lastRenderedStacks = new ConcurrentHashMap<>();

    public static void renderArmor(
            ItemStack itemStack,
            BlockPos pos,
            Level level,
            float partialTicks,
            PoseStack poseStack,
            Object bufferSource,
            int combinedLight,
            float rotation,
            float gameTime,
            float facingYaw,
            boolean isFixed
    ) {

        if (itemStack == null || level == null || pos == null) return;

        ArmorStand armorStand = armorStandCache.get(pos);
        if (armorStand == null || !armorStand.isAlive() || Services.PLATFORM.getEntityLevel(armorStand) != level) {
            if (armorStand != null) {
                armorStand.discard();
            }
            armorStand = createArmorStand(level, pos);
            if (armorStand != null) {
                armorStandCache.put(pos, armorStand);
                lastRenderedStacks.remove(pos);
            } else {
                return;
            }
        }

        boolean isStandItem = Services.PLATFORM.isArmorStand(itemStack);
        EquipmentSlot slot = getEquipmentSlot(itemStack);

        ItemStack cachedStack = lastRenderedStacks.get(pos);
        boolean stackChanged = cachedStack == null || !ItemStack.matches(cachedStack, itemStack);

        if (stackChanged) {
            updateArmorStandState(armorStand, itemStack, slot, isStandItem);
            lastRenderedStacks.put(pos, itemStack);
        }

        poseStack.pushPose();

        float scale = 0.85f;
        double yOffset = 0.0;
        double baseOffset = -0.42;

        if (isStandItem) {
            yOffset = baseOffset;
            scale = 0.8f;
        } else {
            if (slot == EquipmentSlot.HEAD) {
                yOffset = baseOffset - 1.42;
            } else if (slot == EquipmentSlot.CHEST || itemStack.is(net.minecraft.world.item.Items.ELYTRA)) {
                yOffset = baseOffset - 0.72;
            } else if (slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET) {
                yOffset = baseOffset;
            } else {
                yOffset = baseOffset - 0.72;
            }
        }

        poseStack.scale(scale, scale, scale);
        poseStack.translate(0, yOffset, 0);

        armorStand.setYRot(0);
        armorStand.yRotO = 0;
        armorStand.setYHeadRot(0);
        armorStand.yHeadRotO = 0;
        armorStand.yBodyRot = 0;
        armorStand.yBodyRotO = 0;

        Services.PLATFORM.renderEntity(
                armorStand,
                0.0,
                0.0,
                0.0,
                0.0f,
                partialTicks,
                poseStack,
                bufferSource,
                combinedLight
        );

        poseStack.popPose();
    }

    private static ArmorStand createArmorStand(Level level, BlockPos pos) {
        net.minecraft.world.entity.Entity entity = Services.PLATFORM.createArmorStandEntity(level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
        if (!(entity instanceof ArmorStand armorStand)) {
            return null;
        }

        Services.PLATFORM.setupArmorStand(armorStand);
        armorStand.setPos(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
        return armorStand;
    }

    private static void updateArmorStandState(ArmorStand armorStand, ItemStack stack, EquipmentSlot slot, boolean isStandItem) {
        Services.PLATFORM.updateArmorStand(armorStand, stack, slot, isStandItem);
    }


    private static EquipmentSlot getEquipmentSlot(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        EquipmentSlot slot = Services.PLATFORM.getEquipmentSlot(stack);
        if (slot != null && slot != EquipmentSlot.MAINHAND) {
            return slot;
        }
        if (Services.PLATFORM.isElytra(stack)) {
            return EquipmentSlot.CHEST;
        }
        return null;
    }

    public static void clearCache(BlockPos pos) {
        ArmorStand as = armorStandCache.remove(pos);
        if (as != null) {
            as.discard();
        }
        lastRenderedStacks.remove(pos);
    }

    public static void clearAllCaches() {
        armorStandCache.values().forEach(ArmorStand::discard);
        armorStandCache.clear();
        lastRenderedStacks.clear();
    }
}
