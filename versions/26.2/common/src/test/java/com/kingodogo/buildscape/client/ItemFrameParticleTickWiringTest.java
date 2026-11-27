package com.kingodogo.buildscape.client;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemFrameParticleTickWiringTest {
    @Test
    void sharedDispatcherTicksFramesOnceAfterTheWorldAndPauseGuard() throws Exception {
        String source = Files.readString(repositoryRoot().resolve(
                "common/src/main/java/com/kingodogo/buildscape/client/ClientEvents.java"));
        String tick = source.substring(source.indexOf("public static void onClientTick()"),
                source.indexOf("public static void onClientDisconnect()"));
        String call = "ItemFrameParticleHandler.clientTick(mc.level, mc.player, mc.level.getGameTime());";
        assertEquals(1, occurrences(source, "ItemFrameParticleHandler.clientTick("));
        int guard = tick.indexOf("if (mc.level == null || mc.player == null || mc.isPaused())");
        assertTrue(guard >= 0, "Frame particles need a level, player and unpaused client");
        int guardReturn = tick.indexOf("return;", guard);
        assertTrue(guardReturn > guard && tick.indexOf(call) > guardReturn,
                "Frame particles must run after the early return");
    }

    @Test
    void everyLoaderDispatchesTheSharedTickOnceAtEndWithoutASeparateFrameTick() throws Exception {
        Path root = repositoryRoot();
        for (String loader : List.of("fabric", "forge", "neoforge")) {
            for (String version : List.of("1.18.2", "1.21.1", "26.2", "26.3")) {
                if (loader.equals("neoforge") && version.equals("1.18.2")) continue;
                String clientClass = switch (loader) {
                    case "fabric" -> "BuildscapeFabricClient.java";
                    case "neoforge" -> "BuildscapeNeoForgeClient.java";
                    default -> version.equals("1.18.2")
                            ? "client/ForgeClientEvents.java" : "BuildscapeForgeClient.java";
                };
                String target = loader + "/" + version;
                String source = Files.readString(root.resolve(target
                        + "/src/main/java/com/kingodogo/buildscape/" + clientClass));
                assertEquals(1, occurrences(source, "ClientEvents.onClientTick();"), target);
                assertEquals(0, occurrences(source, "ItemFrameParticleHandler.clientTick("), target);
                String endHook = loader.equals("fabric") ? "END_CLIENT_TICK.register"
                        : version.equals("1.18.2") ? "event.phase != TickEvent.Phase.END"
                        : "ClientTickEvent.Post";
                assertTrue(source.indexOf(endHook) >= 0
                        && source.indexOf(endHook) < source.indexOf("ClientEvents.onClientTick();"), target);
            }
        }
    }

    private static long occurrences(String source, String text) {
        return Pattern.compile(Pattern.quote(text)).matcher(source).results().count();
    }

    private static Path repositoryRoot() {
        for (Path path = Path.of("").toAbsolutePath(); path != null; path = path.getParent()) {
            if (Files.isRegularFile(path.resolve(
                    "common/src/main/java/com/kingodogo/buildscape/client/ClientEvents.java"))) return path;
        }
        throw new IllegalStateException("Cannot locate common client sources");
    }
}
