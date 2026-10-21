package com.kingodogo.buildscape.resources.bundle.tag;

import java.util.List;

public record TagIR(
        String registry, // e.g. "block", "item"
        String id, // e.g. "buildscape:slabs", "minecraft:mineable/pickaxe"
        boolean replace,
        List<TagEntryIR> entries) {
    public record TagEntryIR(
            String id,
            boolean tag, // true if tag reference (#namespace:id), false if direct element
            boolean required // true if required, false if optional
    ) {
    }
}
