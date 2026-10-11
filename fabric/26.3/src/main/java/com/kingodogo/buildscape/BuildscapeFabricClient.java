package com.kingodogo.buildscape;

import net.fabricmc.api.ClientModInitializer;

public class BuildscapeFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        com.kingodogo.buildscape.platform.Services.PLATFORM.registerMenuScreens();
        com.kingodogo.buildscape.particle.ParticleFactory.registerProviders();
        com.kingodogo.buildscape.client.ClientEvents.initializeConfigCallback();

        registerClientTick();
        registerClientNetworking();
    }

    private void registerClientTick() {
        try {
            Class<?> tickEvents = Class.forName("net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents");
            Object endTickEvent = tickEvents.getField("END_CLIENT_TICK").get(null);
            java.lang.reflect.Method register = java.util.Arrays.stream(endTickEvent.getClass().getMethods())
                    .filter(m -> m.getName().equals("register") && m.getParameterCount() == 1)
                    .findFirst().orElse(null);
            if (register != null) {
                Class<?> listenerClass = register.getParameterTypes()[0];
                Object proxy = java.lang.reflect.Proxy.newProxyInstance(
                        listenerClass.getClassLoader(),
                        new Class<?>[]{listenerClass},
                        (p, method, args) -> {
                            com.kingodogo.buildscape.client.ClientEvents.onClientTick();
                            return null;
                        }
                );
                register.invoke(endTickEvent, proxy);
            }
        } catch (Throwable t) {
            BuildscapeCommon.LOGGER.debug("Fabric 26.3 client tick registration deferred: {}", t.getMessage());
        }
    }

    private void registerClientNetworking() {
        try {
            Class<?> clientNetworking = Class.forName("net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking");
            Class<?> clientReceiverClass = Class.forName("net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking$PlayPayloadHandler");
            java.lang.reflect.Method registerClientReceiver = clientNetworking.getMethod("registerGlobalReceiver",
                    net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type.class,
                    clientReceiverClass);

            for (com.kingodogo.buildscape.network.IPacketFactory.PacketDescriptor desc : com.kingodogo.buildscape.network.PacketFactory.getRegisteredDescriptors()) {
                if (desc.direction() == com.kingodogo.buildscape.network.PacketDirection.SERVER_TO_CLIENT) {
                    net.minecraft.resources.Identifier loc = net.minecraft.resources.Identifier.fromNamespaceAndPath(desc.id().getNamespace(), desc.id().getPath());
                    net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<com.kingodogo.buildscape.adapter.v26x.PacketFactory.BuildscapeCustomPayload> type =
                            new net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<>(loc);

                    Object handler = java.lang.reflect.Proxy.newProxyInstance(
                            clientReceiverClass.getClassLoader(),
                            new Class<?>[]{clientReceiverClass},
                            (proxy, method, args) -> {
                                if (method.getName().equals("receive") && args != null && args.length >= 2) {
                                    com.kingodogo.buildscape.adapter.v26x.PacketFactory.BuildscapeCustomPayload payload =
                                            (com.kingodogo.buildscape.adapter.v26x.PacketFactory.BuildscapeCustomPayload) args[0];
                                    com.kingodogo.buildscape.network.PacketFactory.handleClientbound(desc.id(), payload.data(), net.minecraft.client.Minecraft.getInstance().player);
                                }
                                return null;
                            }
                    );
                    registerClientReceiver.invoke(null, type, handler);
                }
            }
        } catch (Throwable t) {
            BuildscapeCommon.LOGGER.debug("Fabric 26.3 client networking registration deferred: {}", t.getMessage());
        }
    }
}
