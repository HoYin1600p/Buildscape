package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.item.BiomeBrushItem;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ClearBiomeBrushPacket implements CommonPacket {

    public static final CommonId ID = new CommonId("buildscape", "clear_biome_brush");

    public ClearBiomeBrushPacket() {}

    public ClearBiomeBrushPacket(FriendlyByteBuf buf) {}

    public static ClearBiomeBrushPacket decode(FriendlyByteBuf buf) {
        return new ClearBiomeBrushPacket(buf);
    }

    @Override
    public CommonId getId() {
        return ID;
    }

    @Override
    public PacketDirection getDirection() {
        return PacketDirection.CLIENT_TO_SERVER;
    }

    @Override
    public void write(FriendlyByteBuf buf) {}

    @Override
    public void handle(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        ItemStack stack = serverPlayer.getMainHandItem();
        if (stack.isEmpty() || !(stack.getItem() instanceof BiomeBrushItem)) {
            stack = serverPlayer.getOffhandItem();
        }

        if (!stack.isEmpty() && stack.getItem() instanceof BiomeBrushItem brush) {
            brush.clearCapturedBiome(stack, serverPlayer);
        }
    }
}
