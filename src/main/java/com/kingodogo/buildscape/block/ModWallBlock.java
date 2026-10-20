package com.kingodogo.buildscape.block;

import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;

public class ModWallBlock extends WallBlock {

    @SuppressWarnings("unused")
    private final RegistryObject<?> dropItem;

    public ModWallBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.dropItem = null;
    }

    public ModWallBlock(
            BlockBehaviour.Properties properties,
            RegistryObject<?> dropItem) {
        super(properties);
        this.dropItem = dropItem;
    }

}
