package com.kingodogo.buildscape.sound;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.registry.RegistrySupplier;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;

import com.kingodogo.buildscape.block.CustomSoundType;
public class ModSounds {
    public static final RegistrySupplier<SoundEvent> COPPER_GRATE_BREAK = registerSound("block.copper_grate.break");
    public static final RegistrySupplier<SoundEvent> COPPER_GRATE_STEP = registerSound("block.copper_grate.step");
    public static final RegistrySupplier<SoundEvent> COPPER_GRATE_PLACE = COPPER_GRATE_STEP;
    public static final RegistrySupplier<SoundEvent> COPPER_GRATE_HIT = COPPER_GRATE_STEP;
    public static final RegistrySupplier<SoundEvent> COPPER_GRATE_FALL = COPPER_GRATE_STEP;
    public static final RegistrySupplier<SoundEvent> COPPER_BULB_BREAK = registerSound("block.copper_bulb.break");
    public static final RegistrySupplier<SoundEvent> COPPER_BULB_PLACE = registerSound("block.copper_bulb.place");
    public static final RegistrySupplier<SoundEvent> COPPER_BULB_STEP = registerSound("block.copper_bulb.step");
    public static final RegistrySupplier<SoundEvent> COPPER_BULB_TOGGLE = registerSound("block.copper_bulb.toggle");
    public static final RegistrySupplier<SoundEvent> COPPER_BULB_HIT = COPPER_BULB_STEP;
    public static final RegistrySupplier<SoundEvent> COPPER_BULB_FALL = COPPER_BULB_STEP;
    public static final RegistrySupplier<SoundEvent> MUD_BREAK = registerSound("block.mud.break");
    public static final RegistrySupplier<SoundEvent> MUD_STEP = registerSound("block.mud.step");
    public static final RegistrySupplier<SoundEvent> MUD_PLACE = MUD_STEP;
    public static final RegistrySupplier<SoundEvent> MUD_HIT = MUD_STEP;
    public static final RegistrySupplier<SoundEvent> MUD_FALL = MUD_STEP;
    public static final RegistrySupplier<SoundEvent> PACKED_MUD_BREAK = registerSound("block.packed_mud.break");
    public static final RegistrySupplier<SoundEvent> PACKED_MUD_STEP = registerSound("block.packed_mud.step");
    public static final RegistrySupplier<SoundEvent> PACKED_MUD_PLACE = registerSound("block.packed_mud.place");
    public static final RegistrySupplier<SoundEvent> PACKED_MUD_HIT = registerSound("block.packed_mud.hit");
    public static final RegistrySupplier<SoundEvent> PACKED_MUD_FALL = registerSound("block.packed_mud.fall");
    public static final RegistrySupplier<SoundEvent> MUD_BRICKS_BREAK = registerSound("block.mud_bricks.break");
    public static final RegistrySupplier<SoundEvent> MUD_BRICKS_STEP = registerSound("block.mud_bricks.step");
    public static final RegistrySupplier<SoundEvent> MUD_BRICKS_PLACE = registerSound("block.mud_bricks.place");
    public static final RegistrySupplier<SoundEvent> MUD_BRICKS_HIT = registerSound("block.mud_bricks.hit");
    public static final RegistrySupplier<SoundEvent> MUD_BRICKS_FALL = registerSound("block.mud_bricks.fall");
    public static final RegistrySupplier<SoundEvent> DECORATED_POT_PLACE = registerSound("block.decorated_pot.place");
    public static final RegistrySupplier<SoundEvent> DECORATED_POT_BREAK = registerSound("block.decorated_pot.break");
    public static final RegistrySupplier<SoundEvent> DECORATED_POT_HIT = registerSound("block.decorated_pot.hit");
    public static final RegistrySupplier<SoundEvent> DECORATED_POT_STEP = registerSound("block.decorated_pot.step");
    public static final RegistrySupplier<SoundEvent> DECORATED_POT_FALL = registerSound("block.decorated_pot.fall");
    public static final RegistrySupplier<SoundEvent> DECORATED_POT_INSERT_ITEM = registerSound("block.decorated_pot.insert_item");
    public static final RegistrySupplier<SoundEvent> DECORATED_POT_INSERT_FAIL = registerSound("block.decorated_pot.insert_fail");
    public static final RegistrySupplier<SoundEvent> DECORATED_POT_SHATTER = registerSound("block.decorated_pot.shatter");
    public static final RegistrySupplier<SoundEvent> FLOWER_BED_BREAK = registerSound("block.flower_bed.break");
    public static final RegistrySupplier<SoundEvent> FLOWER_BED_STEP = registerSound("block.flower_bed.step");
    public static final RegistrySupplier<SoundEvent> FLOWER_BED_PLACE = registerSound("block.flower_bed.place");
    public static final RegistrySupplier<SoundEvent> FLOWER_BED_HIT = registerSound("block.flower_bed.hit");
    public static final RegistrySupplier<SoundEvent> FLOWER_BED_FALL = registerSound("block.flower_bed.fall");
    public static final RegistrySupplier<SoundEvent> MANGROVE_ROOTS_BREAK = registerSound("block.mangrove_roots.break");
    public static final RegistrySupplier<SoundEvent> MANGROVE_ROOTS_STEP = registerSound("block.mangrove_roots.step");
    public static final RegistrySupplier<SoundEvent> MANGROVE_ROOTS_PLACE = registerSound("block.mangrove_roots.place");
    public static final RegistrySupplier<SoundEvent> MANGROVE_ROOTS_HIT = registerSound("block.mangrove_roots.hit");
    public static final RegistrySupplier<SoundEvent> MANGROVE_ROOTS_FALL = registerSound("block.mangrove_roots.fall");
    public static final RegistrySupplier<SoundEvent> MUDDY_MANGROVE_ROOTS_BREAK = registerSound("block.muddy_mangrove_roots.break");
    public static final RegistrySupplier<SoundEvent> MUDDY_MANGROVE_ROOTS_STEP = registerSound("block.muddy_mangrove_roots.step");
    public static final RegistrySupplier<SoundEvent> MUDDY_MANGROVE_ROOTS_PLACE = registerSound("block.muddy_mangrove_roots.place");
    public static final RegistrySupplier<SoundEvent> MUDDY_MANGROVE_ROOTS_HIT = registerSound("block.muddy_mangrove_roots.hit");
    public static final RegistrySupplier<SoundEvent> MUDDY_MANGROVE_ROOTS_FALL = registerSound("block.muddy_mangrove_roots.fall");
    public static final RegistrySupplier<SoundEvent> GEYSER_CONTINUOUS_START = registerSound("block.geyser_continuous.start");
    public static final RegistrySupplier<SoundEvent> GEYSER_CONTINUOUS_ACTIVE = registerSound("block.geyser_continuous.active");
    public static final RegistrySupplier<SoundEvent> GEYSER_ERUPTION_START = registerSound("block.geyser_eruption.start");
    public static final RegistrySupplier<SoundEvent> GEYSER_ERUPTION_ACTIVE = registerSound("block.geyser_eruption.active");
    public static final RegistrySupplier<SoundEvent> NOXIOUS_GAS = registerSound("block.noxious_gas");
    public static final RegistrySupplier<SoundEvent> SHELF_ACTIVATE = registerSound("block.shelf.activate");
    public static final RegistrySupplier<SoundEvent> SHELF_DEACTIVATE = registerSound("block.shelf.deactivate");
    public static final RegistrySupplier<SoundEvent> SHELF_PLACE_ITEM = registerSound("block.shelf.place_item");
    public static final RegistrySupplier<SoundEvent> SHELF_TAKE_ITEM = registerSound("block.shelf.take_item");
    public static final RegistrySupplier<SoundEvent> SHELF_SINGLE_SWAP = registerSound("block.shelf.single_swap");
    public static final RegistrySupplier<SoundEvent> SHELF_MULTI_SWAP = registerSound("block.shelf.multi_swap");
    public static final RegistrySupplier<SoundEvent> MUSIC_DISC_CELEBRATION = registerSound("music_disc_celebration");
    public static final RegistrySupplier<SoundEvent> MUSIC_DISC_SNOWFALL = registerSound("music_disc_snowfall");
    public static final RegistrySupplier<SoundEvent> MUSIC_DISC_BUILDER = registerSound("music_disc_builder");

