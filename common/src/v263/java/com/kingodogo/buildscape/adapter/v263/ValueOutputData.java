package com.kingodogo.buildscape.adapter.v263;

import com.kingodogo.buildscape.block.entity.BufferedBlockEntityData;
import com.kingodogo.buildscape.block.entity.IBlockEntityWriteData;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.List;
import java.util.UUID;
public class ValueOutputData implements IBlockEntityWriteData {
    private final ValueOutput output;

    public ValueOutputData(ValueOutput output) {
        this.output = output;
    }

    @Override public void putDouble(String key, double value) { output.putDouble(key, value); }
    @Override public void putIntArray(String key, int[] value) { output.putIntArray(key, value); }

    @Override public void putSavedItemList(String key, List<ItemStack> items) {
        var list = output.childrenList(key);
        for (int slot = 0; slot < items.size(); slot++) {
            ItemStack item = items.get(slot);
            if (item != null && !item.isEmpty()) {
                var child = list.addChild();
                child.putByte("Slot", (byte) slot);
                child.store(ItemStack.MAP_CODEC, item);
            }
        }
    }

    @Override
    public void putString(String key, String value) {
        if (value != null) {
            output.putString(key, value);
        }
    }

    @Override
    public void putInt(String key, int value) {
        output.putInt(key, value);
    }

    @Override
    public void putLong(String key, long value) {
        output.putLong(key, value);
    }

    @Override
    public void putFloat(String key, float value) {
        output.putFloat(key, value);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        output.putBoolean(key, value);
    }

    @Override
    public void putUUID(String key, UUID value) {
        if (value != null) {
            output.store(key, UUIDUtil.CODEC, value);
        }
    }

    @Override
    public void putStringList(String key, List<String> list) {
        if (list != null) {
            var typedList = output.list(key, com.mojang.serialization.Codec.STRING);
            for (String s : list) {
                typedList.add(s);
            }
        }
    }

    @Override
    public void putItem(String key, ItemStack item) {
        if (item != null && !item.isEmpty()) {
            output.store(key, ItemStack.CODEC, item);
        }
    }

    @Override
    public void putItemList(String key, List<ItemStack> items) {
        ValueOutput.ValueOutputList list = output.childrenList(key);
        for (int slot = 0; slot < items.size(); slot++) {
            ItemStack stack = items.get(slot);
            if (stack != null && !stack.isEmpty()) {
                ValueOutput child = list.addChild();
                child.putInt("Slot", slot);
                child.store("Item", ItemStack.CODEC, stack);
            }
        }
    }

    @Override
    public void putBlockState(String key, BlockState state) {
        if (state != null) {
            // Keep the reference's compound format, including default block states.
            BlockState.FULL_CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE, state).result().ifPresent(encoded -> {
                net.minecraft.nbt.CompoundTag tag = ((net.minecraft.nbt.CompoundTag) encoded).copy();
                tag.putString("Name", tag.getStringOr("id", "minecraft:air"));
                tag.remove("id");
                if (tag.get("properties") != null) {
                    tag.put("Properties", tag.get("properties"));
                }
                tag.remove("properties");
                output.store(key, net.minecraft.nbt.CompoundTag.CODEC, tag);
            });
        }
    }

    @Override
    public void putSubData(String key, IBlockEntityWriteData data) {
        if (data != null) {
            data.copyTo(new ValueOutputData(output.child(key)));
        }
    }

    @Override
    public IBlockEntityWriteData createChild() {
        return new BufferedBlockEntityData();
    }
}
