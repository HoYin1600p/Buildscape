package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.TradeCost;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.item.trading.TradeSets;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/** Extend the 26.3 data-driven pools while retaining vanilla selection and predicates. */
public final class LoaderWandererTrades {
    private static final Set<TradeSet> EXTENDED = Collections.newSetFromMap(new WeakHashMap<>());
    private LoaderWandererTrades() {}

    public static void register(MinecraftServer server) {
        var registry = server.registryAccess().lookupOrThrow(Registries.TRADE_SET);
        extend(registry.get(TradeSets.WANDERING_TRADER_COMMON), false);
        extend(registry.get(TradeSets.WANDERING_TRADER_UNCOMMON), true);
    }

    private static void extend(java.util.Optional<? extends Holder.Reference<TradeSet>> reference, boolean rare) {
        if (reference.isEmpty()) return;
        var holder = reference.get();
        TradeSet set = holder.value();
        if (EXTENDED.contains(set)) return;
        try {
            var trades = new ArrayList<Holder<VillagerTrade>>();
            set.trades().forEach(trades::add);
            if (rare) {
                var scroll = Services.PLATFORM.getItem(new CommonId("buildscape", "ancient_ashen_scroll"));
                if (scroll != null && scroll != Items.AIR) {
                    trades.add(Holder.direct(VillagerTrade.builder(new TradeCost(Items.DIAMOND, 12),
                            new ItemStackTemplate(scroll, 1), 1, 1, 0F)
                            .merchantPredicate(Holder.direct(LootItemRandomChanceCondition.randomChance(1F / 5000F).build()))
                            .build()));
                }
            } else {
                for (var entry : WandererTradeTable.generic()) {
                    var item = Services.PLATFORM.getItem(CommonId.parse(entry.item().contains(":") ? entry.item() : "buildscape:" + entry.item()));
                    if (item != null && item != Items.AIR) {
                        trades.add(Holder.direct(VillagerTrade.builder(new TradeCost(Items.EMERALD, entry.emeralds()),
                                new ItemStackTemplate(item, entry.count()), entry.maxUses(), 1, .05F).build()));
                    }
                }
            }
            // 26.3 exposes no pool-extension event or setter on either loader, and TradeSet is now a
            // record whose fields cannot be reassigned. Only the holder list changes: a copy of the set
            // with the extended list replaces the value the registry reference serves. Count, duplicate
            // policy, and random sequence remain intact.
            var extended = new TradeSet(HolderSet.direct(trades), set.amount(), set.allowDuplicates(), set.randomSequence());
            var field = Holder.Reference.class.getDeclaredField("value");
            field.setAccessible(true);
            field.set(holder, extended);
            EXTENDED.add(extended);
        } catch (ReflectiveOperationException | RuntimeException exception) {
            BuildscapeCommon.LOGGER.error("Failed to extend wandering trader offers", exception);
        }
    }
}
