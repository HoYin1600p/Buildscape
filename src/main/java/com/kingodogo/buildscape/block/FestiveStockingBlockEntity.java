package com.kingodogo.buildscape.block;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FestiveStockingBlockEntity
        extends BlockEntity
        implements WorldlyContainer {

    private ItemStack storedItem = ItemStack.EMPTY;

    private static final int[] SLOTS = new int[]{0};

    public FestiveStockingBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FESTIVE_STOCKING_BLOCK_ENTITY.get(), pos, state);
    }

    public ItemStack getStoredItem() {
        return storedItem;
    }

    public void setStoredItem(ItemStack stack) {
        setStoredItem(stack, true);
    }

    public void setStoredItem(ItemStack stack, boolean sendUpdate) {
        this.storedItem = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
        this.setChanged();
        if (sendUpdate && level != null && !level.isClientSide) {
            level.sendBlockUpdated(
                    worldPosition,
                    getBlockState(),
                    getBlockState(),
                    3
            );
        }
    }

    public boolean isEmpty() {
        return storedItem.isEmpty();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(
            int slot,
            ItemStack stack,
            @Nullable Direction direction
    ) {
        if (stack.getItem() instanceof net.minecraft.world.item.BlockItem blockItem) {
            if (blockItem.getBlock() instanceof FestiveStockingBlock) {
                return false;
            }
        }

        if (storedItem.isEmpty()) {
            return true;
        }
        return (
                storedItem.sameItem(stack) &&
                        storedItem.getCount() < storedItem.getMaxStackSize()
        );
    }

    @Override
    public boolean canTakeItemThroughFace(
            int slot,
            ItemStack stack,
            Direction direction
    ) {
        return !storedItem.isEmpty();
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot == 0 ? storedItem : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (slot != 0 || storedItem.isEmpty()) {
            return ItemStack.EMPTY;
        }

        int toRemove = Math.min(amount, storedItem.getCount());
        ItemStack result = storedItem.copy();
        result.setCount(toRemove);
        storedItem.shrink(toRemove);

        if (storedItem.isEmpty()) {
            storedItem = ItemStack.EMPTY;
        }

        setChanged();
        if (level != null && !level.isClientSide) {
            level.gameEvent(
                    null,
                    net.minecraft.world.level.gameevent.GameEvent.BLOCK_CHANGE,
                    worldPosition
            );
            level.sendBlockUpdated(
                    worldPosition,
                    getBlockState(),
                    getBlockState(),
                    3
            );
        }
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot != 0 || storedItem.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack result = storedItem.copy();
        storedItem = ItemStack.EMPTY;
        return result;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == 0) {
            storedItem = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
            setChanged();
            if (level != null && !level.isClientSide) {
                level.gameEvent(
                        null,
                        net.minecraft.world.level.gameevent.GameEvent.BLOCK_CHANGE,
                        worldPosition
                );
                level.sendBlockUpdated(
                        worldPosition,
                        getBlockState(),
                        getBlockState(),
                        3
                );
            }
        }
    }

    @Override
    public void clearContent() {
        storedItem = ItemStack.EMPTY;
        setChanged();
    }

    public int getComparatorOutput() {
        if (storedItem.isEmpty()) {
            return 0;
        }
        float fillRatio =
                (float) storedItem.getCount() / (float) storedItem.getMaxStackSize();
        return Math.max(1, (int) Math.ceil(fillRatio * 15.0f));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("StoredItem", 10)) {
            this.storedItem = ItemStack.of(tag.getCompound("StoredItem"));
        } else {
            this.storedItem = ItemStack.EMPTY;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!storedItem.isEmpty()) {
            tag.put("StoredItem", storedItem.save(new CompoundTag()));
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.put(
                "StoredItem",
                storedItem.isEmpty()
                        ? ItemStack.EMPTY.save(new CompoundTag())
                        : storedItem.save(new CompoundTag())
        );
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
