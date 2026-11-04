package com.kingodogo.buildscape.feature.biome;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.network.ModMessages;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = BuildScape.MODID)
public class BiomeTintSync {
    /**
     * Sends a newly watched chunk's tints, but only when it has any. Tints are never removed and the client
     * drops a chunk's tints when it unloads, so an unpainted chunk needs no packet.
     */
    @SubscribeEvent
    public static void onWatch(ChunkWatchEvent.Watch event) {
        ServerLevel level = event.getWorld();
        BiomeTintData data = BiomeTintData.get(level);
        if (!data.hasChunk(event.getPos())) {
            return;
        }
        ModMessages.INSTANCE.send(PacketDistributor.PLAYER.with(event::getPlayer),
                new SyncBiomeTintPacket(level.dimension().location(), event.getPos(), data.getChunk(event.getPos())));
    }

    public static void sync(ServerLevel level, ChunkPos chunk) {
        SyncBiomeTintPacket packet = packet(level, chunk);
        level.getChunkSource().chunkMap.getPlayers(chunk, false)
                .forEach(player -> ModMessages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet));
    }

    private static SyncBiomeTintPacket packet(ServerLevel level, ChunkPos chunk) {
        return new SyncBiomeTintPacket(level.dimension().location(), chunk, BiomeTintData.get(level).getChunk(chunk));
    }
}
