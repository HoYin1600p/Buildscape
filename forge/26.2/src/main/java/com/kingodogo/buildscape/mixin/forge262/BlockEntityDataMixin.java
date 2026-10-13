package com.kingodogo.buildscape.mixin.forge262;

import com.kingodogo.buildscape.platform.ForgeBlockEntityData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Retains custom block-entity data through the 26.2 value serialization API. */
@Mixin(BlockEntity.class)
public abstract class BlockEntityDataMixin implements ForgeBlockEntityData {
    @Unique private CompoundTag buildscape$persistentData;

    @Override public CompoundTag buildscape$getPersistentData() {
        if (buildscape$persistentData == null) buildscape$persistentData = new CompoundTag();
        return buildscape$persistentData;
    }

    @Inject(method = "loadAdditional", at = @At("HEAD"))
    private void buildscape$loadPersistentData(ValueInput input, CallbackInfo ci) {
        buildscape$persistentData = input.read("BuildscapeData", CompoundTag.CODEC)
                .map(CompoundTag::copy).orElse(null);
    }

    @Inject(method = "saveAdditional", at = @At("HEAD"))
    private void buildscape$savePersistentData(ValueOutput output, CallbackInfo ci) {
        if (buildscape$persistentData != null) {
            output.store("BuildscapeData", CompoundTag.CODEC, buildscape$persistentData.copy());
        }
    }
}
