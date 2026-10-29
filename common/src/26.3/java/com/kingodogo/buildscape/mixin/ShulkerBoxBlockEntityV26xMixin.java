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
public class ShulkerBoxBlockEntityV26xMixin implements GhostFilterable {
    @Unique
    private final String[] buildscape$ghostFilters = new String[27];
    @Dynamic
    @Inject(method = "loadAdditional(Lnet/minecraft/world/level/storage/ValueInput;)V", at = @At("TAIL"))
    private void buildscape$onLoad26(@Coerce Object valueInput, CallbackInfo ci) {
        MixinFactory.readGhostFilters(valueInput, this.buildscape$ghostFilters);
        // Main's load() HEAD hook migrates legacy ghost entries (Items entries tagged ghost) into the filter list and
        // drops them from the inventory. 26.x reads Items through a codec that discards the legacy "tag", so the raw
        // entries are read back here (also accepting the data-fixed custom_data form) once the inventory is loaded.
        if (valueInput instanceof net.minecraft.world.level.storage.ValueInput input) {
            for (net.minecraft.world.level.storage.ValueInput entry : input.childrenListOrEmpty("Items")) {
                int slot = entry.getByteOr("Slot", (byte) 0) & 255;
                if (slot >= 27) continue;
                net.minecraft.nbt.CompoundTag legacy = entry.read("tag", net.minecraft.nbt.CompoundTag.CODEC).orElse(null);
                if (legacy == null) {
                    net.minecraft.nbt.CompoundTag components = entry.read("components", net.minecraft.nbt.CompoundTag.CODEC).orElse(null);
                    if (components != null) legacy = components.getCompound("minecraft:custom_data").orElse(null);
                }
                if (legacy == null || !legacy.getBooleanOr("ghost", false)) continue;

                if (this.buildscape$ghostFilters[slot] == null || this.buildscape$ghostFilters[slot].isEmpty()) {
                    this.buildscape$ghostFilters[slot] = entry.getStringOr("id", "");
                }
                ((net.minecraft.world.Container) (Object) this).removeItemNoUpdate(slot);
            }
        }
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
