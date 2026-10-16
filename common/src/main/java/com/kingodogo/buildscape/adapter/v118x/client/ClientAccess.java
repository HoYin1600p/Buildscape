package com.kingodogo.buildscape.adapter.v118x.client;

import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/**
 * Client-only helpers whose bodies assign client subtypes (LocalPlayer, DynamicTexture) to
 * common supertypes. The bytecode verifier loads such types, so they must not live in a class
 * that the dedicated server loads (PlatformAdapterBase).
 */
public final class ClientAccess {
    private ClientAccess() {}

    public static Player player() {
        return Minecraft.getInstance().player;
    }

    public static void handleOnClientThread(com.kingodogo.buildscape.network.CommonPacket packet, Player player) {
        Minecraft mc = Minecraft.getInstance();
        mc.execute(() -> packet.handle(player != null ? player : mc.player));
    }

    public static CommonId registerDynamicTexture(String name, DynamicTexture texture) {
        ResourceLocation id = Minecraft.getInstance().getTextureManager().register(name, texture);
        return CommonId.of(id.getNamespace(), id.getPath());
    }

    public static CommonId registerExternalTexture(String name, java.io.InputStream stream) {
        try {
            com.mojang.blaze3d.platform.NativeImage image = com.mojang.blaze3d.platform.NativeImage.read(stream);
            DynamicTexture texture = new DynamicTexture(image);
            texture.upload();
            return registerDynamicTexture(name, texture);
        } catch (Throwable t) {
            return null;
        }
    }
}
