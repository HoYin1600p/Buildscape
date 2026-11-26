package com.kingodogo.buildscape.mixin.fabric;

import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PoiType.class)
public interface PoiMapAccessor {
    @Accessor("TYPE_BY_STATE") static java.util.Map<BlockState, PoiType> buildscape$getTypes() { throw new AssertionError(); }
}
