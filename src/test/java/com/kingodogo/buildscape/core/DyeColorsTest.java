package com.kingodogo.buildscape.core;

import com.kingodogo.buildscape.testutil.BootstrappedGameTest;
import com.kingodogo.buildscape.block.OrnamentBlock;
import com.kingodogo.buildscape.block.StringLightBlock;
import net.minecraft.world.item.Items;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DyeColorsTest extends BootstrappedGameTest {

    @Test
    public void tableHasTheSixteenExpectedColours() {
        String expected = "white #E8FEFD White, orange #FF5C00 Orange, magenta #FF00FF Magenta, light_blue #3CDFFF Light Blue, "
                + "yellow #FFFF00 Yellow, lime #BFFE00 Lime, pink #F686B7 Pink, gray #232526 Gray, "
                + "light_gray #B1B8C5 Light Gray, cyan #00FFFF Cyan, purple #AB87FF Purple, blue #1919EA Blue, "
                + "brown #411900 Brown, green #39FF14 Green, red #FF0000 Red, black #07010C Black";
        StringBuilder actual = new StringBuilder();
        for (DyeColors color : DyeColors.values()) {
            if (actual.length() > 0) {
                actual.append(", ");
            }
            actual.append(color.serializedName()).append(' ').append(color.hex()).append(' ')
                    .append(color.displayName());
            assertEquals(Integer.parseInt(color.hex().substring(1), 16), color.rgb());
        }
        assertEquals(expected, actual.toString());
    }

    @Test
    public void dyeItemsMapBackToTheirColour() {
        for (DyeColors color : DyeColors.values()) {
            assertEquals(color, DyeColors.fromDyeItem(color.dyeItem()));
        }
        assertEquals(Items.WHITE_DYE, DyeColors.WHITE.dyeItem());
        assertNull(DyeColors.fromDyeItem(Items.STICK));
    }

    @Test
    public void stringColourEnumsMatchTheTable() {
        OrnamentBlock.StringColor[] ornament = OrnamentBlock.StringColor.values();
        StringLightBlock.StringColor[] light = StringLightBlock.StringColor.values();
        assertEquals(DyeColors.values().length, ornament.length);
        assertEquals(DyeColors.values().length, light.length);
        for (DyeColors color : DyeColors.values()) {
            OrnamentBlock.StringColor o = ornament[color.ordinal()];
            StringLightBlock.StringColor l = light[color.ordinal()];
            assertEquals(color.serializedName(), o.getSerializedName());
            assertEquals(color.rgb(), o.getColor());
            assertEquals(color.displayName(), o.getDisplayName());
            assertEquals(color.serializedName(), l.getSerializedName());
            assertEquals(color.rgb(), l.getColor());
            assertEquals(color.displayName(), l.getDisplayName());
        }
    }
}
