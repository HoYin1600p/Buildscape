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
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nullable;

public class ColoredItemFrameEntity extends HangingEntity {

    private static final EntityDataAccessor<ItemStack> DATA_ITEM = SynchedEntityData
            .defineId(ColoredItemFrameEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Integer> DATA_ROTATION = SynchedEntityData
            .defineId(ColoredItemFrameEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> DATA_COLOR = SynchedEntityData
            .defineId(ColoredItemFrameEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> DATA_PARTICLE_PATTERN = SynchedEntityData
            .defineId(ColoredItemFrameEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> DATA_PARTICLE_COLORS = SynchedEntityData
            .defineId(ColoredItemFrameEntity.class, EntityDataSerializers.STRING);

    private float dropChance = 1.0F;
    private boolean fixed = false;

    public ColoredItemFrameEntity(EntityType<? extends ColoredItemFrameEntity> entityType, Level level) {
        super(entityType, level);
    }

    public ColoredItemFrameEntity(EntityType<? extends ColoredItemFrameEntity> entityType, Level level, BlockPos pos,
                                  Direction direction) {
        super(entityType, level, pos);
        this.setDirection(direction);
    }

    public ColoredItemFrameEntity(Level level, BlockPos pos, Direction direction) {
        this(ModEntities.COLORED_ITEM_FRAME.get(), level, pos, direction);
    }

    public ColoredItemFrameEntity(Level level, BlockPos pos, Direction direction, String color) {
        this(ModEntities.COLORED_ITEM_FRAME.get(), level, pos, direction);
        this.setColorVariant(color);
        if ("invisible".equals(color)) {
            this.setInvisible(true);
        }
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_ITEM, ItemStack.EMPTY);
        this.getEntityData().define(DATA_ROTATION, 0);
        this.getEntityData().define(DATA_COLOR, "white");
        this.getEntityData().define(DATA_PARTICLE_PATTERN, "none");
        this.getEntityData().define(DATA_PARTICLE_COLORS, "");
    }

    @Override
    protected void setDirection(Direction direction) {
        Validate.notNull(direction);
        this.direction = direction;
        if (direction.getAxis().isHorizontal()) {
            this.setXRot(0.0F);
            this.setYRot((float) (this.direction.get2DDataValue() * 90));
        } else {
            this.setXRot((float) (-90 * direction.getAxisDirection().getStep()));
            this.setYRot(0.0F);
        }
        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();
        this.recalculateBoundingBox();
    }

    @Override
    protected void recalculateBoundingBox() {
        if (this.direction == null) {
            return;
        }

        double x = (double) this.pos.getX() + 0.5D - (double) this.direction.getStepX() * 0.46875D;
        double y = (double) this.pos.getY() + 0.5D - (double) this.direction.getStepY() * 0.46875D;
        double z = (double) this.pos.getZ() + 0.5D - (double) this.direction.getStepZ() * 0.46875D;
        this.setPosRaw(x, y, z);

        double w = this.getWidth();
        double h = this.getHeight();
        double d = this.getWidth();

        Direction.Axis axis = this.direction.getAxis();
        switch (axis) {
            case X:
                w = 1.0D;
                break;
            case Y:
                h = 1.0D;
                break;
            case Z:
                d = 1.0D;
                break;
        }

        w /= 32.0D;
        h /= 32.0D;
        d /= 32.0D;

        this.setBoundingBox(new AABB(x - w, y - h, z - d, x + w, y + h, z + d));
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

    public String getParticlePattern() {
        return this.getEntityData().get(DATA_PARTICLE_PATTERN);
    }

    public void setParticlePattern(String pattern) {
        this.getEntityData().set(DATA_PARTICLE_PATTERN, (pattern == null || pattern.isEmpty()) ? "none" : pattern);
    }

    public String getParticleColorsRaw() {
        return this.getEntityData().get(DATA_PARTICLE_COLORS);
    }

    public void setParticleColorsRaw(String colors) {
        this.getEntityData().set(DATA_PARTICLE_COLORS, colors == null ? "" : colors);
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
        boolean hasItemInHand = !heldItem.isEmpty();

        if (this.fixed) {
            return InteractionResult.PASS;
        } else if (!this.level.isClientSide) {
            if (!hasItemInFrame) {
                if (hasItemInHand && !this.isRemoved()) {
                    if (heldItem.is(Items.FILLED_MAP)) {
                        MapItemSavedData mapData = MapItem.getSavedData(heldItem, this.level);
                        if (mapData != null && mapData.isTrackedCountOverLimit(256)) {
                            return InteractionResult.FAIL;
                        }
                    }
                    this.setItem(heldItem);
                    if (!player.getAbilities().instabuild) {
                        heldItem.shrink(1);
                    }
                }
            } else {
                this.playSound(SoundEvents.ITEM_FRAME_ROTATE_ITEM, 1.0F, 1.0F);
                this.setRotation(this.getRotation() + 1);
            }
            return InteractionResult.CONSUME;
        } else {
            return !hasItemInFrame && !hasItemInHand ? InteractionResult.PASS : InteractionResult.SUCCESS;
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.fixed) {
            return (source == DamageSource.OUT_OF_WORLD || source.isCreativePlayer()) && super.hurt(source, amount);
        } else if (this.isInvulnerableTo(source)) {
            return false;
        } else if (!source.isExplosion() && !this.getItem().isEmpty()) {
            if (!this.level.isClientSide) {
                this.dropItem(source.getEntity(), false);
                this.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
                this.gameEvent(net.minecraft.world.level.gameevent.GameEvent.BLOCK_CHANGE, source.getEntity());
            }
            return true;
        } else {
            return super.hurt(source, amount);
        }
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean skipAttackInteraction(Entity entity) {
        if (entity instanceof Player player) {
            return !this.level.mayInteract(player, this.pos);
        }
        return false;
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
                if (entity instanceof Player player) {
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
                    if (this.random.nextFloat() < this.dropChance) {
                        this.spawnAtLocation(itemstack);
                    }
                }
            }
        }
    }

    private void removeFramedMap(ItemStack stack) {
        if (stack.is(Items.FILLED_MAP)) {
            MapItemSavedData mapData = MapItem.getSavedData(stack, this.level);
            if (mapData != null) {
                mapData.removedFromFrame(this.pos, this.getId());
                mapData.setDirty(true);
            }
        }
        stack.setEntityRepresentation(null);
    }

    @Override
    public void playPlacementSound() {
        this.playSound(SoundEvents.ITEM_FRAME_PLACE, 1.0F, 1.0F);
    }

    @Override
    public boolean survives() {
        if (this.fixed) {
            return true;
        } else if (!this.level.noCollision(this)) {
            return false;
        } else {
            BlockState blockstate = this.level.getBlockState(this.pos.relative(this.direction.getOpposite()));
            return (blockstate.getMaterial().isSolid()
                    || (this.direction.getAxis().isHorizontal() && DiodeBlock.isDiode(blockstate)))
                    && this.level.getEntities(this, this.getBoundingBox(), HANGING_ENTITY).isEmpty();
        }
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

        // Save particle pattern and colors from synchronized data
        tag.putString("BuildScapeParticlePattern", this.getParticlePattern());
        tag.putString("BuildScapeParticleColorsRaw", this.getParticleColorsRaw());

        CompoundTag persistentData = this.getPersistentData();
        if (persistentData.contains("BuildScapeFrameId", 8)) {
            tag.putString("BuildScapeFrameId", persistentData.getString("BuildScapeFrameId"));
        }
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
        // Handle ITEM tag from /give or /summon commands (custom NBT format)
        else if (tag.contains("ITEM", 8)) {
            String itemId = tag.getString("ITEM");
            try {
                net.minecraft.resources.ResourceLocation itemLocation =
                        new net.minecraft.resources.ResourceLocation(itemId);
                net.minecraft.world.item.Item item =
                        net.minecraftforge.registries.ForgeRegistries.ITEMS.getValue(itemLocation);
                if (item != null && item != net.minecraft.world.item.Items.AIR) {
                    ItemStack itemstack = new ItemStack(item);
                    this.setItem(itemstack, false);
                }
            } catch (Exception e) {
                // Invalid item ID, ignore
            }
        }

        // Load particle pattern and colors from saved data
        if (tag.contains("BuildScapeParticlePattern", 8)) {
            this.setParticlePattern(tag.getString("BuildScapeParticlePattern"));
        }
        if (tag.contains("BuildScapeParticleColorsRaw", 8)) {
            this.setParticleColorsRaw(tag.getString("BuildScapeParticleColorsRaw"));
        }

        CompoundTag persistentData = this.getPersistentData();
        if (tag.contains("BuildScapeFrameId", 8)) {
            persistentData.putString("BuildScapeFrameId", tag.getString("BuildScapeFrameId"));
        }

        // Handle PATTERN tag from /give or /summon commands (custom NBT format)
        if (tag.contains("PATTERN", 8)) {
            String pattern = tag.getString("PATTERN");
            this.setParticlePattern(pattern);
        }

        // Handle COLORS tag from /give or /summon commands (custom NBT format)
        if (tag.contains("COLORS", 9)) {
            net.minecraft.nbt.ListTag colorList = tag.getList("COLORS", 8);
            if (colorList.size() > 0) {
                java.util.List<String> colors = new java.util.ArrayList<>();
                for (int i = 0; i < colorList.size(); i++) {
                    colors.add(colorList.getString(i));
                }
                this.setParticleColorsRaw(String.join(";", colors));
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
        return new ClientboundAddEntityPacket(this, this.getType(), this.direction.get3DDataValue(), this.getPos());
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        this.setDirection(Direction.from3DDataValue(packet.getData()));
    }

    @Override
    public ItemStack getPickResult() {
        ItemStack frameItem = this.getFrameItemForColor(this.getColorVariant());
        ItemStack displayedItem = this.getItem();
        CompoundTag tag = frameItem.getOrCreateTag();
        boolean hasCustomData = false;

        // If the frame has an item, add it to the NBT so it persists when placed
        if (!displayedItem.isEmpty()) {
            net.minecraft.resources.ResourceLocation itemId =
                    net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(displayedItem.getItem());
            if (itemId != null) {
                tag.putString("ITEM", itemId.toString());
                hasCustomData = true;
            }
        }

        // Preserve particle pattern from persistent data
        CompoundTag persistentData = this.getPersistentData();
        if (persistentData.contains("BuildScapeParticlePattern", 8)) {
            tag.putString("PATTERN", persistentData.getString("BuildScapeParticlePattern"));
            hasCustomData = true;
        }

        // Preserve particle colors from persistent data
        if (persistentData.contains("BuildScapeParticleColors", 9)) {
            net.minecraft.nbt.ListTag colorList = persistentData.getList("BuildScapeParticleColors", 8);
            if (colorList.size() > 0) {
                tag.put("COLORS", colorList.copy());
                hasCustomData = true;
            }
        }

        if (hasCustomData) {
            frameItem.setTag(tag);
        }

        return frameItem;
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
            case "invisible":
                return new ItemStack(com.kingodogo.buildscape.item.ModItems.INVISIBLE_ITEM_FRAME.get());
            default:
                return new ItemStack(com.kingodogo.buildscape.item.ModItems.WHITE_ITEM_FRAME.get());
        }
    }
}
