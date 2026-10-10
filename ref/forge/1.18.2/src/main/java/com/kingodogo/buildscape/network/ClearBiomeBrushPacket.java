package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.item.BiomeBrushItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClearBiomeBrushPacket {

    public ClearBiomeBrushPacket() {}

    public static void encode(ClearBiomeBrushPacket msg, FriendlyByteBuf buf) {}

    public static ClearBiomeBrushPacket decode(FriendlyByteBuf buf) {
        return new ClearBiomeBrushPacket();
    }

    public static void handle(ClearBiomeBrushPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;

            ItemStack stack = player.getMainHandItem();
            if (stack.isEmpty() || !(stack.getItem() instanceof BiomeBrushItem)) {
                stack = player.getOffhandItem();
            }

            if (!stack.isEmpty() && stack.getItem() instanceof BiomeBrushItem brush) {
                brush.clearCapturedBiome(stack, player);
            }
        });
        ctx.setPacketHandled(true);
    }
}
