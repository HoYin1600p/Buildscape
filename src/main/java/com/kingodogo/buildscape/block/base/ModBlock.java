package com.kingodogo.buildscape.block.base;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
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
            RegistryObject<?> dropItem) {
        super(properties);
        this.dropItem = dropItem;
    }

}
