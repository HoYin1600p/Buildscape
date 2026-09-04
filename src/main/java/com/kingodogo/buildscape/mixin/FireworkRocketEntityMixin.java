package com.kingodogo.buildscape.mixin;

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
public abstract class FireworkRocketEntityMixin extends Projectile {

    @Shadow @Final
    private static EntityDataAccessor<ItemStack> DATA_ID_FIREWORKS_ITEM;

    @Shadow
    private LivingEntity attachedToEntity;

    protected FireworkRocketEntityMixin(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    private void buildscape$applyShotYaw(float yaw) {
        this.setYRot(yaw);
        this.yRotO = yaw;
        ItemStack stack = this.entityData.get(DATA_ID_FIREWORKS_ITEM);
        if (!stack.isEmpty()) {
            ItemStack copy = stack.copy();
            CompoundTag fireworks = copy.getOrCreateTagElement("Fireworks");
            fireworks.putFloat("ShotYaw", yaw);
            ListTag explosions = fireworks.getList("Explosions", 10);
            for (int i = 0; i < explosions.size(); i++) {
                explosions.getCompound(i).putFloat("ShotYaw", yaw);
            }
            this.entityData.set(DATA_ID_FIREWORKS_ITEM, copy);
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

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)V", at = @At("RETURN"))
    private void buildscape$onConstructAttached(Level level, ItemStack stack, LivingEntity shooter, CallbackInfo ci) {
        if (shooter != null) {
            buildscape$applyShotYaw(shooter.getYRot());
        }
    }

    @Inject(method = "handleEntityEvent", at = @At("HEAD"))
    private void buildscape$onHandleEntityEvent(byte eventId, CallbackInfo ci) {
        if (eventId == 17 && this.level.isClientSide) {
            ItemStack stack = this.entityData.get(DATA_ID_FIREWORKS_ITEM);
            if (!stack.isEmpty()) {
                CompoundTag fireworks = stack.getTagElement("Fireworks");
                if (fireworks != null && !fireworks.contains("ShotYaw")) {
                    float yaw = this.getYRot();
                    if (this.attachedToEntity != null) {
                        yaw = this.attachedToEntity.getYRot();
                    } else if (this.getOwner() != null) {
                        yaw = this.getOwner().getYRot();
                    }
                    fireworks.putFloat("ShotYaw", yaw);
                    ListTag explosions = fireworks.getList("Explosions", 10);
                    for (int i = 0; i < explosions.size(); i++) {
                        if (!explosions.getCompound(i).contains("ShotYaw")) {
                            explosions.getCompound(i).putFloat("ShotYaw", yaw);
                        }
                    }
                }
            }
        }
    }
}
