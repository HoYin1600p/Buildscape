package com.kingodogo.buildscape.core;

import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NbtKeysTest {
    @Test
    public void storedKeysKeepTheirSavedSpelling() {
        assertEquals("StoredItem", NbtKeys.STORED_ITEM);
        assertEquals("BuildScapeFrameId", NbtKeys.BUILD_SCAPE_FRAME_ID);
        assertEquals("BuildScapeParticlePattern", NbtKeys.BUILD_SCAPE_PARTICLE_PATTERN);
        assertEquals("BuildScapeParticleColors", NbtKeys.BUILD_SCAPE_PARTICLE_COLORS);
        assertEquals("GhostFilters", NbtKeys.GHOST_FILTERS);
        assertEquals("WanderingHomemakerCooldownRealTime", NbtKeys.WANDERING_HOMEMAKER_COOLDOWN_REAL_TIME);
        assertEquals("ITEM", NbtKeys.PILLAR_ITEM);
        assertEquals("PATTERN", NbtKeys.PILLAR_PATTERN);
        assertEquals("COLORS", NbtKeys.PILLAR_COLORS);
        assertEquals("has_haze", NbtKeys.HAS_HAZE_PROPERTY);
        assertEquals("HasHaze", NbtKeys.HAS_HAZE);
        assertEquals("BlockEntityTag", NbtKeys.BLOCK_ENTITY_TAG);
        assertEquals("equipped_cosmetics", NbtKeys.EQUIPPED_COSMETICS);
    }

    @Test
    public void everyKeyIsAUniqueNonEmptyString() throws IllegalAccessException {
        Set<String> seen = new HashSet<>();
        for (Field field : NbtKeys.class.getDeclaredFields()) {
            if (!Modifier.isPublic(field.getModifiers()) || !Modifier.isStatic(field.getModifiers())) continue;
            String value = (String) field.get(null);
            assertTrue(field.getName(), !value.isEmpty());
            assertTrue("duplicate value " + value + " in " + field.getName(), seen.add(value));
        }
        assertTrue(seen.size() > 90);
    }
}
