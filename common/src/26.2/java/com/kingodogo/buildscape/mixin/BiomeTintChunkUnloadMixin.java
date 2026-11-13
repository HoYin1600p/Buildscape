package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.biome.BiomeTintStore;
import com.kingodogo.buildscape.client.biome.BiomeTintRendering;
import com.kingodogo.buildscape.client.biome.ClientBiomeTints;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientChunkCache.class)
abstract class BiomeTintChunkUnloadMixin {
    @Shadow @Final private ClientLevel level;

    @Inject(method = "drop", at = @At("TAIL"))
    private void buildscape$unloadBiomeTints(ChunkPos chunk, CallbackInfo ci) {
        int x = chunk.getMinBlockX() >> 4, z = chunk.getMinBlockZ() >> 4;
        if (ClientBiomeTints.cache().remove(level, BiomeTintStore.chunkKey(x, z))) {
            BiomeTintRendering.rebuild(level, x, z);
        }
    }
}
