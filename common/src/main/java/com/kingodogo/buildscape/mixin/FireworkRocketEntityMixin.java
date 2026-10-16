package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixinsupport.MixinFactory;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin extends Entity {

    @Shadow @Final
    private static EntityDataAccessor<ItemStack> DATA_ID_FIREWORKS_ITEM;

    @Shadow
    private LivingEntity attachedToEntity;

    protected FireworkRocketEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    private void buildscape$applyShotYaw(float yaw) {
        this.setYRot(yaw);
        this.yRotO = yaw;
        ItemStack stack = this.entityData.get(DATA_ID_FIREWORKS_ITEM);
        if (!stack.isEmpty()) {
            this.entityData.set(DATA_ID_FIREWORKS_ITEM, MixinFactory.applyFireworkShotYaw(stack, yaw));
        }
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/world/item/ItemStack;)V", at = @At("RETURN"))
    private void buildscape$onConstructWithShooter(Level level, Entity shooter, double x, double y, double z, ItemStack stack, CallbackInfo ci) {
        if (shooter != null) {
            buildscape$applyShotYaw(shooter.getYRot());
        }
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/Entity;DDDZ)V", at = @At("RETURN"))
    private void buildscape$onConstructCrossbow(Level level, ItemStack stack, Entity shooter, double x, double y, double z, boolean shotAtAngle, CallbackInfo ci) {
        if (shooter != null) {
            buildscape$applyShotYaw(shooter.getYRot());
        }
    }

    @org.spongepowered.asm.mixin.Dynamic
    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)V", at = @At("RETURN"), require = 0)
    private void buildscape$onConstructAttached(Level level, ItemStack stack, LivingEntity shooter, CallbackInfo ci) {
        if (shooter != null) {
            buildscape$applyShotYaw(shooter.getYRot());
        }
    }

    @Inject(method = "handleEntityEvent", at = @At("HEAD"))
    private void buildscape$onHandleEntityEvent(byte eventId, CallbackInfo ci) {
        if (eventId != 17 || !com.kingodogo.buildscape.platform.Services.PLATFORM.getEntityLevel(this).isClientSide()) return;
        ItemStack stack = this.entityData.get(DATA_ID_FIREWORKS_ITEM);
        if (stack.isEmpty()) return;
        CompoundTag data = com.kingodogo.buildscape.platform.Services.PLATFORM.getCustomData(stack, false);
        CompoundTag fireworks = data == null ? null : com.kingodogo.buildscape.platform.Services.PLATFORM.getTagCompound(data, "Fireworks");
        if (fireworks != null && fireworks.contains("ShotYaw")) return;
        Entity owner = ((Projectile) (Object) this).getOwner();
        float yaw = this.attachedToEntity != null ? this.attachedToEntity.getYRot()
                : owner != null ? owner.getYRot() : this.getYRot();
        ItemStack copy = stack.copy();
        com.kingodogo.buildscape.platform.Services.PLATFORM.updateCustomData(copy, tag -> {
            CompoundTag facing = com.kingodogo.buildscape.platform.Services.PLATFORM.getTagCompound(tag, "Fireworks");
            if (facing == null) facing = new CompoundTag();
            facing.putFloat("ShotYaw", yaw);
            if (facing.get("Explosions") instanceof ListTag explosions) {
                for (net.minecraft.nbt.Tag value : explosions) {
                    if (value instanceof CompoundTag explosion && !explosion.contains("ShotYaw")) {
                        explosion.putFloat("ShotYaw", yaw);
                    }
                }
            }
            tag.put("Fireworks", facing);
        });
        this.entityData.set(DATA_ID_FIREWORKS_ITEM, copy);
    }
}
