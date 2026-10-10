package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** 26.2 advancement access, installed before gameplay callbacks are registered. */
public final class LoaderAdvancementAccess implements AdvancementEvents.Access {
    @Override public int incrementStat(ServerPlayer player, com.kingodogo.buildscape.util.CommonId id, int amount) {
        var stat = net.minecraft.stats.Stats.CUSTOM.get(Identifier.fromNamespaceAndPath(id.getNamespace(), id.getPath()));
        int current = player.getStats().getValue(stat);
        int updated = MilestoneCounter.advance(current, amount);
        if (updated > current) player.awardStat(stat, updated - current);
        return player.getStats().getValue(stat);
    }

    private static AdvancementHolder find(ServerPlayer player, String id) {
        var server = Services.PLATFORM.getServer(player);
        return server == null ? null : server.getAdvancements().get(Identifier.fromNamespaceAndPath("buildscape", id));
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
