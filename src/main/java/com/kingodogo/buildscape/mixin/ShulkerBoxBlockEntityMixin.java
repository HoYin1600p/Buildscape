package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.core.NbtKeys;
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
        if (tag.getBoolean(NbtKeys.GHOST_MIGRATED)) return;
        ListTag filters = tag.contains(NbtKeys.GHOST_FILTERS, 9)
                ? tag.getList(NbtKeys.GHOST_FILTERS, 8).copy()
                : new ListTag();
        while (filters.size() < 27) filters.add(StringTag.valueOf(""));

        if (tag.contains(NbtKeys.ITEMS, 9)) {
            ListTag items = tag.getList(NbtKeys.ITEMS, 10);
            for (int i = items.size() - 1; i >= 0; i--) {
                CompoundTag item = items.getCompound(i);
                int slot = item.getByte("Slot") & 255;
                if (slot >= 27 || !item.contains(NbtKeys.TAG, 10)
                        || !item.getCompound(NbtKeys.TAG).getBoolean(NbtKeys.GHOST)) continue;

                if (filters.getString(slot).isEmpty()) {
                    filters.set(slot, StringTag.valueOf(item.getString(NbtKeys.ID)));
                }
                items.remove(i);
            }
            tag.put(NbtKeys.ITEMS, items);
        }
        tag.put(NbtKeys.GHOST_FILTERS, filters);
        tag.putBoolean(NbtKeys.GHOST_MIGRATED, true);
    }

    @Inject(method = "load", at = @At("TAIL"))
    private void onLoad(CompoundTag tag, CallbackInfo ci) {
        for (int i = 0; i < 27; i++) buildscape$ghostFilters[i] = "";
        if (tag.contains(NbtKeys.GHOST_FILTERS, 9)) {
            ListTag list = tag.getList(NbtKeys.GHOST_FILTERS, 8);
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
        tag.putBoolean(NbtKeys.GHOST_MIGRATED, true);
        boolean hasFilter = false;
        ListTag list = new ListTag();
        for (int i = 0; i < 27; i++) {
            String filter = buildscape$ghostFilters[i] != null ? buildscape$ghostFilters[i] : "";
            if (!filter.isEmpty()) hasFilter = true;
            list.add(StringTag.valueOf(filter));
        }
        if (hasFilter) {
            tag.put(NbtKeys.GHOST_FILTERS, list);
        } else {
            tag.remove(NbtKeys.GHOST_FILTERS);
        }
    }

    @Override
    @Unique
    public String[] buildscape$getGhostFilters() {
        return buildscape$ghostFilters;
    }
}
