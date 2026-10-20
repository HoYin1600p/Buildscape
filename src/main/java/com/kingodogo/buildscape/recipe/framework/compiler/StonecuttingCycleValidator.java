package com.kingodogo.buildscape.recipe.framework.compiler;

import com.google.gson.JsonElement;
import com.kingodogo.buildscape.recipe.framework.parser.RecipeIR;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class StonecuttingCycleValidator {
    private final Map<String, List<Edge>> outgoing = new LinkedHashMap<>();
    private final RecipeIdAllocator recipeIds = new RecipeIdAllocator();
    private final Set<String> registeredIds = new HashSet<>();

    private record Edge(String input, String output, int count, String recipeId) {
    }

    // Cycles through tag inputs are not detected by either the generator or runtime validation.
    public static List<String> inputNodes(JsonElement ingredient) {
        List<String> inputs = new ArrayList<>();
        if (ingredient.isJsonArray()) {
            for (JsonElement alternative : ingredient.getAsJsonArray()) {
                inputs.addAll(inputNodes(alternative));
            }
        } else if (ingredient.isJsonObject()) {
            var object = ingredient.getAsJsonObject();
            if (object.has("item")) {
                inputs.add(inputNode(object.get("item").getAsString(), false));
            }
            if (object.has("tag")) {
                inputs.add(inputNode(object.get("tag").getAsString(), true));
            }
        }
        return inputs;
    }

    private static String inputNode(String id, boolean tag) {
        return tag ? "#" + id : id;
    }

    public void addRecipe(String id, List<String> inputs, String output, int count) {
        if (count < 1) {
            return;
        }
        if (!registeredIds.add(id)) {
            return;
        }
        for (String input : inputs) {
            outgoing.computeIfAbsent(input, key -> new ArrayList<>()).add(new Edge(input, output, count, id));
        }
    }

    public void removeRecipe(String id) {
        if (!registeredIds.remove(id)) {
            return;
        }
        outgoing.values().forEach(edges -> edges.removeIf(edge -> edge.recipeId().equals(id)));
        outgoing.values().removeIf(List::isEmpty);
    }

    private record SearchNode(String item, boolean gain) {
    }

    /** Adds a candidate to an accepted graph that has no gain cycles. */
    public void addRecipeChecked(String id, List<String> inputs, String output, int count) {
        if (count < 1 || registeredIds.contains(id)) {
            return;
        }
        Set<String> targets = new HashSet<>(inputs);
        SearchNode start = new SearchNode(output, count > 1);
        Map<SearchNode, Edge> previous = new HashMap<>();
        Map<SearchNode, SearchNode> predecessors = new HashMap<>();
        Set<SearchNode> visited = new HashSet<>();
        ArrayDeque<SearchNode> queue = new ArrayDeque<>();
        visited.add(start);
        queue.add(start);
        while (!queue.isEmpty()) {
            SearchNode node = queue.remove();
            if (node.gain() && targets.contains(node.item())) {
                List<Edge> cycle = new ArrayList<>();
                SearchNode step = node;
                while (!step.equals(start)) {
                    Edge edge = previous.get(step);
                    cycle.add(edge);
                    step = predecessors.get(step);
                }
                Collections.reverse(cycle);
                cycle.add(0, new Edge(node.item(), output, count, id));
                throw gainCycle(cycle);
            }
            for (Edge edge : outgoing.getOrDefault(node.item(), List.of())) {
                SearchNode next = new SearchNode(edge.output(), node.gain() || edge.count() > 1);
                if (visited.add(next)) {
                    previous.put(next, edge);
                    predecessors.put(next, node);
                    queue.add(next);
                }
            }
        }
        addRecipe(id, inputs, output, count);
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
            String output = aliases.resolveString(spec.result().item());
            addInput(spec.input(), output, spec.result().count(), id, aliases);
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
        resolved = resolved.startsWith("#") ? inputNode(resolved.substring(1), true) : inputNode(resolved, false);
        outgoing.computeIfAbsent(resolved, key -> new ArrayList<>()).add(new Edge(resolved, output, count, id));
    }

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
                    List<Edge> cycle = new ArrayList<>();
                    String item = gain.input();
                    while (!item.equals(gain.output())) {
                        Edge edge = previous.get(item);
                        cycle.add(edge);
                        item = edge.input();
                    }
                    Collections.reverse(cycle);
                    cycle.add(0, gain);
                    throw gainCycle(cycle);
                }
            }
        }
    }

    private static IllegalArgumentException gainCycle(List<Edge> cycle) {
        return new IllegalArgumentException("Stonecutting cycle with net item gain: "
                + cycle.stream().map(edge -> edge.recipeId() + " (" + edge.input()
                        + " -> " + edge.count() + " " + edge.output() + ")")
                        .collect(Collectors.joining("; ")));
    }

    public void clear() {
        outgoing.clear();
        recipeIds.clear();
        registeredIds.clear();
    }
}
