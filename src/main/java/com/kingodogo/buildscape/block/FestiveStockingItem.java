package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.core.NbtKeys;
import com.kingodogo.buildscape.registry.DyeVariants;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import javax.annotation.Nullable;

public class FestiveStockingItem extends BlockItem {

    private final String colorVariant;

    public FestiveStockingItem(Properties properties, String color) {
        super(getBlockForColor(color), properties);
        this.colorVariant = color;
    }

    private static Block getBlockForColor(String color) {
        return DyeVariants.festiveStockingBlock(color);
    }

    public String getColorVariant() {
        return colorVariant;
    }

    @Override
    protected boolean updateCustomBlockEntityTag(
            BlockPos pos,
            Level level,
            @Nullable Player player,
            ItemStack stack,
            BlockState state) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof FestiveStockingBlockEntity) {
            FestiveStockingBlockEntity stockingEntity = (FestiveStockingBlockEntity) be;
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains(NbtKeys.STORED_ITEM, 10)) {
                CompoundTag storedTag = tag.getCompound(NbtKeys.STORED_ITEM);
                if (!storedTag.isEmpty()) {
                    ItemStack storedItem = ItemStack.of(storedTag);
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
            TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
