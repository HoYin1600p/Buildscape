package com.kingodogo.buildscape.recipe.framework.compiler;

import java.util.HashMap;
import java.util.Map;

public class AliasResolver {

    private final Map<String, String> aliases = new HashMap<>();

    public AliasResolver() {
        aliases.put("BS:", "buildscape:");
        aliases.put("MC:", "minecraft:");
        aliases.put("F:", "forge:");
    }

    public void registerAliases(Map<String, String> newAliases) {
        if (newAliases != null) {
            aliases.putAll(newAliases);
        }
    }

    public String resolveString(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        boolean isTag = input.startsWith("#");
        if (isTag) {
            input = input.substring(1);
        }

        if (aliases.containsKey(input)) {
            input = aliases.get(input);
        }

        for (Map.Entry<String, String> entry : aliases.entrySet()) {
            String prefix = entry.getKey();
            if (prefix.endsWith(":") && input.startsWith(prefix)) {
                input = entry.getValue() + input.substring(prefix.length());
                break;
            }
        }

        if (!input.contains(":")) {
            input = "buildscape:" + input;
        }

        return isTag ? "#" + input : input;
    }
}
