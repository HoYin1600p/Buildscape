package com.kingodogo.buildscape.config;

import com.kingodogo.buildscape.testutil.BootstrappedGameTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LegacyUiPrefsReadOnlyTest extends BootstrappedGameTest {
    @Rule
    public TemporaryFolder temporary = new TemporaryFolder();

    @Test
    public void malformedMainConfigKeepsLegacyPreferencesAcrossLoadsAndSaveAttempts() throws Exception {
        Path root = temporary.newFolder().toPath();
        Path directory = Files.createDirectories(root.resolve("buildscape"));
        Path main = directory.resolve("buildscape.cfg");
        Path legacy = directory.resolve("ui-state.cfg");
        byte[] mainBytes = "# keep this\nHideBuildscapeConfig=true\ngarbage\n"
                .getBytes(StandardCharsets.UTF_8);
        byte[] legacyBytes = "UITheme=light\nUIBackground=custom.png\n".getBytes(StandardCharsets.UTF_8);
        Files.write(main, mainBytes);
        Files.write(legacy, legacyBytes);

        for (int load = 0; load < 2; load++) {
            BuildscapeClientConfig config = new BuildscapeClientConfig(root);
            assertTrue("Read-only migration must retain the only persisted UI preferences", Files.exists(legacy));
            assertTrue(config.isConfigButtonHidden());
            assertEquals("light", config.getUiTheme());
            assertEquals("custom.png", config.getUiBackground());
            config.save();
            assertArrayEquals(mainBytes, Files.readAllBytes(main));
            assertArrayEquals(legacyBytes, Files.readAllBytes(legacy));
        }
    }
}
