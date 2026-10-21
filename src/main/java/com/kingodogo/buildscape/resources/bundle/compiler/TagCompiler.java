package com.kingodogo.buildscape.resources.bundle.compiler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kingodogo.buildscape.resources.bundle.tag.TagIR;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class TagCompiler {

    public static TagIR compile(String registry, String tagId, String jsonContent) {
        JsonObject root = JsonParser.parseString(jsonContent).getAsJsonObject();
        return compile(registry, tagId, root);
    }

    public static TagIR compile(String registry, String tagId, Reader reader) {
        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
        return compile(registry, tagId, root);
    }

    public static TagIR compile(String registry, String tagId, JsonObject root) {
        boolean replace = root.has("replace") && root.get("replace").getAsBoolean();
        List<TagIR.TagEntryIR> entries = Collections.emptyList();

        if (root.has("values") && root.get("values").isJsonArray()) {
            JsonArray arr = root.getAsJsonArray("values");
            entries = new ArrayList<>(arr.size());
            for (JsonElement el : arr) {
                if (el.isJsonPrimitive()) {
                    String val = el.getAsString();
                    boolean isTag = val.startsWith("#");
                    String actualId = isTag ? val.substring(1) : val;
                    entries.add(new TagIR.TagEntryIR(actualId, isTag, true));
                } else if (el.isJsonObject()) {
                    JsonObject obj = el.getAsJsonObject();
                    String val = obj.get("id").getAsString();
                    boolean isTag = val.startsWith("#");
                    String actualId = isTag ? val.substring(1) : val;
                    boolean required = !obj.has("required") || obj.get("required").getAsBoolean();
                    entries.add(new TagIR.TagEntryIR(actualId, isTag, required));
                }
            }
        }

        return new TagIR(registry, tagId, replace, entries);
    }
}
