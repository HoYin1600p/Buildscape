package net.minecraft.world.level.chunk;

import java.util.function.BiConsumer;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.lighting.ChunkSkyLightSources;

public interface LightChunk extends BlockGetter {
   void findBlockLightSources(BiConsumer consumer);

   ChunkSkyLightSources getSkyLightSources();
}
