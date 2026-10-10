package com.kingodogo.buildscape.dispenser;

import com.kingodogo.buildscape.particle.ModParticles;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;

public final class DispenserEffects {
    private DispenserEffects() {}

    public static ItemStack mist(ServerLevel level, BlockPos pos, Direction facing, ItemStack stack) {
        double cx = pos.getX() + .5 + facing.getStepX() * .5;
        double cy = pos.getY() + .5 + facing.getStepY() * .5;
        double cz = pos.getZ() + .5 + facing.getStepZ() * .5;
        for (int i = 0; i < 40; i++) {
            level.sendParticles(ModParticles.CASCADE.get(), cx + (level.getRandom().nextDouble() - .5) * 2,
                    cy + level.getRandom().nextDouble() - .5, cz + (level.getRandom().nextDouble() - .5) * 2,
                    1, (level.getRandom().nextDouble() - .5) * .2, level.getRandom().nextDouble() * .05,
                    (level.getRandom().nextDouble() - .5) * .2, 0);
        }
        level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, .1F, .1F);
        stack.shrink(1);
        return stack;
    }

    public static ItemStack confetti(ServerLevel level, BlockPos pos, Direction facing, ItemStack stack, int slot) {
        double offset = (slot - 4) * .15;
        Direction right = facing.getAxis().isHorizontal() ? facing.getClockWise() : Direction.EAST;
        double dx = facing.getStepX() + right.getStepX() * offset;
        double dy = facing.getStepY();
        double dz = facing.getStepZ() + right.getStepZ() * offset;
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        dx /= length;
        dy /= length;
        dz /= length;
        var data = Services.PLATFORM.getCustomData(stack, false);
        int burst = data == null ? 1 : Math.clamp(Services.PLATFORM.getTagInt(data, "BurstLevel", 1), 1, 5);
        int count = (65 + level.getRandom().nextInt(36)) * burst;
        for (int i = 0; i < count; i++) {
            double speed = (.08 + level.getRandom().nextDouble() * .12) * (1 + (burst - 1) * .2);
            double spread = (.04 + level.getRandom().nextDouble() * .06) * (1 + (burst - 1) * .1);
            level.sendParticles(ModParticles.CONFETTI.get(), pos.getX() + .5 + (level.getRandom().nextDouble() - .5) * .15,
                    pos.getY() + .5 + (level.getRandom().nextDouble() - .5) * .15,
                    pos.getZ() + .5 + (level.getRandom().nextDouble() - .5) * .15, 0,
                    dx * speed + (level.getRandom().nextDouble() - .5) * spread,
                    dy * speed + (level.getRandom().nextDouble() - .5) * spread + (facing.getAxis().isHorizontal() ? .05 : 0),
                    dz * speed + (level.getRandom().nextDouble() - .5) * spread, 1);
        }
        level.playSound(null, pos, SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.BLOCKS, .8F, 1.4F);
        level.playSound(null, pos, SoundEvents.FIREWORK_ROCKET_TWINKLE, SoundSource.BLOCKS, .6F, 1.6F);
        stack.shrink(1);
        return stack;
    }
}
