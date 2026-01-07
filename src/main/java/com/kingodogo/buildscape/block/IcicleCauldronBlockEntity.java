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

public class IcicleCauldronBlockEntity
        extends BlockEntity
        implements WorldlyContainer {

    private ItemStack storedIcicle = ItemStack.EMPTY;

    private static final int[] SLOTS = new int[]{0};

    public IcicleCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ICICLE_CAULDRON_BLOCK_ENTITY.get(), pos, state);
    }

    public ItemStack getStoredIcicle() {
        return storedIcicle;
    }

    public void setStoredIcicle(ItemStack stack) {
        boolean wasEmpty = this.storedIcicle.isEmpty();
        this.storedIcicle = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
        this.setChanged();
        if (level != null && !level.isClientSide) {
            if (this.storedIcicle.isEmpty() && !wasEmpty) {
                convertToRegularCauldron();
            } else if (!this.storedIcicle.isEmpty()) {
                level.sendBlockUpdated(
                        worldPosition,
                        getBlockState(),
                        getBlockState(),
                        3
                );
            }
        }
    }

    private void convertToRegularCauldron() {
        if (level != null && !level.isClientSide) {
            level.setBlock(
                    worldPosition,
                    net.minecraft.world.level.block.Blocks.CAULDRON.defaultBlockState(),
                    3
            );
        }
    }

    public boolean hasIcicle() {
        return !storedIcicle.isEmpty();
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
        if (direction != Direction.UP) {
            return false;
        }
        if (
                stack.getItem() !=
                        com.kingodogo.buildscape.item.ModItems.ICICLE_BLOCK.get()
        ) {
            return false;
        }
        return storedIcicle.isEmpty();
    }

    @Override
    public boolean canTakeItemThroughFace(
            int slot,
            ItemStack stack,
            @Nullable Direction direction
    ) {
        return !storedIcicle.isEmpty();
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return storedIcicle.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot == 0 ? storedIcicle : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (slot == 0 && !storedIcicle.isEmpty()) {
            ItemStack result = storedIcicle.split(amount);
            if (storedIcicle.isEmpty()) {
                storedIcicle = ItemStack.EMPTY;
                this.setChanged();
                if (level != null && !level.isClientSide) {
                    convertToRegularCauldron();
                }
            } else {
                this.setChanged();
            }
            return result;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot == 0) {
            ItemStack result = storedIcicle;
            storedIcicle = ItemStack.EMPTY;
            this.setChanged();
            if (level != null && !level.isClientSide) {
                convertToRegularCauldron();
            }
            return result;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == 0) {
            setStoredIcicle(stack);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        if (level == null || level.getBlockEntity(worldPosition) != this) {
            return false;
        }
        return (
                player.distanceToSqr(
                        (double) worldPosition.getX() + 0.5D,
                        (double) worldPosition.getY() + 0.5D,
                        (double) worldPosition.getZ() + 0.5D
                ) <=
                        64.0D
        );
    }

    @Override
    public void clearContent() {
        storedIcicle = ItemStack.EMPTY;
        this.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!storedIcicle.isEmpty()) {
            tag.put("Icicle", storedIcicle.save(new CompoundTag()));
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Icicle")) {
            storedIcicle = ItemStack.of(tag.getCompound("Icicle"));
        } else {
            storedIcicle = ItemStack.EMPTY;
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }
}
