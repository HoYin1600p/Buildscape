package com.kingodogo.buildscape.client.screen.widget;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen;

/**
 * Helper class for positioning child components relative to parent widgets.
 * Ensures search boxes, buttons, and other components auto-scale with their parent.
 */
public class WidgetLayoutHelper {
    
    /**
     * Position a search box at the top of a parent widget with buttons on the right.
     * @param parent The parent widget
     * @param searchBox The search box to position
     * @param buttonAreaWidth Total width reserved for buttons (will be subtracted from search box width)
     * @param topOffset Offset from top of parent widget
     * @param leftPadding Left padding from parent edge
     */
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
    
    /**
     * Position buttons in a row next to a search box.
     * @param parent The parent widget
     * @param buttons Array of buttons to position
     * @param buttonSize Size of each button (width and height)
     * @param buttonSpacing Spacing between buttons
     * @param searchBoxWidth Width of the search box (to position buttons after it)
     * @param topOffset Offset from top of parent widget (should match search box)
     * @param leftPadding Left padding from parent edge
     */
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
                        // Ignore
                    }
                }
            }
        }
    }
    
    /**
     * Position a child widget below a search box within a parent widget.
     * @param parent The parent widget
     * @param child The child widget to position
     * @param searchBoxHeight Height of the search box above
     * @param searchBoxOffset Offset of search box from top
     * @param bottomPadding Padding from bottom of parent
     */
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
                // Ignore
            }
        } catch (Exception e) {
            // Ignore
        }
    }
    
    /**
     * Update all child component positions when parent widget resizes.
     * Call this whenever the parent widget's size changes.
     */
    public static void updateChildPositions(AbstractWidget parent, EditBox searchBox, 
                                           AbstractWidget[] buttons, AbstractWidget childWidget,
                                           int buttonAreaWidth, int topOffset, int leftPadding,
                                           int searchBoxHeight, int bottomPadding) {
        if (parent == null) return;
        
        // Update search box position
        if (searchBox != null) {
            positionSearchBoxWithButtons(parent, searchBox, buttonAreaWidth, topOffset, leftPadding);
        }
        
        // Update button positions
        if (buttons != null && searchBox != null) {
            int searchBoxWidth = parent.getWidth() - buttonAreaWidth - leftPadding * 2;
            int buttonSize = BuildScapeConfigScreen.scaleSize(20);
            int buttonSpacing = BuildScapeConfigScreen.scaleSize(5);
            positionButtonsInRow(parent, buttons, buttonSize, buttonSpacing, searchBoxWidth, topOffset, leftPadding);
        }
        
        // Update child widget position
        if (childWidget != null && searchBox != null) {
            positionWidgetBelowSearchBox(parent, childWidget, searchBoxHeight, topOffset, bottomPadding);
        }
    }
}

