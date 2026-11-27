package com.kingodogo.buildscape.block;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.sounds.BlockSoundSet;
import net.minecraft.world.level.block.sounds.BlockSoundSets;

public class CustomSoundType {
    public static final CustomSoundType EMPTY = named(BlockSoundSets.STONE, 1f, 1f, "intentionally_empty", "intentionally_empty", "intentionally_empty", "intentionally_empty", "intentionally_empty");
    public static final CustomSoundType STONE = named(BlockSoundSets.STONE, 1f, 1f, "block.stone.break", "block.stone.step", "block.stone.place", "block.stone.hit", "block.stone.fall");
    public static final CustomSoundType WOOD = named(BlockSoundSets.WOOD, 1f, 1f, "block.wood.break", "block.wood.step", "block.wood.place", "block.wood.hit", "block.wood.fall");
    public static final CustomSoundType METAL = named(BlockSoundSets.METAL, 1f, 1.5f, "block.metal.break", "block.metal.step", "block.metal.place", "block.metal.hit", "block.metal.fall");
    public static final CustomSoundType GLASS = named(BlockSoundSets.GLASS, 1f, 1f, "block.glass.break", "block.glass.step", "block.glass.place", "block.glass.hit", "block.glass.fall");
    public static final CustomSoundType GRASS = named(BlockSoundSets.GRASS, 1f, 1f, "block.grass.break", "block.grass.step", "block.grass.place", "block.grass.hit", "block.grass.fall");
    public static final CustomSoundType GRAVEL = named(BlockSoundSets.GRAVEL, 1f, 1f, "block.gravel.break", "block.gravel.step", "block.gravel.place", "block.gravel.hit", "block.gravel.fall");
    public static final CustomSoundType SAND = named(BlockSoundSets.SAND, 1f, 1f, "block.sand.break", "block.sand.step", "block.sand.place", "block.sand.hit", "block.sand.fall");
    public static final CustomSoundType SNOW = named(BlockSoundSets.SNOW, 1f, 1f, "block.snow.break", "block.snow.step", "block.snow.place", "block.snow.hit", "block.snow.fall");
    public static final CustomSoundType WOOL = named(BlockSoundSets.WOOL, 1f, 1f, "block.wool.break", "block.wool.step", "block.wool.place", "block.wool.hit", "block.wool.fall");
    public static final CustomSoundType COPPER = named(BlockSoundSets.COPPER, 1f, 1f, "block.copper.break", "block.copper.step", "block.copper.place", "block.copper.hit", "block.copper.fall");
    public static final CustomSoundType NETHERITE_BLOCK = named(BlockSoundSets.NETHERITE_BLOCK, 1f, 1f, "block.netherite_block.break", "block.netherite_block.step", "block.netherite_block.place", "block.netherite_block.hit", "block.netherite_block.fall");
    public static final CustomSoundType AMETHYST = named(BlockSoundSets.AMETHYST_BLOCK, 1f, 1f, "block.amethyst_block.break", "block.amethyst_block.step", "block.amethyst_block.place", "block.amethyst_block.hit", "block.amethyst_block.fall");
    public static final CustomSoundType DEEPSLATE = named(BlockSoundSets.DEEPSLATE, 1f, 1f, "block.deepslate.break", "block.deepslate.step", "block.deepslate.place", "block.deepslate.hit", "block.deepslate.fall");
    public static final CustomSoundType AZALEA = named(BlockSoundSets.AZALEA, 1f, 1f, "block.azalea.break", "block.azalea.step", "block.azalea.place", "block.azalea.hit", "block.azalea.fall");
    public static final CustomSoundType VINE = named(BlockSoundSets.VINE, 1f, 1f, "block.vine.break", "block.vine.step", "block.vine.place", "block.vine.hit", "block.vine.fall");
    public static final CustomSoundType CANDLE = named(BlockSoundSets.CANDLE, 1f, 1f, "block.candle.break", "block.candle.step", "block.candle.place", "block.candle.hit", "block.candle.fall");
    public static final CustomSoundType CHAIN = named(BlockSoundSets.CHAIN, 1f, 1f, "block.chain.break", "block.chain.step", "block.chain.place", "block.chain.hit", "block.chain.fall");
    public static final CustomSoundType BAMBOO = named(BlockSoundSets.BAMBOO, 1f, 1f, "block.bamboo.break", "block.bamboo.step", "block.bamboo.place", "block.bamboo.hit", "block.bamboo.fall");
    public static final CustomSoundType ANVIL = named(BlockSoundSets.ANVIL, 0.3f, 1f, "block.anvil.break", "block.anvil.step", "block.anvil.place", "block.anvil.hit", "block.anvil.fall");
    public static final CustomSoundType SLIME_BLOCK = named(BlockSoundSets.SLIME_BLOCK, 1f, 1f, "block.slime_block.break", "block.slime_block.step", "block.slime_block.place", "block.slime_block.hit", "block.slime_block.fall");
    public static final CustomSoundType HONEY_BLOCK = named(BlockSoundSets.HONEY_BLOCK, 1f, 1f, "block.honey_block.break", "block.honey_block.step", "block.honey_block.place", "block.honey_block.hit", "block.honey_block.fall");
    public static final CustomSoundType BONE_BLOCK = named(BlockSoundSets.BONE_BLOCK, 1f, 1f, "block.bone_block.break", "block.bone_block.step", "block.bone_block.place", "block.bone_block.hit", "block.bone_block.fall");
    public static final CustomSoundType LADDER = named(BlockSoundSets.LADDER, 1f, 1f, "block.ladder.break", "block.ladder.step", "block.ladder.place", "block.ladder.hit", "block.ladder.fall");
    public static final CustomSoundType POWDER_SNOW = named(BlockSoundSets.POWDER_SNOW, 1f, 1f, "block.powder_snow.break", "block.powder_snow.step", "block.powder_snow.place", "block.powder_snow.hit", "block.powder_snow.fall");
    public static final CustomSoundType AZALEA_LEAVES = named(BlockSoundSets.AZALEA_LEAVES, 1f, 1f, "block.azalea_leaves.break", "block.azalea_leaves.step", "block.azalea_leaves.place", "block.azalea_leaves.hit", "block.azalea_leaves.fall");
    public static final CustomSoundType FLOWERING_AZALEA = named(BlockSoundSets.FLOWERING_AZALEA, 1f, 1f, "block.flowering_azalea.break", "block.flowering_azalea.step", "block.flowering_azalea.place", "block.flowering_azalea.hit", "block.flowering_azalea.fall");

