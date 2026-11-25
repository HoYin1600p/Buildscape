package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.network.FriendlyByteBuf;

import java.util.HashMap;
import java.util.Map;

/** A complete snapshot for one chunk, including an empty snapshot to clear it. */
public record BiomeTintPayload(CommonId dimension, int chunkX, int chunkZ, Map<Integer, CommonId> columns) {
    public BiomeTintPayload {
        requireId(dimension);
        columns = Map.copyOf(columns);
        NetworkPacketLimits.checkCount(columns.size(), 256, "biome tint column");
        columns.forEach((index, biome) -> {
            if (index < 0 || index >= 256) throw new IllegalArgumentException("Invalid biome tint column");
            requireId(biome);
        });
    }

    public void write(FriendlyByteBuf buffer) {
        writeId(buffer, dimension);
        buffer.writeInt(chunkX);
        buffer.writeInt(chunkZ);
        buffer.writeInt(columns.size());
        columns.forEach((index, biome) -> {
            buffer.writeByte(index);
            writeId(buffer, biome);
        });
    }

    public static BiomeTintPayload read(FriendlyByteBuf buffer) {
        CommonId dimension = readId(buffer);
        int chunkX = buffer.readInt();
        int chunkZ = buffer.readInt();
        int count = NetworkPacketLimits.readCount(buffer, 256, "biome tint column");
        Map<Integer, CommonId> columns = new HashMap<>();
        for (int i = 0; i < count; i++) {
            int index = buffer.readUnsignedByte();
            if (columns.putIfAbsent(index, readId(buffer)) != null) {
                throw new IllegalArgumentException("Duplicate biome tint column: " + index);
            }
        }
        return new BiomeTintPayload(dimension, chunkX, chunkZ, columns);
    }

    private static CommonId readId(FriendlyByteBuf buffer) {
        CommonId id = CommonId.tryParse(NetworkPacketLimits.readUtf(buffer,
                NetworkPacketLimits.MAX_RESOURCE_ID_LENGTH, "biome tint identifier"));
        requireId(id);
        return id;
    }

    private static void writeId(FriendlyByteBuf buffer, CommonId id) {
        NetworkPacketLimits.writeUtf(buffer, id.toString(),
                NetworkPacketLimits.MAX_RESOURCE_ID_LENGTH, "biome tint identifier");
    }

    private static void requireId(CommonId id) {
        if (id == null || !id.equals(CommonId.tryParse(id.toString()))
                || id.toString().length() > NetworkPacketLimits.MAX_RESOURCE_ID_LENGTH) {
            throw new IllegalArgumentException("Invalid biome tint identifier");
        }
    }
}
