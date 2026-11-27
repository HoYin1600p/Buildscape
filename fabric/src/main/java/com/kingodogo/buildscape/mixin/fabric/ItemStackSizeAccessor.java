package com.kingodogo.buildscape.mixin.fabric;

import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Item.class)
public interface ItemStackSizeAccessor {
    @Mutable @Accessor("maxStackSize") void buildscape$setMaxStackSize(int size);
}
