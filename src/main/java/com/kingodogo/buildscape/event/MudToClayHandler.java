package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
public final class MudToClayHandler {

    private static final List<TrackedMud> TRACKED_MUD = new ArrayList<>();

    private MudToClayHandler() {}
    public static void onBlockPlace(Level world, BlockPos pos, BlockState placedState) {
        if (!(world instanceof ServerLevel level)) return;
        if (level.dimension() == Level.NETHER) return;

        Block mudBlock = Services.PLATFORM.getBlock(new CommonId("buildscape", "mud"));
        if (mudBlock == null || mudBlock == Blocks.AIR) return;

        if (placedState.is(mudBlock)) {
            tryTrackMud(level, pos, mudBlock);
        }

        if (placedState.is(Blocks.POINTED_DRIPSTONE) &&
                placedState.getValue(PointedDripstoneBlock.TIP_DIRECTION) == Direction.DOWN) {
            BlockPos mudPos = pos.above(2);
            if (level.getBlockState(mudPos).is(mudBlock)) {
                tryTrackMud(level, mudPos, mudBlock);
            }
        }

        BlockPos abovePos = pos.above();
        BlockPos belowPos = pos.below();
        if (level.getBlockState(abovePos).is(mudBlock)) {
            BlockState belowState = level.getBlockState(belowPos);
            if (belowState.is(Blocks.POINTED_DRIPSTONE) &&
                    belowState.getValue(PointedDripstoneBlock.TIP_DIRECTION) == Direction.DOWN) {
                tryTrackMud(level, abovePos, mudBlock);
            }
        }
    }

    private static void tryTrackMud(ServerLevel level, BlockPos mudPos, Block mudBlock) {
        for (TrackedMud tracked : TRACKED_MUD) {
            if (tracked.pos.equals(mudPos) && tracked.dimension == level.dimension()) return;
        }

        BlockPos belowMud = mudPos.below();
        BlockPos dripstonePos = mudPos.below(2);

        BlockState belowState = level.getBlockState(belowMud);
        BlockState dripstoneState = level.getBlockState(dripstonePos);

        if (!belowState.isAir() &&
                dripstoneState.is(Blocks.POINTED_DRIPSTONE) &&
                dripstoneState.getValue(PointedDripstoneBlock.TIP_DIRECTION) == Direction.DOWN) {
            int ticks = 20 + level.getRandom().nextInt(21);
            TRACKED_MUD.add(new TrackedMud(level.dimension(), mudPos, ticks));
        }
    }
    public static void onServerStopping() {
        TRACKED_MUD.clear();
    }
    public static void onServerTick(MinecraftServer server) {
        if (TRACKED_MUD.isEmpty()) return;

        Block mudBlock = Services.PLATFORM.getBlock(new CommonId("buildscape", "mud"));
        if (mudBlock == null || mudBlock == Blocks.AIR) {
            TRACKED_MUD.clear();
            return;
        }

        Iterator<TrackedMud> it = TRACKED_MUD.iterator();
        while (it.hasNext()) {
            TrackedMud tracked = it.next();
            tracked.ticksRemaining--;

            if (tracked.ticksRemaining <= 0) {
                ServerLevel level = server.getLevel(tracked.dimension);
                if (level != null && level.isLoaded(tracked.pos)
                        && level.getBlockState(tracked.pos).is(mudBlock)) {
                    level.setBlock(tracked.pos, Blocks.CLAY.defaultBlockState(), 3);
                }
                it.remove();
            }
        }
    }

    private static class TrackedMud {
        final ResourceKey<Level> dimension;
        final BlockPos pos;
        int ticksRemaining;

        TrackedMud(ResourceKey<Level> dimension, BlockPos pos, int ticks) {
            this.dimension = dimension;
            this.pos = pos;
            this.ticksRemaining = ticks;
        }
    }
}
