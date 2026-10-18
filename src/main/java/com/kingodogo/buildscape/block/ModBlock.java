package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;

public class ModBlock extends Block {

    @SuppressWarnings("unused")
    private final RegistryObject<?> dropItem;

    public ModBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.dropItem = null;
    }

    public ModBlock(
            BlockBehaviour.Properties properties,
            RegistryObject<?> dropItem
    ) {
        super(properties);
        this.dropItem = dropItem;
    }

}
