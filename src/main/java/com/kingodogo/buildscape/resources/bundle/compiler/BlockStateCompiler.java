package com.kingodogo.buildscape.resources.bundle.compiler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kingodogo.buildscape.resources.bundle.blockstate.BlockStateIR;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class BlockStateCompiler {

    public static BlockStateIR compile(String blockStateId, String jsonContent) {
        JsonObject root = JsonParser.parseString(jsonContent).getAsJsonObject();
        return compile(blockStateId, root);
    }

    public static BlockStateIR compile(String blockStateId, Reader reader) {
        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
        return compile(blockStateId, root);
    }

    public static BlockStateIR compile(String blockStateId, JsonObject root) {
        if (root.has("variants") && root.get("variants").isJsonObject()) {
            JsonObject variantsObj = root.getAsJsonObject("variants");
            Map<String, List<BlockStateIR.VariantModelIR>> variants = new LinkedHashMap<>();
            for (Map.Entry<String, JsonElement> entry : variantsObj.entrySet()) {
                variants.put(entry.getKey(), parseVariantModels(entry.getValue()));
            }
            return new BlockStateIR(blockStateId, BlockStateIR.TYPE_VARIANTS, -1, null, variants,
                    Collections.emptyList());
        } else if (root.has("multipart") && root.get("multipart").isJsonArray()) {
            JsonArray mpArr = root.getAsJsonArray("multipart");
            List<BlockStateIR.MultiPartIR> multiparts = new ArrayList<>(mpArr.size());
            for (JsonElement el : mpArr) {
                if (el.isJsonObject()) {
                    JsonObject selObj = el.getAsJsonObject();
                    BlockStateIR.ConditionIR cond = null;
                    if (selObj.has("when") && selObj.get("when").isJsonObject()) {
                        cond = parseCondition(selObj.getAsJsonObject("when"));
                    }
                    List<BlockStateIR.VariantModelIR> models = parseVariantModels(selObj.get("apply"));
                    multiparts.add(new BlockStateIR.MultiPartIR(cond, models));
                }
            }
            return new BlockStateIR(blockStateId, BlockStateIR.TYPE_MULTIPART, -1, null, Collections.emptyMap(),
                    multiparts);
        }
        throw new IllegalArgumentException("Blockstate has neither 'variants' nor 'multipart': " + blockStateId);
    }

    private static List<BlockStateIR.VariantModelIR> parseVariantModels(JsonElement element) {
        if (element == null) {
            return Collections.emptyList();
        }
        List<BlockStateIR.VariantModelIR> list = new ArrayList<>();
        if (element.isJsonArray()) {
            for (JsonElement item : element.getAsJsonArray()) {
                if (item.isJsonObject()) {
                    list.add(parseSingleVariantModel(item.getAsJsonObject()));
                }
            }
        } else if (element.isJsonObject()) {
            list.add(parseSingleVariantModel(element.getAsJsonObject()));
        }
        return list;
    }

    private static BlockStateIR.VariantModelIR parseSingleVariantModel(JsonObject obj) {
        String model = obj.has("model") ? obj.get("model").getAsString() : "";
        int x = obj.has("x") ? obj.get("x").getAsInt() : 0;
        int y = obj.has("y") ? obj.get("y").getAsInt() : 0;
        boolean uvLock = obj.has("uvlock") && obj.get("uvlock").getAsBoolean();
        int weight = obj.has("weight") ? obj.get("weight").getAsInt() : 1;
        return new BlockStateIR.VariantModelIR(model, x, y, uvLock, weight);
    }

    private static BlockStateIR.ConditionIR parseCondition(JsonObject when) {
        if (when.has("OR") && when.get("OR").isJsonArray()) {
            JsonArray orArr = when.getAsJsonArray("OR");
            List<BlockStateIR.ConditionIR> terms = new ArrayList<>(orArr.size());
            for (JsonElement item : orArr) {
                if (item.isJsonObject()) {
                    terms.add(parseCondition(item.getAsJsonObject()));
                }
            }
            return new BlockStateIR.ConditionIR(BlockStateIR.ConditionIR.KIND_OR, null, null, terms);
        } else if (when.has("AND") && when.get("AND").isJsonArray()) {
            JsonArray andArr = when.getAsJsonArray("AND");
            List<BlockStateIR.ConditionIR> terms = new ArrayList<>(andArr.size());
            for (JsonElement item : andArr) {
                if (item.isJsonObject()) {
                    terms.add(parseCondition(item.getAsJsonObject()));
                }
            }
            return new BlockStateIR.ConditionIR(BlockStateIR.ConditionIR.KIND_AND, null, null, terms);
        } else {
            // Multiple key-values in one object represent AND of equality conditions
            if (when.size() == 1) {
                Map.Entry<String, JsonElement> entry = when.entrySet().iterator().next();
                return new BlockStateIR.ConditionIR(BlockStateIR.ConditionIR.KIND_KEY_VALUE, entry.getKey(),
                        entry.getValue().getAsString(), Collections.emptyList());
            } else {
                List<BlockStateIR.ConditionIR> terms = new ArrayList<>(when.size());
                for (Map.Entry<String, JsonElement> entry : when.entrySet()) {
                    terms.add(new BlockStateIR.ConditionIR(BlockStateIR.ConditionIR.KIND_KEY_VALUE, entry.getKey(),
                            entry.getValue().getAsString(), Collections.emptyList()));
                }
                return new BlockStateIR.ConditionIR(BlockStateIR.ConditionIR.KIND_AND, null, null, terms);
            }
        }
    }
}
