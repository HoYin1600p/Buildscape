package com.kingodogo.buildscape.network;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
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
        ctx
                .get()
                .enqueueWork(() -> {
                    DistExecutor.unsafeRunWhenOn(
                            Dist.CLIENT,
                            () ->
                                    () -> {
                                        com.kingodogo.buildscape.client.ClientEvents.setOverlayMessage(
                                                message
                                        );
                                    }
                    );
                });
        ctx.get().setPacketHandled(true);
    }
}
