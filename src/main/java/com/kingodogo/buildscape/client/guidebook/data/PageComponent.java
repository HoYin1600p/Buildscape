package com.kingodogo.buildscape.client.guidebook.data;

/**
 * Sealed hierarchy of renderable page content pieces.
 * Each variant corresponds to one JSON "type" field.
 */
public sealed interface PageComponent permits
        PageComponent.HeaderComponent,
        PageComponent.TextComponent,
        PageComponent.ImageComponent,
        PageComponent.ItemDisplayComponent,
        PageComponent.SpacerComponent {

    // -------------------------------------------------------------------------
    // Subtypes
    // -------------------------------------------------------------------------

    /**
     * A large-font section header line.
     */
    record HeaderComponent(String textKey) implements PageComponent {}

    /**
     * A rich text paragraph. Supports §-colour codes and basic markdown-like
     * **bold** / *italic* inline markers (parsed at render time).
     */
    record TextComponent(String textKey) implements PageComponent {}

    /**
     * An image region that blits a texture from the resource-pack.
     *
     * @param texturePath resource-location string, e.g. "buildscape:textures/gui/guidebook/pillar_demo.png"
     * @param width       desired render width in GUI pixels (height scales proportionally)
     */
    record ImageComponent(String texturePath, int width) implements PageComponent {}

    /**
     * Renders a live {@code ItemStack} using the game's item-renderer.
     *
     * @param itemId   the item's registry name, e.g. "buildscape:pillar_oak"
     * @param captionKey optional lang key for a caption below the item; may be {@code null}
     */
    record ItemDisplayComponent(String itemId, String captionKey) implements PageComponent {}

    /**
     * Blank vertical gap (in pixels) for layout breathing room.
     */
    record SpacerComponent(int pixels) implements PageComponent {}
}
