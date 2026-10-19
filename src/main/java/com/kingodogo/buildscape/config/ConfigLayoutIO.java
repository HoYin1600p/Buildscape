package com.kingodogo.buildscape.config;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.core.io.AtomicFiles;
import com.kingodogo.buildscape.core.io.Json;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.DosFileAttributeView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/** Lossless layout migration: validate first, back up once, then replace a complete file. */
final class ConfigLayoutIO {
    private static final Gson GSON = Json.PLAIN;
    private static final Map<Path, FailedMigration> failedMigrations = new LinkedHashMap<>();
    private record FailedMigration(byte[] bytes, String text) { }
    private ConfigLayoutIO() { }

    static String readText(Path file) throws IOException {
        return readText(file, legacyCharset());
    }

    static synchronized String readText(Path file, Charset legacy) throws IOException {
        return readText(file, Files.readAllBytes(file), legacy);
    }

    static synchronized String readText(Path file, byte[] bytes, Charset legacy) throws IOException {
        Path key = file.toAbsolutePath().normalize();
        FailedMigration failure = failedMigrations.get(key);
        if (failure != null && Arrays.equals(failure.bytes(), bytes)) return failure.text();
        failedMigrations.remove(key);
        try {
            return decode(bytes, StandardCharsets.UTF_8);
        } catch (CharacterCodingException invalidUtf8) {
            String text;
            try {
                text = decode(bytes, legacy);
            } catch (CharacterCodingException invalidLegacy) {
                return new String(bytes, StandardCharsets.UTF_8);
            }
            try {
                backupOriginal(file, bytes);
                if (!isWritable(file)) throw new IOException("Config is not writable: " + file);
                replace(file, text);
            } catch (IOException | SecurityException e) {
                failedMigrations.put(key, new FailedMigration(bytes.clone(), text));
                BuildScape.getLogger().warn("Unable to migrate legacy config; keeping it read-only: {}", file, e);
                return text;
            }
            BuildScape.getLogger().info(
                    "Migrated config {} from {} to UTF-8", file, legacy.name());
            return text;
        }
    }

    private static void backupOriginal(Path file, byte[] bytes) throws IOException {
        Path backup = file.resolveSibling(file.getFileName() + ".bak");
        try {
            Files.write(backup, bytes, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        } catch (FileAlreadyExistsException e) {
            if (!Files.isRegularFile(backup)) throw new IOException("Backup is not a regular file: " + backup, e);
        }
    }

    static boolean isWritable(Path file) throws IOException {
        if (!Files.isWritable(file)) return false;
        DosFileAttributeView dos = Files.getFileAttributeView(file,
                DosFileAttributeView.class);
        return dos == null || !dos.readAttributes().isReadOnly();
    }

    private static String decode(byte[] bytes, Charset charset) throws CharacterCodingException {
        return charset.newDecoder().onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(bytes)).toString();
    }

    private static Charset legacyCharset() {
        Charset legacy = Charset.defaultCharset();
        String nativeEncoding = System.getProperty("native.encoding");
        if (nativeEncoding != null) {
            try {
                legacy = Charset.forName(nativeEncoding);
            } catch (IllegalArgumentException ignored) {
            }
        }
        return legacy.equals(StandardCharsets.UTF_8) ? Charset.forName("windows-1252") : legacy;
    }

    static boolean hasReplacementCharacters(String text) {
        return text != null && text.indexOf('\uFFFD') >= 0;
    }

    static synchronized boolean protectInvalidEncoding(Path file) throws IOException {
        return protectInvalidEncoding(file, legacyCharset());
    }

    static synchronized boolean protectInvalidEncoding(Path file, Charset legacy) throws IOException {
        String text = readText(file, legacy);
        if (failedMigrations.containsKey(file.toAbsolutePath().normalize())) return true;
        if (!hasReplacementCharacters(text)) return false;
        Path backup = file.resolveSibling(file.getFileName() + ".bak");
        try {
            if (!Files.exists(backup)) Files.copy(file, backup);
        } catch (IOException e) {
            BuildScape.getLogger().warn("Unable to back up unreadable config {}", file, e);
        }
        BuildScape.getLogger().warn("Config contains invalid encoding or replacement characters; keeping it read-only: {}", file);
        return true;
    }

