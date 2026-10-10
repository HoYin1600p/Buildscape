package com.kingodogo.buildscape.block.entity;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
public final class CompoundTagData implements IBlockEntityData {
    private final CompoundTag tag;
    private final HolderLookup.Provider registries;

    public CompoundTagData(CompoundTag tag, HolderLookup.Provider registries) { this.tag = tag; this.registries = registries; }

    @Override public void putString(String key, String value) { if (value != null) tag.putString(key, value); }
    @Override public void putInt(String key, int value) { tag.putInt(key, value); }
    @Override public void putLong(String key, long value) { tag.putLong(key, value); }
    @Override public void putFloat(String key, float value) { tag.putFloat(key, value); }
    @Override public void putBoolean(String key, boolean value) { tag.putBoolean(key, value); }
    @Override public void putUUID(String key, UUID value) { if (value != null) tag.putUUID(key, value); }
    @Override public void putStringList(String key, List<String> values) {
        ListTag list = new ListTag();
        if (values != null) for (String value : values) if (value != null) list.add(StringTag.valueOf(value));
        tag.put(key, list);
    }
    @Override public void putItem(String key, ItemStack item) {
        if (item != null && !item.isEmpty()) {
            CompoundTag itemTag = (CompoundTag) item.save(registries);
            itemTag.putInt("RealCount", item.getCount());
            tag.put(key, itemTag);
        }
    }
    @Override public void putItemList(String key, List<ItemStack> items) {
        ListTag list = new ListTag();
        for (int slot = 0; slot < items.size(); slot++) {
            ItemStack stack = items.get(slot);
            if (stack != null && !stack.isEmpty()) {
                CompoundTag item = (CompoundTag) stack.save(registries);
                item.putByte("Slot", (byte) slot);
                list.add(item);
            }
        }
        tag.put(key, list);
    }
    @Override public void putBlockState(String key, BlockState state) { if (state != null) tag.put(key, NbtUtils.writeBlockState(state)); }
    @Override public void putSubData(String key, IBlockEntityData data) {
        if (!(data instanceof CompoundTagData child)) throw new IllegalArgumentException("Expected 1.21 CompoundTagData child");
        tag.put(key, child.tag);
    }
    @Override public IBlockEntityData createChild() { return new CompoundTagData(new CompoundTag(), registries); }

    @Override public String getStringOr(String key, String fallback) { return tag.contains(key, Tag.TAG_STRING) ? tag.getString(key) : fallback; }
    @Override public int getIntOr(String key, int fallback) { return tag.contains(key, Tag.TAG_INT) ? tag.getInt(key) : fallback; }
    @Override public long getLongOr(String key, long fallback) { return tag.contains(key, Tag.TAG_LONG) ? tag.getLong(key) : fallback; }
    @Override public float getFloatOr(String key, float fallback) { return tag.contains(key, Tag.TAG_FLOAT) ? tag.getFloat(key) : fallback; }
    @Override public boolean getBooleanOr(String key, boolean fallback) { return tag.contains(key, Tag.TAG_BYTE) ? tag.getBoolean(key) : fallback; }
    @Override public UUID getUUIDOrNull(String key) { return tag.hasUUID(key) ? tag.getUUID(key) : null; }
    @Override public List<String> getStringListOrEmpty(String key) {
        List<String> values = new ArrayList<>();
        ListTag list = tag.getList(key, Tag.TAG_STRING);
        for (int i = 0; i < list.size(); i++) values.add(list.getString(i));
        return values;
    }
    @Override public ItemStack getItemOrEmpty(String key) {
        if (!tag.contains(key, Tag.TAG_COMPOUND)) return ItemStack.EMPTY;
        CompoundTag itemTag = tag.getCompound(key);
        ItemStack stack = ItemStack.parseOptional(registries, itemTag);
        if (!stack.isEmpty() && itemTag.contains("RealCount", Tag.TAG_INT)) stack.setCount(itemTag.getInt("RealCount"));
        return stack;
    }
    @Override public List<ItemStack> getItemListOrEmpty(String key, int size) {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < size; i++) items.add(ItemStack.EMPTY);
        ListTag list = tag.getList(key, Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag item = list.getCompound(i);
            int slot = item.getByte("Slot") & 255;
            if (slot < size) items.set(slot, ItemStack.parseOptional(registries, item));
        }
        return items;
    }
    @Override public BlockState getBlockStateOrAir(String key) { return tag.contains(key, Tag.TAG_COMPOUND) ? NbtUtils.readBlockState(registries.lookupOrThrow(Registries.BLOCK), tag.getCompound(key)) : Blocks.AIR.defaultBlockState(); }
    @Override public Optional<IBlockEntityData> getSubData(String key) { return tag.contains(key, Tag.TAG_COMPOUND) ? Optional.of(new CompoundTagData(tag.getCompound(key), registries)) : Optional.empty(); }
    @Override public boolean contains(String key) { return tag.contains(key); }
}
