package com.kingodogo.buildscape.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class ConfigAssembly {
    private static final int MAX_UPDATE_BYTES = 2 * 1024 * 1024;
    private UUID transferId;
    private int chunkCount;
    private int nextIndex;
    private long started;
    private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();

    public long getStarted() {
        return started;
    }

    public SyncConfigPacket accept(UpdateConfigPacket packet) {
        long now = System.currentTimeMillis();
        if (nextIndex == 0) {
            transferId = packet.getTransferId();
            chunkCount = packet.getChunkCount();
            started = now;
        }
        if (!transferId.equals(packet.getTransferId()) || chunkCount != packet.getChunkCount()
                || nextIndex != packet.getChunkIndex() || now - started > 30_000L
                || bytes.size() + packet.getPayload().length > MAX_UPDATE_BYTES) {
            throw new IllegalArgumentException("Incomplete or oversized config update");
        }
        bytes.write(packet.getPayload(), 0, packet.getPayload().length);
        nextIndex++;
        if (nextIndex != chunkCount) {
            return null;
        }
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.wrappedBuffer(bytes.toByteArray()));
        try {
            SyncConfigPacket data = new SyncConfigPacket(buffer);
            if (buffer.isReadable()) {
                throw new IllegalArgumentException("Trailing config update data");
            }
            validateItems(data);
            return data;
        } finally {
            buffer.release();
        }
    }

    private static void validateItems(SyncConfigPacket data) {
        for (String item : data.items) {
            String id = item.startsWith("#") ? item.substring(1) : item;
            if (!net.minecraft.resources.ResourceLocation.isValidResourceLocation(id)) {
                throw new IllegalArgumentException("Invalid configured item or tag: " + item);
            }
        }
    }
}
