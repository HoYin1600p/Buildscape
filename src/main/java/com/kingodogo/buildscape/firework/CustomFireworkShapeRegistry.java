package com.kingodogo.buildscape.firework;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.firework.shapes.CakeFireworkShape;
import com.kingodogo.buildscape.firework.shapes.CandyCaneFireworkShape;
import com.kingodogo.buildscape.firework.shapes.ChristmasTreeFireworkShape;
import com.kingodogo.buildscape.firework.shapes.CrownFireworkShape;
import com.kingodogo.buildscape.firework.shapes.PhoenixFireworkShape;
import com.kingodogo.buildscape.firework.shapes.PresentsFireworkShape;
import com.kingodogo.buildscape.firework.shapes.SnowflakeFireworkShape;
import com.kingodogo.buildscape.firework.shapes.TrophyFireworkShape;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CustomFireworkShapeRegistry {
    private static final Map<Byte, CustomFireworkShape> BY_NUMERIC_ID = new HashMap<>();
    private static final Map<ResourceLocation, CustomFireworkShape> BY_RESOURCE_LOCATION = new HashMap<>();

    public static final byte CAKE_ID = 5;
    public static final byte CROWN_ID = 6;
    public static final byte TROPHY_ID = 7;
    public static final byte CHRISTMAS_TREE_ID = 8;
    public static final byte PRESENTS_ID = 9;
    public static final byte CANDY_CANE_ID = 10;
    public static final byte PHOENIX_ID = 11;
    public static final byte SNOWFLAKE_ID = 12;

    public static final CustomFireworkShape CAKE = register(new CakeFireworkShape(new ResourceLocation(BuildScape.MODID, "cake"), CAKE_ID));
    public static final CustomFireworkShape CROWN = register(new CrownFireworkShape(new ResourceLocation(BuildScape.MODID, "crown"), CROWN_ID));
    public static final CustomFireworkShape TROPHY = register(new TrophyFireworkShape(new ResourceLocation(BuildScape.MODID, "trophy"), TROPHY_ID));
    public static final CustomFireworkShape CHRISTMAS_TREE = register(new ChristmasTreeFireworkShape(new ResourceLocation(BuildScape.MODID, "christmas_tree"), CHRISTMAS_TREE_ID));
    public static final CustomFireworkShape PRESENTS = register(new PresentsFireworkShape(new ResourceLocation(BuildScape.MODID, "presents"), PRESENTS_ID));
    public static final CustomFireworkShape CANDY_CANE = register(new CandyCaneFireworkShape(new ResourceLocation(BuildScape.MODID, "candy_cane"), CANDY_CANE_ID));
    public static final CustomFireworkShape PHOENIX = register(new PhoenixFireworkShape(new ResourceLocation(BuildScape.MODID, "phoenix"), PHOENIX_ID));
    public static final CustomFireworkShape SNOWFLAKE = register(new SnowflakeFireworkShape(new ResourceLocation(BuildScape.MODID, "snowflake"), SNOWFLAKE_ID));

    public static CustomFireworkShape register(CustomFireworkShape shape) {
        BY_NUMERIC_ID.put(shape.getNumericId(), shape);
        BY_RESOURCE_LOCATION.put(shape.getId(), shape);
        return shape;
    }

    public static Optional<CustomFireworkShape> getByNumericId(byte id) {
        return Optional.ofNullable(BY_NUMERIC_ID.get(id));
    }

    public static Optional<CustomFireworkShape> getByResourceLocation(ResourceLocation id) {
        return Optional.ofNullable(BY_RESOURCE_LOCATION.get(id));
    }

    public static boolean isCustomShape(byte id) {
        return BY_NUMERIC_ID.containsKey(id);
    }
}
