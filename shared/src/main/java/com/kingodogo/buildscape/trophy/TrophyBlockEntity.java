package com.kingodogo.buildscape.trophy;

import com.kingodogo.buildscape.block.ModBlockEntities;
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
public class TrophyBlockEntity extends DataBlockEntity implements IDataSerializable {
    private String obtainedBy = "";
    private String obtainedOn = "";

    public TrophyBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TROPHY_TYPE, pos, state);
    }

    public String getObtainedBy() {
        return obtainedBy;
    }

    public void setObtainedBy(String obtainedBy) {
        this.obtainedBy = obtainedBy != null ? obtainedBy : "";
        setChanged();
    }

    public String getObtainedOn() {
        return obtainedOn;
    }

    public void setObtainedOn(String obtainedOn) {
        this.obtainedOn = obtainedOn != null ? obtainedOn : "";
        setChanged();
    }

    @Override
    public void readData(IBlockEntityReadData data) {
        this.obtainedBy = data.getStringOr("ObtainedBy", "");
        this.obtainedOn = data.getStringOr("ObtainedOn", "");
    }

    @Override
    public void writeData(IBlockEntityWriteData data) {
        if (!this.obtainedBy.isEmpty()) {
            data.putString("ObtainedBy", this.obtainedBy);
        }
        if (!this.obtainedOn.isEmpty()) {
            data.putString("ObtainedOn", this.obtainedOn);
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
