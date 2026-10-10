package com.kingodogo.buildscape.adapter.v26x;

import com.kingodogo.buildscape.block.entity.IBlockEntityReadData;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
public class ValueInputData implements IBlockEntityReadData {
    private final ValueInput input;

    public ValueInputData(ValueInput input) {
        this.input = input;
    }

    @Override
    public String getStringOr(String key, String defaultValue) {
        return input.getStringOr(key, defaultValue);
    }

    @Override
    public int getIntOr(String key, int defaultValue) {
        return input.getIntOr(key, defaultValue);
    }

    @Override
    public long getLongOr(String key, long defaultValue) {
        return input.getLongOr(key, defaultValue);
    }

    @Override
    public float getFloatOr(String key, float defaultValue) {
        return input.getFloatOr(key, defaultValue);
    }

    @Override
    public boolean getBooleanOr(String key, boolean defaultValue) {
        return input.getBooleanOr(key, defaultValue);
    }

    @Override
    public UUID getUUIDOrNull(String key) {
        return input.read(key, UUIDUtil.CODEC).orElse(null);
    }

    @Override
    public List<String> getStringListOrEmpty(String key) {
        return input.list(key, com.mojang.serialization.Codec.STRING)
                .map(list -> {
                    List<String> result = new ArrayList<>();
                    list.forEach(result::add);
                    return result;
                }).orElseGet(ArrayList::new);
    }

    @Override
    public ItemStack getItemOrEmpty(String key) {
        return input.read(key, ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    public List<ItemStack> getItemListOrEmpty(String key, int size) {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < size; i++) items.add(ItemStack.EMPTY);
        input.childrenList(key).ifPresent(list -> list.forEach(child -> {
            int slot = child.getIntOr("Slot", -1);
            if (slot >= 0 && slot < size) items.set(slot, child.read("Item", ItemStack.CODEC).orElse(ItemStack.EMPTY));
        }));
        return items;
    }

    @Override
    public BlockState getBlockStateOrAir(String key) {
        return input.read(key, BlockState.CODEC).orElse(Blocks.AIR.defaultBlockState());
    }

    @Override
    public Optional<IBlockEntityReadData> getSubData(String key) {
        return input.child(key).map(ValueInputData::new);
    }

    @Override
    public boolean contains(String key) {
        return input.child(key).isPresent()
                || input.getString(key).isPresent()
                || input.getInt(key).isPresent()
                || input.getLong(key).isPresent()
                || input.read(key, com.mojang.serialization.Codec.FLOAT).isPresent()
                || input.read(key, com.mojang.serialization.Codec.BOOL).isPresent()
                || input.read(key, UUIDUtil.CODEC).isPresent()
                || input.read(key, ItemStack.CODEC).isPresent()
                || input.read(key, BlockState.CODEC).isPresent()
                || input.list(key, com.mojang.serialization.Codec.STRING).isPresent();
    }
}
