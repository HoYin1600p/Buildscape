package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import com.kingodogo.buildscape.block.entity.IBlockEntityReadData;
import com.kingodogo.buildscape.block.entity.IBlockEntityWriteData;
import com.kingodogo.buildscape.block.entity.IDataSerializable;
import com.kingodogo.buildscape.block.entity.DataBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
public class DecoratedPotBlockEntity extends DataBlockEntity implements WorldlyContainer, IDataSerializable {

    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    private long wobbleStartedAtTick = 0;
    private WobbleStyle lastWobbleStyle = WobbleStyle.NONE;
    private static final int[] SLOTS = new int[]{0};
    public enum WobbleStyle {
        NONE,
        POSITIVE,
        NEGATIVE
    }

    public DecoratedPotBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
        this(ModBlockEntities.DECORATED_POT_TYPE, worldPosition, blockState);
    }

    public DecoratedPotBlockEntity(final BlockEntityType<?> type, final BlockPos worldPosition, final BlockState blockState) {
        super(type, worldPosition, blockState);
    }
    public ItemStack getStoredItem() {
        return items.get(0);
    }
    public void setStoredItem(ItemStack stack) {
        setStoredItem(stack, true);
    }
    public void setStoredItem(ItemStack stack, boolean sendUpdate) {
        this.items.set(0, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
        this.setChanged();
        if (sendUpdate && level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public boolean isEmpty() {
        return items.get(0).isEmpty();
    }
    public void triggerWobble(WobbleStyle style) {
        if (level != null && !level.isClientSide() && style != WobbleStyle.NONE) {
            this.wobbleStartedAtTick = level.getGameTime();
            this.lastWobbleStyle = style;
            this.setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
    public long getWobbleStartedAtTick() {
        return wobbleStartedAtTick;
    }
    public WobbleStyle getLastWobbleStyle() {
        return lastWobbleStyle;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction direction) {
        if (stack.getItem() instanceof net.minecraft.world.item.BlockItem blockItem) {
            if (blockItem.getBlock() instanceof DecoratedPotBlock) {
                return false;
            }
        }
        ItemStack stored = getStoredItem();
        return direction != Direction.DOWN && (stored.isEmpty() || (com.kingodogo.buildscape.platform.Services.PLATFORM.isSameItemSameComponents(stored, stack) && stored.getCount() < stored.getMaxStackSize()));
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return direction == Direction.DOWN && !isEmpty();
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot == 0 ? items.get(0) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (slot != 0 || items.get(0).isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack stored = items.get(0);
        int toRemove = Math.min(amount, stored.getCount());
        ItemStack result = stored.copy();
        result.setCount(toRemove);
        stored.shrink(toRemove);
        if (stored.isEmpty()) {
            items.set(0, ItemStack.EMPTY);
        }

        this.setChanged();
        if (level != null && !level.isClientSide()) {
            level.gameEvent(null, net.minecraft.world.level.gameevent.GameEvent.BLOCK_CHANGE, worldPosition);
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot != 0 || items.get(0).isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack result = items.get(0).copy();
        items.set(0, ItemStack.EMPTY);
        return result;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == 0) {
            items.set(0, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
            this.setChanged();
            if (level != null && !level.isClientSide()) {
                level.gameEvent(null, net.minecraft.world.level.gameevent.GameEvent.BLOCK_CHANGE, worldPosition);
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    }

    @Override
    public void clearContent() {
        this.items.clear();
        this.setChanged();
    }
    public int getComparatorOutput() {
        ItemStack stored = getStoredItem();
        if (stored.isEmpty()) {
            return 0;
        }
        float fillRatio = (float) stored.getCount() / (float) stored.getMaxStackSize();
        return Math.max(1, (int) Math.ceil(fillRatio * 15.0f));
    }

    @Override
    public void readData(IBlockEntityReadData data) {
        this.items.clear();
        this.items.set(0, data.getItemOrEmpty("StoredItem"));
        this.wobbleStartedAtTick = data.getLongOr("WobbleStartTick", 0L);
        try {
            this.lastWobbleStyle = WobbleStyle.valueOf(data.getStringOr("LastWobbleStyle", WobbleStyle.NONE.name()));
        } catch (IllegalArgumentException e) {
            // The reference resets unknown saved wobble styles to NONE.
            this.lastWobbleStyle = WobbleStyle.NONE;
        }
    }

    @Override
    public void writeData(IBlockEntityWriteData data) {
        ItemStack stored = this.items.get(0);
        if (!stored.isEmpty()) {
            data.putItem("StoredItem", stored);
        }
        data.putLong("WobbleStartTick", this.wobbleStartedAtTick);
        data.putString("LastWobbleStyle", this.lastWobbleStyle.name());
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void dropContents(BlockPos pos) {
        if (this.level != null && !this.level.isClientSide() && !this.isEmpty()) {
            Containers.dropItemStack(this.level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, this.getStoredItem().copy());
        }
    }
}
