package com.kingodogo.buildscape.recipe.framework.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

// Formats BDRE recipe JSON files into compact format single-line recipe entries.
public class RecipePackFormatter {

    private static final Gson PRETTY_GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    private static final Gson COMPACT_GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .create();

    private static final Set<String> PRETTY_TOP_LEVEL_KEYS = Set.of(
            "_comment",
            "aliases",
            "wood_families",
            "stone_families",
            "templates",
            "families"
    );

    public static String format(String jsonString) {
        JsonElement rootElement = JsonParser.parseString(jsonString);
        if (!rootElement.isJsonObject()) {
            return jsonString;
        }

        JsonObject root = rootElement.getAsJsonObject();
        StringBuilder sb = new StringBuilder("{\n");

        List<Map.Entry<String, JsonElement>> entries = new ArrayList<>(root.entrySet());
        for (int i = 0; i < entries.size(); i++) {
            Map.Entry<String, JsonElement> entry = entries.get(i);
            String key = entry.getKey();
            JsonElement val = entry.getValue();
            boolean isLast = (i == entries.size() - 1);
            String comma = isLast ? "" : ",";

            if (PRETTY_TOP_LEVEL_KEYS.contains(key)) {
                String prettyVal = PRETTY_GSON.toJson(val);
                String[] lines = prettyVal.split("\r?\n");
                StringBuilder indented = new StringBuilder();
                for (int l = 0; l < lines.length; l++) {
                    if (l > 0) indented.append("  ");
                    indented.append(lines[l]);
                    if (l < lines.length - 1) indented.append("\n");
                }
                sb.append("  \"").append(key).append("\": ").append(indented).append(comma).append("\n");
            } else if (val.isJsonArray()) {
                JsonArray array = val.getAsJsonArray();
                sb.append("  \"").append(key).append("\": [\n");
                for (int j = 0; j < array.size(); j++) {
                    JsonElement item = array.get(j);
                    String compactItem = COMPACT_GSON.toJson(item);
                    boolean isLastItem = (j == array.size() - 1);
                    sb.append("    ").append(compactItem).append(isLastItem ? "" : ",").append("\n");
                }
                sb.append("  ]").append(comma).append("\n");
            } else {
                String compactVal = COMPACT_GSON.toJson(val);
                sb.append("  \"").append(key).append("\": ").append(compactVal).append(comma).append("\n");
            }
        }

        sb.append("}\n");
        return sb.toString();
    }

    public static boolean formatFile(Path path) throws IOException {
        if (!Files.isRegularFile(path)) {
            return false;
        }
        String content = Files.readString(path, StandardCharsets.UTF_8);
        String formatted = format(content);
        if (!content.equals(formatted)) {
            Files.writeString(path, formatted, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return true;
        }
        return false;
    }

    public static void formatDirectory(Path dir) throws IOException {
        if (!Files.isDirectory(dir)) {
            return;
        }
        try (Stream<Path> stream = Files.walk(dir)) {
            stream.filter(p -> Files.isRegularFile(p) && p.toString().endsWith(".json"))
                    .sorted()
                    .forEach(p -> {
                        try {
                            boolean changed = formatFile(p);
                            System.out.println((changed ? "Formatted: " : "Unchanged: ") + p.toAbsolutePath());
                        } catch (Exception e) {
                            System.err.println("Failed to format " + p + ": " + e.getMessage());
                        }
                    });
        }
    }

    public static void main(String[] args) throws Exception {
        Path targetDir;
        if (args.length > 0) {
            targetDir = Path.of(args[0]);
        } else {
            targetDir = Path.of("src/main/resources/data/buildscape/recipes_pack");
        }

        if (Files.isRegularFile(targetDir)) {
            boolean changed = formatFile(targetDir);
            System.out.println((changed ? "Formatted: " : "Unchanged: ") + targetDir.toAbsolutePath());
        } else if (Files.isDirectory(targetDir)) {
            formatDirectory(targetDir);
        } else {
            System.err.println("Target path not found: " + targetDir.toAbsolutePath());
        }
    }
}
