package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.item.BiomeBrushItem;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.concurrent.ThreadLocalRandom;
public final class BiomeBrushClientHandler {

    private BiomeBrushClientHandler() {}

    public static void onAttack(Player player, net.minecraft.world.phys.HitResult target) {
        if (player == null || Services.PLATFORM.isScreenOpen() || !player.isShiftKeyDown()) return;
        if (target != null && target.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK) return;
        if (player.getMainHandItem().getItem() instanceof BiomeBrushItem
                || player.getOffhandItem().getItem() instanceof BiomeBrushItem) {
            com.kingodogo.buildscape.network.PacketFactory.sendToServer(
                    new com.kingodogo.buildscape.network.ClearBiomeBrushPacket());
        }
    }
    public static void tickClient(Player player) {
        if (player == null) return;
        Level level = Services.PLATFORM.getEntityLevel(player);
        if (level == null || !level.isClientSide()) return;

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        ItemStack brushStack = ItemStack.EMPTY;

        if (!mainHand.isEmpty() && mainHand.getItem() instanceof BiomeBrushItem) {
            brushStack = mainHand;
        } else if (!offHand.isEmpty() && offHand.getItem() instanceof BiomeBrushItem) {
            brushStack = offHand;
        }

        if (brushStack.isEmpty()) return;

        BiomeBrushItem brush = (BiomeBrushItem) brushStack.getItem();
        BlockPos pos1 = brush.getPos1(brushStack);
        BlockPos pos2 = brush.getPos2(brushStack);

        ThreadLocalRandom random = ThreadLocalRandom.current();

        if (pos1 != null && pos2 != null) {
            int minX = Math.min(pos1.getX(), pos2.getX());
            int maxX = Math.max(pos1.getX(), pos2.getX());
            int minY = Math.min(pos1.getY(), pos2.getY());
            int maxY = Math.max(pos1.getY(), pos2.getY());
            int minZ = Math.min(pos1.getZ(), pos2.getZ());
            int maxZ = Math.max(pos1.getZ(), pos2.getZ());

            double x1 = minX;
            double x2 = maxX + 1.0;
            double y1 = minY + 0.0625;
            double y2 = maxY + 1.0 + 0.0625;
            double z1 = minZ;
            double z2 = maxZ + 1.0;

            spawnEdgeParticlesY(level, x1, y1, y2, z1, random);
            spawnEdgeParticlesY(level, x1, y1, y2, z2, random);
            spawnEdgeParticlesY(level, x2, y1, y2, z1, random);
            spawnEdgeParticlesY(level, x2, y1, y2, z2, random);

            spawnEdgeParticlesX(level, x1, x2, y1, z1, random);
            spawnEdgeParticlesX(level, x1, x2, y1, z2, random);
            spawnEdgeParticlesX(level, x1, x2, y2, z1, random);
            spawnEdgeParticlesX(level, x1, x2, y2, z2, random);

            spawnEdgeParticlesZ(level, x1, y1, z1, z2, random);
            spawnEdgeParticlesZ(level, x1, y2, z1, z2, random);
            spawnEdgeParticlesZ(level, x2, y1, z1, z2, random);
            spawnEdgeParticlesZ(level, x2, y2, z1, z2, random);

            spawnPosParticles(level, pos1, random);
            spawnPosParticles(level, pos2, random);
        } else {
            if (pos1 != null) {
                spawnPosParticles(level, pos1, random);
            }
            if (pos2 != null) {
                spawnPosParticles(level, pos2, random);
            }
        }
    }

    private static void spawnEdgeParticlesX(Level level, double x1, double x2, double y, double z, ThreadLocalRandom random) {
        if (random.nextFloat() < 0.3f) {
            double x = x1 + random.nextDouble() * (x2 - x1);
            level.addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0, 0);
        }
    }

    private static void spawnEdgeParticlesY(Level level, double x, double y1, double y2, double z, ThreadLocalRandom random) {
        if (random.nextFloat() < 0.3f) {
            double y = y1 + random.nextDouble() * (y2 - y1);
            level.addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0, 0);
        }
    }

    private static void spawnEdgeParticlesZ(Level level, double x, double y, double z1, double z2, ThreadLocalRandom random) {
        if (random.nextFloat() < 0.3f) {
            double z = z1 + random.nextDouble() * (z2 - z1);
            level.addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0, 0);
        }
    }

    private static void spawnPosParticles(Level level, BlockPos pos, ThreadLocalRandom random) {
        if (random.nextFloat() < 0.4f) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + random.nextDouble() + 0.0625;
            double z = pos.getZ() + random.nextDouble();
            level.addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0, 0);
        }
    }
}
