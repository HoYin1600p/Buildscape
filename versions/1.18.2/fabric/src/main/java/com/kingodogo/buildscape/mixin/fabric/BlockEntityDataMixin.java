package com.kingodogo.buildscape.mixin.fabric;

import com.kingodogo.buildscape.data.FabricPersistentData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
public abstract class BlockEntityDataMixin implements FabricPersistentData {
    @Unique private CompoundTag buildscape$data = new CompoundTag();
    @Override public CompoundTag buildscape$getPersistentData() { return buildscape$data; }
    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void buildscape$save(CompoundTag tag, CallbackInfo ci) {
        if (!buildscape$data.isEmpty()) tag.put("ForgeData", buildscape$data.copy());
    }
    @Inject(method = "load", at = @At("HEAD"))
    private void buildscape$load(CompoundTag tag, CallbackInfo ci) { buildscape$data = tag.getCompound("ForgeData").copy(); }
}
