package com.kingodogo.buildscape.event;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WandererTradeTableTest {
    @Test void ordinaryTraderPricesRemainDistinctFromHomemakerBundles() {
        var trades = WandererTradeTable.generic();
        assertEquals(26, trades.size());
        assertTrue(trades.contains(new WandererTradeTable.Trade("minecraft:mangrove_propagule", 5, 1, 8)));
        assertTrue(trades.contains(new WandererTradeTable.Trade("clover", 1, 4, 6)));
        assertTrue(trades.contains(new WandererTradeTable.Trade("snowy_grass_block", 1, 1, 2)));
        assertEquals(2, trades.stream().filter(trade -> trade.item().equals("poplar_sapling")).count());
        assertThrows(UnsupportedOperationException.class, () -> trades.clear());
    }
}
