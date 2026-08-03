package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.block.BuildersWorkbenchBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Handles server-side actions for the Builders Workbench.
 * <p>
 * actionId:
 * 0 – Solve gradient from input slots 1-9, write results to slots 12-20
 * 1 – Copy solved gradient (slots 12-20) into the Output Pouch (slot 11)
 */
public class BuildersWorkbenchActionPacket {

    private final int actionId;
    private final BlockPos pos;
    private final int filterMask;

    public BuildersWorkbenchActionPacket(int actionId, BlockPos pos, int filterMask) {
        this.actionId = actionId;
        this.pos = pos;
        this.filterMask = filterMask;
    }

    public BuildersWorkbenchActionPacket(FriendlyByteBuf buf) {
        this.actionId = buf.readInt();
        this.pos = buf.readBlockPos();
        this.filterMask = buf.readInt();
    }

    public static BuildersWorkbenchActionPacket decode(FriendlyByteBuf buf) {
        return new BuildersWorkbenchActionPacket(buf);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(actionId);
        buf.writeBlockPos(pos);
        buf.writeInt(filterMask);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null || player.level == null) return;

            if (!(player.level.getBlockEntity(pos) instanceof BuildersWorkbenchBlockEntity wbe)) return;

            if (actionId == 0) {
                // ── Solve gradient ────────────────────────────────────────────
                wbe.updateGradientResults();
                player.level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE,
                        SoundSource.BLOCKS, 0.6f, 1.2f);

            } else if (actionId == 1) {
                // ── Instant copy to output pouch ──────────────────────────────
                ItemStack inPouch = wbe.getItem(BuildersWorkbenchBlockEntity.SLOT_INPUT_POUCH);
                ItemStack outPouch = wbe.getItem(BuildersWorkbenchBlockEntity.SLOT_OUTPUT_POUCH);
                if (!inPouch.isEmpty() && BuildersWorkbenchBlockEntity.isPouch(inPouch) && outPouch.isEmpty()) {
                    ItemStack pouchCopy = inPouch.copy();
                    wbe.writeSolvedToPouch(pouchCopy);
                    wbe.setItem(BuildersWorkbenchBlockEntity.SLOT_OUTPUT_POUCH, pouchCopy);
                    wbe.setItem(BuildersWorkbenchBlockEntity.SLOT_INPUT_POUCH, ItemStack.EMPTY);
                    wbe.setCopyProgress(0);

                    player.level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP,
                            SoundSource.BLOCKS, 0.5f, 1.0f);
                }
            } else if (actionId == 2) {
                // ── Set filter mask ───────────────────────────────────────────
                wbe.setFilterMask(filterMask);
            } else if (actionId == 3) {
                // ── Cycle result offset for a slot index ──────────────────────
                wbe.incrementResultOffset(filterMask);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
