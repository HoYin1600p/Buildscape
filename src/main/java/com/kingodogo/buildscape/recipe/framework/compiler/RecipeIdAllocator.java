package com.kingodogo.buildscape.recipe.framework.compiler;

import com.kingodogo.buildscape.recipe.framework.parser.RecipeIR;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class RecipeIdAllocator {
    private final Map<String, RecipeIR.RecipeSpec> assigned = new HashMap<>();

    public String allocate(String category, RecipeIR.RecipeSpec spec, AliasResolver aliases) {
        String type = spec.type() == null ? "" : spec.type().toLowerCase(Locale.ROOT);
        if (type.equals("confetti_configure") || type.equals("buildscape:confetti_configure")) {
            return "buildscape:autogen/special/confetti_configure";
        }
        if (type.equals("clear_shulker_filters") || type.equals("buildscape:clear_shulker_filters")) {
            return "buildscape:autogen/special/clear_shulker_filters";
        }
        String name;
        if (spec.id() != null) {
            name = spec.id();
        } else {
            String output = spec.result() == null ? "unknown" : aliases.resolveString(spec.result().item());
            name = output.substring(output.indexOf(':') + 1);
        }
        String path = "autogen/" + category + "/" + name;
        String base = "buildscape:" + sanitize(spec.id() == null ? path : aliases.resolveString(path));
        if (spec.id() != null) {
            assigned.putIfAbsent(base, spec);
            return base;
        }
        String candidate = base;
        int collision = 0;
        while (assigned.containsKey(candidate) && !assigned.get(candidate).equals(spec)) {
            candidate = base + "_" + Integer.toUnsignedString(spec.hashCode())
                    + (collision == 0 ? "" : "_" + collision);
            collision++;
        }
        assigned.putIfAbsent(candidate, spec);
        return candidate;
    }

    private static String sanitize(String path) {
        return path.toLowerCase(Locale.ROOT)
                .replace(":", "_").replaceAll("[^a-z0-9/._-]", "_");
    }

    public void clear() {
        assigned.clear();
    }
}
