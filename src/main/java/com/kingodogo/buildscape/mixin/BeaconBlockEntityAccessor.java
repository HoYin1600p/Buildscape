package com.kingodogo.buildscape.mixin;

import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BeaconBlockEntity.BeaconBeamSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(BeaconBlockEntity.class)
public interface BeaconBlockEntityAccessor {
    @Accessor("lastCheckY")
    int getLastCheckY();

    @Accessor("beamSections")
    List<BeaconBeamSection> getBeamSections();
}
