package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import com.kingodogo.buildscape.block.entity.IBlockEntityReadData;
import com.kingodogo.buildscape.block.entity.IBlockEntityWriteData;
import com.kingodogo.buildscape.block.entity.IDataSerializable;
import com.kingodogo.buildscape.block.entity.DataBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
public class ShelfBlockEntity extends DataBlockEntity implements Container, IDataSerializable {
    public static final int MAX_ITEMS = 3;
    private final NonNullList<ItemStack> items = NonNullList.withSize(MAX_ITEMS, ItemStack.EMPTY);
    private boolean alignItemsToBottom = false;

    public ShelfBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
        super(ModBlockEntities.SHELF_TYPE, worldPosition, blockState);
    }

    @Override
    public void readData(IBlockEntityReadData data) {
        this.items.clear();
        java.util.List<ItemStack> loaded = data.getItemListOrEmpty("Items", MAX_ITEMS);
        for (int i = 0; i < MAX_ITEMS; i++) this.items.set(i, loaded.get(i));
        this.alignItemsToBottom = data.getBooleanOr("align_items_to_bottom", false);
    }

    @Override
    public void writeData(IBlockEntityWriteData data) {
        data.putItemList("Items", this.items);
        data.putBoolean("align_items_to_bottom", this.alignItemsToBottom);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
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
        ItemStack itemstack = com.kingodogo.buildscape.platform.Services.PLATFORM.removeItem(this.items, slot, amount);
        if (!itemstack.isEmpty()) {
            this.setChanged();
        }
        return itemstack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack itemstack = com.kingodogo.buildscape.platform.Services.PLATFORM.takeItem(this.items, slot);
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
        if (this.level == null || this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) <= 64.0D;
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

    public void dropContents(BlockPos pos) {
        if (this.level != null) {
            Containers.dropContents(this.level, pos, this);
        }
    }
    public float getVisualRotationYInDegrees() {
        return ((Direction) this.getBlockState().getValue(ShelfBlock.FACING)).getOpposite().toYRot();
    }
    public boolean getAlignItemsToBottom() {
        return this.alignItemsToBottom;
    }
}
