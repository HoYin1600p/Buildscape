package com.kingodogo.buildscape.block;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Shears cut hedges and leaf layers as fast as leaves. They are kept out of minecraft:leaves, which trees use,
 * so the loaders' break-speed hooks scale the speed here instead; Haste and other changes already applied stay.
 */
public final class LeafMiningSpeed {
    public static final TagKey<Block> SHEARS_MINEABLE = TagKey.create(Registry.BLOCK_REGISTRY,
            new ResourceLocation("buildscape", "shears_mineable"));
    private static final float LEAF_SPEED = 15.0F;

    private LeafMiningSpeed() {}

    public static float adjust(Player player, BlockState state, float speed) {
        ItemStack tool = player.getMainHandItem();
        if (!(tool.getItem() instanceof ShearsItem) || !state.is(SHEARS_MINEABLE)) {
            return speed;
        }
        return scale(speed, tool.getDestroySpeed(state), EnchantmentHelper.getBlockEfficiency(player));
    }

    /** Replaces the shears' own speed for this block with leaf speed, keeping Efficiency and other factors. */
    static float scale(float speed, float toolSpeed, int efficiency) {
        float bonus = efficiency > 0 ? efficiency * efficiency + 1.0F : 0.0F;
        float originalSpeed = toolSpeed > 1.0F ? toolSpeed + bonus : toolSpeed;
        if (originalSpeed <= 0.0F) {
            return speed;
        }
        return speed * (Math.max(LEAF_SPEED, toolSpeed) + bonus) / originalSpeed;
    }
}
