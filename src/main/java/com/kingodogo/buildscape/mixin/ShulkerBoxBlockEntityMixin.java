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

    @Inject(method = "load", at = @At("HEAD"))
    private void migrateStoredGhostItems(CompoundTag tag, CallbackInfo ci) {
        ListTag filters = tag.contains("GhostFilters", 9)
                ? tag.getList("GhostFilters", 8).copy()
                : new ListTag();
        while (filters.size() < 27) filters.add(StringTag.valueOf(""));

        if (tag.contains("Items", 9)) {
            ListTag items = tag.getList("Items", 10);
            for (int i = items.size() - 1; i >= 0; i--) {
                CompoundTag item = items.getCompound(i);
                int slot = item.getByte("Slot") & 255;
                if (slot >= 27 || !item.contains("tag", 10)
                        || !item.getCompound("tag").getBoolean("ghost")) continue;

                if (filters.getString(slot).isEmpty()) {
                    filters.set(slot, StringTag.valueOf(item.getString("id")));
                }
                items.remove(i);
            }
            tag.put("Items", items);
        }
        tag.put("GhostFilters", filters);
    }

    @Inject(method = "load", at = @At("TAIL"))
    private void onLoad(CompoundTag tag, CallbackInfo ci) {
        for (int i = 0; i < 27; i++) buildscape$ghostFilters[i] = "";
        if (tag.contains("GhostFilters", 9)) {
            ListTag list = tag.getList("GhostFilters", 8);
            for (int i = 0; i < 27; i++) {
                if (i < list.size()) {
                    String str = list.getString(i);
                    buildscape$ghostFilters[i] = str != null ? str : "";
                }
            }
        }
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void onSaveAdditional(CompoundTag tag, CallbackInfo ci) {
        boolean hasFilter = false;
        ListTag list = new ListTag();
        for (int i = 0; i < 27; i++) {
            String filter = buildscape$ghostFilters[i] != null ? buildscape$ghostFilters[i] : "";
            if (!filter.isEmpty()) hasFilter = true;
            list.add(StringTag.valueOf(filter));
        }
        if (hasFilter) {
            tag.put("GhostFilters", list);
        } else {
            tag.remove("GhostFilters");
        }
    }

    @Override
    @Unique
    public String[] buildscape$getGhostFilters() {
        return buildscape$ghostFilters;
    }
}
