package com.kingodogo.buildscape.registry;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.CustomSoundType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

/**
 * Deferred register for the mod's sound events. Registered names have dots replaced by underscores; the matching entries
 * in sounds.json use the same names.
 */
public class ModSounds {

    private static final Logger LOGGER = BuildScape.LOGGER;

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister
            .create(ForgeRegistries.SOUND_EVENTS, BuildScape.MODID);

    public static final RegistryObject<SoundEvent> SCULK_CATALYST_BLOOM = SOUND_EVENTS.register(
            "block.sculk_catalyst.bloom",
            () -> new SoundEvent(new ResourceLocation(BuildScape.MODID, "block.sculk_catalyst.bloom")));
    public static final RegistryObject<SoundEvent> SCULK_SPREAD = SOUND_EVENTS.register("block.sculk.spread",
            () -> new SoundEvent(new ResourceLocation(BuildScape.MODID, "block.sculk.spread")));
    public static final RegistryObject<SoundEvent> SCULK_CHARGE = SOUND_EVENTS.register("block.sculk.charge",
            () -> new SoundEvent(new ResourceLocation(BuildScape.MODID, "block.sculk.charge")));

    public static final RegistryObject<SoundEvent> COPPER_GRATE_BREAK = registerSoundEvent("block.copper_grate.break");
    public static final RegistryObject<SoundEvent> COPPER_GRATE_STEP = registerSoundEvent("block.copper_grate.step");

    public static final RegistryObject<SoundEvent> COPPER_GRATE_PLACE = COPPER_GRATE_STEP;
    public static final RegistryObject<SoundEvent> COPPER_GRATE_HIT = COPPER_GRATE_STEP;
    public static final RegistryObject<SoundEvent> COPPER_GRATE_FALL = COPPER_GRATE_STEP;

    public static final RegistryObject<SoundEvent> COPPER_BULB_BREAK = registerSoundEvent("block.copper_bulb.break");
    public static final RegistryObject<SoundEvent> COPPER_BULB_PLACE = registerSoundEvent("block.copper_bulb.place");
    public static final RegistryObject<SoundEvent> COPPER_BULB_STEP = registerSoundEvent("block.copper_bulb.step");
    public static final RegistryObject<SoundEvent> COPPER_BULB_TOGGLE = registerSoundEvent("block.copper_bulb.toggle");

    public static final RegistryObject<SoundEvent> COPPER_BULB_HIT = COPPER_BULB_STEP;
    public static final RegistryObject<SoundEvent> COPPER_BULB_FALL = COPPER_BULB_STEP;

    public static final RegistryObject<SoundEvent> MUD_BREAK = registerSoundEvent(
            "block.mud.break");
    public static final RegistryObject<SoundEvent> MUD_STEP = registerSoundEvent(
            "block.mud.step");

    public static final RegistryObject<SoundEvent> MUD_PLACE = MUD_STEP;
    public static final RegistryObject<SoundEvent> MUD_HIT = MUD_STEP;
    public static final RegistryObject<SoundEvent> MUD_FALL = MUD_STEP;

    public static final RegistryObject<SoundEvent> PACKED_MUD_BREAK = registerSoundEvent("block.packed_mud.break");
    public static final RegistryObject<SoundEvent> PACKED_MUD_STEP = registerSoundEvent("block.packed_mud.step");
    public static final RegistryObject<SoundEvent> PACKED_MUD_PLACE = registerSoundEvent("block.packed_mud.place");
    public static final RegistryObject<SoundEvent> PACKED_MUD_HIT = registerSoundEvent("block.packed_mud.hit");
    public static final RegistryObject<SoundEvent> PACKED_MUD_FALL = registerSoundEvent("block.packed_mud.fall");

