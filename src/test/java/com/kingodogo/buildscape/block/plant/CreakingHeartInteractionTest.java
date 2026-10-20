package com.kingodogo.buildscape.block.plant;

import com.kingodogo.buildscape.testutil.BootstrappedGameTest;
import net.minecraft.world.InteractionResult;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CreakingHeartInteractionTest extends BootstrappedGameTest {
    @Test
    public void heldItemsPassOnBothSidesRegardlessOfResin() {
        for (int spawned = 0; spawned <= 2; spawned++) {
            assertEquals(InteractionResult.PASS, CreakingHeartBlock.resinInteractionResult(false, spawned, false));
            assertEquals(InteractionResult.PASS, CreakingHeartBlock.resinInteractionResult(false, spawned, true));
        }
    }

    @Test
    public void emptyHandPassesWhenNoResinWasPlaced() {
        assertEquals(InteractionResult.PASS, CreakingHeartBlock.resinInteractionResult(true, 0, false));
    }

    @Test
    public void emptyHandConsumesWhenResinWasPlaced() {
        assertEquals(InteractionResult.CONSUME, CreakingHeartBlock.resinInteractionResult(true, 1, false));
        assertEquals(InteractionResult.CONSUME, CreakingHeartBlock.resinInteractionResult(true, 2, false));
    }

    @Test
    public void emptyHandPredictsSuccessOnTheClientBeforePlacement() {
        assertEquals(InteractionResult.SUCCESS, CreakingHeartBlock.resinInteractionResult(true, 0, true));
    }
}
