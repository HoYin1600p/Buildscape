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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;

/** Extend the 26.2 data-driven pools while retaining vanilla selection and predicates. */
public final class LoaderWandererTrades {
    private static final Set<TradeSet> EXTENDED = Collections.newSetFromMap(new WeakHashMap<>());
    private LoaderWandererTrades() {}

    public static void register(MinecraftServer server) {
        var registry = server.registryAccess().lookupOrThrow(Registries.TRADE_SET);
        registry.getOptional(TradeSets.WANDERING_TRADER_COMMON).ifPresent(set -> extend(set, false));
        registry.getOptional(TradeSets.WANDERING_TRADER_UNCOMMON).ifPresent(set -> extend(set, true));
    }

    private static void extend(TradeSet set, boolean rare) {
        if (EXTENDED.contains(set)) return;
        try {
            var trades = new ArrayList<Holder<VillagerTrade>>();
            set.getTrades().forEach(trades::add);
            if (rare) {
                var scroll = Services.PLATFORM.getItem(new CommonId("buildscape", "ancient_ashen_scroll"));
                if (scroll != null && scroll != Items.AIR) {
                    trades.add(Holder.direct(new VillagerTrade(new TradeCost(Items.DIAMOND, 12),
                            new ItemStackTemplate(scroll, 1), 1, 1, 0,
                            Optional.of(LootItemRandomChanceCondition.randomChance(1F / 5000F).build()), List.of())));
                }
            } else {
                for (var entry : WandererTradeTable.generic()) {
                    var item = Services.PLATFORM.getItem(CommonId.parse(entry.item().contains(":") ? entry.item() : "buildscape:" + entry.item()));
                    if (item != null && item != Items.AIR) {
                        trades.add(Holder.direct(new VillagerTrade(new TradeCost(Items.EMERALD, entry.emeralds()),
                                new ItemStackTemplate(item, entry.count()), entry.maxUses(), 1, .05F,
                                Optional.empty(), List.of())));
                    }
                }
            }
            // 26.2 exposes no pool-extension event or setter on either loader.
            // Only the holder list changes; count, duplicate policy, and random sequence remain intact.
            var field = TradeSet.class.getDeclaredField("trades");
            field.setAccessible(true);
            field.set(set, HolderSet.direct(trades));
            EXTENDED.add(set);
        } catch (ReflectiveOperationException | RuntimeException exception) {
            BuildscapeCommon.LOGGER.error("Failed to extend wandering trader offers", exception);
        }
    }
}
