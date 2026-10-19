package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.config.ConfigSetting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Tells a client that the server refused its config-screen edit, so an open config screen can show the
 * refusal feedback it shows for locally refused clicks. The chat message and the resync are sent separately.
 */
public class EditRefusedPacket {
    public enum Reason {
        /** The edit policy locks this setting for the sender. */
        LOCKED,
        /** Changing the edit policy needs permission level 4. */
        POLICY_LEVEL,
        /** A config file on the server is read-only. */
        READ_ONLY,
        /** The upload held invalid or incomplete data. */
        INVALID
    }

    public final Reason reason;
    /** Ordinal of the refused {@link ConfigSetting}, or -1 when the refusal is not tied to one. */
    public final int settingId;

    public EditRefusedPacket(Reason reason, ConfigSetting setting) {
        this.reason = reason;
        this.settingId = setting == null ? -1 : setting.ordinal();
    }

    private EditRefusedPacket(Reason reason, int settingId) {
        this.reason = reason;
        this.settingId = settingId;
    }

    public ConfigSetting setting() {
        ConfigSetting[] all = ConfigSetting.values();
        return settingId >= 0 && settingId < all.length ? all[settingId] : null;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeEnum(reason);
        buf.writeVarInt(settingId + 1);
    }

    public static EditRefusedPacket decode(FriendlyByteBuf buf) {
        Reason reason = buf.readEnum(Reason.class);
        return new EditRefusedPacket(reason, buf.readVarInt() - 1);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.handle(this)));
        ctx.get().setPacketHandled(true);
    }

    private static final class ClientHandler {
        private static void handle(EditRefusedPacket packet) {
            net.minecraft.client.gui.screens.Screen screen = net.minecraft.client.Minecraft.getInstance().screen;
            if (screen instanceof com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen open) {
                open.onServerRefusal(packet.reason, packet.setting());
            }
        }
    }
}
