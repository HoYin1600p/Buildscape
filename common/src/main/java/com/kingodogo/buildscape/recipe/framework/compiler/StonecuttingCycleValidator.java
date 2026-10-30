package com.kingodogo.buildscape.recipe.framework.compiler;

import com.kingodogo.buildscape.recipe.framework.parser.RecipeIR;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Rejects stonecutting cycles that can create additional items. */
public final class StonecuttingCycleValidator {
    private final Map<String, List<Edge>> outgoing = new LinkedHashMap<>();
    private final RecipeIdAllocator recipeIds = new RecipeIdAllocator();
    private final Set<String> registeredIds = new HashSet<>();

    private record Edge(String input, String output, int count, String recipeId) {
    }

    public void addCategory(RecipeIR.CategoryPack pack) {
        AliasResolver aliases = new AliasResolver();
        aliases.registerAliases(pack.aliases());
        TemplateEngine templates = new TemplateEngine();
        templates.registerTemplates(pack.templates());
        FamilyExpander families = new FamilyExpander(templates);
        List<RecipeIR.RecipeSpec> specs = new ArrayList<>();
        if (pack.families() != null) {
            for (RecipeIR.FamilySpec family : pack.families()) {
                specs.addAll(families.expandFamily(family));
            }
        }
        if (pack.recipes() != null) {
            specs.addAll(pack.recipes());
        }
        for (RecipeIR.RecipeSpec spec : specs) {
            if (spec == null) {
                continue;
            }
            String id = recipeIds.allocate(pack.category(), spec, aliases);
            if (!registeredIds.add(id)) {
                continue;
            }
            String type = spec.type() != null ? spec.type() : pack.category();
            if (!"stonecutting".equalsIgnoreCase(type) || spec.result() == null
                    || spec.result().count() < 1 || spec.result().item() == null) {
                continue;
            }
            addInput(spec.input(), aliases.resolveString(spec.result().item()), spec.result().count(), id, aliases);
        }
    }

    private void addInput(String input, String output, int count, String id, AliasResolver aliases) {
        if (input == null || input.isEmpty()) {
            return;
        }
        if (input.startsWith("[") && input.endsWith("]")) {
            for (String part : input.substring(1, input.length() - 1).split(",")) {
                addInput(part.trim(), output, count, id, aliases);
            }
            return;
        }
        String resolved = aliases.resolveString(input);
        outgoing.computeIfAbsent(resolved, key -> new ArrayList<>()).add(new Edge(resolved, output, count, id));
    }

    /** Throws when an output can reach its own input through an item-gain edge. */
    public void validate() {
        for (List<Edge> edges : outgoing.values()) {
            for (Edge gain : edges) {
                if (gain.count() <= 1) {
                    continue;
                }
                Map<String, Edge> previous = new HashMap<>();
                Set<String> visited = new HashSet<>();
                ArrayDeque<String> queue = new ArrayDeque<>();
                visited.add(gain.output());
                queue.add(gain.output());
                while (!queue.isEmpty() && !visited.contains(gain.input())) {
                    String item = queue.remove();
                    for (Edge edge : outgoing.getOrDefault(item, List.of())) {
                        if (visited.add(edge.output())) {
                            previous.put(edge.output(), edge);
                            queue.add(edge.output());
                        }
                    }
                }
                if (visited.contains(gain.input())) {
                    throw new IllegalArgumentException("Stonecutting cycle with net item gain involving " + gain.recipeId());
                }
            }
        }
    }

    public void clear() {
        outgoing.clear();
        recipeIds.clear();
        registeredIds.clear();
    }
}