    static void append(Path file, String suffix, String expected) throws IOException {
        if (protectInvalidEncoding(file)) throw new IOException("Config is read-only: " + file);
        if (!Objects.equals(expected, readText(file))) {
            throw new IOException("Config changed on disk; reload before saving: " + file);
        }
        Path backup = file.resolveSibling(file.getFileName() + ".pre-4.0.5.bak");
        if (!Files.exists(backup)) Files.copy(file, backup);
        else if (!Files.isRegularFile(backup)) throw new IOException("Backup is not a regular file: " + backup);
        Files.write(file, suffix.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
    }

    /** The old items writer left a comma before the closing object brace. */
    static String itemsJson(String text) throws IOException {
        String clean = withoutComments(text);
        StringBuilder out = new StringBuilder();
        char quote = 0;
        boolean escape = false;
        for (int i = 0; i < clean.length(); i++) {
            char c = clean.charAt(i);
            if (quote == 0 && c == ',') {
                int next = i + 1;
                while (next < clean.length() && Character.isWhitespace(clean.charAt(next))) next++;
                if (next < clean.length() && (clean.charAt(next) == '}' || clean.charAt(next) == ']')) continue;
            }
            out.append(c);
            if (quote != 0) {
                if (escape) escape = false;
                else if (c == '\\') escape = true;
                else if (c == quote) quote = 0;
            } else if (c == '"' || c == '\'') quote = c;
        }
        return out.toString();
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
            case NUMBER: return new JsonPrimitive(new BigDecimal(reader.nextString()));
            case BOOLEAN: return new JsonPrimitive(reader.nextBoolean());
            case NULL:
                reader.nextNull();
                return JsonNull.INSTANCE;
            default: throw new IOException("Expected JSON value");
        }
    }

    private static String withoutComments(String text) throws IOException {
        StringBuilder out = new StringBuilder();
        char quote = 0;
        boolean escape = false;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (quote != 0) {
                out.append(c);
                if (escape) escape = false;
                else if (c == '\\') escape = true;
                else if (c == quote) quote = 0;
            } else if (c == '"' || c == '\'') {
                quote = c;
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
        if (quote != 0) throw new IOException("Unterminated JSON string");
        return out.toString();
    }

    /** Move comments with their setting, including inline comments and sorted array entries. */
    static String retainComments(String source, String output, boolean json) {
        if (source == null) return output;
        Layout old = new Layout(source, json);
        Layout generated = new Layout(output, json);
        Map<String, List<String>> comments = old.attachedComments(generated);
        Map<String, List<String>> generatedComments = generated.attachedComments(generated);
        for (Map.Entry<String, List<String>> entry : generatedComments.entrySet()) {
            List<String> retained = comments.computeIfAbsent(entry.getKey(), key -> new ArrayList<>());
            // Preserve repeated user comments, but do not multiply generated descriptions on each save.
            Map<String, Integer> remaining = new LinkedHashMap<>();
            for (String comment : retained) remaining.merge(comment, 1, Integer::sum);
            for (String comment : entry.getValue()) {
                int count = remaining.getOrDefault(comment, 0);
                if (count == 0) retained.add(comment);
                else remaining.put(comment, count - 1);
            }
        }
        String plain = generated.withoutCommentLines();
        Layout target = new Layout(plain, json);
        TreeMap<Integer, StringBuilder> insertions = new TreeMap<>();
        StringBuilder orphaned = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : comments.entrySet()) {
            Setting setting = target.settings.get(entry.getKey());
            if (setting == null) {
                for (String comment : entry.getValue()) orphaned.append(comment).append('\n');
                continue;
            }
            int position = setting.start;
            int line = plain.lastIndexOf('\n', Math.max(0, position - 1)) + 1;
            String indent = plain.substring(line, position);
            boolean midLine = !indent.isBlank();
            if (midLine) indent = "";
            StringBuilder insertion = insertions.computeIfAbsent(position, key -> new StringBuilder());
            if (midLine) insertion.append('\n');
            for (String comment : entry.getValue()) insertion.append(comment).append('\n').append(indent);
        }
        StringBuilder result = new StringBuilder(plain);
        for (Map.Entry<Integer, StringBuilder> entry : insertions.descendingMap().entrySet()) {
            result.insert(entry.getKey().intValue(), entry.getValue());
        }
        return orphaned.append(result).toString();
    }

