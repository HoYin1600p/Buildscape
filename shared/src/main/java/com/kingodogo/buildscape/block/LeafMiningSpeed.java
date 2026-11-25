package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Shears cut hedges and leaf layers as fast as leaves. They are kept out of minecraft:leaves, which trees use,
 * so each loader's break-speed hook scales the speed here instead; Haste and other changes already applied stay.
 */
public final class LeafMiningSpeed {
    public static final CommonId SHEARS_MINEABLE = new CommonId("buildscape", "shears_mineable");
    private static final float LEAF_SPEED = 15.0F;

    private LeafMiningSpeed() {}

    public static float adjust(Player player, BlockState state, float speed) {
        ItemStack tool = player.getMainHandItem();
        if (!(tool.getItem() instanceof ShearsItem) || !Services.PLATFORM.isBlockInTag(state.getBlock(), SHEARS_MINEABLE)) {
            return speed;
        }
        return scale(speed, tool.getDestroySpeed(state), Services.PLATFORM.getEfficiencyLevel(tool));
    }

    /**
     * Replaces the shears' own speed for this block with leaf speed, keeping Efficiency and other factors.
     * Efficiency adds level squared plus one on every version (an attribute from 1.21 on).
     */
    static float scale(float speed, float toolSpeed, int efficiency) {
        float bonus = efficiency > 0 ? efficiency * efficiency + 1.0F : 0.0F;
        float originalSpeed = toolSpeed > 1.0F ? toolSpeed + bonus : toolSpeed;
        if (originalSpeed <= 0.0F) {
            return speed;
        }
        return speed * (Math.max(LEAF_SPEED, toolSpeed) + bonus) / originalSpeed;
    }
}
