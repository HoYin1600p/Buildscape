package com.kingodogo.buildscape.test.animations;

public class AnimationTarget {
    public float headX, headY, headZ, headYPos, headZPos;
    public float bodyX, bodyY, bodyZ, bodyYPos, bodyZPos;
    public float rightArmX, rightArmY, rightArmZ, rightArmYPos, rightArmZPos;
    public float leftArmX, leftArmY, leftArmZ, leftArmYPos, leftArmZPos;
    public float rightLegX, rightLegY, rightLegZ, rightLegYPos;
    public float leftLegX, leftLegY, leftLegZ, leftLegYPos;
    public float rootX, rootY, rootZ, rootXPos, rootYPos, rootZPos;

    // Reset back to standard Base Humanoid Offsets before an animation applies
    public void resetDefaults(float headPitch, float netHeadYaw) {
        rootX = 0; rootY = 0; rootZ = 0; rootXPos = 0; rootYPos = 0; rootZPos = 0;
        headX = headPitch * ((float)Math.PI / 180F);
        headY = netHeadYaw * ((float)Math.PI / 180F);
        headZ = 0; headYPos = 0; headZPos = 0;
        bodyX = 0; bodyY = 0; bodyZ = 0; bodyYPos = 0; bodyZPos = 0;
        rightArmX = 0; rightArmY = 0; rightArmZ = 0; rightArmYPos = 2.0F; rightArmZPos = 0;
        leftArmX = 0; leftArmY = 0; leftArmZ = 0; leftArmYPos = 2.0F; leftArmZPos = 0;
        rightLegX = 0; rightLegY = 0; rightLegZ = 0; rightLegYPos = 12.0F;
        leftLegX = 0; leftLegY = 0; leftLegZ = 0; leftLegYPos = 12.0F;
    }
}
