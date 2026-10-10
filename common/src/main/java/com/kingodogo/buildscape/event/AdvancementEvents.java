package com.kingodogo.buildscape.event;

import net.minecraft.server.level.ServerPlayer;

public final class AdvancementEvents {
    private AdvancementEvents() {}

    public static void grant(ServerPlayer player, String id) {
        AdvancementMilestoneLogic.grant(player, id);
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
