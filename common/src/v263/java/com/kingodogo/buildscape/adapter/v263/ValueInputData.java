package com.kingodogo.buildscape.adapter.v263;

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

    @Override public Optional<Double> getDouble(String key) { return input.read(key, com.mojang.serialization.Codec.DOUBLE); }
    @Override public Optional<int[]> getIntArray(String key) { return input.getIntArray(key); }
    @Override public Optional<Integer> getInt(String key) { return input.getInt(key); }
    @Override public Optional<Boolean> getBoolean(String key) { return input.read(key, com.mojang.serialization.Codec.BOOL); }

    @Override public ItemStack getSavedItemOrEmpty(String key) {
        return input.child(key).map(ValueInputData::readSavedItem).orElse(ItemStack.EMPTY);
    }

    @Override public List<ItemStack> getSavedItemListOrEmpty(String key, int size) {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < size; i++) items.add(ItemStack.EMPTY);
        for (ValueInput child : input.childrenListOrEmpty(key)) {
            int slot = child.getByteOr("Slot", (byte) -1) & 255;
            if (slot < size) items.set(slot, readSavedItem(child));
        }
        return items;
    }

    public static ItemStack readSavedItem(ValueInput input) {
        if (input.getInt("Count").isEmpty()) {
            return input.read(ItemStack.MAP_CODEC).orElse(ItemStack.EMPTY);
        }
        // Custom block entity IDs are not in vanilla's schema: update legacy stacks explicitly.
        net.minecraft.nbt.CompoundTag legacy = new net.minecraft.nbt.CompoundTag();
        legacy.putString("id", input.getStringOr("id", "minecraft:air"));
        legacy.putByte("Count", input.getByteOr("Count", (byte) 0));
        input.read("tag", net.minecraft.nbt.CompoundTag.CODEC).ifPresent(tag -> legacy.put("tag", tag));
        var fixed = net.minecraft.util.datafix.DataFixers.getDataFixer().update(
                net.minecraft.util.datafix.fixes.References.ITEM_STACK,
                new com.mojang.serialization.Dynamic<>(net.minecraft.nbt.NbtOps.INSTANCE, legacy),
                2975, net.minecraft.SharedConstants.getCurrentVersion().dataVersion().version());
        return ItemStack.CODEC.parse(input.lookup().createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE),
                fixed.getValue()).result().orElse(ItemStack.EMPTY);
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
        if (input.child(key).flatMap(child -> child.getString("Name")).isPresent()) {
            net.minecraft.nbt.CompoundTag state = input.read(key, net.minecraft.nbt.CompoundTag.CODEC)
                    .orElseGet(net.minecraft.nbt.CompoundTag::new).copy();
            state.putString("id", state.getStringOr("Name", "minecraft:air"));
            state.remove("Name");
            if (state.get("Properties") != null) {
                state.put("properties", state.get("Properties"));
            }
            state.remove("Properties");
            return BlockState.FULL_CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE, state)
                    .result().orElse(Blocks.AIR.defaultBlockState());
        }
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
