package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.block.HollowLogBlock;
import com.kingodogo.buildscape.block.HollowPipeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "buildscape", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HollowLogCrawlHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Player player = event.player;
        if (player == null || player.isSpectator()) return;

        Level level = player.level;
        if (level == null) return;

        boolean isSneaking = player.isShiftKeyDown() || player.isCrouching();
        boolean isAlreadyCrawling = player.getPose() == Pose.SWIMMING;

        if (isSneaking || isAlreadyCrawling) {
            if (isPlayerAtHorizontalLogOpening(player, level)) {
                player.setPose(Pose.SWIMMING);
                player.setForcedPose(Pose.SWIMMING);
                return;
            }
        }

        // Clear forced pose if player is no longer at opening/crawling condition
        if (player.getForcedPose() == Pose.SWIMMING) {
            player.setForcedPose(null);
        }
    }

    public static boolean isPlayerAtHorizontalLogOpening(Player player, Level level) {
        AABB bb = player.getBoundingBox();

        int minX = Mth.floor(bb.minX - 0.3);
        int maxX = Mth.floor(bb.maxX + 0.3);
        int minY = Mth.floor(bb.minY - 0.1);
        int maxY = Mth.floor(bb.maxY + 0.1);
        int minZ = Mth.floor(bb.minZ - 0.3);
        int maxZ = Mth.floor(bb.maxZ + 0.3);

        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    mpos.set(x, y, z);
                    BlockState state = level.getBlockState(mpos);
                    if (state.getBlock() instanceof HollowLogBlock || state.getBlock() instanceof HollowPipeBlock) {
                        if (state.getBlock() instanceof HollowPipeBlock && HollowPipeBlock.hasVerticalChannel(state)) {
                            // Intersections and vertical pipes act as ladders and do not force crawling
                            continue;
                        }
                        if (state.hasProperty(RotatedPillarBlock.AXIS)) {
                            Direction.Axis axis = state.getValue(RotatedPillarBlock.AXIS);
                            if (axis == Direction.Axis.X) {
                                // Openings at West/East. Cavity Z bounds: 0.125 to 0.875. Y bounds: 0.0 to 1.0
                                double cavityMinZ = z + 0.125;
                                double cavityMaxZ = z + 0.875;
                                double openingMinX = x - 0.3;
                                double openingMaxX = x + 1.3;
                                double cavityMinY = y;
                                double cavityMaxY = y + 1.0;

                                if (bb.minZ < cavityMaxZ && bb.maxZ > cavityMinZ &&
                                    bb.minX < openingMaxX && bb.maxX > openingMinX &&
                                    bb.minY < cavityMaxY && bb.maxY > cavityMinY) {
                                    return true;
                                }
                            } else if (axis == Direction.Axis.Z) {
                                // Openings at North/South. Cavity X bounds: 0.125 to 0.875. Y bounds: 0.0 to 1.0
                                double cavityMinX = x + 0.125;
                                double cavityMaxX = x + 0.875;
                                double openingMinZ = z - 0.3;
                                double openingMaxZ = z + 1.3;
                                double cavityMinY = y;
                                double cavityMaxY = y + 1.0;

                                if (bb.minX < cavityMaxX && bb.maxX > cavityMinX &&
                                    bb.minZ < openingMaxZ && bb.maxZ > openingMinZ &&
                                    bb.minY < cavityMaxY && bb.maxY > cavityMinY) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean isHorizontalHollowLog(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof HollowPipeBlock) {
            return !HollowPipeBlock.hasVerticalChannel(state);
        }
        if (state.getBlock() instanceof HollowLogBlock) {
            if (state.hasProperty(RotatedPillarBlock.AXIS)) {
                Direction.Axis axis = state.getValue(RotatedPillarBlock.AXIS);
                return axis != Direction.Axis.Y;
            }
        }
        return false;
    }
}

