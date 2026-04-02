package com.kingodogo.buildscape.client.guidebook.data;

import java.util.Collections;
import java.util.List;

/**
 * A top-level category in the BuildScape Guidebook (shown in the left sidebar).
 * <p>
 * Categories are loaded from JSON at startup and sorted by {@link #sortOrder ()}.
 *
 * @param displayNameKey lang key
 * @param iconItem       item registry name for the icon, or null
 */
public record GuideCategory(String id, String displayNameKey, String iconItem, int sortOrder,
                            List<GuideEntry> entries) {

    public GuideCategory(String id,
                         String displayNameKey,
                         String iconItem,
                         int sortOrder,
                         List<GuideEntry> entries) {
        this.id = id;
        this.displayNameKey = displayNameKey;
        this.iconItem = iconItem;
        this.sortOrder = sortOrder;
        this.entries = Collections.unmodifiableList(entries);
    }

    /**
     * True if any entry title or entry ID matches the search query (case-insensitive).
     */
    public boolean matchesSearch(String query) {
        String lq = query.toLowerCase();
        return id.toLowerCase().contains(lq) ||
                entries.stream().anyMatch(e -> e.id().toLowerCase().contains(lq) ||
                        e.titleKey().toLowerCase().contains(lq));
    }
}
