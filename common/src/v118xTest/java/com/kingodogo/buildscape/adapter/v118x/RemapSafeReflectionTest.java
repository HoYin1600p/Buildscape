package com.kingodogo.buildscape.adapter.v118x;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RemapSafeReflectionTest {
    // Ban member enumeration too: selecting a remapped member by its name inside
    // a fallback loop is just as unsafe as getDeclaredMethod("mojangName").
    private static final Pattern LOOKUP = Pattern.compile(
            "\\.\\s*get(?:Declared)?(?:Methods?|Fields?|Constructors?|Classes)\\s*\\("
            + "|Class\\s*\\.\\s*forName\\s*\\(\\s*\"net\\.(?:minecraft|minecraftforge)\\.");

    // Explicit allowlist: no exceptions currently. Any future own-class or optional
    // third-party lookup must name its exact relative path and matched expression
    // here, with a comment explaining why it cannot target Minecraft/Forge members.
    private static final Map<String, Set<String>> ALLOWED_LOOKUPS = Map.of();

    static Path commonRoot() {
        Path config = Path.of(System.getProperty("buildscape.v118xMixinConfig",
                "src/v118x/resources/buildscape.mixins.json")).toAbsolutePath();
        return config.getParent().getParent().getParent().getParent();
    }

    @Test
    void production1182SourcesContainNoUnapprovedReflectiveLookups() throws IOException {
        Path common = commonRoot();
        List<Path> sources = new ArrayList<>();
        for (String root : List.of("src/main/java/com/kingodogo/buildscape/adapter/v118x", "src/v118x")) {
            Path directory = common.resolve(root);
            assertTrue(Files.isDirectory(directory), "Missing scan root: " + directory);
            try (var paths = Files.walk(directory)) {
                sources.addAll(paths.filter(path -> path.toString().endsWith(".java")).toList());
            }
        }
        sources.add(common.resolve("src/main/java/com/kingodogo/buildscape/menu/MenuTypeBridge.java"));
        List<String> failures = new ArrayList<>();
        for (Path path : sources) {
            String relative = common.relativize(path).toString().replace('\\', '/');
            var matches = LOOKUP.matcher(Files.readString(path));
            while (matches.find()) {
                if (!ALLOWED_LOOKUPS.getOrDefault(relative, Set.of()).contains(matches.group())) {
                    failures.add(relative + ": " + matches.group());
                }
            }
        }
        assertTrue(failures.isEmpty(), "Reflection bypasses production remapping:\n" + String.join("\n", failures));
    }

    @Test
    void detectorRejectsNamedAndFallbackMemberLookups() {
        assertTrue(LOOKUP.matcher("MenuType.class.getDeclaredConstructor(factory)").find());
        assertTrue(LOOKUP.matcher("model.getClass().getDeclaredField(\"customData\")").find());
        assertTrue(LOOKUP.matcher("ParticleEngine.class.getMethods()").find());
        assertTrue(LOOKUP.matcher("Class.forName(\"net.minecraft.client.particle.ParticleEngine\")").find());
        assertTrue(LOOKUP.matcher("Class.forName(\"net.minecraftforge.client.ForgeHooksClient\")").find());
        assertFalse(LOOKUP.matcher("@Accessor(\"mainImage\")").find());
        assertFalse(LOOKUP.matcher("bakery.bake(location, rotation)").find());
    }
}
