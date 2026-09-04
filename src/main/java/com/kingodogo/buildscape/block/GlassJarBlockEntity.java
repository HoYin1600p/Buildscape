package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.HoneyBottleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.LingeringPotionItem;
import net.minecraft.world.item.MilkBucketItem;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.SplashPotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GlassJarBlockEntity extends BlockEntity {

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
        if (stack == null || stack.isEmpty()) return false;
        return stack.is(net.minecraft.world.item.Items.EXPERIENCE_BOTTLE) || stack.is(com.kingodogo.buildscape.item.ModItems.EXPERIENCE_BUCKET.get());
    }

    public boolean isBucketLiquid() {
        if (!hasLiquid()) return false;
        ItemStack bucket = getBucketRepresentation();
        return !bucket.isEmpty();
    }

    public ItemStack getBucketRepresentation() {
        if (!hasLiquid()) return ItemStack.EMPTY;
        if (isXpLiquid(storedLiquidItem)) {
            return new ItemStack(com.kingodogo.buildscape.item.ModItems.EXPERIENCE_BUCKET.get());
        }
        if (storedLiquidItem.is(net.minecraft.world.item.Items.WATER_BUCKET)
                || (storedLiquidItem.getItem() instanceof PotionItem && PotionUtils.getPotion(storedLiquidItem) == net.minecraft.world.item.alchemy.Potions.WATER)) {
            return new ItemStack(net.minecraft.world.item.Items.WATER_BUCKET);
        }
        if (storedLiquidItem.is(net.minecraft.world.item.Items.LAVA_BUCKET)) {
            return new ItemStack(net.minecraft.world.item.Items.LAVA_BUCKET);
        }
        if (storedLiquidItem.is(net.minecraft.world.item.Items.MILK_BUCKET)) {
            return new ItemStack(net.minecraft.world.item.Items.MILK_BUCKET);
        }
        if (storedLiquidItem.getItem() instanceof BucketItem || storedLiquidItem.getItem() instanceof MilkBucketItem) {
            ItemStack copy = storedLiquidItem.copy();
            copy.setCount(1);
            return copy;
        }
        return ItemStack.EMPTY;
    }

    public ItemStack getBottleRepresentation() {
        if (!hasLiquid()) return ItemStack.EMPTY;
        if (isXpLiquid(storedLiquidItem)) {
            return new ItemStack(net.minecraft.world.item.Items.EXPERIENCE_BOTTLE);
        }
        if (storedLiquidItem.is(net.minecraft.world.item.Items.WATER_BUCKET)
                || (storedLiquidItem.getItem() instanceof PotionItem && PotionUtils.getPotion(storedLiquidItem) == net.minecraft.world.item.alchemy.Potions.WATER)) {
            return PotionUtils.setPotion(new ItemStack(net.minecraft.world.item.Items.POTION), net.minecraft.world.item.alchemy.Potions.WATER);
        }
        if (storedLiquidItem.is(net.minecraft.world.item.Items.HONEY_BOTTLE)) {
            return new ItemStack(net.minecraft.world.item.Items.HONEY_BOTTLE);
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
        net.minecraft.world.item.Item item = stack.getItem();

        if (item instanceof MobBucketItem) {
            return false;
        }

        if (item instanceof BucketItem || item instanceof MilkBucketItem) {
            return true;
        }

        if (item instanceof SplashPotionItem || item instanceof LingeringPotionItem) {
            return false;
        }

        if (stack.is(net.minecraft.world.item.Items.EXPERIENCE_BOTTLE)) {
            return true;
        }

        if (item instanceof PotionItem || item instanceof HoneyBottleItem) {
            return true;
        }

        return false;
    }

    public static boolean isSameLiquid(ItemStack a, ItemStack b) {
        if (a == null || b == null || a.isEmpty() || b.isEmpty()) return false;
        if (isXpLiquid(a) && isXpLiquid(b)) return true;
        boolean aIsWater = a.is(net.minecraft.world.item.Items.WATER_BUCKET)
                || (a.getItem() instanceof PotionItem && PotionUtils.getPotion(a) == net.minecraft.world.item.alchemy.Potions.WATER);
        boolean bIsWater = b.is(net.minecraft.world.item.Items.WATER_BUCKET)
                || (b.getItem() instanceof PotionItem && PotionUtils.getPotion(b) == net.minecraft.world.item.alchemy.Potions.WATER);
        if (aIsWater && bIsWater) return true;
        if (a.getItem() != b.getItem()) return false;
        if (a.getItem() instanceof PotionItem) {
            return PotionUtils.getPotion(a) == PotionUtils.getPotion(b);
        }
        return ItemStack.isSameItemSameTags(a, b);
    }

    public boolean canAcceptFood(ItemStack stack) {
        if (hasLiquid()) return false;
        if (!isFoodItem(stack)) return false;
        if (isEmpty()) return true;
        return ItemStack.isSameItemSameTags(storedItem, stack) && storedItem.getCount() < 64;
    }

    public boolean canAcceptLiquid(ItemStack stack) {
        if (!isEmpty() && !storedItem.isEmpty()) return false;
        if (!isLiquidItem(stack)) return false;
        if (isEmpty() || !hasLiquid()) return true;
        int cap = isXpLiquid(stack) ? XP_BOTTLE_MAX : MAX_LIQUID_LEVEL;
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
            liquidLevel = 16;
            triggerWobble();
            sync();
            return true;
        } else if (stack.getItem() instanceof PotionItem || stack.getItem() instanceof HoneyBottleItem || stack.is(net.minecraft.world.item.Items.EXPERIENCE_BOTTLE)) {
            if (isEmpty() || !hasLiquid()) {
                storedLiquidItem = stack.copy();
                storedLiquidItem.setCount(1);
                liquidLevel = 1;
            } else {
                liquidLevel = Math.min(16, liquidLevel + 1);
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
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.getBoolean("IsEmpty")) {
            this.storedItem = ItemStack.EMPTY;
            this.storedLiquidItem = ItemStack.EMPTY;
            this.liquidLevel = 0;
        } else {
            if (tag.contains("StoredItem", 10)) {
                CompoundTag itemTag = tag.getCompound("StoredItem");
                this.storedItem = ItemStack.of(itemTag);
                if (itemTag.contains("RealCount")) {
                    this.storedItem.setCount(itemTag.getInt("RealCount"));
                }
            } else {
                this.storedItem = ItemStack.EMPTY;
            }

            if (tag.contains("StoredLiquidItem", 10)) {
                CompoundTag liquidTag = tag.getCompound("StoredLiquidItem");
                this.storedLiquidItem = ItemStack.of(liquidTag);
                this.liquidLevel = tag.getInt("LiquidLevel");
            } else {
                this.storedLiquidItem = ItemStack.EMPTY;
                this.liquidLevel = 0;
            }
        }
        this.wobbleStartedAtTick = tag.getLong("WobbleStartTick");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!isEmpty()) {
            tag.putBoolean("IsEmpty", false);
            if (!storedItem.isEmpty()) {
                CompoundTag itemTag = storedItem.save(new CompoundTag());
                itemTag.putInt("RealCount", storedItem.getCount());
                tag.put("StoredItem", itemTag);
            }
            if (hasLiquid()) {
                CompoundTag liquidTag = storedLiquidItem.save(new CompoundTag());
                tag.put("StoredLiquidItem", liquidTag);
                tag.putInt("LiquidLevel", liquidLevel);
            }
        } else {
            tag.putBoolean("IsEmpty", true);
        }
        tag.putLong("WobbleStartTick", wobbleStartedAtTick);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        if (tag != null) {
            load(tag);
        }
    }

    @Override
    public void onDataPacket(net.minecraft.network.Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            load(tag);
        }
    }
}
