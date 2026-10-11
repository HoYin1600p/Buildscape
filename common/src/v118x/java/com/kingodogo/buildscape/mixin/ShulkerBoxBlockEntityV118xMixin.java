package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixinsupport.MixinFactory;

import com.kingodogo.buildscape.util.GhostFilterable;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShulkerBoxBlockEntity.class)
public class ShulkerBoxBlockEntityV118xMixin implements GhostFilterable {
    @Unique
    private final String[] buildscape$ghostFilters = new String[27];
    @Inject(method = "load(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("HEAD"))
    private void buildscape$migrateStoredGhostItems118(CompoundTag tag, CallbackInfo ci) {
        MixinFactory.migrateStoredGhostItems(tag);
    }

    @Inject(method = "load(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
    private void buildscape$onLoad118(CompoundTag tag, CallbackInfo ci) {
        MixinFactory.readGhostFilters(tag, this.buildscape$ghostFilters);
    }

    @Inject(method = "saveAdditional(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
    private void buildscape$onSaveAdditional118(CompoundTag outputOrTag, CallbackInfo ci) {
        MixinFactory.writeGhostFilters(outputOrTag, this.buildscape$ghostFilters);
    }
    @Override
    @Unique
    public String[] buildscape$getGhostFilters() {
        return buildscape$ghostFilters;
    }
}
