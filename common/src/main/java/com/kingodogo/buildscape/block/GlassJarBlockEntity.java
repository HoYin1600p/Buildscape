package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.LingeringPotionItem;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.SplashPotionItem;
import com.kingodogo.buildscape.block.entity.IBlockEntityReadData;
import com.kingodogo.buildscape.block.entity.IBlockEntityWriteData;
import com.kingodogo.buildscape.block.entity.IDataSerializable;
import com.kingodogo.buildscape.block.entity.DataBlockEntity;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
public class GlassJarBlockEntity extends DataBlockEntity implements IDataSerializable {
    public static final int MAX_LIQUID_LEVEL = 16;
    public static final int XP_BOTTLE_MAX = 3;

    private ItemStack storedItem = ItemStack.EMPTY;
    private ItemStack storedLiquidItem = ItemStack.EMPTY;
    private int liquidLevel = 0;
    private long wobbleStartedAtTick = 0;

    public GlassJarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GLASS_JAR_TYPE, pos, state);
    }
    public ItemStack getStoredItem() {
        return storedItem;
    }
    public ItemStack getStoredLiquidItem() {
        return storedLiquidItem;
    }
    public int getLiquidLevel() {
        return liquidLevel;
    }
    public boolean hasLiquid() {
        return storedLiquidItem != null && !storedLiquidItem.isEmpty() && liquidLevel > 0;
    }
    public static boolean isXpLiquid(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        return stack.is(Items.EXPERIENCE_BOTTLE)
                || stack.getItem() == Services.PLATFORM.getItem(com.kingodogo.buildscape.util.CommonId.of("buildscape", "experience_bucket"));
    }
    public static int getLiquidCap(ItemStack stack) {
        return isXpLiquid(stack) ? XP_BOTTLE_MAX : MAX_LIQUID_LEVEL;
    }
    public boolean isBucketLiquid() {
        if (!hasLiquid()) return false;
        ItemStack bucket = getBucketRepresentation();
        return !bucket.isEmpty();
    }
    public ItemStack getBucketRepresentation() {
        if (!hasLiquid()) return ItemStack.EMPTY;
        if (isXpLiquid(storedLiquidItem)) {
            return new ItemStack(Services.PLATFORM.getItem(com.kingodogo.buildscape.util.CommonId.of("buildscape", "experience_bucket")));
        }
        if (isWater(storedLiquidItem)) {
            return new ItemStack(Items.WATER_BUCKET);
        }
        if (storedLiquidItem.is(Items.LAVA_BUCKET)) {
            return new ItemStack(Items.LAVA_BUCKET);
        }
        if (storedLiquidItem.is(Items.MILK_BUCKET)) {
            return new ItemStack(Items.MILK_BUCKET);
        }
        if (storedLiquidItem.getItem() instanceof BucketItem) {
            ItemStack copy = storedLiquidItem.copy();
            copy.setCount(1);
            return copy;
        }
        return ItemStack.EMPTY;
    }
    public ItemStack getBottleRepresentation() {
        if (!hasLiquid()) return ItemStack.EMPTY;
        if (isXpLiquid(storedLiquidItem)) {
            return new ItemStack(Items.EXPERIENCE_BOTTLE);
        }
        if (isWater(storedLiquidItem)) {
            return com.kingodogo.buildscape.platform.Services.PLATFORM.createWaterPotion();
        }
        if (storedLiquidItem.is(Items.HONEY_BOTTLE)) {
            return new ItemStack(Items.HONEY_BOTTLE);
        }
        if (storedLiquidItem.getItem() instanceof PotionItem) {
            ItemStack copy = storedLiquidItem.copy();
            copy.setCount(1);
            return copy;
        }
        return ItemStack.EMPTY;
    }
    private static boolean isWater(ItemStack stack) {
        return com.kingodogo.buildscape.platform.Services.PLATFORM.isWaterPotion(stack);
    }
    public boolean isEmpty() {
        return (storedItem == null || storedItem.isEmpty() || storedItem.getCount() <= 0) && !hasLiquid();
    }
    public int getItemCount() {
        return (storedItem == null || storedItem.isEmpty()) ? 0 : storedItem.getCount();
    }
    public long getWobbleStartedAtTick() {
        return wobbleStartedAtTick;
    }
    public void triggerWobble() {
        if (level != null) {
            this.wobbleStartedAtTick = level.getGameTime();
        }
    }
    public static boolean isFoodItem(ItemStack stack) {
        return com.kingodogo.buildscape.platform.Services.PLATFORM.isFood(stack);
    }
    public static boolean isLiquidItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        net.minecraft.world.item.Item item = stack.getItem();
        if (item instanceof MobBucketItem) {
            return false;
        }
        if (item instanceof BucketItem || stack.is(Items.MILK_BUCKET)) {
            return true;
        }
        if (item instanceof SplashPotionItem || item instanceof LingeringPotionItem) {
            return false;
        }
        if (stack.is(Items.EXPERIENCE_BOTTLE)) {
            return true;
        }
        return item instanceof PotionItem || stack.is(Items.HONEY_BOTTLE);
    }
    public static boolean isSameLiquid(ItemStack a, ItemStack b) {
        if (a == null || b == null || a.isEmpty() || b.isEmpty()) return false;
        if (isXpLiquid(a) && isXpLiquid(b)) return true;
        boolean aIsWater = isWater(a);
        boolean bIsWater = isWater(b);
        if (aIsWater && bIsWater) return true;
        if (a.getItem() != b.getItem()) return false;
        return Services.PLATFORM.isSameItemSameComponents(a, b);
    }
    public boolean canAcceptFood(ItemStack stack) {
        if (hasLiquid()) return false;
        if (!isFoodItem(stack)) return false;
        if (isEmpty()) return true;
        return Services.PLATFORM.isSameItemSameComponents(storedItem, stack) && storedItem.getCount() < 64;
    }
    public boolean canAcceptLiquid(ItemStack stack) {
        if (!isEmpty() && !storedItem.isEmpty()) return false;
        if (!isLiquidItem(stack)) return false;
        if (isEmpty() || !hasLiquid()) return true;
        if (stack.getItem() instanceof BucketItem || stack.is(Items.MILK_BUCKET)) return false;
        int cap = getLiquidCap(stack);
        if (liquidLevel >= cap) return false;
        return isSameLiquid(storedLiquidItem, stack);
    }
    public int addFood(ItemStack stack) {
        if (!isFoodItem(stack) || hasLiquid()) {
            return 0;
        }
        int added = 0;
        if (isEmpty()) {
            int toAdd = Math.min(stack.getCount(), 64);
            storedItem = stack.copy();
            storedItem.setCount(toAdd);
            added = toAdd;
        } else if (Services.PLATFORM.isSameItemSameComponents(storedItem, stack)) {
            int space = 64 - storedItem.getCount();
            if (space <= 0) {
                return 0;
            }
            int toAdd = Math.min(stack.getCount(), space);
            storedItem.grow(toAdd);
            added = toAdd;
        }

        if (added > 0) {
            triggerWobble();
            sync();
        }
        return added;
    }
    public boolean addLiquid(ItemStack stack) {
        if (!canAcceptLiquid(stack)) {
            return false;
        }

        if (stack.getItem() instanceof BucketItem || stack.is(Items.MILK_BUCKET)) {
            storedLiquidItem = stack.copy();
            storedLiquidItem.setCount(1);
            liquidLevel = getLiquidCap(stack);
            triggerWobble();
            sync();
            return true;
        } else if (stack.getItem() instanceof PotionItem || stack.is(Items.HONEY_BOTTLE) || stack.is(Items.EXPERIENCE_BOTTLE)) {
            if (isEmpty() || !hasLiquid()) {
                storedLiquidItem = stack.copy();
                storedLiquidItem.setCount(1);
                liquidLevel = 1;
            } else {
                liquidLevel = Math.min(getLiquidCap(stack), liquidLevel + 1);
            }
            triggerWobble();
            sync();
            return true;
        }
        return false;
    }
    public ItemStack extractFood(int amount) {
        if (isEmpty() || storedItem.isEmpty()) {
            return ItemStack.EMPTY;
        }
        int toExtract = Math.min(amount, storedItem.getCount());
        ItemStack extracted = storedItem.copy();
        extracted.setCount(toExtract);

        storedItem.shrink(toExtract);
        if (storedItem.getCount() <= 0) {
            storedItem = ItemStack.EMPTY;
        }

        triggerWobble();
        sync();
        return extracted;
    }
    public ItemStack extractBucket() {
        int required = isXpLiquid(storedLiquidItem) ? XP_BOTTLE_MAX : MAX_LIQUID_LEVEL;
        if (!hasLiquid() || liquidLevel < required) {
            return ItemStack.EMPTY;
        }
        ItemStack result = getBucketRepresentation();
        if (!result.isEmpty()) {
            storedLiquidItem = ItemStack.EMPTY;
            liquidLevel = 0;
            triggerWobble();
            sync();
        }
        return result;
    }
    public ItemStack extractBottle() {
        if (!hasLiquid() || liquidLevel <= 0) {
            return ItemStack.EMPTY;
        }
        ItemStack result = getBottleRepresentation();
        if (!result.isEmpty()) {
            liquidLevel--;
            if (liquidLevel <= 0) {
                storedLiquidItem = ItemStack.EMPTY;
                liquidLevel = 0;
            }
            triggerWobble();
            sync();
        }
        return result;
    }
    public void sync() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public void readData(IBlockEntityReadData data) {
        if (data.getBooleanOr("IsEmpty", false)) {
            this.storedItem = ItemStack.EMPTY;
            this.storedLiquidItem = ItemStack.EMPTY;
            this.liquidLevel = 0;
        } else {
            this.storedItem = data.getItemOrEmpty("StoredItem");
            this.storedLiquidItem = data.getItemOrEmpty("StoredLiquidItem");
            this.liquidLevel = Math.max(0, Math.min(getLiquidCap(this.storedLiquidItem), data.getIntOr("LiquidLevel", 0)));
        }
        this.wobbleStartedAtTick = data.getLongOr("WobbleStartTick", 0L);
    }

    @Override
    public void writeData(IBlockEntityWriteData data) {
        if (!isEmpty()) {
            data.putBoolean("IsEmpty", false);
            if (!storedItem.isEmpty()) {
                data.putItem("StoredItem", storedItem);
            }
            if (hasLiquid()) {
                data.putItem("StoredLiquidItem", storedLiquidItem);
                data.putInt("LiquidLevel", liquidLevel);
            }
        } else {
            data.putBoolean("IsEmpty", true);
        }
        data.putLong("WobbleStartTick", wobbleStartedAtTick);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
