package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.state.BlockState;
public class TrappedDecoratedPotBlockEntity extends DecoratedPotBlockEntity {

    public TrappedDecoratedPotBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
        super(ModBlockEntities.TRAPPED_DECORATED_POT_TYPE, worldPosition, blockState);
    }
    public static int maxStoredCount(ItemStack stack) {
        return com.kingodogo.buildscape.platform.Services.PLATFORM.isSpawnEgg(stack) ? 1 : stack.getMaxStackSize();
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction direction) {
        if (stack.getItem() instanceof net.minecraft.world.item.BlockItem blockItem) {
            if (blockItem.getBlock() instanceof TrappedDecoratedPotBlock) {
                return false;
            }
        }
        ItemStack stored = getStoredItem();
        return direction != Direction.DOWN && (stored.isEmpty() || (com.kingodogo.buildscape.platform.Services.PLATFORM.isSameItemSameComponents(stored, stack) && stored.getCount() < maxStoredCount(stored)));
    }

    public void dropContents(net.minecraft.world.level.Level level, BlockPos pos) {
        if (!level.isClientSide() && !this.isEmpty()) {
            ItemStack stored = this.getStoredItem().copy();
            if (com.kingodogo.buildscape.platform.Services.PLATFORM.isSpawnEgg(stored) && level instanceof ServerLevel serverLevel) {
                com.kingodogo.buildscape.platform.Services.PLATFORM.spawnMobFromEgg(serverLevel, pos, stored);
            } else {
                if (com.kingodogo.buildscape.platform.Services.isVersionCluster("v121x")) {
                    net.minecraft.world.entity.item.ItemEntity itemEntity = new net.minecraft.world.entity.item.ItemEntity(
                            level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stored);
                    itemEntity.setDefaultPickUpDelay();
                    level.addFreshEntity(itemEntity);
                } else {
                    Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stored);
                }
            }
        }
    }
}
