package com.kingodogo.buildscape.block;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GlowLightsBlockEntity extends BlockEntity {

    public static final int MAX_DYE_COLORS = 5;
    private final List<String> dyeColors = new ArrayList<>();

    public GlowLightsBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GLOW_LIGHTS_BLOCK_ENTITY.get(), pos, state);
    }

    public boolean addDyeColor(String colorCode) {
        if (dyeColors.size() >= MAX_DYE_COLORS) {
            return false;
        }
        if (dyeColors.contains(colorCode)) {
            return false;
        }

        dyeColors.add(colorCode);
        this.setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(
                    worldPosition,
                    getBlockState(),
                    getBlockState(),
                    3
            );
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
        this.setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(
                    worldPosition,
                    getBlockState(),
                    getBlockState(),
                    3
            );
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        net.minecraft.nbt.ListTag colorsList = new net.minecraft.nbt.ListTag();
        for (String color : dyeColors) {
            colorsList.add(net.minecraft.nbt.StringTag.valueOf(color));
        }
        tag.put("DyeColors", colorsList);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        dyeColors.clear();
        if (tag.contains("DyeColors", net.minecraft.nbt.Tag.TAG_LIST)) {
            net.minecraft.nbt.ListTag colorsList = tag.getList(
                    "DyeColors",
                    net.minecraft.nbt.Tag.TAG_STRING
            );
            for (int i = 0; i < colorsList.size(); i++) {
                String color = colorsList.getString(i);
                if (color != null && !color.isEmpty()) {
                    dyeColors.add(color);
                }
            }
        }
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

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }
}
