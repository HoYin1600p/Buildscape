package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import com.kingodogo.buildscape.block.entity.IBlockEntityReadData;
import com.kingodogo.buildscape.block.entity.IBlockEntityWriteData;
import com.kingodogo.buildscape.block.entity.IDataSerializable;
import com.kingodogo.buildscape.block.entity.DataBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
public class IcicleCauldronBlockEntity extends DataBlockEntity implements WorldlyContainer, IDataSerializable {

    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    private static final int[] SLOTS = new int[]{0};

    public IcicleCauldronBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
        super(ModBlockEntities.ICICLE_CAULDRON_TYPE, worldPosition, blockState);
    }
    public ItemStack getStoredIcicle() {
        return items.get(0);
    }
    public void setStoredIcicle(ItemStack stack) {
        boolean wasNotEmpty = !this.items.get(0).isEmpty();
        this.items.set(0, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
        this.setChanged();
        if (level != null && !level.isClientSide()) {
            if (this.items.get(0).isEmpty() && wasNotEmpty) {
                level.setBlock(worldPosition, Blocks.CAULDRON.defaultBlockState(), 3);
            } else if (!this.items.get(0).isEmpty()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    }
    public boolean hasIcicle() {
        return !items.get(0).isEmpty();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return direction == Direction.UP
                && IcicleCauldronBlock.isIcicleBlockItem(stack)
                && items.get(0).isEmpty();
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return hasIcicle();
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return items.get(0).isEmpty();
    }

    @Override
    public boolean stillValid(Player player) {
        if (level == null || level.getBlockEntity(worldPosition) != this) return false;
        return player.distanceToSqr((double) worldPosition.getX() + 0.5D, (double) worldPosition.getY() + 0.5D, (double) worldPosition.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot == 0 ? items.get(0) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (slot != 0 || items.get(0).isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack result = com.kingodogo.buildscape.platform.Services.PLATFORM.removeItem(this.items, slot, amount);
        if (!result.isEmpty()) {
            this.setChanged();
            if (level != null && !level.isClientSide()) {
                if (items.get(0).isEmpty()) {
                    level.setBlock(worldPosition, Blocks.CAULDRON.defaultBlockState(), 3);
                } else {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                }
            }
        }
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot != 0) return ItemStack.EMPTY;
        ItemStack result = items.get(0);
        items.set(0, ItemStack.EMPTY);
        this.setChanged();
        if (level != null && !level.isClientSide()) {
            level.setBlock(worldPosition, Blocks.CAULDRON.defaultBlockState(), 3);
        }
        return result;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == 0) {
            setStoredIcicle(stack);
        }
    }

    @Override
    public void clearContent() {
        this.items.set(0, ItemStack.EMPTY);
        this.setChanged();
        if (level != null && !level.isClientSide()) {
            level.setBlock(worldPosition, Blocks.CAULDRON.defaultBlockState(), 3);
        }
    }

    @Override
    public void readData(IBlockEntityReadData data) {
        this.items.set(0, data.getItemOrEmpty("Icicle"));
    }

    @Override
    public void writeData(IBlockEntityWriteData data) {
        ItemStack stored = this.items.get(0);
        if (!stored.isEmpty()) {
            data.putItem("Icicle", stored);
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void dropContents(net.minecraft.world.level.Level level, BlockPos pos) {
        if (!level.isClientSide() && hasIcicle()) {
            Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, getStoredIcicle().copy());
        }
    }
}
