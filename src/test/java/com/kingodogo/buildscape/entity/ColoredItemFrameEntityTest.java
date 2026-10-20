package com.kingodogo.buildscape.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.kingodogo.buildscape.core.NbtKeys;
import com.kingodogo.buildscape.core.ParticlePatterns;
import com.kingodogo.buildscape.testutil.BootstrappedGameTest;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.entity.EntityType;
import org.junit.Test;

/**
 * Save and load of the colored item frame's own data, on an entity built without a level. The item paths need a level
 * (setting an item notifies neighbours), so these cover the colour, particle, facing and frame-id data.
 */
public class ColoredItemFrameEntityTest extends BootstrappedGameTest {

    /** The mod's own entity type is registered late and the registries are frozen here, so a vanilla type stands in. */
    @SuppressWarnings("unchecked")
    private static ColoredItemFrameEntity newFrame() {
        return new ColoredItemFrameEntity((EntityType<ColoredItemFrameEntity>) (EntityType<?>) EntityType.ITEM_FRAME,
                null);
    }

    private static CompoundTag baseTag() {
        CompoundTag tag = new CompoundTag();
        tag.putByte("Facing", (byte) Direction.NORTH.get3DDataValue());
        return tag;
    }

    @Test
    public void aNewFrameStartsWhiteEmptyAndWithoutParticles() {
        ColoredItemFrameEntity frame = newFrame();

        assertEquals("white", frame.getColorVariant());
        assertEquals(ParticlePatterns.NONE, frame.getParticlePattern());
        assertEquals("", frame.getParticleColorsRaw());
        assertTrue(frame.getItem().isEmpty());
        assertEquals(0, frame.getRotation());
    }

    @Test
    public void emptyParticleValuesFallBackToTheDefaults() {
        ColoredItemFrameEntity frame = newFrame();
        frame.setParticlePattern("spiral");
        frame.setParticleColorsRaw("red;blue");

        frame.setParticlePattern("");
        frame.setParticleColorsRaw(null);

        assertEquals(ParticlePatterns.NONE, frame.getParticlePattern());
        assertEquals("", frame.getParticleColorsRaw());
    }

    @Test
    public void colorParticlesFacingAndFrameIdSurviveASaveAndLoad() {
        CompoundTag source = baseTag();
        source.putString(NbtKeys.COLOR_VARIANT, "lime");
        source.putString(NbtKeys.BUILD_SCAPE_PARTICLE_PATTERN, "spiral");
        source.putString(NbtKeys.BUILD_SCAPE_PARTICLE_COLORS_RAW, "red;blue");
        source.putString(NbtKeys.BUILD_SCAPE_FRAME_ID, "I-F-LG-1");
        source.putBoolean(NbtKeys.INVISIBLE, true);
        source.putBoolean("Fixed", true);

        ColoredItemFrameEntity first = newFrame();
        first.readAdditionalSaveData(source);
        CompoundTag saved = new CompoundTag();
        first.addAdditionalSaveData(saved);
        ColoredItemFrameEntity second = newFrame();
        second.readAdditionalSaveData(saved);

        assertEquals("lime", saved.getString(NbtKeys.COLOR_VARIANT));
        assertEquals((byte) Direction.NORTH.get3DDataValue(), saved.getByte("Facing"));
        assertTrue(saved.getBoolean("Fixed"));
        assertFalse(saved.contains(NbtKeys.ITEM));
        assertEquals("lime", second.getColorVariant());
        assertEquals("spiral", second.getParticlePattern());
        assertEquals("red;blue", second.getParticleColorsRaw());
        assertTrue(second.isInvisible());
        assertEquals("I-F-LG-1", second.getPersistentData().getString(NbtKeys.BUILD_SCAPE_FRAME_ID));
        assertEquals(Direction.NORTH, second.getDirection());
    }

    @Test
    public void theOlderPillarStyleKeysAreStillRead() {
        CompoundTag legacy = baseTag();
        legacy.putString(NbtKeys.PILLAR_PATTERN, "ring");
        ListTag colors = new ListTag();
        colors.add(StringTag.valueOf("green"));
        colors.add(StringTag.valueOf("pink"));
        legacy.put(NbtKeys.PILLAR_COLORS, colors);

        ColoredItemFrameEntity frame = newFrame();
        frame.readAdditionalSaveData(legacy);

        assertEquals("ring", frame.getParticlePattern());
        assertEquals("green;pink", frame.getParticleColorsRaw());
    }

    @Test
    public void anUnknownLegacyItemLeavesTheFrameEmpty() {
        CompoundTag legacy = baseTag();
        legacy.putString(NbtKeys.PILLAR_ITEM, "no such item!");

        ColoredItemFrameEntity frame = newFrame();
        frame.readAdditionalSaveData(legacy);

        assertTrue(frame.getItem().isEmpty());
    }
}
