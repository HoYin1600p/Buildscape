package com.kingodogo.buildscape.block.entity;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
public interface IBlockEntityReadData {
    String getStringOr(String key, String defaultValue);
    int getIntOr(String key, int defaultValue);
    long getLongOr(String key, long defaultValue);
    float getFloatOr(String key, float defaultValue);
    boolean getBooleanOr(String key, boolean defaultValue);
    UUID getUUIDOrNull(String key);
    List<String> getStringListOrEmpty(String key);
    ItemStack getItemOrEmpty(String key);
    List<ItemStack> getItemListOrEmpty(String key, int size);
    BlockState getBlockStateOrAir(String key);
    Optional<? extends IBlockEntityReadData> getSubData(String key);
    boolean contains(String key);
    Optional<Double> getDouble(String key);
    Optional<int[]> getIntArray(String key);
    default Optional<Integer> getInt(String key) {
        return contains(key) ? Optional.of(getIntOr(key, 0)) : Optional.empty();
    }
    default Optional<Boolean> getBoolean(String key) {
        return contains(key) ? Optional.of(getBooleanOr(key, false)) : Optional.empty();
    }
    default ItemStack getSavedItemOrEmpty(String key) { return getItemOrEmpty(key); }
    default List<ItemStack> getSavedItemListOrEmpty(String key, int size) { return getItemListOrEmpty(key, size); }
}
