package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.util.CommonId;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import java.util.function.Function;

/** Forge transport; descriptors and decoders remain owned by the shared packet catalog. */
public final class ForgePacketFactory implements IPacketFactory {
    private record Registered(PacketDescriptor descriptor, Function<FriendlyByteBuf, ? extends CommonPacket> decoder) {}
    private final Map<CommonId, Registered> packets = new LinkedHashMap<>();
    private final Map<CommonId, Class<? extends CommonPacket>> types = new LinkedHashMap<>();
    private SimpleChannel channel;

    @Override
    public <T extends CommonPacket> void registerPacket(CommonId id, PacketDirection direction, Function<FriendlyByteBuf, T> decoder) {
        if (channel != null || packets.putIfAbsent(id, new Registered(new PacketDescriptor(id, direction), decoder)) != null) {
            throw new IllegalStateException("Duplicate or late packet registration: " + id);
        }
    }

    @Override public Collection<PacketDescriptor> getRegisteredDescriptors() {
        return packets.values().stream().map(Registered::descriptor).toList();
    }

    public void installChannel() {
        if (channel != null) throw new IllegalStateException("Forge channel already installed");
        types.put(ActionBarMessagePacket.ID, ActionBarMessagePacket.class);
        types.put(SyncConfigPacket.ID, SyncConfigPacket.class);
        types.put(TreeChopPacket.ID, TreeChopPacket.class);
        types.put(UpdatePillarDataPacket.ID, UpdatePillarDataPacket.class);
        types.put(SyncPillarIdsPacket.ID, SyncPillarIdsPacket.class);
        types.put(RequestPillarIdsPacket.ID, RequestPillarIdsPacket.class);
        types.put(UpdateConfigPacket.ID, UpdateConfigPacket.class);
        types.put(RemovePillarPacket.ID, RemovePillarPacket.class);
        types.put(UpdateAllPillarIdsPacket.ID, UpdateAllPillarIdsPacket.class);
        types.put(SyncGameRulesPacket.ID, SyncGameRulesPacket.class);
        types.put(UpdateGameRulePacket.ID, UpdateGameRulePacket.class);
        types.put(SyncHomemakerCooldownPacket.ID, SyncHomemakerCooldownPacket.class);
        types.put(RotateBlockPacket.ID, RotateBlockPacket.class);
        types.put(HammerReplacePacket.ID, HammerReplacePacket.class);
        types.put(BuildersWorkbenchResultsPacket.ID, BuildersWorkbenchResultsPacket.class);
        types.put(ClearBiomeBrushPacket.ID, ClearBiomeBrushPacket.class);
        types.put(SyncSignFramePacket.ID, SyncSignFramePacket.class);
        types.put(ConfettiBurstPacket.ID, ConfettiBurstPacket.class);
        if (!types.keySet().equals(packets.keySet())) throw new IllegalStateException("Forge packet types differ from shared descriptors");
        channel = ChannelBuilder.named(Identifier.fromNamespaceAndPath(BuildscapeCommon.MOD_ID, "main"))
                .networkProtocolVersion(1).simpleChannel();
        int discriminator = 1;
        for (PacketDescriptor descriptor : PacketFactory.getRegisteredDescriptors()) {
            bind(discriminator++, descriptor, types.get(descriptor.id()));
        }
        channel.build();
    }

    private <T extends CommonPacket> void bind(int discriminator, PacketDescriptor descriptor, Class<T> type) {
        var direction = descriptor.direction() == PacketDirection.CLIENT_TO_SERVER
                ? NetworkDirection.PLAY_TO_SERVER : NetworkDirection.PLAY_TO_CLIENT;
        channel.messageBuilder(type, discriminator, direction)
                .encoder(CommonPacket::write)
                .decoder(buffer -> type.cast(decode(descriptor.id(), buffer)))
                .consumerMainThread((packet, context) -> {
                    if (descriptor.direction() == PacketDirection.CLIENT_TO_SERVER) {
                        if (context.getSender() != null) packet.handle(context.getSender());
                    } else if (FMLEnvironment.dist == Dist.CLIENT) {
                        com.kingodogo.buildscape.adapter.v263.client.ClientPacketHooks.handle(packet, null);
                    }
                }).add();
    }

    private CommonPacket decode(CommonId id, FriendlyByteBuf buffer) {
        Registered registered = packets.get(id);
        if (registered == null) throw new IllegalArgumentException("Unknown packet: " + id);
        CommonPacket packet = registered.decoder().apply(buffer);
        if (!id.equals(packet.getId()) || packet.getDirection() != registered.descriptor().direction()
                || buffer.isReadable()) throw new IllegalArgumentException("Invalid packet payload: " + id);
        return packet;
    }

    private void requireDirection(CommonId id, PacketDirection direction) {
        Registered registered = packets.get(id);
        if (registered == null || registered.descriptor().direction() != direction) {
            throw new IllegalArgumentException("Invalid packet direction: " + id);
        }
    }

    @Override public void handleServerbound(CommonId id, FriendlyByteBuf buffer, ServerPlayer player) {
        requireDirection(id, PacketDirection.CLIENT_TO_SERVER);
        if (player == null) throw new IllegalArgumentException("Missing packet sender");
        CommonPacket packet = decode(id, buffer);
        player.level().getServer().execute(() -> packet.handle(player));
    }
    @Override public void handleClientbound(CommonId id, FriendlyByteBuf buffer, Player player) {
        requireDirection(id, PacketDirection.SERVER_TO_CLIENT);
        CommonPacket packet = decode(id, buffer);
        if (FMLEnvironment.dist == Dist.CLIENT) com.kingodogo.buildscape.adapter.v263.client.ClientPacketHooks.handle(packet, player);
    }
    @Override public void handleServerbound(CommonId id, byte[] data, ServerPlayer player) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.wrappedBuffer(data));
        try { handleServerbound(id, buffer, player); } finally { buffer.release(); }
    }
    @Override public void handleClientbound(CommonId id, byte[] data, Player player) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.wrappedBuffer(data));
        try { handleClientbound(id, buffer, player); } finally { buffer.release(); }
    }
    @Override public void sendToServer(CommonPacket packet) {
        requireDirection(packet.getId(), PacketDirection.CLIENT_TO_SERVER);
        channel.send(packet, PacketDistributor.SERVER.noArg());
    }
    @Override public void sendToPlayer(ServerPlayer player, CommonPacket packet) {
        requireDirection(packet.getId(), PacketDirection.SERVER_TO_CLIENT);
        if (player != null) channel.send(packet, PacketDistributor.PLAYER.with(player));
    }
    @Override public void sendToAll(CommonPacket packet) {
        requireDirection(packet.getId(), PacketDirection.SERVER_TO_CLIENT);
        if (net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer() != null) {
            channel.send(packet, PacketDistributor.ALL.noArg());
        }
    }
    @Override public void sendToTracking(Level level, BlockPos pos, CommonPacket packet) {
        if (level instanceof ServerLevel serverLevel) {
            for (ServerPlayer player : serverLevel.players()) {
                if (player.distanceToSqr(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5) <= 64D * 64D) {
                    sendToPlayer(player, packet);
                }
            }
        }
    }
}

