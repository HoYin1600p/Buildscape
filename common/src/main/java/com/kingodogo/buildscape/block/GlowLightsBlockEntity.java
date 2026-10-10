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

import java.util.ArrayList;
import java.util.List;
public class GlowLightsBlockEntity extends DataBlockEntity implements IDataSerializable {

    public static final int MAX_DYE_COLORS = 5;
    private final List<String> dyeColors = new ArrayList<>();

    public GlowLightsBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GLOW_LIGHTS_TYPE, pos, state);
    }

    public boolean addDyeColor(String colorCode) {
        if (dyeColors.size() >= MAX_DYE_COLORS || dyeColors.contains(colorCode)) {
            return false;
        }
        dyeColors.add(colorCode);
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
        return true;
    }

    public List<String> getDyeColors() {
        return new ArrayList<>(dyeColors);
    }

    public int getDyeColorCount() {
        return dyeColors.size();
    }

    public boolean canAddMoreColors() {
        return dyeColors.size() < MAX_DYE_COLORS;
    }

    public void clearColors() {
        dyeColors.clear();
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public void readData(IBlockEntityReadData data) {
        this.dyeColors.clear();
        for (String color : data.getStringListOrEmpty("DyeColors")) {
            if (color != null && !color.isEmpty()) {
                this.dyeColors.add(color);
            }
        }
    }

    @Override
    public void writeData(IBlockEntityWriteData data) {
        if (!this.dyeColors.isEmpty()) {
            data.putStringList("DyeColors", this.dyeColors);
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
