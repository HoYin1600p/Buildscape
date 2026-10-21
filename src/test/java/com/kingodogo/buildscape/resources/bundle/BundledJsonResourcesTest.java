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
