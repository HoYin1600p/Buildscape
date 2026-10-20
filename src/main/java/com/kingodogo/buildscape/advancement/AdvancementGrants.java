package com.kingodogo.buildscape.advancement;

import com.kingodogo.buildscape.registry.ModStats;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;

public class AdvancementGrants {

    public static boolean grant(ServerPlayer player, String id) {
        if (player == null || player.getServer() == null) {
            return false;
        }
        Advancement adv = player.getServer().getAdvancements().getAdvancement(new ResourceLocation("buildscape", id));
        if (adv != null) {
            AdvancementProgress progress = player.getAdvancements().getOrStartProgress(adv);
            if (!progress.isDone()) {
                for (String criterion : progress.getRemainingCriteria()) {
                    player.getAdvancements().award(adv, criterion);
                }
                return true;
            }
        }
        return false;
    }

    public static void checkFullCubeAdvancement(ServerPlayer serverPlayer) {
        if (serverPlayer == null || serverPlayer.getServer() == null) {
            return;
        }
        Advancement cubeAdv = serverPlayer.getServer().getAdvancements().getAdvancement(new ResourceLocation("buildscape", "a_full_buildscape_cube"));
        if (cubeAdv == null) {
            return;
        }
        AdvancementProgress cubeProgress = serverPlayer.getAdvancements().getOrStartProgress(cubeAdv);
        if (cubeProgress.isDone()) {
            return;
        }

        for (String criterion : cubeAdv.getCriteria().keySet()) {
            CriterionProgress cp = cubeProgress.getCriterion(criterion);
            if (cp != null && cp.isDone()) {
                continue;
            }

            Advancement reqAdv = serverPlayer.getServer().getAdvancements().getAdvancement(new ResourceLocation("buildscape", criterion));
            if (reqAdv != null) {
                AdvancementProgress reqProgress = serverPlayer.getAdvancements().getOrStartProgress(reqAdv);
                if (reqProgress.isDone()) {
                    serverPlayer.getAdvancements().award(cubeAdv, criterion);
                }
            }
        }
    }

    public static boolean checkRelativeMilestone(ServerPlayer player, String advId, int targetCount, int currentStat) {
        Advancement adv = player.getServer().getAdvancements().getAdvancement(new ResourceLocation("buildscape", advId));
        if (adv == null) {
            return false;
        }
        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(adv);
        if (progress.isDone()) {
            return false;
        }

        CompoundTag tag = player.getPersistentData();
        String baseKey = "BS_Base_" + advId;

        if (!tag.contains(baseKey)) {
            tag.putInt(baseKey, Math.max(0, currentStat - 1));
        }

        int baseStat = tag.getInt(baseKey);
        int delta = currentStat - baseStat;

        if (delta < 0) {
            tag.putInt(baseKey, Math.max(0, currentStat - 1));
            baseStat = tag.getInt(baseKey);
            delta = currentStat - baseStat;
        }

        if (delta >= targetCount) {
            boolean granted = grant(player, advId);
            if (granted) {
                tag.remove(baseKey);
            }
            return granted;
        }
        return false;
    }

    public static void onPillarItemInserted(ServerPlayer serverPlayer) {
        if (serverPlayer == null) {
            return;
        }

        serverPlayer.awardStat(ModStats.INTERACT_WITH_PILLAR);
        int count = serverPlayer.getStats().getValue(Stats.CUSTOM, ModStats.INTERACT_WITH_PILLAR);

        if (checkRelativeMilestone(serverPlayer, "put_it_on_display", 10, count)) {
            return;
        }
        if (checkRelativeMilestone(serverPlayer, "columnist", 69, count)) {
            return;
        }
        if (checkRelativeMilestone(serverPlayer, "art_collector", 100, count)) {
            return;
        }
        if (checkRelativeMilestone(serverPlayer, "buildscape_museum", 1000, count)) {
            return;
        }
    }

    public static void onHammerReplace(ServerPlayer player) {
        if (player == null) {
            return;
        }
        player.awardStat(ModStats.HAMMER_USED);
        int count = player.getStats().getValue(Stats.CUSTOM, ModStats.HAMMER_USED);

        grant(player, "fixer_upper");
        checkRelativeMilestone(player, "hammer_time", 1000, count);
    }

    public static void onConfettiUsed(ServerPlayer player) {
        if (player == null) {
            return;
        }
        player.awardStat(ModStats.CONFETTI_USED);
        int count = player.getStats().getValue(Stats.CUSTOM, ModStats.CONFETTI_USED);
        checkRelativeMilestone(player, "celebrate_in_style", 20, count);
    }
}
