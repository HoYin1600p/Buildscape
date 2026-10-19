package com.kingodogo.buildscape.core;

import com.kingodogo.buildscape.block.OrnamentBlock;
import com.kingodogo.buildscape.block.StringLightBlock;
import com.kingodogo.buildscape.recipe.framework.cache.RecipeCacheRegressionTest;
import net.minecraft.world.item.Items;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DyeColorsTest {
    private static final Path SOURCES = Path.of("src", "main", "java", "com", "kingodogo", "buildscape");

    @BeforeClass
    public static void bootstrap() {
        RecipeCacheRegressionTest.bootstrap();
    }

    @Test
    public void tableHasTheSixteenExpectedColours() {
        String expected = "white #E8FEFD White, orange #FF5C00 Orange, magenta #FF00FF Magenta, light_blue #3CDFFF Light Blue, "
                + "yellow #FFFF00 Yellow, lime #BFFE00 Lime, pink #F686B7 Pink, gray #232526 Gray, "
                + "light_gray #B1B8C5 Light Gray, cyan #00FFFF Cyan, purple #AB87FF Purple, blue #1919EA Blue, "
                + "brown #411900 Brown, green #39FF14 Green, red #FF0000 Red, black #07010C Black";
        StringBuilder actual = new StringBuilder();
        for (DyeColors color : DyeColors.values()) {
            if (actual.length() > 0) actual.append(", ");
            actual.append(color.serializedName()).append(' ').append(color.hex()).append(' ').append(color.displayName());
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

    /** The five hand-written dye lookups (only present before they were replaced by the shared table). */
    @Test
    public void everyRemainingHandWrittenLookupMatchesTheTable() throws IOException {
        Pattern entry = Pattern.compile("Items\\.(\\w+)_DYE\\)\\s*\\{?\\s*return\\s+(?:new AbstractMap\\.SimpleEntry<>|Map\\.entry)"
                + "\\(\"(#\\w+)\",\\s*\"([^\"]+)\"\\)");
        for (String file : List.of("block/AshenKingPillarBlock.java", "block/GlowLightsBlock.java", "block/PillarBlock.java",
                "block/SmokeVentBlock.java", "event/ItemFrameParticleHandler.java")) {
            String text = Files.readString(SOURCES.resolve(file));
            Matcher matcher = entry.matcher(text);
            int count = 0;
            while (matcher.find()) {
                DyeColors color = DyeColors.valueOf(matcher.group(1));
                assertEquals(file, color.hex(), matcher.group(2));
                assertEquals(file, color.displayName(), matcher.group(3));
                count++;
            }
            assertEquals(file + " should hold the whole table or none of it", 16, count);
        }
    }
}
