package net.minecraft.world.level.gameevent;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.StreamCodec;

public interface PositionSourceType {
   PositionSourceType BLOCK = register("block", new BlockPositionSource.Type());
   PositionSourceType ENTITY = register("entity", new EntityPositionSource.Type());

   MapCodec codec();

   StreamCodec streamCodec();

   static PositionSourceType register(final String name, final PositionSourceType serializer) {
      return (PositionSourceType)Registry.register(BuiltInRegistries.POSITION_SOURCE_TYPE, name, serializer);
   }
}
