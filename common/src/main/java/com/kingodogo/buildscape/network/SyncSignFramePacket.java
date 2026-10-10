package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.cosmetic.sign.SignFrameAttachment;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;

public class SyncSignFramePacket implements CommonPacket {

    public static final CommonId ID = new CommonId("buildscape", "sync_sign_frame");

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

    public static SyncSignFramePacket decode(FriendlyByteBuf buf) {
        return new SyncSignFramePacket(buf);
    }

    @Override
    public CommonId getId() {
        return ID;
    }

    @Override
    public PacketDirection getDirection() {
        return PacketDirection.SERVER_TO_CLIENT;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        NetworkPacketLimits.writeUtf(buf, this.frameId,
                NetworkPacketLimits.MAX_FRAME_ID_LENGTH, "sign frame id");
    }

    @Override
    public void handle(Player player) {
        if (player != null) {
            net.minecraft.world.level.Level level = Services.PLATFORM.getEntityLevel(player);
            if (level != null) {
                BlockEntity be = level.getBlockEntity(this.pos);
                if (be instanceof SignBlockEntity sign) {
                    CompoundTag persistentData = Services.PLATFORM.getBlockEntityData(sign);
                    if (this.frameId.isEmpty()) {
                        persistentData.remove(SignFrameAttachment.NBT_KEY);
                    } else {
                        persistentData.putString(SignFrameAttachment.NBT_KEY, this.frameId);
                    }
                }
            }
        }
    }

    public BlockPos getPos() {
        return pos;
    }

    public String getFrameId() {
        return frameId;
    }
}
