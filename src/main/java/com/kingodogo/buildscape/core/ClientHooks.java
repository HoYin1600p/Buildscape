package com.kingodogo.buildscape.core;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;

import java.util.Random;

/**
 * Client-only calls that shared code (blocks, config) needs, behind one interface so that common packages never
 * import {@code client} classes.
 *
 * <p>The client installs its implementation from the mod constructor on {@code Dist.CLIENT}; a dedicated server keeps
 * {@link #NONE}. Callers still guard each call with the same client check as before, so {@code NONE} is only a safe
 * default and never the behaviour a client sees.
 */
public interface ClientHooks {
    ClientHooks NONE = new ClientHooks() {
    };

    /** The installed hooks; {@link #NONE} until the client installs its own. */
    static ClientHooks get() {
        return Holder.current;
    }

    /** Called once by the client during mod construction. */
    static void install(ClientHooks hooks) {
        Holder.current = hooks;
    }

    default void registerTintedDripColor(double x, double y, double z, String colorCode) {}

    default void registerMuffBlock(BlockPos pos) {}

    default void unregisterMuffBlock(BlockPos pos) {}

    default void hazeBushAnimateTick(Level level, BlockPos pos, Random random, DyeColor color) {}

    default boolean isConnectedToServer() {
        return false;
    }

    default boolean canUploadConfig() {
        return false;
    }

    default boolean hasLocalServer() {
        return false;
    }

    /** Holds the installed hooks; a nested class because interface fields cannot be reassigned. */
    final class Holder {
        private static volatile ClientHooks current = NONE;

        private Holder() {}
    }
}