    public static SoundType COPPER_GRATE_SOUNDS() {
        return soundType(1f, 1f, sound(COPPER_GRATE_BREAK, SoundEvents.COPPER_BREAK),
                sound(COPPER_GRATE_STEP, SoundEvents.COPPER_STEP), sound(COPPER_GRATE_PLACE, SoundEvents.COPPER_STEP),
                sound(COPPER_GRATE_HIT, SoundEvents.COPPER_STEP), sound(COPPER_GRATE_FALL, SoundEvents.COPPER_STEP));
    }

    public static SoundType COPPER_BULB_SOUNDS() {
        return soundType(1f, 1f, sound(COPPER_BULB_BREAK, SoundEvents.COPPER_BREAK),
                sound(COPPER_BULB_STEP, SoundEvents.COPPER_STEP), sound(COPPER_BULB_PLACE, SoundEvents.COPPER_STEP),
                sound(COPPER_BULB_HIT, SoundEvents.COPPER_STEP), sound(COPPER_BULB_FALL, SoundEvents.COPPER_STEP));
    }

    public static SoundType DECORATED_POT_SOUNDS() {
        return soundType(1f, 1f, sound(DECORATED_POT_BREAK, SoundEvents.STONE_BREAK),
                sound(DECORATED_POT_STEP, SoundEvents.STONE_STEP), sound(DECORATED_POT_PLACE, SoundEvents.STONE_PLACE),
                sound(DECORATED_POT_HIT, SoundEvents.STONE_HIT), sound(DECORATED_POT_FALL, SoundEvents.STONE_FALL));
    }

