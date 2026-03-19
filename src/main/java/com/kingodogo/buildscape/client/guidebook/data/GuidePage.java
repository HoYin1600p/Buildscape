package com.kingodogo.buildscape.client.guidebook.data;

import java.util.Collections;
import java.util.List;

/**
 * A single "page" within a {@link GuideEntry}.
 * Pages are rendered one at a time; the player navigates with prev/next arrows.
 */
public final class GuidePage {

    private final List<PageComponent> components;

    public GuidePage(List<PageComponent> components) {
        this.components = Collections.unmodifiableList(components);
    }

    /** All renderable components for this page, in order. */
    public List<PageComponent> getComponents() {
        return components;
    }
}
