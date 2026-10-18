package com.kingodogo.buildscape.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class EditPillarRowsResultPacket {
    private final long batchId;
    private final List<String> oldIds;
    private final String reason;

    public EditPillarRowsResultPacket(long batchId, List<String> oldIds, String reason) {
        NetworkPacketLimits.checkCount(oldIds.size(), EditPillarRowsPacket.MAX_ROWS, "pillar edit result");
        this.batchId = batchId;
        this.oldIds = new ArrayList<>(oldIds);
        this.reason = reason;
    }

    public static EditPillarRowsResultPacket decode(FriendlyByteBuf buf) {
        long batchId = buf.readLong();
        int count = NetworkPacketLimits.readCount(buf, EditPillarRowsPacket.MAX_ROWS, "pillar edit result");
        List<String> ids = new ArrayList<>(count);
        for (int i = 0; i < count; i++) ids.add(NetworkPacketLimits.readUtf(buf, 32, "old pillar id"));
        return new EditPillarRowsResultPacket(batchId, ids, NetworkPacketLimits.readUtf(buf, 32, "edit result reason"));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeLong(batchId);
        NetworkPacketLimits.checkCount(oldIds.size(), EditPillarRowsPacket.MAX_ROWS, "pillar edit result");
        buf.writeInt(oldIds.size());
        for (String id : oldIds) NetworkPacketLimits.writeUtf(buf, id, 32, "old pillar id");
        NetworkPacketLimits.writeUtf(buf, reason, 32, "edit result reason");
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> applyClient()));
        ctx.get().setPacketHandled(true);
    }

    private void applyClient() {
        if (net.minecraft.client.Minecraft.getInstance().screen instanceof
                com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen screen
                && screen.getActiveTab() instanceof com.kingodogo.buildscape.client.screen.PillarIdsConfigTab tab) {
            tab.acceptEditResult(batchId, oldIds, reason);
        }
    }
}
