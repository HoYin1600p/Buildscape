package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.cosmetic.sign.SignFrameAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

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
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
                if (mc.level != null) {
                    BlockEntity be = mc.level.getBlockEntity(this.pos);
                    if (be instanceof SignBlockEntity sign) {
                        if (this.frameId.isEmpty()) {
                            sign.getTileData().remove(SignFrameAttachment.NBT_KEY);
                        } else {
                            sign.getTileData().putString(SignFrameAttachment.NBT_KEY, this.frameId);
                        }
                    }
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
