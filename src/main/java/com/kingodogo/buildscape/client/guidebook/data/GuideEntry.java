package com.kingodogo.buildscape.client.guidebook.data;

import java.util.Collections;
import java.util.List;

/**
 * A named topic/article inside a {@link GuideCategory}.
 * Contains one or more {@link GuidePage}s that the player reads sequentially.
 *
 * @param titleKey lang key
 */
public record GuideEntry(String id, String titleKey, List<GuidePage> pages) {

    public GuideEntry(String id, String titleKey, List<GuidePage> pages) {
        this.id = id;
        this.titleKey = titleKey;
        this.pages = Collections.unmodifiableList(pages);
    }

    /**
     * Convenience: total number of pages in this entry.
     */
    public int pageCount() {
        return pages.size();
    }
}
