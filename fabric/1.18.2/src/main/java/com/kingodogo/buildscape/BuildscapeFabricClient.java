package com.kingodogo.buildscape;

import com.kingodogo.buildscape.client.ClientEvents;
import com.kingodogo.buildscape.network.IPacketFactory;
import com.kingodogo.buildscape.network.PacketDirection;
import com.kingodogo.buildscape.network.PacketFactory;
import com.kingodogo.buildscape.platform.Services;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class BuildscapeFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Services.PLATFORM.registerMenuScreens();
        com.kingodogo.buildscape.particle.ParticleFactory.registerProviders();
        ClientEvents.initializeConfigCallback();

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
                            ClientEvents.onClientTick();
                            return null;
                        }
                );
                register.invoke(endTickEvent, proxy);
            }
        } catch (Throwable t) {
            BuildscapeCommon.LOGGER.warn("Failed to register Fabric 1.18.2 client tick: {}", t.getMessage());
        }
    }

    private void registerClientNetworking() {
        try {
            Class<?> clientNetworking = Class.forName("net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking");
            Class<?> handlerType = Class.forName("net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking$PlayChannelHandler");
            java.lang.reflect.Method registerMethod = java.util.Arrays.stream(clientNetworking.getMethods())
                    .filter(m -> m.getName().equals("registerGlobalReceiver") && m.getParameterCount() == 2)
                    .findFirst().orElse(null);
            if (registerMethod == null) return;

            Class<?> idClass = registerMethod.getParameterTypes()[0];
            java.lang.reflect.Constructor<?> idCtor = idClass.getConstructor(String.class, String.class);

            for (IPacketFactory.PacketDescriptor desc : PacketFactory.getRegisteredDescriptors()) {
                if (desc.direction() == PacketDirection.SERVER_TO_CLIENT) {
                    Object idObj = idCtor.newInstance(desc.id().getNamespace(), desc.id().getPath());
                    Object handlerProxy = java.lang.reflect.Proxy.newProxyInstance(
                            handlerType.getClassLoader(),
                            new Class<?>[]{handlerType},
                            (proxy, method, args) -> {
                                if (method.getName().equals("receive") && args != null && args.length >= 3) {
                                    Object clientObj = args[0];
                                    Object bufObj = args[2];
                                    net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
                                    if (bufObj instanceof net.minecraft.network.FriendlyByteBuf friendlyBuf) {
                                        PacketFactory.handleClientbound(desc.id(), friendlyBuf, mc.player);
                                    } else if (bufObj instanceof io.netty.buffer.ByteBuf rawBuf) {
                                        PacketFactory.handleClientbound(desc.id(), new net.minecraft.network.FriendlyByteBuf(rawBuf), mc.player);
                                    }
                                }
                                return null;
                            }
                    );
                    registerMethod.invoke(null, idObj, handlerProxy);
                }
            }
        } catch (Throwable t) {
            BuildscapeCommon.LOGGER.warn("Failed to register Fabric 1.18.2 client networking: {}", t.getMessage());
        }
    }
}
