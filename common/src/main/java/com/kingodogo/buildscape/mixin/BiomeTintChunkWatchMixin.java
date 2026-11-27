package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.biome.BiomeTintSync;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.PlayerChunkSender;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerChunkSender.class)
abstract class BiomeTintChunkWatchMixin {
    @Inject(method = "sendChunk", at = @At("TAIL"))
    private static void buildscape$sendBiomeTints(ServerGamePacketListenerImpl connection,
            ServerLevel level, LevelChunk chunk, CallbackInfo ci) {
        BiomeTintSync.sendChunk(level, connection.player, chunk.getPos().getMinBlockX() >> 4,
                chunk.getPos().getMinBlockZ() >> 4);
    }
}
