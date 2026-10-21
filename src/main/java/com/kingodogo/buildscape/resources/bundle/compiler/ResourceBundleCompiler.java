package com.kingodogo.buildscape.resources.bundle.compiler;

import com.kingodogo.buildscape.resources.bundle.ResourceBundleContainer;
import com.kingodogo.buildscape.resources.bundle.BundledJsonResources;
import com.kingodogo.buildscape.resources.bundle.blockstate.BlockStateIR;
import com.kingodogo.buildscape.resources.bundle.manifest.ResourceCoverageManifest;
import com.kingodogo.buildscape.resources.bundle.manifest.ResourceCoverageManifest.ResourceKind;
import com.kingodogo.buildscape.resources.bundle.model.ModelIR;
import com.kingodogo.buildscape.resources.bundle.tag.TagIR;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

public final class ResourceBundleCompiler {

    public static void compile(List<Path> resourceRoots, Path outputBundle, Path outputManifest)
            throws IOException {
        compileAssetsAndData(resourceRoots, outputBundle, null, outputManifest);
    }

    public static void compileAssetsAndData(List<Path> resourceRoots, Path assetsBundle, Path dataBundle,
            Path outputManifest) throws IOException {
        // Collect files respecting precedence (first root in list wins)
        Map<String, Path> resolvedFiles = new TreeMap<>(); // sorted lexically for determinism
        for (int r = resourceRoots.size() - 1; r >= 0; r--) {
            Path root = resourceRoots.get(r);
            if (!Files.exists(root)) {
                continue;
            }
            try (Stream<Path> stream = Files.walk(root)) {
                for (Path file : (Iterable<Path>) stream.filter(Files::isRegularFile)::iterator) {
                    String rel = root.relativize(file).toString().replace('\\', '/');
                    if (isCoveredPath(rel)) {
                        resolvedFiles.put(rel, file);
                    }
                }
            }
        }

        ResourceCoverageManifest manifest = new ResourceCoverageManifest();
        Map<String, byte[]> sourceResources = new TreeMap<>();
        ResourceBundleContainer.Writer assetsWriter = new ResourceBundleContainer.Writer();
        ResourceBundleContainer.Writer dataWriter = dataBundle != null
                ? new ResourceBundleContainer.Writer()
                : assetsWriter;

        // Pattern sharing managers
        Map<String, Integer> elementSignatureToTemplateId = new HashMap<>();
        int nextTemplateId = 1;
        java.util.Set<String> seenModelIds = new java.util.HashSet<>();
        java.util.Set<String> seenBlockStateIds = new java.util.HashSet<>();
        java.util.Set<String> seenTagIds = new java.util.HashSet<>();

        for (Map.Entry<String, Path> entry : resolvedFiles.entrySet()) {
            String rel = entry.getKey();
            Path file = entry.getValue();
            byte[] fileBytes = Files.readAllBytes(file);
            String sha256Hex = computeSha256Hex(fileBytes);
            String jsonContent = new String(fileBytes, StandardCharsets.UTF_8);

            String[] parts = rel.split("/");
            String top = parts[0]; // "assets" or "data"
            String namespace = parts[1];
            String category = parts[2];

            if ("assets".equals(top)) {
                sourceResources.put(rel, fileBytes);
            }

            if ("assets".equals(top) && "models".equals(category)) {
                String modelPath = rel.substring(("assets/" + namespace + "/models/").length(), rel.length() - 5);
                String modelId = namespace + ":" + modelPath;
                if (!seenModelIds.add(modelId)) {
                    throw new IllegalStateException("Duplicate model ID encountered: " + modelId);
                }
                ModelIR model = ModelCompiler.compile(modelId, jsonContent);

                if (model.elements() != null && !model.elements().isEmpty()) {
                    String sig = computeElementSignature(model.elements());
                    Integer tmplId = elementSignatureToTemplateId.get(sig);
                    if (tmplId == null) {
                        tmplId = nextTemplateId++;
                        elementSignatureToTemplateId.put(sig, tmplId);
                        assetsWriter.addGeometryTemplate(tmplId, model.elements());
                    }
                    model = new ModelIR(model.id(), model.parent(), model.ambientOcclusion(), model.guiLight(),
                            model.textures(), tmplId, Collections.emptyList(), model.overrides(), model.transforms());
                }

                assetsWriter.addModel(model);
                manifest.addEntry(rel, namespace, ResourceKind.MODEL, sha256Hex);
            } else if ("assets".equals(top) && "blockstates".equals(category)) {
                String bsPath = rel.substring(("assets/" + namespace + "/blockstates/").length(), rel.length() - 5);
                String blockStateId = namespace + ":" + bsPath;
                if (!seenBlockStateIds.add(blockStateId)) {
                    throw new IllegalStateException("Duplicate blockstate ID encountered: " + blockStateId);
                }
                BlockStateIR bs = BlockStateCompiler.compile(blockStateId, jsonContent);
                assetsWriter.addBlockState(bs);
                manifest.addEntry(rel, namespace, ResourceKind.BLOCKSTATE, sha256Hex);
            } else if ("data".equals(top) && "tags".equals(category)) {
                String registry = parts[3];
                String tagPath = rel.substring(("data/" + namespace + "/tags/" + registry + "/").length(),
                        rel.length() - 5);
                String tagId = namespace + ":" + tagPath;
                String uniqueTagKey = registry + "/" + tagId;
                if (!seenTagIds.add(uniqueTagKey)) {
                    throw new IllegalStateException("Duplicate tag ID encountered: " + uniqueTagKey);
                }
                TagIR tag = TagCompiler.compile(registry, tagId, jsonContent);
                dataWriter.addTag(tag);
                manifest.addEntry(rel, namespace, ResourceKind.TAG, sha256Hex);
            }
        }

        // Generate deterministic bundle content digest
        MessageDigest digest = createSha256();
        for (ResourceCoverageManifest.Entry e : manifest.getEntries().values()) {
            digest.update(e.relativePath().getBytes(StandardCharsets.UTF_8));
            digest.update(e.sha256Hex().getBytes(StandardCharsets.UTF_8));
        }
        byte[] contentDigest = digest.digest();

        // Write outputs
        if (assetsBundle != null) {
            if (assetsBundle.getParent() != null) {
                Files.createDirectories(assetsBundle.getParent());
            }
            byte[] bundleBytes = assetsWriter.write(contentDigest);
            Files.write(assetsBundle, bundleBytes);
            try (var sourceOutput = Files.newOutputStream(assetsBundle.resolveSibling("resources.bsrc"))) {
                BundledJsonResources.write(sourceResources, sourceOutput);
            }
        }

        if (dataBundle != null) {
            if (dataBundle.getParent() != null) {
                Files.createDirectories(dataBundle.getParent());
            }
            byte[] dataBytes = dataWriter.write(contentDigest);
            Files.write(dataBundle, dataBytes);
        }

        if (outputManifest != null) {
            manifest.writeToFile(outputManifest);
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            System.err.println(
                    "Usage: ResourceBundleCompiler <assetsBundle> <dataBundle> <manifest> <root1> [root2...]");
            System.exit(1);
        }
        Path assetsBundle = Path.of(args[0]);
        Path dataBundle = Path.of(args[1]);
        Path manifest = Path.of(args[2]);
        List<Path> roots = new ArrayList<>();
        for (int i = 3; i < args.length; i++) {
            roots.add(Path.of(args[i]));
        }
        compileAssetsAndData(roots, assetsBundle, dataBundle, manifest);
        System.out.println("Resource bundle compilation finished with " + roots.size() + " roots.");
    }

