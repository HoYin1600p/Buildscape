package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.mixinsupport.MixinFactory;

import com.kingodogo.buildscape.util.GhostFilterable;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShulkerBoxBlockEntity.class)
public class ShulkerBoxBlockEntityV121xMixin implements GhostFilterable {
    @Unique
    private final String[] buildscape$ghostFilters = new String[27];
    @Dynamic
    @Inject(method = "loadAdditional(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/HolderLookup$Provider;)V", at = @At("HEAD"), require = 0)
    private void buildscape$migrateStoredGhostItems121(CompoundTag tag, @Coerce Object provider, CallbackInfo ci) {
        MixinFactory.migrateStoredGhostItems(tag);
    }

    @Dynamic
    @Inject(method = "loadAdditional(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/HolderLookup$Provider;)V", at = @At("TAIL"), require = 0)
    private void buildscape$onLoad121(CompoundTag tag, @Coerce Object provider, CallbackInfo ci) {
        MixinFactory.readGhostFilters(tag, this.buildscape$ghostFilters);
    }

    @Dynamic
    @Inject(method = "saveAdditional(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/HolderLookup$Provider;)V", at = @At("TAIL"), require = 0)
    private void buildscape$onSaveAdditional121(CompoundTag outputOrTag, @Coerce Object provider, CallbackInfo ci) {
        MixinFactory.writeGhostFilters(outputOrTag, this.buildscape$ghostFilters);
    }
    @Override
    @Unique
    public String[] buildscape$getGhostFilters() {
        return buildscape$ghostFilters;
    }
}
