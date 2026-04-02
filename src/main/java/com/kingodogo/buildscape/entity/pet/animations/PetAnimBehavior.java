package com.kingodogo.buildscape.entity.pet.animations;

import com.kingodogo.buildscape.entity.pet.PetEntity;

public interface PetAnimBehavior {
    void apply(AnimationTarget target, float tick, float limbSwing, float limbSwingAmount, PetEntity entity);
}
