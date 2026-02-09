package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mod.EventBusSubscriber(modid = BuildScape.MODID)
public class MudToClayHandler {

    private static final List<TrackedMud> trackedMud = new ArrayList<>();

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getWorld() instanceof ServerLevel level)) return;
        if (level.dimension() == Level.NETHER) return;

        BlockPos pos = event.getPos();
        BlockState placedState = event.getPlacedBlock();

        // Case 1: Mud was placed - check for dripstone below
        if (placedState.is(ModBlocks.MUD.get())) {
            tryTrackMud(level, pos);
        }

        // Case 2: Pointed dripstone placed - check for mud two blocks above
        if (placedState.is(Blocks.POINTED_DRIPSTONE) &&
                placedState.getValue(PointedDripstoneBlock.TIP_DIRECTION) == Direction.DOWN) {
            BlockPos mudPos = pos.above(2);
            if (level.getBlockState(mudPos).is(ModBlocks.MUD.get())) {
                tryTrackMud(level, mudPos);
            }
        }

        // Case 3: Block placed between existing mud and dripstone
        BlockPos abovePos = pos.above();
        BlockPos belowPos = pos.below();
        if (level.getBlockState(abovePos).is(ModBlocks.MUD.get())) {
            BlockState belowState = level.getBlockState(belowPos);
            if (belowState.is(Blocks.POINTED_DRIPSTONE) &&
                    belowState.getValue(PointedDripstoneBlock.TIP_DIRECTION) == Direction.DOWN) {
                tryTrackMud(level, abovePos);
            }
        }
    }

    private static void tryTrackMud(ServerLevel level, BlockPos mudPos) {
        // Avoid duplicate tracking
        for (TrackedMud tracked : trackedMud) {
            if (tracked.pos.equals(mudPos) && tracked.level == level) return;
        }

        BlockPos belowMud = mudPos.below();
        BlockPos dripstonePos = mudPos.below(2);

        BlockState belowState = level.getBlockState(belowMud);
        BlockState dripstoneState = level.getBlockState(dripstonePos);

        // Requires: non-air block below mud, pointed dripstone facing down below that
        if (!belowState.isAir() &&
                dripstoneState.is(Blocks.POINTED_DRIPSTONE) &&
                dripstoneState.getValue(PointedDripstoneBlock.TIP_DIRECTION) == Direction.DOWN) {
            // 1-2 seconds (20-40 ticks)
            int ticks = 20 + level.random.nextInt(21);
            trackedMud.add(new TrackedMud(level, mudPos, ticks));
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (trackedMud.isEmpty()) return;

        Iterator<TrackedMud> it = trackedMud.iterator();
        while (it.hasNext()) {
            TrackedMud tracked = it.next();
            tracked.ticksRemaining--;

            if (tracked.ticksRemaining <= 0) {
                // Verify mud is still there before converting
                if (tracked.level.getBlockState(tracked.pos).is(ModBlocks.MUD.get())) {
                    tracked.level.setBlock(tracked.pos, Blocks.CLAY.defaultBlockState(), 3);
                }
                it.remove();
            }
        }
    }

    private static class TrackedMud {
        final ServerLevel level;
        final BlockPos pos;
        int ticksRemaining;

        TrackedMud(ServerLevel level, BlockPos pos, int ticks) {
            this.level = level;
            this.pos = pos;
            this.ticksRemaining = ticks;
        }
    }
}
