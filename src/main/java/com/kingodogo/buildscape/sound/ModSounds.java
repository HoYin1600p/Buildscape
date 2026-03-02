package com.kingodogo.buildscape.sound;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class ModSounds {

    private static final Logger LOGGER = LogManager.getLogger();

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BuildScape.MODID);

    public static final RegistryObject<SoundEvent> COPPER_GRATE_BREAK =
            registerSoundEvent("block.copper_grate.break");
    public static final RegistryObject<SoundEvent> COPPER_GRATE_STEP =
            registerSoundEvent("block.copper_grate.step");

    public static final RegistryObject<SoundEvent> COPPER_GRATE_PLACE =
            COPPER_GRATE_STEP;
    public static final RegistryObject<SoundEvent> COPPER_GRATE_HIT =
            COPPER_GRATE_STEP;
    public static final RegistryObject<SoundEvent> COPPER_GRATE_FALL =
            COPPER_GRATE_STEP;

    public static final RegistryObject<SoundEvent> COPPER_BULB_BREAK =
            registerSoundEvent("block.copper_bulb.break");
    public static final RegistryObject<SoundEvent> COPPER_BULB_PLACE =
            registerSoundEvent("block.copper_bulb.place");
    public static final RegistryObject<SoundEvent> COPPER_BULB_STEP =
            registerSoundEvent("block.copper_bulb.step");
    public static final RegistryObject<SoundEvent> COPPER_BULB_TOGGLE =
            registerSoundEvent("block.copper_bulb.toggle");

    public static final RegistryObject<SoundEvent> COPPER_BULB_HIT =
            COPPER_BULB_STEP;
    public static final RegistryObject<SoundEvent> COPPER_BULB_FALL =
            COPPER_BULB_STEP;

    public static final RegistryObject<SoundEvent> MUD_BREAK = registerSoundEvent(
            "block.mud.break"
    );
    public static final RegistryObject<SoundEvent> MUD_STEP = registerSoundEvent(
            "block.mud.step"
    );

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

    public static final RegistryObject<SoundEvent> DECORATED_POT_PLACE =
            registerSoundEvent("block.decorated_pot.place");
    public static final RegistryObject<SoundEvent> DECORATED_POT_BREAK =
            registerSoundEvent("block.decorated_pot.break");
    public static final RegistryObject<SoundEvent> DECORATED_POT_HIT =
            registerSoundEvent("block.decorated_pot.hit");
    public static final RegistryObject<SoundEvent> DECORATED_POT_STEP =
            registerSoundEvent("block.decorated_pot.step");
    public static final RegistryObject<SoundEvent> DECORATED_POT_FALL =
            registerSoundEvent("block.decorated_pot.fall");
    public static final RegistryObject<SoundEvent> DECORATED_POT_INSERT_ITEM =
            registerSoundEvent("block.decorated_pot.insert_item");
    public static final RegistryObject<SoundEvent> DECORATED_POT_INSERT_FAIL =
            registerSoundEvent("block.decorated_pot.insert_fail");
    public static final RegistryObject<SoundEvent> DECORATED_POT_SHATTER =
            registerSoundEvent("block.decorated_pot.shatter");

    public static final RegistryObject<SoundEvent> FLOWER_BED_BREAK =
            registerSoundEvent("block.flower_bed.break");
    public static final RegistryObject<SoundEvent> FLOWER_BED_STEP =
            registerSoundEvent("block.flower_bed.step");
    public static final RegistryObject<SoundEvent> FLOWER_BED_PLACE =
            registerSoundEvent("block.flower_bed.place");
    public static final RegistryObject<SoundEvent> FLOWER_BED_HIT =
            registerSoundEvent("block.flower_bed.hit");
    public static final RegistryObject<SoundEvent> FLOWER_BED_FALL =
            registerSoundEvent("block.flower_bed.fall");

    public static final RegistryObject<SoundEvent> MANGROVE_ROOTS_BREAK =
            registerSoundEvent("block.mangrove_roots.break");
    public static final RegistryObject<SoundEvent> MANGROVE_ROOTS_STEP =
            registerSoundEvent("block.mangrove_roots.step");
    public static final RegistryObject<SoundEvent> MANGROVE_ROOTS_PLACE =
            registerSoundEvent("block.mangrove_roots.place");
    public static final RegistryObject<SoundEvent> MANGROVE_ROOTS_HIT =
            registerSoundEvent("block.mangrove_roots.hit");
    public static final RegistryObject<SoundEvent> MANGROVE_ROOTS_FALL =
            registerSoundEvent("block.mangrove_roots.fall");

    public static final RegistryObject<SoundEvent> MUDDY_MANGROVE_ROOTS_BREAK =
            registerSoundEvent("block.muddy_mangrove_roots.break");
    public static final RegistryObject<SoundEvent> MUDDY_MANGROVE_ROOTS_STEP =
            registerSoundEvent("block.muddy_mangrove_roots.step");
    public static final RegistryObject<SoundEvent> MUDDY_MANGROVE_ROOTS_PLACE =
            registerSoundEvent("block.muddy_mangrove_roots.place");
    public static final RegistryObject<SoundEvent> MUDDY_MANGROVE_ROOTS_HIT =
            registerSoundEvent("block.muddy_mangrove_roots.hit");
    public static final RegistryObject<SoundEvent> MUDDY_MANGROVE_ROOTS_FALL =
            registerSoundEvent("block.muddy_mangrove_roots.fall");

    private static ForgeSoundType copperGrateSounds = null;
    private static ForgeSoundType copperBulbSounds = null;
    private static ForgeSoundType mangroveRootsSounds = null;
    private static ForgeSoundType muddyMangroveRootsSounds = null;

    public static ForgeSoundType COPPER_GRATE_SOUNDS() {
        if (copperGrateSounds == null) {
            try {
                Supplier<SoundEvent> breakSound = () -> {
                    if (COPPER_GRATE_BREAK.isPresent()) {
                        return COPPER_GRATE_BREAK.get();
                    }
                    return net.minecraft.sounds.SoundEvents.COPPER_BREAK;
                };
                Supplier<SoundEvent> stepSound = () -> {
                    if (COPPER_GRATE_STEP.isPresent()) {
                        return COPPER_GRATE_STEP.get();
                    }
                    return net.minecraft.sounds.SoundEvents.COPPER_STEP;
                };

                copperGrateSounds = new ForgeSoundType(
                        1f,
                        1f,
                        breakSound,
                        stepSound,
                        stepSound,
                        stepSound,
                        stepSound
                );
            } catch (Exception e) {
                LOGGER.error(
                        "Failed to create copper grate sound type: " + e.getMessage(),
                        e
                );
                return new ForgeSoundType(
                        1f,
                        1f,
                        () -> net.minecraft.sounds.SoundEvents.COPPER_BREAK,
                        () -> net.minecraft.sounds.SoundEvents.COPPER_STEP,
                        () -> net.minecraft.sounds.SoundEvents.COPPER_STEP,
                        () -> net.minecraft.sounds.SoundEvents.COPPER_STEP,
                        () -> net.minecraft.sounds.SoundEvents.COPPER_STEP
                );
            }
        }
        return copperGrateSounds;
    }

    public static ForgeSoundType COPPER_BULB_SOUNDS() {
        if (copperBulbSounds == null) {
            try {
                Supplier<SoundEvent> breakSound = () -> {
                    if (COPPER_BULB_BREAK.isPresent()) {
                        return COPPER_BULB_BREAK.get();
                    }
                    return net.minecraft.sounds.SoundEvents.COPPER_BREAK;
                };
                Supplier<SoundEvent> stepSound = () -> {
                    if (COPPER_BULB_STEP.isPresent()) {
                        return COPPER_BULB_STEP.get();
                    }
                    return net.minecraft.sounds.SoundEvents.COPPER_STEP;
                };
                Supplier<SoundEvent> placeSound = () -> {
                    if (COPPER_BULB_PLACE.isPresent()) {
                        return COPPER_BULB_PLACE.get();
                    }
                    return net.minecraft.sounds.SoundEvents.COPPER_STEP;
                };

                copperBulbSounds = new ForgeSoundType(
                        1f,
                        1f,
                        breakSound,
                        stepSound,
                        placeSound,
                        stepSound,
                        stepSound
                );
            } catch (Exception e) {
                LOGGER.error(
                        "Failed to create copper bulb sound type: " + e.getMessage(),
                        e
                );
                return new ForgeSoundType(
                        1f,
                        1f,
                        () -> net.minecraft.sounds.SoundEvents.COPPER_BREAK,
                        () -> net.minecraft.sounds.SoundEvents.COPPER_STEP,
                        () -> net.minecraft.sounds.SoundEvents.COPPER_STEP,
                        () -> net.minecraft.sounds.SoundEvents.COPPER_STEP,
                        () -> net.minecraft.sounds.SoundEvents.COPPER_STEP
                );
            }
        }
        return copperBulbSounds;
    }

    public static com.kingodogo.buildscape.block.CustomSoundType MUD_SOUNDS() {
        return new com.kingodogo.buildscape.block.CustomSoundType(
                1.0f, 1.0f,   // break
                1.0f, 1.0f,  // step
                1.0f, 1.0f,   // place
                1.0f, 1.0f,   // hit
                1.0f, 1.0f,  // fall
                () -> MUD_BREAK.isPresent() ? MUD_BREAK.get() : net.minecraft.sounds.SoundEvents.GRAVEL_BREAK,
                () -> MUD_STEP.isPresent() ? MUD_STEP.get() : net.minecraft.sounds.SoundEvents.GRAVEL_STEP,
                () -> MUD_STEP.isPresent() ? MUD_STEP.get() : net.minecraft.sounds.SoundEvents.GRAVEL_PLACE,
                () -> MUD_STEP.isPresent() ? MUD_STEP.get() : net.minecraft.sounds.SoundEvents.GRAVEL_HIT,
                () -> MUD_STEP.isPresent() ? MUD_STEP.get() : net.minecraft.sounds.SoundEvents.GRAVEL_FALL
        );
    }

    public static com.kingodogo.buildscape.block.CustomSoundType PACKED_MUD_SOUNDS() {
        return new com.kingodogo.buildscape.block.CustomSoundType(
                0.3f, 1.0f,   // break
                1.0f, 0.95f, // step
                0.3f, 1.0f,   // place
                1.0f, 1.0f,  // hit
                1.0f, 1.0f,  // fall
                () -> PACKED_MUD_BREAK.isPresent() ? PACKED_MUD_BREAK.get() : net.minecraft.sounds.SoundEvents.GRAVEL_BREAK,
                () -> PACKED_MUD_STEP.isPresent() ? PACKED_MUD_STEP.get() : net.minecraft.sounds.SoundEvents.GRAVEL_STEP,
                () -> PACKED_MUD_PLACE.isPresent() ? PACKED_MUD_PLACE.get() : net.minecraft.sounds.SoundEvents.GRAVEL_PLACE,
                () -> PACKED_MUD_HIT.isPresent() ? PACKED_MUD_HIT.get() : net.minecraft.sounds.SoundEvents.GRAVEL_HIT,
                () -> PACKED_MUD_FALL.isPresent() ? PACKED_MUD_FALL.get() : net.minecraft.sounds.SoundEvents.GRAVEL_FALL
        );
    }

    public static com.kingodogo.buildscape.block.CustomSoundType MUD_BRICKS_SOUNDS() {
        return new com.kingodogo.buildscape.block.CustomSoundType(
                1.0f, 1.0f,  // break
                1.0f, 1.0f,   // step
                1.0f, 1.0f,  // place
                1.0f, 1.0f,  // hit
                1.0f, 1.0f,  // fall
                () -> MUD_BRICKS_BREAK.isPresent() ? MUD_BRICKS_BREAK.get() : net.minecraft.sounds.SoundEvents.STONE_BREAK,
                () -> MUD_BRICKS_STEP.isPresent() ? MUD_BRICKS_STEP.get() : net.minecraft.sounds.SoundEvents.STONE_STEP,
                () -> MUD_BRICKS_PLACE.isPresent() ? MUD_BRICKS_PLACE.get() : net.minecraft.sounds.SoundEvents.STONE_PLACE,
                () -> MUD_BRICKS_HIT.isPresent() ? MUD_BRICKS_HIT.get() : net.minecraft.sounds.SoundEvents.STONE_HIT,
                () -> MUD_BRICKS_FALL.isPresent() ? MUD_BRICKS_FALL.get() : net.minecraft.sounds.SoundEvents.STONE_FALL
        );
    }

    public static com.kingodogo.buildscape.block.CustomSoundType PETAL_CLOVER_SOUNDS() {
        return new com.kingodogo.buildscape.block.CustomSoundType(
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
                () -> net.minecraft.sounds.SoundEvents.FLOWERING_AZALEA_BREAK,
                () -> net.minecraft.sounds.SoundEvents.FLOWERING_AZALEA_STEP,
                () -> net.minecraft.sounds.SoundEvents.FLOWERING_AZALEA_PLACE,
                () -> net.minecraft.sounds.SoundEvents.FLOWERING_AZALEA_HIT,
                () -> net.minecraft.sounds.SoundEvents.FLOWERING_AZALEA_FALL
        );
    }

    public static com.kingodogo.buildscape.block.CustomSoundType FLOWER_BED_SOUNDS() {
        return new com.kingodogo.buildscape.block.CustomSoundType(
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
                    return net.minecraft.sounds.SoundEvents.FLOWERING_AZALEA_BREAK;
                },
                () -> {
                    if (FLOWER_BED_STEP.isPresent()) {
                        return FLOWER_BED_STEP.get();
                    }
                    return net.minecraft.sounds.SoundEvents.FLOWERING_AZALEA_STEP;
                },
                () -> {
                    if (FLOWER_BED_PLACE.isPresent()) {
                        return FLOWER_BED_PLACE.get();
                    }
                    return net.minecraft.sounds.SoundEvents.FLOWERING_AZALEA_PLACE;
                },
                () -> {
                    if (FLOWER_BED_HIT.isPresent()) {
                        return FLOWER_BED_HIT.get();
                    }
                    return net.minecraft.sounds.SoundEvents.FLOWERING_AZALEA_HIT;
                },
                () -> {
                    if (FLOWER_BED_FALL.isPresent()) {
                        return FLOWER_BED_FALL.get();
                    }
                    return net.minecraft.sounds.SoundEvents.FLOWERING_AZALEA_FALL;
                }
        );
    }

    public static com.kingodogo.buildscape.block.CustomSoundType VINE_SOUNDS() {
        return new com.kingodogo.buildscape.block.CustomSoundType(
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
                () -> net.minecraft.sounds.SoundEvents.VINE_BREAK,
                () -> net.minecraft.sounds.SoundEvents.VINE_STEP,
                () -> net.minecraft.sounds.SoundEvents.VINE_PLACE,
                () -> net.minecraft.sounds.SoundEvents.VINE_HIT,
                () -> net.minecraft.sounds.SoundEvents.VINE_FALL
        );
    }

    public static com.kingodogo.buildscape.block.CustomSoundType AZALEA_SOUNDS() {
        return new com.kingodogo.buildscape.block.CustomSoundType(
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
                () -> net.minecraft.sounds.SoundEvents.AZALEA_BREAK,
                () -> net.minecraft.sounds.SoundEvents.AZALEA_STEP,
                () -> net.minecraft.sounds.SoundEvents.AZALEA_PLACE,
                () -> net.minecraft.sounds.SoundEvents.AZALEA_HIT,
                () -> net.minecraft.sounds.SoundEvents.AZALEA_FALL
        );
    }

    public static ForgeSoundType MANGROVE_ROOTS_SOUNDS() {
        if (mangroveRootsSounds == null) {
            try {
                Supplier<SoundEvent> breakSound = () -> {
                    if (MANGROVE_ROOTS_BREAK.isPresent()) {
                        return MANGROVE_ROOTS_BREAK.get();
                    }
                    return net.minecraft.sounds.SoundEvents.WOOD_BREAK;
                };
                Supplier<SoundEvent> stepSound = () -> {
                    if (MANGROVE_ROOTS_STEP.isPresent()) {
                        return MANGROVE_ROOTS_STEP.get();
                    }
                    return net.minecraft.sounds.SoundEvents.WOOD_STEP;
                };
                Supplier<SoundEvent> placeSound = () -> {
                    if (MANGROVE_ROOTS_PLACE.isPresent()) {
                        return MANGROVE_ROOTS_PLACE.get();
                    }
                    return net.minecraft.sounds.SoundEvents.WOOD_PLACE;
                };
                Supplier<SoundEvent> hitSound = () -> {
                    if (MANGROVE_ROOTS_HIT.isPresent()) {
                        return MANGROVE_ROOTS_HIT.get();
                    }
                    return net.minecraft.sounds.SoundEvents.WOOD_HIT;
                };
                Supplier<SoundEvent> fallSound = () -> {
                    if (MANGROVE_ROOTS_FALL.isPresent()) {
                        return MANGROVE_ROOTS_FALL.get();
                    }
                    return net.minecraft.sounds.SoundEvents.WOOD_FALL;
                };

                mangroveRootsSounds = new ForgeSoundType(
                        1f,
                        1f,
                        breakSound,
                        stepSound,
                        placeSound,
                        hitSound,
                        fallSound
                );
            } catch (Exception e) {
                LOGGER.error(
                        "Failed to create mangrove roots sound type: " + e.getMessage(),
                        e
                );
                return new ForgeSoundType(
                        1f,
                        1f,
                        () -> net.minecraft.sounds.SoundEvents.WOOD_BREAK,
                        () -> net.minecraft.sounds.SoundEvents.WOOD_STEP,
                        () -> net.minecraft.sounds.SoundEvents.WOOD_STEP,
                        () -> net.minecraft.sounds.SoundEvents.WOOD_STEP,
                        () -> net.minecraft.sounds.SoundEvents.WOOD_STEP
                );
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
                    return net.minecraft.sounds.SoundEvents.WOOD_BREAK;
                };
                Supplier<SoundEvent> stepSound = () -> {
                    if (MUDDY_MANGROVE_ROOTS_STEP.isPresent()) {
                        return MUDDY_MANGROVE_ROOTS_STEP.get();
                    }
                    return net.minecraft.sounds.SoundEvents.WOOD_STEP;
                };
                Supplier<SoundEvent> placeSound = () -> {
                    if (MUDDY_MANGROVE_ROOTS_PLACE.isPresent()) {
                        return MUDDY_MANGROVE_ROOTS_PLACE.get();
                    }
                    return net.minecraft.sounds.SoundEvents.WOOD_PLACE;
                };
                Supplier<SoundEvent> hitSound = () -> {
                    if (MUDDY_MANGROVE_ROOTS_HIT.isPresent()) {
                        return MUDDY_MANGROVE_ROOTS_HIT.get();
                    }
                    return net.minecraft.sounds.SoundEvents.WOOD_HIT;
                };
                Supplier<SoundEvent> fallSound = () -> {
                    if (MUDDY_MANGROVE_ROOTS_FALL.isPresent()) {
                        return MUDDY_MANGROVE_ROOTS_FALL.get();
                    }
                    return net.minecraft.sounds.SoundEvents.WOOD_FALL;
                };

                muddyMangroveRootsSounds = new ForgeSoundType(
                        1f,
                        1f,
                        breakSound,
                        stepSound,
                        placeSound,
                        hitSound,
                        fallSound
                );
            } catch (Exception e) {
                LOGGER.error(
                        "Failed to create muddy mangrove roots sound type: " + e.getMessage(),
                        e
                );
                return new ForgeSoundType(
                        1f,
                        1f,
                        () -> net.minecraft.sounds.SoundEvents.WOOD_BREAK,
                        () -> net.minecraft.sounds.SoundEvents.WOOD_STEP,
                        () -> net.minecraft.sounds.SoundEvents.WOOD_STEP,
                        () -> net.minecraft.sounds.SoundEvents.WOOD_STEP,
                        () -> net.minecraft.sounds.SoundEvents.WOOD_STEP
                );
            }
        }
        return muddyMangroveRootsSounds;
    }

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name.replace('.', '_'), () ->
                new SoundEvent(new ResourceLocation(BuildScape.MODID, name))
        );
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
