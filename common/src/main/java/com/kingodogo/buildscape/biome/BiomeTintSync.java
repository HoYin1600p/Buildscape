package com.kingodogo.buildscape.biome;

import com.kingodogo.buildscape.network.BiomeTintPayload;
import com.kingodogo.buildscape.network.PacketFactory;
import com.kingodogo.buildscape.network.SyncBiomeTintPacket;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

/** Sends complete snapshots so clients can replace stale chunk state. */
public final class BiomeTintSync {
    private BiomeTintSync() { }

    public static void sendChunk(ServerLevel level, ServerPlayer player, int chunkX, int chunkZ) {
        BiomeTintData data = BiomeTintData.get(level);
        long chunk = BiomeTintStore.chunkKey(chunkX, chunkZ);
        if (!data.hasChunk(chunk)) return;
        PacketFactory.sendToPlayer(player, new SyncBiomeTintPacket(new BiomeTintPayload(
                Services.PLATFORM.getDimensionId(level), chunkX, chunkZ, data.getChunk(chunk))));
    }

    public static void syncChunk(ServerLevel level, int chunkX, int chunkZ) {
        long chunk = BiomeTintStore.chunkKey(chunkX, chunkZ);
        var packet = new SyncBiomeTintPacket(new BiomeTintPayload(Services.PLATFORM.getDimensionId(level),
                chunkX, chunkZ, BiomeTintData.get(level).getChunk(chunk)));
        for (ServerPlayer player : level.getChunkSource().chunkMap.getPlayers(new ChunkPos(chunkX, chunkZ), false)) {
            PacketFactory.sendToPlayer(player, packet);
        }
    }
}
