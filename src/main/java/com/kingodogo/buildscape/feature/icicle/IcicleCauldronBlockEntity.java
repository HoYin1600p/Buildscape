package com.kingodogo.buildscape.feature.icicle;

import com.kingodogo.buildscape.core.NbtKeys;
import com.kingodogo.buildscape.core.SyncedBlockEntity;
import com.kingodogo.buildscape.registry.ModBlockEntities;
import com.kingodogo.buildscape.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class IcicleCauldronBlockEntity
        extends SyncedBlockEntity
        implements WorldlyContainer {

    private ItemStack storedIcicle = ItemStack.EMPTY;

    private static final int[] SLOTS = new int[]{0};

    private final LazyOptional<IItemHandlerModifiable>[] itemHandlers = SidedInvWrapper.create(this,
            Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);

    public IcicleCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ICICLE_CAULDRON_BLOCK_ENTITY.get(), pos, state);
    }

    public ItemStack getStoredIcicle() {
        return storedIcicle;
    }

    public void setStoredIcicle(ItemStack stack) {
        boolean wasEmpty = this.storedIcicle.isEmpty();
        this.storedIcicle = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
        this.setChanged();
        if (level != null && !level.isClientSide) {
            if (this.storedIcicle.isEmpty() && !wasEmpty) {
                scheduleEmptyCheck();
            } else if (!this.storedIcicle.isEmpty()) {
                level.sendBlockUpdated(
                        worldPosition,
                        getBlockState(),
                        getBlockState(),
                        3
                );
            }
        }
    }

    private void scheduleEmptyCheck() {
        if (level != null && !level.isClientSide) {
            level.scheduleTick(worldPosition, getBlockState().getBlock(), 1);
        }
    }

    public void convertToRegularCauldron() {
        if (level != null && !level.isClientSide
                && level.getBlockEntity(worldPosition) == this && storedIcicle.isEmpty()) {
            level.setBlock(
                    worldPosition,
                    Blocks.CAULDRON.defaultBlockState(),
                    3
            );
        }
    }

    public boolean hasIcicle() {
        return !storedIcicle.isEmpty();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(
            int slot,
            ItemStack stack,
            @Nullable Direction direction
    ) {
        if (direction != Direction.UP) {
            return false;
        }
        if (
                stack.getItem() !=
                        ModItems.ICICLE_BLOCK.get()
        ) {
            return false;
        }
        return storedIcicle.isEmpty();
    }

    @Override
    public boolean canTakeItemThroughFace(
            int slot,
            ItemStack stack,
            @Nullable Direction direction
    ) {
        return !storedIcicle.isEmpty();
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return storedIcicle.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot == 0 ? storedIcicle : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (slot == 0 && !storedIcicle.isEmpty()) {
            ItemStack result = storedIcicle.split(amount);
            if (storedIcicle.isEmpty()) {
                storedIcicle = ItemStack.EMPTY;
                this.setChanged();
                if (level != null && !level.isClientSide) {
                    scheduleEmptyCheck();
                }
            } else {
                this.setChanged();
            }
            return result;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot == 0) {
            ItemStack result = storedIcicle;
            storedIcicle = ItemStack.EMPTY;
            this.setChanged();
            if (level != null && !level.isClientSide) {
                scheduleEmptyCheck();
            }
            return result;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == 0) {
            setStoredIcicle(stack);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        if (level == null || level.getBlockEntity(worldPosition) != this) {
            return false;
        }
        return (
                player.distanceToSqr(
                        (double) worldPosition.getX() + 0.5D,
                        (double) worldPosition.getY() + 0.5D,
                        (double) worldPosition.getZ() + 0.5D
                ) <=
                        64.0D
        );
    }

    @Override
    public void clearContent() {
        storedIcicle = ItemStack.EMPTY;
        this.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!storedIcicle.isEmpty()) {
            tag.put(NbtKeys.ICICLE, storedIcicle.save(new CompoundTag()));
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(NbtKeys.ICICLE)) {
            storedIcicle = ItemStack.of(tag.getCompound(NbtKeys.ICICLE));
        } else {
            storedIcicle = ItemStack.EMPTY;
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (!this.remove && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (side == null) {
                return itemHandlers[0].cast();
            }
            switch (side) {
                case UP:
                    return itemHandlers[0].cast();
                case DOWN:
                    return itemHandlers[1].cast();
                case NORTH:
                    return itemHandlers[2].cast();
                case SOUTH:
                    return itemHandlers[3].cast();
                case EAST:
                    return itemHandlers[4].cast();
                case WEST:
                    return itemHandlers[5].cast();
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        for (LazyOptional<IItemHandlerModifiable> handler : itemHandlers) {
            handler.invalidate();
        }
    }
}
