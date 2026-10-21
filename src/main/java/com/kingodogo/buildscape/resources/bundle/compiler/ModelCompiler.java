package com.kingodogo.buildscape.resources.bundle.compiler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kingodogo.buildscape.resources.bundle.model.ModelIR;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ModelCompiler {

    public static ModelIR compile(String modelId, String jsonContent) {
        JsonObject root = JsonParser.parseString(jsonContent).getAsJsonObject();
        return compile(modelId, root);
    }

    public static ModelIR compile(String modelId, Reader reader) {
        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
        return compile(modelId, root);
    }

    public static ModelIR compile(String modelId, JsonObject root) {
        String parent = root.has("parent") ? root.get("parent").getAsString() : null;
        Boolean ao = root.has("ambientocclusion") ? root.get("ambientocclusion").getAsBoolean() : null;

        byte guiLight = ModelIR.GUI_LIGHT_INHERIT;
        if (root.has("gui_light")) {
            String gl = root.get("gui_light").getAsString().toLowerCase(Locale.ROOT);
            if ("front".equals(gl)) {
                guiLight = ModelIR.GUI_LIGHT_FRONT;
            } else if ("side".equals(gl)) {
                guiLight = ModelIR.GUI_LIGHT_SIDE;
            }
        }

        Map<String, String> textures = Collections.emptyMap();
        if (root.has("textures") && root.get("textures").isJsonObject()) {
            JsonObject texObj = root.getAsJsonObject("textures");
            textures = new LinkedHashMap<>();
            for (Map.Entry<String, JsonElement> entry : texObj.entrySet()) {
                if (entry.getValue().isJsonPrimitive()) {
                    textures.put(entry.getKey(), entry.getValue().getAsString());
                }
            }
        }

        List<ModelIR.ElementIR> elements = Collections.emptyList();
        if (root.has("elements") && root.get("elements").isJsonArray()) {
            JsonArray arr = root.getAsJsonArray("elements");
            elements = new ArrayList<>(arr.size());
            for (JsonElement el : arr) {
                if (el.isJsonObject()) {
                    elements.add(parseElement(el.getAsJsonObject()));
                }
            }
        }

        List<ModelIR.OverrideIR> overrides = Collections.emptyList();
        if (root.has("overrides") && root.get("overrides").isJsonArray()) {
            JsonArray ovArr = root.getAsJsonArray("overrides");
            overrides = new ArrayList<>(ovArr.size());
            for (JsonElement ov : ovArr) {
                if (ov.isJsonObject()) {
                    JsonObject ovObj = ov.getAsJsonObject();
                    String targetModel = ovObj.has("model") ? ovObj.get("model").getAsString() : "";
                    Map<String, Float> preds = Collections.emptyMap();
                    if (ovObj.has("predicate") && ovObj.get("predicate").isJsonObject()) {
                        preds = new LinkedHashMap<>();
                        for (Map.Entry<String, JsonElement> p : ovObj.getAsJsonObject("predicate").entrySet()) {
                            preds.put(p.getKey(), p.getValue().getAsFloat());
                        }
                    }
                    overrides.add(new ModelIR.OverrideIR(targetModel, preds));
                }
            }
        }

        Map<String, ModelIR.TransformIR> transforms = Collections.emptyMap();
        if (root.has("display") && root.get("display").isJsonObject()) {
            JsonObject dispObj = root.getAsJsonObject("display");
            transforms = new LinkedHashMap<>();
            for (Map.Entry<String, JsonElement> entry : dispObj.entrySet()) {
                if (entry.getValue().isJsonObject()) {
                    JsonObject tObj = entry.getValue().getAsJsonObject();
                    float[] rot = tObj.has("rotation") && tObj.get("rotation").isJsonArray()
                            ? parseFloatArray(tObj.getAsJsonArray("rotation"), 3)
                            : new float[]{0, 0, 0};
                    float[] trans = tObj.has("translation") && tObj.get("translation").isJsonArray()
                            ? parseFloatArray(tObj.getAsJsonArray("translation"), 3)
                            : new float[]{0, 0, 0};
                    float[] scale = tObj.has("scale") && tObj.get("scale").isJsonArray()
                            ? parseFloatArray(tObj.getAsJsonArray("scale"), 3)
                            : new float[]{1, 1, 1};
                    transforms.put(entry.getKey(), new ModelIR.TransformIR(rot, trans, scale));
                }
            }
        }

        return new ModelIR(modelId, parent, ao, guiLight, textures, -1, elements, overrides, transforms);
    }

    private static ModelIR.ElementIR parseElement(JsonObject obj) {
        float[] from = parseFloatArray(obj.getAsJsonArray("from"), 3);
        float[] to = parseFloatArray(obj.getAsJsonArray("to"), 3);

        ModelIR.RotationIR rotation = null;
        if (obj.has("rotation") && obj.get("rotation").isJsonObject()) {
            JsonObject rotObj = obj.getAsJsonObject("rotation");
            float[] origin = parseFloatArray(rotObj.getAsJsonArray("origin"), 3);
            String axisStr = rotObj.has("axis") ? rotObj.get("axis").getAsString().toLowerCase(Locale.ROOT) : "y";
            byte axis = switch (axisStr) {
                case "x" -> (byte) 0;
                case "z" -> (byte) 2;
                default -> (byte) 1; // y
            };
            float angle = rotObj.has("angle") ? rotObj.get("angle").getAsFloat() : 0.0f;
            boolean rescale = rotObj.has("rescale") && rotObj.get("rescale").getAsBoolean();
            rotation = new ModelIR.RotationIR(origin, axis, angle, rescale);
        }

        boolean shade = !obj.has("shade") || obj.get("shade").getAsBoolean();

        Map<Byte, ModelIR.FaceIR> faces = Collections.emptyMap();
        if (obj.has("faces") && obj.get("faces").isJsonObject()) {
            JsonObject facesObj = obj.getAsJsonObject("faces");
            faces = new LinkedHashMap<>();
            for (Map.Entry<String, JsonElement> entry : facesObj.entrySet()) {
                byte dir = directionToByte(entry.getKey());
                if (dir >= 0 && entry.getValue().isJsonObject()) {
                    faces.put(dir, parseFace(entry.getValue().getAsJsonObject()));
                }
            }
        }

        return new ModelIR.ElementIR(from, to, rotation, shade, faces);
    }

    private static ModelIR.FaceIR parseFace(JsonObject obj) {
        float[] uv = null;
        if (obj.has("uv") && obj.get("uv").isJsonArray()) {
            uv = parseFloatArray(obj.getAsJsonArray("uv"), 4);
        }
        String texture = obj.has("texture") ? obj.get("texture").getAsString() : "";
        Byte cullface = null;
        if (obj.has("cullface")) {
            byte c = directionToByte(obj.get("cullface").getAsString());
            if (c >= 0) {
                cullface = c;
            }
        }
        int rotation = obj.has("rotation") ? obj.get("rotation").getAsInt() : 0;
        int tint = obj.has("tintindex") ? obj.get("tintindex").getAsInt() : -1;
        return new ModelIR.FaceIR(uv, texture, cullface, rotation, tint);
    }

    private static float[] parseFloatArray(JsonArray arr, int expectedSize) {
        float[] res = new float[expectedSize];
        for (int i = 0; i < expectedSize && i < arr.size(); i++) {
            res[i] = arr.get(i).getAsFloat();
        }
        return res;
    }

    public static byte directionToByte(String dir) {
        if (dir == null) {
            return -1;
        }
        return switch (dir.toLowerCase(Locale.ROOT)) {
            case "down" -> (byte) 0;
            case "up" -> (byte) 1;
            case "north" -> (byte) 2;
            case "south" -> (byte) 3;
            case "west" -> (byte) 4;
            case "east" -> (byte) 5;
            default -> -1;
        };
    }

    public static String byteToDirection(byte b) {
        return switch (b) {
            case 0 -> "down";
            case 1 -> "up";
            case 2 -> "north";
            case 3 -> "south";
            case 4 -> "west";
            case 5 -> "east";
            default -> "down";
        };
    }
}
