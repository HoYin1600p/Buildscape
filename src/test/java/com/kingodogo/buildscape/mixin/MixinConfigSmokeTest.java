package com.kingodogo.buildscape.mixin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kingodogo.buildscape.BuildScape;
import org.junit.Test;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Checks the mixin configs without a running game: every class a config lists exists in the compiled output and is
 * annotated {@code @Mixin}, every {@code @Mixin} class in the output is listed in a config, and every mixin target
 * resolves to a class on the test classpath. The compiled classes are read with ASM, because {@code @Mixin} has class
 * retention and loading mixin classes through reflection would fail outside the mixin transformer.
 */
public class MixinConfigSmokeTest {

    private static final String MIXIN = "Lorg/spongepowered/asm/mixin/Mixin;";

    /** Targets that are not on the unit-test classpath, each with the reason. Anything else must resolve. */
    private static final Map<String, String> UNRESOLVABLE_TARGETS = Map.of(
            "me.jellysquid.mods.sodium.client.world.biome.BiomeColorCache",
            "Embeddium/Sodium is an optional third-party mod (the mixin is @Pseudo)",
            "me.jellysquid.mods.sodium.client.render.pipeline.FluidRenderer",
            "Embeddium/Sodium is an optional third-party mod (the mixin is @Pseudo)",
            "vazkii.quark.content.tools.module.BeaconRedirectionModule",
            "Quark is an optional third-party mod (the mixin is @Pseudo)");

    private static final class MixinClass {
        String name;
        boolean annotated;
        final List<String> targets = new ArrayList<>();
    }

    private static Path classesRoot() throws Exception {
        return Path.of(BuildScape.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    }

    private static MixinClass read(Path file) throws IOException {
        MixinClass mixin = new MixinClass();
        try (InputStream stream = Files.newInputStream(file)) {
            new ClassReader(stream).accept(new ClassVisitor(Opcodes.ASM9) {
                @Override
                public void visit(int version, int access, String name, String signature, String superName,
                        String[] interfaces) {
                    mixin.name = name.replace('/', '.');
                }

                @Override
                public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                    if (!MIXIN.equals(descriptor)) {
                        return null;
                    }
                    mixin.annotated = true;
                    return new AnnotationVisitor(Opcodes.ASM9) {
                        @Override
                        public AnnotationVisitor visitArray(String name) {
                            if (!"value".equals(name) && !"targets".equals(name)) {
                                return null;
                            }
                            return new AnnotationVisitor(Opcodes.ASM9) {
                                @Override
                                public void visit(String ignored, Object value) {
                                    if (value instanceof Type type) {
                                        mixin.targets.add(type.getClassName());
                                    } else if (value instanceof String text) {
                                        mixin.targets.add(text);
                                    }
                                }
                            };
                        }
                    };
                }
            }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        }
        return mixin;
    }

    /** All mixin config files in the main resources (the folder holding buildscape.mixins.json). */
    private static List<Path> configFiles() throws Exception {
        Path first = Path.of(BuildScape.class.getClassLoader().getResource("buildscape.mixins.json").toURI());
        try (Stream<Path> files = Files.list(first.getParent())) {
            return files.filter(p -> p.getFileName().toString().endsWith(".mixins.json")).sorted()
                    .collect(Collectors.toList());
        }
    }

    private static JsonObject parse(Path config) throws IOException {
        try (Reader reader = Files.newBufferedReader(config, StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        }
    }

    /** Fully qualified mixin class names listed by a config, across its "mixins", "client" and "server" arrays. */
    private static Set<String> listedMixins(JsonObject config) {
        String prefix = config.get("package").getAsString() + ".";
        Set<String> names = new TreeSet<>();
        for (String section : new String[]{"mixins", "client", "server"}) {
            if (!config.has(section)) {
                continue;
            }
            JsonArray entries = config.getAsJsonArray(section);
            for (JsonElement entry : entries) {
                String name = prefix + entry.getAsString();
                assertTrue("duplicate mixin entry " + name, names.add(name));
            }
        }
        return names;
    }

    private static Path classFile(Path root, String className) {
        return root.resolve(className.replace('.', '/') + ".class");
    }

    @Test
    public void thereIsAtLeastOneMixinConfigAndItNamesItsPlugin() throws Exception {
        List<Path> configs = configFiles();
        assertFalse("no *.mixins.json found", configs.isEmpty());
        for (Path config : configs) {
            JsonObject json = parse(config);
            assertTrue(config + " has no package", json.has("package"));
            String plugin = json.has("plugin") ? json.get("plugin").getAsString() : null;
            if (plugin != null) {
                assertTrue("plugin class missing: " + plugin, Files.exists(classFile(classesRoot(), plugin)));
            }
        }
    }

    @Test
    public void everyListedMixinExistsAndIsAnnotated() throws Exception {
        Path root = classesRoot();
        for (Path config : configFiles()) {
            for (String name : listedMixins(parse(config))) {
                Path file = classFile(root, name);
                assertTrue(config.getFileName() + " lists a missing class: " + name, Files.exists(file));
                MixinClass mixin = read(file);
                assertEquals(name, mixin.name);
                assertTrue(name + " is listed in " + config.getFileName() + " but has no @Mixin", mixin.annotated);
                assertFalse(name + " has @Mixin but no target", mixin.targets.isEmpty());
            }
        }
    }

    @Test
    public void everyMixinClassInTheOutputIsListedInAConfig() throws Exception {
        Path root = classesRoot();
        Set<String> listed = new TreeSet<>();
        for (Path config : configFiles()) {
            listed.addAll(listedMixins(parse(config)));
        }
        List<Path> classFiles;
        try (Stream<Path> files = Files.walk(root)) {
            classFiles = files.filter(p -> p.toString().endsWith(".class")).sorted().collect(Collectors.toList());
        }
        Set<String> annotated = new TreeSet<>();
        for (Path file : classFiles) {
            MixinClass mixin = read(file);
            if (mixin.annotated) {
                annotated.add(mixin.name);
            }
        }
        assertFalse("no @Mixin classes found, the scan is broken", annotated.isEmpty());
        Set<String> orphans = new TreeSet<>(annotated);
        orphans.removeAll(listed);
        assertTrue("@Mixin classes missing from every config: " + orphans, orphans.isEmpty());
        assertEquals(listed, annotated);
    }

    @Test
    public void everyMixinTargetResolvesOrIsAllowlisted() throws Exception {
        Path root = classesRoot();
        Map<String, String> unresolved = new TreeMap<>();
        Set<String> seenAllowlisted = new TreeSet<>();
        ClassLoader loader = BuildScape.class.getClassLoader();
        for (Path config : configFiles()) {
            for (String name : listedMixins(parse(config))) {
                for (String target : read(classFile(root, name)).targets) {
                    String resource = target.replace('.', '/') + ".class";
                    if (loader.getResource(resource) != null) {
                        continue;
                    }
                    if (UNRESOLVABLE_TARGETS.containsKey(target)) {
                        seenAllowlisted.add(target);
                    } else {
                        unresolved.put(target, name);
                    }
                }
            }
        }
        assertTrue("mixin targets not on the classpath (target=mixin): " + unresolved, unresolved.isEmpty());
        assertEquals("allowlist entries that now resolve or are unused must be removed",
                new TreeSet<>(UNRESOLVABLE_TARGETS.keySet()), seenAllowlisted);
    }
}
