package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.VerticalStairBlock;
import com.kingodogo.buildscape.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// Elevator feature disabled for now (kept as backup for future release)
// @Mod.EventBusSubscriber(modid = BuildScape.MODID)
public class VerticalStairsElevatorHandler {

    private static final Map<UUID, Double> playerTargetY = new HashMap<>();
    private static final Map<UUID, Long> playerLastInput = new HashMap<>();
    private static final Map<UUID, Long> playerArrivedTime = new HashMap<>();
    
    private static final double PASSIVE_SPEED = 0.0; 
    private static final double ACTIVE_SPEED = 0.35; 
    private static final long INPUT_COOLDOWN = 600;
    private static final long LANDING_PAUSE = 800;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level.isClientSide) {
            return;
        }

        Player player = event.player;
        Level level = player.level;
        UUID id = player.getUUID();
        long now = System.currentTimeMillis();

        // 1. Detection
        ShaftInfo shaft = findElevatorShaft(player, level);
        
        if (shaft == null) {
            if (playerTargetY.containsKey(id)) {
                playerTargetY.remove(id);
                player.displayClientMessage(new net.minecraft.network.chat.TextComponent(""), true);
            }
            return;
        }

        // 2. Logic Variables
        Double targetHeight = playerTargetY.get(id);
        boolean sneaking = player.isShiftKeyDown();
        boolean jumping = player.getDeltaMovement().y > 0.01;
        boolean isLanding = shaft.stairCount < 4;
        
        // Input detection - Server side xxa/zza are only available via packets or LivingEntity fields
        // We use a backup check: if horizontal velocity is purely from player input
        boolean inputtingMove = Math.abs(player.xxa) > 0.1 || Math.abs(player.zza) > 0.1;
        
        // 3. Movement Calculations
        double vx = 0;
        double vz = 0;
        double vy = 0;
        String status = "";
        boolean overwriteXZ = false;

        // A: Fast Travel / Seeking
        if (targetHeight != null) {
            double dy = targetHeight - player.getY();
            if (Math.abs(dy) > 0.4) {
                vy = Math.signum(dy) * 0.6;
                status = "§b[ >> SEEKING FLOOR << ]";
                spawnElevatorParticles(player);
                overwriteXZ = true;
                vx = (shaft.centerX - player.getX()) * 0.2;
                vz = (shaft.centerZ - player.getZ()) * 0.2;
            } else {
                playerTargetY.remove(id);
                playerArrivedTime.put(id, now);
                // Pause inputs for half a second so they don't instantly fly to the next floor if holding space
                playerLastInput.put(id, now);
                vy = 0;
                status = "§a[ LANDED ]";
            }
        } 
        // Enforce brief manual pause after arriving
        else if (now - playerLastInput.getOrDefault(id, 0L) < 400L) {
            vy = 0;
            if (isLanding) {
                return; // Let them walk out immediately if they want
            } else {
                overwriteXZ = true;
                vx = (shaft.centerX - player.getX()) * 0.1;
                vz = (shaft.centerZ - player.getZ()) * 0.1;
                status = "§a[ - WAITING - ]";
            }
        }
        // B: Manual Controls
        else if (sneaking) {
            vy = -ACTIVE_SPEED;
            status = "§7[ v DESCENDING v ]";
        } else if (jumping) {
            vy = ACTIVE_SPEED * 1.2;
            status = "§e[ ^ ASCENDING ^ ]";
            spawnElevatorParticles(player);
        }
        // C: Passive / Landing Logic
        else {
            if (isLanding) {
                // At a landing, we stop. We hover for a few ms, then release control
                boolean justArrived = (now - playerArrivedTime.getOrDefault(id, 0L)) < LANDING_PAUSE;
                if (justArrived) {
                    vy = 0.0; // Perfect hover
                    status = "§a[ - ARRIVED - ]";
                } else {
                    player.displayClientMessage(new net.minecraft.network.chat.TextComponent("§e[ LANDED ]"), true);
                    return; // DO NOT apply elevator physics; allow normal walking out!
                }
            } else {
                // In a full shaft, we just WAIT for input
                vy = PASSIVE_SPEED; // 0.0
                status = "§a[ - WAITING - ]";
                if (level.random.nextFloat() < 0.05) spawnElevatorParticles(player);
                overwriteXZ = true;
                vx = (shaft.centerX - player.getX()) * 0.1;
                vz = (shaft.centerZ - player.getZ()) * 0.1;
            }
        }

        // Final Velocity Apply
        applyElevatorMotion(player, vx, vy, vz, overwriteXZ);

        if (!status.isEmpty()) {
            player.displayClientMessage(new net.minecraft.network.chat.TextComponent(status), true);
        }

        // 4. Floor Hopping (Instant Seek)
        if (now - playerLastInput.getOrDefault(id, 0L) > INPUT_COOLDOWN) {
            if (sneaking && player.getDeltaMovement().y < -0.1) {
                // Double tap sneak or hold sneak while moving to seek down
                Double next = findNextFloor(level, shaft, player.getY(), -1);
                if (next != null) {
                    playerTargetY.put(id, next);
                    playerLastInput.put(id, now);
                }
            } else if (jumping && player.getDeltaMovement().y > 0.35) {
                Double next = findNextFloor(level, shaft, player.getY(), 1);
                if (next != null) {
                    playerTargetY.put(id, next);
                    playerLastInput.put(id, now);
                }
            }
        }
    }

    private static void applyElevatorMotion(Player player, double vx, double vy, double vz, boolean overwriteXZ) {
        net.minecraft.world.phys.Vec3 delta = player.getDeltaMovement();
        double newX = overwriteXZ ? vx : delta.x;
        double newZ = overwriteXZ ? vz : delta.z;
        player.setDeltaMovement(newX, vy, newZ);
        player.fallDistance = 0;
        player.hurtMarked = true; 
    }

    private static void spawnElevatorParticles(Player player) {
        if (player.level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ModParticles.GLOW_LIME_SPARKLE.get(),
                    player.getX(), player.getY() + 0.1, player.getZ(),
                    2, 0.4, 0.2, 0.4, 0.05);
        }
    }

    private static ShaftInfo findElevatorShaft(Player player, Level level) {
        int px = player.getBlockX();
        int py = player.getBlockY();
        int pz = player.getBlockZ();

        for (int dy : new int[]{0, -1}) {
            int y = py + dy;
            for (int x = px - 1; x <= px; x++) {
                for (int z = pz - 1; z <= pz; z++) {
                    int count = getStairCount(level, x, y, z);
                    if (count >= 2 && !hasOuters(level, x, y, z)) {
                        double cx = x + 1.0;
                        double cz = z + 1.0;
                        if (Math.abs(player.getX() - cx) < 0.75 && Math.abs(player.getZ() - cz) < 0.75) {
                            return new ShaftInfo(x, y, z, cx, cz, count);
                        }
                    }
                }
            }
        }
        return null;
    }

    private static int getStairCount(Level level, int x, int y, int z) {
        int count = 0;
        BlockPos base = new BlockPos(x, y, z);
        if (level.getBlockState(base).getBlock() instanceof VerticalStairBlock) count++;
        if (level.getBlockState(base.east()).getBlock() instanceof VerticalStairBlock) count++;
        if (level.getBlockState(base.south()).getBlock() instanceof VerticalStairBlock) count++;
        if (level.getBlockState(base.east().south()).getBlock() instanceof VerticalStairBlock) count++;
        return count;
    }

    private static boolean hasOuters(Level level, int x, int y, int z) {
        for (int ix = x - 1; ix <= x + 2; ix++) {
            for (int iz = z - 1; iz <= z + 2; iz++) {
                if (ix >= x && ix <= x + 1 && iz >= z && iz <= z + 1) continue;
                if (level.getBlockState(new BlockPos(ix, y, iz)).getBlock() instanceof VerticalStairBlock) return true;
            }
        }
        return false;
    }

    private static Double findNextFloor(Level level, ShaftInfo shaft, double currentY, int direction) {
        int startY = (int)Math.round(currentY) + direction;
        boolean leftCurrentLanding = false;
        
        for (int i = 0; i < 64; i++) {
            int checkY = startY + (i * direction);
            if (level.isOutsideBuildHeight(checkY)) break;
            
            int count = getStairCount(level, shaft.x, checkY, shaft.z);
            
            // Assume 4 is a fully enclosed passenger tube, less than 4 is an opening/doorway
            if (count == 4) {
                leftCurrentLanding = true;
            } else if (count < 4 && leftCurrentLanding) {
                return (double)checkY; // Land right on the opening block base
            }
        }
        return null;
    }

    private static class ShaftInfo {
        final int x, y, z;
        final double centerX, centerZ;
        final int stairCount;
        ShaftInfo(int x, int y, int z, double cx, double cz, int count) {
            this.x = x; this.y = y; this.z = z; this.centerX = cx; this.centerZ = cz; this.stairCount = count;
        }
    }
}
