package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.config.PillarIdManager;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class EditPillarRowsPacket {

    public static final int MAX_ROWS = 64;
    private final List<Row> rows;

    public EditPillarRowsPacket(List<Row> rows) {
        NetworkPacketLimits.checkCount(rows.size(), MAX_ROWS, "pillar edit row");
        this.rows = new ArrayList<>(rows);
    }

    public EditPillarRowsPacket(FriendlyByteBuf buf) {
        int count = NetworkPacketLimits.readCount(buf, MAX_ROWS, "pillar edit row");
        this.rows = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Row row = new Row(NetworkPacketLimits.readUtf(buf, 32, "old pillar id"),
                    NetworkPacketLimits.readUtf(buf, 32, "new pillar id"),
                    NetworkPacketLimits.readUtf(buf, NetworkPacketLimits.MAX_RESOURCE_ID_LENGTH, "pillar dimension"),
                    buf.readInt(), buf.readInt(), buf.readInt());
            if (!row.isValid()) {
                throw new DecoderException("invalid pillar edit row");
            }
            rows.add(row);
        }
    }

    public static EditPillarRowsPacket decode(FriendlyByteBuf buf) {
        return new EditPillarRowsPacket(buf);
    }

    public void encode(FriendlyByteBuf buf) {
        NetworkPacketLimits.checkCount(rows.size(), MAX_ROWS, "pillar edit row");
        buf.writeInt(rows.size());
        for (Row row : rows) {
            if (row == null || !row.isValid()) {
                throw new EncoderException("invalid pillar edit row");
            }
            NetworkPacketLimits.writeUtf(buf, row.oldId, 32, "old pillar id");
            NetworkPacketLimits.writeUtf(buf, row.newId, 32, "new pillar id");
            NetworkPacketLimits.writeUtf(buf, row.dimension, NetworkPacketLimits.MAX_RESOURCE_ID_LENGTH, "pillar dimension");
            buf.writeInt(row.x);
            buf.writeInt(row.y);
            buf.writeInt(row.z);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null || !player.hasPermissions(2)) return;
            PillarIdManager.get().editPillarRows(rows);
        });
        ctx.get().setPacketHandled(true);
    }

    public static class Row {
        public final String oldId;
        public final String newId;
        public final String dimension;
        public final int x, y, z;

        public Row(String oldId, String newId, String dimension, int x, int y, int z) {
            this.oldId = oldId;
            this.newId = newId;
            this.dimension = dimension;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public boolean isValid() {
            return oldId != null && oldId.matches(PillarIdManager.VALID_ID_PATTERN)
                    && newId != null && newId.matches(PillarIdManager.VALID_ID_PATTERN)
                    && dimension != null && dimension.length() <= NetworkPacketLimits.MAX_RESOURCE_ID_LENGTH
                    && ResourceLocation.tryParse(dimension) != null;
        }
    }
}