    public static final RegistryObject<SoundEvent> MUD_BRICKS_BREAK = registerSoundEvent("block.mud_bricks.break");
    public static final RegistryObject<SoundEvent> MUD_BRICKS_STEP = registerSoundEvent("block.mud_bricks.step");
    public static final RegistryObject<SoundEvent> MUD_BRICKS_PLACE = registerSoundEvent("block.mud_bricks.place");
    public static final RegistryObject<SoundEvent> MUD_BRICKS_HIT = registerSoundEvent("block.mud_bricks.hit");
    public static final RegistryObject<SoundEvent> MUD_BRICKS_FALL = registerSoundEvent("block.mud_bricks.fall");

    public static final RegistryObject<SoundEvent> DECORATED_POT_PLACE = registerSoundEvent(
            "block.decorated_pot.place");
    public static final RegistryObject<SoundEvent> DECORATED_POT_BREAK = registerSoundEvent(
            "block.decorated_pot.break");
    public static final RegistryObject<SoundEvent> DECORATED_POT_HIT = registerSoundEvent("block.decorated_pot.hit");
    public static final RegistryObject<SoundEvent> DECORATED_POT_STEP = registerSoundEvent("block.decorated_pot.step");
    public static final RegistryObject<SoundEvent> DECORATED_POT_FALL = registerSoundEvent("block.decorated_pot.fall");
    public static final RegistryObject<SoundEvent> DECORATED_POT_INSERT_ITEM = registerSoundEvent(
            "block.decorated_pot.insert_item");
    public static final RegistryObject<SoundEvent> DECORATED_POT_INSERT_FAIL = registerSoundEvent(
            "block.decorated_pot.insert_fail");
    public static final RegistryObject<SoundEvent> DECORATED_POT_SHATTER = registerSoundEvent(
            "block.decorated_pot.shatter");

    public static final RegistryObject<SoundEvent> FLOWER_BED_BREAK = registerSoundEvent("block.flower_bed.break");
    public static final RegistryObject<SoundEvent> FLOWER_BED_STEP = registerSoundEvent("block.flower_bed.step");
    public static final RegistryObject<SoundEvent> FLOWER_BED_PLACE = registerSoundEvent("block.flower_bed.place");
    public static final RegistryObject<SoundEvent> FLOWER_BED_HIT = registerSoundEvent("block.flower_bed.hit");
    public static final RegistryObject<SoundEvent> FLOWER_BED_FALL = registerSoundEvent("block.flower_bed.fall");

    public static final RegistryObject<SoundEvent> MANGROVE_ROOTS_BREAK = registerSoundEvent(
            "block.mangrove_roots.break");
    public static final RegistryObject<SoundEvent> MANGROVE_ROOTS_STEP = registerSoundEvent(
            "block.mangrove_roots.step");
    public static final RegistryObject<SoundEvent> MANGROVE_ROOTS_PLACE = registerSoundEvent(
            "block.mangrove_roots.place");
    public static final RegistryObject<SoundEvent> MANGROVE_ROOTS_HIT = registerSoundEvent("block.mangrove_roots.hit");
    public static final RegistryObject<SoundEvent> MANGROVE_ROOTS_FALL = registerSoundEvent(
            "block.mangrove_roots.fall");

    public static final RegistryObject<SoundEvent> MUDDY_MANGROVE_ROOTS_BREAK = registerSoundEvent(
            "block.muddy_mangrove_roots.break");
    public static final RegistryObject<SoundEvent> MUDDY_MANGROVE_ROOTS_STEP = registerSoundEvent(
            "block.muddy_mangrove_roots.step");
    public static final RegistryObject<SoundEvent> MUDDY_MANGROVE_ROOTS_PLACE = registerSoundEvent(
            "block.muddy_mangrove_roots.place");
    public static final RegistryObject<SoundEvent> MUDDY_MANGROVE_ROOTS_HIT = registerSoundEvent(
            "block.muddy_mangrove_roots.hit");
    public static final RegistryObject<SoundEvent> MUDDY_MANGROVE_ROOTS_FALL = registerSoundEvent(
            "block.muddy_mangrove_roots.fall");

