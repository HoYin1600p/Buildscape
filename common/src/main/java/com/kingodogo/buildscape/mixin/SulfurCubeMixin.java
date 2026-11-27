package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.block.CushionBlock;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.cubemob.AbstractCubeMob;
import net.minecraft.world.entity.monster.cubemob.SulfurCube;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SulfurCube.class)
public abstract class SulfurCubeMixin extends AbstractCubeMob {

    @Shadow
    private static native boolean isSwallowableItem(ItemStack itemStack);

    @Shadow
    private native SoundEvent getEjectSound();

    @Unique
    private static final TagKey<Item> BUILDCAPE$SITABLE_TAG = TagKey.create(
            Registries.ITEM,
            Identifier.withDefaultNamespace("sulfur_cube_archetype/sitable")
    );

    protected SulfurCubeMixin(EntityType<? extends AbstractCubeMob> entityType, Level level) {
        super(entityType, level);
    }

    @Unique
    private static boolean buildscape$isCushion(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (stack.is(BUILDCAPE$SITABLE_TAG)) return true;
        Block block = Block.byItem(stack.getItem());
        return block instanceof CushionBlock;
    }

    @Inject(method = "canUseSlot", at = @At("HEAD"), cancellable = true)
    private void buildscape$canUseHeadSlot(EquipmentSlot slot, CallbackInfoReturnable<Boolean> cir) {
        if (slot == EquipmentSlot.HEAD) {
            cir.setReturnValue(this.isAlive() && !this.isBaby());
        }
    }

    @Inject(method = "isEquippableInSlot", at = @At("HEAD"), cancellable = true)
    private void buildscape$isEquippableHeadSlot(ItemStack stack, EquipmentSlot slot, CallbackInfoReturnable<Boolean> cir) {
        if (slot == EquipmentSlot.HEAD && buildscape$isCushion(stack)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getEquipmentSlotForItem", at = @At("HEAD"), cancellable = true)
    private void buildscape$getCushionEquipmentSlot(ItemStack stack, CallbackInfoReturnable<EquipmentSlot> cir) {
        if (buildscape$isCushion(stack)) {
            cir.setReturnValue(EquipmentSlot.HEAD);
        }
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void buildscape$handleCushionInteraction(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack held = player.getItemInHand(hand);
        ItemStack headItem = this.getItemBySlot(EquipmentSlot.HEAD);

        // 1. Shears remove and drop cushion first
        if (held.is(Items.SHEARS) && !headItem.isEmpty()) {
            if (this.level() instanceof ServerLevel serverLevel) {
                Vec3 dropPos = this.getAttachments().getAverage(EntityAttachment.PASSENGER);
                this.spawnAtLocation(serverLevel, headItem, dropPos);
                this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                this.playSound(this.getEjectSound());
                held.hurtAndBreak(1, player, hand.asEquipmentSlot());
                this.gameEvent(GameEvent.SHEAR, player);
            }
            cir.setReturnValue(this.level().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER);
            return;
        }

        // 2. Equip cushion on top if empty and not baby
        if (buildscape$isCushion(held)) {
            if (!this.isBaby() && headItem.isEmpty()) {
                if (!this.level().isClientSide()) {
                    this.setItemSlot(EquipmentSlot.HEAD, held.copyWithCount(1));
                    held.consume(1, player);
                    this.playSound(SoundType.WOOL.getPlaceSound());
                    this.gameEvent(GameEvent.ENTITY_INTERACT);
                }
                cir.setReturnValue(this.level().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER);
                return;
            }
        }

        // 3. Mount and sit on cushion if cushion present
        if (!headItem.isEmpty() && !player.isShiftKeyDown()) {
            // If player is holding a solid swallowable block and body is empty, let vanilla equip it inside
            boolean canFeedBody = isSwallowableItem(held) && this.getItemBySlot(EquipmentSlot.BODY).isEmpty();
            if (!canFeedBody && this.getPassengers().isEmpty()) {
                if (!this.level().isClientSide()) {
                    player.startRiding(this);
                }
                cir.setReturnValue(this.level().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER);
            }
        }
    }

    @Inject(method = "applyContactDamage", at = @At("HEAD"), cancellable = true)
    private void buildscape$cancelContactDamageToRider(Entity entity, CallbackInfo ci) {
        if (this.hasPassenger(entity) || (entity instanceof Player player && player.getVehicle() == this)) {
            ci.cancel();
        }
    }

    @Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
    private void buildscape$cancelPlayerTouchWhenRiding(Player player, CallbackInfo ci) {
        if (this.hasPassenger(player) || player.getVehicle() == this) {
            ci.cancel();
        }
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float scale) {
        if (!this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            return new Vec3(0.0, (double) dimensions.height() + 0.15, 0.0);
        }
        return super.getPassengerAttachmentPoint(passenger, dimensions, scale);
    }
}

