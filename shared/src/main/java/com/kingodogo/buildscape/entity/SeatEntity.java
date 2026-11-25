package com.kingodogo.buildscape.entity;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
public interface SeatEntity {
    static void createSeat(Level level, double x, double y, double z, Player player) {
        if (!level.isClientSide()) {
            Entity seat = Services.PLATFORM.createSeatEntity(level, x, y, z);
            if (seat != null) {
                level.addFreshEntity(seat);
                player.startRiding(seat);
            }
        }
    }
}
