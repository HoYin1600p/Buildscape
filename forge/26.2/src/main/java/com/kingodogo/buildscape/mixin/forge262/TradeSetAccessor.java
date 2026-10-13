package com.kingodogo.buildscape.mixin.forge262;

import net.minecraft.core.HolderSet;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.item.trading.VillagerTrade;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TradeSet.class)
public interface TradeSetAccessor {
    @Mutable
    @Accessor("trades")
    void buildscape$setTrades(HolderSet<VillagerTrade> trades);
}
