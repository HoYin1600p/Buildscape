package com.kingodogo.buildscape.mixin.forge262;

import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CauldronInteraction.Dispatcher.class)
public interface CauldronDispatcherInvoker {
    @Invoker("put")
    void buildscape$put(Item item, CauldronInteraction interaction);
}
