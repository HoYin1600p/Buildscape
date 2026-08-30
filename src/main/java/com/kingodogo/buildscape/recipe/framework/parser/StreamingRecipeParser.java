package com.kingodogo.buildscape.recipe.framework.parser;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.kingodogo.buildscape.BuildScape;

import java.io.IOException;
import java.io.Reader;
import java.util.*;

/**
 * High-performance streaming JSON reader using GSON JsonReader.
 * Supports wood/stone family categories, reversal/reciprocal auto-recipes, and compact array recipe formats.
 */
public class StreamingRecipeParser {

    public static RecipeIR.CategoryPack parseCategory(String categoryName, Reader reader) throws IOException {
        try (JsonReader jsonReader = new JsonReader(reader)) {
            jsonReader.setLenient(true);

            Map<String, String> aliases = new HashMap<>();
            Map<String, RecipeIR.TemplateSpec> templates = new HashMap<>();
            List<RecipeIR.FamilySpec> families = new ArrayList<>();
            List<RecipeIR.RecipeSpec> recipes = new ArrayList<>();

            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                switch (name) {
                    case "aliases" -> parseAliases(jsonReader, aliases);
                    case "templates" -> parseTemplates(jsonReader, templates);
                    case "families" -> parseFamilies(jsonReader, families, "auto");
                    case "wood_families", "wood" -> parseFamilies(jsonReader, families, "wood");
                    case "stone_families", "stone" -> parseFamilies(jsonReader, families, "stone");
                    case "crafting_shaped", "shaped" -> parseSectionRecipes(jsonReader, recipes, "shaped", false);
                    case "crafting_shapeless", "shapeless" -> parseSectionRecipes(jsonReader, recipes, "shapeless", false);
                    case "crafting_shaped_durability", "shaped_durability" -> parseSectionRecipes(jsonReader, recipes, "shaped_durability", false);
                    case "crafting_shapeless_durability", "shapeless_durability" -> parseSectionRecipes(jsonReader, recipes, "shapeless_durability", false);
                    case "stonecutting" -> parseSectionRecipes(jsonReader, recipes, "stonecutting", false);
                    case "stonecutting_reversible", "reversals", "reversible" -> parseSectionRecipes(jsonReader, recipes, "stonecutting", true);
                    case "smelting" -> parseSectionRecipes(jsonReader, recipes, "smelting", false);
                    case "blasting" -> parseSectionRecipes(jsonReader, recipes, "blasting", false);
                    case "smoking" -> parseSectionRecipes(jsonReader, recipes, "smoking", false);
                    case "campfire" -> parseSectionRecipes(jsonReader, recipes, "campfire", false);
                    case "smithing" -> parseSectionRecipes(jsonReader, recipes, "smithing", false);
                    case "recipes" -> parseSectionRecipes(jsonReader, recipes, categoryName, false);
                    default -> jsonReader.skipValue();
                }
            }
            jsonReader.endObject();

            return new RecipeIR.CategoryPack(categoryName, aliases, templates, families, recipes);
        }
    }

    private static void parseAliases(JsonReader reader, Map<String, String> aliases) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String key = reader.nextName();
            JsonToken token = reader.peek();
            if (token == JsonToken.STRING) {
                String value = reader.nextString();
                aliases.put(key, value);
            } else if (token == JsonToken.BEGIN_ARRAY) {
                List<String> list = new ArrayList<>();
                parseStringList(reader, list);
                aliases.put(key, "[" + String.join(",", list) + "]");
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    private static void parseTemplates(JsonReader reader, Map<String, RecipeIR.TemplateSpec> templates) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String templateName = reader.nextName();
            templates.put(templateName, parseTemplateSpec(reader));
        }
        reader.endObject();
    }

    private static RecipeIR.TemplateSpec parseTemplateSpec(JsonReader reader) throws IOException {
        String type = "shaped";
        List<String> pattern = new ArrayList<>();
        Map<String, String> keys = new HashMap<>();
        List<String> ingredients = new ArrayList<>();
        RecipeIR.ResultSpec result = null;
        int cookingTime = 200;
        float experience = 0.1f;

        reader.beginObject();
        while (reader.hasNext()) {
            String key = reader.nextName();
            switch (key) {
                case "type" -> type = reader.nextString();
                case "pattern" -> parseStringList(reader, pattern);
                case "keys" -> parseAliases(reader, keys);
                case "ingredients" -> parseStringList(reader, ingredients);
                case "result" -> result = parseResultSpec(reader);
                case "cookingTime" -> cookingTime = reader.nextInt();
                case "experience" -> experience = (float) reader.nextDouble();
                default -> reader.skipValue();
            }
        }
        reader.endObject();

        return new RecipeIR.TemplateSpec(type, pattern, keys, ingredients, result, cookingTime, experience);
    }

    private static void parseFamilies(JsonReader reader, List<RecipeIR.FamilySpec> families, String defaultFamilyType) throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
            JsonToken token = reader.peek();
            if (token == JsonToken.STRING) {
                String baseItem = reader.nextString();
                families.add(new RecipeIR.FamilySpec(defaultFamilyType, baseItem, "", Collections.emptyList(), Collections.emptyList(), false));
            } else if (token == JsonToken.BEGIN_ARRAY) {
                reader.beginArray();
                String baseItem = reader.nextString();
                String type = defaultFamilyType;
                List<String> variants = new ArrayList<>();
                List<String> excludes = new ArrayList<>();
                boolean reversible = false;

                if (reader.hasNext()) {
                    JsonToken secondToken = reader.peek();
                    if (secondToken == JsonToken.STRING) {
                        type = reader.nextString();
                        if (reader.hasNext() && reader.peek() == JsonToken.BEGIN_ARRAY) {
                            parseStringList(reader, variants);
                        }
                    } else if (secondToken == JsonToken.BEGIN_ARRAY) {
                        parseStringList(reader, variants);
                    } else if (secondToken == JsonToken.BOOLEAN) {
                        reversible = reader.nextBoolean();
                    }
                }

                if (reader.hasNext() && reader.peek() == JsonToken.BOOLEAN) {
                    reversible = reader.nextBoolean();
                }

                while (reader.hasNext()) {
                    reader.skipValue();
                }
                reader.endArray();
                families.add(new RecipeIR.FamilySpec(type, baseItem, "", variants, excludes, reversible));
            } else if (token == JsonToken.BEGIN_OBJECT) {
                families.add(parseFamilySpec(reader, defaultFamilyType));
            } else {
                reader.skipValue();
            }
        }
        reader.endArray();
    }

    private static RecipeIR.FamilySpec parseFamilySpec(JsonReader reader, String defaultFamilyType) throws IOException {
        String type = defaultFamilyType;
        String base = "";
        String prefix = "";
        List<String> generate = new ArrayList<>();
        List<String> exclude = new ArrayList<>();
        boolean reversible = false;

        reader.beginObject();
        while (reader.hasNext()) {
            String key = reader.nextName();
            switch (key) {
                case "type" -> type = reader.nextString();
                case "base" -> base = reader.nextString();
                case "prefix" -> prefix = reader.nextString();
                case "generate", "variants", "include" -> parseStringList(reader, generate);
                case "exclude", "ignore" -> parseStringList(reader, exclude);
                case "reversible", "reverse", "reciprocal" -> reversible = reader.nextBoolean();
                default -> reader.skipValue();
            }
        }
        reader.endObject();

        return new RecipeIR.FamilySpec(type, base, prefix, generate, exclude, reversible);
    }

    private static void parseSectionRecipes(JsonReader reader, List<RecipeIR.RecipeSpec> recipes, String defaultType, boolean isReversibleSection) throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
            JsonToken token = reader.peek();
            if (token == JsonToken.BEGIN_ARRAY) {
                parseAndAddCompactArrayRecipes(reader, recipes, defaultType, isReversibleSection);
            } else if (token == JsonToken.BEGIN_OBJECT) {
                recipes.add(parseRecipeSpec(reader, defaultType));
            } else {
                reader.skipValue();
            }
        }
        reader.endArray();
    }

    private static void parseAndAddCompactArrayRecipes(JsonReader reader, List<RecipeIR.RecipeSpec> recipes, String defaultType, boolean isReversibleSection) throws IOException {
        reader.beginArray();
        String resultItem = reader.nextString();

        List<String> pattern = new ArrayList<>();
        Map<String, String> keys = new HashMap<>();
        List<String> ingredients = new ArrayList<>();
        String input = null;
        int count = 1;
        String type = defaultType;
        boolean isReversible = isReversibleSection;
        int cookingTime = 200;
        float experience = 0.1f;

        if (reader.hasNext()) {
            JsonToken secondToken = reader.peek();
            if (secondToken == JsonToken.BEGIN_ARRAY) {
                if ("shapeless".equalsIgnoreCase(defaultType) || "shapeless_durability".equalsIgnoreCase(defaultType)) {
                    parseStringList(reader, ingredients);
                    type = defaultType;
                } else {
                    parseStringList(reader, pattern);
                    type = defaultType;
                    if (reader.hasNext()) {
                        JsonToken thirdToken = reader.peek();
                        if (thirdToken == JsonToken.BEGIN_OBJECT) {
                            parseAliases(reader, keys);
                        } else if (thirdToken == JsonToken.BEGIN_ARRAY) {
                            parseKeyArrayPairs(reader, keys);
                        }
                    }
                }
            } else if (secondToken == JsonToken.STRING) {
                input = reader.nextString();
                if ("smithing".equalsIgnoreCase(defaultType)) {
                    if (reader.hasNext()) {
                        JsonToken thirdToken = reader.peek();
                        if (thirdToken == JsonToken.BEGIN_ARRAY) {
                            parseStringList(reader, ingredients);
                        } else if (thirdToken == JsonToken.STRING) {
                            ingredients.add(reader.nextString());
                        }
                    }
                } else {
                    ingredients.add(input);
                }
            }
        }

        int numIndex = 0;
        while (reader.hasNext()) {
            JsonToken nextToken = reader.peek();
            if (nextToken == JsonToken.NUMBER) {
                double val = reader.nextDouble();
                if (numIndex == 0) {
                    count = (int) val;
                } else if (numIndex == 1) {
                    if (val == (int) val) {
                        cookingTime = (int) val;
                    } else {
                        experience = (float) val;
                    }
                } else if (numIndex == 2) {
                    if (val == (int) val) {
                        cookingTime = (int) val;
                    } else {
                        experience = (float) val;
                    }
                }
                numIndex++;
            } else if (nextToken == JsonToken.STRING) {
                if (numIndex == 0) {
                    try {
                        count = Integer.parseInt(reader.nextString());
                    } catch (NumberFormatException ignored) {}
                    numIndex++;
                } else {
                    reader.skipValue();
                }
            } else if (nextToken == JsonToken.BOOLEAN) {
                isReversible = reader.nextBoolean();
            } else if (nextToken == JsonToken.END_ARRAY) {
                break;
            } else {
                reader.skipValue();
            }
        }
        if (reader.peek() == JsonToken.END_ARRAY) {
            reader.endArray();
        }

        // Add Primary Forward Recipe (Result = resultItem, Input = input)
        RecipeIR.ResultSpec resultSpec = new RecipeIR.ResultSpec(resultItem, count, null);
        recipes.add(new RecipeIR.RecipeSpec(null, type, "", pattern, keys, ingredients, input, resultSpec, cookingTime, experience));

        // Add Reversal Recipe if reversible is true and input is a single item/tag
        if (isReversible && input != null && !input.isEmpty()) {
            String reverseRecipeId = sanitizeId(input + "_from_" + resultItem + "_reversal");
            RecipeIR.ResultSpec reverseResultSpec = new RecipeIR.ResultSpec(input, 1, null);
            recipes.add(new RecipeIR.RecipeSpec(reverseRecipeId, type, "", List.of(), Map.of(), List.of(resultItem), resultItem, reverseResultSpec, cookingTime, experience));
        }
    }

    private static void parseKeyArrayPairs(JsonReader reader, Map<String, String> keys) throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
            if (reader.peek() == JsonToken.BEGIN_ARRAY) {
                reader.beginArray();
                if (reader.hasNext() && reader.peek() == JsonToken.STRING) {
                    String k = reader.nextString();
                    if (reader.hasNext()) {
                        JsonToken token = reader.peek();
                        if (token == JsonToken.STRING) {
                            String v = reader.nextString();
                            keys.put(k, v);
                        } else if (token == JsonToken.BEGIN_ARRAY) {
                            List<String> list = new ArrayList<>();
                            parseStringList(reader, list);
                            keys.put(k, "[" + String.join(",", list) + "]");
                        }
                    }
                }
                while (reader.hasNext() && reader.peek() != JsonToken.END_ARRAY) {
                    reader.skipValue();
                }
                if (reader.peek() == JsonToken.END_ARRAY) {
                    reader.endArray();
                }
            } else {
                reader.skipValue();
            }
        }
        reader.endArray();
    }

    private static RecipeIR.RecipeSpec parseRecipeSpec(JsonReader reader, String defaultType) throws IOException {
        com.google.gson.JsonElement jsonElement = com.google.gson.JsonParser.parseReader(reader);
        if (!jsonElement.isJsonObject()) {
            return new RecipeIR.RecipeSpec(null, defaultType, "", List.of(), Map.of(), List.of(), null, null, 200, 0.1f, null);
        }
        com.google.gson.JsonObject json = jsonElement.getAsJsonObject();
        String rawJson = json.toString();

        String id = json.has("id") ? json.get("id").getAsString() : null;
        String type = json.has("type") ? json.get("type").getAsString() : defaultType;
        String group = json.has("group") ? json.get("group").getAsString() : "";
        List<String> pattern = new ArrayList<>();
        if (json.has("pattern") && json.get("pattern").isJsonArray()) {
            for (com.google.gson.JsonElement el : json.getAsJsonArray("pattern")) {
                pattern.add(el.getAsString());
            }
        }
        Map<String, String> keys = new HashMap<>();
        if (json.has("keys") && json.get("keys").isJsonObject()) {
            for (Map.Entry<String, com.google.gson.JsonElement> e : json.getAsJsonObject("keys").entrySet()) {
                keys.put(e.getKey(), e.getValue().getAsString());
            }
        } else if (json.has("key") && json.get("key").isJsonObject()) {
            for (Map.Entry<String, com.google.gson.JsonElement> e : json.getAsJsonObject("key").entrySet()) {
                keys.put(e.getKey(), e.getValue().toString());
            }
        }
        List<String> ingredients = new ArrayList<>();
        if (json.has("ingredients") && json.get("ingredients").isJsonArray()) {
            for (com.google.gson.JsonElement el : json.getAsJsonArray("ingredients")) {
                ingredients.add(el.getAsString());
            }
        }
        String input = json.has("input") ? json.get("input").getAsString() : null;
        RecipeIR.ResultSpec result = null;
        if (json.has("result")) {
            com.google.gson.JsonElement resEl = json.get("result");
            if (resEl.isJsonPrimitive()) {
                result = new RecipeIR.ResultSpec(resEl.getAsString(), 1, null);
            } else if (resEl.isJsonObject()) {
                com.google.gson.JsonObject resObj = resEl.getAsJsonObject();
                String item = resObj.has("item") ? resObj.get("item").getAsString() : null;
                int count = resObj.has("count") ? resObj.get("count").getAsInt() : 1;
                String nbt = resObj.has("nbt") ? resObj.get("nbt").toString() : null;
                result = new RecipeIR.ResultSpec(item, count, nbt);
            }
        }
        int cookingTime = json.has("cookingtime") ? json.get("cookingtime").getAsInt() : (json.has("cookingTime") ? json.get("cookingTime").getAsInt() : 200);
        float experience = json.has("experience") ? json.get("experience").getAsFloat() : 0.1f;

        return new RecipeIR.RecipeSpec(id, type, group, pattern, keys, ingredients, input, result, cookingTime, experience, rawJson);
    }

    private static RecipeIR.ResultSpec parseResultSpec(JsonReader reader) throws IOException {
        String item = null;
        int count = 1;
        String nbt = null;

        if (reader.peek() == JsonToken.STRING) {
            item = reader.nextString();
        } else {
            reader.beginObject();
            while (reader.hasNext()) {
                String key = reader.nextName();
                switch (key) {
                    case "item" -> item = reader.nextString();
                    case "count" -> count = reader.nextInt();
                    case "nbt" -> nbt = com.google.gson.JsonParser.parseReader(reader).toString();
                    default -> reader.skipValue();
                }
            }
            reader.endObject();
        }

        return new RecipeIR.ResultSpec(item, count, nbt);
    }

    private static void parseStringList(JsonReader reader, List<String> list) throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
            JsonToken token = reader.peek();
            if (token == JsonToken.STRING) {
                list.add(reader.nextString());
            } else if (token == JsonToken.BEGIN_ARRAY) {
                List<String> subList = new ArrayList<>();
                parseStringList(reader, subList);
                list.add("[" + String.join(",", subList) + "]");
            } else {
                reader.skipValue();
            }
        }
        reader.endArray();
    }

    private static String sanitizeId(String raw) {
        if (raw == null) return "unknown";
        return raw.toLowerCase(Locale.ROOT)
                .replace(":", "_")
                .replaceAll("[^a-z0-9_.-]", "_");
    }
}
