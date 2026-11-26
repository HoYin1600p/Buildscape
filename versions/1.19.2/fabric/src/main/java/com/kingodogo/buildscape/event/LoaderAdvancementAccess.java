package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Forge 1.18.2 advancement access, installed before gameplay callbacks. */
public final class LoaderAdvancementAccess implements AdvancementEvents.Access {
    private static final java.util.Set<ResourceLocation> UNREGISTERED = java.util.concurrent.ConcurrentHashMap.newKeySet();

    @Override public int incrementStat(ServerPlayer player, com.kingodogo.buildscape.util.CommonId id, int amount) {
        ResourceLocation key = new ResourceLocation(id.getNamespace(), id.getPath());
        // Stats are registered under minecraft:<path> like main; the value is the Buildscape id.
        ResourceLocation registered = net.minecraft.core.Registry.CUSTOM_STAT.getOptional(new ResourceLocation(id.getPath()))
                .or(() -> net.minecraft.core.Registry.CUSTOM_STAT.getOptional(key)).orElse(null);
        if (registered == null) {
            if (UNREGISTERED.add(key)) com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn(
                    "Skipping unregistered Buildscape custom stat {}", key);
            return 0;
        }
        var stat = net.minecraft.stats.Stats.CUSTOM.get(registered, net.minecraft.stats.StatFormatter.DEFAULT);
        int current = player.getStats().getValue(stat);
        int updated = MilestoneCounter.advance(current, amount);
        if (updated > current) player.awardStat(stat, updated - current);
        return player.getStats().getValue(stat);
    }

    private static Advancement find(ServerPlayer player, String id) {
        var server = Services.PLATFORM.getServer(player);
        return server == null ? null : server.getAdvancements().getAdvancement(new ResourceLocation("buildscape", id));
    }

    @Override public boolean grant(ServerPlayer player, String id) {
        var holder = find(player, id);
        if (holder == null) return false;
        var progress = player.getAdvancements().getOrStartProgress(holder);
        if (progress.isDone()) return false;
        for (String criterion : remainingCriteria(player, id)) player.getAdvancements().award(holder, criterion);
        return progress.isDone();
    }

    @Override public boolean isDone(ServerPlayer player, String id) {
        var holder = find(player, id);
        return holder != null && player.getAdvancements().getOrStartProgress(holder).isDone();
    }

    @Override public Collection<String> remainingCriteria(ServerPlayer player, String id) {
        var holder = find(player, id);
        if (holder == null) return List.of();
        var remaining = new ArrayList<String>();
        player.getAdvancements().getOrStartProgress(holder).getRemainingCriteria().forEach(remaining::add);
        return remaining;
    }

    @Override public void awardCriterion(ServerPlayer player, String id, String criterion) {
        var holder = find(player, id);
        if (holder != null) player.getAdvancements().award(holder, criterion);
    }
}
