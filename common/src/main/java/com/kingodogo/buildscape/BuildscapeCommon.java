package com.kingodogo.buildscape;

import com.kingodogo.buildscape.block.ModBlockEntities;
import com.kingodogo.buildscape.block.ModBlocks;
import com.kingodogo.buildscape.entity.ModEntities;
import com.kingodogo.buildscape.item.ModItems;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.sound.ModSounds;
import com.kingodogo.buildscape.stat.ModStats;

public class BuildscapeCommon {
    public static final String MOD_ID = "buildscape";

    public static class ModLogger {
        public void info(String msg, Object... args) { System.out.println("[Buildscape] INFO: " + format(msg, args)); }
        public void warn(String msg, Object... args) { System.out.println("[Buildscape] WARN: " + format(msg, args)); }
        public void error(String msg, Object... args) { System.err.println("[Buildscape] ERROR: " + format(msg, args)); }
        public void debug(String msg, Object... args) { }
        private String format(String msg, Object... args) {
            if (args == null || args.length == 0) return msg;
            for (Object arg : args) {
                if (arg instanceof Throwable t) {
                    msg = msg.replaceFirst("\\{\\}", String.valueOf(t.getMessage()));
                } else {
                    msg = msg.replaceFirst("\\{\\}", String.valueOf(arg));
                }
            }
            return msg;
        }
    }

    public static final ModLogger LOGGER = new ModLogger();

    public static void logError(String message) {
        System.err.println("[Buildscape] ERROR: " + message);
    }

    public static void logError(String message, Throwable throwable) {
        logError(message + ": " + throwable.getMessage());
    }

    public static void logWarning(String message) {
        System.err.println("[Buildscape] WARN: " + message);
    }

    private static volatile boolean serverFullyInitialized = false;

    public static boolean isServerFullyInitialized() {
        return serverFullyInitialized;
    }

    public static void setServerFullyInitialized(boolean initialized) {
        serverFullyInitialized = initialized;
    }

    public static void init() {
        System.out.println("Initializing Buildscape Core on platform: " + Services.PLATFORM.getPlatformName());
        ModBlocks.init();
        ModItems.init();
        com.kingodogo.buildscape.trophy.Trophies.init();
        ModBlockEntities.init();
        ModEntities.init();
        ModSounds.init();
        com.kingodogo.buildscape.particle.ModParticles.init();
        Services.PLATFORM.registerWorldGen();
        Services.PLATFORM.registerRecipeSerializers();
        Services.REGISTRY.init();
        ModStats.registerStats();
        com.kingodogo.buildscape.block.CopperOxidationHandler.init();
        com.kingodogo.buildscape.network.ModPackets.registerAll();
        Services.PLATFORM.registerCommonLifecycleInteractions();
    }

    public static void main(String[] args) {
        init();
    }
}