    public static CustomSoundType MUD_SOUNDS() {
        return custom(1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f,
                sound(MUD_BREAK, SoundEvents.GRAVEL_BREAK), sound(MUD_STEP, SoundEvents.GRAVEL_STEP),
                sound(MUD_PLACE, SoundEvents.GRAVEL_PLACE), sound(MUD_HIT, SoundEvents.GRAVEL_HIT),
                sound(MUD_FALL, SoundEvents.GRAVEL_FALL));
    }

    public static CustomSoundType PACKED_MUD_SOUNDS() {
        return custom(.3f, 1f, 1f, .95f, .3f, 1f, 1f, 1f, 1f, 1f,
                sound(PACKED_MUD_BREAK, SoundEvents.GRAVEL_BREAK), sound(PACKED_MUD_STEP, SoundEvents.GRAVEL_STEP),
                sound(PACKED_MUD_PLACE, SoundEvents.GRAVEL_PLACE), sound(PACKED_MUD_HIT, SoundEvents.GRAVEL_HIT),
                sound(PACKED_MUD_FALL, SoundEvents.GRAVEL_FALL));
    }

    public static CustomSoundType MUD_BRICKS_SOUNDS() {
        return custom(1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f,
                sound(MUD_BRICKS_BREAK, SoundEvents.STONE_BREAK), sound(MUD_BRICKS_STEP, SoundEvents.STONE_STEP),
                sound(MUD_BRICKS_PLACE, SoundEvents.STONE_PLACE), sound(MUD_BRICKS_HIT, SoundEvents.STONE_HIT),
                sound(MUD_BRICKS_FALL, SoundEvents.STONE_FALL));
    }

    public static CustomSoundType PETAL_CLOVER_SOUNDS() {
        return custom(.8f, .96f, .12f, 1.2f, .8f, .96f, .2f, .6f, .4f, .9f,
                SoundEvents.FLOWERING_AZALEA_BREAK, SoundEvents.FLOWERING_AZALEA_STEP,
                SoundEvents.FLOWERING_AZALEA_PLACE, SoundEvents.FLOWERING_AZALEA_HIT,
                SoundEvents.FLOWERING_AZALEA_FALL);
    }

