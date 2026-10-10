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
        AdvancementEvents.grant(player, id);
    }

    private static boolean checkMilestone(ServerPlayer player, String advId, int targetCount, String counterKey) {
        return checkMilestone(player, advId, targetCount, counterKey, 1);
    }

    private static boolean checkMilestone(ServerPlayer player, String advId, int targetCount, String counterKey, int amount) {
        String stat = switch (counterKey) {
            case "BS_Stat_HammerUsed" -> "hammer_used";
            case "BS_Stat_ConfettiUsed" -> "confetti_used";
            case "BS_Stat_JarsCrafted" -> "jars_crafted";
            case "BS_Stat_StockingsCrafted" -> "stockings_crafted";
            case "BS_Stat_HollowPlaced" -> "hollow_logs_placed";
            case "BS_Stat_IciclesPlaced" -> "icicles_placed";
            case "BS_Stat_OrnamentsPlaced" -> "ornaments_placed";
            case "BS_Stat_StringLightsPlaced" -> "string_lights_placed";
            case "BS_Stat_StarsPlaced" -> "stars_placed";
            case "BS_Stat_SnowyLeavesPlaced" -> "snowy_leaves_placed";
            case "BS_Stat_SmokeVentsPlaced" -> "smoke_vents_placed";
            case "BS_Stat_BoltsPlaced" -> "bolts_placed";
            default -> throw new IllegalArgumentException("Unknown milestone counter: " + counterKey);
        };
        return checkRelativeMilestone(player, advId, targetCount, AdvancementEvents.incrementStat(player, stat, amount));
    }

    private static boolean checkRelativeMilestone(ServerPlayer player, String advId, int targetCount, int currentCount) {
        if (AdvancementEvents.isDone(player, advId)) return false;
        CompoundTag tag = Services.PLATFORM.getEntityData(player);
        String key = "BS_Base_" + advId;
        Integer previous = tag.contains(key) ? Services.PLATFORM.getTagInt(tag, key, 0) : null;
        int baseline = MilestoneCounter.baseline(currentCount, previous);
        tag.putInt(key, baseline);
        if (MilestoneCounter.reached(currentCount, baseline, targetCount) && AdvancementEvents.grant(player, advId)) {
            tag.remove(key);
            return true;
        }
        return false;
    }

    public static void onPillarItemInserted(ServerPlayer serverPlayer) {
        if (serverPlayer == null) return;
        int count = AdvancementEvents.incrementStat(serverPlayer, "interact_with_pillar", 1);
        if (checkRelativeMilestone(serverPlayer, "put_it_on_display", 10, count)) return;
        if (checkRelativeMilestone(serverPlayer, "columnist", 69, count)) return;
        if (checkRelativeMilestone(serverPlayer, "art_collector", 100, count)) return;
        checkRelativeMilestone(serverPlayer, "buildscape_museum", 1000, count);
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
        onItemCrafted(serverPlayer, itemStack, itemStack.getCount());
    }

    public static void onItemCrafted(ServerPlayer serverPlayer, ItemStack itemStack, int amount) {
        if (serverPlayer == null || itemStack.isEmpty()) return;
        CommonId id = Services.PLATFORM.getItemId(itemStack.getItem());
        if (id == null) return;
        String path = id.getPath();

        if (path.contains("jar") && !path.contains("pattern")) {
            checkMilestone(serverPlayer, "jar_ring_display", 100, "BS_Stat_JarsCrafted", amount);
        }

        if (path.contains("festive_stocking") || path.contains("stocking")) {
            checkMilestone(serverPlayer, "christmas_every_day", 365, "BS_Stat_StockingsCrafted", amount);
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
            int placedCount = AdvancementEvents.incrementStat(serverPlayer, "blocks_placed", 1);
            if (!checkRelativeMilestone(serverPlayer, "one_more_block", 100, placedCount)
                    && !checkRelativeMilestone(serverPlayer, "okay_one_more", 1000, placedCount)
                    && !checkRelativeMilestone(serverPlayer, "actually_one_last", 10000, placedCount)) {
                checkRelativeMilestone(serverPlayer, "one_last_one_i_promise", 100000, placedCount);
            }
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
            AdvancementEvents.incrementStat(serverPlayer, "frosty_roses_placed", 1);
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
            AdvancementEvents.incrementStat(serverPlayer, "cascade_blocks_placed", 1);
            grant(serverPlayer, "let_it_cascade");
        }

        if (path.equals("smoke_vent")) {
            checkMilestone(serverPlayer, "let_it_out", 5, "BS_Stat_SmokeVentsPlaced");
        }

        if (path.equals("muff_block") && level.hasNeighborSignal(pos)) {
            AdvancementEvents.incrementStat(serverPlayer, "muff_blocks_activated", 1);
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
