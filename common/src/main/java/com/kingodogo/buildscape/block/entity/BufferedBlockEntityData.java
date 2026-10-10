package com.kingodogo.buildscape.block.entity;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
public final class BufferedBlockEntityData implements IBlockEntityData {
    private final Map<String, Object> values = new LinkedHashMap<>();

    @Override public void putString(String key, String value) { if (value != null) values.put(key, value); }
    @Override public void putInt(String key, int value) { values.put(key, value); }
    @Override public void putLong(String key, long value) { values.put(key, value); }
    @Override public void putFloat(String key, float value) { values.put(key, value); }
    @Override public void putBoolean(String key, boolean value) { values.put(key, value); }
    @Override public void putUUID(String key, UUID value) { if (value != null) values.put(key, value); }
    @Override public void putStringList(String key, List<String> list) { values.put(key, list == null ? List.of() : new ArrayList<>(list)); }
    @Override public void putItem(String key, ItemStack item) { if (item != null && !item.isEmpty()) values.put(key, item.copy()); }
    @Override public void putItemList(String key, List<ItemStack> items) { values.put(key, new ItemStackList(items == null ? List.of() : items.stream().map(ItemStack::copy).toList())); }
    @Override public void putBlockState(String key, BlockState state) { if (state != null) values.put(key, state); }
    @Override public void putSubData(String key, IBlockEntityData data) {
        if (!(data instanceof BufferedBlockEntityData child)) throw new IllegalArgumentException("Expected buffered child data");
        values.put(key, child);
    }
    @Override public IBlockEntityData createChild() { return new BufferedBlockEntityData(); }

    @Override public String getStringOr(String key, String fallback) { Object value = values.get(key); return value instanceof String string ? string : fallback; }
    @Override public int getIntOr(String key, int fallback) { Object value = values.get(key); return value instanceof Integer number ? number : fallback; }
    @Override public long getLongOr(String key, long fallback) { Object value = values.get(key); return value instanceof Long number ? number : fallback; }
    @Override public float getFloatOr(String key, float fallback) { Object value = values.get(key); return value instanceof Float number ? number : fallback; }
    @Override public boolean getBooleanOr(String key, boolean fallback) { Object value = values.get(key); return value instanceof Boolean bool ? bool : fallback; }
    @Override public UUID getUUIDOrNull(String key) { Object value = values.get(key); return value instanceof UUID uuid ? uuid : null; }
    @Override public List<String> getStringListOrEmpty(String key) { Object value = values.get(key); return value instanceof List<?> list ? list.stream().filter(String.class::isInstance).map(String.class::cast).toList() : List.of(); }
    @Override public ItemStack getItemOrEmpty(String key) { Object value = values.get(key); return value instanceof ItemStack stack ? stack.copy() : ItemStack.EMPTY; }
    @Override public List<ItemStack> getItemListOrEmpty(String key, int size) {
        List<ItemStack> result = new ArrayList<>();
        Object value = values.get(key);
        List<?> stored = value instanceof ItemStackList itemList ? itemList.items : value instanceof List<?> list ? list : List.of();
        for (int i = 0; i < size; i++) result.add(i < stored.size() && stored.get(i) instanceof ItemStack stack ? stack.copy() : ItemStack.EMPTY);
        return result;
    }
    @Override public BlockState getBlockStateOrAir(String key) { Object value = values.get(key); return value instanceof BlockState state ? state : Blocks.AIR.defaultBlockState(); }
    @Override public Optional<IBlockEntityData> getSubData(String key) { Object value = values.get(key); return value instanceof BufferedBlockEntityData child ? Optional.of(child) : Optional.empty(); }
    @Override public boolean contains(String key) { return values.containsKey(key); }

    public void copyTo(IBlockEntityWriteData target) {
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String key = entry.getKey(); Object value = entry.getValue();
            if (value instanceof String string) target.putString(key, string);
            else if (value instanceof Integer number) target.putInt(key, number);
            else if (value instanceof Long number) target.putLong(key, number);
            else if (value instanceof Float number) target.putFloat(key, number);
            else if (value instanceof Boolean bool) target.putBoolean(key, bool);
            else if (value instanceof UUID uuid) target.putUUID(key, uuid);
            else if (value instanceof ItemStack stack) target.putItem(key, stack);
            else if (value instanceof BlockState state) target.putBlockState(key, state);
            else if (value instanceof BufferedBlockEntityData child) {
                IBlockEntityWriteData targetChild = target.createChild();
                child.copyTo(targetChild);
                target.putSubData(key, targetChild);
            } else if (value instanceof ItemStackList itemList) target.putItemList(key, itemList.items);
            else if (value instanceof List<?> list) target.putStringList(key, list.stream().filter(String.class::isInstance).map(String.class::cast).toList());
        }
    }

    private record ItemStackList(List<ItemStack> items) {}
}
