package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class ActionBarMessagePacket implements CommonPacket {

    public static final CommonId ID = new CommonId("buildscape", "action_bar_message");

    private final Component message;

    public ActionBarMessagePacket(Component message) {
        this.message = message;
    }

    public ActionBarMessagePacket(FriendlyByteBuf buf) {
        this.message = Services.PLATFORM.literal(buf.readUtf(32767));
    }

    public static ActionBarMessagePacket decode(FriendlyByteBuf buf) {
        return new ActionBarMessagePacket(buf);
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
        buf.writeUtf(message != null ? message.getString() : "", 32767);
    }

    @Override
    public void handle(Player player) {
        if (player != null) {
            Services.PLATFORM.sendActionBarMessage(player, message);
        }
    }

    public Component getMessage() {
        return message;
    }
}
