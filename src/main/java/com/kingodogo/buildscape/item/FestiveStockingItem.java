package com.kingodogo.buildscape.item;

import com.kingodogo.buildscape.block.FestiveStockingBlockEntity;
import com.kingodogo.buildscape.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public class FestiveStockingItem extends BlockItem {

    private final String colorVariant;

    public FestiveStockingItem(Properties properties, String color) {
        super(getBlockForColor(color), properties);
        this.colorVariant = color;
    }

    private static Block getBlockForColor(String color) {
        if (color == null) {
            return ModBlocks.FESTIVE_STOCKING.get();
        }
        switch (color) {
            case "black":
                return ModBlocks.BLACK_FESTIVE_STOCKING.get();
            case "blue":
                return ModBlocks.BLUE_FESTIVE_STOCKING.get();
            case "brown":
                return ModBlocks.BROWN_FESTIVE_STOCKING.get();
            case "cyan":
                return ModBlocks.CYAN_FESTIVE_STOCKING.get();
            case "gray":
                return ModBlocks.GRAY_FESTIVE_STOCKING.get();
            case "green":
                return ModBlocks.GREEN_FESTIVE_STOCKING.get();
            case "light_blue":
                return ModBlocks.LIGHT_BLUE_FESTIVE_STOCKING.get();
            case "light_gray":
                return ModBlocks.LIGHT_GRAY_FESTIVE_STOCKING.get();
            case "lime":
                return ModBlocks.LIME_FESTIVE_STOCKING.get();
            case "magenta":
                return ModBlocks.MAGENTA_FESTIVE_STOCKING.get();
            case "orange":
                return ModBlocks.ORANGE_FESTIVE_STOCKING.get();
            case "pink":
                return ModBlocks.PINK_FESTIVE_STOCKING.get();
            case "purple":
                return ModBlocks.PURPLE_FESTIVE_STOCKING.get();
            case "red":
                return ModBlocks.RED_FESTIVE_STOCKING.get();
            case "white":
                return ModBlocks.WHITE_FESTIVE_STOCKING.get();
            case "yellow":
                return ModBlocks.YELLOW_FESTIVE_STOCKING.get();
            default:
                return ModBlocks.FESTIVE_STOCKING.get();
        }
    }

    public String getColorVariant() {
        return colorVariant;
    }

    @Override
    protected boolean updateCustomBlockEntityTag(
            BlockPos pos,
            Level level,
            @Nullable net.minecraft.world.entity.player.Player player,
            ItemStack stack,
            BlockState state
    ) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof FestiveStockingBlockEntity) {
            FestiveStockingBlockEntity stockingEntity =
                    (FestiveStockingBlockEntity) be;
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains("StoredItem", 10)) {
                CompoundTag storedTag = tag.getCompound("StoredItem");
                if (!storedTag.isEmpty()) {
                    net.minecraft.world.item.ItemStack storedItem =
                            net.minecraft.world.item.ItemStack.of(storedTag);
                    stockingEntity.setStoredItem(storedItem, true);
                }
            }
        }
        return super.updateCustomBlockEntityTag(pos, level, player, stack, state);
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            @Nullable Level level,
            List<Component> tooltip,
            TooltipFlag flag
    ) {
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
