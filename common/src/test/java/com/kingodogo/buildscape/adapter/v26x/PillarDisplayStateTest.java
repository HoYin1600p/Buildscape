package com.kingodogo.buildscape.adapter.v26x;

import com.kingodogo.buildscape.adapter.v26x.client.ClientPillarEntities;
import com.kingodogo.buildscape.adapter.v26x.client.ClientPillarVariants;
import com.kingodogo.buildscape.client.renderer.MobState;
import net.minecraft.world.item.DyeColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PillarDisplayStateTest {
    @Test void namedColorsChooseTheMatchingSheepVariant() {
        for (DyeColor color : DyeColor.values()) {
            MobState state = new MobState();
            state.parsedStates.add(color.getName());
            assertEquals(color, ClientPillarEntities.dyeColor(state));
        }
    }

    @Test void slimeSizesMatchTheReferenceSavedSizePlusOne() {
        String[] names = {"tiny", "small", "medium", "large", "huge", "giant"};
        int[] expected = {1, 2, 3, 5, 9, 9};
        for (int i = 0; i < names.length; i++) {
            MobState state = new MobState();
            state.parsedStates.add(names[i]);
            assertEquals(expected[i], ClientPillarEntities.slimeSize(state));
        }
    }

    @Test void referenceVariantAliasesResolveToTheSameVariant() {
        for (String alias : new String[] {"white_splotched", "spotted"}) {
            MobState state = new MobState();
            state.parsedStates.add(alias);
            assertEquals(3, ClientPillarVariants.namedIndex(state, "brown", "white", "black", "white_splotched|spotted"));
        }
        MobState unknown = new MobState();
        unknown.parsedStates.add("unrecognised");
        assertEquals(-1, ClientPillarVariants.namedIndex(unknown, "brown", "white", "black"));
    }
}
