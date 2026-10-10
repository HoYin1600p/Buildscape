package com.kingodogo.buildscape.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class SeatEntity extends Entity {

    public SeatEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public SeatEntity(Level level, double x, double y, double z) {
        this(ModEntities.SEAT_ENTITY.get(), level);
        this.setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level.isClientSide) {
            if (this.getPassengers().isEmpty()) {
                this.discard();
            }
        }
    }

    @Override
    public double getPassengersRidingOffset() {
        return 0.0;
    }

    @Override
    protected boolean canRide(Entity entity) {
        return true;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public static void createSeat(Level level, double x, double y, double z, Player player) {
        if (!level.isClientSide) {
            SeatEntity seat = new SeatEntity(level, x, y, z);
            level.addFreshEntity(seat);
            player.startRiding(seat);
        }
    }
}
