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
public class ShulkerBoxBlockEntityMixin implements GhostFilterable {
    @Unique
    private final String[] buildscape$ghostFilters = new String[27];
    @Dynamic
    @Inject(method = "load(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("HEAD"), require = 0)
    private void buildscape$migrateStoredGhostItems118(CompoundTag tag, CallbackInfo ci) {
        MixinFactory.migrateStoredGhostItems(tag);
    }

    @Dynamic
    @Inject(method = "load(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"), require = 0)
    private void buildscape$onLoad118(CompoundTag tag, CallbackInfo ci) {
        MixinFactory.readGhostFilters(tag, this.buildscape$ghostFilters);
    }

    @Dynamic
    @Inject(method = "saveAdditional(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"), require = 0)
    private void buildscape$onSaveAdditional118(CompoundTag outputOrTag, CallbackInfo ci) {
        MixinFactory.writeGhostFilters(outputOrTag, this.buildscape$ghostFilters);
    }
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
    @Dynamic
    @Inject(method = "loadAdditional(Lnet/minecraft/world/level/storage/ValueInput;)V", at = @At("TAIL"))
    private void buildscape$onLoad26(@Coerce Object valueInput, CallbackInfo ci) {
        MixinFactory.readGhostFilters(valueInput, this.buildscape$ghostFilters);
    }

    @Dynamic
    @Inject(method = "saveAdditional(Lnet/minecraft/world/level/storage/ValueOutput;)V", at = @At("TAIL"))
    private void buildscape$onSaveAdditional26(@Coerce Object valueOutput, CallbackInfo ci) {
        MixinFactory.writeGhostFilters(valueOutput, this.buildscape$ghostFilters);
    }

    @Override
    @Unique
    public String[] buildscape$getGhostFilters() {
        return buildscape$ghostFilters;
    }
}
