package com.kingodogo.buildscape.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;

/** Lossless layout migration: validate first, back up once, then replace a complete file. */
final class ConfigLayoutIO {
    private ConfigLayoutIO() { }

    static String readText(Path file) throws IOException {
        // Match the FileReader/FileWriter encoding used by existing installations.
        return Files.readString(file, java.nio.charset.Charset.defaultCharset());
    }

    static Map<String, String> readEntries(String text) throws IOException {
        Map<String, String> result = new LinkedHashMap<>();
        for (String line : text.split("\\R")) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            int equals = line.indexOf('=');
            if (equals <= 0) throw new IOException("Expected key = value");
            String key = line.substring(0, equals).trim();
            String value = line.substring(equals + 1).trim();
            if (result.containsKey(key)) throw new IOException("Duplicate config key: " + key);
            result.put(key, value);
        }
        return result;
    }

    /** Gson's convenience parsers enable leniency; use the strict token reader explicitly. */
    static JsonObject readProperties(String text) throws IOException {
        try (JsonReader reader = new JsonReader(new StringReader(withoutComments(text)))) {
            reader.setLenient(false);
            JsonElement value = readJson(reader);
            if (!value.isJsonObject() || reader.peek() != JsonToken.END_DOCUMENT) {
                throw new IOException("Expected one JSON object");
            }
            return value.getAsJsonObject();
        } catch (IllegalStateException | NumberFormatException e) {
            throw new IOException("Invalid pillar properties", e);
        }
    }

    private static JsonElement readJson(JsonReader reader) throws IOException {
        switch (reader.peek()) {
            case BEGIN_OBJECT:
                JsonObject object = new JsonObject();
                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    if (object.has(name)) throw new IOException("Duplicate JSON key: " + name);
                    object.add(name, readJson(reader));
                }
                reader.endObject();
                return object;
            case BEGIN_ARRAY:
                JsonArray array = new JsonArray();
                reader.beginArray();
                while (reader.hasNext()) array.add(readJson(reader));
                reader.endArray();
                return array;
            case STRING: return new JsonPrimitive(reader.nextString());
            case NUMBER: return new JsonPrimitive(new java.math.BigDecimal(reader.nextString()));
            case BOOLEAN: return new JsonPrimitive(reader.nextBoolean());
            case NULL:
                reader.nextNull();
                return JsonNull.INSTANCE;
            default: throw new IOException("Expected JSON value");
        }
    }

    private static String withoutComments(String text) throws IOException {
        StringBuilder out = new StringBuilder();
        boolean string = false;
        boolean escape = false;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (string) {
                out.append(c);
                if (escape) escape = false;
                else if (c == '\\') escape = true;
                else if (c == '"') string = false;
            } else if (c == '"') {
                string = true;
                out.append(c);
            } else if (c == '/' && i + 1 < text.length() && text.charAt(i + 1) == '/') {
                while (i + 1 < text.length() && text.charAt(i + 1) != '\n' && text.charAt(i + 1) != '\r') i++;
                out.append(' ');
            } else if (c == '/' && i + 1 < text.length() && text.charAt(i + 1) == '*') {
                int end = text.indexOf("*/", i + 2);
                if (end < 0) throw new IOException("Unterminated JSON comment");
                i = end + 1;
                out.append(' ');
            } else out.append(c);
        }
        if (string) throw new IOException("Unterminated JSON string");
        return out.toString();
    }

    /** Keep user comments even when settings move into their documented sections. */
    static String retainComments(String source, String output, boolean json) {
        if (source == null) return output;
        java.util.List<String> comments = new java.util.ArrayList<>();
        if (!json) {
            for (String line : source.split("\\R")) {
                String trimmed = line.trim();
                if (trimmed.startsWith("#")) comments.add(trimmed);
            }
            return prependComments(comments, output);
        }
        boolean string = false;
        boolean escape = false;
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            if (string) {
                if (escape) escape = false;
                else if (c == '\\') escape = true;
                else if (c == '"') string = false;
            } else if (c == '"') {
                string = true;
            } else if (c == '/' && i + 1 < source.length() && source.charAt(i + 1) == '/') {
                int end = i;
                while (end < source.length() && source.charAt(end) != '\r' && source.charAt(end) != '\n') end++;
                comments.add(source.substring(i, end));
                i = end - 1;
            } else if (c == '/' && i + 1 < source.length() && source.charAt(i + 1) == '*') {
                int end = source.indexOf("*/", i + 2);
                if (end < 0) break;
                comments.add(source.substring(i, end + 2));
                i = end + 1;
            }
        }
        return prependComments(comments, output);
    }

    private static String prependComments(java.util.List<String> comments, String output) {
        StringBuilder retained = new StringBuilder();
        Map<String, Integer> positions = new LinkedHashMap<>();
        for (String comment : comments) {
            int position = output.indexOf(comment, positions.getOrDefault(comment, 0));
            if (position < 0) retained.append(comment).append('\n');
            else positions.put(comment, position + comment.length());
        }
        return retained.append(output).toString();
    }

    static void write(Path file, String text, String expected, boolean backup) throws IOException {
        String current = Files.exists(file) ? readText(file) : null;
        if (!java.util.Objects.equals(expected, current)) {
            throw new IOException("Config changed on disk; reload before saving: " + file);
        }
        if (text.equals(current)) return;
        if (backup && current != null) {
            Path original = file.resolveSibling(file.getFileName() + ".pre-4.0.5.bak");
            if (!Files.exists(original)) Files.copy(file, original);
            else if (!Files.isRegularFile(original)) throw new IOException("Backup is not a regular file: " + original);
        }
        Files.createDirectories(file.getParent());
        Path temporary = Files.createTempFile(file.getParent(), file.getFileName().toString(), ".tmp");
        try {
            Files.writeString(temporary, text, java.nio.charset.Charset.defaultCharset());
            try {
                Files.move(temporary, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporary);
        }
    }
}
