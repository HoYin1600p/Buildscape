package com.kingodogo.buildscape.client.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;

public final class StairModelTest {
    private static final Path ASSETS = Path.of("src/main/resources/assets/buildscape");
    private static final Map<String, Integer> FACING = Map.of(
            "east", 0, "south", 90, "west", 180, "north", 270);
    private static final String[] SHAPES = {
            "straight", "inner_left", "inner_right", "outer_left", "outer_right"
    };

    public static void main(String[] args) throws IOException {
        int blocks = 0;
        int states = 0;
        try (Stream<Path> paths = Files.list(ASSETS.resolve("blockstates"))) {
            for (Path path : paths.filter(p -> p.getFileName().toString().endsWith("_stairs.json")).toList()) {
                JsonObject variants = read(path).getAsJsonObject("variants");
                require(variants != null && variants.size() == 40, path + ": expected 40 variants");
                for (Map.Entry<String, Integer> facing : FACING.entrySet()) {
                    for (String half : new String[]{"bottom", "top"}) {
                        for (String shape : SHAPES) {
                            String key = "facing=" + facing.getKey() + ",half=" + half + ",shape=" + shape;
                            JsonObject variant = variants.getAsJsonObject(key);
                            require(variant != null, path + ": missing " + key);
                            boolean top = half.equals("top");
                            int y = facing.getValue();
                            if (!top && shape.endsWith("left")) y -= 90;
                            if (top && shape.endsWith("right")) y += 90;
                            require(number(variant, "x") == (top ? 180 : 0)
                                            && number(variant, "y") == Math.floorMod(y, 360),
                                    path + ": incorrect rotation for " + key);
                            String modelId = variant.get("model").getAsString();
                            require(modelId.startsWith("buildscape:"), path + ": unexpected model " + modelId);
                            JsonObject model = read(ASSETS.resolve("models/"
                                    + modelId.substring("buildscape:".length()) + ".json"));
                            String parent = model.get("parent").getAsString();
                            String kind = shape.startsWith("inner") ? "inner" : shape.startsWith("outer") ? "outer" : "straight";
                            require(parent.equals("minecraft:block/" + (kind.equals("straight") ? "stairs" : kind + "_stairs"))
                                            || parent.equals("block/" + (kind.equals("straight") ? "stairs" : kind + "_stairs"))
                                            || parent.equals("buildscape:block/glass_stairs"
                                                + (kind.equals("straight") ? "" : "_" + kind) + "_template"),
                                    path + ": wrong model parent for " + key);
                            states++;
                        }
                    }
                }
                blocks++;
            }
        }
        require(blocks > 0, "No stair blockstates found");
        checkGlassGeometry("straight");
        checkGlassGeometry("inner");
        checkGlassGeometry("outer");
        System.out.println("Stair models passed: " + blocks + " blocks, " + states + " states, 3 glass templates");
    }

    private static void checkGlassGeometry(String kind) throws IOException {
        JsonObject model = read(ASSETS.resolve("models/block/glass_stairs"
                + (kind.equals("straight") ? "" : "_" + kind) + "_template.json"));
        // Sample every model-space voxel so split transparent elements match vanilla's solid shape.
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    boolean expected = y < 8 || switch (kind) {
                        case "straight" -> x >= 8;
                        case "inner" -> x >= 8 || z >= 8;
                        case "outer" -> x >= 8 && z >= 8;
                        default -> throw new AssertionError(kind);
                    };
                    int covering = 0;
                    for (JsonElement value : model.getAsJsonArray("elements")) {
                        JsonObject element = value.getAsJsonObject();
                        require(!element.has("rotation") || number(element.getAsJsonObject("rotation"), "angle") == 0,
                                kind + ": unsupported element rotation");
                        int[] point = {x, y, z};
                        boolean inside = true;
                        for (int axis = 0; axis < 3; axis++) {
                            double from = element.getAsJsonArray("from").get(axis).getAsDouble();
                            double to = element.getAsJsonArray("to").get(axis).getAsDouble();
                            inside &= point[axis] + 0.5 >= from && point[axis] + 0.5 < to;
                        }
                        if (inside) covering++;
                    }
                    require(covering == (expected ? 1 : 0), kind + ": geometry mismatch at " + x + "," + y + "," + z);
                }
            }
        }
    }

    private static int number(JsonObject object, String key) {
        return object.has(key) ? object.get(key).getAsInt() : 0;
    }

    private static JsonObject read(Path path) throws IOException {
        try (Reader reader = Files.newBufferedReader(path)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
