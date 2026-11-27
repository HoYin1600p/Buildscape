package com.kingodogo.buildscape.mixin;

import com.mojang.datafixers.DataFixer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.storage.SavedDataStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SavedDataStorage.class)
public abstract class SavedDataStorageMixin {
    @Redirect(method = "readTagFromDisk", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/util/datafix/DataFixTypes;update(Lcom/mojang/datafixers/DataFixer;Lnet/minecraft/nbt/CompoundTag;II)Lnet/minecraft/nbt/CompoundTag;"), require = 0)
    private CompoundTag buildscape$allowUnfixedModData(DataFixTypes type, DataFixer fixer,
            CompoundTag data, int oldVersion, int newVersion) {
        return type == null ? data : type.update(fixer, data, oldVersion, newVersion);
    }
}