    public static final RegistryObject<SoundEvent> GEYSER_CONTINUOUS_START = registerSoundEvent(
            "block.geyser_continuous.start");
    public static final RegistryObject<SoundEvent> GEYSER_CONTINUOUS_ACTIVE = registerSoundEvent(
            "block.geyser_continuous.active");
    public static final RegistryObject<SoundEvent> GEYSER_ERUPTION_START = registerSoundEvent(
            "block.geyser_eruption.start");
    public static final RegistryObject<SoundEvent> GEYSER_ERUPTION_ACTIVE = registerSoundEvent(
            "block.geyser_eruption.active");
    public static final RegistryObject<SoundEvent> NOXIOUS_GAS = registerSoundEvent("block.noxious_gas");

    public static final RegistryObject<SoundEvent> SHELF_ACTIVATE = registerSoundEvent("block.shelf.activate");
    public static final RegistryObject<SoundEvent> SHELF_DEACTIVATE = registerSoundEvent("block.shelf.deactivate");
    public static final RegistryObject<SoundEvent> SHELF_PLACE_ITEM = registerSoundEvent("block.shelf.place_item");
    public static final RegistryObject<SoundEvent> SHELF_TAKE_ITEM = registerSoundEvent("block.shelf.take_item");
    public static final RegistryObject<SoundEvent> SHELF_SINGLE_SWAP = registerSoundEvent("block.shelf.single_swap");
    public static final RegistryObject<SoundEvent> SHELF_MULTI_SWAP = registerSoundEvent("block.shelf.multi_swap");

    private static ForgeSoundType copperGrateSounds = null;
    private static ForgeSoundType copperBulbSounds = null;
    private static ForgeSoundType mangroveRootsSounds = null;
    private static ForgeSoundType muddyMangroveRootsSounds = null;

    public static ForgeSoundType COPPER_GRATE_SOUNDS() {
        if (copperGrateSounds == null) {
            try {
                Supplier<SoundEvent> breakSound = () -> {
                    try {
                        if (COPPER_GRATE_BREAK != null && COPPER_GRATE_BREAK.isPresent()) {
                            return COPPER_GRATE_BREAK.get();
                        }
                    } catch (RuntimeException ignored) {
                        // RegistryObject not bound yet; use the vanilla fallback sound
                    }
                    return SoundEvents.COPPER_BREAK;
                };
                Supplier<SoundEvent> stepSound = () -> {
                    try {
                        if (COPPER_GRATE_STEP != null && COPPER_GRATE_STEP.isPresent()) {
                            return COPPER_GRATE_STEP.get();
                        }
                    } catch (RuntimeException ignored) {
                        // RegistryObject not bound yet; use the vanilla fallback sound
                    }
                    return SoundEvents.COPPER_STEP;
                };

                copperGrateSounds = new ForgeSoundType(
                        1f,
                        1f,
                        breakSound,
                        stepSound,
                        stepSound,
                        stepSound,
                        stepSound);
            } catch (Exception e) {
                LOGGER.error("Failed to create copper grate sound type", e);
                return new ForgeSoundType(
                        1f,
                        1f,
                        () -> SoundEvents.COPPER_BREAK,
                        () -> SoundEvents.COPPER_STEP,
                        () -> SoundEvents.COPPER_STEP,
                        () -> SoundEvents.COPPER_STEP,
                        () -> SoundEvents.COPPER_STEP);
            }
        }
        return copperGrateSounds;
    }

