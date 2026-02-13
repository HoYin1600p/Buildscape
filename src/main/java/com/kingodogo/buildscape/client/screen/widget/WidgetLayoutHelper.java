package com.kingodogo.buildscape.client.screen.widget;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen;

public class WidgetLayoutHelper {
    
    public static void positionSearchBoxWithButtons(AbstractWidget parent, EditBox searchBox,
                                                    int buttonAreaWidth, int topOffset, int leftPadding) {
        if (parent == null || searchBox == null) return;
        
        int searchBoxY = parent.y + topOffset;
        int searchBoxWidth = parent.getWidth() - buttonAreaWidth - leftPadding * 2;
        int searchBoxX = parent.x + leftPadding;
        
        searchBox.x = searchBoxX;
        searchBox.y = searchBoxY;
        searchBox.setWidth(searchBoxWidth);
    }
    
    public static void positionButtonsInRow(AbstractWidget parent, AbstractWidget[] buttons,
                                           int buttonSize, int buttonSpacing,
                                           int searchBoxWidth, int topOffset, int leftPadding) {
        if (parent == null || buttons == null) return;
        
        int buttonY = parent.y + topOffset;
        int buttonsStartX = parent.x + leftPadding + searchBoxWidth + BuildScapeConfigScreen.scaleSize(10);
        
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i] != null) {
                buttons[i].x = buttonsStartX + i * (buttonSize + buttonSpacing);
                buttons[i].y = buttonY;
                if (buttons[i] instanceof AbstractWidget) {
                    buttons[i].setWidth(buttonSize);
                    try {
                        java.lang.reflect.Field heightField = AbstractWidget.class.getDeclaredField("height");
                        heightField.setAccessible(true);
                        heightField.setInt(buttons[i], buttonSize);
                    } catch (Exception e) {
                    }
                }
            }
        }
    }
    
    public static void positionWidgetBelowSearchBox(AbstractWidget parent, AbstractWidget child,
                                                   int searchBoxHeight, int searchBoxOffset, int bottomPadding) {
        if (parent == null || child == null) return;
        
        int childY = parent.y + searchBoxOffset + searchBoxHeight + BuildScapeConfigScreen.scaleSize(5);
        int childHeight = parent.y + parent.getHeight() - childY - bottomPadding;
        
        child.x = parent.x;
        child.y = childY;
        child.setWidth(parent.getWidth());
        
        try {
            java.lang.reflect.Method setHeightMethod = child.getClass().getMethod("setHeight", int.class);
            setHeightMethod.invoke(child, childHeight);
        } catch (NoSuchMethodException e) {
            try {
                java.lang.reflect.Field heightField = AbstractWidget.class.getDeclaredField("height");
                heightField.setAccessible(true);
                heightField.setInt(child, childHeight);
            } catch (Exception ex) {
            }
        } catch (Exception e) {
        }
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

