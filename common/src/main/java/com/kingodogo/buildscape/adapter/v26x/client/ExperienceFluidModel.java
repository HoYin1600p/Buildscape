package com.kingodogo.buildscape.adapter.v26x.client;

import com.kingodogo.buildscape.fluid.ExperienceFluidProperties;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;

/** 26.2 fluid textures and constant white tint (the reference textures are already colored). */
public final class ExperienceFluidModel {
    private ExperienceFluidModel() {}

    public static FluidModel.Unbaked create() {
        return new FluidModel.Unbaked(
                new Material(Identifier.fromNamespaceAndPath("buildscape", "fluid/experience_still"), true),
                new Material(Identifier.fromNamespaceAndPath("buildscape", "fluid/experience_flow"), true),
                null, state -> ExperienceFluidProperties.TINT);
    }
}
