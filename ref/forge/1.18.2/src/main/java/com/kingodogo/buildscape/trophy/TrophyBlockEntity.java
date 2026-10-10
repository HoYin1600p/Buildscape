package com.kingodogo.buildscape.trophy;

import com.kingodogo.buildscape.block.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class TrophyBlockEntity extends BlockEntity {
    private String obtainedBy = "";
    private String obtainedOn = "";

    public TrophyBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TROPHY_BLOCK_ENTITY.get(), pos, state);
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
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("ObtainedBy")) {
            this.obtainedBy = tag.getString("ObtainedBy");
        }
        if (tag.contains("ObtainedOn")) {
            this.obtainedOn = tag.getString("ObtainedOn");
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!this.obtainedBy.isEmpty()) {
            tag.putString("ObtainedBy", this.obtainedBy);
        }
        if (!this.obtainedOn.isEmpty()) {
            tag.putString("ObtainedOn", this.obtainedOn);
        }
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
}
