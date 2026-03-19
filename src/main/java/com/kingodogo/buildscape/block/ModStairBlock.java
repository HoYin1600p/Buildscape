package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;

public class ModStairBlock extends StairBlock {

    @SuppressWarnings("unused")
    private final RegistryObject<?> dropItem;
    private final net.minecraft.world.level.block.Block baseBlock;

    public ModStairBlock(
            BlockState baseState,
            BlockBehaviour.Properties properties
    ) {
        super(baseState, properties);
        this.dropItem = null;
        this.baseBlock = baseState.getBlock();
    }

    public ModStairBlock(
            BlockState baseState,
            BlockBehaviour.Properties properties,
            RegistryObject<?> dropItem
    ) {
        super(baseState, properties);
        this.dropItem = dropItem;
        this.baseBlock = baseState.getBlock();
    }

    public net.minecraft.world.level.block.Block getBaseBlock() {
        return this.baseBlock;
    }

    @Override
    public float getDestroyProgress(
            BlockState state,
            Player player,
            BlockGetter level,
            BlockPos pos
    ) {
        float destroySpeed = state.getDestroySpeed(level, pos);
        if (destroySpeed == -1.0F) {
            return 0.0F;
        }

        int efficiencyLevel =
                net.minecraft.world.item.enchantment.EnchantmentHelper.getBlockEfficiency(
                        player
                );
        ItemStack tool = player.getMainHandItem();

        float speedMultiplier = 1.0F;
        if (!tool.isEmpty()) {
            speedMultiplier = tool.getDestroySpeed(state);
        }

        if (speedMultiplier > 1.0F) {
            int efficiencyBonus = efficiencyLevel > 0
                    ? efficiencyLevel * efficiencyLevel + 1
                    : 0;
            speedMultiplier += (float) efficiencyBonus;
        }

        float difficultyModifier = player.hasCorrectToolForDrops(state)
                ? 30.0F
                : 100.0F;
        return speedMultiplier / destroySpeed / difficultyModifier;
    }
}
