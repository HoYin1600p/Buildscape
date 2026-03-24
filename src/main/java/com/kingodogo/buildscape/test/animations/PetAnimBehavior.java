package com.kingodogo.buildscape.test.animations;

import com.kingodogo.buildscape.test.TestPetEntity;

public interface PetAnimBehavior {
    void apply(AnimationTarget target, float tick, float limbSwing, float limbSwingAmount, TestPetEntity entity);
}
