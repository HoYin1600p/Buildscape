package com.kingodogo.buildscape.core;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;

/**
 * The 16 BuildScape dye colours in vanilla order: the name used in block states and save data, the RGB value, and
 * the English display name. These are BuildScape's own colours, not vanilla's {@code DyeColor} values.
 */
public enum DyeColors {
    WHITE("white", "#E8FEFD", "White", "W"),
    ORANGE("orange", "#FF5C00", "Orange", "O"),
    MAGENTA("magenta", "#FF00FF", "Magenta", "M"),
    LIGHT_BLUE("light_blue", "#3CDFFF", "Light Blue", "LB"),
    YELLOW("yellow", "#FFFF00", "Yellow", "Y"),
    LIME("lime", "#BFFE00", "Lime", "L"),
    PINK("pink", "#F686B7", "Pink", "P"),
    GRAY("gray", "#232526", "Gray", "GR"),
    LIGHT_GRAY("light_gray", "#B1B8C5", "Light Gray", "LG"),
    CYAN("cyan", "#00FFFF", "Cyan", "C"),
    PURPLE("purple", "#AB87FF", "Purple", "PU"),
    BLUE("blue", "#1919EA", "Blue", "B"),
    BROWN("brown", "#411900", "Brown", "BR"),
    GREEN("green", "#39FF14", "Green", "G"),
    RED("red", "#FF0000", "Red", "R"),
    BLACK("black", "#07010C", "Black", "BL");

    private final String serializedName;
    private final String hex;
    private final int rgb;
    private final String displayName;
    private final String shortCode;

    DyeColors(String serializedName, String hex, String displayName, String shortCode) {
        this.serializedName = serializedName;
        this.hex = hex;
        this.rgb = Integer.parseInt(hex.substring(1), 16);
        this.displayName = displayName;
        this.shortCode = shortCode;
    }

    /** The colour with this {@link #serializedName()}, or {@code null} for any other text (including null). */
    @Nullable
    public static DyeColors fromName(@Nullable String name) {
        for (DyeColors color : values()) {
            if (color.serializedName.equals(name)) {
                return color;
            }
        }
        return null;
    }

    /** Short code such as {@code LB} that item frame ids use for the colour. */
    public String shortCode() {
        return shortCode;
    }

    /** Lower-case id such as {@code light_blue}. */
    public String serializedName() {
        return serializedName;
    }

    /** Colour as {@code #RRGGBB} in upper case. */
    public String hex() {
        return hex;
    }

    /** Colour as {@code 0xRRGGBB}. */
    public int rgb() {
        return rgb;
    }

    /** English name such as {@code Light Blue}. */
    public String displayName() {
        return displayName;
    }

    /** The vanilla dye item of this colour. */
    public Item dyeItem() {
        return switch (this) {
            case WHITE -> Items.WHITE_DYE;
            case ORANGE -> Items.ORANGE_DYE;
            case MAGENTA -> Items.MAGENTA_DYE;
            case LIGHT_BLUE -> Items.LIGHT_BLUE_DYE;
            case YELLOW -> Items.YELLOW_DYE;
            case LIME -> Items.LIME_DYE;
            case PINK -> Items.PINK_DYE;
            case GRAY -> Items.GRAY_DYE;
            case LIGHT_GRAY -> Items.LIGHT_GRAY_DYE;
            case CYAN -> Items.CYAN_DYE;
            case PURPLE -> Items.PURPLE_DYE;
            case BLUE -> Items.BLUE_DYE;
            case BROWN -> Items.BROWN_DYE;
            case GREEN -> Items.GREEN_DYE;
            case RED -> Items.RED_DYE;
            case BLACK -> Items.BLACK_DYE;
        };
    }

    /** The colour of a vanilla dye item (compared by identity), or {@code null} for any other item. */
    @Nullable
    public static DyeColors fromDyeItem(Item item) {
        for (DyeColors color : values()) {
            if (color.dyeItem() == item) {
                return color;
            }
        }
        return null;
    }
}
