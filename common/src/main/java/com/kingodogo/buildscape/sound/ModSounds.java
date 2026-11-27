package com.kingodogo.buildscape.sound;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.registry.RegistrySupplier;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import com.kingodogo.buildscape.block.CustomSoundType;

import com.kingodogo.buildscape.block.CustomSoundType;
public class ModSounds {
    // The vanilla 1.19 sculk sounds exist only for the 1.18.2 sculk backport.
    public static final RegistrySupplier<SoundEvent> SCULK_CATALYST_BLOOM = registerLegacySound("block.sculk_catalyst.bloom");
    public static final RegistrySupplier<SoundEvent> SCULK_SPREAD = registerLegacySound("block.sculk.spread");
    public static final RegistrySupplier<SoundEvent> SCULK_CHARGE = registerLegacySound("block.sculk.charge");
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

    public static SoundEvent getDiscSound(String id) {
        return switch (id) {
            case "music_disc_celebration" -> MUSIC_DISC_CELEBRATION.get();
            case "music_disc_snowfall" -> MUSIC_DISC_SNOWFALL.get();
            case "music_disc_builder" -> MUSIC_DISC_BUILDER.get();
            default -> null;
        };
    }

    public static CustomSoundType COPPER_GRATE_SOUNDS() {
        return customSoundType(1f, 1f, () -> sound(COPPER_GRATE_BREAK, CustomSoundType.COPPER.getBreakSound()),
                () -> sound(COPPER_GRATE_STEP, CustomSoundType.COPPER.getStepSound()), () -> sound(COPPER_GRATE_PLACE, CustomSoundType.COPPER.getStepSound()),
                () -> sound(COPPER_GRATE_HIT, CustomSoundType.COPPER.getStepSound()), () -> sound(COPPER_GRATE_FALL, CustomSoundType.COPPER.getStepSound()));
    }

    public static CustomSoundType COPPER_BULB_SOUNDS() {
        return customSoundType(1f, 1f, () -> sound(COPPER_BULB_BREAK, CustomSoundType.COPPER.getBreakSound()),
                () -> sound(COPPER_BULB_STEP, CustomSoundType.COPPER.getStepSound()), () -> sound(COPPER_BULB_PLACE, CustomSoundType.COPPER.getStepSound()),
                () -> sound(COPPER_BULB_HIT, CustomSoundType.COPPER.getStepSound()), () -> sound(COPPER_BULB_FALL, CustomSoundType.COPPER.getStepSound()));
    }

    public static CustomSoundType DECORATED_POT_SOUNDS() {
        return customSoundType(1f, 1f, () -> sound(DECORATED_POT_BREAK, CustomSoundType.STONE.getBreakSound()),
                () -> sound(DECORATED_POT_STEP, CustomSoundType.STONE.getStepSound()), () -> sound(DECORATED_POT_PLACE, CustomSoundType.STONE.getPlaceSound()),
                () -> sound(DECORATED_POT_HIT, CustomSoundType.STONE.getHitSound()), () -> sound(DECORATED_POT_FALL, CustomSoundType.STONE.getFallSound()));
    }

    public static CustomSoundType MUD_SOUNDS() {
        return custom(1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f,
                () -> sound(MUD_BREAK, CustomSoundType.GRAVEL.getBreakSound()), () -> sound(MUD_STEP, CustomSoundType.GRAVEL.getStepSound()),
                () -> sound(MUD_PLACE, CustomSoundType.GRAVEL.getPlaceSound()), () -> sound(MUD_HIT, CustomSoundType.GRAVEL.getHitSound()),
                () -> sound(MUD_FALL, CustomSoundType.GRAVEL.getFallSound()));
    }

    public static CustomSoundType PACKED_MUD_SOUNDS() {
        return custom(.3f, 1f, 1f, .95f, .3f, 1f, 1f, 1f, 1f, 1f,
                () -> sound(PACKED_MUD_BREAK, CustomSoundType.GRAVEL.getBreakSound()), () -> sound(PACKED_MUD_STEP, CustomSoundType.GRAVEL.getStepSound()),
                () -> sound(PACKED_MUD_PLACE, CustomSoundType.GRAVEL.getPlaceSound()), () -> sound(PACKED_MUD_HIT, CustomSoundType.GRAVEL.getHitSound()),
                () -> sound(PACKED_MUD_FALL, CustomSoundType.GRAVEL.getFallSound()));
    }

    public static CustomSoundType MUD_BRICKS_SOUNDS() {
        return custom(1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f,
                () -> sound(MUD_BRICKS_BREAK, CustomSoundType.STONE.getBreakSound()), () -> sound(MUD_BRICKS_STEP, CustomSoundType.STONE.getStepSound()),
                () -> sound(MUD_BRICKS_PLACE, CustomSoundType.STONE.getPlaceSound()), () -> sound(MUD_BRICKS_HIT, CustomSoundType.STONE.getHitSound()),
                () -> sound(MUD_BRICKS_FALL, CustomSoundType.STONE.getFallSound()));
    }

    public static CustomSoundType PETAL_CLOVER_SOUNDS() {
        return custom(.8f, .96f, .12f, 1.2f, .8f, .96f, .2f, .6f, .4f, .9f,
                () -> CustomSoundType.FLOWERING_AZALEA.getBreakSound(), () -> CustomSoundType.FLOWERING_AZALEA.getStepSound(),
                () -> CustomSoundType.FLOWERING_AZALEA.getPlaceSound(), () -> CustomSoundType.FLOWERING_AZALEA.getHitSound(),
                () -> CustomSoundType.FLOWERING_AZALEA.getFallSound());
    }