    public static ForgeSoundType COPPER_BULB_SOUNDS() {
        if (copperBulbSounds == null) {
            try {
                Supplier<SoundEvent> breakSound = () -> {
                    try {
                        if (COPPER_BULB_BREAK != null && COPPER_BULB_BREAK.isPresent()) {
                            return COPPER_BULB_BREAK.get();
                        }
                    } catch (RuntimeException ignored) {
                        // RegistryObject not bound yet; use the vanilla fallback sound
                    }
                    return SoundEvents.COPPER_BREAK;
                };
                Supplier<SoundEvent> stepSound = () -> {
                    try {
                        if (COPPER_BULB_STEP != null && COPPER_BULB_STEP.isPresent()) {
                            return COPPER_BULB_STEP.get();
                        }
                    } catch (RuntimeException ignored) {
                        // RegistryObject not bound yet; use the vanilla fallback sound
                    }
                    return SoundEvents.COPPER_STEP;
                };
                Supplier<SoundEvent> placeSound = () -> {
                    try {
                        if (COPPER_BULB_PLACE != null && COPPER_BULB_PLACE.isPresent()) {
                            return COPPER_BULB_PLACE.get();
                        }
                    } catch (RuntimeException ignored) {
                        // RegistryObject not bound yet; use the vanilla fallback sound
                    }
                    return SoundEvents.COPPER_STEP;
                };

                copperBulbSounds = new ForgeSoundType(
                        1f,
                        1f,
                        breakSound,
                        stepSound,
                        placeSound,
                        stepSound,
                        stepSound);
            } catch (Exception e) {
                LOGGER.error("Failed to create copper bulb sound type", e);
                return new ForgeSoundType(
                        1f,
                        1f,
                        () -> SoundEvents.COPPER_BREAK,
                        () -> SoundEvents.COPPER_STEP,
                        () -> SoundEvents.COPPER_STEP,
                        () -> SoundEvents.COPPER_STEP,
                        () -> SoundEvents.COPPER_STEP);
            }
        }
        return copperBulbSounds;
    }

    public static CustomSoundType MUD_SOUNDS() {
        return new CustomSoundType(
                1.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 1.0f,
                () -> MUD_BREAK.isPresent() ? MUD_BREAK.get() : SoundEvents.GRAVEL_BREAK,
                () -> MUD_STEP.isPresent() ? MUD_STEP.get() : SoundEvents.GRAVEL_STEP,
                () -> MUD_STEP.isPresent() ? MUD_STEP.get() : SoundEvents.GRAVEL_PLACE,
                () -> MUD_STEP.isPresent() ? MUD_STEP.get() : SoundEvents.GRAVEL_HIT,
                () -> MUD_STEP.isPresent() ? MUD_STEP.get() : SoundEvents.GRAVEL_FALL);
    }

    public static CustomSoundType PACKED_MUD_SOUNDS() {
        return new CustomSoundType(
                0.3f, 1.0f,
                1.0f, 0.95f,
                0.3f, 1.0f,
                1.0f, 1.0f,
                1.0f, 1.0f,
                () -> PACKED_MUD_BREAK.isPresent() ? PACKED_MUD_BREAK.get() : SoundEvents.GRAVEL_BREAK,
                () -> PACKED_MUD_STEP.isPresent() ? PACKED_MUD_STEP.get() : SoundEvents.GRAVEL_STEP,
                () -> PACKED_MUD_PLACE.isPresent() ? PACKED_MUD_PLACE.get() : SoundEvents.GRAVEL_PLACE,
                () -> PACKED_MUD_HIT.isPresent() ? PACKED_MUD_HIT.get() : SoundEvents.GRAVEL_HIT,
                () -> PACKED_MUD_FALL.isPresent() ? PACKED_MUD_FALL.get() : SoundEvents.GRAVEL_FALL);
    }

    public static CustomSoundType MUD_BRICKS_SOUNDS() {
        return new CustomSoundType(
                1.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 1.0f,
                () -> MUD_BRICKS_BREAK.isPresent() ? MUD_BRICKS_BREAK.get() : SoundEvents.STONE_BREAK,
                () -> MUD_BRICKS_STEP.isPresent() ? MUD_BRICKS_STEP.get() : SoundEvents.STONE_STEP,
                () -> MUD_BRICKS_PLACE.isPresent() ? MUD_BRICKS_PLACE.get() : SoundEvents.STONE_PLACE,
                () -> MUD_BRICKS_HIT.isPresent() ? MUD_BRICKS_HIT.get() : SoundEvents.STONE_HIT,
                () -> MUD_BRICKS_FALL.isPresent() ? MUD_BRICKS_FALL.get() : SoundEvents.STONE_FALL);
    }

