package com.kingodogo.buildscape.item;

import com.kingodogo.buildscape.block.FestiveStockingBlockEntity;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
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

    // 26.3 made BlockItem.updateCustomBlockEntityTag static, so it can no longer be overridden.
    // BlockItem.place calls placeBlock right before it, with nothing in between that touches the
    // block entity, so restoring the stored item here keeps the 26.2 order (stored item first,
    // then the block entity data component is applied on top).
    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState placedState) {
        boolean placed = super.placeBlock(context, placedState);
        if (placed) {
            Level level = context.getLevel();
            BlockPos pos = context.getClickedPos();
            if (level.getBlockState(pos).is(this.getBlock())) {
                restoreStoredItem(pos, level, context.getItemInHand());
            }
        }
        return placed;
    }

    private void restoreStoredItem(BlockPos pos, Level level, ItemStack stack) {
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
    }
}