    public static CustomSoundType FLOWER_BED_SOUNDS() {
        return custom(.8f, .96f, .25f, 1.2f, .8f, .96f, .2f, .6f, .4f, .9f,
                () -> sound(FLOWER_BED_BREAK, CustomSoundType.FLOWERING_AZALEA.getBreakSound()),
                () -> sound(FLOWER_BED_STEP, CustomSoundType.FLOWERING_AZALEA.getStepSound()),
                () -> sound(FLOWER_BED_PLACE, CustomSoundType.FLOWERING_AZALEA.getPlaceSound()),
                () -> sound(FLOWER_BED_HIT, CustomSoundType.FLOWERING_AZALEA.getHitSound()),
                () -> sound(FLOWER_BED_FALL, CustomSoundType.FLOWERING_AZALEA.getFallSound()));
    }

    public static CustomSoundType VINE_SOUNDS() {
        return custom(.9f, .8f, .15f, 1f, .9f, .8f, .25f, .5f, .5f, .75f,
                () -> CustomSoundType.VINE.getBreakSound(), () -> CustomSoundType.VINE.getStepSound(), () -> CustomSoundType.VINE.getPlaceSound(),
                () -> CustomSoundType.VINE.getHitSound(), () -> CustomSoundType.VINE.getFallSound());
    }

    public static CustomSoundType AZALEA_SOUNDS() {
        return custom(1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f,
                () -> CustomSoundType.AZALEA.getBreakSound(), () -> CustomSoundType.AZALEA.getStepSound(), () -> CustomSoundType.AZALEA.getPlaceSound(),
                () -> CustomSoundType.AZALEA.getHitSound(), () -> CustomSoundType.AZALEA.getFallSound());
    }

    public static CustomSoundType MANGROVE_ROOTS_SOUNDS() {
        return customSoundType(1f, 1f, () -> sound(MANGROVE_ROOTS_BREAK, CustomSoundType.WOOD.getBreakSound()),
                () -> sound(MANGROVE_ROOTS_STEP, CustomSoundType.WOOD.getStepSound()), () -> sound(MANGROVE_ROOTS_PLACE, CustomSoundType.WOOD.getPlaceSound()),
                () -> sound(MANGROVE_ROOTS_HIT, CustomSoundType.WOOD.getHitSound()), () -> sound(MANGROVE_ROOTS_FALL, CustomSoundType.WOOD.getFallSound()));
    }

    public static CustomSoundType MUDDY_MANGROVE_ROOTS_SOUNDS() {
        return customSoundType(1f, 1f, () -> sound(MUDDY_MANGROVE_ROOTS_BREAK, CustomSoundType.WOOD.getBreakSound()),
                () -> sound(MUDDY_MANGROVE_ROOTS_STEP, CustomSoundType.WOOD.getStepSound()),
                () -> sound(MUDDY_MANGROVE_ROOTS_PLACE, CustomSoundType.WOOD.getPlaceSound()),
                () -> sound(MUDDY_MANGROVE_ROOTS_HIT, CustomSoundType.WOOD.getHitSound()),
                () -> sound(MUDDY_MANGROVE_ROOTS_FALL, CustomSoundType.WOOD.getFallSound()));
    }

    private static SoundEvent sound(RegistrySupplier<SoundEvent> supplier, SoundEvent fallback) {
        try {
            SoundEvent value = supplier == null ? null : supplier.get();
            return value == null ? fallback : value;
        } catch (Throwable ignored) {
            return fallback;
        }
    }

    private static CustomSoundType customSoundType(float volume, float pitch,
                                       java.util.function.Supplier<SoundEvent> breakSound,
                                       java.util.function.Supplier<SoundEvent> stepSound,
                                       java.util.function.Supplier<SoundEvent> placeSound,
                                       java.util.function.Supplier<SoundEvent> hitSound,
                                       java.util.function.Supplier<SoundEvent> fallSound) {
        return custom(volume, pitch, volume, pitch, volume, pitch, volume, pitch, volume, pitch,
                breakSound, stepSound, placeSound, hitSound, fallSound);
    }

    private static CustomSoundType custom(float breakVolume, float breakPitch, float stepVolume, float stepPitch,
                                          float placeVolume, float placePitch, float hitVolume, float hitPitch,
                                          float fallVolume, float fallPitch,
                                          java.util.function.Supplier<SoundEvent> breakSound,
                                          java.util.function.Supplier<SoundEvent> stepSound,
                                          java.util.function.Supplier<SoundEvent> placeSound,
                                          java.util.function.Supplier<SoundEvent> hitSound,
                                          java.util.function.Supplier<SoundEvent> fallSound) {
        return new CustomSoundType(breakVolume, breakPitch, stepVolume, stepPitch, placeVolume, placePitch,
                hitVolume, hitPitch, fallVolume, fallPitch, breakSound.get(), stepSound.get(), placeSound.get(),
                hitSound.get(), fallSound.get());
    }
    private static RegistrySupplier<SoundEvent> registerSound(String path) {
        String sanitized = path.replace('.', '_');
        return Services.REGISTRY.registerSound(sanitized, () -> Services.PLATFORM.createSoundEvent(new CommonId(BuildscapeCommon.MOD_ID, sanitized)));
    }

    private static RegistrySupplier<SoundEvent> registerLegacySound(String path) {
        return Services.isVersionCluster("minecraft1182") ? registerSound(path) : null;
    }
    public static void init() {
    }
}