    static String retainInvalidItemTokens(String source, String output) {
        if (source == null) return output;
        Layout old = new Layout(source, true);
        Layout target = new Layout(output, true);
        TreeMap<Integer, Setting> retained = new TreeMap<>();
        for (Setting setting : old.settings.values()) {
            if (!"$/\"items\"".equals(setting.array)) continue;
            String token = source.substring(setting.valueStart, setting.end);
            String item = GSON.fromJson(token, String.class);
            Setting replacement = target.settings.get(setting.key);
            if (!PillarParticleConfig.isValidItemId(item) && replacement != null) {
                retained.put(replacement.valueStart, setting);
            }
        }
        StringBuilder result = new StringBuilder(output);
        for (Map.Entry<Integer, Setting> entry : retained.descendingMap().entrySet()) {
            Setting original = entry.getValue();
            Setting replacement = target.settings.get(original.key);
            result.replace(replacement.valueStart, replacement.end, source.substring(original.valueStart, original.end));
        }
        return result.toString();
    }

    private static final class Token {
        final int start;
        final int end;
        final String text;

        Token(String source, int start, int end) {
            this.start = start;
            this.end = end;
            this.text = source.substring(start, end);
        }
    }

    private static final class Setting {
        final String key;
        final String array;
        final int start;
        final int end;
        final int valueStart;

        Setting(String key, String array, int start, int valueStart, int end) {
            this.key = key;
            this.array = array;
            this.start = start;
            this.valueStart = valueStart;
            this.end = end;
        }
    }

    /** A layout-only lexer: validation remains the responsibility of the config readers. */
    private static final class Layout {
        final String source;
        final List<Token> tokens = new ArrayList<>();
        final List<Token> comments = new ArrayList<>();
        final Map<String, Setting> settings = new LinkedHashMap<>();
        int cursor;

        Layout(String source, boolean json) {
            this.source = source;
            if (!json) {
                int offset = 0;
                for (String line : source.split("\n", -1)) {
                    String trimmed = line.trim();
                    if (trimmed.startsWith("#")) {
                        int start = offset + line.indexOf('#');
                        comments.add(new Token(source, start, start + trimmed.length()));
                    } else if (line.indexOf('=') > 0) {
                        String key = line.substring(0, line.indexOf('=')).trim();
                        settings.put(key, new Setting(key, null, offset + line.indexOf(key), offset + line.indexOf('=') + 1,
                                offset + line.length()));
                    }
                    offset += line.length() + 1;
                }
                return;
            }
            for (int i = 0; i < source.length();) {
                char c = source.charAt(i);
                if (Character.isWhitespace(c)) { i++; continue; }
                int start = i++;
                boolean comment = false;
                if (c == '"' || c == '\'') {
                    while (i < source.length()) {
                        char next = source.charAt(i++);
                        if (next == '\\' && i < source.length()) i++;
                        else if (next == c) break;
                    }
                } else if (c == '/' && i < source.length() && source.charAt(i) == '/') {
                    comment = true;
                    while (i < source.length() && source.charAt(i) != '\n' && source.charAt(i) != '\r') i++;
                } else if (c == '/' && i < source.length() && source.charAt(i) == '*') {
                    comment = true;
                    int end = source.indexOf("*/", i + 1);
                    i = end < 0 ? source.length() : end + 2;
                } else if ("{}[]:,".indexOf(c) < 0) {
                    while (i < source.length() && !Character.isWhitespace(source.charAt(i))
                            && "{}[]:,/".indexOf(source.charAt(i)) < 0) i++;
                }
                (comment ? comments : tokens).add(new Token(source, start, i));
            }
            if (!tokens.isEmpty()) value("$", 0, null);
        }

        private String canonical(Token token) {
            if (token.text.startsWith("\"") || token.text.startsWith("'")) {
                return GSON.toJson(GSON.fromJson(token.text, String.class));
            }
            return token.text;
        }

        private boolean at(String text) {
            return cursor < tokens.size() && tokens.get(cursor).text.equals(text);
        }

        private void value(String path, int start, String array) {
            if (cursor >= tokens.size()) return;
            Token first = tokens.get(cursor++);
            if (first.text.equals("{")) {
                while (cursor < tokens.size() && !at("}")) {
                    if (at(",")) { cursor++; continue; }
                    Token key = tokens.get(cursor++);
                    if (at(":")) cursor++;
                    value(path + "/" + canonical(key), key.start, null);
                }
                if (at("}")) cursor++;
            } else if (first.text.equals("[")) {
                Map<String, Integer> occurrences = new LinkedHashMap<>();
                while (cursor < tokens.size() && !at("]")) {
                    if (at(",")) { cursor++; continue; }
                    Token item = tokens.get(cursor);
                    String id = canonical(item);
                    int occurrence = occurrences.merge(id, 1, Integer::sum);
                    value(path + "[" + id + "]" + occurrence, item.start, path);
                }
                if (at("]")) cursor++;
            }
            if (!path.equals("$")) {
                settings.put(path, new Setting(path, array, start, first.start, tokens.get(cursor - 1).end));
            }
        }

