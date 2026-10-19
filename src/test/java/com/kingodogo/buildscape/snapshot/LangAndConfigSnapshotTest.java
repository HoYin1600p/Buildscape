package com.kingodogo.buildscape.snapshot;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kingodogo.buildscape.config.BuildscapeClientConfig;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static org.junit.Assert.assertNotNull;

/** The lang key set and the config defaults must not change. */
public class LangAndConfigSnapshotTest {
    private static String resourceText(String path) throws IOException {
        try (InputStream stream = LangAndConfigSnapshotTest.class.getClassLoader().getResourceAsStream(path)) {
            assertNotNull(path, stream);
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8).replace("\r\n", "\n");
        }
    }

    @Test
    public void enUsLangKeySet() throws IOException {
        try (InputStream stream = getClass().getClassLoader()
                .getResourceAsStream("assets/buildscape/lang/en_us.json")) {
            assertNotNull(stream);
            JsonObject lang = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
                    .getAsJsonObject();
            SnapshotSupport.assertSnapshot("lang-en_us-keys.txt", new ArrayList<>(new TreeSet<>(lang.keySet())));
        }
    }

    @Test
    public void pillarParticleConfigDefaults() throws IOException, IllegalAccessException {
        PillarParticleConfig defaults = new PillarParticleConfig();
        List<String> lines = new ArrayList<>();
        for (Field field : PillarParticleConfig.class.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) || !Modifier.isPublic(modifiers)) {
                continue;
            }
            field.setAccessible(true);
            lines.add(field.getName() + " = " + field.get(defaults));
        }
        lines.sort(null);
        SnapshotSupport.assertSnapshot("config-pillar-particle-defaults.txt", lines);
    }

    @Test
    public void defaultPillarPropertiesFile() throws IOException {
        SnapshotSupport.assertSnapshot("config-default-pillar-properties.json.txt",
                List.of(resourceText("data/buildscape/config/default_pillar_properties.json").split("\n", -1)));
    }

    @Test
    public void defaultPillarPresetsFile() throws IOException {
        SnapshotSupport.assertSnapshot("config-default-pillar-presets.json.txt",
                List.of(resourceText("data/buildscape/config/default_pillar_presets.json").split("\n", -1)));
    }

    @Test
    public void buildscapeCfgDefaultKeys() throws IOException, ReflectiveOperationException {
        Field field = BuildscapeClientConfig.class.getDeclaredField("DEFAULTS");
        field.setAccessible(true);
        Map<?, ?> defaults = (Map<?, ?>) field.get(null);
        List<String> lines = new ArrayList<>();
        for (Map.Entry<?, ?> entry : defaults.entrySet()) {
            lines.add(entry.getKey() + " = " + entry.getValue());
        }
        SnapshotSupport.assertSnapshot("config-buildscape-cfg-defaults.txt", lines);
    }
}
