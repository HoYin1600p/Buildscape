package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.block.entity.IBlockEntityReadData;
import com.kingodogo.buildscape.block.entity.IBlockEntityWriteData;
import com.kingodogo.buildscape.block.entity.IDataSerializable;
import com.kingodogo.buildscape.block.entity.DataBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
public class SmokeVentBlockEntity extends DataBlockEntity implements IDataSerializable {
    private String smokeColor = null;
    private boolean active = true;

    public SmokeVentBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SMOKE_VENT_TYPE, pos, state);
    }
    public String getSmokeColor() {
        return smokeColor;
    }
    public void setSmokeColor(String color) {
        this.smokeColor = color;
        this.setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
        this.setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public void readData(IBlockEntityReadData data) {
        String color = data.getStringOr("SmokeColor", "");
        if (!color.isEmpty() && color.matches("^#[0-9A-Fa-f]{6}$")) {
            this.smokeColor = color.toUpperCase();
        } else {
            this.smokeColor = null;
        }
        this.active = data.getBooleanOr("Active", true);
    }

    @Override
    public void writeData(IBlockEntityWriteData data) {
        if (smokeColor != null && !smokeColor.isEmpty()) {
            data.putString("SmokeColor", smokeColor);
        }
        data.putBoolean("Active", active);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
