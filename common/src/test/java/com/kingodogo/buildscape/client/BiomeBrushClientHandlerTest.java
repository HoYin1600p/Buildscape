package com.kingodogo.buildscape.client;

import net.minecraft.world.phys.HitResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BiomeBrushClientHandlerTest {
    @Test void sneakingWithBrushClearsOnlyOnAirAttack() {
        assertTrue(BiomeBrushClientHandler.shouldClearSelection(true, true, HitResult.Type.MISS));
        assertFalse(BiomeBrushClientHandler.shouldClearSelection(true, true, HitResult.Type.BLOCK));
        assertFalse(BiomeBrushClientHandler.shouldClearSelection(true, true, HitResult.Type.ENTITY));
        assertFalse(BiomeBrushClientHandler.shouldClearSelection(true, true, null));
    }

    @Test void airAttackRequiresSneakingAndBrushInEitherHand() {
        assertFalse(BiomeBrushClientHandler.shouldClearSelection(false, true, HitResult.Type.MISS));
        assertFalse(BiomeBrushClientHandler.shouldClearSelection(true, false, HitResult.Type.MISS));
        assertFalse(BiomeBrushClientHandler.shouldClearSelection(false, false, HitResult.Type.MISS));
    }
}
