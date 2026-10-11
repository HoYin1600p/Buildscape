package com.kingodogo.buildscape.parity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

final class DumpIO {
    private static final Logger LOGGER = LogManager.getLogger("BuildscapeParityDumper");

    private DumpIO() {}

    static boolean included(ResourceLocation id) {
        return id != null && (id.getNamespace().equals("buildscape") || id.getNamespace().equals("minecraft"));
    }

    static String implementation(Object value) {
        if (value == null) return null;
        String name = value.getClass().getName();
        // Hidden lambda numbers and addresses vary with load order and JVM runs.
        int lambda = name.indexOf("$$Lambda$");
        return lambda < 0 ? name : name.substring(0, lambda) + "::lambda";
    }

    static Object field(Class<?> owner, Object instance, String mojmap, String searge) {
        try {
            Field field;
            try {
                field = owner.getDeclaredField(mojmap);
                field.setAccessible(true);
            } catch (NoSuchFieldException ignored) {
                field = ObfuscationReflectionHelper.findField(owner, searge);
            }
            return field.get(instance);
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Cannot inspect " + owner.getName() + "." + mojmap, failure);
        }
    }

    static JsonObject object(Object... pairs) {
        JsonObject result = new JsonObject();
        for (int i = 0; i < pairs.length; i += 2) result.add((String) pairs[i], json(pairs[i + 1]));
        return result;
    }

    static JsonElement json(Object value) {
        if (value instanceof ResourceLocation id) return new JsonPrimitive(id.toString());
        return DeterministicJson.json(value);
    }

    static JsonElement nbt(Tag tag) {
        if (tag instanceof CompoundTag compound) {
            JsonObject values = new JsonObject();
            compound.getAllKeys().stream().sorted().forEach(key -> values.add(key, nbt(compound.get(key))));
            return object("type", tag.getId(), "value", values);
        }
        if (tag instanceof ListTag list) {
            JsonArray values = new JsonArray();
            list.forEach(value -> values.add(nbt(value)));
            return object("type", tag.getId(), "value", values);
        }
        if (tag instanceof NumericTag number) return object("type", tag.getId(), "value", number.getAsNumber());
        if (tag instanceof StringTag string) return object("type", tag.getId(), "value", string.getAsString());
        // Primitive arrays have deterministic SNBT, including their element type.
        return object("type", tag.getId(), "value", tag.getAsString());
    }

    private static void write(Path path, JsonElement value) throws IOException {
        DeterministicJson.write(path, value);
    }

    static synchronized void capture(Path folder, String side, Supplier<Map<String, JsonElement>> supplier) {
        JsonObject manifest = object("schema", 1, "side", side, "minecraft", "1.18.2",
                "forge", "40.3.12", "complete", false, "files", new ArrayList<String>());
        try {
            Files.createDirectories(folder);
            // Invalidate a previous snapshot before evaluating or writing any new data.
            write(folder.resolve("manifest.json"), manifest);
            Map<String, JsonElement> files = DeterministicJson.sortedFiles(supplier.get());
            for (var entry : files.entrySet()) write(folder.resolve(entry.getKey()), entry.getValue());
            manifest.add("files", json(files.keySet()));
            manifest.addProperty("complete", true);
            write(folder.resolve("manifest.json"), manifest);
            LOGGER.info("Complete {} parity dump written to {}", side, folder);
        } catch (Exception failure) {
            manifest.addProperty("complete", false);
            manifest.addProperty("error", failure.getClass().getName() + ": " + failure.getMessage());
            try { write(folder.resolve("manifest.json"), manifest); }
            catch (IOException writeFailure) { failure.addSuppressed(writeFailure); }
            LOGGER.error("Parity dump failed; manifest is incomplete", failure);
        }
    }
}
