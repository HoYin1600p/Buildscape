package com.kingodogo.buildscape.feature.trophy;

import com.kingodogo.buildscape.core.NbtKeys;
import com.kingodogo.buildscape.core.SyncedBlockEntity;
import com.kingodogo.buildscape.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class TrophyBlockEntity extends SyncedBlockEntity {
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
        if (tag.contains(NbtKeys.OBTAINED_BY)) {
            this.obtainedBy = tag.getString(NbtKeys.OBTAINED_BY);
        }
        if (tag.contains(NbtKeys.OBTAINED_ON)) {
            this.obtainedOn = tag.getString(NbtKeys.OBTAINED_ON);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!this.obtainedBy.isEmpty()) {
            tag.putString(NbtKeys.OBTAINED_BY, this.obtainedBy);
        }
        if (!this.obtainedOn.isEmpty()) {
            tag.putString(NbtKeys.OBTAINED_ON, this.obtainedOn);
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }
}
