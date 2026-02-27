package com.kingodogo.buildscape.network;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraft.client.Minecraft;
import net.minecraftforge.network.NetworkEvent;

public class ActionBarMessagePacket {

    private final Component message;

    public ActionBarMessagePacket(Component message) {
        this.message = message;
    }

    public ActionBarMessagePacket(FriendlyByteBuf buf) {
        this.message = buf.readComponent();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeComponent(message);
    }

    public static ActionBarMessagePacket decode(FriendlyByteBuf buf) {
        return new ActionBarMessagePacket(buf);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleActionBarMessage(this));
        });
        ctx.get().setPacketHandled(true);
    }

    private static class ClientPacketHandler {
        private static void handleActionBarMessage(ActionBarMessagePacket packet) {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.displayClientMessage(packet.message, true);
            }
        }
    }
}
