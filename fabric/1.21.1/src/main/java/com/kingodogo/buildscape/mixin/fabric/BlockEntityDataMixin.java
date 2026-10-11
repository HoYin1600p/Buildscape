package com.kingodogo.buildscape.mixin.fabric;

import com.kingodogo.buildscape.data.FabricPersistentData;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntity.class)
public abstract class BlockEntityDataMixin implements FabricPersistentData {
    @Unique private CompoundTag buildscape$data = new CompoundTag();
    @Override public CompoundTag buildscape$getPersistentData() { return buildscape$data; }
    @Inject(method = "saveWithoutMetadata", at = @At("RETURN"))
    private void buildscape$save(HolderLookup.Provider registries, CallbackInfoReturnable<CompoundTag> cir) {
        if (!buildscape$data.isEmpty()) cir.getReturnValue().put("ForgeData", buildscape$data.copy());
    }
    @Inject(method = "loadWithComponents", at = @At("HEAD"))
    private void buildscape$load(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        buildscape$data = tag.getCompound("ForgeData").copy();
    }
}
