package com.kingodogo.buildscape.block;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public final class PlanterHelper {
    public static final EnumProperty<PlanterType> PLANTER = EnumProperty.create("planter", PlanterType.class);

    private PlanterHelper() {}

    public enum PlanterType implements StringRepresentable {
        NONE("none"),
        DIRT("dirt"),
        COARSE_DIRT("coarse_dirt"),
        MUD("mud"),
        MOSS_BLOCK("moss_block"),
        ROOTED_DIRT("rooted_dirt"),
        RED_MOSS_BLOCK("red_moss_block"),
        YELLOW_MOSS_BLOCK("yellow_moss_block"),
        ORANGE_MOSS_BLOCK("orange_moss_block"),
        PALE_MOSS_BLOCK("pale_moss_block"),
        MUDDY_MANGROVE_ROOTS("muddy_mangrove_roots"),
        GRASS_BLOCK("grass_block"),
        MYCELIUM("mycelium"),
        PODZOL("podzol"),
        SNOWY_GRASS_BLOCK("snowy_grass_block"),
        CRIMSON_NYLIUM("crimson_nylium"),
        WARPED_NYLIUM("warped_nylium"),
        SAND("sand");

        private final String name;

        PlanterType(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
