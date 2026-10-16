package com.kingodogo.buildscape.firework;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.firework.shapes.CakeFireworkShape;
import com.kingodogo.buildscape.firework.shapes.CandyCaneFireworkShape;
import com.kingodogo.buildscape.firework.shapes.ChristmasTreeFireworkShape;
import com.kingodogo.buildscape.firework.shapes.CrownFireworkShape;
import com.kingodogo.buildscape.firework.shapes.PhoenixFireworkShape;
import com.kingodogo.buildscape.firework.shapes.PresentsFireworkShape;
import com.kingodogo.buildscape.firework.shapes.SnowflakeFireworkShape;
import com.kingodogo.buildscape.firework.shapes.TrophyFireworkShape;
import com.kingodogo.buildscape.util.CommonId;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class CustomFireworkShapeRegistry {
    private static final Map<Byte, CustomFireworkShape> BY_NUMERIC_ID = new HashMap<>();
    private static final Map<CommonId, CustomFireworkShape> BY_RESOURCE_LOCATION = new HashMap<>();

    public static final byte CAKE_ID = 5;
    public static final byte CROWN_ID = 6;
    public static final byte TROPHY_ID = 7;
    public static final byte CHRISTMAS_TREE_ID = 8;
    public static final byte PRESENTS_ID = 9;
    public static final byte CANDY_CANE_ID = 10;
    public static final byte PHOENIX_ID = 11;
    public static final byte SNOWFLAKE_ID = 12;

    public static final CustomFireworkShape CAKE = register(new CakeFireworkShape(id("cake"), CAKE_ID));
    public static final CustomFireworkShape CROWN = register(new CrownFireworkShape(id("crown"), CROWN_ID));
    public static final CustomFireworkShape TROPHY = register(new TrophyFireworkShape(id("trophy"), TROPHY_ID));
    public static final CustomFireworkShape CHRISTMAS_TREE = register(new ChristmasTreeFireworkShape(id("christmas_tree"), CHRISTMAS_TREE_ID));
    public static final CustomFireworkShape PRESENTS = register(new PresentsFireworkShape(id("presents"), PRESENTS_ID));
    public static final CustomFireworkShape CANDY_CANE = register(new CandyCaneFireworkShape(id("candy_cane"), CANDY_CANE_ID));
    public static final CustomFireworkShape PHOENIX = register(new PhoenixFireworkShape(id("phoenix"), PHOENIX_ID));
    public static final CustomFireworkShape SNOWFLAKE = register(new SnowflakeFireworkShape(id("snowflake"), SNOWFLAKE_ID));

    private CustomFireworkShapeRegistry() {
    }

    public static CustomFireworkShape register(CustomFireworkShape shape) {
        BY_NUMERIC_ID.put(shape.getNumericId(), shape);
        BY_RESOURCE_LOCATION.put(shape.getId(), shape);
        return shape;
    }

    public static Optional<CustomFireworkShape> getByNumericId(byte id) {
        return Optional.ofNullable(BY_NUMERIC_ID.get(id));
    }

    public static Optional<CustomFireworkShape> getByResourceLocation(CommonId id) {
        return Optional.ofNullable(BY_RESOURCE_LOCATION.get(id));
    }

    public static boolean isCustomShape(byte id) {
        return BY_NUMERIC_ID.containsKey(id);
    }

    private static CommonId id(String path) {
        return CommonId.of(BuildscapeCommon.MOD_ID, path);
    }
}
