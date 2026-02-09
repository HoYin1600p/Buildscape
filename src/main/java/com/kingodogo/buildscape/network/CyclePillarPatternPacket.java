package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.PillarBlockEntity;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class CyclePillarPatternPacket {

    private final BlockPos pillarPos;

    public CyclePillarPatternPacket(BlockPos pillarPos) {
        this.pillarPos = pillarPos;
    }

    public CyclePillarPatternPacket(FriendlyByteBuf buf) {
        this.pillarPos = buf.readBlockPos();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pillarPos);
    }

    public static CyclePillarPatternPacket decode(FriendlyByteBuf buf) {
        return new CyclePillarPatternPacket(buf);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx
                .get()
                .enqueueWork(() -> {
                    ServerPlayer player = ctx.get().getSender();
                    if (player == null || player.level == null) return;

                    BlockEntity be = player.level.getBlockEntity(pillarPos);

                    if (!(be instanceof PillarBlockEntity pillar)) {
                        player.displayClientMessage(
                                new net.minecraft.network.chat.TextComponent("Not a valid pillar!"),
                                false
                        );
                        return;
                    }

                    if (!pillar.hasDisplayItem()) {
                        player.displayClientMessage(
                                new net.minecraft.network.chat.TextComponent("Pillar has no item!"),
                                false
                        );
                        return;
                    }

                    pillar.cycleParticlePattern();
                    String pattern = pillar.getParticlePattern();
                    if (pattern == null) {
                        pattern = com.kingodogo.buildscape.config.PillarParticleConfig.get()
                                .pattern;
                    }

                    String capitalizedPattern =
                            pattern.substring(0, 1).toUpperCase() + pattern.substring(1);

                    net.minecraft.ChatFormatting color = getPatternColor(pattern);

                    net.minecraft.network.chat.MutableComponent patternText =
                            new net.minecraft.network.chat.TextComponent(
                                    capitalizedPattern
                            ).withStyle(color);
                    ModMessages.INSTANCE.send(
                            net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player
                            ),
                            new ActionBarMessagePacket(patternText)
                    );

                    player.level.playSound(
                            null,
                            pillarPos,
                            net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK,
                            net.minecraft.sounds.SoundSource.BLOCKS,
                            0.5f,
                            1.0f
                    );
                });
        ctx.get().setPacketHandled(true);
    }

    private static net.minecraft.ChatFormatting getPatternColor(String pattern) {
        switch (pattern) {
            case "default":
                return net.minecraft.ChatFormatting.WHITE;
            case "beam":
                return net.minecraft.ChatFormatting.YELLOW;
            case "spiral":
                return net.minecraft.ChatFormatting.AQUA;
            case "fountain":
                return net.minecraft.ChatFormatting.BLUE;
            case "pulse":
                return net.minecraft.ChatFormatting.LIGHT_PURPLE;
            case "ring":
                return net.minecraft.ChatFormatting.GREEN;
            case "burst":
                return net.minecraft.ChatFormatting.RED;
            default:
                return net.minecraft.ChatFormatting.GRAY;
        }
    }
}
