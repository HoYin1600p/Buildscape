package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.client.screen.widget.IWidgetBounds;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetBoundsMixin implements IWidgetBounds {
    @Shadow
    public int x;
    @Shadow
    public int y;
    @Shadow
    protected int height;

    @Override
    public int buildscape$getX() {
        return x;
    }

    @Override
    public int buildscape$getY() {
        return y;
    }

    @Override
    public void buildscape$setX(int x) {
        this.x = x;
    }

    @Override
    public void buildscape$setY(int y) {
        this.y = y;
    }

    @Override
    public int buildscape$getHeight() {
        return height;
    }

    @Override
    public void buildscape$setHeight(int height) {
        this.height = height;
    }
}
