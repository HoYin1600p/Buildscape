package com.kingodogo.buildscape.block;

import java.util.function.Supplier;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.common.util.ForgeSoundType;

public class CustomSoundType extends ForgeSoundType {

    private final float breakVolume;
    private final float breakPitch;
    private final float stepVolume;
    private final float stepPitch;
    private final float placeVolume;
    private final float placePitch;
    private final float hitVolume;
    private final float hitPitch;
    private final float fallVolume;
    private final float fallPitch;

    public CustomSoundType(
            float breakVolume,
            float breakPitch,
            float stepVolume,
            float stepPitch,
            float placeVolume,
            float placePitch,
            float hitVolume,
            float hitPitch,
            float fallVolume,
            float fallPitch,
            Supplier<SoundEvent> breakSound,
            Supplier<SoundEvent> stepSound,
            Supplier<SoundEvent> placeSound,
            Supplier<SoundEvent> hitSound,
            Supplier<SoundEvent> fallSound
    ) {
        super(1.0f, 1.0f, breakSound, stepSound, placeSound, hitSound, fallSound);
        this.breakVolume = breakVolume;
        this.breakPitch = breakPitch;
        this.stepVolume = stepVolume;
        this.stepPitch = stepPitch;
        this.placeVolume = placeVolume;
        this.placePitch = placePitch;
        this.hitVolume = hitVolume;
        this.hitPitch = hitPitch;
        this.fallVolume = fallVolume;
        this.fallPitch = fallPitch;
    }

    @Override
    public float getVolume() {
        return breakVolume;
    }

    @Override
    public float getPitch() {
        return breakPitch;
    }

    public float getStepVolume() {
        return stepVolume;
    }

    public float getStepPitch() {
        return stepPitch;
    }

    public float getPlaceVolume() {
        return placeVolume;
    }

    public float getPlacePitch() {
        return placePitch;
    }

    public float getHitVolume() {
        return hitVolume;
    }

    public float getHitPitch() {
        return hitPitch;
    }

    public float getFallVolume() {
        return fallVolume;
    }

    public float getFallPitch() {
        return fallPitch;
    }
}
