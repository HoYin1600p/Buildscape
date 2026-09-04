package com.kingodogo.buildscape.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ElytraItem;
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
            MultiBufferSource bufferSource,
            int combinedLight,
            float rotation,
            float gameTime,
            float facingYaw,
            boolean isFixed
    ) {
        if (itemStack == null || level == null || pos == null) return;

        ArmorStand armorStand = armorStandCache.get(pos);
        if (armorStand == null || !armorStand.isAlive() || armorStand.level != level) {
            if (armorStand != null) {
                armorStand.remove(Entity.RemovalReason.DISCARDED);
            }
            armorStand = createArmorStand(level, pos);
            if (armorStand != null) {
                armorStandCache.put(pos, armorStand);
                lastRenderedStacks.remove(pos);
            } else {
                return;
            }
        }

        boolean isStandItem = itemStack.getItem() instanceof net.minecraft.world.item.ArmorStandItem;
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
            } else if (slot == EquipmentSlot.CHEST || itemStack.getItem() instanceof ElytraItem) {
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

        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();

        dispatcher.render(
                armorStand,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                partialTicks,
                poseStack,
                bufferSource,
                combinedLight
        );

        poseStack.popPose();
    }

    private static ArmorStand createArmorStand(Level level, BlockPos pos) {
        ArmorStand armorStand = new ArmorStand(EntityType.ARMOR_STAND, level);

        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("ShowArms", true);
        nbt.putBoolean("Small", false);
        nbt.putBoolean("NoGravity", true);
        nbt.putBoolean("NoBasePlate", true);
        nbt.putBoolean("Invisible", true);
        armorStand.readAdditionalSaveData(nbt);

        armorStand.setUUID(UUID.randomUUID());
        armorStand.setPos(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
        armorStand.noPhysics = true;
        return armorStand;
    }

    private static void updateArmorStandState(ArmorStand armorStand, ItemStack stack, EquipmentSlot slot, boolean isStandItem) {
        for (EquipmentSlot s : EquipmentSlot.values()) {
            armorStand.setItemSlot(s, ItemStack.EMPTY);
        }


        CompoundTag nbt = new CompoundTag();


        if (isStandItem) {
            nbt.putBoolean("Invisible", false);
            nbt.putBoolean("NoBasePlate", false);
        } else {
            nbt.putBoolean("Invisible", true);
            nbt.putBoolean("NoBasePlate", true);

            if (slot != null && !stack.isEmpty()) {
                armorStand.setItemSlot(slot, stack.copy());
            } else if (stack.getItem() instanceof ElytraItem) {
                armorStand.setItemSlot(EquipmentSlot.CHEST, stack.copy());
            }
        }

        try {
            armorStand.readAdditionalSaveData(nbt);
        } catch (Exception e) {
        }
    }

    private static EquipmentSlot getEquipmentSlot(ItemStack stack) {
        if (stack.getItem() instanceof ArmorItem) {
            return ((ArmorItem) stack.getItem()).getSlot();
        }
        if (stack.getItem() instanceof ElytraItem) {
            return EquipmentSlot.CHEST;
        }

        return null;
    }

    public static void clearCache(BlockPos pos) {
        ArmorStand as = armorStandCache.remove(pos);
        if (as != null) {
            as.remove(Entity.RemovalReason.DISCARDED);
        }
        lastRenderedStacks.remove(pos);
    }

    public static void clearAllCaches() {
        armorStandCache.values().forEach(as -> as.remove(Entity.RemovalReason.DISCARDED));
        armorStandCache.clear();
        lastRenderedStacks.clear();
    }
}
