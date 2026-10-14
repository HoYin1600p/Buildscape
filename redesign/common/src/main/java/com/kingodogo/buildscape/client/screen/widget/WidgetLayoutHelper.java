package com.kingodogo.buildscape.client.screen.widget;

import com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class WidgetLayoutHelper {

    private static Field xField;
    private static Field yField;
    private static Field heightField;
    private static Method getXMethod;
    private static Method getYMethod;
    private static Method setXMethod;
    private static Method setYMethod;
    private static Method setPositionMethod;

    static {
        try {
            heightField = AbstractWidget.class.getDeclaredField("height");
            heightField.setAccessible(true);
        } catch (Throwable ignored) {}

        try {
            xField = AbstractWidget.class.getDeclaredField("x");
            xField.setAccessible(true);
        } catch (Throwable ignored) {}

        try {
            yField = AbstractWidget.class.getDeclaredField("y");
            yField.setAccessible(true);
        } catch (Throwable ignored) {}

        try {
            getXMethod = AbstractWidget.class.getMethod("getX");
        } catch (Throwable ignored) {}

        try {
            getYMethod = AbstractWidget.class.getMethod("getY");
        } catch (Throwable ignored) {}

        try {
            setXMethod = AbstractWidget.class.getMethod("setX", int.class);
        } catch (Throwable ignored) {}

        try {
            setYMethod = AbstractWidget.class.getMethod("setY", int.class);
        } catch (Throwable ignored) {}

        try {
            setPositionMethod = AbstractWidget.class.getMethod("setPosition", int.class, int.class);
        } catch (Throwable ignored) {}
    }

    public static int getX(AbstractWidget widget) {
        if (widget == null) return 0;
        if (getXMethod != null) {
            try {
                return (int) getXMethod.invoke(widget);
            } catch (Throwable ignored) {}
        }
        if (xField != null) {
            try {
                return xField.getInt(widget);
            } catch (Throwable ignored) {}
        }
        return 0;
    }

    public static int getY(AbstractWidget widget) {
        if (widget == null) return 0;
        if (getYMethod != null) {
            try {
                return (int) getYMethod.invoke(widget);
            } catch (Throwable ignored) {}
        }
        if (yField != null) {
            try {
                return yField.getInt(widget);
            } catch (Throwable ignored) {}
        }
        return 0;
    }

    public static int getHeight(AbstractWidget widget) {
        if (widget == null) return 0;
        try {
            Method getHeightMethod = widget.getClass().getMethod("getHeight");
            return (int) getHeightMethod.invoke(widget);
        } catch (Throwable ignored) {}
        if (heightField != null) {
            try {
                return heightField.getInt(widget);
            } catch (Throwable ignored) {}
        }
        return 0;
    }

    public static void setX(AbstractWidget widget, int x) {
        if (widget == null) return;
        if (setXMethod != null) {
            try {
                setXMethod.invoke(widget, x);
                return;
            } catch (Throwable ignored) {}
        }
        if (xField != null) {
            try {
                xField.setInt(widget, x);
            } catch (Throwable ignored) {}
        }
    }

    public static void setY(AbstractWidget widget, int y) {
        if (widget == null) return;
        if (setYMethod != null) {
            try {
                setYMethod.invoke(widget, y);
                return;
            } catch (Throwable ignored) {}
        }
        if (yField != null) {
            try {
                yField.setInt(widget, y);
            } catch (Throwable ignored) {}
        }
    }

    public static void setPosition(AbstractWidget widget, int x, int y) {
        if (widget == null) return;
        if (setPositionMethod != null) {
            try {
                setPositionMethod.invoke(widget, x, y);
                return;
            } catch (Throwable ignored) {}
        }
        setX(widget, x);
        setY(widget, y);
    }

    public static void setWidgetHeight(AbstractWidget widget, int height) {
        if (widget == null) return;
        try {
            Method setHeightMethod = widget.getClass().getMethod("setHeight", int.class);
            setHeightMethod.invoke(widget, height);
            return;
        } catch (Throwable ignored) {
        }
        if (heightField != null) {
            try {
                heightField.setInt(widget, height);
            } catch (Throwable ignored) {
            }
        }
    }

    public static void positionSearchBoxWithButtons(AbstractWidget parent, EditBox searchBox,
                                                    int buttonAreaWidth, int topOffset, int leftPadding) {
        if (parent == null || searchBox == null) return;

        int parentX = getX(parent);
        int parentY = getY(parent);
        int searchBoxY = parentY + topOffset;
        int searchBoxWidth = parent.getWidth() - buttonAreaWidth - leftPadding * 2;
        int searchBoxX = parentX + leftPadding;

        setPosition(searchBox, searchBoxX, searchBoxY);
        searchBox.setWidth(searchBoxWidth);
    }

    public static void positionButtonsInRow(AbstractWidget parent, AbstractWidget[] buttons,
                                           int buttonSize, int buttonSpacing,
                                           int searchBoxWidth, int topOffset, int leftPadding) {
        if (parent == null || buttons == null) return;

        int parentX = getX(parent);
        int parentY = getY(parent);
        int buttonY = parentY + topOffset;
        int buttonsStartX = parentX + leftPadding + searchBoxWidth + BuildScapeConfigScreen.scaleSize(10);

        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i] != null) {
                setPosition(buttons[i], buttonsStartX + i * (buttonSize + buttonSpacing), buttonY);
                buttons[i].setWidth(buttonSize);
                setWidgetHeight(buttons[i], buttonSize);
            }
        }
    }

    public static void positionWidgetBelowSearchBox(AbstractWidget parent, AbstractWidget child,
                                                   int searchBoxHeight, int searchBoxOffset, int bottomPadding) {
        if (parent == null || child == null) return;

        int parentX = getX(parent);
        int parentY = getY(parent);
        int childY = parentY + searchBoxOffset + searchBoxHeight + BuildScapeConfigScreen.scaleSize(5);
        int childHeight = parentY + parent.getHeight() - childY - bottomPadding;

        setPosition(child, parentX, childY);
        child.setWidth(parent.getWidth());
        setWidgetHeight(child, childHeight);
    }

    public static void updateChildPositions(AbstractWidget parent, EditBox searchBox,
                                           AbstractWidget[] buttons, AbstractWidget childWidget,
                                           int buttonAreaWidth, int topOffset, int leftPadding,
                                           int searchBoxHeight, int bottomPadding) {
        if (parent == null) return;

        if (searchBox != null) {
            positionSearchBoxWithButtons(parent, searchBox, buttonAreaWidth, topOffset, leftPadding);
        }

        if (buttons != null && searchBox != null) {
            int searchBoxWidth = parent.getWidth() - buttonAreaWidth - leftPadding * 2;
            int buttonSize = BuildScapeConfigScreen.scaleSize(20);
            int buttonSpacing = BuildScapeConfigScreen.scaleSize(5);
            positionButtonsInRow(parent, buttons, buttonSize, buttonSpacing, searchBoxWidth, topOffset, leftPadding);
        }

        if (childWidget != null && searchBox != null) {
            positionWidgetBelowSearchBox(parent, childWidget, searchBoxHeight, topOffset, bottomPadding);
        }
    }
}
