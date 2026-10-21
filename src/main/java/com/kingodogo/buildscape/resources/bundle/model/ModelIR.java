package com.kingodogo.buildscape.resources.bundle.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public record ModelIR(
        String id,
        String parent,
        Boolean ambientOcclusion, // null = inherit from parent, true = enabled, false = disabled
        byte guiLight, // 0 = inherit/null, 1 = front, 2 = side
        Map<String, String> textures,
        int geometryTemplateId, // -1 if custom/inline elements, >= 0 if shared template
        List<ElementIR> elements,
        List<OverrideIR> overrides,
        Map<String, TransformIR> transforms) {
    public static final byte GUI_LIGHT_INHERIT = 0;
    public static final byte GUI_LIGHT_FRONT = 1;
    public static final byte GUI_LIGHT_SIDE = 2;

    public ModelIR(
            String id,
            String parent,
            Boolean ambientOcclusion,
            byte guiLight,
            Map<String, String> textures,
            int geometryTemplateId,
            List<ElementIR> elements,
            List<OverrideIR> overrides) {
        this(id, parent, ambientOcclusion, guiLight, textures, geometryTemplateId, elements, overrides,
                Collections.emptyMap());
    }

    public ModelIR(
            String id,
            String parent,
            boolean ambientOcclusion,
            byte guiLight,
            Map<String, String> textures,
            int geometryTemplateId,
            List<ElementIR> elements,
            List<OverrideIR> overrides) {
        this(id, parent, (Boolean) ambientOcclusion, guiLight, textures, geometryTemplateId, elements, overrides,
                Collections.emptyMap());
    }

    public record ElementIR(
            float[] from,
            float[] to,
            RotationIR rotation,
            boolean shade,
            Map<Byte, FaceIR> faces // key is direction byte: 0:down, 1:up, 2:north, 3:south, 4:west, 5:east
    ) {
    }

    public record RotationIR(
            float[] origin,
            byte axis, // 0:X, 1:Y, 2:Z
            float angle,
            boolean rescale) {
    }

    public record FaceIR(
            float[] uv, // null or float[4] {u0, v0, u1, v1}
            String texture,
            Byte cullface, // null or direction byte
            int rotation, // 0, 90, 180, 270
            int tintIndex // -1 if none
    ) {
    }

    public record OverrideIR(
            String model,
            Map<String, Float> predicates) {
    }

    public record TransformIR(
            float[] rotation,
            float[] translation,
            float[] scale) {
    }
}
