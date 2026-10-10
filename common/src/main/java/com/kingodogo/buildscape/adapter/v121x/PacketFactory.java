package com.kingodogo.buildscape.adapter.v121x;

import com.kingodogo.buildscape.network.CommonPacket;
import com.kingodogo.buildscape.network.IPacketFactory;
import com.kingodogo.buildscape.network.PacketDirection;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class PacketFactory implements IPacketFactory {

    private static volatile MinecraftServer currentServer;
    private final Map<ResourceLocation, RegisteredPacket<?>> registry = new ConcurrentHashMap<>();

    private record RegisteredPacket<T extends CommonPacket>(
            CommonId id,
            PacketDirection direction,
            Function<FriendlyByteBuf, T> decoder
    ) {}

    public static void setServer(MinecraftServer server) {
        currentServer = server;
    }

    public static MinecraftServer getServer() {
        return currentServer;
    }

    @Override
    public <T extends CommonPacket> void registerPacket(CommonId id, PacketDirection direction, Function<FriendlyByteBuf, T> decoder) {
        ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath());
        registry.put(loc, new RegisteredPacket<>(id, direction, decoder));
    }

    @Override
    public java.util.Collection<IPacketFactory.PacketDescriptor> getRegisteredDescriptors() {
        return registry.values().stream()
                .map(r -> new IPacketFactory.PacketDescriptor(r.id, r.direction))
                .toList();
    }

    @Override
    public void handleServerbound(CommonId id, FriendlyByteBuf buf, ServerPlayer player) {
        if (player == null) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Received serverbound packet {} with null player", id);
            return;
        }
        ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath());
        RegisteredPacket<?> reg = registry.get(loc);
        if (reg == null) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Received unknown serverbound packet {}", id);
            return;
        }
        if (reg.direction != PacketDirection.CLIENT_TO_SERVER) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Received packet {} on server but expected direction is {}", id, reg.direction);
            return;
        }
        try {
            CommonPacket pkt = reg.decoder.apply(buf);
            if (buf.readableBytes() > 0) {
                com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Packet {} had {} trailing unread bytes", id, buf.readableBytes());
            }
            MinecraftServer server = player.getServer();
            if (server != null) {
                server.execute(() -> pkt.handle(player));
            } else {
                pkt.handle(player);
            }
        } catch (Throwable t) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.error("Failed to decode or handle serverbound packet {}", id, t);
        }
    }

    @Override
    public void handleClientbound(CommonId id, FriendlyByteBuf buf, Player player) {
        ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath());
        RegisteredPacket<?> reg = registry.get(loc);
        if (reg == null) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Received unknown clientbound packet {}", id);
            return;
        }
        if (reg.direction != PacketDirection.SERVER_TO_CLIENT) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Received packet {} on client but expected direction is {}", id, reg.direction);
            return;
        }
        try {
            CommonPacket pkt = reg.decoder.apply(buf);
            if (buf.readableBytes() > 0) {
                com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Packet {} had {} trailing unread bytes", id, buf.readableBytes());
            }
            Minecraft mc = Minecraft.getInstance();
            mc.execute(() -> pkt.handle(player != null ? player : mc.player));
        } catch (Throwable t) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.error("Failed to decode or handle clientbound packet {}", id, t);
        }
    }

    public void handlePayload(ResourceLocation id, FriendlyByteBuf buf, Player player) {
        RegisteredPacket<?> reg = registry.get(id);
        if (reg != null) {
            if (reg.direction == PacketDirection.CLIENT_TO_SERVER && player instanceof ServerPlayer serverPlayer) {
                handleServerbound(reg.id, buf, serverPlayer);
            } else {
                handleClientbound(reg.id, buf, player);
            }
        }
    }

    public void handlePayload(ResourceLocation id, byte[] data, Player player) {
        FriendlyByteBuf buf = new FriendlyByteBuf(io.netty.buffer.Unpooled.wrappedBuffer(data));
        handlePayload(id, buf, player);
    }

    public static class BuildscapeCustomPayload implements CustomPacketPayload {
        private final CustomPacketPayload.Type<BuildscapeCustomPayload> type;
        private final byte[] data;

        public BuildscapeCustomPayload(ResourceLocation id, byte[] data) {
            this.type = new CustomPacketPayload.Type<>(id);
            this.data = data;
        }

        public BuildscapeCustomPayload(CustomPacketPayload.Type<BuildscapeCustomPayload> type, byte[] data) {
            this.type = type;
            this.data = data;
        }

        @Override
        public CustomPacketPayload.Type<BuildscapeCustomPayload> type() {
            return type;
        }

        public byte[] data() {
            return data;
        }

        public void write(FriendlyByteBuf buf) {
            buf.writeBytes(data);
        }

        public static BuildscapeCustomPayload read(CustomPacketPayload.Type<BuildscapeCustomPayload> type, FriendlyByteBuf buf) {
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            return new BuildscapeCustomPayload(type, bytes);
        }

        public static net.minecraft.network.codec.StreamCodec<FriendlyByteBuf, BuildscapeCustomPayload> codec(CustomPacketPayload.Type<BuildscapeCustomPayload> type) {
            return net.minecraft.network.codec.StreamCodec.of(
                    (buf, payload) -> payload.write(buf),
                    buf -> read(type, buf)
            );
        }
    }

    private BuildscapeCustomPayload createPayload(CommonPacket packet) {
        ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(packet.getId().getNamespace(), packet.getId().getPath());
        FriendlyByteBuf buf = new FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
        packet.write(buf);
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        return new BuildscapeCustomPayload(loc, bytes);
    }

    @Override
    public void sendToServer(CommonPacket packet) {
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc.getConnection() != null) {
                CustomPacketPayload payload = createPayload(packet);
                mc.getConnection().send(new ServerboundCustomPayloadPacket(payload));
            }
        } catch (Throwable t) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.error("Failed to send packet to server: {}", packet.getId(), t);
        }
    }

    @Override
    public void sendToPlayer(ServerPlayer player, CommonPacket packet) {
        if (player != null && player.connection != null) {
            try {
                CustomPacketPayload payload = createPayload(packet);
                player.connection.send(new ClientboundCustomPayloadPacket(payload));
            } catch (Throwable t) {
                com.kingodogo.buildscape.BuildscapeCommon.LOGGER.error("Failed to send packet to player: {}", packet.getId(), t);
            }
        }
    }

    @Override
    public void sendToAll(CommonPacket packet) {
        if (currentServer != null && currentServer.getPlayerList() != null) {
            for (ServerPlayer player : currentServer.getPlayerList().getPlayers()) {
                sendToPlayer(player, packet);
            }
            return;
        }

        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc.hasSingleplayerServer() && mc.getSingleplayerServer() != null) {
                for (ServerPlayer player : mc.getSingleplayerServer().getPlayerList().getPlayers()) {
                    sendToPlayer(player, packet);
                }
            }
        } catch (Throwable t) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.error("Failed to send packet to all players: {}", packet.getId(), t);
        }
    }

    @Override
    public void sendToTracking(Level level, BlockPos pos, CommonPacket packet) {
        if (level instanceof ServerLevel serverLevel) {
            double maxDistSq = 64.0 * 64.0;
            for (ServerPlayer player : serverLevel.players()) {
                if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= maxDistSq) {
                    sendToPlayer(player, packet);
                }
            }
        }
    }
}
