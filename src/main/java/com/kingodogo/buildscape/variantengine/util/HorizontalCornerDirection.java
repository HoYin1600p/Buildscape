package com.kingodogo.buildscape.variantengine.util;

import net.minecraft.Util;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public enum HorizontalCornerDirection implements StringRepresentable {
    SOUTH_WEST(0, 2, "south_west", new Vec3i(-1, 0, 1)),
    NORTH_WEST(1, 3, "north_west", new Vec3i(-1, 0, -1)),
    NORTH_EAST(2, 0, "north_east", new Vec3i(1, 0, -1)),
    SOUTH_EAST(3, 1, "south_east", new Vec3i(1, 0, 1));

    private static final HorizontalCornerDirection[] ALL = values();
    private static final Map<String, HorizontalCornerDirection> NAME_MAP = Arrays.stream(ALL).collect(Collectors.toMap(HorizontalCornerDirection::getName, (direction) -> direction));
    private static final HorizontalCornerDirection[] VALUES = Arrays.stream(ALL).sorted(Comparator.comparingInt((direction) -> direction.id)).toArray(HorizontalCornerDirection[]::new);
    private final String name;
    private final int idOpposite;
    private final int id;
    private final Vec3i vector;

    HorizontalCornerDirection(int id, int idOpposite, String name, Vec3i vector) {
        this.id = id;
        this.idOpposite = idOpposite;
        this.name = name;
        this.vector = vector;
    }

    @Nullable
    public static HorizontalCornerDirection byName(@Nullable String name) {
        return name == null ? null : NAME_MAP.get(name.toLowerCase(Locale.ROOT));
    }

    public static HorizontalCornerDirection byId(int id) {
        return VALUES[Mth.abs(id % VALUES.length)];
    }

    public static HorizontalCornerDirection fromHorizontal(int value) {
        return VALUES[Mth.abs(value % VALUES.length)];
    }

    public static HorizontalCornerDirection fromRotation(double rotation) {
        return fromHorizontal(Mth.floor(rotation / 90.0D) & 3);
    }

    public static HorizontalCornerDirection random(Random random) {
        return Util.getRandom(ALL, random);
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }

    public HorizontalCornerDirection getOpposite() {
        return byId(this.idOpposite);
    }

    public HorizontalCornerDirection rotateYClockwise() {
        return byId(Math.floorMod(this.id + 1, VALUES.length));
    }

    public HorizontalCornerDirection rotateYCounterclockwise() {
        return byId(Math.floorMod(this.id - 1, VALUES.length));
    }

    public String getName() {
        return this.name;
    }

    public float asRotation() {
        return (float) (((this.id & 3) + 0.5) * 90);
    }

    @Override
    public String toString() {
        return this.name;
    }

    public Vec3i getVector() {
        return this.vector;
    }

    public HorizontalCornerDirection rotate(Rotation rotation) {
        switch (rotation) {
            case CLOCKWISE_90:
                return this.rotateYClockwise();
            case CLOCKWISE_180:
                return this.getOpposite();
            case COUNTERCLOCKWISE_90:
                return this.rotateYCounterclockwise();
            default:
                return this;
        }
    }

    public HorizontalCornerDirection mirror(Mirror mirror) {
        switch (mirror) {
            case FRONT_BACK:
                switch (this) {
                    case NORTH_EAST:
                        return NORTH_WEST;
                    case NORTH_WEST:
                        return NORTH_EAST;
                    case SOUTH_EAST:
                        return SOUTH_WEST;
                    case SOUTH_WEST:
                        return SOUTH_EAST;
                }
            case LEFT_RIGHT:
                switch (this) {
                    case NORTH_EAST:
                        return SOUTH_EAST;
                    case NORTH_WEST:
                        return SOUTH_WEST;
                    case SOUTH_EAST:
                        return NORTH_EAST;
                    case SOUTH_WEST:
                        return NORTH_WEST;
                }
            default:
                return this;
        }
    }
}