    public static boolean isCoveredPath(String rel) {
        String[] parts = rel.split("/");
        if (parts.length < 4 || !rel.endsWith(".json")) {
            return false;
        }

        String top = parts[0];
        String category = parts[2];

        if ("assets".equals(top)) {
            if ("models".equals(category)) {
                return true;
            }
            if ("blockstates".equals(category)) {
                return true;
            }
        } else if ("data".equals(top)) {
            if ("tags".equals(category)) {
                return true;
            }
        }
        return false;
    }

    private static String computeElementSignature(List<ModelIR.ElementIR> elements) {
        StringBuilder sb = new StringBuilder();
        for (ModelIR.ElementIR el : elements) {
            sb.append("from:").append(Float.floatToIntBits(el.from()[0]))
                    .append(',').append(Float.floatToIntBits(el.from()[1]))
                    .append(',').append(Float.floatToIntBits(el.from()[2])).append(';');
            sb.append("to:").append(Float.floatToIntBits(el.to()[0]))
                    .append(',').append(Float.floatToIntBits(el.to()[1]))
                    .append(',').append(Float.floatToIntBits(el.to()[2])).append(';');
            sb.append("shade:").append(el.shade()).append(';');
            if (el.rotation() != null) {
                sb.append("rot:").append(el.rotation().axis()).append(',')
                        .append(Float.floatToIntBits(el.rotation().angle())).append(';');
            }
            for (byte dir = 0; dir < 6; dir++) {
                ModelIR.FaceIR face = el.faces().get(dir);
                if (face != null) {
                    sb.append("face:").append(dir).append(',').append(face.rotation()).append(',')
                            .append(face.tintIndex()).append(';');
                    sb.append("tex:").append(face.texture()).append(';');
                    if (face.cullface() != null) {
                        sb.append("cull:").append(face.cullface()).append(';');
                    }
                    if (face.uv() != null) {
                        sb.append("uv:").append(Float.floatToIntBits(face.uv()[0])).append(',')
                                .append(Float.floatToIntBits(face.uv()[1])).append(',')
                                .append(Float.floatToIntBits(face.uv()[2])).append(',')
                                .append(Float.floatToIntBits(face.uv()[3])).append(';');
                    }
                }
            }
        }
        return sb.toString();
    }

    private static MessageDigest createSha256() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    public static String computeSha256Hex(byte[] bytes) {
        byte[] hash = createSha256().digest(bytes);
        StringBuilder sb = new StringBuilder(64);
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private ResourceBundleCompiler() {}
}
