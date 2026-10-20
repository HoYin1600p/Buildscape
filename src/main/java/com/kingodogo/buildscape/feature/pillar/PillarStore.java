package com.kingodogo.buildscape.feature.pillar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.core.io.AtomicFiles;
import com.kingodogo.buildscape.core.io.Json;
import com.kingodogo.buildscape.feature.pillar.PillarIdManager.PillarData;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;

/**
 * Where the pillar data file lives and how it is read and written: the world folder when a server is
 * running, the config folder otherwise. The file format and names are unchanged.
 */
final class PillarStore {

    private PillarStore() {
    }


    static final Gson GSON = Json.PRETTY;
    static final String FILE_NAME = "pillar-ids.dat";
    static final String BACKUP_FILE_NAME = "pillar-ids.bak.dat";
    static final String FOLDER_NAME = "buildscape";

    static File cachedWorldSaveDir = null;

    static void updateCachedWorldDir() {
        try {
            if (!BuildScape.isServerFullyInitialized()) {
                return;
            }

            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (
                    server != null &&
                            server.isRunning() &&
                            server.getPlayerList().getPlayerCount() > 0
            ) {
                try {
                    Path worldPath = server.getWorldPath(LevelResource.ROOT);
                    if (worldPath != null) {
                        File buildscapeDir = worldPath.resolve(FOLDER_NAME).toFile();
                        if (!buildscapeDir.exists()) {
                            buildscapeDir.mkdirs();
                        }
                        cachedWorldSaveDir = buildscapeDir;
                    }
                } catch (Exception e) {
                }
            }
        } catch (Throwable t) {
        }
    }

    static File getDataFile() {
        return new File(getDataDir(), FILE_NAME);
    }

    static File getBackupDataFile() {
        return new File(getDataDir(), BACKUP_FILE_NAME);
    }

    private static File getDataDir() {
        try {
            if (cachedWorldSaveDir != null && cachedWorldSaveDir.exists()) {
                return cachedWorldSaveDir;
            }

            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                try {
                    Path worldPath = server.getWorldPath(LevelResource.ROOT);
                    if (worldPath != null) {
                        File buildscapeDir = worldPath.resolve(FOLDER_NAME).toFile();
                        if (!buildscapeDir.exists()) {
                            buildscapeDir.mkdirs();
                        }
                        cachedWorldSaveDir = buildscapeDir;
                        return buildscapeDir;
                    }
                } catch (Exception e) {
                }
            }

            String configPath = Paths.get(
                    "config",
                    "buildscape",
                    "pillar"
            ).toString();
            File dir = new File(configPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            return dir;
        } catch (Throwable t) {
            return new File(".");
        }
    }

    static Map<String, PillarData> loadFromFile(File file) throws Exception {
        try (
                FileInputStream fis = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(isr)
        ) {
            Type type = new TypeToken<Map<String, PillarData>>() {}.getType();
            return GSON.fromJson(reader, type);
        }
    }
    static void resetCachedWorldDir() {
        cachedWorldSaveDir = null;
    }

    /** Copies an unreadable data file aside. Returns false when the copy failed, which blocks saving. */
    static boolean preserveCorrupted(File file, Throwable error, String errorType) {
        boolean preserved = false;
        try {
            Files.copy(file.toPath(), new File(file.getParentFile(),
                    file.getName() + ".corrupted." + System.currentTimeMillis()).toPath());
            preserved = true;
        } catch (IOException e) {
            LogManager.getLogger(PillarIdManager.class).error("Unable to preserve corrupted pillar data; saves are blocked", e);
        }
        LogManager.getLogger(PillarIdManager.class).error("Unable to read pillar data (" + errorType + ")", error);
        return preserved;
    }

    /** Writes the removed frames and the live records to a temp file and moves it over the target atomically. */
    static boolean write(File file, String tempFileName, Map<String, PillarData> removedFrames,
                         Map<String, PillarData> pillarData) {
        try {
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            File tempFile = new File(file.getParentFile(), tempFileName + ".tmp");

            try (
                    FileOutputStream fos = new FileOutputStream(tempFile);
                    OutputStreamWriter osw = new OutputStreamWriter(
                            fos,
                            StandardCharsets.UTF_8
                    );
                    BufferedWriter writer = new BufferedWriter(osw)
            ) {
                Map<String, PillarData> savedData = new HashMap<>(removedFrames);
                savedData.putAll(pillarData);
                GSON.toJson(savedData, writer);
                writer.flush();
                osw.flush();
                fos.flush();

                try {
                    FileChannel channel = fos.getChannel();
                    channel.force(true);
                } catch (Exception forceEx) {
                }
            }

            AtomicFiles.moveReplacing(tempFile.toPath(), file.toPath());
            return true;
        } catch (Exception e) {
            BuildScape.LOGGER.error("Error saving pillar data to {}", file.getAbsolutePath(), e);
            return false;
        }
    }

}
