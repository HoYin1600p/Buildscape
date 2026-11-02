package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen;
import com.kingodogo.buildscape.config.ConfigSetting;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public final class EditRefusedPacket implements CommonPacket {
    public static final CommonId ID = new CommonId("buildscape", "edit_refused");
    private final ConfigSetting setting;

    public EditRefusedPacket(ConfigSetting setting) { this.setting = setting; }

    public static EditRefusedPacket decode(FriendlyByteBuf buf) {
        int id = buf.readVarInt() - 1;
        ConfigSetting[] settings = ConfigSetting.values();
        return new EditRefusedPacket(id >= 0 && id < settings.length ? settings[id] : null);
    }

    @Override public CommonId getId() { return ID; }
    @Override public PacketDirection getDirection() { return PacketDirection.SERVER_TO_CLIENT; }
    @Override public void write(FriendlyByteBuf buf) { buf.writeVarInt(setting == null ? 0 : setting.ordinal() + 1); }
    @Override public void handle(Player player) { BuildScapeConfigScreen.onServerRefusal(setting); }
}
