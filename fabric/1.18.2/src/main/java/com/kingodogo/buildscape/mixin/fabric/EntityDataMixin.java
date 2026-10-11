package com.kingodogo.buildscape.mixin.fabric;

import com.kingodogo.buildscape.data.FabricPersistentData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityDataMixin implements FabricPersistentData {
    @Unique private CompoundTag buildscape$data = new CompoundTag();
    @Override public CompoundTag buildscape$getPersistentData() { return buildscape$data; }
    @Inject(method = "saveWithoutId", at = @At("RETURN"))
    private void buildscape$save(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
        if (!buildscape$data.isEmpty()) tag.put("ForgeData", buildscape$data.copy());
    }
    @Inject(method = "load", at = @At("HEAD"))
    private void buildscape$load(CompoundTag tag, CallbackInfo ci) { buildscape$data = tag.getCompound("ForgeData").copy(); }
}