    private final ResourceKey<BlockSoundSet> soundSetKey;
    public final float volume;
    public final float pitch;
    private final SoundEvent breakSound;
    private final SoundEvent stepSound;
    private final SoundEvent placeSound;
    private final SoundEvent hitSound;
    private final SoundEvent fallSound;
    private final String breakId, stepId, placeId, hitId, fallId;

    private final float breakVolume, breakPitch, stepVolume, stepPitch, placeVolume, placePitch, hitVolume, hitPitch, fallVolume, fallPitch;

    private static CustomSoundType named(ResourceKey<BlockSoundSet> key, float volume, float pitch, String b, String s, String p, String h, String f) {
        return new CustomSoundType(key, volume, pitch, volume, pitch, volume, pitch, volume, pitch, volume, pitch, b, s, p, h, f);
    }

    public CustomSoundType(ResourceKey<BlockSoundSet> key, float breakVolume, float breakPitch, float stepVolume, float stepPitch,
                           float placeVolume, float placePitch, float hitVolume, float hitPitch,
                           float fallVolume, float fallPitch, String b, String s, String p, String h, String f) {
        this.soundSetKey = key;
        this.volume = breakVolume; this.pitch = breakPitch;
        this.breakSound = null; this.stepSound = null; this.placeSound = null; this.hitSound = null; this.fallSound = null;
        this.breakId = b; this.stepId = s; this.placeId = p; this.hitId = h; this.fallId = f;
        this.breakVolume = breakVolume; this.breakPitch = breakPitch;
        this.stepVolume = stepVolume; this.stepPitch = stepPitch;
        this.placeVolume = placeVolume; this.placePitch = placePitch;
        this.hitVolume = hitVolume; this.hitPitch = hitPitch;
        this.fallVolume = fallVolume; this.fallPitch = fallPitch;
    }

    public CustomSoundType(float breakVolume, float breakPitch, float stepVolume, float stepPitch,
                           float placeVolume, float placePitch, float hitVolume, float hitPitch,
                           float fallVolume, float fallPitch, SoundEvent breakSound, SoundEvent stepSound,
                           SoundEvent placeSound, SoundEvent hitSound, SoundEvent fallSound) {
        this.soundSetKey = BlockSoundSets.STONE;
        this.volume = breakVolume; this.pitch = breakPitch;
        this.breakSound = breakSound; this.stepSound = stepSound; this.placeSound = placeSound; this.hitSound = hitSound; this.fallSound = fallSound;
        this.breakId = null; this.stepId = null; this.placeId = null; this.hitId = null; this.fallId = null;
        this.breakVolume = breakVolume; this.breakPitch = breakPitch;
        this.stepVolume = stepVolume; this.stepPitch = stepPitch;
        this.placeVolume = placeVolume; this.placePitch = placePitch;
        this.hitVolume = hitVolume; this.hitPitch = hitPitch;
        this.fallVolume = fallVolume; this.fallPitch = fallPitch;
    }

    public ResourceKey<BlockSoundSet> soundSetKey() { return soundSetKey; }

    private static SoundEvent resolve(SoundEvent direct, String id) {
        if (direct != null) return direct;
        if (id != null) {
            var opt = BuiltInRegistries.SOUND_EVENT.getOptional(Identifier.withDefaultNamespace(id));
            if (opt.isPresent()) return opt.get();
        }
        return SoundEvents.AMETHYST_BLOCK_CHIME;
    }

    public float getVolume() { return volume; }
    public float getPitch() { return pitch; }
    public SoundEvent getBreakSound() { return resolve(breakSound, breakId); }
    public SoundEvent getStepSound() { return resolve(stepSound, stepId); }
    public SoundEvent getPlaceSound() { return resolve(placeSound, placeId); }
    public SoundEvent getHitSound() { return resolve(hitSound, hitId); }
    public SoundEvent getFallSound() { return resolve(fallSound, fallId); }

    public float getBreakVolume() { return breakVolume; }
    public float getBreakPitch() { return breakPitch; }
    public float getStepVolume() { return stepVolume; }
    public float getStepPitch() { return stepPitch; }
    public float getPlaceVolume() { return placeVolume; }
    public float getPlacePitch() { return placePitch; }
    public float getHitVolume() { return hitVolume; }
    public float getHitPitch() { return hitPitch; }
    public float getFallVolume() { return fallVolume; }
    public float getFallPitch() { return fallPitch; }
}
