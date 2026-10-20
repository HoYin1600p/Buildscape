package com.kingodogo.buildscape.snapshot;

import com.kingodogo.buildscape.recipe.framework.cache.RecipeCacheRegressionTest;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Compares generated lines with the checked-in expected file under {@code src/test/resources/snapshot/}.
 * Set the environment variable {@code SNAPSHOT_UPDATE=1} to rewrite the expected files from the current code.
 */
final class SnapshotSupport {
    private static final Path SNAPSHOT_DIR = Path.of("src", "test", "resources", "snapshot");

    private SnapshotSupport() {}

    static void bootstrap() {
        RecipeCacheRegressionTest.bootstrap();
    }

    static List<String> ids(DeferredRegister<?> register) {
        List<String> ids = new ArrayList<>();
        for (RegistryObject<?> entry : register.getEntries()) {
            ids.add(entry.getId().toString());
        }
        return ids;
    }

    static void assertSnapshot(String name, List<String> actual) throws IOException {
        if ("1".equals(System.getenv("SNAPSHOT_UPDATE"))) {
            Files.createDirectories(SNAPSHOT_DIR);
            Files.writeString(SNAPSHOT_DIR.resolve(name), String.join("\n", actual) + "\n", StandardCharsets.UTF_8);
            return;
        }
        String resource = "snapshot/" + name;
        try (InputStream stream = SnapshotSupport.class.getClassLoader().getResourceAsStream(resource)) {
            assertNotNull("missing expected snapshot " + resource, stream);
            String text = new String(stream.readAllBytes(), StandardCharsets.UTF_8).replace("\r\n", "\n");
            List<String> expected = text.isEmpty()
                    ? List.of()
                    : List.of(text.substring(0, text.length() - 1).split("\n", -1));
            assertEquals(name + " line count", expected.size(), actual.size());
            for (int i = 0; i < expected.size(); i++) {
                assertEquals(name + " line " + (i + 1), expected.get(i), actual.get(i));
            }
        }
    }
}
