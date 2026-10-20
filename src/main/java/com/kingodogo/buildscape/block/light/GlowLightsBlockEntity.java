package com.kingodogo.buildscape.block.light;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

import com.kingodogo.buildscape.core.NbtKeys;
import com.kingodogo.buildscape.core.SyncedBlockEntity;
import com.kingodogo.buildscape.registry.ModBlockEntities;

public class GlowLightsBlockEntity extends SyncedBlockEntity {

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
                    3);
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
                    3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ListTag colorsList = new ListTag();
        for (String color : dyeColors) {
            colorsList.add(StringTag.valueOf(color));
        }
        tag.put(NbtKeys.DYE_COLORS, colorsList);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        dyeColors.clear();
        if (tag.contains(NbtKeys.DYE_COLORS, Tag.TAG_LIST)) {
            ListTag colorsList = tag.getList(
                    NbtKeys.DYE_COLORS,
                    Tag.TAG_STRING);
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
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }
}