    public static CustomSoundType PETAL_CLOVER_SOUNDS() {
        return new CustomSoundType(
                0.8f,
                0.96f,
                0.12f,
                1.2f,
                0.8f,
                0.96f,
                0.2f,
                0.6f,
                0.4f,
                0.9f,
                () -> SoundEvents.FLOWERING_AZALEA_BREAK,
                () -> SoundEvents.FLOWERING_AZALEA_STEP,
                () -> SoundEvents.FLOWERING_AZALEA_PLACE,
                () -> SoundEvents.FLOWERING_AZALEA_HIT,
                () -> SoundEvents.FLOWERING_AZALEA_FALL);
    }

    public static CustomSoundType FLOWER_BED_SOUNDS() {
        return new CustomSoundType(
                0.8f,
                0.96f,
                0.25f,
                1.2f,
                0.8f,
                0.96f,
                0.2f,
                0.6f,
                0.4f,
                0.9f,
                () -> {
                    if (FLOWER_BED_BREAK.isPresent()) {
                        return FLOWER_BED_BREAK.get();
                    }
                    return SoundEvents.FLOWERING_AZALEA_BREAK;
                },
                () -> {
                    if (FLOWER_BED_STEP.isPresent()) {
                        return FLOWER_BED_STEP.get();
                    }
                    return SoundEvents.FLOWERING_AZALEA_STEP;
                },
                () -> {
                    if (FLOWER_BED_PLACE.isPresent()) {
                        return FLOWER_BED_PLACE.get();
                    }
                    return SoundEvents.FLOWERING_AZALEA_PLACE;
                },
                () -> {
                    if (FLOWER_BED_HIT.isPresent()) {
                        return FLOWER_BED_HIT.get();
                    }
                    return SoundEvents.FLOWERING_AZALEA_HIT;
                },
                () -> {
                    if (FLOWER_BED_FALL.isPresent()) {
                        return FLOWER_BED_FALL.get();
                    }
                    return SoundEvents.FLOWERING_AZALEA_FALL;
                });
    }

    public static CustomSoundType VINE_SOUNDS() {
        return new CustomSoundType(
                0.9f,
                0.8f,
                0.15f,
                1.0f,
                0.9f,
                0.8f,
                0.25f,
                0.5f,
                0.5f,
                0.75f,
                () -> SoundEvents.VINE_BREAK,
                () -> SoundEvents.VINE_STEP,
                () -> SoundEvents.VINE_PLACE,
                () -> SoundEvents.VINE_HIT,
                () -> SoundEvents.VINE_FALL);
    }

    public static CustomSoundType AZALEA_SOUNDS() {
        return new CustomSoundType(
                1.0f,
                1.0f,
                1.0f,
                1.0f,
                1.0f,
                1.0f,
                1.0f,
                1.0f,
                1.0f,
                1.0f,
                () -> SoundEvents.AZALEA_BREAK,
                () -> SoundEvents.AZALEA_STEP,
                () -> SoundEvents.AZALEA_PLACE,
                () -> SoundEvents.AZALEA_HIT,
                () -> SoundEvents.AZALEA_FALL);
    }

