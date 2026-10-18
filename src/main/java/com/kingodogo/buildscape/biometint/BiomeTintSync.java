package com.kingodogo.buildscape.biometint;

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
    @SubscribeEvent
    public static void onWatch(ChunkWatchEvent.Watch event) {
        ServerLevel level = event.getWorld();
        ModMessages.INSTANCE.send(PacketDistributor.PLAYER.with(event::getPlayer), packet(level, event.getPos()));
    }

    public static void sync(ServerLevel level, ChunkPos chunk) {
        SyncBiomeTintPacket packet = packet(level, chunk);
        level.getChunkSource().chunkMap.getPlayers(chunk, false).forEach(player ->
                ModMessages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet));
    }

    private static SyncBiomeTintPacket packet(ServerLevel level, ChunkPos chunk) {
        return new SyncBiomeTintPacket(level.dimension().location(), chunk, BiomeTintData.get(level).getChunk(chunk));
    }
}
