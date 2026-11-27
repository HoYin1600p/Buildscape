package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.client.biome.BiomeTintRendering;
import com.kingodogo.buildscape.network.BiomeTintPayload;
import com.kingodogo.buildscape.network.SyncBiomeTintPacket;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SyncBiomeTintPacket.class)
abstract class BiomeTintPacketMixin {
    @Shadow(remap = false) public abstract BiomeTintPayload payload();

    @Inject(method = "handle", at = @At("TAIL"), remap = false)
    private void buildscape$refreshBiomeTints(Player player, CallbackInfo ci) {
        if (player != null && Services.PLATFORM.getEntityLevel(player) instanceof ClientLevel level
                && payload().dimension().equals(Services.PLATFORM.getDimensionId(level))) {
            BiomeTintRendering.rebuild(level, payload().chunkX(), payload().chunkZ());
        }
    }
}
