package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SmokeVentBlockEntity extends BlockEntity {

    private String smokeColor = null; // hex color string e.g. "#FF0000", null = default gray smoke
    private boolean active = true;

    public SmokeVentBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SMOKE_VENT_BLOCK_ENTITY.get(), pos, state);
    }

    public String getSmokeColor() {
        return smokeColor;
    }

    public void setSmokeColor(String color) {
        this.smokeColor = color;
        this.setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        this.setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        if (tag.contains("SmokeColor", 8)) {
            String color = tag.getString("SmokeColor");
            if (color != null && !color.isEmpty() && color.matches("^#[0-9A-Fa-f]{6}$")) {
                this.smokeColor = color.toUpperCase();
            } else {
                this.smokeColor = null;
            }
        } else {
            this.smokeColor = null;
        }

        if (tag.contains("Active", 1)) {
            this.active = tag.getBoolean("Active");
        } else {
            this.active = true;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        if (smokeColor != null && !smokeColor.isEmpty()) {
            tag.putString("SmokeColor", smokeColor);
        }

        tag.putBoolean("Active", active);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
