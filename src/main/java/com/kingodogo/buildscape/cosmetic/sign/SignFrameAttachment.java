package com.kingodogo.buildscape.cosmetic.sign;

import com.kingodogo.buildscape.network.ModMessages;
import com.kingodogo.buildscape.network.SyncSignFramePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

/**
 * Manages storing, querying, and updating cosmetic frame attachments on signs.
 * Uses Forge's BlockEntity persistent data to ensure survival through saves, unloads, and reloads.
 */
public class SignFrameAttachment {

    public static final String NBT_KEY = "BuildscapeSignFrame";

    /**
     * Checks whether the given blockstate and block entity represent a valid sign.
     * Generically detects any vanilla or modded standing/wall sign.
     */
    public static boolean isValidSign(BlockState state, @Nullable BlockEntity be) {
        return state.getBlock() instanceof SignBlock && be instanceof SignBlockEntity;
    }

    public static boolean isValidSign(BlockGetter level, BlockPos pos) {
        return isValidSign(level.getBlockState(pos), level.getBlockEntity(pos));
    }

    /**
     * Retrieves the attached frame type from a sign block entity.
     */
    public static SignFrameType getFrame(@Nullable SignBlockEntity sign) {
        if (sign == null) {
            return SignFrameType.NONE;
        }
        CompoundTag persistentData = sign.getTileData();
        if (persistentData.contains(NBT_KEY)) {
            return SignFrameType.fromId(persistentData.getString(NBT_KEY));
        }
        return SignFrameType.NONE;
    }

    /**
     * Checks if the sign has any cosmetic frame attached.
     */
    public static boolean hasFrame(@Nullable SignBlockEntity sign) {
        return getFrame(sign) != SignFrameType.NONE;
    }

    /**
     * Sets or removes the frame on a sign block entity, persisting data and syncing to clients.
     */
    public static void setFrame(SignBlockEntity sign, SignFrameType frame) {
        CompoundTag persistentData = sign.getTileData();
        if (frame == SignFrameType.NONE) {
            persistentData.remove(NBT_KEY);
        } else {
            persistentData.putString(NBT_KEY, frame.getId());
        }
        sign.setChanged();

        if (sign.getLevel() != null && !sign.getLevel().isClientSide() && sign.getLevel() instanceof ServerLevel serverLevel) {
            BlockPos pos = sign.getBlockPos();
            BlockState state = sign.getBlockState();
            serverLevel.sendBlockUpdated(pos, state, state, 3);
            ModMessages.INSTANCE.send(
                    PacketDistributor.TRACKING_CHUNK.with(() -> serverLevel.getChunkAt(pos)),
                    new SyncSignFramePacket(pos, frame.getId())
            );
        }
    }
}
