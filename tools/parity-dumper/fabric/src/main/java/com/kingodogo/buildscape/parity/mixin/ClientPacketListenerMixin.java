package com.kingodogo.buildscape.parity.mixin;

import com.kingodogo.buildscape.parity.ClientDump;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundUpdateTagsPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Refresh only after the received tags have actually been bound. */
@Mixin(ClientPacketListener.class)
abstract class ClientPacketListenerMixin {
    @Inject(method = "handleUpdateTags", at = @At("TAIL"))
    private void parityDumpTags(ClientboundUpdateTagsPacket packet, CallbackInfo callback) {
        ClientDump.requestSnapshot();
    }
}
