package net.minecraft.world.level.material;

import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;

public class Fluids {
   public static final Fluid EMPTY = register(FluidIds.EMPTY, new EmptyFluid());
   public static final FlowingFluid FLOWING_WATER = (FlowingFluid)register(FluidIds.FLOWING_WATER, new WaterFluid.Flowing());
   public static final FlowingFluid WATER = (FlowingFluid)register(FluidIds.WATER, new WaterFluid.Source());
   public static final FlowingFluid FLOWING_LAVA = (FlowingFluid)register(FluidIds.FLOWING_LAVA, new LavaFluid.Flowing());
   public static final FlowingFluid LAVA = (FlowingFluid)register(FluidIds.LAVA, new LavaFluid.Source());

   private static Fluid register(final ResourceKey id, final Fluid fluid) {
      return (Fluid)Registry.register(BuiltInRegistries.FLUID, id, fluid);
   }

   static {
      for(Fluid fluid : BuiltInRegistries.FLUID) {
         UnmodifiableIterator var2 = fluid.getStateDefinition().getPossibleStates().iterator();

         while(var2.hasNext()) {
            FluidState state = (FluidState)var2.next();
            Fluid.FLUID_STATE_REGISTRY.add(state);
         }
      }

   }
}
