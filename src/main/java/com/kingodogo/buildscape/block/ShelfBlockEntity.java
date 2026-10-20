package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.core.SyncedBlockEntity;
import com.kingodogo.buildscape.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class ShelfBlockEntity extends SyncedBlockEntity implements Container {
    public static final int MAX_ITEMS = 3;
    private final NonNullList<ItemStack> items = NonNullList.withSize(MAX_ITEMS, ItemStack.EMPTY);
    private boolean alignItemsToBottom;

    public ShelfBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
        super(ModBlockEntities.SHELF.get(), worldPosition, blockState);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.items.clear();
        ContainerHelper.loadAllItems(tag, this.items);
        this.alignItemsToBottom = tag.getBoolean("align_items_to_bottom");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.items);
        tag.putBoolean("align_items_to_bottom", this.alignItemsToBottom);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    public int getContainerSize() {
        return MAX_ITEMS;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack itemstack = ContainerHelper.removeItem(this.items, slot, amount);
        if (!itemstack.isEmpty()) {
            this.setChanged();
        }
        return itemstack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack itemstack = ContainerHelper.takeItem(this.items, slot);
        this.setChanged();
        return itemstack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.items.set(slot, limitToMaxStackSize(stack, this.getMaxStackSize()));
        this.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return player.distanceToSqr((double) this.worldPosition.getX() + 0.5,
                    (double) this.worldPosition.getY() + 0.5, (double) this.worldPosition.getZ() + 0.5) <= 64.0;
        }
    }

    @Override
    public void clearContent() {
        this.items.clear();
        this.setChanged();
    }

    public ItemStack swapItemNoUpdate(final int slot, final ItemStack newStack) {
        ItemStack retrievedItem = this.items.get(slot);
        this.items.set(slot, limitToMaxStackSize(newStack.copy(), this.getMaxStackSize()));
        return retrievedItem;
    }

    private static ItemStack limitToMaxStackSize(final ItemStack stack, final int containerMaxStackSize) {
        if (!stack.isEmpty()) {
            int max = Math.min(containerMaxStackSize, stack.getMaxStackSize());
            if (stack.getCount() > max) {
                stack.setCount(max);
            }
        }
        return stack;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public float getVisualRotationYInDegrees() {
        return ((Direction) this.getBlockState().getValue(ShelfBlock.FACING)).getOpposite().toYRot();
    }

    public boolean getAlignItemsToBottom() {
        return this.alignItemsToBottom;
    }
}
