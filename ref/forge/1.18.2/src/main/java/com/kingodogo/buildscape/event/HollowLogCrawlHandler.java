package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.block.HollowLogBlock;
import com.kingodogo.buildscape.block.HollowPipeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "buildscape", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HollowLogCrawlHandler {

    public static boolean USE_FORCED_SWIM_POSE = false;

    private static final Set<UUID> FORCED_PLAYERS = Collections.synchronizedSet(new HashSet<>());
    private static final Set<UUID> CUSTOM_CRAWLING_PLAYERS = Collections.synchronizedSet(new HashSet<>());

    private static final EntityDimensions CRAWL_DIMENSIONS = EntityDimensions.scalable(0.6F, 0.6F);
    private static final float CRAWL_EYE_HEIGHT = 0.4F;

    public static boolean isPlayerCustomCrawling(Player player) {
        return player != null && CUSTOM_CRAWLING_PLAYERS.contains(player.getUUID());
    }

    @SubscribeEvent
    public static void onEntitySize(EntityEvent.Size event) {
        if (event.getEntity() instanceof Player player) {
            if (!USE_FORCED_SWIM_POSE && isPlayerCustomCrawling(player)) {
                event.setNewSize(CRAWL_DIMENSIONS);
                event.setNewEyeHeight(CRAWL_EYE_HEIGHT);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Player player = event.player;
        if (player == null || player.isSpectator()) return;

        Level level = player.level;
        if (level == null) return;

        if (player.isPassenger() || player.isSleeping() || player.isFallFlying()) {
            clearCrawling(player);
            return;
        }

        if (player.isInWater() && !player.isOnGround()) {
            clearCrawling(player);
            return;
        }

        boolean insideLog = isPlayerInsideHollowBlock(player, level);
        boolean enteringLog = isPlayerEnteringHollowBlock(player, level);

        if (USE_FORCED_SWIM_POSE) {
            if (insideLog || enteringLog) {
                player.setPose(Pose.SWIMMING);
                player.setForcedPose(Pose.SWIMMING);
                FORCED_PLAYERS.add(player.getUUID());
            } else {
                clearForcedPose(player);
            }
        } else {
            if (insideLog || enteringLog) {
                if (!CUSTOM_CRAWLING_PLAYERS.contains(player.getUUID())) {
                    CUSTOM_CRAWLING_PLAYERS.add(player.getUUID());
                    player.refreshDimensions();
                }
                if (player.getPose() != Pose.SWIMMING) {
                    player.setPose(Pose.SWIMMING);
                }
            } else {
                clearCrawling(player);
            }
        }
    }

    private static void clearCrawling(Player player) {
        if (CUSTOM_CRAWLING_PLAYERS.remove(player.getUUID())) {
            player.refreshDimensions();
        }
        clearForcedPose(player);
    }

    private static void clearForcedPose(Player player) {
        FORCED_PLAYERS.remove(player.getUUID());
        if (player.getForcedPose() == Pose.SWIMMING) {
            player.setForcedPose(null);
        }
        if (player.getPose() == Pose.SWIMMING && !player.isInWater() && !player.isInLava()) {
            if (player.isShiftKeyDown()) {
                player.setPose(Pose.CROUCHING);
            } else {
                player.setPose(Pose.STANDING);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() != null) {
            UUID id = event.getEntity().getUUID();
            FORCED_PLAYERS.remove(id);
            CUSTOM_CRAWLING_PLAYERS.remove(id);
        }
    }

    public static boolean isPlayerAtHorizontalLogOpening(Player player, Level level) {
        return isPlayerInsideHollowBlock(player, level) || isPlayerEnteringHollowBlock(player, level);
    }

    private static boolean isConnectingHollowEnd(Level level, BlockPos pos, Direction dir) {
        BlockPos neighborPos = pos.relative(dir);
        BlockState neighborState = level.getBlockState(neighborPos);
        Block block = neighborState.getBlock();
        if (block instanceof HollowLogBlock) {
            Direction.Axis nAxis = neighborState.getValue(RotatedPillarBlock.AXIS);
            if (nAxis == dir.getAxis()) {
                return HollowLogBlock.isOpenEnd(neighborState, dir.getOpposite());
            }
            return false;
        }
        if (block instanceof HollowPipeBlock) {
            BooleanProperty prop = HollowPipeBlock.getPropertyForDirection(dir.getOpposite());
            return neighborState.hasProperty(prop) && neighborState.getValue(prop);
        }
        return false;
    }

    public static boolean isPlayerInsideHollowBlock(Player player, Level level) {
        AABB bb = player.getBoundingBox();
        int minX = Mth.floor(bb.minX);
        int maxX = Mth.floor(bb.maxX);
        int minY = Mth.floor(bb.minY);
        int maxY = Mth.floor(bb.maxY);
        int minZ = Mth.floor(bb.minZ);
        int maxZ = Mth.floor(bb.maxZ);

        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    mpos.set(x, y, z);
                    BlockState state = level.getBlockState(mpos);

                    if (state.getBlock() instanceof HollowLogBlock) {
                        Direction.Axis axis = state.getValue(RotatedPillarBlock.AXIS);
                        if (axis == Direction.Axis.Y) continue;

                        double cavityMinY = y + 0.125;
                        double cavityMaxY = y + 0.875;
                        if (bb.minY >= y - 0.15 && bb.minY < cavityMaxY && bb.maxY > cavityMinY) {
                            if (axis == Direction.Axis.X) {
                                double cavityMinZ = z + 0.125;
                                double cavityMaxZ = z + 0.875;
                                if (bb.minZ < cavityMaxZ && bb.maxZ > cavityMinZ) {
                                    boolean connNeg = isConnectingHollowEnd(level, mpos, Direction.WEST);
                                    boolean connPos = isConnectingHollowEnd(level, mpos, Direction.EAST);
                                    double minInsideX = connNeg ? x : (x + 0.35);
                                    double maxInsideX = connPos ? (x + 1.0) : (x + 0.65);
                                    if (bb.maxX > minInsideX && bb.minX < maxInsideX) {
                                        return true;
                                    }
                                }
                            } else if (axis == Direction.Axis.Z) {
                                double cavityMinX = x + 0.125;
                                double cavityMaxX = x + 0.875;
                                if (bb.minX < cavityMaxX && bb.maxX > cavityMinX) {
                                    boolean connNeg = isConnectingHollowEnd(level, mpos, Direction.NORTH);
                                    boolean connPos = isConnectingHollowEnd(level, mpos, Direction.SOUTH);
                                    double minInsideZ = connNeg ? z : (z + 0.35);
                                    double maxInsideZ = connPos ? (z + 1.0) : (z + 0.65);
                                    if (bb.maxZ > minInsideZ && bb.minZ < maxInsideZ) {
                                        return true;
                                    }
                                }
                            }
                        }
                    } else if (state.getBlock() instanceof HollowPipeBlock) {
                        Direction.Axis axis = HollowPipeBlock.getPrimaryAxis(state);
                        boolean hasHorizontal = state.getValue(HollowPipeBlock.NORTH) || state.getValue(HollowPipeBlock.SOUTH)
                                || state.getValue(HollowPipeBlock.WEST) || state.getValue(HollowPipeBlock.EAST)
                                || axis != Direction.Axis.Y;
                        if (!hasHorizontal) continue;

                        double cavityMinY = y + 0.125;
                        double cavityMaxY = y + 0.875;
                        if (bb.minY >= y - 0.15 && bb.minY < cavityMaxY && bb.maxY > cavityMinY) {
                            boolean connW = isConnectingHollowEnd(level, mpos, Direction.WEST);
                            boolean connE = isConnectingHollowEnd(level, mpos, Direction.EAST);
                            boolean connN = isConnectingHollowEnd(level, mpos, Direction.NORTH);
                            boolean connS = isConnectingHollowEnd(level, mpos, Direction.SOUTH);

                            boolean hasW = state.getValue(HollowPipeBlock.WEST) || axis == Direction.Axis.X;
                            boolean hasE = state.getValue(HollowPipeBlock.EAST) || axis == Direction.Axis.X;
                            boolean hasN = state.getValue(HollowPipeBlock.NORTH) || axis == Direction.Axis.Z;
                            boolean hasS = state.getValue(HollowPipeBlock.SOUTH) || axis == Direction.Axis.Z;

                            double minInsideX = connW ? x : (hasW ? x + 0.35 : x + 0.125);
                            double maxInsideX = connE ? (x + 1.0) : (hasE ? x + 0.65 : x + 0.875);
                            double minInsideZ = connN ? z : (hasN ? z + 0.35 : z + 0.125);
                            double maxInsideZ = connS ? (z + 1.0) : (hasS ? z + 0.65 : z + 0.875);

                            if (bb.maxX > minInsideX && bb.minX < maxInsideX &&
                                bb.maxZ > minInsideZ && bb.minZ < maxInsideZ) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean isPlayerEnteringHollowBlock(Player player, Level level) {
        if (!player.isShiftKeyDown() && !player.isCrouching()) {
            return false;
        }

        AABB bb = player.getBoundingBox();
        int minY = Mth.floor(bb.minY - 0.2);
        int maxY = Mth.floor(bb.maxY + 0.2);
        int minX = Mth.floor(bb.minX - 0.3);
        int maxX = Mth.floor(bb.maxX + 0.3);
        int minZ = Mth.floor(bb.minZ - 0.3);
        int maxZ = Mth.floor(bb.maxZ + 0.3);

        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    mpos.set(x, y, z);
                    BlockState state = level.getBlockState(mpos);

                    if (bb.minY < y - 0.25 || bb.minY > y + 0.6) {
                        continue;
                    }

                    if (state.getBlock() instanceof HollowLogBlock) {
                        Direction.Axis axis = state.getValue(RotatedPillarBlock.AXIS);
                        if (axis == Direction.Axis.Y) continue;

                        if (axis == Direction.Axis.X) {
                            double cavityMinZ = z + 0.15;
                            double cavityMaxZ = z + 0.85;
                            if (bb.minZ < cavityMaxZ && bb.maxZ > cavityMinZ) {
                                if (!state.getValue(HollowLogBlock.HAS_GLASS_NEG) && !isConnectingHollowEnd(level, mpos, Direction.WEST)) {
                                    if (bb.maxX >= x - 0.25 && bb.minX <= x + 0.25) {
                                        return true;
                                    }
                                }
                                if (!state.getValue(HollowLogBlock.HAS_GLASS_POS) && !isConnectingHollowEnd(level, mpos, Direction.EAST)) {
                                    if (bb.minX <= x + 1.25 && bb.maxX >= x + 0.75) {
                                        return true;
                                    }
                                }
                            }
                        } else if (axis == Direction.Axis.Z) {
                            double cavityMinX = x + 0.15;
                            double cavityMaxX = x + 0.85;
                            if (bb.minX < cavityMaxX && bb.maxX > cavityMinX) {
                                if (!state.getValue(HollowLogBlock.HAS_GLASS_NEG) && !isConnectingHollowEnd(level, mpos, Direction.NORTH)) {
                                    if (bb.maxZ >= z - 0.25 && bb.minZ <= z + 0.25) {
                                        return true;
                                    }
                                }
                                if (!state.getValue(HollowLogBlock.HAS_GLASS_POS) && !isConnectingHollowEnd(level, mpos, Direction.SOUTH)) {
                                    if (bb.minZ <= z + 1.25 && bb.maxZ >= z + 0.75) {
                                        return true;
                                    }
                                }
                            }
                        }
                    } else if (state.getBlock() instanceof HollowPipeBlock) {
                        Direction.Axis axis = HollowPipeBlock.getPrimaryAxis(state);
                        boolean hasHorizontal = state.getValue(HollowPipeBlock.NORTH) || state.getValue(HollowPipeBlock.SOUTH)
                                || state.getValue(HollowPipeBlock.WEST) || state.getValue(HollowPipeBlock.EAST)
                                || axis != Direction.Axis.Y;
                        if (!hasHorizontal) continue;

                        if ((state.getValue(HollowPipeBlock.WEST) || axis == Direction.Axis.X) && !isConnectingHollowEnd(level, mpos, Direction.WEST)) {
                            if (bb.minZ < z + 0.85 && bb.maxZ > z + 0.15 && bb.maxX >= x - 0.25 && bb.minX <= x + 0.25) {
                                return true;
                            }
                        }
                        if ((state.getValue(HollowPipeBlock.EAST) || axis == Direction.Axis.X) && !isConnectingHollowEnd(level, mpos, Direction.EAST)) {
                            if (bb.minZ < z + 0.85 && bb.maxZ > z + 0.15 && bb.minX <= x + 1.25 && bb.maxX >= x + 0.75) {
                                return true;
                            }
                        }
                        if ((state.getValue(HollowPipeBlock.NORTH) || axis == Direction.Axis.Z) && !isConnectingHollowEnd(level, mpos, Direction.NORTH)) {
                            if (bb.minX < x + 0.85 && bb.maxX > x + 0.15 && bb.maxZ >= z - 0.25 && bb.minZ <= z + 0.25) {
                                return true;
                            }
                        }
                        if ((state.getValue(HollowPipeBlock.SOUTH) || axis == Direction.Axis.Z) && !isConnectingHollowEnd(level, mpos, Direction.SOUTH)) {
                            if (bb.minX < x + 0.85 && bb.maxX > x + 0.15 && bb.minZ <= z + 1.25 && bb.maxZ >= z + 0.75) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
