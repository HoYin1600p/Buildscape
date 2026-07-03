package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
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
        super(safeBaseState(baseState), safeProperties(baseState, properties));
        this.dropItem = null;
        this.baseBlock = baseState.getBlock();
    }

    protected ModStairBlock(
            BlockState baseState,
            BlockBehaviour.Properties properties,
            boolean preserveProperties
    ) {
        super(
                preserveProperties ? baseState : safeBaseState(baseState),
                preserveProperties ? properties : safeProperties(baseState, properties));
        this.dropItem = null;
        this.baseBlock = baseState.getBlock();
    }

    public ModStairBlock(
            BlockState baseState,
            BlockBehaviour.Properties properties,
            RegistryObject<?> dropItem
    ) {
        super(safeBaseState(baseState), safeProperties(baseState, properties));
        this.dropItem = dropItem;
        this.baseBlock = baseState.getBlock();
    }

    private static BlockState safeBaseState(BlockState baseState) {
        if (baseState.getProperties().isEmpty()) {
            return baseState;
        }

        return Blocks.OAK_PLANKS.defaultBlockState();
    }

    private static BlockBehaviour.Properties safeProperties(
            BlockState baseState,
            BlockBehaviour.Properties properties
    ) {
        if (baseState.getProperties().isEmpty()) {
            return properties;
        }

        Block baseBlock = baseState.getBlock();
        SoundType soundType = baseBlock.getSoundType(baseState);
        BlockBehaviour.Properties safeProperties = BlockBehaviour.Properties
                .of(baseState.getMaterial())
                .strength(safeDestroySpeed(baseBlock), baseBlock.getExplosionResistance())
                .sound(soundType);

        if (baseState.requiresCorrectToolForDrops()) {
            safeProperties.requiresCorrectToolForDrops();
        }

        return safeProperties;
    }

    private static float safeDestroySpeed(Block baseBlock) {
        if (baseBlock == Blocks.AZALEA_LEAVES || baseBlock == Blocks.FLOWERING_AZALEA_LEAVES) {
            return 0.2f;
        }

        if (baseBlock == Blocks.REDSTONE_LAMP) {
            return 0.3f;
        }

        if (baseBlock == Blocks.HAY_BLOCK) {
            return 0.5f;
        }

        if (baseBlock == Blocks.CARVED_PUMPKIN || baseBlock == Blocks.TARGET) {
            return 1.0f;
        }

        if (baseBlock == Blocks.PURPUR_PILLAR) {
            return 1.5f;
        }

        return 2.0f;
    }

    public net.minecraft.world.level.block.Block getBaseBlock() {
        return this.baseBlock;
    }

    private boolean isTintedGlassLike() {
        return this.baseBlock == Blocks.TINTED_GLASS;
    }

    @Override
    public boolean propagatesSkylightDown(
            BlockState state,
            BlockGetter level,
            BlockPos pos
    ) {
        return !this.isTintedGlassLike() && super.propagatesSkylightDown(state, level, pos);
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        if (this.isTintedGlassLike()) {
            return 15;
        }

        return super.getLightBlock(state, level, pos);
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