    public static CustomSoundType FLOWER_BED_SOUNDS() {
        return custom(.8f, .96f, .25f, 1.2f, .8f, .96f, .2f, .6f, .4f, .9f,
                sound(FLOWER_BED_BREAK, SoundEvents.FLOWERING_AZALEA_BREAK),
                sound(FLOWER_BED_STEP, SoundEvents.FLOWERING_AZALEA_STEP),
                sound(FLOWER_BED_PLACE, SoundEvents.FLOWERING_AZALEA_PLACE),
                sound(FLOWER_BED_HIT, SoundEvents.FLOWERING_AZALEA_HIT),
                sound(FLOWER_BED_FALL, SoundEvents.FLOWERING_AZALEA_FALL));
    }

    public static CustomSoundType VINE_SOUNDS() {
        return custom(.9f, .8f, .15f, 1f, .9f, .8f, .25f, .5f, .5f, .75f,
                SoundEvents.VINE_BREAK, SoundEvents.VINE_STEP, SoundEvents.VINE_PLACE,
                SoundEvents.VINE_HIT, SoundEvents.VINE_FALL);
    }

    public static CustomSoundType AZALEA_SOUNDS() {
        return custom(1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f,
                SoundEvents.AZALEA_BREAK, SoundEvents.AZALEA_STEP, SoundEvents.AZALEA_PLACE,
                SoundEvents.AZALEA_HIT, SoundEvents.AZALEA_FALL);
    }

    public static SoundType MANGROVE_ROOTS_SOUNDS() {
        return soundType(1f, 1f, sound(MANGROVE_ROOTS_BREAK, SoundEvents.WOOD_BREAK),
                sound(MANGROVE_ROOTS_STEP, SoundEvents.WOOD_STEP), sound(MANGROVE_ROOTS_PLACE, SoundEvents.WOOD_PLACE),
                sound(MANGROVE_ROOTS_HIT, SoundEvents.WOOD_HIT), sound(MANGROVE_ROOTS_FALL, SoundEvents.WOOD_FALL));
    }

    public static SoundType MUDDY_MANGROVE_ROOTS_SOUNDS() {
        return soundType(1f, 1f, sound(MUDDY_MANGROVE_ROOTS_BREAK, SoundEvents.WOOD_BREAK),
                sound(MUDDY_MANGROVE_ROOTS_STEP, SoundEvents.WOOD_STEP),
                sound(MUDDY_MANGROVE_ROOTS_PLACE, SoundEvents.WOOD_PLACE),
                sound(MUDDY_MANGROVE_ROOTS_HIT, SoundEvents.WOOD_HIT),
                sound(MUDDY_MANGROVE_ROOTS_FALL, SoundEvents.WOOD_FALL));
    }

    private static SoundEvent sound(RegistrySupplier<SoundEvent> supplier, SoundEvent fallback) {
        try {
            SoundEvent value = supplier == null ? null : supplier.get();
            return value == null ? fallback : value;
        } catch (Throwable ignored) {
            return fallback;
        }
    }

    private static SoundType soundType(float volume, float pitch, SoundEvent breakSound, SoundEvent stepSound,
                                       SoundEvent placeSound, SoundEvent hitSound, SoundEvent fallSound) {
        return new SoundType(volume, pitch, breakSound, stepSound, placeSound, hitSound, fallSound);
    }

    private static CustomSoundType custom(float breakVolume, float breakPitch, float stepVolume, float stepPitch,
                                          float placeVolume, float placePitch, float hitVolume, float hitPitch,
                                          float fallVolume, float fallPitch, SoundEvent breakSound, SoundEvent stepSound,
                                          SoundEvent placeSound, SoundEvent hitSound, SoundEvent fallSound) {
        return new CustomSoundType(breakVolume, breakPitch, stepVolume, stepPitch, placeVolume, placePitch,
                hitVolume, hitPitch, fallVolume, fallPitch, breakSound, stepSound, placeSound, hitSound, fallSound);
    }
    private static RegistrySupplier<SoundEvent> registerSound(String path) {
        String sanitized = path.replace('.', '_');
        return Services.REGISTRY.registerSound(sanitized, () -> Services.PLATFORM.createSoundEvent(new CommonId(BuildscapeCommon.MOD_ID, sanitized)));
    }
    public static void init() {
    }
}
