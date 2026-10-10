package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.block.AshenKingPillarBlock;
import com.kingodogo.buildscape.block.FrostRoseBlock;
import com.kingodogo.buildscape.block.PillarBlock;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class AdvancementMilestoneLogic {

    private AdvancementMilestoneLogic() {}

    public static void grant(ServerPlayer player, String id) {
        if (player == null) return;
        Services.PLATFORM.awardAdvancement(player, new CommonId("buildscape", id), id);
    }

    private static boolean checkMilestone(ServerPlayer player, String advId, int targetCount, String counterKey) {
        CompoundTag tag = Services.PLATFORM.getEntityData(player);
        int currentCount = Services.PLATFORM.getTagInt(tag, counterKey, 0) + 1;
        tag.putInt(counterKey, currentCount);

        if (currentCount >= targetCount) {
            grant(player, advId);
            return true;
        }
        return false;
    }

    public static void onPillarItemInserted(ServerPlayer serverPlayer) {
        if (serverPlayer == null) return;
        CompoundTag tag = Services.PLATFORM.getEntityData(serverPlayer);
        int count = Services.PLATFORM.getTagInt(tag, "BS_Stat_PillarsInteracted", 0) + 1;
        tag.putInt("BS_Stat_PillarsInteracted", count);

        if (count >= 10) grant(serverPlayer, "put_it_on_display");
        if (count >= 69) grant(serverPlayer, "columnist");
        if (count >= 100) grant(serverPlayer, "art_collector");
        if (count >= 1000) grant(serverPlayer, "buildscape_museum");
    }

    public static void onHammerReplace(ServerPlayer player) {
        if (player == null) return;
        grant(player, "fixer_upper");
        checkMilestone(player, "hammer_time", 1000, "BS_Stat_HammerUsed");
    }

    public static void onConfettiUsed(ServerPlayer player) {
        if (player == null) return;
        checkMilestone(player, "celebrate_in_style", 20, "BS_Stat_ConfettiUsed");
    }

    public static void onItemCrafted(ServerPlayer serverPlayer, ItemStack itemStack) {
        if (serverPlayer == null || itemStack.isEmpty()) return;
        CommonId id = Services.PLATFORM.getItemId(itemStack.getItem());
        if (id == null) return;
        String path = id.getPath();

        if (path.contains("jar") && !path.contains("pattern")) {
            checkMilestone(serverPlayer, "jar_ring_display", 100, "BS_Stat_JarsCrafted");
        }

        if (path.contains("festive_stocking") || path.contains("stocking")) {
            checkMilestone(serverPlayer, "christmas_every_day", 365, "BS_Stat_StockingsCrafted");
        }
    }

    public static void onBlockPlaced(ServerPlayer serverPlayer, Level level, BlockPos pos, BlockState state) {
        if (serverPlayer == null || level.isClientSide()) return;

        Block block = state.getBlock();
        CommonId id = Services.PLATFORM.getBlockId(block);
        if (id == null) return;

        String modId = id.getNamespace();
        String path = id.getPath();

        if ("buildscape".equals(modId)) {
            CompoundTag tag = Services.PLATFORM.getEntityData(serverPlayer);
            int placedCount = Services.PLATFORM.getTagInt(tag, "BS_Stat_BlocksPlaced", 0) + 1;
            tag.putInt("BS_Stat_BlocksPlaced", placedCount);

            if (placedCount >= 100) grant(serverPlayer, "one_more_block");
            if (placedCount >= 1000) grant(serverPlayer, "okay_one_more");
            if (placedCount >= 10000) grant(serverPlayer, "actually_one_last");
            if (placedCount >= 100000) grant(serverPlayer, "one_last_one_i_promise");
        }

        if (path.contains("stained_brick")) {
            grant(serverPlayer, "brick_by_brick");
        }

        if (path.startsWith("hollow_")) {
            checkMilestone(serverPlayer, "i_vented", 10, "BS_Stat_HollowPlaced");
        }

        if (path.contains("icicle")) {
            checkMilestone(serverPlayer, "chill_out", 10, "BS_Stat_IciclesPlaced");
        }

        if (path.contains("ornament")) {
            checkMilestone(serverPlayer, "ornamental", 100, "BS_Stat_OrnamentsPlaced");
        }

        if (path.contains("string_light")) {
            checkMilestone(serverPlayer, "light_em_up", 100, "BS_Stat_StringLightsPlaced");
        }

        if (path.contains("_star")) {
            checkMilestone(serverPlayer, "santas_little_helper", 100, "BS_Stat_StarsPlaced");
        }

        if (path.startsWith("snowy_")) {
            checkMilestone(serverPlayer, "a_white_christmas", 100, "BS_Stat_SnowyLeavesPlaced");
        }

        if (block instanceof FrostRoseBlock || path.equals("frost_rose")) {
            int radius = 3;
            int roseCount = 0;
            for (BlockPos p : BlockPos.betweenClosed(pos.offset(-radius, -radius, -radius), pos.offset(radius, radius, radius))) {
                if (level.getBlockState(p).getBlock() instanceof FrostRoseBlock) {
                    roseCount++;
                }
            }
            if (roseCount >= 5) {
                grant(serverPlayer, "let_it_snow");
            }
        }

        if (path.contains("cascade_block")) {
            grant(serverPlayer, "let_it_cascade");
        }

        if (path.equals("smoke_vent")) {
            checkMilestone(serverPlayer, "let_it_out", 5, "BS_Stat_SmokeVentsPlaced");
        }

        if (path.equals("muff_block") && level.hasNeighborSignal(pos)) {
            grant(serverPlayer, "can_you_hear_me_now");
        }

        if (path.endsWith("_bolts")) {
            checkMilestone(serverPlayer, "are_you_nuts", 20, "BS_Stat_BoltsPlaced");
        }

        if (block instanceof PillarBlock || block instanceof AshenKingPillarBlock) {
            checkPillars(serverPlayer, level, pos, path);
        }
    }

    private static void checkPillars(ServerPlayer player, Level level, BlockPos pos, String path) {
        CompoundTag tag = Services.PLATFORM.getEntityData(player);
        int samePillarCount = Services.PLATFORM.getTagInt(tag, "BS_PillarPlaced_" + path, 0) + 1;
        tag.putInt("BS_PillarPlaced_" + path, samePillarCount);

        if (samePillarCount >= 4) {
            grant(player, "support_system");
        }

        int height = 1;
        BlockPos current = pos.below();
        while (level.getBlockState(current).getBlock() instanceof PillarBlock || level.getBlockState(current).getBlock() instanceof AshenKingPillarBlock) {
            height++;
            current = current.below();
        }
        current = pos.above();
        while (level.getBlockState(current).getBlock() instanceof PillarBlock || level.getBlockState(current).getBlock() instanceof AshenKingPillarBlock) {
            height++;
            current = current.above();
        }

        if (height >= 50) {
            grant(player, "thats_a_tall_order");
        }

        if (pos.getY() >= Services.PLATFORM.getMaxBuildHeight(level) - 1) {
            grant(player, "reach_for_the_sky");
        }
    }
}
