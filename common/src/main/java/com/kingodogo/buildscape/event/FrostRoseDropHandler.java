package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
public final class FrostRoseDropHandler {

    private static final List<TrackedDeath> TRACKED_DEATHS = new ArrayList<>();

    private FrostRoseDropHandler() {}
    public static void onLivingDeath(LivingEntity entity, DamageSource source) {
        if (!EntityType.getKey(entity.getType()).getPath().equals("snow_golem")) return;
        Level entityLevel = Services.PLATFORM.getEntityLevel(entity);
        if (entityLevel.isClientSide()) return;
        if (source.getEntity() == null || !(source.getEntity() instanceof WitherBoss)) return;

        ServerLevel level = (ServerLevel) entityLevel;
        BlockPos deathPos = entity.blockPosition();
        double deathX = entity.getX();
        double deathY = entity.getY();
        double deathZ = entity.getZ();

        Block frostRoseBlock = Services.PLATFORM.getBlock(new CommonId("buildscape", "frost_rose"));
        if (frostRoseBlock == null || frostRoseBlock == Blocks.AIR) return;

        ItemStack frostRose = new ItemStack(frostRoseBlock);
        ItemEntity itemEntity = new ItemEntity(
                level,
                deathPos.getX() + 0.5,
                deathPos.getY() + 0.5,
                deathPos.getZ() + 0.5,
                frostRose
        );
        level.addFreshEntity(itemEntity);

        TRACKED_DEATHS.add(new TrackedDeath(level.dimension(), deathPos, deathX, deathY, deathZ, 20));
    }
    public static boolean shouldCancelItemSpawn(ItemEntity itemEntity) {
        Level itemLevel = Services.PLATFORM.getEntityLevel(itemEntity);
        if (itemLevel.isClientSide()) return false;
        ItemStack stack = itemEntity.getItem();
        if (!stack.is(Items.WITHER_ROSE)) return false;

        for (TrackedDeath death : TRACKED_DEATHS) {
            if (death.dimension != itemLevel.dimension()) continue;
            double dx = itemEntity.getX() - death.x;
            double dy = itemEntity.getY() - death.y;
            double dz = itemEntity.getZ() - death.z;
            if (dx * dx + dy * dy + dz * dz < 4.0) {
                return true;
            }
        }
        return false;
    }
    public static void onServerStopping() {
        TRACKED_DEATHS.clear();
    }
    public static void onServerTick(MinecraftServer server) {
        if (TRACKED_DEATHS.isEmpty()) return;

        Iterator<TrackedDeath> it = TRACKED_DEATHS.iterator();
        while (it.hasNext()) {
            TrackedDeath death = it.next();
            ServerLevel level = server.getLevel(death.dimension);
            if (level == null || !level.isLoaded(death.pos)) {
                it.remove();
                continue;
            }

            if (level.getBlockState(death.pos).is(Blocks.WITHER_ROSE)) {
                level.removeBlock(death.pos, false);
                it.remove();
            } else if (level.getBlockState(death.pos.above()).is(Blocks.WITHER_ROSE)) {
                level.removeBlock(death.pos.above(), false);
                it.remove();
            } else {
                death.ticksRemaining--;
                if (death.ticksRemaining <= 0) {
                    it.remove();
                }
            }
        }
    }

    private static class TrackedDeath {
        final ResourceKey<Level> dimension;
        final BlockPos pos;
        final double x, y, z;
        int ticksRemaining;

        TrackedDeath(ResourceKey<Level> dimension, BlockPos pos, double x, double y, double z, int ticks) {
            this.dimension = dimension;
            this.pos = pos;
            this.x = x;
            this.y = y;
            this.z = z;
            this.ticksRemaining = ticks;
        }
    }
}
