package com.kingodogo.buildscape.adapter.v26x;

import com.kingodogo.buildscape.adapter.v26x.client.CopperChestRenderer;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CopperChestTextureTest {
    @Test void waxedAndUnwaxedChestsShareEveryOxidationAndHalfTexture() {
        for (String oxidation : new String[] {"", "exposed_", "weathered_", "oxidized_"}) {
            for (ChestType half : ChestType.values()) {
                String suffix = half == ChestType.SINGLE ? "" : "_" + half.getSerializedName();
                String expected = "buildscape:textures/entity/chest/" + oxidation + "copper_chest" + suffix + ".png";
                for (String wax : new String[] {"", "waxed_"}) {
                    assertEquals(expected, CopperChestRenderer.textureFor(wax + oxidation + "copper_chest", half).toString());
                }
            }
        }
    }
}
