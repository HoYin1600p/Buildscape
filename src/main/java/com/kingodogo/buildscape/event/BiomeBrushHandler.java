package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.item.BiomeBrushItem;
import com.kingodogo.buildscape.network.ClearBiomeBrushPacket;
import com.kingodogo.buildscape.network.ModMessages;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BiomeBrushHandler {

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getPlayer();
        if (player == null) return;

        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!stack.isEmpty() && stack.getItem() instanceof BiomeBrushItem brush) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);

            if (!player.level.isClientSide()) {
                if (stack.getDamageValue() >= stack.getMaxDamage()) {
                    player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1.0f, 0.8f);
                    return;
                }

                if (player.isShiftKeyDown()) {
                    brush.clearCapturedBiome(stack, player);
                } else {
                    brush.setPos2(stack, event.getPos(), player);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        Player player = event.getPlayer();
        if (player == null || !player.level.isClientSide()) return;

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        boolean holdingBrush = (!mainHand.isEmpty() && mainHand.getItem() instanceof BiomeBrushItem) ||
                               (!offHand.isEmpty() && offHand.getItem() instanceof BiomeBrushItem);

        if (holdingBrush && player.isShiftKeyDown()) {
            ModMessages.INSTANCE.sendToServer(new ClearBiomeBrushPacket());
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Player player = event.player;
        if (player == null || !player.level.isClientSide()) return;

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

        Level level = player.level;
        Random random = level.getRandom();

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

    private static void spawnEdgeParticlesX(Level level, double x1, double x2, double y, double z, Random random) {
        if (random.nextFloat() < 0.3f) {
            double x = x1 + random.nextDouble() * (x2 - x1);
            level.addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0, 0);
        }
    }

    private static void spawnEdgeParticlesY(Level level, double x, double y1, double y2, double z, Random random) {
        if (random.nextFloat() < 0.3f) {
            double y = y1 + random.nextDouble() * (y2 - y1);
            level.addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0, 0);
        }
    }

    private static void spawnEdgeParticlesZ(Level level, double x, double y, double z1, double z2, Random random) {
        if (random.nextFloat() < 0.3f) {
            double z = z1 + random.nextDouble() * (z2 - z1);
            level.addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0, 0);
        }
    }

    private static void spawnPosParticles(Level level, BlockPos pos, Random random) {
        if (random.nextFloat() < 0.4f) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + random.nextDouble() + 0.0625;
            double z = pos.getZ() + random.nextDouble();
            level.addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0, 0);
        }
    }
}
