package com.kingodogo.buildscape.block.entity;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.UUID;
public interface IBlockEntityWriteData {
    void putString(String key, String value);
    void putInt(String key, int value);
    void putLong(String key, long value);
    void putFloat(String key, float value);
    void putBoolean(String key, boolean value);
    void putUUID(String key, UUID value);
    void putStringList(String key, List<String> list);
    void putItem(String key, ItemStack item);
    void putItemList(String key, List<ItemStack> items);
    void putBlockState(String key, BlockState state);
    void putSubData(String key, IBlockEntityWriteData data);
    IBlockEntityWriteData createChild();
    default void copyTo(IBlockEntityWriteData target) {}
}
