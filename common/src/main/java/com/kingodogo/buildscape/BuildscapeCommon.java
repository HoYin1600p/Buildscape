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
        private final org.slf4j.Logger delegate = org.slf4j.LoggerFactory.getLogger(MOD_ID);
        public void info(String msg, Object... args) { delegate.info(msg, args); }
        public void warn(String msg, Object... args) { delegate.warn(msg, args); }
        public void error(String msg, Object... args) { delegate.error(msg, args); }
        public void debug(String msg, Object... args) { delegate.debug(msg, args); }
    }

    public static final ModLogger LOGGER = new ModLogger();

    public static void logError(String message) {
        LOGGER.error(message);
    }

    public static void logError(String message, Throwable throwable) {
        LOGGER.error(message, throwable);
    }

    public static void logWarning(String message) {
        LOGGER.warn(message);
    }

    private static volatile boolean serverFullyInitialized = false;

    public static boolean isServerFullyInitialized() {
        return serverFullyInitialized;
    }

    public static void setServerFullyInitialized(boolean initialized) {
        serverFullyInitialized = initialized;
    }

    private static final com.kingodogo.buildscape.registry.StartupOnce PREPARATION = new com.kingodogo.buildscape.registry.StartupOnce();
    private static final com.kingodogo.buildscape.registry.StartupOnce STARTUP = new com.kingodogo.buildscape.registry.StartupOnce();

    public static void prepareRegistration() {
        PREPARATION.run(() -> {
            LOGGER.info("Preparing Buildscape on platform: {}", Services.PLATFORM.getPlatformName());
            ModBlocks.init();
            ModItems.init();
            com.kingodogo.buildscape.trophy.Trophies.init();
            ModEntities.init();
            ModSounds.init();
            com.kingodogo.buildscape.particle.ModParticles.init();
            Services.PLATFORM.registerWorldGen();
            Services.PLATFORM.registerRecipeSerializers();
            com.kingodogo.buildscape.network.ModPackets.registerAll();
        });
    }

    public static void init() {
        STARTUP.run(() -> {
            prepareRegistration();
            Services.REGISTRY.init();
            ModStats.registerStats();
            com.kingodogo.buildscape.block.CopperOxidationHandler.init();
            Services.PLATFORM.registerCommonLifecycleInteractions();
        });
    }

    public static void main(String[] args) {
        init();
    }
}
