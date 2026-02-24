package com.kingodogo.buildscape.client.renderer;

import net.minecraft.client.particle.SpriteSet;
import java.util.HashMap;
import java.util.Map;

/**
 * WingParticleAssets - Hub for capturing registered particle sprite sets.
 * This allows the CosmeticWingRenderer to use the exact textures registered in the particle system.
 */
public class WingParticleAssets {
    private static final Map<String, SpriteSet> SPRITE_SETS = new HashMap<>();

    public static void registerSprites(String id, SpriteSet sprites) {
        SPRITE_SETS.put(id.toLowerCase(), sprites);
    }

    public static SpriteSet getSprites(String id) {
        return SPRITE_SETS.get(id.toLowerCase());
    }
}
