package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.util.GhostFilterable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShulkerBoxBlockEntity.class)
public class ShulkerBoxBlockEntityMixin implements GhostFilterable {
    @Unique
    private final String[] buildscape$ghostFilters = new String[27];

    @Inject(method = "load", at = @At("TAIL"))
    private void onLoad(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("GhostFilters", 9)) {
            ListTag list = tag.getList("GhostFilters", 8);
            for (int i = 0; i < 27; i++) {
                if (i < list.size()) {
                    buildscape$ghostFilters[i] = list.getString(i);
                } else {
                    buildscape$ghostFilters[i] = "";
                }
            }
        } else {
            // Fallback: populate from existing items if they are ghost
            for (int i = 0; i < 27; i++) {
                buildscape$ghostFilters[i] = "";
            }
            if (tag.contains("Items", 9)) {
                ListTag itemsList = tag.getList("Items", 10);
                for (int i = 0; i < itemsList.size(); i++) {
                    CompoundTag itemTag = itemsList.getCompound(i);
                    int slot = itemTag.getByte("Slot") & 255;
                    if (slot >= 0 && slot < 27) {
                        if (itemTag.contains("tag", 10)) {
                            CompoundTag tagTag = itemTag.getCompound("tag");
                            if (tagTag.getBoolean("ghost")) {
                                buildscape$ghostFilters[slot] = itemTag.getString("id");
                            }
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void onSaveAdditional(CompoundTag tag, CallbackInfo ci) {
        ListTag list = new ListTag();
        for (int i = 0; i < 27; i++) {
            list.add(StringTag.valueOf(buildscape$ghostFilters[i] != null ? buildscape$ghostFilters[i] : ""));
        }
        tag.put("GhostFilters", list);
    }

    @Override
    @Unique
    public String[] buildscape$getGhostFilters() {
        return buildscape$ghostFilters;
    }
}