    public static ForgeSoundType MANGROVE_ROOTS_SOUNDS() {
        if (mangroveRootsSounds == null) {
            try {
                Supplier<SoundEvent> breakSound = () -> {
                    if (MANGROVE_ROOTS_BREAK.isPresent()) {
                        return MANGROVE_ROOTS_BREAK.get();
                    }
                    return SoundEvents.WOOD_BREAK;
                };
                Supplier<SoundEvent> stepSound = () -> {
                    if (MANGROVE_ROOTS_STEP.isPresent()) {
                        return MANGROVE_ROOTS_STEP.get();
                    }
                    return SoundEvents.WOOD_STEP;
                };
                Supplier<SoundEvent> placeSound = () -> {
                    if (MANGROVE_ROOTS_PLACE.isPresent()) {
                        return MANGROVE_ROOTS_PLACE.get();
                    }
                    return SoundEvents.WOOD_PLACE;
                };
                Supplier<SoundEvent> hitSound = () -> {
                    if (MANGROVE_ROOTS_HIT.isPresent()) {
                        return MANGROVE_ROOTS_HIT.get();
                    }
                    return SoundEvents.WOOD_HIT;
                };
                Supplier<SoundEvent> fallSound = () -> {
                    if (MANGROVE_ROOTS_FALL.isPresent()) {
                        return MANGROVE_ROOTS_FALL.get();
                    }
                    return SoundEvents.WOOD_FALL;
                };

                mangroveRootsSounds = new ForgeSoundType(
                        1f,
                        1f,
                        breakSound,
                        stepSound,
                        placeSound,
                        hitSound,
                        fallSound);
            } catch (Exception e) {
                LOGGER.error("Failed to create mangrove roots sound type", e);
                return new ForgeSoundType(
                        1f,
                        1f,
                        () -> SoundEvents.WOOD_BREAK,
                        () -> SoundEvents.WOOD_STEP,
                        () -> SoundEvents.WOOD_STEP,
                        () -> SoundEvents.WOOD_STEP,
                        () -> SoundEvents.WOOD_STEP);
            }
        }
        return mangroveRootsSounds;
    }

    public static ForgeSoundType MUDDY_MANGROVE_ROOTS_SOUNDS() {
        if (muddyMangroveRootsSounds == null) {
            try {
                Supplier<SoundEvent> breakSound = () -> {
                    if (MUDDY_MANGROVE_ROOTS_BREAK.isPresent()) {
                        return MUDDY_MANGROVE_ROOTS_BREAK.get();
                    }
                    return SoundEvents.WOOD_BREAK;
                };
                Supplier<SoundEvent> stepSound = () -> {
                    if (MUDDY_MANGROVE_ROOTS_STEP.isPresent()) {
                        return MUDDY_MANGROVE_ROOTS_STEP.get();
                    }
                    return SoundEvents.WOOD_STEP;
                };
                Supplier<SoundEvent> placeSound = () -> {
                    if (MUDDY_MANGROVE_ROOTS_PLACE.isPresent()) {
                        return MUDDY_MANGROVE_ROOTS_PLACE.get();
                    }
                    return SoundEvents.WOOD_PLACE;
                };
                Supplier<SoundEvent> hitSound = () -> {
                    if (MUDDY_MANGROVE_ROOTS_HIT.isPresent()) {
                        return MUDDY_MANGROVE_ROOTS_HIT.get();
                    }
                    return SoundEvents.WOOD_HIT;
                };
                Supplier<SoundEvent> fallSound = () -> {
                    if (MUDDY_MANGROVE_ROOTS_FALL.isPresent()) {
                        return MUDDY_MANGROVE_ROOTS_FALL.get();
                    }
                    return SoundEvents.WOOD_FALL;
                };

                muddyMangroveRootsSounds = new ForgeSoundType(
                        1f,
                        1f,
                        breakSound,
                        stepSound,
                        placeSound,
                        hitSound,
                        fallSound);
            } catch (Exception e) {
                LOGGER.error("Failed to create muddy mangrove roots sound type", e);
                return new ForgeSoundType(
                        1f,
                        1f,
                        () -> SoundEvents.WOOD_BREAK,
                        () -> SoundEvents.WOOD_STEP,
                        () -> SoundEvents.WOOD_STEP,
                        () -> SoundEvents.WOOD_STEP,
                        () -> SoundEvents.WOOD_STEP);
            }
        }
        return muddyMangroveRootsSounds;
    }

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        String eventName = name.replace('.', '_');
        return SOUND_EVENTS.register(eventName,
                () -> new SoundEvent(new ResourceLocation(BuildScape.MODID, eventName)));
    }

    public static final RegistryObject<SoundEvent> MUSIC_DISC_CELEBRATION = registerSoundEvent(
            "music_disc_celebration");
    public static final RegistryObject<SoundEvent> MUSIC_DISC_SNOWFALL = registerSoundEvent("music_disc_snowfall");
    public static final RegistryObject<SoundEvent> MUSIC_DISC_BUILDER = registerSoundEvent("music_disc_builder");

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
