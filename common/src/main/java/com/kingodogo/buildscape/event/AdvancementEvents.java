package com.kingodogo.buildscape.event;

import net.minecraft.server.level.ServerPlayer;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import com.kingodogo.buildscape.trophy.Trophies;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import java.util.Collection;

public final class AdvancementEvents {
    private AdvancementEvents() {}

    /** Loader implementations keep advancement-holder API changes out of common code. */
    public interface Access {
        boolean grant(ServerPlayer player, String id);
        boolean isDone(ServerPlayer player, String id);
        Collection<String> remainingCriteria(ServerPlayer player, String id);
        void awardCriterion(ServerPlayer player, String id, String criterion);
        int incrementStat(ServerPlayer player, CommonId id, int amount);
    }

    private static Access access;

    public static void configure(Access implementation) {
        access = java.util.Objects.requireNonNull(implementation);
    }

    public static boolean grant(ServerPlayer player, String id) {
        if (player == null) return false;
        if (access == null) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Advancement access is not registered for {}", id);
            return false;
        }
        return access.grant(player, id);
    }

    public static boolean isDone(ServerPlayer player, String id) {
        return access != null && access.isDone(player, id);
    }

    public static int incrementStat(ServerPlayer player, String id, int amount) {
        if (access == null) throw new IllegalStateException("Advancement access is not registered");
        return access.incrementStat(player, new CommonId("buildscape", id), amount);
    }

    public static void onAdvancementEarned(ServerPlayer player, CommonId id) {
        if (player == null || id == null || !"buildscape".equals(id.getNamespace())) return;
        String path = id.getPath();
        giveItemReward(player, Trophies.getRewardForAdvancement(path));
        String reward = switch (path) {
            case "put_it_on_display" -> "ashenking_gold_pillar";
            case "columnist" -> "ashenking_emerald_pillar";
            case "art_collector" -> "ashenking_diamond_pillar";
            case "buildscape_museum" -> "ashenking_netherite_pillar";
            case "ornamental" -> "big_ornament_template";
            case "jar_ring_display" -> "golden_jar_pattern";
            case "christmas_every_day" -> "music_disc_snowfall";
            case "light_em_up" -> "stringlight_frame_pattern";
            case "santas_little_helper" -> "festive_star_pattern";
            case "celebrate_in_style" -> "music_disc_celebration";
            case "a_very_buildscape_christmas" -> "festive_glint_shard";
            case "a_full_buildscape_cube" -> "music_disc_builder";
            default -> null;
        };
        if (reward != null) giveItemReward(player, Services.PLATFORM.getItem(new CommonId("buildscape", reward)));
        if ("grand_celebration".equals(path)) {
            giveStack(player, com.kingodogo.buildscape.item.InfinitePhoenixFireworkStarItem.createDefaultStack());
        }
        if (!"a_full_buildscape_cube".equals(path) && !path.startsWith("recipes/")) checkFullCubeAdvancement(player);
    }

    private static void giveItemReward(ServerPlayer player, Item item) {
        if (item == null || item == net.minecraft.world.item.Items.AIR) return;
        ItemStack stack = new ItemStack(item);
        CommonId id = Services.PLATFORM.getItemId(item);
        if (id != null && (id.getPath().endsWith("_trophy") || java.util.Set.of(
                "golden_jar", "festive_star", "golden_jar_pattern", "festive_star_pattern",
                "stringlight_frame_pattern").contains(id.getPath()))) {
            Services.PLATFORM.updateCustomData(stack, tag -> {
                tag.putString("ObtainedBy", player.getScoreboardName());
                tag.putString("ObtainedOn", java.time.LocalDateTime.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
            });
        }
        giveStack(player, stack);
    }

    private static void giveStack(ServerPlayer player, ItemStack stack) {
        if (!player.getInventory().add(stack)) player.drop(stack, false);
    }

    public static void checkFullCubeAdvancement(ServerPlayer player) {
        if (player == null || access == null || access.isDone(player, "a_full_buildscape_cube")) return;
        for (String criterion : access.remainingCriteria(player, "a_full_buildscape_cube")) {
            if (access.isDone(player, criterion)) access.awardCriterion(player, "a_full_buildscape_cube", criterion);
        }
    }

    public static void onPillarItemInserted(ServerPlayer player) {
        AdvancementMilestoneLogic.onPillarItemInserted(player);
    }

    public static void onHammerReplace(ServerPlayer player) {
        AdvancementMilestoneLogic.onHammerReplace(player);
    }

    public static void onConfettiUsed(ServerPlayer player) {
        AdvancementMilestoneLogic.onConfettiUsed(player);
    }
}
