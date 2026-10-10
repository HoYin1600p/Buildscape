package com.kingodogo.buildscape.event;

import java.util.ArrayList;
import java.util.List;

/** Reference generic wandering-trader additions, distinct from homemaker prices. */
public final class WandererTradeTable {
    public record Trade(String item, int emeralds, int count, int maxUses) {}
    private WandererTradeTable() {}

    public static List<Trade> generic() {
        var trades = new ArrayList<Trade>();
        trades.add(new Trade("mangrove_propagule", 5, 1, 8));
        for (String item : List.of("poplar_sapling", "cherry_sapling", "pale_oak_sapling")) {
            trades.add(new Trade(item, 1, 2, 8));
        }
        for (String color : List.of("red", "blue", "purple", "light_blue", "pink", "yellow")) {
            trades.add(new Trade(color + "_monets", 1, 1, 6));
        }
        trades.add(new Trade("clover", 1, 4, 6));
        for (String color : List.of("red", "black", "blue", "white")) trades.add(new Trade(color + "_rose_vines", 1, 1, 6));
        trades.add(new Trade("snowy_grass_block", 1, 1, 2));
        for (String color : List.of("red", "cyan", "blue", "purple", "orange")) trades.add(new Trade(color + "_spore_blossom", 1, 1, 6));
        trades.add(new Trade("icicle", 1, 2, 6));
        trades.add(new Trade("minecraft:sulfur_spike", 1, 2, 6));
        trades.add(new Trade("minecraft:sulfur", 1, 1, 8));
        trades.add(new Trade("minecraft:cinnabar", 1, 1, 8));
        // The reference adds poplar twice, giving it twice the selection weight.
        trades.add(new Trade("poplar_sapling", 1, 2, 8));
        return List.copyOf(trades);
    }
}
