package com.kingodogo.buildscape.mixin.forge121;

import com.kingodogo.buildscape.platform.ForgeBlockEntityData;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Restores Forge's persistent custom-data contract without changing shared block entities. */
@Mixin(BlockEntity.class)
public abstract class BlockEntityDataMixin implements ForgeBlockEntityData {
    @Unique
    private CompoundTag buildscape$persistentData;

    @Override
    public CompoundTag buildscape$getPersistentData() {
        if (buildscape$persistentData == null) buildscape$persistentData = new CompoundTag();
        return buildscape$persistentData;
    }

    @Inject(method = "loadAdditional(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/HolderLookup$Provider;)V", at = @At("HEAD"))
    private void buildscape$loadPersistentData(CompoundTag tag, HolderLookup.Provider lookup, CallbackInfo ci) {
        buildscape$persistentData = tag.contains("ForgeData", Tag.TAG_COMPOUND)
                ? tag.getCompound("ForgeData").copy() : null;
    }

    @Inject(method = "saveAdditional(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/HolderLookup$Provider;)V", at = @At("HEAD"))
    private void buildscape$savePersistentData(CompoundTag tag, HolderLookup.Provider lookup, CallbackInfo ci) {
        if (buildscape$persistentData != null) tag.put("ForgeData", buildscape$persistentData.copy());
    }
}