package com.kingodogo.buildscape.mixin.forge;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = BlockEvent.EntityPlaceEvent.class, remap = false)
public interface PlacementEventAccessor {
    @Mutable
    @Accessor("placedBlock")
    void buildscape$setPlacedBlock(BlockState state);
}
