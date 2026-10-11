package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;
public class ModStairBlock extends StairBlock {
    @SuppressWarnings("unused")
    private final Supplier<?> dropItem;
    private final Block baseBlock;
    public ModStairBlock(BlockState baseState, BlockBehaviour.Properties properties) {
        super(safeBaseState(baseState), properties);
        this.dropItem = null;
        this.baseBlock = baseState.getBlock();
    }
    public ModStairBlock(BlockState baseState, BlockBehaviour.Properties properties, Supplier<?> dropItem) {
        super(safeBaseState(baseState), properties);
        this.dropItem = dropItem;
        this.baseBlock = baseState.getBlock();
    }
    private static BlockState safeBaseState(BlockState baseState) {
        if (baseState.getProperties().isEmpty()) {
            return baseState;
        }
        return Blocks.OAK_PLANKS.defaultBlockState();
    }
    public Block getBaseBlock() {
        return this.baseBlock;
    }
    private boolean isTintedGlassLike() {
        return this.baseBlock == Blocks.TINTED_GLASS;
    }

    public boolean isTintedBase() { return isTintedGlassLike(); }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        float destroySpeed = state.getDestroySpeed(level, pos);
        if (destroySpeed == -1.0F) {
            return 0.0F;
        }

        ItemStack tool = player.getMainHandItem();
        float speedMultiplier = tool.isEmpty() ? 1.0F : tool.getDestroySpeed(state);
        if (speedMultiplier > 1.0F) {
            int efficiencyLevel = com.kingodogo.buildscape.platform.Services.PLATFORM.getEfficiencyLevel(tool);
            speedMultiplier += efficiencyLevel > 0 ? efficiencyLevel * efficiencyLevel + 1 : 0;
        }

        float difficultyModifier = player.hasCorrectToolForDrops(state) ? 30.0F : 100.0F;
        return speedMultiplier / destroySpeed / difficultyModifier;
    }
}
