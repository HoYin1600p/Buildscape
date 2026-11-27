package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.biome.BiomeTintSync;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkMap.class)
abstract class BiomeTintChunkWatchMixin {
    @Inject(method = "playerLoadedChunk", at = @At("TAIL"))
    private void buildscape$sendBiomeTints(ServerPlayer player,
            MutableObject<ClientboundLevelChunkWithLightPacket> packet, LevelChunk chunk, CallbackInfo ci) {
        if (Services.PLATFORM.getEntityLevel(player) instanceof ServerLevel level) {
            BiomeTintSync.sendChunk(level, player, chunk.getPos().getMinBlockX() >> 4,
                    chunk.getPos().getMinBlockZ() >> 4);
        }
    }
}
