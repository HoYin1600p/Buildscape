package com.kingodogo.buildscape.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

/**
 * An invisible entity that a player can "ride" to sit on blocks like log slabs.
 */
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
        // No data to sync
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        // No data to save
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        // No data to save
    }

    @Override
    public void tick() {
        super.tick();
        
        // Remove the seat if it has no passengers (e.g. player dismounted)
        if (!this.level.isClientSide) {
            if (this.getPassengers().isEmpty()) {
                this.discard();
            }
        }
    }

    @Override
    public double getPassengersRidingOffset() {
        // Adjust the height of the player relative to the entity
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

    /**
     * Helper to spawn a seat and make the player sit on it.
     */
    public static void createSeat(Level level, double x, double y, double z, Player player) {
        if (!level.isClientSide) {
            SeatEntity seat = new SeatEntity(level, x, y, z);
            level.addFreshEntity(seat);
            player.startRiding(seat);
        }
    }
}