        private boolean sameLine(int start, int end) {
            String between = source.substring(start, end);
            return between.indexOf('\n') < 0 && between.indexOf('\r') < 0;
        }

        Map<String, List<String>> attachedComments(Layout target) {
            Map<String, List<String>> result = new LinkedHashMap<>();
            for (Token comment : comments) {
                Setting attached = null;
                int previousValueEnd = -1;
                for (Token token : tokens) {
                    if (token.end > comment.start) break;
                    if (!token.text.equals(",")) previousValueEnd = token.end;
                }
                // Inline comments belong to the value on that line, including multiline arrays/objects.
                for (Setting setting : settings.values()) {
                    if (setting.end <= comment.start && sameLine(setting.end, comment.start)
                            && (tokens.isEmpty() || setting.end == previousValueEnd)
                            && (attached == null || setting.end > attached.end)) attached = setting;
                }
                if (attached == null) {
                    for (Setting setting : settings.values()) {
                        if (setting.start < comment.start && setting.valueStart >= comment.end
                                && sameLine(setting.start, comment.start)
                                && (attached == null || setting.start > attached.start)) attached = setting;
                    }
                }
                if (attached == null) {
                    for (Setting setting : settings.values()) {
                        if (setting.start >= comment.end && (attached == null || setting.start < attached.start)) {
                            attached = setting;
                        }
                    }
                }
                String key = attached == null ? "" : attached.key;
                if (!tokens.isEmpty() && comment.end <= tokens.get(0).start) key = "";
                // If a sorted item disappeared, retain its comments in order above the list.
                if (!target.settings.containsKey(key)) {
                    key = attached != null && attached.array != null && target.settings.containsKey(attached.array)
                            ? attached.array : "";
                }
                result.computeIfAbsent(key, ignored -> new ArrayList<>()).add(comment.text);
            }
            return result;
        }

        String withoutCommentLines() {
            StringBuilder plain = new StringBuilder(source);
            for (int i = comments.size() - 1; i >= 0; i--) {
                Token comment = comments.get(i);
                int start = source.lastIndexOf('\n', Math.max(0, comment.start - 1)) + 1;
                int end = source.indexOf('\n', comment.end);
                if (end < 0) end = source.length();
                if (source.substring(start, comment.start).isBlank()
                        && source.substring(comment.end, end).isBlank()) {
                    plain.delete(start, end < source.length() ? end + 1 : end);
                } else plain.delete(comment.start, comment.end);
            }
            return plain.toString();
        }
    }

    static void writeNew(Path file, String text) throws IOException {
        Files.createDirectories(file.getParent());
        Path temporary = Files.createTempFile(file.getParent(), file.getFileName().toString(), ".tmp");
        try {
            Files.writeString(temporary, text, StandardCharsets.UTF_8);
            Files.move(temporary, file);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    static void write(Path file, String text, String expected, boolean backup) throws IOException {
        write(file, text, expected, backup, legacyCharset());
    }

    static void write(Path file, String text, String expected, boolean backup, Charset legacy) throws IOException {
        write(file, text, expected, backup, legacy, ".pre-4.0.5.bak");
    }

    static void writeItems(Path file, String text, String expected, boolean legacyItems) throws IOException {
        write(file, text, expected, true, legacyCharset(), legacyItems ? ".pre-4.0.5.bak" : ".bak");
    }

    private static void write(Path file, String text, String expected, boolean backup, Charset legacy, String backupSuffix) throws IOException {
        if (Files.exists(file) && !isWritable(file)) throw new IOException("Config is not writable: " + file);
        String current = Files.exists(file) ? readText(file, legacy) : null;
        if (current != null && protectInvalidEncoding(file, legacy)) {
            throw new IOException("Config encoding is protected; retaining original bytes: " + file);
        }
        if (current != null && !Objects.equals(expected, current)) {
            throw new IOException("Config changed on disk; reload before saving: " + file);
        }
        if (text.equals(current)) return;
        if (backup && current != null) {
            Path original = file.resolveSibling(file.getFileName() + backupSuffix);
            if (!Files.exists(original)) Files.copy(file, original);
            else if (!Files.isRegularFile(original)) throw new IOException("Backup is not a regular file: " + original);
        }
        replace(file, text);
    }

    private static void replace(Path file, String text) throws IOException {
        AtomicFiles.write(file, temporary -> Files.writeString(temporary, text, StandardCharsets.UTF_8));
    }
}
