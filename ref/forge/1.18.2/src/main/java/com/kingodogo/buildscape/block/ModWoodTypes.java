package com.kingodogo.buildscape.block;

import net.minecraft.world.level.block.state.properties.WoodType;

public class ModWoodTypes {

    public static final WoodType MANGROVE = WoodType.create(
            "buildscape:mangrove"
    );
    public static final WoodType BAMBOO = WoodType.create("buildscape:bamboo");

    static {
        WoodType.register(MANGROVE);
        WoodType.register(BAMBOO);
    }
}
