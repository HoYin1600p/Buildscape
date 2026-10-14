package com.kingodogo.buildscape.client.screen.widget;

/**
 * Position and height of a vanilla widget where the version has no public accessors (1.18.2): a mixin
 * implements this on AbstractWidget, because reflection on the Mojang field names fails in production.
 */
public interface IWidgetBounds {
    int buildscape$getX();

    int buildscape$getY();

    void buildscape$setX(int x);

    void buildscape$setY(int y);

    int buildscape$getHeight();

    void buildscape$setHeight(int height);
}
