package com.kingodogo.buildscape.item;

import com.kingodogo.buildscape.block.FestiveStockingBlockEntity;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class FestiveStockingItem extends BlockItem {

    private final String colorVariant;

    public FestiveStockingItem(Block block, Properties properties, String colorVariant) {
        super(block, properties);
        this.colorVariant = colorVariant;
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
            BlockState state
    ) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof FestiveStockingBlockEntity stockingEntity) {
            CompoundTag tag = Services.PLATFORM.getCustomData(stack, false);
            if (tag != null && tag.contains("StoredItem")) {
                CompoundTag storedTag = Services.PLATFORM.getTagCompound(tag, "StoredItem");
                if (storedTag != null && !storedTag.isEmpty()) {
                    ItemStack storedItem = Services.PLATFORM.loadSingleItemStack(storedTag);
                    if (!storedItem.isEmpty()) {
                        stockingEntity.setStoredItem(storedItem, true);
                    }
                }
            }
        }
        return super.updateCustomBlockEntityTag(pos, level, player, stack, state);
    }
}
