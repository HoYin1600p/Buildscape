package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.MapCodec;

public class NopProcessor implements StructureProcessor {
   public static final MapCodec MAP_CODEC = MapCodec.unit(() -> NopProcessor.INSTANCE);
   public static final NopProcessor INSTANCE = new NopProcessor();

   private NopProcessor() {
   }

   public MapCodec codec() {
      return MAP_CODEC;
   }
}
