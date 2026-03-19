package com.kingodogo.buildscape.client.guidebook.data;

import java.util.*;

/**
 * Runtime singleton holding all loaded {@link GuideCategory} objects.
 * Populated once by {@link GuidePageLoader} during resource-load phase.
 */
public final class GuideRegistry {

    private static GuideRegistry INSTANCE;

    private final List<GuideCategory> categories = new ArrayList<>();

    private GuideRegistry() {}

    public static GuideRegistry getInstance() {
        if (INSTANCE == null) INSTANCE = new GuideRegistry();
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Population (called by GuidePageLoader)
    // -------------------------------------------------------------------------

    /** Replace all categories with fresh data (called on resource reload). */
    public synchronized void reload(List<GuideCategory> newCategories) {
        categories.clear();
        categories.addAll(newCategories);
        categories.sort(Comparator.comparingInt(GuideCategory::getSortOrder));
    }

    // -------------------------------------------------------------------------
    // Queries
    // -------------------------------------------------------------------------

    public List<GuideCategory> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    public Optional<GuideCategory> findCategory(String id) {
        return categories.stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    public Optional<GuideEntry> findEntry(String categoryId, String entryId) {
        return findCategory(categoryId)
                .flatMap(cat -> cat.getEntries().stream()
                        .filter(e -> e.getId().equals(entryId))
                        .findFirst());
    }

    /**
     * Returns all categories that contain at least one entry matching {@code query}.
     * Each returned category only exposes the matching entries (filtered view).
     */
    public List<SearchResult> search(String query) {
        if (query == null || query.isBlank()) {
            List<SearchResult> all = new ArrayList<>();
            for (GuideCategory cat : categories) {
                for (GuideEntry entry : cat.getEntries()) {
                    all.add(new SearchResult(cat, entry));
                }
            }
            return all;
        }

        String lq = query.strip().toLowerCase();
        List<SearchResult> results = new ArrayList<>();

        for (GuideCategory cat : categories) {
            for (GuideEntry entry : cat.getEntries()) {
                if (entry.getId().toLowerCase().contains(lq)
                        || entry.getTitleKey().toLowerCase().contains(lq)
                        || cat.getId().toLowerCase().contains(lq)) {
                    results.add(new SearchResult(cat, entry));
                }
            }
        }
        return results;
    }

    /** Simple result pair linking a category to a specific entry within it. */
    public record SearchResult(GuideCategory category, GuideEntry entry) {}
}
