package com.kingodogo.buildscape.item;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class BuildersPouchInventory implements Container {
    private final ItemStack pouch;
    private final NonNullList<ItemStack> items = NonNullList.withSize(BuildersPouchItem.SLOT_COUNT, ItemStack.EMPTY);

    public BuildersPouchInventory(ItemStack pouch) {
        this.pouch = pouch;
        CompoundTag data = BuildersPouchItem.getData(pouch, false);
        if (data != null) ContainerHelper.loadAllItems(data, items);
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(items, slot, amount);
        if (!result.isEmpty()) setChanged();
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack result = ContainerHelper.takeItem(items, slot);
        if (!result.isEmpty()) setChanged();
        return result;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize());
        setChanged();
    }

    @Override
    public void setChanged() {
        CompoundTag data = BuildersPouchItem.getData(pouch, true);
        if (data != null) ContainerHelper.saveAllItems(data, items);
    }

    @Override
    public boolean stillValid(Player player) {
        return !pouch.isEmpty() && pouch.getItem() instanceof BuildersPouchItem;
    }

    @Override
    public void clearContent() {
        items.clear();
        setChanged();
    }
}
