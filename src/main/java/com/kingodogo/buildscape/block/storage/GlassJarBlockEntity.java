package com.kingodogo.buildscape.block.storage;

import com.kingodogo.buildscape.core.NbtKeys;
import com.kingodogo.buildscape.core.SyncedBlockEntity;
import com.kingodogo.buildscape.registry.ModBlockEntities;
import com.kingodogo.buildscape.registry.items.CardboardItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.HoneyBottleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.LingeringPotionItem;
import net.minecraft.world.item.MilkBucketItem;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.SplashPotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public class GlassJarBlockEntity extends SyncedBlockEntity {

    public static final int MAX_LIQUID_LEVEL = 16;
    public static final int XP_BOTTLE_MAX = 3;

    private ItemStack storedItem = ItemStack.EMPTY;
    private ItemStack storedLiquidItem = ItemStack.EMPTY;
    private int liquidLevel = 0;
    private long wobbleStartedAtTick = 0;

    public GlassJarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GLASS_JAR_BLOCK_ENTITY.get(), pos, state);
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
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        return stack.is(Items.EXPERIENCE_BOTTLE) || stack.is(CardboardItems.EXPERIENCE_BUCKET.get());
    }

    public static int getLiquidCap(ItemStack stack) {
        return isXpLiquid(stack) ? XP_BOTTLE_MAX : MAX_LIQUID_LEVEL;
    }

    static boolean shouldGlow(ItemStack liquid, int liquidLevel) {
        return liquidLevel > 0 && liquid != null && !liquid.isEmpty()
                && (isXpLiquid(liquid) || liquid.is(Items.LAVA_BUCKET)
                        || (liquid.getItem() instanceof BucketItem bucket && bucket.getFluid() == Fluids.LAVA));
    }

    public boolean isBucketLiquid() {
        if (!hasLiquid()) {
            return false;
        }
        ItemStack bucket = getBucketRepresentation();
        return !bucket.isEmpty();
    }

    public ItemStack getBucketRepresentation() {
        if (!hasLiquid()) {
            return ItemStack.EMPTY;
        }
        if (isXpLiquid(storedLiquidItem)) {
            return new ItemStack(CardboardItems.EXPERIENCE_BUCKET.get());
        }
        if (isPlainWater(storedLiquidItem)) {
            return new ItemStack(Items.WATER_BUCKET);
        }
        if (storedLiquidItem.is(Items.LAVA_BUCKET)) {
            return new ItemStack(Items.LAVA_BUCKET);
        }
        if (storedLiquidItem.is(Items.MILK_BUCKET)) {
            return new ItemStack(Items.MILK_BUCKET);
        }
        if (storedLiquidItem.getItem() instanceof BucketItem || storedLiquidItem.getItem() instanceof MilkBucketItem) {
            ItemStack copy = storedLiquidItem.copy();
            copy.setCount(1);
            return copy;
        }
        return ItemStack.EMPTY;
    }

    public ItemStack getBottleRepresentation() {
        if (!hasLiquid()) {
            return ItemStack.EMPTY;
        }
        if (isXpLiquid(storedLiquidItem)) {
            return new ItemStack(Items.EXPERIENCE_BOTTLE);
        }
        if (isPlainWater(storedLiquidItem)) {
            return PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);
        }
        if (storedLiquidItem.is(Items.HONEY_BOTTLE)) {
            return new ItemStack(Items.HONEY_BOTTLE);
        }
        if (storedLiquidItem.getItem() instanceof PotionItem || storedLiquidItem.getItem() instanceof HoneyBottleItem) {
            ItemStack copy = storedLiquidItem.copy();
            copy.setCount(1);
            return copy;
        }
        return ItemStack.EMPTY;
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
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        return stack.isEdible() || stack.getItem().isEdible() || stack.getItem().getFoodProperties() != null;
    }

    public static boolean isLiquidItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        Item item = stack.getItem();

        if (item instanceof MobBucketItem) {
            return false;
        }

        if (item instanceof BucketItem || item instanceof MilkBucketItem) {
            return true;
        }

        if (item instanceof SplashPotionItem || item instanceof LingeringPotionItem) {
            return false;
        }

        if (stack.is(Items.EXPERIENCE_BOTTLE)) {
            return true;
        }

        if (item instanceof PotionItem || item instanceof HoneyBottleItem) {
            return true;
        }

        return false;
    }

    private static boolean isPlainWater(ItemStack stack) {
        return stack.is(Items.WATER_BUCKET)
                || (stack.getItem() instanceof PotionItem
                        && PotionUtils.getPotion(stack) == Potions.WATER
                        && (stack.getTag() == null || (!stack.getTag().contains("CustomPotionEffects")
                                && !stack.getTag().contains("CustomPotionColor"))));
    }

    public static boolean isSameLiquid(ItemStack a, ItemStack b) {
        if (a == null || b == null || a.isEmpty() || b.isEmpty()) {
            return false;
        }
        if (isXpLiquid(a) && isXpLiquid(b)) {
            return true;
        }
        if (a.getItem() instanceof PotionItem && b.getItem() instanceof PotionItem) {
            return ItemStack.isSameItemSameTags(a, b);
        }
        if (isPlainWater(a) && isPlainWater(b)) {
            return true;
        }
        return ItemStack.isSameItemSameTags(a, b);
    }

    public boolean canAcceptFood(ItemStack stack) {
        if (hasLiquid()) {
            return false;
        }
        if (!isFoodItem(stack)) {
            return false;
        }
        if (isEmpty()) {
            return true;
        }
        return ItemStack.isSameItemSameTags(storedItem, stack) && storedItem.getCount() < 64;
    }

    public boolean canAcceptLiquid(ItemStack stack) {
        if (!isEmpty() && !storedItem.isEmpty()) {
            return false;
        }
        if (!isLiquidItem(stack)) {
            return false;
        }
        if (isEmpty() || !hasLiquid()) {
            return true;
        }
        if (stack.getItem() instanceof BucketItem || stack.getItem() instanceof MilkBucketItem) {
            return false;
        }
        int cap = getLiquidCap(stack);
        if (liquidLevel >= cap) {
            return false;
        }
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
        } else if (ItemStack.isSameItemSameTags(storedItem, stack)) {
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

        if (stack.getItem() instanceof BucketItem || stack.getItem() instanceof MilkBucketItem) {
            storedLiquidItem = stack.copy();
            storedLiquidItem.setCount(1);
            liquidLevel = getLiquidCap(stack);
            triggerWobble();
            sync();
            return true;
        } else if (stack.getItem() instanceof PotionItem || stack.getItem() instanceof HoneyBottleItem
                || stack.is(Items.EXPERIENCE_BOTTLE)) {
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
            consumeLevel();
        }
        return result;
    }

    public void consumeLevel() {
        if (!hasLiquid()) {
            return;
        }
        liquidLevel--;
        if (liquidLevel <= 0) {
            storedLiquidItem = ItemStack.EMPTY;
            liquidLevel = 0;
        }
        triggerWobble();
        sync();
    }

    public void sync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            updateGlowingState();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    void updateGlowingState() {
        if (level == null || level.isClientSide || level.getBlockEntity(worldPosition) != this) {
            return;
        }
        BlockState state = level.getBlockState(worldPosition);
        boolean glowing = shouldGlow(storedLiquidItem, liquidLevel);
        if (state.getBlock() instanceof GlassJarBlock && state.getValue(GlassJarBlock.GLOWING) != glowing) {
            level.setBlock(worldPosition, state.setValue(GlassJarBlock.GLOWING, glowing), Block.UPDATE_ALL);
        }
    }

    private void scheduleGlowingUpdate() {
        if (level != null && !level.isClientSide) {
            level.scheduleTick(worldPosition, getBlockState().getBlock(), 1);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        scheduleGlowingUpdate();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.getBoolean(NbtKeys.IS_EMPTY)) {
            this.storedItem = ItemStack.EMPTY;
            this.storedLiquidItem = ItemStack.EMPTY;
            this.liquidLevel = 0;
        } else {
            if (tag.contains(NbtKeys.STORED_ITEM, 10)) {
                CompoundTag itemTag = tag.getCompound(NbtKeys.STORED_ITEM);
                this.storedItem = ItemStack.of(itemTag);
                if (itemTag.contains(NbtKeys.REAL_COUNT)) {
                    this.storedItem.setCount(itemTag.getInt(NbtKeys.REAL_COUNT));
                }
            } else {
                this.storedItem = ItemStack.EMPTY;
            }

            if (tag.contains(NbtKeys.STORED_LIQUID_ITEM, 10)) {
                CompoundTag liquidTag = tag.getCompound(NbtKeys.STORED_LIQUID_ITEM);
                this.storedLiquidItem = ItemStack.of(liquidTag);
                this.liquidLevel = Math.max(0,
                        Math.min(getLiquidCap(this.storedLiquidItem), tag.getInt(NbtKeys.LIQUID_LEVEL)));
            } else {
                this.storedLiquidItem = ItemStack.EMPTY;
                this.liquidLevel = 0;
            }
        }
        this.wobbleStartedAtTick = tag.getLong(NbtKeys.WOBBLE_START_TICK);
        scheduleGlowingUpdate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!isEmpty()) {
            tag.putBoolean(NbtKeys.IS_EMPTY, false);
            if (!storedItem.isEmpty()) {
                CompoundTag itemTag = storedItem.save(new CompoundTag());
                itemTag.putInt(NbtKeys.REAL_COUNT, storedItem.getCount());
                tag.put(NbtKeys.STORED_ITEM, itemTag);
            }
            if (hasLiquid()) {
                CompoundTag liquidTag = storedLiquidItem.save(new CompoundTag());
                tag.put(NbtKeys.STORED_LIQUID_ITEM, liquidTag);
                tag.putInt(NbtKeys.LIQUID_LEVEL, liquidLevel);
            }
        } else {
            tag.putBoolean(NbtKeys.IS_EMPTY, true);
        }
        tag.putLong(NbtKeys.WOBBLE_START_TICK, wobbleStartedAtTick);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        if (tag != null) {
            load(tag);
        }
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            load(tag);
        }
    }
}
