package com.kingodogo.buildscape.entity;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class FallingIcicleEntity extends Entity {

    private BlockState blockState = Blocks.AIR.defaultBlockState();
    private boolean hasLanded = false;
    private int fallTime = 0;
    private BlockPos startPos;

    public FallingIcicleEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public FallingIcicleEntity(
            Level level,
            double x,
            double y,
            double z,
            BlockState state
    ) {
        this(ModEntities.FALLING_ICICLE.get(), level);
        this.blockState = state;
        this.setPos(x, y, z);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.blocksBuilding = true;
        this.startPos = this.blockPosition();
    }

    public BlockPos getStartPos() {
        return this.startPos != null ? this.startPos : this.blockPosition();
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public void tick() {
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
        }

        this.move(MoverType.SELF, this.getDeltaMovement());

        if (!this.level.isClientSide && fallTime > 2) {
            checkEntityCollision();
        }

        this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));

        fallTime++;

        if (this.onGround && !hasLanded) {
            hasLanded = true;

            if (!this.level.isClientSide && !blockState.isAir()) {
                ItemStack itemStack = new ItemStack(blockState.getBlock().asItem(), 1);
                ItemEntity itemEntity = new ItemEntity(
                        this.level,
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        itemStack
                );
                itemEntity.setDefaultPickUpDelay();
                this.level.addFreshEntity(itemEntity);

                this.level.playSound(
                        null,
                        this.blockPosition(),
                        blockState.getSoundType().getBreakSound(),
                        net.minecraft.sounds.SoundSource.BLOCKS,
                        1.0f,
                        1.0f
                );

                this.level.levelEvent(
                        2001,
                        this.blockPosition(),
                        Block.getId(blockState)
                );
            }

            this.discard();
            return;
        }

        if (fallTime > 600 || this.getY() < this.level.getMinBuildHeight() - 64) {
            this.discard();
        }
    }

    private void checkEntityCollision() {
        AABB boundingBox = this.getBoundingBox().inflate(0.25, 0.5, 0.25);

        Vec3 motion = this.getDeltaMovement();
        if (motion.y < 0) {
            boundingBox = boundingBox.expandTowards(0, motion.y, 0);
        }

        List<LivingEntity> entities =
                this.level.getEntitiesOfClass(LivingEntity.class, boundingBox);

        for (LivingEntity entity : entities) {
            float fallDistance = (float) (this.startPos != null
                    ? this.startPos.getY() - this.getY()
                    : fallTime * 0.04);
            float damage = Math.min(Math.max(2.0f, fallDistance * 2.0f), 40.0f);

            boolean damaged = entity.hurt(DamageSource.FALLING_STALACTITE, damage);

            if (damaged) {
                entity.setDeltaMovement(entity.getDeltaMovement().add(0, -0.3, 0));
            }

            if (!blockState.isAir()) {
                ItemStack itemStack = new ItemStack(blockState.getBlock().asItem(), 1);
                ItemEntity itemEntity = new ItemEntity(
                        this.level,
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        itemStack
                );
                itemEntity.setDefaultPickUpDelay();
                this.level.addFreshEntity(itemEntity);

                this.level.playSound(
                        null,
                        this.blockPosition(),
                        blockState.getSoundType().getBreakSound(),
                        net.minecraft.sounds.SoundSource.BLOCKS,
                        1.0f,
                        1.0f
                );

                this.level.levelEvent(
                        2001,
                        this.blockPosition(),
                        Block.getId(blockState)
                );
            }

            this.discard();
            return;
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.blockState = NbtUtils.readBlockState(tag.getCompound("BlockState"));
        this.fallTime = tag.getInt("FallTime");
        if (tag.contains("StartX")) {
            this.startPos = new BlockPos(
                    tag.getInt("StartX"),
                    tag.getInt("StartY"),
                    tag.getInt("StartZ")
            );
        }
        if (this.blockState.isAir()) {
            this.blockState = Blocks.ICE.defaultBlockState();
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.put("BlockState", NbtUtils.writeBlockState(this.blockState));
        tag.putInt("FallTime", this.fallTime);
        if (this.startPos != null) {
            tag.putInt("StartX", this.startPos.getX());
            tag.putInt("StartY", this.startPos.getY());
            tag.putInt("StartZ", this.startPos.getZ());
        }
    }

    public BlockState getBlockState() {
        return this.blockState;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, Block.getId(this.blockState));
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        this.blockState = Block.stateById(packet.getData());
    }

    @Override
    public boolean isAttackable() {
        return false;
    }
}
