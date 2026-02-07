package com.kingodogo.buildscape.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;

public class ColoredItemFrameEntity extends HangingEntity {

    private static final EntityDataAccessor<ItemStack> DATA_ITEM =
            SynchedEntityData.defineId(ColoredItemFrameEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Integer> DATA_ROTATION =
            SynchedEntityData.defineId(ColoredItemFrameEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> DATA_COLOR =
            SynchedEntityData.defineId(ColoredItemFrameEntity.class, EntityDataSerializers.STRING);

    private float dropChance = 1.0F;
    private boolean fixed = false;

    public ColoredItemFrameEntity(EntityType<? extends ColoredItemFrameEntity> entityType, Level level) {
        super(entityType, level);
    }

    public ColoredItemFrameEntity(Level level, BlockPos pos, Direction direction) {
        this(ModEntities.COLORED_ITEM_FRAME.get(), level, pos);
        this.setDirection(direction);
    }

    public ColoredItemFrameEntity(Level level, BlockPos pos, Direction direction, String color) {
        this(ModEntities.COLORED_ITEM_FRAME.get(), level, pos);
        this.setColorVariant(color);
        this.setDirection(direction);
    }

    public ColoredItemFrameEntity(EntityType<? extends ColoredItemFrameEntity> entityType, Level level, BlockPos pos) {
        super(entityType, level, pos);
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_ITEM, ItemStack.EMPTY);
        this.getEntityData().define(DATA_ROTATION, 0);
        this.getEntityData().define(DATA_COLOR, "white");
    }

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(x, y, z);
        if (this.direction != null) {
            this.recalculateBoundingBox();
        }
    }

    @Override
    public void setDirection(Direction direction) {
        if (direction != null) {
            try {
                java.lang.reflect.Field directionField = HangingEntity.class.getDeclaredField("direction");
                directionField.setAccessible(true);
                directionField.set(this, direction);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Failed to set direction for ColoredItemFrameEntity: " + e.getMessage(), e);
            }

            if (this.pos != null) {
                this.recalculateBoundingBox();
            }
        }
    }

