package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.block.*;
import com.kingodogo.buildscape.entity.ModEntities;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.time.LocalDate;
import java.util.UUID;

public final class WanderingHomemakerSpawningLogic {

    private WanderingHomemakerSpawningLogic() {}

    private enum BlockCategory {
        DECORATED_POT,
        PILLAR,
        STAR,
        CUSHION,
        NONE
    }

    private static BlockCategory getCategory(Block block) {
        if (block instanceof DecoratedPotBlock || block instanceof TrappedDecoratedPotBlock) return BlockCategory.DECORATED_POT;
        if (block instanceof PillarBlock || block instanceof AshenKingPillarBlock) return BlockCategory.PILLAR;
        if (block instanceof StarBlock) return BlockCategory.STAR;
        if (block instanceof CushionBlock) return BlockCategory.CUSHION;
        return BlockCategory.NONE;
    }

    private static final int[][] OFFSETS_1 = {{0, 0}, {1, 0}, {0, 1}, {1, 1}};
    private static final int[][] OFFSETS_2 = {{-1, 0}, {0, 0}, {-1, 1}, {0, 1}};
    private static final int[][] OFFSETS_3 = {{0, -1}, {1, -1}, {0, 0}, {1, 0}};
    private static final int[][] OFFSETS_4 = {{-1, -1}, {0, -1}, {-1, 0}, {0, 0}};
    private static final int[][][] ALL_SQUARES = {OFFSETS_1, OFFSETS_2, OFFSETS_3, OFFSETS_4};

    private static boolean formsSquare(Level level, BlockPos pos, BlockCategory category) {
        if (category == BlockCategory.NONE) return false;

        for (int[][] square : ALL_SQUARES) {
            boolean match = true;
            for (int[] offset : square) {
                BlockPos p = pos.offset(offset[0], 0, offset[1]);
                BlockState state = level.getBlockState(p);
                if (getCategory(state.getBlock()) != category) {
                    match = false;
                    break;
                }
            }
            if (match) {
                return true;
            }
        }
        return false;
    }

    public static void onBlockPlaced(Level level, BlockPos pos, BlockState state, Player player) {
        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel) || player == null) {
            return;
        }

        BlockCategory category = getCategory(state.getBlock());
        if (category == BlockCategory.NONE) {
            return;
        }

        if (formsSquare(level, pos, category)) {
            long currentTime = System.currentTimeMillis();
            long cooldown = 0;

            CompoundTag data = Services.PLATFORM.getEntityData(player);
            cooldown = Services.PLATFORM.getTagLong(data, "WanderingHomemakerCooldownRealTime", 0L);

            if (currentTime < cooldown) {
                return;
            }

            if (Services.PLATFORM.hasTagUUID(data, "WanderingHomemakerUUID")) {
                UUID oldUuid = Services.PLATFORM.getTagUUID(data, "WanderingHomemakerUUID");
                if (oldUuid != null) {
                    for (ServerLevel sl : serverLevel.getServer().getAllLevels()) {
                        Entity oldEntity = sl.getEntity(oldUuid);
                        if (oldEntity != null && oldEntity.isAlive()) {
                            oldEntity.discard();
                        }
                    }
                }
            }

            LocalDate today = LocalDate.now();
            int month = today.getMonthValue();
            int day = today.getDayOfMonth();

            boolean spawnFestive = false;
            if (month == 12) {
                if (day == 24 || day == 25) {
                    spawnFestive = level.getRandom().nextFloat() < 0.99f;
                } else {
                    spawnFestive = level.getRandom().nextFloat() < 0.50f;
                }
            }

            String entityName = spawnFestive ? "festive_wandering_homemaker" : "wandering_homemaker";
            double angle = level.getRandom().nextDouble() * 2.0D * Math.PI;
            double distance = 2.0D + level.getRandom().nextDouble() * 1.0D;
            double spawnX = pos.getX() + 0.5D + Math.cos(angle) * distance;
            double spawnY = pos.getY();
            double spawnZ = pos.getZ() + 0.5D + Math.sin(angle) * distance;

            BlockPos spawnPos = new BlockPos((int) spawnX, (int) spawnY, (int) spawnZ);
            Services.PLATFORM.playEvokerPrepareSummon(serverLevel, spawnPos);

            Entity homemaker = Services.PLATFORM.createWanderingHomemakerEntity(serverLevel, spawnFestive);
            if (homemaker != null) {
                homemaker.setPos(spawnX, spawnY, spawnZ);
                homemaker.setYRot(level.getRandom().nextFloat() * 360F);
                serverLevel.addFreshEntity(homemaker);
                Services.PLATFORM.putTagUUID(data, "WanderingHomemakerUUID", homemaker.getUUID());
            }

            for (int i = 0; i < 20; i++) {
                double px = spawnX + (level.getRandom().nextDouble() - 0.5D) * 1.5D;
                double py = spawnY + 0.5D + level.getRandom().nextDouble() * 2.0D;
                double pz = spawnZ + (level.getRandom().nextDouble() - 0.5D) * 1.5D;
                serverLevel.sendParticles(ParticleTypes.CLOUD, px, py, pz, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }

            long cooldownEnd = currentTime + 1800000L;
            data.putLong("WanderingHomemakerCooldownRealTime", cooldownEnd);
        }
    }
}
