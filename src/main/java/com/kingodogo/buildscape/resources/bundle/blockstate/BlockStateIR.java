package com.kingodogo.buildscape.resources.bundle.blockstate;

import java.util.List;
import java.util.Map;

public record BlockStateIR(
        String id,
        byte type, // 0 = variants, 1 = multipart, 2 = template
        int templateId, // -1 if not templated
        String templateParameter, // e.g. base model name for templates
        Map<String, List<VariantModelIR>> variants,
        List<MultiPartIR> multiparts) {
    public static final byte TYPE_VARIANTS = 0;
    public static final byte TYPE_MULTIPART = 1;
    public static final byte TYPE_TEMPLATE = 2;

    public record VariantModelIR(
            String model,
            int x,
            int y,
            boolean uvLock,
            int weight) {
    }

    public record MultiPartIR(
            ConditionIR condition, // null if unconditional
            List<VariantModelIR> models) {
    }

    public record ConditionIR(
            byte kind, // 0 = key_value, 1 = and, 2 = or
            String key,
            String value,
            List<ConditionIR> terms) {
        public static final byte KIND_KEY_VALUE = 0;
        public static final byte KIND_AND = 1;
        public static final byte KIND_OR = 2;
    }
}
