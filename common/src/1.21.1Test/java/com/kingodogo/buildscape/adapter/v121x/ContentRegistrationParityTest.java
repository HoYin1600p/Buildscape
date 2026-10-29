package com.kingodogo.buildscape.adapter.v121x;

import com.kingodogo.buildscape.block.ModBlocks;
import com.kingodogo.buildscape.item.ModItems;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.registry.ContentRemovalPolicy;
import com.kingodogo.buildscape.registry.RegistrySupplier;
import com.kingodogo.buildscape.trophy.Trophies;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.TreeSet;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class ContentRegistrationParityTest {
    @org.junit.jupiter.api.BeforeAll
    static void bootstrapMinecraft() {
        // Trophies touch BuiltInRegistries, which refuse access before Minecraft's bootstrap.
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
    }

    @Test
    void actualRegistrationQueuesEqualMainMinusExactly121xRemovals() throws Exception {
        ModBlocks.init();
        ModItems.init();
        Trophies.init();
        // main's real registry (helpers, loops and trophies included), captured from a parity dump of main's jar.
        var reference = new String(ContentRegistrationParityTest.class.getResourceAsStream("/main-1.18.2-registry-ids.txt").readAllBytes(),
                java.nio.charset.StandardCharsets.UTF_8).lines().filter(line -> !line.isBlank() && !line.startsWith("#")).toList();
        for (String kind : new String[]{"block", "item"}) {
            var expected = new TreeSet<String>();
            reference.stream().filter(line -> line.startsWith(kind + " ")).forEach(line -> expected.add(line.substring(kind.length() + 1)));
            assertFalse(expected.isEmpty(), "Missing reference registration ids");
            for (var removal : ContentRemovalPolicy.V121X) {
                if (kind.equals("block") ? removal.block() : removal.item()) expected.remove(removal.id());
            }
            Collection<RegistrySupplier<?>> queue = kind.equals("block")
                    ? Services.REGISTRY.getRegisteredBlocks() : Services.REGISTRY.getRegisteredItems();
            var actual = new TreeSet<String>();
            queue.forEach(entry -> assertTrue(actual.add(entry.getId()), "Duplicate registered ID: " + entry.getId()));
            var missing = new TreeSet<>(expected);
            missing.removeAll(actual);
            var extra = new TreeSet<>(actual);
            extra.removeAll(expected);
            assertTrue(missing.isEmpty() && extra.isEmpty(), kind + " registry differs from main minus removals121x.json; missing "
                    + missing.size() + ": " + missing + "; unexpected " + extra.size() + ": " + extra);
        }
    }

    private static TreeSet<String> declaredIds(Path file, String call) throws Exception {
        String source = Files.readString(file).replaceAll("(?s)/\\*.*?\\*/", "").replaceAll("(?m)//[^\\r\\n]*", "");
        var matcher = Pattern.compile("\\b" + call + "\\s*\\(\\s*\"([a-z0-9_]+)\"").matcher(source);
        var ids = new TreeSet<String>();
        while (matcher.find()) ids.add("buildscape:" + matcher.group(1));
        return ids;
    }

    private static Path repositoryRoot() {
        for (Path root = Path.of("").toAbsolutePath(); root != null; root = root.getParent()) {
            if (Files.isRegularFile(root.resolve("sync/removals121x.json"))) return root;
        }
        throw new IllegalStateException("Cannot locate reference registration sources");
    }
}