    @Override
    protected void recalculateBoundingBox() {
        if (this.direction != null && this.pos != null) {
            double d0 = (double) this.pos.getX() + 0.5D;
            double d1 = (double) this.pos.getY() + 0.5D;
            double d2 = (double) this.pos.getZ() + 0.5D;
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
                d6 = (double) this.getWidth();
                d7 = (double) this.getHeight();
                d8 = 2.0D;
            } else if (this.direction.getAxis() == Direction.Axis.X) {
                d6 = 2.0D;
                d7 = (double) this.getHeight();
                d8 = (double) this.getWidth();
            } else {
                d6 = (double) this.getWidth();
                d7 = 2.0D;
                d8 = (double) this.getHeight();
            }

            d6 /= 32.0D;
            d7 /= 32.0D;
            d8 /= 32.0D;

            this.setBoundingBox(new AABB(d0 - d6, d1 - d7, d2 - d8, d0 + d6, d1 + d7, d2 + d8));
        }
    }

    private double offs(int size) {
        return size % 32 == 0 ? 0.5D : 0.0D;
    }

    @Override
    public int getWidth() {
        return 12;
    }

    @Override
    public int getHeight() {
        return 12;
    }

    public String getColorVariant() {
        return this.getEntityData().get(DATA_COLOR);
    }

    public void setColorVariant(String color) {
        this.getEntityData().set(DATA_COLOR, color);
    }

    public ItemStack getItem() {
        return this.getEntityData().get(DATA_ITEM);
    }

    public void setItem(ItemStack stack) {
        this.setItem(stack, true);
    }

    public void setItem(ItemStack stack, boolean playSound) {
        if (!stack.isEmpty()) {
            stack = stack.copy();
            stack.setCount(1);
            stack.setEntityRepresentation(this);
        }
        this.getEntityData().set(DATA_ITEM, stack);
        if (!stack.isEmpty() && playSound) {
            this.playSound(SoundEvents.ITEM_FRAME_ADD_ITEM, 1.0F, 1.0F);
        }
        if (this.pos != null) {
            this.level.updateNeighbourForOutputSignal(this.pos, Blocks.AIR);
        }
    }

    public int getRotation() {
        return this.getEntityData().get(DATA_ROTATION);
    }

    public void setRotation(int rotation) {
        this.setRotation(rotation, true);
    }

    private void setRotation(int rotation, boolean playSound) {
        this.getEntityData().set(DATA_ROTATION, rotation % 8);
        if (playSound && !this.getItem().isEmpty()) {
            this.playSound(SoundEvents.ITEM_FRAME_ROTATE_ITEM, 1.0F, 1.0F);
        }
        if (this.pos != null) {
            this.level.updateNeighbourForOutputSignal(this.pos, Blocks.AIR);
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);
        ItemStack frameItem = this.getItem();
        boolean hasItemInFrame = !frameItem.isEmpty();

        if (!this.level.isClientSide) {
            if (!hasItemInFrame) {
                if (!heldItem.isEmpty()) {
                    this.setItem(heldItem);
                    if (!player.getAbilities().instabuild) {
                        heldItem.shrink(1);
                    }
                }
            } else {
                this.setRotation(this.getRotation() + 1);
            }
        }

        return InteractionResult.sidedSuccess(this.level.isClientSide);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (!source.isExplosion() && !this.getItem().isEmpty()) {
            if (!this.level.isClientSide) {
                this.dropItem(source.getEntity(), false);
                this.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
            }
            return true;
        } else {
            return super.hurt(source, amount);
        }
    }

    @Override
    public void dropItem(@Nullable Entity entity) {
        this.playSound(SoundEvents.ITEM_FRAME_BREAK, 1.0F, 1.0F);
        this.dropItem(entity, true);
    }

    private void dropItem(@Nullable Entity entity, boolean dropSelf) {
        if (!this.fixed) {
            ItemStack itemstack = this.getItem();
            this.setItem(ItemStack.EMPTY);

            if (!this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                if (entity == null) {
                    this.removeFramedMap(itemstack);
                }
            } else {
                if (entity instanceof Player) {
                    Player player = (Player) entity;
                    if (player.getAbilities().instabuild) {
                        this.removeFramedMap(itemstack);
                        return;
                    }
                }

                if (dropSelf) {
                    this.spawnAtLocation(this.getFrameItemForColor(this.getColorVariant()));
                }

                if (!itemstack.isEmpty()) {
                    itemstack = itemstack.copy();
                    this.removeFramedMap(itemstack);
                    this.spawnAtLocation(itemstack);
                }
            }
        }
    }

    private void removeFramedMap(ItemStack stack) {
        if (stack.is(Items.FILLED_MAP)) {
            // Maps handle special framing logic, clear it
            stack.setEntityRepresentation(null);
        }
    }

    @Override
    public void playPlacementSound() {
        this.playSound(SoundEvents.ITEM_FRAME_PLACE, 1.0F, 1.0F);
    }

    @Override
    public boolean survives() {
        // Simplified check - just verify we have valid pos and direction
        if (this.pos == null || this.direction == null) {
            return false;
        }
        // Check the block behind is solid
        BlockPos blockpos = this.pos.relative(this.direction.getOpposite());
        return this.level.getBlockState(blockpos).getMaterial().isSolid();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (!this.getItem().isEmpty()) {
            tag.put("Item", this.getItem().save(new CompoundTag()));
            tag.putByte("ItemRotation", (byte) this.getRotation());
            tag.putFloat("ItemDropChance", this.dropChance);
        }
        tag.putString("ColorVariant", this.getColorVariant());
        tag.putByte("Facing", (byte) this.direction.get3DDataValue());
        tag.putBoolean("Invisible", this.isInvisible());
        tag.putBoolean("Fixed", this.fixed);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        CompoundTag itemTag = tag.getCompound("Item");
        if (itemTag != null && !itemTag.isEmpty()) {
            ItemStack itemstack = ItemStack.of(itemTag);
            if (itemstack.isEmpty()) {
                this.setItem(ItemStack.EMPTY, false);
            } else {
                this.setItem(itemstack, false);
            }
            this.setRotation(tag.getByte("ItemRotation"), false);
            if (tag.contains("ItemDropChance", 99)) {
                this.dropChance = tag.getFloat("ItemDropChance");
            }
        }

        if (tag.contains("ColorVariant")) {
            this.setColorVariant(tag.getString("ColorVariant"));
        }

        this.setDirection(Direction.from3DDataValue(tag.getByte("Facing")));
        this.setInvisible(tag.getBoolean("Invisible"));
        this.fixed = tag.getBoolean("Fixed");
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, this.direction != null ? this.direction.get3DDataValue() : Direction.NORTH.get3DDataValue());
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        this.setDirection(Direction.from3DDataValue(packet.getData()));
    }

    @Override
    public ItemStack getPickResult() {
        return this.getFrameItemForColor(this.getColorVariant());
    }

    public int getAnalogOutput() {
        return this.getItem().isEmpty() ? 0 : this.getRotation() % 8 + 1;
    }

    private ItemStack getFrameItemForColor(String color) {
        switch (color) {
            case "black":
                return new ItemStack(com.kingodogo.buildscape.item.ModItems.BLACK_ITEM_FRAME.get());
            case "blue":
                return new ItemStack(com.kingodogo.buildscape.item.ModItems.BLUE_ITEM_FRAME.get());
            case "brown":
                return new ItemStack(com.kingodogo.buildscape.item.ModItems.BROWN_ITEM_FRAME.get());
            case "cyan":
                return new ItemStack(com.kingodogo.buildscape.item.ModItems.CYAN_ITEM_FRAME.get());
            case "gray":
                return new ItemStack(com.kingodogo.buildscape.item.ModItems.GRAY_ITEM_FRAME.get());
            case "green":
                return new ItemStack(com.kingodogo.buildscape.item.ModItems.GREEN_ITEM_FRAME.get());
            case "light_blue":
                return new ItemStack(com.kingodogo.buildscape.item.ModItems.LIGHT_BLUE_ITEM_FRAME.get());
            case "light_gray":
                return new ItemStack(com.kingodogo.buildscape.item.ModItems.LIGHT_GRAY_ITEM_FRAME.get());
            case "lime":
                return new ItemStack(com.kingodogo.buildscape.item.ModItems.LIME_ITEM_FRAME.get());
            case "magenta":
                return new ItemStack(com.kingodogo.buildscape.item.ModItems.MAGENTA_ITEM_FRAME.get());
            case "orange":
                return new ItemStack(com.kingodogo.buildscape.item.ModItems.ORANGE_ITEM_FRAME.get());
            case "pink":
                return new ItemStack(com.kingodogo.buildscape.item.ModItems.PINK_ITEM_FRAME.get());
            case "purple":
                return new ItemStack(com.kingodogo.buildscape.item.ModItems.PURPLE_ITEM_FRAME.get());
            case "red":
                return new ItemStack(com.kingodogo.buildscape.item.ModItems.RED_ITEM_FRAME.get());
            case "white":
                return new ItemStack(com.kingodogo.buildscape.item.ModItems.WHITE_ITEM_FRAME.get());
            case "yellow":
                return new ItemStack(com.kingodogo.buildscape.item.ModItems.YELLOW_ITEM_FRAME.get());
            default:
                return new ItemStack(com.kingodogo.buildscape.item.ModItems.WHITE_ITEM_FRAME.get());
        }
    }
}
