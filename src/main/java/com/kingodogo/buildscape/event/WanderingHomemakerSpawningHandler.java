package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.*;
import com.kingodogo.buildscape.entity.ModEntities;
import com.kingodogo.buildscape.entity.WanderingHomemakerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WanderingHomemakerSpawningHandler {

    private enum BlockCategory {
        DECORATED_POT,
        BIG_CANDLE,
        PILLAR,
        STAR,
        CUSHION,
        NONE
    }

    private static final Set<UUID> ACTIVE_HOMEMAKERS = Collections.synchronizedSet(new HashSet<>());

    private static BlockCategory getCategory(Block block) {
        if (block instanceof DecoratedPotBlock) return BlockCategory.DECORATED_POT;
        if (block instanceof BigCandleBlock) return BlockCategory.BIG_CANDLE;
        if (block instanceof PillarBlock) return BlockCategory.PILLAR;
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

    public static boolean isHomemakerAlive(ServerLevel level, Player player) {
        if (player.getPersistentData().hasUUID("WanderingHomemakerUUID")) {
            UUID uuid = player.getPersistentData().getUUID("WanderingHomemakerUUID");
            if (ACTIVE_HOMEMAKERS.contains(uuid)) {
                return true;
            }
            for (ServerLevel sl : level.getServer().getAllLevels()) {
                net.minecraft.world.entity.Entity entity = sl.getEntity(uuid);
                if (entity != null && entity.isAlive()) {
                    ACTIVE_HOMEMAKERS.add(uuid);
                    return true;
                }
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (event.isCanceled()) return;
        net.minecraft.world.level.LevelAccessor worldAccessor = event.getWorld();
        if (!(worldAccessor instanceof ServerLevel level)) return;
        if (!(event.getEntity() instanceof Player player)) return;

        BlockState placedState = event.getPlacedBlock();
        BlockCategory category = getCategory(placedState.getBlock());
        if (category == BlockCategory.NONE) return;

        BlockPos pos = event.getPos();
        if (formsSquare(level, pos, category)) {
            long gameTime = level.getGameTime();
            long cooldown = 0;
            if (player.getPersistentData().contains("WanderingHomemakerCooldown")) {
                cooldown = player.getPersistentData().getLong("WanderingHomemakerCooldown");
            }

            if (gameTime < cooldown) {
                return; // Cooldown active
            }

            if (isHomemakerAlive(level, player)) {
                return; // Homemaker is already alive for this player
            }

            WanderingHomemakerEntity homemaker = ModEntities.WANDERING_HOMEMAKER.get().create(level);
            if (homemaker != null) {
                homemaker.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, level.random.nextFloat() * 360F, 0.0F);
                level.addFreshEntity(homemaker);

                level.playSound(null, pos, SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.NEUTRAL, 1.0F, 1.0F);
                for (int i = 0; i < 20; i++) {
                    double px = pos.getX() + 0.5D + (level.random.nextDouble() - 0.5D) * 1.5D;
                    double py = pos.getY() + 0.5D + level.random.nextDouble() * 2.0D;
                    double pz = pos.getZ() + 0.5D + (level.random.nextDouble() - 0.5D) * 1.5D;
                    level.sendParticles(ParticleTypes.CLOUD, px, py, pz, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }

                player.getPersistentData().putLong("WanderingHomemakerCooldown", gameTime + 144000L); // 2 hours in ticks
                player.getPersistentData().putUUID("WanderingHomemakerUUID", homemaker.getUUID());
                ACTIVE_HOMEMAKERS.add(homemaker.getUUID());

                if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                    com.kingodogo.buildscape.network.ModMessages.INSTANCE.send(
                            net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> serverPlayer),
                            new com.kingodogo.buildscape.network.SyncHomemakerCooldownPacket(gameTime + 144000L)
                    );
                }
            }
        }
    }
}
