package com.kingodogo.buildscape;

import com.kingodogo.buildscape.adapter.v121x.PacketFactory.BuildscapeCustomPayload;
import com.kingodogo.buildscape.network.PacketDirection;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Client bindings stay behind Fabric's client entrypoint. */
public final class BuildscapeFabricClient implements ClientModInitializer {
    @Override public void onInitializeClient() {
        com.kingodogo.buildscape.platform.Services.PLATFORM.registerMenuScreens();
        com.kingodogo.buildscape.platform.Services.PLATFORM.registerParticleProviders();
        com.kingodogo.buildscape.client.ClientEvents.initializeConfigCallback();
        ClientTickEvents.END_CLIENT_TICK.register(client -> com.kingodogo.buildscape.client.ClientEvents.onClientTick());
        for (var descriptor : com.kingodogo.buildscape.network.PacketFactory.getRegisteredDescriptors()) {
            if (descriptor.direction() != PacketDirection.SERVER_TO_CLIENT) continue;
            var type = new CustomPacketPayload.Type<BuildscapeCustomPayload>(ResourceLocation.fromNamespaceAndPath(
                    descriptor.id().getNamespace(), descriptor.id().getPath()));
            ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) ->
                    com.kingodogo.buildscape.network.PacketFactory.handleClientbound(
                            descriptor.id(), payload.data(), context.player()));
        }
    }
}