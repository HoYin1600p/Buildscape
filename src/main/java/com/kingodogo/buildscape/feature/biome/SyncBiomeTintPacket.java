package com.kingodogo.buildscape.feature.biome;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SyncBiomeTintPacket {
    final ResourceLocation dimension;
    final ChunkPos chunk;
    final Map<Integer, ResourceLocation> columns;

    public SyncBiomeTintPacket(ResourceLocation dimension, ChunkPos chunk, Map<Integer, ResourceLocation> columns) {
        if (columns.size() > 256 || columns.keySet().stream().anyMatch(index -> index < 0 || index >= 256)) {
            throw new IllegalArgumentException("Invalid biome tint columns");
        }
        this.dimension = dimension;
        this.chunk = chunk;
        this.columns = Map.copyOf(columns);
    }

    public static void encode(SyncBiomeTintPacket msg, FriendlyByteBuf buf) {
        buf.writeResourceLocation(msg.dimension);
        buf.writeChunkPos(msg.chunk);
        buf.writeVarInt(msg.columns.size());
        msg.columns.forEach((index, biome) -> {
            buf.writeByte(index);
            buf.writeResourceLocation(biome);
        });
    }

    public static SyncBiomeTintPacket decode(FriendlyByteBuf buf) {
        ResourceLocation dimension = buf.readResourceLocation();
        ChunkPos chunk = buf.readChunkPos();
        int count = buf.readVarInt();
        if (count < 0 || count > 256) {
            throw new IllegalArgumentException("Invalid biome tint count");
        }
        Map<Integer, ResourceLocation> columns = new HashMap<>();
        for (int i = 0; i < count; i++) {
            int index = buf.readUnsignedByte();
            if (columns.put(index, buf.readResourceLocation()) != null) {
                throw new IllegalArgumentException("Duplicate biome tint column");
            }
        }
        return new SyncBiomeTintPacket(dimension, chunk, columns);
    }

    public static void handle(SyncBiomeTintPacket msg, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> ClientBiomeTints.apply(msg)));
        context.setPacketHandled(true);
    }
}
