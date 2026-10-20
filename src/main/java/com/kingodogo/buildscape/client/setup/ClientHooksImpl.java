package com.kingodogo.buildscape.client.setup;

import com.kingodogo.buildscape.client.feature.hazebush.HazeBushParticleEvents;
import com.kingodogo.buildscape.client.feature.muff.MuffBlockManager;
import com.kingodogo.buildscape.client.network.ClientConfigHooks;
import com.kingodogo.buildscape.client.particle.TintedDripParticle;
import com.kingodogo.buildscape.core.ClientHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

/** The client's {@link ClientHooks}; only ever loaded through {@code DistExecutor} on {@code Dist.CLIENT}. */
@OnlyIn(Dist.CLIENT)
public final class ClientHooksImpl implements ClientHooks {

    public static void install() {
        ClientHooks.install(new ClientHooksImpl());
    }

    @Override
    public void registerTintedDripColor(double x, double y, double z, String colorCode) {
        TintedDripParticle.registerColorForPosition(x, y, z, colorCode);
    }

    @Override
    public void registerMuffBlock(BlockPos pos) {
        MuffBlockManager.register(pos);
    }

    @Override
    public void unregisterMuffBlock(BlockPos pos) {
        MuffBlockManager.unregister(pos);
    }

    @Override
    public void hazeBushAnimateTick(Level level, BlockPos pos, Random random, DyeColor color) {
        HazeBushParticleEvents.animateTick(level, pos, random, color);
    }

    @Override
    public boolean isConnectedToServer() {
        return ClientConfigHooks.isConnected();
    }

    @Override
    public boolean canUploadConfig() {
        return ClientConfigHooks.canUploadConfig();
    }

    @Override
    public boolean hasLocalServer() {
        return ClientConfigHooks.hasLocalServer();
    }
}
