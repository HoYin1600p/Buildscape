package com.kingodogo.buildscape.resources.bundle;

import com.kingodogo.buildscape.resources.bundle.runtime.BundledJsonPack;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.FileSystems;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BundledJsonResourcesTest {

    @Test
    void binaryBundleOwnershipUsesTheForgeResolverWithoutAJsonArchive(@TempDir Path directory) throws IOException {
        Path jar = directory.resolve("buildscape.jar");
        byte[] payload = {0x42, 0x53, 0x52, 0x42};
        String[] paths = {"assets/buildscape/resources.bsrb", "data/buildscape/data.bsrb"};
        try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(jar))) {
            for (String path : paths) {
                zip.putNextEntry(new ZipEntry(path));
                zip.write(payload);
                zip.closeEntry();
            }
        }
        try (var fs = FileSystems.newFileSystem(jar)) {
            BundledJsonPack pack = new BundledJsonPack(path -> fs.getPath("/" + path));
            for (String path : paths) {
                assertTrue(pack.contains(path));
                try (var in = pack.open(path)) {
                    assertArrayEquals(payload, in.readAllBytes());
                }
            }
            assertFalse(pack.contains("assets/other/resources.bsrb"));
            assertNull(pack.open("data/other/data.bsrb"));
        }
    }

    @Test
    void parallelPreloadAndReadersPreserveEveryResource() throws Exception {
        Map<String, byte[]> input = new LinkedHashMap<>();
        for (int i = 0; i < 600; i++) {
            input.put("assets/example/models/block/m" + i + ".json",
                    ("{\"parent\":\"example:block/m" + i + "\"}" + " ".repeat(i % 300))
                            .getBytes(StandardCharsets.UTF_8));
        }
        ByteArrayOutputStream archive = new ByteArrayOutputStream();
        BundledJsonResources.write(input, archive);
        BundledJsonResources resources = BundledJsonResources.read(new ByteArrayInputStream(archive.toByteArray()));
        var executor = java.util.concurrent.Executors.newFixedThreadPool(4);
        try {
            var prepare = executor.submit(() -> {
                resources.preload();
                return null;
            });
            var readers = new java.util.ArrayList<java.util.concurrent.Future<?>>();
            for (int worker = 0; worker < 3; worker++) {
                readers.add(executor.submit(() -> {
                    for (var entry : input.entrySet()) {
                        try (var first = resources.open(entry.getKey());
                                var second = resources.open(entry.getKey())) {
                            assertEquals(entry.getValue()[0], first.read());
                            assertArrayEquals(entry.getValue(), second.readAllBytes());
                            assertArrayEquals(
                                    java.util.Arrays.copyOfRange(entry.getValue(), 1, entry.getValue().length),
                                    first.readAllBytes());
                        }
                    }
                    return null;
                }));
            }
            prepare.get();
            for (var reader : readers) {
                reader.get();
            }
        } finally {
            executor.shutdownNow();
        }
        assertNull(resources.open("assets/example/models/block/missing.json"));
    }

    @Test
    void preloadLeavesCorruptEntryErrorsLocalToThatResource() throws IOException {
        ByteArrayOutputStream archive = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(archive);
        out.writeInt(0x42534a31);
        out.writeInt(2);
        out.writeUTF("assets/example/models/bad.json");
        out.writeInt(1);
        out.writeInt(1);
        out.writeByte(255);
        ByteArrayOutputStream payload = new ByteArrayOutputStream();
        try (var deflater = new java.util.zip.DeflaterOutputStream(payload)) {
            deflater.write("{}".getBytes(StandardCharsets.UTF_8));
        }
        out.writeUTF("assets/example/models/good.json");
        out.writeInt(2);
        out.writeInt(payload.size());
        out.write(payload.toByteArray());
        BundledJsonResources resources = BundledJsonResources.read(new ByteArrayInputStream(archive.toByteArray()));
        resources.preload();
        try (var good = resources.open("assets/example/models/good.json")) {
            assertArrayEquals("{}".getBytes(StandardCharsets.UTF_8), good.readAllBytes());
        }
        assertThrows(IOException.class, () -> resources.open("assets/example/models/bad.json"));
    }

    @Test
    void listingWorkIsLimitedToTheRequestedPrefix(@TempDir Path directory) throws IOException {
        Map<String, byte[]> paths = new LinkedHashMap<>();
        for (int group = 0; group < 100; group++) {
            for (int model = 0; model < 100; model++) {
                paths.put("assets/example/models/block/group" + group + "/m" + model + ".json", new byte[0]);
            }
        }
        Path archive = directory.resolve(BundledJsonResources.FILE);
        Files.createDirectories(archive.getParent());
        try (var out = Files.newOutputStream(archive)) {
            BundledJsonResources.write(paths, out);
        }
        BundledJsonPack pack = new BundledJsonPack(directory);
        pack.namespaces(PackType.CLIENT_RESOURCES);
        long[] scanned = new long[10];
        long[] indexed = new long[10];
        for (int run = 0; run < 10; run++) {
            long start = System.nanoTime();
            int referenceCount = 0;
            for (int query = 0; query < 100; query++) {
                for (String path : paths.keySet()) {
                    if (path.startsWith("assets/example/models/block/group42/") && path.endsWith(".json")) {
                        referenceCount++;
                    }
                }
            }
            scanned[run] = System.nanoTime() - start;
            start = System.nanoTime();
            int actualCount = 0;
            for (int query = 0; query < 100; query++) {
                actualCount += pack.list(PackType.CLIENT_RESOURCES, "example", "models/block/group42", 9,
                        name -> name.endsWith(".json")).size();
            }
            indexed[run] = System.nanoTime() - start;
            assertEquals(referenceCount, actualCount);
        }
        java.util.Arrays.sort(scanned);
        java.util.Arrays.sort(indexed);
        System.out.println("Listing 100 queries over 10000 paths, 10 trials; scan median ns="
                + (scanned[4] + scanned[5]) / 2 + "; indexed median ns=" + (indexed[4] + indexed[5]) / 2);
    }

    @Test
    void indexedListingPreservesNamespacePrefixDepthAndReload(@TempDir Path directory) throws IOException {
        Map<String, byte[]> paths = new LinkedHashMap<>();
        paths.put("assets/example/models/block/a.json", "{}".getBytes(StandardCharsets.UTF_8));
        paths.put("assets/example/models/block/nested/b.json", "{}".getBytes(StandardCharsets.UTF_8));
        paths.put("assets/example/models/block_extra/c.json", "{}".getBytes(StandardCharsets.UTF_8));
        paths.put("assets/other/models/block/d.json", "{}".getBytes(StandardCharsets.UTF_8));
        Path archive = directory.resolve(BundledJsonResources.FILE);
        Files.createDirectories(archive.getParent());
        try (var out = Files.newOutputStream(archive)) {
            BundledJsonResources.write(paths, out);
        }
        BundledJsonPack pack = new BundledJsonPack(directory);
        assertEquals(Set.of(new ResourceLocation("example", "models/block/a.json")),
                Set.copyOf(
                        pack.list(PackType.CLIENT_RESOURCES, "example", "models/block", 3, n -> n.endsWith(".json"))));
        assertEquals(2, pack.list(PackType.CLIENT_RESOURCES, "example", "models/block/", 4, n -> true).size());
        assertEquals(0, pack.list(PackType.SERVER_DATA, "example", "models", 9, n -> true).size());
        assertEquals(Set.of("example", "other"), pack.namespaces(PackType.CLIENT_RESOURCES));
        paths.clear();
        paths.put("assets/reloaded/models/new.json", "{}".getBytes(StandardCharsets.UTF_8));
        try (var out = Files.newOutputStream(archive)) {
            BundledJsonResources.write(paths, out);
        }
        pack.close();
        assertEquals(Set.of("reloaded"), pack.namespaces(PackType.CLIENT_RESOURCES));
        assertTrue(pack.list(PackType.CLIENT_RESOURCES, "example", "", 9, n -> true).isEmpty());
    }

    @Test
    void packagedWoodResourcesUseForgeResolverRatherThanJarFilename(@TempDir Path tempDir) throws IOException {
        Map<String, byte[]> json = new LinkedHashMap<>();
        String[] woods = {"cherry", "mangrove", "pale_oak", "poplar"};
        Path assets = Path.of("src/main/resources");
        for (String wood : woods) {
            for (String category : new String[]{"blockstates/", "models/block/", "models/item/"}) {
                String path = "assets/buildscape/" + category + wood + "_planks.json";
                json.put(path, Files.readAllBytes(assets.resolve(path)));
            }
        }
        ByteArrayOutputStream archive = new ByteArrayOutputStream();
        BundledJsonResources.write(json, archive);
        Path jar = tempDir.resolve("buildscape.jar");
        try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(jar))) {
            zip.putNextEntry(new ZipEntry(BundledJsonResources.FILE));
            zip.write(archive.toByteArray());
            zip.closeEntry();
        }
        // Forge's getSource() is the physical jar; resolve() reads its virtual filesystem.
        assertFalse(new BundledJsonPack(jar).contains("assets/buildscape/blockstates/cherry_planks.json"));
        try (var fileSystem = FileSystems.newFileSystem(jar)) {
            BundledJsonPack pack = new BundledJsonPack(path -> fileSystem.getPath("/" + path));
            assertEquals(4, pack.list(PackType.CLIENT_RESOURCES, "buildscape", "blockstates", 10,
                    name -> name.endsWith(".json")).size());
            for (String wood : woods) {
                String statePath = "assets/buildscape/blockstates/" + wood + "_planks.json";
                assertTrue(pack.contains(statePath));
                String model;
                try (InputStream in = pack.open(statePath);
                        var reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                    model = JsonParser.parseReader(reader).getAsJsonObject().getAsJsonObject("variants")
                            .getAsJsonObject("").get("model").getAsString();
                }
                String modelPath = "assets/" + model.replace(":", "/models/") + ".json";
                try (InputStream in = pack.open(modelPath);
                        var reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                    String texture = JsonParser.parseReader(reader).getAsJsonObject()
                            .getAsJsonObject("textures").get("all").getAsString();
                    assertEquals("buildscape:block/" + wood + "_planks", texture);
                    assertTrue(Files.isRegularFile(assets.resolve(
                            "assets/" + texture.replace(":", "/textures/") + ".png")));
                }
                assertFalse(Files.exists(fileSystem.getPath("/" + modelPath)), "JSON must stay bundled");
                try (InputStream in = pack.open(modelPath)) {
                    assertArrayEquals(json.get(modelPath), in.readAllBytes());
                }
            }
            pack.close();
            assertTrue(pack.contains("assets/buildscape/blockstates/cherry_planks.json"));
        }
    }

    @Test
    void resolverPreservesLooseResourcesInSamePack(@TempDir Path tempDir) throws IOException {
        String path = "assets/buildscape/models/block/cherry_planks.json";
        Path archive = tempDir.resolve(BundledJsonResources.FILE);
        Files.createDirectories(archive.getParent());
        try (var out = Files.newOutputStream(archive)) {
            BundledJsonResources.write(Map.of(path, "{}".getBytes(StandardCharsets.UTF_8)), out);
        }
        Path loose = tempDir.resolve(path);
        Files.createDirectories(loose.getParent());
        Files.writeString(loose, "{\"textures\":{\"all\":\"another:override\"}}");
        BundledJsonPack pack = new BundledJsonPack(tempDir::resolve);
        assertFalse(pack.contains(path));
        assertNull(pack.open(path));
    }

    @Test
    void roundTripValidResources() throws IOException {
        String path1 = "assets/buildscape/models/block/maple_planks.json";
        String path2 = "assets/buildscape/blockstates/maple_planks.json";
        byte[] content1 = "{\"parent\":\"minecraft:block/cube_all\"}".getBytes(StandardCharsets.UTF_8);
        byte[] content2 = "{\"variants\":{\"\":{\"model\":\"buildscape:block/maple_planks\"}}}"
                .getBytes(StandardCharsets.UTF_8);

        Map<String, byte[]> input = Map.of(path1, content1, path2, content2);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BundledJsonResources.write(input, out);

        BundledJsonResources read = BundledJsonResources.read(new ByteArrayInputStream(out.toByteArray()));
        assertEquals(Set.of(path1, path2), read.paths());
        assertTrue(read.contains(path1));
        assertTrue(read.contains(path2));
        assertFalse(read.contains("assets/buildscape/models/item/missing.json"));

        try (InputStream stream1 = read.open(path1)) {
            assertNotNull(stream1);
            assertArrayEquals(content1, stream1.readAllBytes());
        }

        try (InputStream stream2 = read.open(path2)) {
            assertNotNull(stream2);
            assertArrayEquals(content2, stream2.readAllBytes());
        }
    }

    @Test
    void rejectsInvalidPaths() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        assertThrows(IOException.class, () -> BundledJsonResources.write(
                Map.of("data/buildscape/recipes/maple_planks.json", new byte[0]), out));

        assertThrows(IOException.class, () -> BundledJsonResources.write(
                Map.of("assets/buildscape/models/../block/maple_planks.json", new byte[0]), out));

        assertThrows(IOException.class, () -> BundledJsonResources.write(
                Map.of("assets/buildscape/textures/block/maple_planks.png", new byte[0]), out));
    }

    @Test
    void rejectsCorruptHeader() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(out);
        assertThrows(IOException.class, () -> {
            data.writeInt(0x12345678);
            data.writeInt(0);
            BundledJsonResources.read(new ByteArrayInputStream(out.toByteArray()));
        });
    }

    @Test
    void bundledJsonPackIntegration(@TempDir Path tempDir) throws IOException {
        Path bundlePath = tempDir.resolve(BundledJsonResources.FILE);
        Files.createDirectories(bundlePath.getParent());

        String modelPath = "assets/buildscape/models/block/maple_planks.json";
        String itemModelPath = "assets/buildscape/models/item/maple_planks.json";
        String blockstatePath = "assets/buildscape/blockstates/maple_planks.json";
        byte[] modelJson = "{\"parent\":\"block/cube_all\"}".getBytes(StandardCharsets.UTF_8);

        Map<String, byte[]> map = Map.of(
                modelPath, modelJson,
                itemModelPath, modelJson,
                blockstatePath, modelJson);

        try (var out = Files.newOutputStream(bundlePath)) {
            BundledJsonResources.write(map, out);
        }

        BundledJsonPack pack = new BundledJsonPack(tempDir);
        assertTrue(pack.contains(modelPath));
        assertTrue(pack.contains(itemModelPath));
        assertTrue(pack.contains(blockstatePath));
        assertFalse(pack.contains("assets/buildscape/textures/block/maple_planks.png"));

        try (InputStream stream = pack.open(modelPath)) {
            assertNotNull(stream);
            assertArrayEquals(modelJson, stream.readAllBytes());
        }

        assertEquals(Set.of("buildscape"), pack.namespaces(PackType.CLIENT_RESOURCES));
        assertTrue(pack.namespaces(PackType.SERVER_DATA).isEmpty());

        Collection<ResourceLocation> models = pack.list(PackType.CLIENT_RESOURCES, "buildscape", "models/block",
                10, name -> name.endsWith(".json"));
        assertEquals(1, models.size());
        assertTrue(models.contains(new ResourceLocation("buildscape", "models/block/maple_planks.json")));

        Collection<ResourceLocation> allModels = pack.list(PackType.CLIENT_RESOURCES, "buildscape", "models",
                10, name -> name.endsWith(".json"));
        assertEquals(2, allModels.size());
        assertTrue(allModels.contains(new ResourceLocation("buildscape", "models/block/maple_planks.json")));
        assertTrue(allModels.contains(new ResourceLocation("buildscape", "models/item/maple_planks.json")));

        Collection<ResourceLocation> serverList = pack.list(PackType.SERVER_DATA, "buildscape", "models",
                10, name -> true);
        assertTrue(serverList.isEmpty());

        pack.close();
    }
}
