package com.kingodogo.buildscape.adapter.v118x;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SignWoodTypesTest {
    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void blockFactoryRegistersBothSignWoodTypesBeforeCreatingBlocks() {
        // This is the factory initialization used by block setup. No client or
        // loader registry services are needed to verify the renderer's input.
        new BlockFactory();
        assertRegisteredOnce("buildscape:mangrove");
        assertRegisteredOnce("buildscape:bamboo");

        // Constructing another factory must not register duplicate wood types.
        new BlockFactory();
        assertRegisteredOnce("buildscape:mangrove");
        assertRegisteredOnce("buildscape:bamboo");
    }

    private static void assertRegisteredOnce(String name) {
        assertEquals(1L, WoodType.values().filter(type -> name.equals(type.name())).count(),
                name + " must be available when SignRenderer builds its model map");
    }
}
