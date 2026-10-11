package com.kingodogo.buildscape.parity;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Fails with a nonzero exit code without needing a test framework or Minecraft bootstrap. */
public final class DeterministicJsonSelfCheck {
    private DeterministicJsonSelfCheck() {}

    public static void main(String[] args) throws Exception {
        Path folder = Path.of(args[0]);
        Files.createDirectories(folder);
        JsonElement input = JsonParser.parseString("{\"z\":[{\"z\":2,\"a\":1},3,1],\"n\":null,\"a\":\"<é>&\"}");
        String original = input.toString();
        JsonElement reordered = JsonParser.parseString("{\"a\":\"<é>&\",\"n\":null,\"z\":[{\"a\":1,\"z\":2},3,1]}");
        Path first = folder.resolve("first.json");
        Path second = folder.resolve("second.json");
        DeterministicJson.write(first, input);
        DeterministicJson.write(second, reordered);
        require(Arrays.equals(Files.readAllBytes(first), Files.readAllBytes(second)),
                "Object insertion order changed the output bytes");
        String expected = "{\n  \"a\": \"<é>&\",\n  \"n\": null,\n  \"z\": [\n    {\n      \"a\": 1,\n      \"z\": 2\n    },\n    3,\n    1\n  ]\n}\n";
        require(Files.readString(first, StandardCharsets.UTF_8).equals(expected),
                "Writer must sort nested keys, retain array order/nulls, and write UTF-8 with LF");
        require(input.toString().equals(original), "Canonicalization mutated the supplied tree");
        require(DeterministicJson.canonical(DeterministicJson.canonical(input)).toString()
                .equals(reordered.toString()), "Canonicalization is not idempotent");

        Map<String, JsonElement> files = new LinkedHashMap<>();
        files.put("z.json", input);
        files.put("a.json", new JsonObject());
        Map<String, JsonElement> sorted = DeterministicJson.sortedFiles(files);
        require(List.copyOf(sorted.keySet()).equals(List.of("a.json", "z.json")),
                "Snapshot filenames are not sorted");
        require(List.copyOf(files.keySet()).equals(List.of("z.json", "a.json")),
                "Filename sorting mutated the supplier's map");
        require(DeterministicJson.json(sorted.keySet()).toString().equals("[\"a.json\",\"z.json\"]"),
                "Manifest filename array lost its sorted order");
        require(DeterministicJson.json(null).isJsonNull(), "Null conversion changed");
        require(DeterministicJson.json(input) == input, "JSON trees must retain their value types");
        System.out.println("Deterministic JSON self-check passed");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
