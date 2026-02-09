package com.kingodogo.buildscape.entity;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class FestiveStockingEntity extends HangingEntity {

    private static final EntityDataAccessor<ItemStack> DATA_ITEM =
            SynchedEntityData.defineId(
                    FestiveStockingEntity.class,
                    EntityDataSerializers.ITEM_STACK
            );
    private String colorVariant = "festive";

    public FestiveStockingEntity(
            EntityType<? extends FestiveStockingEntity> entityType,
            Level level
    ) {
        super(entityType, level);
    }

    public FestiveStockingEntity(Level level, BlockPos pos, Direction direction) {
        this(ModEntities.FESTIVE_STOCKING.get(), level, pos);
        if (direction != null) {
            this.setDirection(direction);
            if (this.pos != null) {
                this.recalculateBoundingBox();
            }
        }
    }

    public FestiveStockingEntity(
            Level level,
            BlockPos pos,
            Direction direction,
            String color
    ) {
        this(ModEntities.FESTIVE_STOCKING.get(), level, pos);
        this.colorVariant = color;
        if (direction != null) {
            this.setDirection(direction);
            if (this.pos != null) {
                this.recalculateBoundingBox();
            }
        }
    }

    public String getColorVariant() {
        return colorVariant;
    }

    public void setColorVariant(String color) {
        this.colorVariant = color;
    }

    public FestiveStockingEntity(
            EntityType<? extends FestiveStockingEntity> entityType,
            Level level,
            BlockPos pos
    ) {
        super(entityType, level, pos);
    }

    @Override
    public void setDirection(Direction direction) {
        if (direction != null) {
            try {
                java.lang.reflect.Field directionField =
                        HangingEntity.class.getDeclaredField("direction");
                directionField.setAccessible(true);
                directionField.set(this, direction);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(
                        "Failed to set direction for FestiveStockingEntity: " +
                                e.getMessage(),
                        e
                );
            }

            if (this.pos != null) {
                this.recalculateBoundingBox();
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_ITEM, ItemStack.EMPTY);
    }

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(x, y, z);
        this.recalculateBoundingBox();
    }

    @Override
    protected void recalculateBoundingBox() {
        if (this.direction != null && this.pos != null) {
            double d0 = (double) this.pos.getX() + 0.5D;
            double d1 = (double) this.pos.getY() + 0.5D;
            double d2 = (double) this.pos.getZ() + 0.5D;
            double d3 = 0.46875D;
            double d4 = this.offs(this.getWidth());
            double d5 = this.offs(this.getHeight());

            d0 -= (double) this.direction.getStepX() * 0.46875D;
            d1 -= (double) this.direction.getStepY() * 0.46875D;
            d2 -= (double) this.direction.getStepZ() * 0.46875D;

            Direction offsetDir;
            if (this.direction.getAxis() == Direction.Axis.Y) {
                offsetDir = Direction.NORTH;
            } else {
                switch (this.direction) {
                    case NORTH:
                        offsetDir = Direction.EAST;
                        break;
                    case SOUTH:
                        offsetDir = Direction.WEST;
                        break;
                    case EAST:
                        offsetDir = Direction.SOUTH;
                        break;
                    case WEST:
                        offsetDir = Direction.NORTH;
                        break;
                    default:
                        offsetDir = Direction.NORTH;
                        break;
                }
            }

            d0 += d4 * (double) offsetDir.getStepX();
            d1 += d5 * (double) offsetDir.getStepY();
            d2 += d4 * (double) offsetDir.getStepZ();

            this.setPosRaw(d0, d1, d2);

            double d6, d7, d8;

            if (this.direction.getAxis() == Direction.Axis.Z) {
                d6 = this.getWidth();
                d7 = this.getHeight();
                d8 = 2.0D;
            } else if (this.direction.getAxis() == Direction.Axis.X) {
                d6 = 2.0D;
                d7 = this.getHeight();
                d8 = this.getWidth();
            } else {
                d6 = this.getWidth();
                d7 = 2.0D;
                d8 = this.getHeight();
            }

            d6 /= 32.0D;
            d7 /= 32.0D;
            d8 /= 32.0D;

            this.setBoundingBox(
                    new net.minecraft.world.phys.AABB(
                            d0 - d6,
                            d1 - d7,
                            d2 - d8,
                            d0 + d6,
                            d1 + d7,
                            d2 + d8
                    )
            );
        }
    }

    private double offs(int p_31731_) {
        return p_31731_ % 32 == 0 ? 0.5D : 0.0D;
    }

    @Override
    public int getWidth() {
        return 12;
    }

    @Override
    public int getHeight() {
        return 16;
    }

    private DamageSource lastDamageSource = null;

    @Override
    public void dropItem(@Nullable Entity entity) {
        this.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
        if (!this.level.isClientSide) {
            ItemStack storedItem = this.getItem();
            boolean hasSilkTouch = false;

            if (entity instanceof Player player) {
                ItemStack tool = player.getMainHandItem();
                if (
                        EnchantmentHelper.getItemEnchantmentLevel(
                                Enchantments.SILK_TOUCH,
                                tool
                        ) >
                                0
                ) {
                    hasSilkTouch = true;
                }
            }

            ItemStack stockingItem = getStockingItemForColor(this.colorVariant);

            if (hasSilkTouch && !storedItem.isEmpty()) {
                CompoundTag tag = stockingItem.getOrCreateTag();
                CompoundTag storedTag = new CompoundTag();
                storedItem.save(storedTag);
                tag.put("StoredItem", storedTag);
            }

            this.spawnAtLocation(stockingItem);

            if (!hasSilkTouch && !storedItem.isEmpty()) {
                this.spawnAtLocation(storedItem);
            }
        }
    }

    @Override
    public void playPlacementSound() {
        this.playSound(SoundEvents.ITEM_FRAME_ADD_ITEM, 1.0F, 1.0F);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);
        ItemStack storedItem = this.getItem();

        if (
                heldItem.isEmpty() && player.isShiftKeyDown() && !storedItem.isEmpty()
        ) {
            if (!this.level.isClientSide) {
                ItemStack toGive = storedItem.copy();
                this.setItem(ItemStack.EMPTY);

                if (!player.getInventory().add(toGive)) {
                    ItemEntity itemEntity = new ItemEntity(
                            this.level,
                            this.getX(),
                            this.getY(),
                            this.getZ(),
                            toGive
                    );
                    itemEntity.setDefaultPickUpDelay();
                    this.level.addFreshEntity(itemEntity);
                }

                this.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
            }
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }

        if (!heldItem.isEmpty()) {
            if (storedItem.isEmpty()) {
                if (!this.level.isClientSide) {
                    ItemStack toStore = heldItem.copy();
                    int maxStack = toStore.getMaxStackSize();
                    int toTake = player.isShiftKeyDown()
                            ? Math.min(heldItem.getCount(), maxStack)
                            : 1;
                    toStore.setCount(toTake);
                    this.setItem(toStore, true);
                    heldItem.shrink(toTake);
                    if (heldItem.isEmpty()) {
                        player.setItemInHand(hand, ItemStack.EMPTY);
                    }
                }
                return InteractionResult.sidedSuccess(this.level.isClientSide);
            } else if (
                    storedItem.sameItem(heldItem) &&
                            storedItem.getCount() < storedItem.getMaxStackSize()
            ) {
                if (!this.level.isClientSide) {
                    int maxStack = storedItem.getMaxStackSize();
                    int spaceAvailable = maxStack - storedItem.getCount();
                    int toAdd = player.isShiftKeyDown()
                            ? Math.min(heldItem.getCount(), spaceAvailable)
                            : 1;
                    int canAdd = Math.min(toAdd, spaceAvailable);
                    storedItem.grow(canAdd);
                    this.setItem(storedItem, true);
                    heldItem.shrink(canAdd);
                    if (heldItem.isEmpty()) {
                        player.setItemInHand(hand, ItemStack.EMPTY);
                    }
                }
                return InteractionResult.sidedSuccess(this.level.isClientSide);
            }
        }

        return InteractionResult.PASS;
    }

    public ItemStack getItem() {
        return this.getEntityData().get(DATA_ITEM);
    }

    public void setItem(ItemStack stack) {
        this.setItem(stack, true);
    }

    private void setItem(ItemStack stack, boolean update) {
        if (!stack.isEmpty()) {
            stack = stack.copy();
            stack.setEntityRepresentation(this);
        }
        this.getEntityData()
                .set(DATA_ITEM, stack.isEmpty() ? ItemStack.EMPTY : stack);
        if (!stack.isEmpty() && update) {
            this.playSound(SoundEvents.ITEM_FRAME_ADD_ITEM, 1.0F, 1.0F);
        }
        if (update && this.pos != null) {
            this.level.updateNeighbourForOutputSignal(this.pos, Blocks.AIR);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        ItemStack storedItem = this.getItem();
        if (!storedItem.isEmpty()) {
            CompoundTag itemTag = new CompoundTag();
            storedItem.save(itemTag);
            tag.put("Item", itemTag);
        }
        tag.putString("ColorVariant", colorVariant);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        CompoundTag itemTag = tag.getCompound("Item");
        if (itemTag != null && !itemTag.isEmpty()) {
            ItemStack item = ItemStack.of(itemTag);
            if (item.isEmpty()) {
                this.setItem(ItemStack.EMPTY, false);
            } else {
                this.setItem(item, false);
            }
        } else {
            this.setItem(ItemStack.EMPTY, false);
        }
        if (tag.contains("ColorVariant")) {
            this.colorVariant = tag.getString("ColorVariant");
        }
    }

    @Override
    public boolean survives() {
        if (!this.level.noCollision(this)) {
            return false;
        } else {
            BlockPos blockpos = this.pos.relative(this.direction.getOpposite());
            if (
                    !this.level.getBlockState(blockpos).isSolidRender(this.level, blockpos)
            ) {
                return false;
            } else {
                java.util.List<Entity> entities =
                        this.level.getEntities(this, this.getBoundingBox());
                for (Entity entity : entities) {
                    if (entity instanceof HangingEntity && entity != this) {
                        return false;
                    }
                }
                return true;
            }
        }
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        this.lastDamageSource = source;
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (!source.isExplosion() && !this.getItem().isEmpty()) {
            if (!this.level.isClientSide) {
                this.dropItem(source.getEntity());
                this.playSound(this.getRemoveItemSound(), 1.0F, 1.0F);
                this.setItem(ItemStack.EMPTY);
            }
            return true;
        } else {
            return super.hurt(source, amount);
        }
    }

    private net.minecraft.sounds.SoundEvent getRemoveItemSound() {
        return SoundEvents.ITEM_FRAME_REMOVE_ITEM;
    }

    @Override
    public ItemStack getPickResult() {
        ItemStack result = getStockingItemForColor(this.colorVariant);
        ItemStack storedItem = this.getItem();
        if (!storedItem.isEmpty()) {
            CompoundTag tag = result.getOrCreateTag();
            CompoundTag storedTag = new CompoundTag();
            storedItem.save(storedTag);
            tag.put("StoredItem", storedTag);
        }
        return result;
    }

    private ItemStack getStockingItemForColor(String color) {
        switch (color) {
            case "black":
                return new ItemStack(
                        com.kingodogo.buildscape.item.ModItems.BLACK_FESTIVE_STOCKING.get()
                );
            case "blue":
                return new ItemStack(
                        com.kingodogo.buildscape.item.ModItems.BLUE_FESTIVE_STOCKING.get()
                );
            case "brown":
                return new ItemStack(
                        com.kingodogo.buildscape.item.ModItems.BROWN_FESTIVE_STOCKING.get()
                );
            case "cyan":
                return new ItemStack(
                        com.kingodogo.buildscape.item.ModItems.CYAN_FESTIVE_STOCKING.get()
                );
            case "gray":
                return new ItemStack(
                        com.kingodogo.buildscape.item.ModItems.GRAY_FESTIVE_STOCKING.get()
                );
            case "green":
                return new ItemStack(
                        com.kingodogo.buildscape.item.ModItems.GREEN_FESTIVE_STOCKING.get()
                );
            case "light_blue":
                return new ItemStack(
                        com.kingodogo.buildscape.item.ModItems.LIGHT_BLUE_FESTIVE_STOCKING.get()
                );
            case "light_gray":
                return new ItemStack(
                        com.kingodogo.buildscape.item.ModItems.LIGHT_GRAY_FESTIVE_STOCKING.get()
                );
            case "lime":
                return new ItemStack(
                        com.kingodogo.buildscape.item.ModItems.LIME_FESTIVE_STOCKING.get()
                );
            case "magenta":
                return new ItemStack(
                        com.kingodogo.buildscape.item.ModItems.MAGENTA_FESTIVE_STOCKING.get()
                );
            case "orange":
                return new ItemStack(
                        com.kingodogo.buildscape.item.ModItems.ORANGE_FESTIVE_STOCKING.get()
                );
            case "pink":
                return new ItemStack(
                        com.kingodogo.buildscape.item.ModItems.PINK_FESTIVE_STOCKING.get()
                );
            case "purple":
                return new ItemStack(
                        com.kingodogo.buildscape.item.ModItems.PURPLE_FESTIVE_STOCKING.get()
                );
            case "red":
                return new ItemStack(
                        com.kingodogo.buildscape.item.ModItems.RED_FESTIVE_STOCKING.get()
                );
            case "white":
                return new ItemStack(
                        com.kingodogo.buildscape.item.ModItems.WHITE_FESTIVE_STOCKING.get()
                );
            case "yellow":
                return new ItemStack(
                        com.kingodogo.buildscape.item.ModItems.YELLOW_FESTIVE_STOCKING.get()
                );
            default:
                return new ItemStack(
                        com.kingodogo.buildscape.item.ModItems.FESTIVE_STOCKING.get()
                );
        }
    }
}
