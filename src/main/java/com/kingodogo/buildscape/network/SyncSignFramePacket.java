package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.client.network.ClientPacketHandlers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Server-to-client packet that sets the frame id of the sign at a position on the client.
 * An empty id means no frame.
 */
public class SyncSignFramePacket {

    private final BlockPos pos;
    private final String frameId;

    public SyncSignFramePacket(BlockPos pos, String frameId) {
        this.pos = pos;
        this.frameId = frameId != null ? frameId : "";
    }

    public SyncSignFramePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.frameId = NetworkPacketLimits.readUtf(buf, NetworkPacketLimits.MAX_FRAME_ID_LENGTH, "sign frame id");
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        NetworkPacketLimits.writeUtf(buf, this.frameId,
                NetworkPacketLimits.MAX_FRAME_ID_LENGTH, "sign frame id");
    }

    public static SyncSignFramePacket decode(FriendlyByteBuf buf) {
        return new SyncSignFramePacket(buf);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> ClientPacketHandlers.applySignFrame(this.pos, this.frameId)));
        ctx.get().setPacketHandled(true);
    }
}
