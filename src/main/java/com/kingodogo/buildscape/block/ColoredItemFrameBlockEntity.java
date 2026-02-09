package com.kingodogo.buildscape.block;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ColoredItemFrameBlockEntity extends BlockEntity {

    private ItemStack displayedItem = ItemStack.EMPTY;
    private int rotation = 0;

    public ColoredItemFrameBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COLORED_ITEM_FRAME_BLOCK_ENTITY.get(), pos, state);
    }

    public ItemStack getDisplayedItem() {
        return displayedItem;
    }

    public void setDisplayedItem(ItemStack item) {
        this.displayedItem = item.copy();
        this.rotation = 0;
        this.setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(
                    worldPosition, getBlockState(), getBlockState(), 3
            );
        }
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation % 8;
        this.setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(
                    worldPosition, getBlockState(), getBlockState(), 3
            );
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!displayedItem.isEmpty()) {
            CompoundTag itemTag = new CompoundTag();
            displayedItem.save(itemTag);
            tag.put("DisplayedItem", itemTag);
        }
        tag.putInt("Rotation", rotation);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("DisplayedItem")) {
            displayedItem = ItemStack.of(tag.getCompound("DisplayedItem"));
        } else {
            displayedItem = ItemStack.EMPTY;
        }
        rotation = tag.getInt("Rotation");
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }
}
