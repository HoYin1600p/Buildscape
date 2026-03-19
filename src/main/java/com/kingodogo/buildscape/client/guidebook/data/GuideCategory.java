package com.kingodogo.buildscape.client.guidebook.data;

import java.util.Collections;
import java.util.List;

/**
 * A top-level category in the BuildScape Guidebook (shown in the left sidebar).
 *
 * Categories are loaded from JSON at startup and sorted by {@link #getSortOrder()}.
 */
public final class GuideCategory {

    private final String id;
    private final String displayNameKey; // lang key
    private final String iconItem;        // item registry name for the icon, or null
    private final int sortOrder;
    private final List<GuideEntry> entries;

    public GuideCategory(String id,
                         String displayNameKey,
                         String iconItem,
                         int sortOrder,
                         List<GuideEntry> entries) {
        this.id              = id;
        this.displayNameKey  = displayNameKey;
        this.iconItem        = iconItem;
        this.sortOrder       = sortOrder;
        this.entries         = Collections.unmodifiableList(entries);
    }

    public String getId()              { return id; }
    public String getDisplayNameKey()  { return displayNameKey; }
    public String getIconItem()        { return iconItem; }
    public int    getSortOrder()       { return sortOrder; }
    public List<GuideEntry> getEntries() { return entries; }

    /** True if any entry title or entry ID matches the search query (case-insensitive). */
    public boolean matchesSearch(String query) {
        String lq = query.toLowerCase();
        return id.toLowerCase().contains(lq) ||
               entries.stream().anyMatch(e -> e.getId().toLowerCase().contains(lq) ||
                                             e.getTitleKey().toLowerCase().contains(lq));
    }
}
